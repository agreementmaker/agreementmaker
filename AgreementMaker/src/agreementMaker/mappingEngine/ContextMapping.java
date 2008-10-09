package agreementMaker.mappingEngine;


import java.io.Serializable;

import agreementMaker.userInterface.vertex.Vertex;

/**
 * ContextMapping class - 
 * 
 * This class will contain information about the context mapping such as 
 * what global nodes are mapped to local nodes, the mapping categoy
 * and mapping type
 * 
 * @author ADVIS Laboratory
 * @version 12/22/2004
 */

public class ContextMapping implements Serializable
{

	static final long serialVersionUID = 1;
	String mappingType;			// mapping type
	Vertex globalVertex;		// global vertices to be mapped
	Vertex localVertex;			// local vertices to be mapped
	boolean fullyMapped;		// is the parent fully mapped

	public ContextMapping()
	{
		localVertex = null;
		globalVertex = null;
		fullyMapped = false;
	}
	/*******************************************************************************************/
	/**     
	 * This function returns the global vertex
	 * @return globalVertex - the global vertex
	 */
	public Vertex getGlobalVertex()
	{
		return globalVertex;
	}
	/*******************************************************************************************/
	/**     
	 * This function sets the global vertex
	 * @param g - the global vertex
	 */
	public void setGlobalVertex(Vertex g)
	{
		globalVertex = g;
	}
	/*******************************************************************************************/
	/**     
	 * This function returns the local vertex
	 * @return localVertex - the local vertex
	 */
	public Vertex getLocalVertex()
	{
		return localVertex;
	}
	/*******************************************************************************************/
	/**     
	 * This function sets the global vertex
	 * @param l - the local vertex
	 */
	public void setLocalVertex(Vertex l)
	{
		localVertex = l;
	}
	/*******************************************************************************************/
	/**     
	 * This function returns the context mapping type
	 * @return mappingType - the mapping type
	 */
	public String getMappingType()
	{
		return mappingType;
	}
	/*******************************************************************************************/
	/**     
	 * This function sets the context mapping type
	 * @param type - the mapping type
	 */
	public void setMappingType(String type)
	{
		mappingType = type;
	}
	
}
