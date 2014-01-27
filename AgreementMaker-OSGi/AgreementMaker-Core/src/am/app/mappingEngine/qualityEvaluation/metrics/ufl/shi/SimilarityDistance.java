package am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;


public class SimilarityDistance extends AbstractQualityMetric{
		
	private SimilarityMatrix amScoreClass;
	private SimilarityMatrix amScoreProp;
	
	public SimilarityDistance(SimilarityMatrix amClass, SimilarityMatrix amProp)
	{
		super();
		this.amScoreClass=amClass;
		this.amScoreProp=amProp;
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		
		
		SimilarityMatrix sm=amScoreClass;
		double min=Double.MAX_VALUE;
		double tmp=0;
		if (type.equals(alignType.aligningProperties))
			sm=amScoreProp; 
		
		if (sm.getSimilarity(i, j)==0.0d) 
			return 0.0d;
		
		for (int k=0;k<sm.getRows();k++)
		{
			double sim=sm.getSimilarity(k, j);
			if ((sim>0d) && (k!=i))
			{
				tmp=Math.abs(sm.getSimilarity(i, j)-sim);
				if (min>tmp)
					min=tmp;
			}
		}
		for (int k=0;k<sm.getColumns();k++)
		{
			double sim=sm.getSimilarity(i, k);
			if ((sim>0d) && (k!=j))
			{
				tmp=Math.abs(sm.getSimilarity(i, j)-sim);
				if (min>tmp)
					min=tmp;
			}
		}
		
		return min;

		
	}
}
