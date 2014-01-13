package am.extension.multiUserFeedback.propagation;
/*
 * 	Francesco Loprete December 2013
 */


import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.MLutility.WekaUtility;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.propagation.FeedbackPropagation;
import am.matcher.Combination.CombinationMatcher;

public class ServerFeedbackPropagation extends FeedbackPropagation<MUExperiment>{


	final double treshold_up=0.6;
	final double treshold_down=0.1;
	final double penalize_ratio=0.9;
	private MUExperiment experiment;
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	
	public static final String PROPAGATION_NONE 		= "none";
	public static final String PROPAGATION_EUCLIDEAN 	= "euzero";
	public static final String PROPAGATION_LOG 			= "logdist";
	public static final String PROPAGATION_REGRESSION 	= "regression";
	
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
	public void propagate(MUExperiment exp) 
	{
		this.experiment=exp;
		
		Mapping candidateMapping = experiment.userFeedback.getCandidateMapping();
		inputMatchers=experiment.initialMatcher.getComponentMatchers();
		experiment.getFinalAlignment();

		SimilarityMatrix feedbackClassMatrix = experiment.getComputedUFLMatrix(alignType.aligningClasses);
		SimilarityMatrix feedbackPropertyMatrix = experiment.getComputedUFLMatrix(alignType.aligningProperties);
		
		final String metric = experiment.setup.parameters.getParameter(Parameter.PROPAGATION_METHOD);
		
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses )
		{
			feedbackClassMatrix = runPropagation(candidateMapping.getAlignmentType(), metric);
			feedbackPropertyMatrix = experiment.getComputedUFLMatrix(alignType.aligningProperties);
		}
		else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
		{
			feedbackClassMatrix = experiment.getComputedUFLMatrix(alignType.aligningClasses);
			feedbackPropertyMatrix = runPropagation(candidateMapping.getAlignmentType(), metric);
		}
		
		AbstractMatcher ufl=new CombinationMatcher();
		ufl.setClassesMatrix(feedbackClassMatrix);
		ufl.setPropertiesMatrix(feedbackPropertyMatrix);
		ufl.select();

		experiment.setMLAlignment(combineResults(ufl, experiment));
		
		experiment.setComputedUFLMatrix(alignType.aligningClasses, feedbackClassMatrix);
		experiment.setComputedUFLMatrix(alignType.aligningProperties, feedbackPropertyMatrix);
		
		done();
	}
	

	private SimilarityMatrix runPropagation(alignType type, String metric) {
		switch(metric) {
		case PROPAGATION_NONE:
			return experiment.getComputedUFLMatrix(type);
		case PROPAGATION_EUCLIDEAN:
			return euclideanDistance(
					experiment.getForbiddenPositions(type), 
					experiment.getComputedUFLMatrix(type),
					experiment.getTrainingSet(type),
					type);
		case PROPAGATION_LOG:
			return logDistance(
					experiment.getForbiddenPositions(type), 
					experiment.getComputedUFLMatrix(type),
					experiment.getTrainingSet(type),
					type);
		case PROPAGATION_REGRESSION:
			return wekaRegression(
					experiment.getForbiddenPositions(type),
					experiment.getComputedUFLMatrix(type),
					experiment.getTrainingSet(type));
		default:
			throw new RuntimeException("Propagation method was not correctly specificied.");
		}
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
	
	private SimilarityMatrix euclideanDistance(SparseMatrix forbidden_pos, SimilarityMatrix sm,Object[][] trainingSet, alignType type)
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

				if ((min==0.0))
				{
					sm.setSimilarity(k, h, (double)trainingSet[index][trainingSet[0].length-1]);
//					if (type.equals(alignType.aligningClasses))
//						experiment.forbiddenPositionsClasses.setSimilarity(k, h, 1);
//					else
//						experiment.forbiddenPositionsProperties.setSimilarity(k, h, 1);
					
				}

			}
		}

		return sm;
	}
	
	private SimilarityMatrix wekaRegression(SparseMatrix sparse,SimilarityMatrix sm,Object[][] trainingSet)
	{
		WekaUtility wk=new WekaUtility();
		wk.setTrainingSet(trainingSet);
		
		Mapping mp;
		Object[] ssv;
		double sim;

		for(int k=0;k<sm.getRows();k++)
		{
			for(int h=0;h<sm.getColumns();h++)
			{
				if(sparse.getSimilarity(k, h)==1)
					continue;
				mp = sm.get(k, h);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
					continue;
				sim=wk.runRegression(ssv);
				
				if ((sim>0.0))
				{
					sm.setSimilarity(k, h, sim);
					
				}
			}
		}
		
		return sm;
	}
	
	private SimilarityMatrix wekaSVM(SparseMatrix sparse,SimilarityMatrix sm,Object[][] trainingSet)
	{
		WekaUtility wk=new WekaUtility();
		wk.setTrainingSet(trainingSet);
		
		Mapping mp;
		Object[] ssv;
		double sim;

		for(int k=0;k<sm.getRows();k++)
		{
			for(int h=0;h<sm.getColumns();h++)
			{
				if(sparse.getSimilarity(k, h)==1)
					continue;
				mp = sm.get(k, h);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
					continue;
				sim=wk.runKNN(ssv);
				
				if ((sim>0.0))
				{
					sm.setSimilarity(k, h, sim);
					
				}
			}
		}
		
		return sm;
	}
	
	private SimilarityMatrix logDistance(SparseMatrix forbidden_pos, SimilarityMatrix sm,Object[][] trainingSet, alignType type)
	{
		Mapping mp;
		Object[] ssv;
		double sim=0;
		double distance=0;
		int count=0;
		double minDistance=Double.MAX_VALUE;
		double avgDistance=0;
		int index=0;;
		for(int k=0;k<sm.getRows();k++)
		{
			for(int h=0;h<sm.getColumns();h++)
			{
				minDistance=Double.MAX_VALUE;
				if(forbidden_pos.getSimilarity(k, h)==1)
					continue;
				mp = sm.get(k, h);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
					continue;
				minDistance=Double.MAX_VALUE;
				for(int i=0;i<trainingSet.length;i++)
				{
					distance=0;
					for(int j=0;j<ssv.length;j++)
					{
						count++;
						distance+=Math.pow((double)ssv[j]-(double)trainingSet[i][j],2);
					}
					distance=Math.sqrt(distance);
					avgDistance+=distance;
					if (distance<minDistance)
					{
						minDistance=distance;
						index=i;
					}
				}
				avgDistance=avgDistance/count;
//				System.out.println(mp.toString());
//				System.out.println("AVG DISTANCE: "+avgDistance);
				if ((minDistance<avgDistance))
				{
					sim=Math.log(2-minDistance) / Math.log(2);
					if ((double)trainingSet[index][trainingSet[0].length-1]==1.0)
						sim=sm.getSimilarity(k, h)+sim;
					else
						sim=sm.getSimilarity(k, h)-sim;
					
					if (sim>1.0) sim=1.0;
					if (sim<0.0) sim=0.0;
					
					sm.setSimilarity(k, h, sim);
					
//					if (type.equals(alignType.aligningClasses))
//						experiment.forbiddenPositionsClasses.setSimilarity(k, h, 1);
//					else
//						experiment.forbiddenPositionsProperties.setSimilarity(k, h, 1);
				}
			}
		}
		
		return sm;
	}




}
