package am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi;


import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public class ContentionPoint  extends AbstractQualityMetric{
		
	private double threshold;
	List<Mapping> lst=new ArrayList<>();
	SimilarityMatrix ufl;
	List<MatchingTask> amInput=new ArrayList<>();
	
	public ContentionPoint(SimilarityMatrix am, List<MatchingTask> inputMatchers, alignType type, double threshold)
	{
		super();
		this.threshold=threshold;
		this.ufl=am;
		this.amInput=inputMatchers;
		double sim=0.0;
		int countP=0;
		int countN=0;
		for (int i=0;i<am.getRows();i++)
			for(int j=0;j<am.getColumns();j++)
			{
				countP=0;
				countN=0;
				for (int k=0; k<inputMatchers.size();k++)
				{
					if (type.equals(alignType.aligningClasses))
						sim=inputMatchers.get(k).matcherResult.getClassesMatrix().getSimilarity(i, j);
					else
						sim=inputMatchers.get(k).matcherResult.getPropertiesMatrix().getSimilarity(i, j);
					
					if(sim>threshold)
						countP++;
					else
						countN++;
				}
				
				if ((countP!=0) & (countN!=0))
					lst.add(am.get(i, j));
				
			}
		
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		

		Mapping m=ufl.get(i, j);
		if (!lst.contains(m))
			return 1.0d;
		
		double max=Double.MIN_VALUE;
		double p_max=Double.MIN_VALUE;
		for (int k=0;k<amInput.size();k++)
		{
			double tmp=0;
			if(type.equals(alignType.aligningClasses))
				tmp=Math.abs(amInput.get(k).matcherResult.getClassesMatrix().getSimilarity(i, j)-threshold);
			else
				tmp=Math.abs(amInput.get(k).matcherResult.getPropertiesMatrix().getSimilarity(i, j)-threshold);
			if(tmp>max)
				max=tmp;
			if((tmp<max)&&(tmp>p_max))
				p_max=tmp;
		}
		
		return max-p_max;
		
//		for (int k=0;k<amInput.size()-1;k++)
//			for(int l=k+1;l<amInput.size();l++)
//			{
//				double tmp=0;
//				if(type.equals(alignType.aligningClasses))
//					tmp=Math.abs(Math.abs(amInput.get(k).getClassesMatrix().getSimilarity(i, j)-threshold)
//							-Math.abs(amInput.get(l).getClassesMatrix().getSimilarity(i, j)-threshold));
//				else
//					tmp=Math.abs(Math.abs(amInput.get(k).getPropertiesMatrix().getSimilarity(i, j)-threshold)
//							-Math.abs(amInput.get(l).getPropertiesMatrix().getSimilarity(i, j)-threshold));
//				if(tmp<min)
//					min=tmp;
//			}
//		
//		return 1-min;
		

		
	}
}
