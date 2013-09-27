package am.app.mappingEngine.qualityEvaluation.metrics;

import java.util.HashMap;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.qualityEvaluation.InterMatcherQualityEvaluation;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.utility.parameters.AMParameter;
import am.utility.parameters.AMParameterSet;
public class InterCouplingQualityMetric implements InterMatcherQualityEvaluation 
{
	
	private HashMap<Node, Integer> hm=new HashMap<>();
	
	public HashMap<Node, Integer> getHm() {
		return hm;
	}

	@Override
	public void setParameter(AMParameter param) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParameters(AMParameterSet params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher,
			AMParameterSet params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher,
			AbstractMatcher[] matcherList) throws Exception {
		QualityEvaluationData q=new QualityEvaluationData();
		Node nd;
		int tmp;
		Mapping maxMapping;
		SimilarityMatrix sm=matcher.getClassesMatrix();
		for(int i=0;i<sm.getRows();i++)
		{
			
			for(int j=0;j<matcherList.length-1;j++)
			{
				
				if(matcher==matcherList[j]) continue;
				
				
				 
				maxMapping = matcherList[j].getClassesMatrix().getRowMaxValues(i, 1)[0];
				nd= maxMapping.getEntity2();
				
				if(hm.containsKey(nd))
				{
					tmp=hm.get(nd).intValue();
					hm.put(nd, new Integer(tmp++));
				}
				else
				{
					hm.put(nd, new Integer(1));
				}
				
			}
		}
		q.setGlobalClassMeasure((double)hm.size()/(sm.getRows()*matcherList.length-1));
		return q;

	}

	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
