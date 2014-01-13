package am.extension.multiUserFeedback.propagation;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.propagation.FeedbackPropagation;
import am.matcher.Combination.CombinationMatcher;

public class MUFeedbackPropagation  extends FeedbackPropagation<MUExperiment> {
		
		final double treshold_up=0.6;
		final double treshold_down=0.01;
		final double penalize_ratio=0.9;
		private MUExperiment experiment;
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
		
		private void cloneTrainingSet(Object[][] trainingSet, Object[][] vector)
		{
			
			for(int i=0;i<vector.length;i++)
				for(int j=0;j<vector[0].length;j++)
				{
					trainingSet[i][j]=vector[i][j];
				}
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
		public void propagate( MUExperiment exp ) 
		{
			this.experiment=exp;
			
			inputMatchers=experiment.initialMatcher.getComponentMatchers();
			Mapping candidateMapping = experiment.selectedMapping;
			List<AbstractMatcher> availableMatchers = experiment.initialMatcher.getComponentMatchers();
			Object[][] trainingSet = null;
			
			Validation userFeedback = experiment.userFeedback.getUserFeedback();
						
			SimilarityMatrix uflMatrix = experiment.getComputedUFLMatrix(candidateMapping.getAlignmentType());
			
			Mapping m = uflMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) {
				uflMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1.0);
			}
			else {
				uflMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
				experiment.getForbiddenPositions(candidateMapping.getAlignmentType());
			}
				
			SimilarityMatrix forbiddenPositions = experiment.getForbiddenPositions(candidateMapping.getAlignmentType());
			forbiddenPositions.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1d);
			
			if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) 
			{
				trainingSet=exp.getTrainingSet_classes();				
			} 
			else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
			{
				trainingSet=exp.getTrainingSet_property();
			}
			
			trainingSet=optimizeTrainingSet(trainingSet);
			
			uflMatrix = com(forbiddenPositions, uflMatrix, trainingSet, alignType.aligningClasses);
			
			
			AbstractMatcher ufl = new CombinationMatcher();
			ufl.setClassesMatrix(experiment.getComputedUFLMatrix(alignType.aligningClasses));
			ufl.setPropertiesMatrix(experiment.getComputedUFLMatrix(alignType.aligningProperties));

			experiment.setMLAlignment(combineResults(ufl));
		
			done();
		}
		
		
		private Object[][] optimizeTrainingSet(Object[][] set)
		{
			int count=0;
			List<Integer> pos=new ArrayList<Integer>();
			for(int i=0;i<set.length;i++)
			{
				count=0;
				for(int j=0;j<set[0].length;j++)
				{
					if((Double)set[i][j]==0.0)
						count++;
				}
				if (count==set[0].length)
					pos.add(i);
			}
			Object[][] newSet=new Object[set.length-pos.size()][set[0].length];
			int index=0;
			if (pos.size()!=0)
			{
				for(int i=0;i<set.length;i++)
				{
					if (pos.contains(i))
						continue;
					
					for(int j=0;j<set[0].length;j++)
					{
						newSet[index][j]=set[i][j];
					}
					index++;
				}
				set=newSet;
			}
			
			return set;
		}
		
				
		private Alignment<Mapping> combineResults(AbstractMatcher am)
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
		
		

		private SimilarityMatrix com(SimilarityMatrix forbidden_pos, SimilarityMatrix sm,Object[][] trainingSet, alignType type)
		{
			Mapping mp;
			Object[] ssv;
			
			double distance=0;
			double min=Double.MAX_VALUE;
			int index=0;
			for(int k=0;k<sm.getRows();k++)
			{
				for(int h=0;h<sm.getColumns();h++)
				{
					if(forbidden_pos.getSimilarity(k, h)==1)
						continue;
					mp = sm.get(k, h);
					ssv=getSignatureVector(mp);
					if (!validSsv(ssv))
						continue;
					min=Double.MAX_VALUE;
					for(int i=0;i<trainingSet.length;i++)
					{
						distance=0;
						for(int j=0;j<ssv.length;j++)
						{
							distance+=Math.pow((double)ssv[j]-(double)trainingSet[i][j],2);
						}
						distance=Math.sqrt(distance);
						if (distance<min)
						{
							min=distance;
							index=i;
						}
					}
					if (min==0)
					{
						sm.setSimilarity(k, h, (double)trainingSet[index][trainingSet[0].length-1]);
						experiment.getForbiddenPositions(type).setSimilarity(k, h, 1d);
					}
				}
			}
			return sm;
		}
		

	}
