package agreementMaker.mappingEngine;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import agreementMaker.userInterface.vertex.Vertex;


/**
 * @author Nalin
 *
 */
public class DefnMapping implements Serializable
{
	
	static final long serialVersionUID = 1;
	Vector globalVertices;		// global vertices to be mapped
	protected Vector localVertices;		// local vertices to be mapped
	HashMap map ;
	String mappingCategory;		// category of the mapping
	double mappingValue = 0d;			// mapping type
	protected Vector similarities; 
	
	
	/**
	 * 
	 */
	public DefnMapping()
	{
		globalVertices = new Vector();
		localVertices = new Vector();
		similarities = new Vector();
		map = new HashMap() ;
	}
	
	/**
	 * @param f
	 */
	public void addToSimilarities(Float f){
		similarities.add(f);
	}
	
	/**
	 * @return Returns the globalVertices.
	 */
	public Vector getGlobalVertices() {
		return globalVertices;
	}
	
	/**
	 * @return
	 */
	public Vector getLocalVertices() {
		return localVertices;
	}
	
	/**
	 * @return Returns the mappingCategory.
	 */
	public String getMappingCategory() {
		return mappingCategory;
	}
	
	/**
	 * @return
	 */
	public String getMappingValue()
	{
		return mappingValue + "";
	}
	/**
	 * @param n
	 * @return
	 */
	public String getMappingValue1(Vertex n )
	{
		Float f = (Float)  map.get(n) ; 
		//return mappingValue + "";
		return f  + "";
		
	}
	/**
	 * @return
	 */
	public Vector getSimilarities() {
		return similarities;
	}
	public void putIntoMap(Vertex node, Float f){
		map.put(node,f);
	}
	
	/**
	 * 
	 */
	public void reSort(){
		int i, j;
		int n = localVertices.size();
		Float tmp;
		Vertex tmp2;
		
		for (i=0; i<n-1; i++)
			for (j=0; j<n-1-i; j++)
				if (    ((Float)(similarities.get(j+1))).floatValue()  >   ((Float)(similarities.get(j))).floatValue()    ) {  
					
					tmp = (Float)similarities.get(j);
					similarities.setElementAt( similarities.get(j+1) ,j);
					similarities.setElementAt( tmp, j+1);
					
					tmp2 = (Vertex)localVertices.get(j);
					localVertices.setElementAt( localVertices.get(j+1) ,j);
					localVertices.setElementAt( tmp2, j+1);
				}
		
	}
	
	/**
	 * @param globalVertices The globalVertices to set.
	 */
	public void setGlobalVertices(Vector globalVertices) {
		this.globalVertices = globalVertices;
	}
	
	/**
	 * @param localVertices
	 */
	public void setLocalVertices(Vector localVertices) {
		this.localVertices = localVertices;
	}
	
	/**
	 * @param mappingCategory The mappingCategory to set.
	 */
	public void setMappingCategory(String mappingCategory) {
		this.mappingCategory = mappingCategory;
	}
	
	/**
	 * @param mappingValue The mappingValue to set.
	 */
	public void setMappingValue(double mappingValue) {
		this.mappingValue = mappingValue;
	}
	
	/**
	 * @param similarities
	 */
	public void setSimilarities(Vector similarities) {
		this.similarities = similarities;
	}
	
}