package am.extension.userfeedback.rankingStrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.CrossCountQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.SimilarityScoreHardness;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public class MappingQualityRanking implements StrategyInterface{

	private SimilarityMatrix classMatrix;
	private SimilarityMatrix propMatrix;
	private double alpha=1.0;
	private double beta=1.0;
	
	
	public MappingQualityRanking(SimilarityMatrix clMatrix, SimilarityMatrix prMatrix)
	{
		this.classMatrix=clMatrix;
		this.propMatrix=prMatrix;
	}
	
	@Override
	public List<Mapping> rank() {
		List<Mapping> rankList=linearCombination(classMatrix);
		rankList.addAll(linearCombination(propMatrix));
		Collections.sort(rankList, new MappingSimilarityComparator() );
		Collections.reverse(rankList);
		
		return rankList;
	}
	
	
	//Linear combination of CCQ and the inverse of SSH
	private List<Mapping> linearCombination(SimilarityMatrix mtrx)
	{
		List<Double> ssh_norm=new ArrayList<Double>();
		List<Double> ccq_norm=new ArrayList<Double>();
		Mapping mp=null;
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		CrossCountQuality ccq=new CrossCountQuality(mtrx);
		SimilarityScoreHardness ssh=new SimilarityScoreHardness(mtrx);
		for(int i=0;i<mtrx.getRows();i++)
		{
			for(int j=0;j<mtrx.getColumns();j++)
			{
				ccq_norm.add(ccq.getQuality(null, i, j));
				ssh_norm.add(1-ssh.getQuality(null, i, j));

			}
		}
		normalize(ccq_norm);
		normalize(ssh_norm);
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
