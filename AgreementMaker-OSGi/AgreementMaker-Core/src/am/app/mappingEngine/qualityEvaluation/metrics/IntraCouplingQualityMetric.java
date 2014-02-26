package am.app.mappingEngine.qualityEvaluation.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
		
		List<Node> ndList=new ArrayList<Node>();
		// Compute the Global Quality for the matrix
		Node nd;
		int tmp;
		// Take the similarity matrix of the matcher
		SimilarityMatrix sm = matcher.getClassesMatrix();
		for(int i=0;i<sm.getRows();i++)
		{
			// For Each row take the column with the higher value
			Mapping[] maxMapping = sm.getRowMaxValues(i, 1);
			nd= maxMapping[0].getEntity2();
			// count the different elements found
			if(hm.containsKey(nd))
			{
				tmp=hm.get(nd).intValue();
				hm.put(nd, new Integer(++tmp));
			}
			else
			{
				ndList.add(nd);
				hm.put(nd, new Integer(1));
			}
		}
		//divide the different element found per the total amount of element
		//a good matcher should be have few similar elements
		//System.out.println("Different "+hm.size());
		//System.out.println("Total "+sm.getRows());
		q.setGlobalClassMeasure((double)hm.size()/sm.getRows());
		/*
		double[] localMeasure=new double[sm.getRows()];
		for (int i=0;i<ndList.size();i++)
		{
			localMeasure[i]=hm.get[i].intValue()/sm.getRows();
		}
		q.setLocalPropMeasures(localMeasure);
		*/
		return q;
	}
	
	

}
