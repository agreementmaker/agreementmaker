package am.app.mappingEngine.qualityEvaluation.metrics;

import java.util.HashMap;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.qualityEvaluation.InterMatcherQualityEvaluation;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.utility.parameters.AMParameter;
import am.utility.parameters.AMParameterSet;
public class InterCouplingQualityMetric implements InterMatcherQualityEvaluation 
{

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
		int count=0;
		SimilarityMatrix sm=matcher.getClassesMatrix();
		Mapping maxTmpMapping;
		for(int i=0;i<sm.getRows();i++)
		{
			Mapping maxMapping = sm.getRowMaxValues(i, 1)[0];
			for(int j=0;j<matcherList.length-1;j++)
			{
				if(matcher==matcherList[j]) continue;
				maxTmpMapping= matcherList[j].getClassesMatrix().getRowMaxValues(i, 1)[0];
				if(maxMapping.equals(maxTmpMapping))
					count++;
			}
		}
		q.setGlobalClassMeasure((double)count/(sm.getRows()*matcherList.length-1));
		return q;

	}

	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
