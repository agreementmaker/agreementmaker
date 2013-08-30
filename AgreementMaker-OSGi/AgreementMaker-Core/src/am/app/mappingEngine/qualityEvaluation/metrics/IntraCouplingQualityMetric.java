package am.app.mappingEngine.qualityEvaluation.metrics;

import java.util.HashMap;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;

public class IntraCouplingQualityMetric extends AbstractQualityMetric 
{
	
	private HashMap<Node, Integer> hm=new HashMap<>();
	
	public HashMap<Node, Integer> getHm() {
		return hm;
	}

	/**
	 * FIXME Implements metric for property also
	 * 
	 */
	@Override
	public QualityEvaluationData getQuality(AbstractMatcher matcher) throws Exception 
	{
		QualityEvaluationData q = new QualityEvaluationData();
		q.setLocal(false); // this is a Global Quality Metric
		
		
		// Compute the Global Quality for the matrix
		Node nd;
		int tmp;
		SimilarityMatrix sm = matcher.getClassesMatrix();
		for(int i=0;i<sm.getRows();i++)
		{
			Mapping[] maxMapping = sm.getRowMaxValues(i, 1);
			nd= maxMapping[0].getEntity2();
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
		q.setGlobalClassMeasure((double)hm.size()/sm.getColumns());
		return q;
	}
	
	

}
