package am.application.mappingEngine.oneToOneSelection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



public class Wrapper {
	
	public final static int DEFAULT_MULTIPLIER = 1000;
	
	public static int getMultipliedInt(double d, int multiplier){
		double d2 = d * multiplier;
		int i2 = (int)d2;
		double residue = d2 - i2;
		if(residue < 0.5){
			return i2;
		}
		return i2+1;
	}
	
	public static int[][] getIntMatrix(double[][] matrix, int multiplier){
		int[][] result = null;
		if(matrix.length > 0){
			result = new int[matrix.length] [matrix[0].length];
			for(int i = 0; i < matrix.length; i++){
				for(int j = 0; j < matrix[0].length; j++){
					result[i][j] = getMultipliedInt(matrix[i][j], multiplier);
				}
			}
		}
		return result;
	}
	/*
	public static <E> BipartiteGraph<E> getGraph(Collection<MappingMWBM<E>> mappings){
			Graph<E> G = new GraphMWBM<E>();
			NodeMWBM<E> sourceNode;
			NodeMWBM<E> targetNode;
			List<NodeMWBM<E>> sources = new ArrayList<NodeMWBM<E>>(sourceElements.size());
			Iterator<E> it = sourceElements.iterator();
			while(it.hasNext()){
				sourceNode = G.addNode(it.next());
				sources.add(sourceNode);
			}
			List<NodeMWBM<E>> targets = new ArrayList<NodeMWBM<E>>(targetElements.size());
			it = targetElements.iterator();
			while(it.hasNext()){
				targetNode = G.addNode(it.next());
				targets.add(targetNode);
			}
			for(int i = 0; i < rows; i++){
				for(int j = 0; j < cols; j++){
					weight = intMatrix[i][j];
					if(weight >= intThreshold){
						G.addEdge(sources.get(i),targets.get(j), weight);
					}
				}
			}
		}
	}
	*/
}
