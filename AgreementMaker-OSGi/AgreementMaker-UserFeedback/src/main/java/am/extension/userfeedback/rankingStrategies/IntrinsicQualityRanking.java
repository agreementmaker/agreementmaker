package am.extension.userfeedback.rankingStrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.CrossCountQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.SimilarityScoreHardness;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.extension.multiUserFeedback.experiment.MUExperiment;

public class IntrinsicQualityRanking extends AbstractRankingStrategy {

	private SimilarityMatrix classMatrix;
	private SimilarityMatrix propMatrix;
	private SimilarityMatrix forbiddenClass;
	private SimilarityMatrix forbiddnProp;
	private double alpha=1.0;
	private double beta=1.0;
	private MUExperiment experiment;
	
	
	public IntrinsicQualityRanking(MUExperiment experiment)
	{
		this.experiment = experiment;
	}
	
	@Override
	public void rank() {
		classMatrix = experiment.getComputedUFLMatrix(alignType.aligningClasses);
		propMatrix = experiment.getComputedUFLMatrix(alignType.aligningProperties);
		forbiddenClass = experiment.getForbiddenPositions(alignType.aligningClasses);
		forbiddnProp = experiment.getForbiddenPositions(alignType.aligningProperties);
		
		rankedList=linearCombination(classMatrix, forbiddenClass);
		rankedList.addAll(linearCombination(propMatrix,forbiddnProp));
		Collections.sort(rankedList, new MappingSimilarityComparator() );
		Collections.reverse(rankedList);
	}
	
	
	//Linear combination of CCQ and the inverse of SSH
	private List<Mapping> linearCombination(SimilarityMatrix mtrx, SimilarityMatrix forbidden)
	{
		List<Double> ssh_norm=new ArrayList<Double>();
		List<Double> ccq_norm=new ArrayList<Double>();
		Mapping mp=null;
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		CrossCountQuality ccq=new CrossCountQuality(mtrx,forbidden);
		SimilarityScoreHardness ssh=new SimilarityScoreHardness(mtrx);
		for(int i=0;i<mtrx.getRows();i++)
		{
			for(int j=0;j<mtrx.getColumns();j++)
			{
				ccq_norm.add(ccq.getQuality(null, i, j));
				ssh_norm.add(1-ssh.getQuality(null, i, j));

			}
		}
		//normalize(ccq_norm);
		//normalize(ssh_norm);
		int count=0;
		for(int i=0;i<mtrx.getRows();i++)
		{
			for(int j=0;j<mtrx.getColumns();j++)
			{
				mp=mtrx.get(i, j);
				sim=alpha*ccq_norm.get(count)+beta*ssh_norm.get(count);
				mp.setSimilarity(sim);
				lst.add(mp);
				count++;
			}
		}
		return lst;
		
	}

	
	private void normalize(List<Double> lst)
	{
		double max=Double.MIN_VALUE;
		double min=Double.MAX_VALUE;
		
		for(Double d :lst)
		{
			if (d<min)
				min=d;
			if (d>max) max=d;	
		}
		for(int i=0;i<lst.size();i++)
		{
			double tmp=lst.get(i);
			tmp=(tmp-min)/(max-min);
			lst.set(i, tmp);
		}
		
	}

}
