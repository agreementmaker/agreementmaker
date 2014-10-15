package am.extension.multiUserFeedback.propagation;
/*
 * 	Francesco Loprete December 2013
 */


import java.io.IOException;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.qualityEvaluation.metrics.InverseOf;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.ConsensusQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.PropagationImpactMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.MLutility.WekaUtility;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.propagation.FeedbackPropagation;
import am.extension.userfeedback.rankingStrategies.MultiSelectedRanking;
import am.extension.userfeedback.utility.UFLutility;


public class ServerFeedbackPropagation extends FeedbackPropagation<MUExperiment>{


	final double log_multiplier=1.2;
	final double dist_perc=3.0;
	
	private MUExperiment experiment;
	private List<MatchingTask> inputMatchers;
	
	public static final String PROPAGATION_NONE 		= "none";
	public static final String PROPAGATION_EUCLIDEAN 	= "euzero";
	public static final String PROPAGATION_LOG 			= "logdist";
	public static final String PROPAGATION_REGRESSION 	= "regression";
	public static final String PROPAGATION_QUALITY 		= "quality";
	public static final String PROPAGATION_CRC 	= "crc";
	
	private double[] getSignatureVector(Mapping mp)
	{
		int size=inputMatchers.size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		MatchingTask a;
		double[] ssv=new double[size];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			ssv[i]=a.selectionResult.getAlignment().getSimilarity(sourceNode, targetNode);
			
		}
		return ssv;
	}
	
	
	//check if the signature vector is valid. A valid signature vector must have at least one non zero element.
	private boolean validSsv(double[] ssv)
	{
		double obj=0.0;
		for(int i=0;i<ssv.length;i++)
		{
			if (!(ssv[i] == obj))
				return true;
		}
		return false;
	}

	@Override
	public void propagate(MUExperiment exp) 
	{
		this.experiment=exp;

		if( inputMatchers == null ) 
			inputMatchers = experiment.initialMatcher.getComponentMatchers();
		
		Mapping candidateMapping = experiment.userFeedback.getCandidateMapping();
		SimilarityMatrix feedbackClassMatrix = experiment.getComputedUFLMatrix(alignType.aligningClasses);
		SimilarityMatrix feedbackPropertyMatrix = experiment.getComputedUFLMatrix(alignType.aligningProperties);
		
		// save the current matrices for debugging
		try {
			writeSimilarityMatrix(feedbackClassMatrix,exp.getIterationNumber(),"Classes");
			writeSimilarityMatrix(feedbackPropertyMatrix,exp.getIterationNumber(),"Properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final String metric = experiment.setup.parameters.getParameter(Parameter.PROPAGATION_METHOD);

		// run the propagation and store the new matrices
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) {
			feedbackClassMatrix = runPropagation(candidateMapping.getAlignmentType(), metric);
			experiment.setComputedUFLMatrix(alignType.aligningClasses, feedbackClassMatrix);
		}
		else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) { 
			feedbackPropertyMatrix = runPropagation(candidateMapping.getAlignmentType(), metric);
			experiment.setComputedUFLMatrix(alignType.aligningProperties, feedbackPropertyMatrix);
		}
						
		// create the new alignment
		Alignment<Mapping> alignment = UFLutility.computeAlignment(
				experiment.getSourceOntology(),
				experiment.getTargetOntology(), 
				feedbackClassMatrix,
				feedbackPropertyMatrix, 
				experiment.setup.parameters.getDoubleParameter(Parameter.IM_THRESHOLD),
				experiment.initialMatcher.getFinalMatcher().getParam().maxSourceAlign,
				experiment.initialMatcher.getFinalMatcher().getParam().maxTargetAlign);
		
		experiment.setMLAlignment(alignment);
		
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
					experiment.feedbackAggregation.getTrainingSet(type, "single"),
					type);
		case PROPAGATION_LOG:
			return logDistance(
					experiment.getForbiddenPositions(type), 
					experiment.getComputedUFLMatrix(type),
					experiment.feedbackAggregation.getTrainingSet(type, "single"),
					type);
		case PROPAGATION_REGRESSION:
			return wekaRegression(
					experiment.getForbiddenPositions(type),
					experiment.getComputedUFLMatrix(type),
					experiment.feedbackAggregation.getTrainingSet(type, "multi")
					);
		case PROPAGATION_QUALITY:
			return qualityPropagation(
					experiment.getForbiddenPositions(type), 
					experiment.getComputedUFLMatrix(type),
					experiment.feedbackAggregation.getTrainingSet(type, "single"),
					type);
		default:
			throw new RuntimeException("Propagation method was not correctly specificied.");
		}

	}

	

	
	private SimilarityMatrix euclideanDistance(SparseMatrix forbidden_pos, SimilarityMatrix sm, double[][] trainingSet, alignType type)
	{
		if (trainingSet==null)
			return sm;
		Mapping mp;
		double[] ssv;
		double threshold=(trainingSet.length-1)*0.0/100;
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

				if (min<=threshold)
				{
					sm.setSimilarity(k, h, (double)trainingSet[index][trainingSet[0].length-1]);
				
				}

			}
		}

		return sm;
	}
	
	private SimilarityMatrix wekaRegression(SparseMatrix sparse,SimilarityMatrix sm, double[][] trainingSet)
	{
		if (trainingSet==null)
			return sm;
		WekaUtility wk=new WekaUtility();
		wk.setTrainingSet(trainingSet);
		
		Mapping mp;
		double[] ssv;
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
					if (sim<1.0)
						sm.setSimilarity(k, h, sim);
					else
						sm.setSimilarity(k, h, 1.0d);
					
				}
			}
		}
		
		return sm;
	}
	

	
	private SimilarityMatrix logDistance(SparseMatrix forbidden_pos, SimilarityMatrix sm, double[][] trainingSet, alignType type)
	{
		if (trainingSet==null)
			return sm;
		Mapping mp;
		double[] ssv;
		double threshold=(trainingSet.length-1)*dist_perc/100;
		double sim=0;
		double distance=0;
		double minDistance=Double.MAX_VALUE;
		int index=0;;
		for(int k=0;k<sm.getRows();k++)
		{
			for(int h=0;h<sm.getColumns();h++)
			{
				minDistance=Double.MAX_VALUE;
				if(forbidden_pos.getSimilarity(k, h)==1)
					continue;
				mp = sm.get(k, h);
				ssv = getSignatureVector(mp);
				if (!validSsv(ssv))
					continue;
				minDistance=Double.MAX_VALUE;
				for(int i=0;i<trainingSet.length;i++)
				{
					distance=0;
					for(int j=0;j<ssv.length;j++)
					{
						
						distance+=Math.pow((double)ssv[j]-(double)trainingSet[i][j],2);
					}
					distance=Math.sqrt(distance);
					if (distance<minDistance)
					{
						minDistance=distance;
						index=i;
					}
				}

				if ((minDistance<=threshold))
				{
					sim=Math.log(2-minDistance) / Math.log(2);
					sim*=log_multiplier;
					if ((double)trainingSet[index][trainingSet[0].length-1]==1.0)
						sim=sm.getSimilarity(k, h)+sim;
					else
						sim=sm.getSimilarity(k, h)-sim;
					
					if (sim>1.0) sim=1.0;
					if (sim<0.0) sim=0.0;
					
					sm.setSimilarity(k, h, sim);
					
				}
			}
		}
		
		return sm;
	}
	
	/** Save the similarity matrices for debugging. */
	private void writeSimilarityMatrix(SimilarityMatrix sm, int iteration, String type) throws IOException
	{
		String fileName = "settings/tmp/SimilarityMatrix" + type + "/similarityMatrix_" + iteration + ".txt";
		UFLutility.saveSimilarityMatrix(fileName, sm);
	}
	

	
	

	private SimilarityMatrix qualityPropagation(SparseMatrix forbidden_pos, SimilarityMatrix sm, double[][] trainingSet, alignType type)
	{
		if (trainingSet==null)
			return sm;
		Mapping mp;
		double[] ssv;
		double threshold=(trainingSet[0].length-1)*dist_perc/100;
		double distance=0;
		double min=Double.MAX_VALUE;
		int index=0;
		
		double ts_quality=0.0;
		double mp_quality=0.0;
		
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

				if (min<=threshold)
				{
					ts_quality=getTSquality(experiment.feedbackAggregation.getCandidateMapping(),type);
					mp_quality=getMquality(sm.get(k, h), type);
					double label=trainingSet[index][trainingSet[0].length-1] == 1.0 ? 1.0 : -1.0;
					
					
					double delta=ts_quality*(1-(mp_quality)/2)*label;
					double sim=sm.getSimilarity(k, h);
					sim+=delta;
					if (sim>1.0)
						sim=1.0;
					if (sim<0.0)
						sim=0.0;
					sm.setSimilarity(k, h, sim);
				
				}

			}
		}

		
		return sm;
	}
	
	
	private double getTSquality(Mapping m, alignType type)
	{
		double quality=0.0;
		PropagationImpactMetric pi=new PropagationImpactMetric(experiment.getFeedbackMatrix(type, Validation.CORRECT),
				experiment.getFeedbackMatrix(type, Validation.INCORRECT),
				5);
		ConsensusQuality cq=new ConsensusQuality(experiment.getFeedbackMatrix(type, Validation.CORRECT),
				experiment.getFeedbackMatrix(type, Validation.INCORRECT),
				5);
		InverseOf invPI=new InverseOf(pi);
		quality=(invPI.getQuality(type, m.getSourceKey(), m.getTargetKey())+cq.getQuality(type, m.getSourceKey(), m.getTargetKey()))/2;
		return quality;
	}
	
	private double getMquality(Mapping m, alignType type)
	{
		double quality=0.0;
		String[] metric=new String[3];
		metric[0]="dis";
		metric[1]="csq";
		metric[2]="ssh";
		String combinationMethod="lwc";
		MultiSelectedRanking msr=new MultiSelectedRanking(experiment, metric, combinationMethod, null);
		quality=msr.getQuality(type, m.getSourceKey(), m.getTargetKey());
		return 1-quality;
	}



}
