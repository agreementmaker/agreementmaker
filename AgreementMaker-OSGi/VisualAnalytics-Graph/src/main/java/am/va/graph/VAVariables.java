package am.va.graph;

import java.util.HashMap;

public interface VAVariables {
	int DEBUG = 0;
	int slotNum = 5;
	double threshold[] = { 0.00, 0.20, 0.40, 0.60, 0.80, 1.00 };
	String thresholdName[] = { "0-20%", "20-40%", "40-60%", "60-80%", "80-100%" };

	String selectionPer[] = { "100%", "90%-99%", "80%-89%", "60-79%",
			"Not matched" };
	String selectionStyle[] = { "#FFD700", "#90EE90", "#B0E0E6", "#FFC0CB",
			"#FF6347" };

	HashMap<String, String> ColorRange = new HashMap<String, String>() {
		{
			put("0-20%", "#ffcc99");
			put("20-40%", "#ffccff");
			put("40-60%", "#66b2ff");
			put("60-80%", "#ff66ff");
			put("80-100%", "#ffff33");
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

}
