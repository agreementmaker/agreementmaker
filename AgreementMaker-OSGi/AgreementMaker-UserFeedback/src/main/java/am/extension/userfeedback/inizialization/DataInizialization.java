package am.extension.userfeedback.inizialization;

import java.util.ArrayList;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.userfeedback.experiments.SUExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;

public class DataInizialization extends FeedbackLoopInizialization<SUExperiment> {
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	public DataInizialization()
	{
		super();
	}
	
	@Override
	public void inizialize(SUExperiment exp) {
		// TODO Auto-generated method stub
		inputMatchers=exp.initialMatcher.getComponentMatchers();
		SimilarityMatrix smClass=exp.initialMatcher.getFinalMatcher().getClassesMatrix().clone();
		SimilarityMatrix smProperty=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix().clone();
		
		
//		for(int i=0;i<smClass.getRows();i++)
//			for(int j=0;j<smClass.getColumns();j++)
//				smClass.setSimilarity(i, j, 0.5);
//		for(int i=0;i<smProperty.getRows();i++)
//			for(int j=0;j<smProperty.getColumns();j++)
//				smProperty.setSimilarity(i, j, 0.5);
		
		SimilarityMatrix am=exp.initialMatcher.getFinalMatcher().getClassesMatrix();
		smClass=prepareSMforNB(smClass, am);
		am=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix();
		smProperty=prepareSMforNB(smProperty, am);
		am=null;
		exp.setUflClassMatrix(smClass);
		exp.setUflPropertyMatrix(smProperty);
		
		
		exp.classesSparseMatrix = 
				new SparseMatrix(
						Core.getInstance().getSourceOntology(),
						Core.getInstance().getTargetOntology(), 
						alignType.aligningClasses);
		
		exp.propertiesSparseMatrix = 
				new SparseMatrix(
						Core.getInstance().getSourceOntology(),
						Core.getInstance().getTargetOntology(), 
						alignType.aligningProperties);
		
		
		// output the experiment description
		exp.info("============================ Running UFL Experiment: =================================");
		exp.info("         NUM_USERS:" + exp.setup.parameters.getIntParameter(Parameter.NUM_USERS));
		exp.info("    NUM_ITERATIONS:" + exp.setup.parameters.getIntParameter(Parameter.NUM_ITERATIONS));
		exp.info("        ERROR_RATE:" + exp.setup.parameters.getDoubleParameter(Parameter.ERROR_RATE));
		exp.info(" REVALIDATION_RATE:" + exp.setup.parameters.getParameter(Parameter.REVALIDATION_RATE));
		exp.info("         STATIC_CS:" + exp.setup.parameters.getBooleanParameter(Parameter.STATIC_CANDIDATE_SELECTION));
		exp.info("PROPAGATION_METHOD:" + exp.setup.parameters.getParameter(Parameter.PROPAGATION_METHOD));
		exp.info("======================================================================================");
		
		String sourceFile = Core.getInstance().getSourceOntology().getFilename();
		if( sourceFile.length() >= 51 ) {
			exp.info("Source Ont: ..." + sourceFile.substring(sourceFile.length()-50-1, sourceFile.length()-1));
		}
		else {
			exp.info("Source Ont: " + sourceFile);
		}
		
		String targetFile = Core.getInstance().getTargetOntology().getFilename();
		if( targetFile.length() >= 51 ) {
			exp.info("Target Ont: ..." + targetFile.substring(targetFile.length()-50-1, targetFile.length()-1));
		}
		else {
			exp.info("Target Ont: " + targetFile);
		}
		
		done();
	}

	private SimilarityMatrix prepareSMforNB(SimilarityMatrix sm, SimilarityMatrix am)
	{
		Mapping mp;
		Object[] ssv;
		for(int i=0;i<sm.getRows();i++)
			for(int j=0;j<sm.getColumns();j++)
			{
				mp = sm.get(i, j);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
				{ 
					sm.setSimilarity(i, j, 0.0);
				}
				else
				{
//					if (am.get(i, j).getSimilarity()>0.6)
//						sm.setSimilarity(i, j, 1.0);
//					else
//						sm.setSimilarity(i, j, 0.5);
					sm.setSimilarity(i, j, am.getSimilarity(i, j));
				}
			}
		
		return sm;
	}
	
	private Object[] getSignatureVector(Mapping mp)
	{
		int size=inputMatchers.size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		AbstractMatcher a;
		Object[] ssv=new Object[size];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			ssv[i]=a.getAlignment().getSimilarity(sourceNode, targetNode);
			
		}
		return ssv;
	}
	
	
	//check if the signature vector is valid. A valid signature vector must have at least one non zero element.
	private boolean validSsv(Object[] ssv)
	{
		Object obj=0.0;
		for(int i=0;i<ssv.length;i++)
		{
			if (!ssv[i].equals(obj))
				return true;
		}
		return false;
	}
}
