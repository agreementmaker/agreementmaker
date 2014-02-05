package am.extension.multiUserFeedback.initialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherResult;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;
import am.extension.multiUserFeedback.evaluation.ServerFeedbackEvaluation;
import am.extension.multiUserFeedback.evaluation.ServerFeedbackEvaluation.ServerFeedbackEvaluationData;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.inizialization.FeedbackLoopInizialization;

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
		
		//SimilarityMatrix am=exp.initialMatcher.getFinalMatcher().getClassesMatrix();
		smClass=prepare(smClass);
		
		//am=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix();
		smProperty=prepare(smProperty);
		computeAlignment(smClass, smProperty);

		
		
		List<SimilarityMatrix> lstC=new ArrayList<>();
		List<SimilarityMatrix> lstP=new ArrayList<>();
		
		for (int i=0;i<inputMatchers.size();i++)
		{
			lstC.add(inputMatchers.get(i).getClassesMatrix());
			lstP.add(inputMatchers.get(i).getPropertiesMatrix());
		}
		
		
//		GVM_Clustering gvm=new GVM_Clustering(lstC.toArray(new SimilarityMatrix[0]), count_vsv/4);
//		gvm.cluster();
//		exp.clusterC=gvm.getClusters();
//		
//		gvm=new GVM_Clustering(lstP.toArray(new SimilarityMatrix[0]), count_vsv/4);
//		gvm.cluster();
//		exp.clusterP=gvm.getClusters();
		
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

//	private SimilarityMatrix prepare(SimilarityMatrix sm, SimilarityMatrix am)
//	{
//		double sim=0;
//		Mapping mp;
//		Object[] ssv;
//		for(int i=0;i<sm.getRows();i++)
//			for(int j=0;j<sm.getColumns();j++)
//			{
//				mp = sm.get(i, j);
//				ssv=UFLutility.getSignatureVector(mp,experiment.initialMatcher.getComponentMatchers());
//				if (!UFLutility.validSsv(ssv))
//				{ 
//					sm.setSimilarity(i, j, 0.0);
//				}
//				else
//				{
//					sim=am.getSimilarity(i, j);
//					if (sim==0)
//						System.out.println("ciao");
//					sm.setSimilarity(i, j, sim );
//					count_vsv++;
//				}
//			}
//		
//		return sm;
//	}
	
	private SimilarityMatrix prepare(SimilarityMatrix sm)
	{
		double sim=0;
		Mapping mp;
		//Object[] ssv;
		for(int i=0;i<sm.getRows();i++)
			for(int j=0;j<sm.getColumns();j++)
			{
				mp = sm.get(i, j);
				//ssv=UFLutility.getSignatureVector(mp,experiment.initialMatcher.getComponentMatchers());
				if (!experiment.initialMatcher.getFinalMatcher().getAlignment().contains(mp))
				{ 
					sm.setSimilarity(i, j, 0.0);
				}
//				else
//				{
//					sim=am.getSimilarity(i, j);
//					sm.setSimilarity(i, j, sim );
//					count_vsv++;
//				}
			}
		
		return sm;
	}
	
	private void computeAlignment(SimilarityMatrix classMatrix, SimilarityMatrix propMatrix)
	{

		MatcherResult mr = new MatcherResult((MatchingTask)null);
		mr.setClassesMatrix(classMatrix);
		mr.setPropertiesMatrix(propMatrix);
		mr.setSourceOntology(experiment.getSourceOntology());
		mr.setTargetOntology(experiment.getTargetOntology());
		
		DefaultSelectionParameters selParam = new DefaultSelectionParameters();
		selParam.threshold = experiment.setup.parameters.getDoubleParameter(Parameter.IM_THRESHOLD);
		selParam.maxSourceAlign = experiment.initialMatcher.getFinalMatcher().getParam().maxSourceAlign;
		selParam.maxTargetAlign = experiment.initialMatcher.getFinalMatcher().getParam().maxTargetAlign;
		selParam.inputResult = mr;
		
		MwbmSelection selection = new MwbmSelection();
		selection.setParameters(selParam);
		
		selection.select();
		
		experiment.setMLAlignment(selection.getResult().getAlignment());
		
		
		DeltaFromReference deltaFromReference = new DeltaFromReference(experiment.getReferenceAlignment());
		
		experiment.info(experiment.getReferenceAlignment().toString());
		
		int initialDelta = deltaFromReference.getDelta(experiment.getFinalAlignment());
		AlignmentMetrics initialMetrics= new AlignmentMetrics(experiment.getReferenceAlignment(), experiment.getFinalAlignment());
		
		int currentIteration = experiment.getIterationNumber();
		
		if( currentIteration != 0 )
			throw new AssertionError("Data initialization can only run at iteration 0.");
		
		experiment.info("Iteration: " + currentIteration + 
				", Delta from reference: " + initialDelta + 
				", Precision: " + initialMetrics.getPrecisionPercent() + 
				", Recall: " + initialMetrics.getRecallPercent() + 
				", FMeasure: " + initialMetrics.getFMeasurePercent());
		experiment.info("");
		
		// save the initial values into the propagation evaluation
		// TODO: Fix this.  It should not be hardcoded.  Move to message passing UFL loop.
		PropagationEvaluation pe = experiment.propagationEvaluation;
		if( pe instanceof ServerFeedbackEvaluation ) {
			ServerFeedbackEvaluation sfe = (ServerFeedbackEvaluation) pe;
			ServerFeedbackEvaluationData data = sfe.getData();
			data.precisionArray[0] = initialMetrics.getPrecision(); 
			data.recallArray[0]    = initialMetrics.getRecall();
			data.fmeasureArray[0]  = initialMetrics.getFMeasure();
			data.deltaArray[0]     = initialDelta;			
		}
		
		// set the iteration number.
		experiment.setIterationNumber(1);
	}


}
