package am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi;


import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public class ContentionPoint  extends AbstractQualityMetric{
		
	private final double threshold=0.4;
	List<Mapping> lst_cpClass=new ArrayList<>();
	List<Mapping> lst_cpProp=new ArrayList<>();
	List<AbstractMatcher> amInput=new ArrayList<>();
	
	public ContentionPoint(SimilarityMatrix amClass, SimilarityMatrix amProp, List<AbstractMatcher> inputMatchers)
	{
		super();
		this.amInput=inputMatchers;
		List<Mapping> lst_cpClass=new ArrayList<>();
		int countP=0;
		int countN=0;
		for (int i=0;i<amClass.getRows();i++)
			for(int j=0;j<amClass.getColumns();j++)
			{
				countP=0;
				countN=0;
				for (int k=0; k<inputMatchers.size();k++)
				{
					if(inputMatchers.get(k).getClassesMatrix().getSimilarity(i, j)>threshold)
						countP++;
					else
						countN++;
				}
				
				if ((countP!=0) & (countN!=0))
					lst_cpClass.add(amClass.get(i, j));
			}
		for (int i=0;i<amProp.getRows();i++)
			for(int j=0;j<amProp.getColumns();j++)
			{
				countP=0;
				countN=0;
				for (int k=0; k<inputMatchers.size();k++)
				{
					if(inputMatchers.get(k).getPropertiesMatrix().getSimilarity(i, j)>threshold)
						countP++;
					else
						countN++;
				}
				
				if ((countP!=0) & (countN!=0))
					lst_cpProp.add(amProp.get(i, j));
			}
		
	}
	
	/**
	 * @param type
	 *            This parameter is ignored for this quality metric.
	 */
	@Override
	public double getQuality(alignType type, int i, int j) 
	{		
		Mapping mp;
		List<Mapping> lst=new ArrayList<>();
		if (type.equals(alignType.aligningClasses))
		{
			mp=amInput.get(0).getClassesMatrix().get(i, j);
			lst=lst_cpClass;
		}
		else
		{
			mp=amInput.get(0).getPropertiesMatrix().get(i, j);
			lst=lst_cpProp;
		}
		if (!lst.contains(mp))
			return 0.0d;
		
		double min=Double.MAX_VALUE;
		for (int k=0;k<amInput.size()-1;k++)
			for(int l=k+1;l<amInput.size();l++)
			{
				double tmp=0;
				if(type.equals(alignType.aligningClasses))
					tmp=Math.abs(Math.abs(amInput.get(k).getClassesMatrix().getSimilarity(i, j)-threshold)
							-Math.abs(amInput.get(l).getClassesMatrix().getSimilarity(i, j)-threshold));
				else
					tmp=Math.abs(Math.abs(amInput.get(k).getPropertiesMatrix().getSimilarity(i, j)-threshold)
							-Math.abs(amInput.get(l).getPropertiesMatrix().getSimilarity(i, j)-threshold));
				if(tmp<min)
					min=tmp;
			}
		
		return min;
		

		
	}
}
