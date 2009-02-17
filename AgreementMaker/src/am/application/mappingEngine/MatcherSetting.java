package am.application.mappingEngine;

/** This enum stores the names of settings for different algorithms ( currently used by AppPreferences ) @author Cosmin Stroe @date Nov 25, 2008 
 * If you need to save settings for your algorithm panel, use this enum along with the AppPreferences savePanelSetting() and getPanelSetting() functions. 
 * */
public enum MatcherSetting {
	DSI_MCP ( "DSI_MCP", 0.75f),  // Descendant's Similarity Inheritance ( MCP value, 0.75 is the default )
	SSC_MCP ( "SSC_MCP", 0.75f),  // Sibling Similarity Inheritance ( MCP Value )
	CON_ANC ( "CON_ANC", 0.75f),
	CON_DES ( "CON_DES", 0.75f),
	CON_TXT ( "CON_TXT", 0.75f),
	BSIM_USEDICT ( "BSIM_USEDICT", false ); // Base Similarity  ( Whether to use the dictionary or not, default is not to use the dictionary)
	

	public String prefKey;
	public boolean defBool;  // boolean setting
	public float defFloat; 	// float setting
	
	MatcherSetting(String key, float def ) { prefKey = key; defFloat = def; }  // float constructor
	MatcherSetting(String key, boolean def ) { prefKey = key; defBool = def; }  // boolean constructor

}
