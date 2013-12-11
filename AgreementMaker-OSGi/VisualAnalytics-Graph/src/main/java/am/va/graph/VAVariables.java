package am.va.graph;

public interface VAVariables {
	int DEBUG = 0;
	int slotNum = 5;
	double threshold[] = { 0.00, 0.20, 0.40, 0.60, 0.80, 1.00 };
	String thresholdName[] = { "0-20%", "20-40%", "40-60%", "60-80%", "80-100%" };

	String selectionPer[] = { "100%", "80%", "50%", "30%", "Not matched" };

	enum nodeType {
		Class, Property;
	}

	enum ontologyType {
		Source, Target;
	}

}
