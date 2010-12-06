package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import java.util.Iterator;
import java.util.Vector;

import am.utility.DirectedGraph;

public class PairwiseConnectivityGraph extends DirectedGraph<PCGEdge, PCGVertex> {

	public Vector<Double> getSimValueVector(boolean old){
		Iterator<PCGVertex> iVert = this.vertices();
		Vector<Double> simVector = new Vector<Double>();
		while(iVert.hasNext()){
			if(old){
				simVector.add(iVert.next().getObject().getOldSimilarityValue());
			}
			else{
				simVector.add(iVert.next().getObject().getNewSimilarityValue());
			}
			
		}
		return simVector;
	}
}
