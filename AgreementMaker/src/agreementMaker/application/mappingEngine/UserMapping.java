package agreementMaker.application.mappingEngine;


import java.io.Serializable;
import java.util.Vector;

/**
 * UserMapping class - 
 * 
 * This class will contain information about the user mapping such as 
 * what global nodes are mapped to local nodes, the mapping categoy
 * and mapping type
 * 
 * @author ADVIS Laboratory
 * @version 11/27/2004
 */

public class UserMapping implements Serializable
{

	static final long serialVersionUID = 1;
	Vector globalVertices;		// global vertices to be mapped
	Vector localVertices;		// local vertices to be mapped
	String mappingCategory;		// category of the mapping
	protected String mappingType;			// mapping type

	public UserMapping()
	{
		globalVertices = new Vector();
		localVertices = new Vector();
	}

	/*******************************************************************************************/
	/**     
	 * This function returns the mapping type of the vertex
	 * @return mappingType - the mapping type
	 */
	public String getMappingType()
	{
		return mappingType;
	}

	/**
	 * @return Returns the localVertices.
	 */
	public Vector getLocalVertices() {
		return localVertices;
	}

	/**
	 * @param localVertices The localVertices to set.
	 */
	public void setLocalVertices(Vector localVertices) {
		this.localVertices = localVertices;
	}

	/**
	 * @param mappingType The mappingType to set.
	 */
	public void setMappingType(String mappingType) {
		this.mappingType = mappingType;
	}

	/**
	 * @return Returns the globalVertices.
	 */
	public Vector getGlobalVertices() {
		return globalVertices;
	}

	/**
	 * @param globalVertices The globalVertices to set.
	 */
	public void setGlobalVertices(Vector globalVertices) {
		this.globalVertices = globalVertices;
	}

	/**
	 * @return Returns the mappingCategory.
	 */
	public String getMappingCategory() {
		return mappingCategory;
	}

	/**
	 * @param mappingCategory The mappingCategory to set.
	 */
	public void setMappingCategory(String mappingCategory) {
		this.mappingCategory = mappingCategory;
	}
}
