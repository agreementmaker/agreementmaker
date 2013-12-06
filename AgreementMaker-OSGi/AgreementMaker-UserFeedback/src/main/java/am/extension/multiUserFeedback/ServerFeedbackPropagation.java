/*
 * 	Francesco Loprete December 2013
 */
package am.extension.multiUserFeedback;


import java.util.ArrayList;
import java.util.List;


import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.userfeedback.MLFeedback.NaiveBayes;
import am.matcher.Combination.CombinationMatcher;

public class ServerFeedbackPropagation extends MUFeedbackPropagation<MUExperiment> {


	final double treshold_up=0.6;
	final double treshold_down=0.1;
	final double penalize_ratio=0.9;
	private MUExperiment experiment;
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	


	
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

	@Override
	public void propagate(MUExperiment exp, String id) 
	{
		this.experiment=exp;

		Mapping candidateMapping = experiment.userFeedback.getCandidateMapping();
		
		experiment.getFinalAlignment();

		SimilarityMatrix feedbackClassMatrix=experiment.getUflClassMatrix();
		SimilarityMatrix feedbackPropertyMatrix=experiment.getUflPropertyMatrix();

		Object[][] trainingSet=experiment.feedbackStorage.getTrainingSet();
		

		

		if( candidateMapping.getAlignmentType() == alignType.aligningClasses )
		{
			feedbackClassMatrix=runNBayes(experiment.classesSparseMatrix , feedbackClassMatrix, trainingSet);
		}
		else
		{
			if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
			{
				feedbackPropertyMatrix=runNBayes(experiment.propertiesSparseMatrix, feedbackPropertyMatrix, trainingSet);
			}
		}
		
		AbstractMatcher ufl=new CombinationMatcher();
		ufl.setClassesMatrix(feedbackClassMatrix);
		ufl.setPropertiesMatrix(feedbackPropertyMatrix);
		ufl.select();

		experiment.setMLAlignment(combineResults(ufl, experiment));
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) 
		{
			experiment.setTrainingSet_classes(trainingSet);
		}
		else
		{
			experiment.setTrainingSet_property(trainingSet);
		}
		
	
		experiment.setUflClassMatrix(feedbackClassMatrix);
		experiment.setUflPropertyMatrix(feedbackPropertyMatrix);
		
		done();
	}
	
	

	
	private Alignment<Mapping> combineResults(AbstractMatcher am, MUExperiment experiment)
	{
		Alignment<Mapping> alg=new Alignment<Mapping>(0,0);
		int row=am.getClassesMatrix().getRows();
		int col=am.getClassesMatrix().getColumns();
		double ufl_sim=0;
		for (int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				ufl_sim=am.getClassesMatrix().getSimilarity(i, j);
				if (ufl_sim!=0.0)
					alg.add(experiment.initialMatcher.getFinalMatcher().getClassesMatrix().get(i, j));
			}
		}
		row=am.getPropertiesMatrix().getRows();
		col=am.getPropertiesMatrix().getColumns();
		ufl_sim=0;
		for (int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				ufl_sim=am.getPropertiesMatrix().getSimilarity(i, j);
				if (ufl_sim!=0.0)
					alg.add(experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix().get(i, j));
			}
		}
		
		return alg;
	}
	
	private SimilarityMatrix runNBayes(SparseMatrix forbidden_pos, SimilarityMatrix sm,Object[][] trainingSet)
	{

		
		int max_row=-1;
		int max_col=-1;
		double max_nBayes=treshold_up;
		double tmp; 
		Mapping mp;

		Object[] ssv;
		NaiveBayes nBayes=new NaiveBayes(trainingSet);
		for(int i=0;i<sm.getRows();i++)
		{
			max_nBayes=treshold_up;
			for(int j=0;j<sm.getColumns();j++)
			{
				if(forbidden_pos.getSimilarity(i, j)==1)
					continue;
				mp = sm.get(i, j);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
					continue;
				
				tmp=nBayes.interfaceComputeElement(ssv);
				if (tmp>max_nBayes)
				{
					max_nBayes=tmp;
					max_row=i;
					max_col=j;
				}
			}
			if (max_nBayes>treshold_up)
			{
				sm.setSimilarity(max_row, max_col, 1);
			}
		}
		return sm;
	}
	

}
