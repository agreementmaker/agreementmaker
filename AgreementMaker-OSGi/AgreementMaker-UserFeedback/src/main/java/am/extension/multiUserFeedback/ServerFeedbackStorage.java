package am.extension.multiUserFeedback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.MUExperiment.alignCardinality;
import am.extension.userfeedback.UserFeedback.Validation;

public class ServerFeedbackStorage extends MUFeedbackStorage<MUExperiment>{
	MUExperiment experiment;
	List<AbstractMatcher> inputMatchers;
	Object[][] trainingSet;
	@Override
	public void addFeedback(MUExperiment exp, Mapping candidateMapping, Validation val, String id) {
		this.experiment=exp;
		if (candidateMapping.getAlignmentType()==alignType.aligningClasses)
		{
			if (val==Validation.CORRECT)
				experiment.usersClass.get(id).setSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey(), 1);
			else 
				experiment.usersClass.get(id).setSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey(), 0);
		}
		else
		{
			if (val==Validation.CORRECT)
				experiment.usersProp.get(id).setSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey(), 1);
			else 
				experiment.usersProp.get(id).setSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey(), 0);
		}	
		
		inputMatchers=experiment.initialMatcher.getComponentMatchers();
		List<AbstractMatcher> availableMatchers = experiment.initialMatcher.getComponentMatchers();
		trainingSet=new Object[1][availableMatchers.size()];
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
			if (experiment.getTrainingSet_property() !=null)
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
		
		Mapping m=null;
		SimilarityMatrix feedbackClassMatrix=experiment.getUflClassMatrix();
		SimilarityMatrix feedbackPropertyMatrix=experiment.getUflPropertyMatrix();
		
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) 
		{
			m = feedbackClassMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) 
				m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 

				feedbackClassMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1.0);
				if (experiment.getAlignCardinalityType()==alignCardinality.c1_1)
					feedbackClassMatrix=(zeroSim(experiment.getUflClassMatrix(), candidateMapping.getSourceKey(), candidateMapping.getTargetKey(),1,1));
				experiment.classesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
				
			}
			else if( userFeedback == Validation.INCORRECT ) 
			{ 
				feedbackClassMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
				experiment.classesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
			}
			
		} 
		else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
		{
			m = feedbackPropertyMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) 
				m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 
				feedbackPropertyMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1.0);
				if (experiment.getAlignCardinalityType()==alignCardinality.c1_1)
					feedbackPropertyMatrix=zeroSim(experiment.getUflPropertyMatrix(), candidateMapping.getSourceKey(), candidateMapping.getTargetKey(),1,1);
				experiment.propertiesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
			}
			else if( userFeedback == Validation.INCORRECT ) 
			{
				feedbackPropertyMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
				experiment.propertiesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
			}
		}
	}
	
	private void cloneTrainingSet(Object[][] trainingSet, Object[][] vector)
	{
		
		for(int i=0;i<vector.length;i++)
			for(int j=0;j<vector[0].length;j++)
			{
				trainingSet[i][j]=vector[i][j];
			}
	}
	
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
	
	@Override
	public Object[][] getTrainingSet() {
		return trainingSet;
	}

	@Override
	public void computeFinalMatrix() {
		ComputeClasses();
		ComputeProperties();		
		
		
	}
	
	private void ComputeClasses()
	{
		SimilarityMatrix classes=experiment.getUflClassMatrix();
		for(int i=0;i<classes.getRows();i++)
		{
			for(int j=0;j<classes.getColumns();j++)
			{
				List<Mapping> lst=new ArrayList<Mapping>();
				for(Entry<String, SimilarityMatrix> entry : experiment.usersClass.entrySet())
				{
					SimilarityMatrix sm=entry.getValue();
					lst.add(sm.get(i, j));
				}
				classes.setSimilarity(i, j, combine(lst));
			}
		}
		experiment.setUflClassMatrix(classes);
	}
	
	private void ComputeProperties()
	{
		SimilarityMatrix classes=experiment.getUflPropertyMatrix();
		for(int i=0;i<classes.getRows();i++)
		{
			for(int j=0;j<classes.getColumns();j++)
			{
				List<Mapping> lst=new ArrayList<Mapping>();
				for(Entry<String, SimilarityMatrix> entry : experiment.usersProp.entrySet())
				{
					SimilarityMatrix sm=entry.getValue();
					lst.add(sm.get(i, j));
				}
				classes.setSimilarity(i, j, combine(lst));
			}
		}
		experiment.setUflPropertyMatrix(classes);
	}
	
	private double combine(List<Mapping> lst)
	{
		int count0=0;
		int count1=0;
		double sim=0;
		for(Mapping m :lst)
		{
			sim=m.getSimilarity();
			if (sim==1)
			{
				count1++;
			}
			if (sim==0)
			{
				count0++;
			}
		}
		if (count0==count1)
			return 0.5;
		if (count0>count1)
			return 0;
		if (count1>count0)
			return 1;
		return 0.5;
	}
	
	private SimilarityMatrix zeroSim(SimilarityMatrix sm,int source_index,int target_index, int sourceCardinality, int targetCardinality)
	{
		ArrayList<Integer> sourceToKeep=new ArrayList<Integer>();
		ArrayList<Integer> targetToKeep=new ArrayList<Integer>();
		if (sourceCardinality!=1)
		{
			sourceToKeep=topN(sm,-1,target_index,sourceCardinality);
		}
		
		if (targetCardinality!=1)
		{
			targetToKeep=topN(sm,source_index,-1,sourceCardinality);
		}
		sourceToKeep.add(source_index);
		targetToKeep.add(target_index);

		
		for(int i=0;i<sm.getRows();i++)
		{
			if (sourceToKeep.contains(i)) 
				continue;
			sm.setSimilarity(i, target_index, 0.0);		
		}
		for(int j=0;j<sm.getColumns();j++)
		{
			if (targetToKeep.contains(j)) 
				continue;
			sm.setSimilarity(source_index, j, 0.0);	
		}
		return sm;
	}
	
	private ArrayList<Integer> topN (SimilarityMatrix sm, int sourceIndex, int targetIndex, int topNumber)
	{
		ArrayList<Integer> top=new ArrayList<Integer>();
		Mapping[] tmp;
		if (targetIndex==-1)
		{
			tmp=sm.getRowMaxValues(sourceIndex, topNumber);	
			for (Mapping m : tmp)
			{
				top.add(m.getTargetKey());
			}
		}
		else
		{
			tmp=sm.getColMaxValues(targetIndex, topNumber);
			for (Mapping m : tmp)
			{
				top.add(m.getSourceKey());
			}
		}

		return top;
	}

}
