package am.matcher.structuralMatchers.similarityFlooding.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import am.utility.DirectedGraph;

public class PairwiseConnectivityGraph extends DirectedGraph<PCGEdge, PCGVertex> {

	/**
	 * Slightly modified insertEdge to insert properly a PCGEdge
	 */
	public void insertEdge(PCGVertex origin, PCGEdge edge, PCGVertex destination) {
		edges.add(edge);
		
		origin.addOutEdge(edge);
		destination.addInEdge(edge);
	}
	
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
	
	public Vector<Double> getSimValueVector(FileWriter fw, int round, boolean old){
		Iterator<PCGVertex> iVert = this.vertices();
		Vector<Double> simVector = new Vector<Double>();
		while(iVert.hasNext()){
			
			PCGVertex vert = iVert.next();
			if(round == 1){
		 		try {
					fw.append(vert + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 	}
			if(old){
				simVector.add(vert.getObject().getOldSimilarityValue());
			}
			else{
				simVector.add(vert.getObject().getNewSimilarityValue());
			}
			
		}
		return simVector;
	}
}
