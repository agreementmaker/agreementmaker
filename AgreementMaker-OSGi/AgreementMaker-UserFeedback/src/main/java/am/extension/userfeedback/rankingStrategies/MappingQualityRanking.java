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
		//Collections.reverse(rankList);
		
		return rankList;
	}
	
	
	//Linear combination of CCQ and the inverse of SSH
	private List<Mapping> linearCombination(SimilarityMatrix mtrx)
	{
		Mapping mp=null;
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		CrossCountQuality ccq=new CrossCountQuality(mtrx);
		SimilarityScoreHardness ssh=new SimilarityScoreHardness(mtrx);
		for(int i=0;i<mtrx.getRows();i++)
		{
			for(int j=0;j<mtrx.getColumns();j++)
			{
				mp=mtrx.get(i, j);
				sim=alpha*ccq.getQuality(null, i, j)+beta*(1-ssh.getQuality(null, i, j));
				mp.setSimilarity(sim);
				lst.add(mp);
			}
		}
		return lst;
		
	}


}
