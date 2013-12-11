/*
 * 	Geli,Kush
 */
package am.extension.userfeedback.MLFeedback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.localByThreshold.LocalByThresholdMethod;
import am.evaluation.clustering.localByThreshold.LocalByThresholdParameters;
import am.extension.userfeedback.FeedbackPropagation;
import am.extension.userfeedback.UserFeedback.Validation;
import am.matcher.Combination.CombinationMatcher;

public class MyMLFeedbackPropagation extends FeedbackPropagation<MLFExperiment> {
	
	final double treshold_up=0.6;
	final double treshold_down=0.1;
	final double penalize_ratio=0.9;
	
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();

	private Object[] addToSV(Mapping mp, Boolean label)
	{
		//initialMatcher.
		int size=inputMatchers.size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		AbstractMatcher a;
		Object obj=new Object();
		Object[] ssv=new Object[size+1];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			obj=a.getAlignment().getSimilarity(sourceNode, targetNode);
			if (obj!=null)
				ssv[i]=obj;
			else
				ssv[i]=0.0;
			
		}
		if (label)
			ssv[size]=1.0;
		else
			ssv[size]=0.0;
		return ssv;
		
	}
	
	private void cloneTrainingSet(Object[][] trainingSet, Object[][] vector)
	{
		
		for(int i=0;i<vector.length;i++)
			for(int j=0;j<vector[0].length;j++)
			{
				trainingSet[i][j]=vector[i][j];
			}
	}
	
	@Override
	public void propagate( MLFExperiment experiment )
	{
		int iteration=experiment.getIterationNumber(); 
		inputMatchers=experiment.initialMatcher.getComponentMatchers(); // does not contain lwc
		Mapping candidateMapping = experiment.userFeedback.getCandidateMapping();
		List<AbstractMatcher> availableMatchers = experiment.initialMatcher.getComponentMatchers();
		Object[][] trainingSet=new Object[1][availableMatchers.size()+1];
		int trainset_index=0;
		
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses) 
		{
			if (experiment.getTrainingSet_classes() !=null)
			{
				trainingSet=new Object[experiment.getTrainingSet_classes().length+1][availableMatchers.size()+1];
				cloneTrainingSet(trainingSet, experiment.getTrainingSet_classes());
				trainset_index=experiment.getTrainingSet_classes().length;
			}
		}
		else
		{
			if (experiment.getTrainingSet_property() != null)
			{
				trainingSet=new Object[experiment.getTrainingSet_property().length+1][availableMatchers.size()+1];
				cloneTrainingSet(trainingSet, experiment.getTrainingSet_property());
				trainset_index=experiment.getTrainingSet_property().length;
			}
		}
		Validation userFeedback = experiment.userFeedback.getUserFeedback();
		if( userFeedback == Validation.CORRECT )
		{
			trainingSet[trainset_index]=addToSV(candidateMapping, true);
		}
		else
		{
			trainingSet[trainset_index]=addToSV(candidateMapping, false);
		}
		
		trainingSet=optimizeTrainingSet(trainingSet);

		SimilarityMatrix feedbackClassMatrix=experiment.getUflClassMatrix();
		SimilarityMatrix feedbackPropertyMatrix=experiment.getUflPropertyMatrix();
		Mapping m=null;
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) 
		{
			m = feedbackClassMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) 
				m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 
				feedbackClassMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1.0);
			}
			else if( userFeedback == Validation.INCORRECT ) 
			{ 
				feedbackClassMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
			}
			
		} else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) {
			m = feedbackPropertyMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) 
				m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 
				feedbackPropertyMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1.0);
			}
			else if( userFeedback == Validation.INCORRECT ) 
			{
				feedbackPropertyMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
			}
		}
		
		if (iteration >= 6) {
			
			LocalByThresholdMethod clusteringMethod = new LocalByThresholdMethod(
					availableMatchers);
			LocalByThresholdParameters clusteringParameters = new LocalByThresholdParameters();
			clusteringParameters.setMatchers(availableMatchers);
			clusteringParameters.clusteringThreshold = 0.1;
			clusteringMethod.setParameters(clusteringParameters);

			// compute the cluster of the validated mapping
			Cluster<Mapping> cluster = clusteringMethod
					.getCluster(candidateMapping);
			
			Object[][] xvalues = new Object[5][];
			int necessaryTraining = 5;
			while (necessaryTraining-- != 0) {
				xvalues[necessaryTraining] = trainingSet[trainingSet.length-necessaryTraining-1];
			}
			double[] weights = new double[]{0.2, 0.2, 0.2, 0.2, 0.2};
			
			for (Mapping mapping : cluster) {
				int r = mapping.getSourceKey();
				int c = mapping.getTargetKey();
				double combined = 0.0;
				
				if ( candidateMapping.getAlignmentType() == alignType.aligningClasses ) {
					for (int w=0; w<weights.length; w++) {
						for (int mat=0; mat<inputMatchers.size(); mat++) {
							combined += weights[w] * inputMatchers.get(mat).getClassesMatrix().getSimilarity(r,c);
						}
					}
					mapping.setSimilarity(combined);
					feedbackClassMatrix.set(r, c, mapping);
				}
				if (candidateMapping.getAlignmentType() == alignType.aligningProperties) {
					for (int w=0; w<weights.length; w++) {
						for (int mat=0; mat<inputMatchers.size(); mat++) {
							combined += weights[w] * inputMatchers.get(mat).getPropertiesMatrix().getSimilarity(r,c);
						}
					}
					mapping.setSimilarity(combined);
					feedbackPropertyMatrix.set(r, c, mapping);
				}
			} // for all clusters
			
		}
		
		
		// Don't touch below
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
	
	
	private Object[][] optimizeTrainingSet(Object[][] set)
	{
		List<Object[]> obj=new ArrayList<Object[]>(Arrays.asList(set));
		Object[] invalidSV=new Object[set[0].length];
		for(int i=0;i<invalidSV.length;i++)
			invalidSV[i]=0.0;
		obj.remove(Arrays.asList(invalidSV));
		
		set=obj.toArray(set);
		
		
		return set;
	}
	

	
	private Alignment<Mapping> combineResults(AbstractMatcher am, MLFExperiment experiment)
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
	

}

