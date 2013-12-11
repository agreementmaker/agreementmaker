package am.evaluation.clustering.localByThreshold;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.evaluation.clustering.Cluster;
import am.utility.PointComparator;

public class LocalByTrendMethod extends LocalByThresholdMethod {
	
	public LocalByTrendMethod(List<AbstractMatcher> availableMatchers) {
		super(availableMatchers);
		
	}
	
	
	@Override
	public Cluster<Mapping> getCluster(int row, int col, alignType t) {

		List<AbstractMatcher> matcherList = params.getMatchers();
		
		SimilarityMatrix[] smArray = new SimilarityMatrix[matcherList.size()];
		for (int i=0; i<matcherList.size(); i++) {
			smArray[i] = getSM(matcherList.get(i), t);
		}
		
		double[] candidateAsRef = new double[matcherList.size()-1];
		for (int i=1; i<smArray.length; i++) {
			candidateAsRef[i-1] = smArray[i].getSimilarity(row, col) - smArray[i-1].getSimilarity(row, col);
		}
		
		HashMap<Point, Double> pointWithSim = new HashMap<Point, Double>();
		
		int rowNum = smArray[0].getRows(), colNum = smArray[0].getColumns();
		for( int r = 0; r < rowNum; r++ ) {
			for( int c = 0; c < colNum; c++ ) {
				if (r == row && c == col) continue;
				
				double[] tmpTrend = new double[matcherList.size()-1];
				for (int i=1; i<smArray.length; i++) {
					tmpTrend[i-1] = smArray[i].getSimilarity(r, c) - smArray[i-1].getSimilarity(r, c);
				}
				
				boolean sameTrend = true;
				for (int i=0; i<tmpTrend.length; i++) {
					if (tmpTrend[i] * candidateAsRef[i] < 0 || Math.abs(tmpTrend[i]-candidateAsRef[i]) > 0.3) {
						sameTrend = false;
						break;
					}
				}
				
				if (sameTrend == true) {
					double sim = computeSimilarity(tmpTrend);
					pointWithSim.put(new Point(r,c), sim);
				}
				
			}
		}

		Cluster<Mapping> c = null;
		c = new Cluster<Mapping>(pointWithSim, matcherList.get(0)
				.getSourceOntology(), matcherList.get(0)
				.getTargetOntology(), t);

		return c;
	}
	
	private SimilarityMatrix getSM(AbstractMatcher m, alignType t) {
		SimilarityMatrix mtx = null;
		
		if( t == alignType.aligningClasses ) {
			mtx = m.getClassesMatrix();
		} else if ( t == alignType.aligningProperties ) {
			mtx = m.getPropertiesMatrix();
		}
		
		return mtx;
	}

	
	private double computeSimilarity(double[] distance) {
		double avg = 0.0;
		for (double val : distance) {
			avg += Math.abs(val);
		}
		return (1.0 - avg / distance.length);
	}
	
	
}
