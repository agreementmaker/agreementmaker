package am.va.graph;

import java.util.HashMap;

public interface VAVariables {
	int DEBUG = 0;
	int slotNum = 5;
	double threshold[] = { 0.00, 0.20, 0.40, 0.60, 0.80, 1.00 };
	String thresholdName[] = { "0-20%", "20-40%", "40-60%", "60-80%", "80-100%" };

	String selectionPer[] = { "100%", "90%-99%", "80%-89%", "60-79%", "Not matched" };

	String sourceRoot = "Source Ontology";
	String targetRoot = "Target Ontology";
	
	String nodeWithChildren = "+";

	String panelColor[] = { "#e6ffcc", "#ffffcc" };
	String uflPanelColor[] = { "#CCCCFF", "#CCFFFF" };

	HashMap<String, String> ColorRange = new HashMap<String, String>() {
		{
			put("0-20%", "#D6F5FF");
			put("20-40%", "#ADEBFF");
			put("40-60%", "#85E0FF");
			put("60-80%", "#5CD6FF");
			put("80-100%", "#33CCFF");
		}
	};

	enum nodeType {
		Class, Property;
	}

	enum ontologyType {
		Source, Target;
	}

	enum ChartType {
		LeftMain, RightMain, LeftSub, RightSub;
	}

	enum currentSetStatus {
		mainSetEmpty, subSetEmpty, noEmpty;
	}

	/**
	 * Testing part
	 */
	String keywordsLabel_Property = "testProperty";
	boolean testPropertyBelongsTo = false;
	boolean testPropertyLists = false;
	boolean testPropertyClustering = false;
	
	String keywordsLabel_InitData = "testInitData";
	boolean testInitData = false;
	
	String keywordsLabel_findAmbiguous = "testAmbiguous";
	boolean testFindAmb = true;	//test pass 11/7/2014
	boolean testClustering = true;
}
