package agreementMaker.application.mappingEngine;

/**
 * MappingType class
 * This class contains the information about the mapping types.
 *
 * @author ADVIS Research Laboratory
 * @version 11/27/2004
 */

public class MappingType
{
	private String constraints;         // ???
	private float contextSimilarity;
	private String description;         // description of the mapping
	// instance variables
	private int ID;                     // unique ID
	private String name;                // name of the mapping type (Exact, Approx, Subset, Superset,..)
	private int numFrom;                // mapping from unique key number (vertex key)
	private int numTo;                  // mapping to unique key number (vertex key)
	private float semanticSimilarity;   // ???    
	
	// ???
	   
	/*******************************************************************************************/
	/**
	 * Constructor for objects of class MappingType
	 */
	public MappingType()
	{
		// initialise instance variables
		ID = 0;
		name = "";
		constraints = "none";
		numFrom = 0;
		numTo = 0;
		description = "";
		semanticSimilarity = 0;
		contextSimilarity = 0;
	}

	/*******************************************************************************************/
	/**
	 * Accessor method which returns constrainsts of the mapping type
	 * 
	 * @return  constraints
	 */
	public String getconstraints()
	{
		return constraints;
	}
	/*******************************************************************************************/
	/**
	 * Accessor method which returns contextSimilarity
	 * 
	 * @return  contextSimilarity
	 */
	public float getcontextSimilarity()
	{
		return contextSimilarity;
	}
	/*******************************************************************************************/
	/**
	 * Accessor method which returns the description of the mapping type
	 * 
	 * @return  description
	 */
	public String getdesc()
	{
		return description;
	}
	/**
	 * Accessor method which returns ID
	 * 
	 * @return ID
	 */
	public int getID()
	{
		return ID;
	}
	/*******************************************************************************************/
	/**
	 * Accessor method which returns name of the mapping type
	 * 
	 * @return  name
	 */
	public String getname()
	{
		return name;
	}
	/*******************************************************************************************/
	/**
	 * Accessor method which returns the unique key which the mapping is assigned from
	 * 
	 * @return  numFrom
	 */
	public int getnumFrom()
	{
		return numFrom;
	}
	/*******************************************************************************************/
	/**
	 * Accessor method which returns the unique key which the mapping is assigned to
	 * 
	 * @return  numTo
	 */
	public int getnumTo()
	{
		return numTo;
	}
	/*******************************************************************************************/
	/**
	 * Accessor method which returns semanticSimilarity
	 * 
	 * @return  semanticSimilarity
	 */
	public float getsemanticSimilarity()
	{
		return semanticSimilarity;
	}
	/*******************************************************************************************/
	/**
	 * Modifier method which sets constraints
	 * 
	 * @param  c of type String
	 */
	public void setconstraints(String c)
	{
		constraints = c;
	}
	/*******************************************************************************************/
	/**
	 * Modifier method which sets contextSimilarity
	 * 
	 * @param  cs of type float
	 */
	public void setcontextSimilarity(float cs)
	{
		contextSimilarity = cs;
	}
	/*******************************************************************************************/
	/*******************************************************************************************/
	/**
	 * Modifier method which sets description of the mapping type
	 * 
	 * @param  desc of type String
	 */
	public void setdesc(String desc)
	{
		description = desc;
	}
	/********************************************************************************************/
	/*                              MODIFIER METHODS                                            */
	/********************************************************************************************/
	/**
	 * Modifier method which sets ID
	 * 
	 * @param  key 
	 */
	public void setID(int key)
	{
		ID = key;
	}
	/*******************************************************************************************/
	/**
	 * Modifier method which sets name
	 * 
	 * @param  n
	 */
	public void setname(String n)
	{
		name = n;
	}
	/*******************************************************************************************/
	/**
	 * Modifier method which sets the mapping type from key # (from)
	 * 
	 * @param  from of type int
	 */
	public void setnumFrom(int from)
	{
		numFrom = from;
	}
	/*******************************************************************************************/
	/**
	 * Modifier method which sets the mapping type to key # (to)
	 * 
	 * @param  to of type int
	 */
	public void setnumTo(int to)
	{
		numTo = to;
	}
	/*******************************************************************************************/
	/**
	 * Modifier method which sets semanticSimilarity
	 * 
	 * @param  ss of type float
	 */
	public void setsemanticSimilarity(float ss)
	{
		semanticSimilarity = ss;
	}
}
