package am.extension.multiUserFeedback.initialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.tomgibara.cluster.gvm.dbl.DblResult;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.evaluation.clustering.gvm.GVM_Clustering;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.inizialization.FeedbackLoopInizialization;
import am.extension.userfeedback.utility.UFLutility;
import am.matcher.Combination.CombinationMatcher;

public class MUDataInitialization  extends FeedbackLoopInizialization<MUExperiment> {
	
	private static final Logger LOG = Logger.getLogger(MUDataInitialization.class);
	
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	MUExperiment experiment;
	int count_vsv=0;
	public MUDataInitialization()
	{
		super();
	}
	
	@Override
	public void inizialize(MUExperiment exp) {
		// TODO Auto-generated method stub
		this.experiment=exp;
		inputMatchers=exp.initialMatcher.getComponentMatchers();
		
		SignatureVectorStats svs=new SignatureVectorStats(exp);
		try {
			svs.printSV(exp.initialMatcher.getFinalMatcher().getClassesMatrix(), alignType.aligningClasses);
			svs.printSV(exp.initialMatcher.getFinalMatcher().getPropertiesMatrix(), alignType.aligningProperties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		SimilarityMatrix smClass=exp.initialMatcher.getFinalMatcher().getClassesMatrix().clone();
		SimilarityMatrix smProperty=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix().clone();


		SimilarityMatrix am=exp.initialMatcher.getFinalMatcher().getClassesMatrix();
		smClass=prepare(smClass, am);
		
		am=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix();
		smProperty=prepare(smProperty, am);
		
		GVM_Clustering gvm=new GVM_Clustering(inputMatchers, count_vsv/5);
		gvm.cluster();
		exp.cluster=gvm.getClusters();
		
		exp.setComputedUFLMatrix(alignType.aligningClasses, smClass);
		exp.setComputedUFLMatrix(alignType.aligningProperties, smProperty);
		
		exp.setForbiddenPositions(alignType.aligningClasses, 
				new SparseMatrix(
						Core.getInstance().getSourceOntology(),
						Core.getInstance().getTargetOntology(), 
						alignType.aligningClasses));
		
		exp.setForbiddenPositions(alignType.aligningProperties, 
				new SparseMatrix(
						Core.getInstance().getSourceOntology(),
						Core.getInstance().getTargetOntology(), 
						alignType.aligningProperties));
		
		
		// set the UFL matrices for classes
		SparseMatrix sparseClassPos = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningClasses);
		
		SparseMatrix sparseClassNeg = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningClasses);
		
		exp.setUflStorageClassPos(sparseClassPos);
		exp.setUflStorageClass_neg(sparseClassNeg);
		
		// set the UFL matrices for properties
		SparseMatrix sparsePropPos = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningProperties);
		
		SparseMatrix sparsePropNeg = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningProperties);
		
		exp.setUflStoragePropertyPos(sparsePropPos);
		exp.setUflStorageProperty_neg(sparsePropNeg);
		
		AbstractMatcher ufl=new CombinationMatcher();
		ufl.setClassesMatrix(smClass);
		ufl.setPropertiesMatrix(smProperty);
		ufl.select();

		exp.setMLAlignment(UFLutility.combineResults(ufl, exp));
		
		// output the experiment description
		StringBuilder d = new StringBuilder();
		
		d.append("============================ Running UFL Experiment: =================================\n");
		d.append("         NUM_USERS:" + exp.setup.parameters.getIntParameter(Parameter.NUM_USERS) + "\n");
		d.append("    NUM_ITERATIONS:" + exp.setup.parameters.getIntParameter(Parameter.NUM_ITERATIONS) + "\n");
		d.append("        ERROR_RATE:" + exp.setup.parameters.getDoubleParameter(Parameter.ERROR_RATE) + "\n");
		d.append(" REVALIDATION_RATE:" + exp.setup.parameters.getParameter(Parameter.REVALIDATION_RATE) + "\n");
		d.append("         STATIC_CS:" + exp.setup.parameters.getBooleanParameter(Parameter.STATIC_CANDIDATE_SELECTION) + "\n");
		d.append("PROPAGATION_METHOD:" + exp.setup.parameters.getParameter(Parameter.PROPAGATION_METHOD) + "\n");
		d.append("======================================================================================\n");
		
		String sourceFile = Core.getInstance().getSourceOntology().getFilename();
		if( sourceFile.length() >= 51 ) {
			d.append("Source Ont: ..." + sourceFile.substring(sourceFile.length()-50-1, sourceFile.length()-1) + "\n");
		}
		else {
			d.append("Source Ont: " + sourceFile + "\n");
		}
		
		String targetFile = Core.getInstance().getTargetOntology().getFilename();
		if( targetFile.length() >= 51 ) {
			d.append("Target Ont: ..." + targetFile.substring(targetFile.length()-50-1, targetFile.length()-1) + "\n");
		}
		else {
			d.append("Target Ont: " + targetFile + "\n");
		}
		
		exp.info(d.toString());
		LOG.info(d.toString());
		
		done();
	}


	
	private SimilarityMatrix prepare(SimilarityMatrix sm, SimilarityMatrix am)
	{
		Mapping mp;
		Object[] ssv;
		for(int i=0;i<sm.getRows();i++)
			for(int j=0;j<sm.getColumns();j++)
			{
				mp = sm.get(i, j);
				ssv=UFLutility.getSignatureVector(mp,experiment.initialMatcher.getComponentMatchers());
				if (!UFLutility.validSsv(ssv))
				{ 
					sm.setSimilarity(i, j, 0.0);
				}
				else
				{
					sm.setSimilarity(i, j, am.getSimilarity(i, j));
					count_vsv++;
				}
			}
		
		return sm;
	}


}
