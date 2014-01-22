package am.extension.multiUserFeedback.propagation;
/*
 * 	Francesco Loprete December 2013
 */


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.tomgibara.cluster.gvm.dbl.DblResult;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.MLutility.WekaUtility;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.propagation.FeedbackPropagation;
import am.extension.userfeedback.utility.UFLutility;
import am.matcher.Combination.CombinationMatcher;

public class ServerFeedbackPropagation extends FeedbackPropagation<MUExperiment>{

	private class ClusterData {
		int clusterID;
		int clusterSize;
		int correctMappings;
	}

	final double treshold_up=0.6;
	final double treshold_down=0.1;
	final double penalize_ratio=0.9;
	final double log_multiplier=1.2;
	final double alpha=0.2;
	private MUExperiment experiment;
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	
	public static final String PROPAGATION_NONE 		= "none";
	public static final String PROPAGATION_EUCLIDEAN 	= "euzero";
	public static final String PROPAGATION_LOG 			= "logdist";
	public static final String PROPAGATION_REGRESSION 	= "regression";
	public static final String PROPAGATION_CRC 	= "crc";
	
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
		
		try {
			writeSimilarityMatrix(feedbackClassMatrix,exp.getIterationNumber(),"Classes");
			writeSimilarityMatrix(feedbackPropertyMatrix,exp.getIterationNumber(),"Properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final String metric = experiment.setup.parameters.getParameter(Parameter.PROPAGATION_METHOD);
		
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses )
		{
			feedbackClassMatrix = runPropagation(candidateMapping.getAlignmentType(), metric);
			feedbackPropertyMatrix = experiment.getComputedUFLMatrix(alignType.aligningProperties);
			/*feedbackClassMatrix=euclideanDistance(
					experiment.getForbiddenPositions(alignType.aligningClasses),
					feedbackClassMatrix,
					trainingSet,
					alignType.aligningClasses);*/
		}
		else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
		{
			feedbackClassMatrix = experiment.getComputedUFLMatrix(alignType.aligningClasses);
			feedbackPropertyMatrix = runPropagation(candidateMapping.getAlignmentType(), metric);
//			feedbackPropertyMatrix=euclideanDistance(
//					experiment.getForbiddenPositions(alignType.aligningProperties),
//					feedbackPropertyMatrix,
//					trainingSet,
//					alignType.aligningProperties);
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
//		switch(metric) {
//		case PROPAGATION_NONE:
//			return experiment.getComputedUFLMatrix(type);
//		case PROPAGATION_EUCLIDEAN:
//			return euclideanDistance(
//					experiment.getForbiddenPositions(type), 
//					experiment.getComputedUFLMatrix(type),
//					experiment.getTrainingSet(type),
//					type);
//		case PROPAGATION_LOG:
//			return logDistance(
//					experiment.getForbiddenPositions(type), 
//					experiment.getComputedUFLMatrix(type),
//					experiment.getTrainingSet(type),
//					type);
//		case PROPAGATION_REGRESSION:
//			return wekaRegression(
//					experiment.getForbiddenPositions(type),
//					experiment.getComputedUFLMatrix(type),
//					experiment.getTrainingSet(type));
//		case PROPAGATION_CRC:
//			return clusterRowColumnPropagation(experiment.getForbiddenPositions(type), 
//					experiment.getComputedUFLMatrix(type),experiment.userFeedback.getCandidateMapping(),
//					experiment.userFeedback.getUserFeedback(), type);
//		default:
//			throw new RuntimeException("Propagation method was not correctly specificied.");
//		}
		return clusterRowColumnPropagation(experiment.getForbiddenPositions(type), 
				experiment.getComputedUFLMatrix(type),experiment.userFeedback.getCandidateMapping(),
				experiment.userFeedback.getUserFeedback(), type);
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
					if (sim<1.0)
						sm.setSimilarity(k, h, sim);
					else
						sm.setSimilarity(k, h, 1.0d);
					
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
					sim*=log_multiplier;
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
	
	private void writeSimilarityMatrix(SimilarityMatrix sm, int iteration, String type) throws IOException
	{
		File dir = new File(Core.getInstance().getRoot() + "settings/tmp/SimilarityMatrix"+type);
		if( !dir.exists() )
			dir.mkdirs();
		File file = new File(dir.getAbsolutePath() + "/similarityMatrix_"+iteration+".txt");
		// if file doesnt exists, then create it
		if (!file.exists()) 
			file.createNewFile();
		FileWriter fw=null;

		fw = new FileWriter(file.getAbsoluteFile());

		BufferedWriter bw = new BufferedWriter(fw);

		for(int i=-1;i<sm.getRows();i++)
		{

			bw.write(i+"\t");
			for (int j=0;j<sm.getColumns();j++)
			{
				if (i==-1)
				{
					bw.write(j+"\t");
				}
				else
				{
					bw.write(round(sm.getSimilarity(i, j),2)+"");
					if (j<sm.getColumns()-1)
						bw.write("\t");
				}
				
			}
			bw.write("\n");
		}

		bw.close();
	}
	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	
	private SimilarityMatrix clusterRowColumnPropagation(SimilarityMatrix forbidden_pos, SimilarityMatrix sm, Mapping candidateMapping, Validation val, alignType type)
	{
		
		Mapping mp;
		Object[] ssv;
		double deltaSim=alpha;
		double sim=0;
		List<List<Mapping>> lst=UFLutility.getRelations(sm, experiment.initialMatcher.getComponentMatchers());
		
		for (List<Mapping> l : lst)
		{
			if (l.contains(candidateMapping))
			{
				for (Mapping m :l)
				{
					if (!m.equals(candidateMapping))
					{
						if (val.equals(Validation.CORRECT))
						{
							sim=sm.getSimilarity(m.getSourceKey(), m.getTargetKey());
							sim=UFLutility.ammortizeSimilarity(sim, (deltaSim)*(-1));
							sm.setSimilarity(m.getSourceKey(), m.getTargetKey(), sim);
						}
						if (val.equals(Validation.INCORRECT))
						{
							sim=sm.getSimilarity(m.getSourceKey(), m.getTargetKey());
							sim=UFLutility.ammortizeSimilarity(sim, (deltaSim));
							sm.setSimilarity(m.getSourceKey(), m.getTargetKey(), sim);
						}
					}
				}
			}
		}
		
		
		List<Node> sourceClasses = experiment.initialMatcher.getFinalMatcher().getSourceOntology().getClassesList();
		List<Node> targetClasses = experiment.initialMatcher.getFinalMatcher().getTargetOntology().getClassesList();
		List<DblResult<List<double[]>>> clusters = experiment.cluster;
		Alignment<Mapping> classesAlignment = experiment.initialMatcher.getFinalMatcher().getClassAlignmentSet();
		//
		List<ClusterData> clusterData = new ArrayList<ClusterData>();
		//loop thru clusters, for each cluster, there is a cluster KEY [for each key we have a 
		// row and column of mapping. keys is a list, of all of the mappings in the cluster
		// and the mappings are identified by row and column stored in array of doubles. 
		
		// every mapping gets a number inside a NEW matrix 
		for( int i = 0; i < clusters.size(); i++ ) {
			DblResult<List<double[]>> currentCluster = clusters.get(i);
			List<double[]> clusterKey = currentCluster.getKey();
			
			ClusterData cd = new ClusterData();
			cd.clusterID = i;
			cd.clusterSize = clusterKey.size();
			cd.correctMappings = 0;
			
			for( int j = 0; j < clusterKey.size(); j++ ) {
				double[] point = clusterKey.get(j);
				
				Node sourceNode = sourceClasses.get((int)point[point.length-2]);
				Node targetNode = targetClasses.get((int)point[point.length-1]);
				
				if( classesAlignment.contains( sourceNode, targetNode) != null )  {
					cd.correctMappings++;
				}
				/*if( classesMatrix.getSimilarity( (int)point[point.length-2], (int)point[point.length-1]) > 0.0d )
					cd.correctMappings++;*/
			}
			
			clusterData.add(cd);
		}
		
		

		return sm;
	}




}
