package agreementMaker.application.mappingEngine;

/**
 * this class is a container for input parameters to any Defn Mapping algorithm, everything is public because it must be easy accessible.
 * Actually this should contains only parameters of all algorithms like threshold and numrel. Then every time a new algorithm is developed it should 
 * have is own DefnMappingOptionMYALG class which extends this one and contains the new parameters for the algo.
 * For example the attribute MCP shouldn't be in this class but in the DSIdefnMappingOptions class that should be an extension of this one.
 * Now it's easier to use only this one because we have few attributes. 
 *
 */
public class DefnMappingOptions {
	
	public final static String BASE = "base_similarity";
	public final static String DSI = "dsi";
	public final static String SSC = "ssc";
	
	/**Name of algo selected: base, dsi , scc e future works*/
	public String algorithm;
	/**Max number of relations for a source node*/
	public int numRel;
	/**Consider only matchings with higher similarity value then this*/
	public int threshold;
	/** ONLY FOR DSI AND SSC, DSI formula is MCP * basesim + (1-MCP) dsi*/
	public float mcp;
	
	public boolean consultDict;
	
	public String toString() {
		return algorithm+", threshold: "+threshold+", numRel: "+numRel+", mcp: "+mcp+", consultDict: "+consultDict; 
	}
}
