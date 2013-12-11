package am.va.graph;

public interface VAVariables {
	int DEBUG = 1;
	int slotNum = 5;
	double threshold[] = { 0.00, 0.20, 0.40, 0.60, 0.80, 1.00 };
	String thresholdName[] = { "0-20%", "20-40%", "40-60%", "60-80%", "80-100%" };

	/**
	 * Here every slice of pie chart corresponds to a set of nodes in a certain
	 * similarity range (defined in array threshold) Also, for every slice of
	 * pie chart, we divide the slice into arcNumPerSlice area (ascending
	 * similarity order from center), when user click the area, we present the
	 * corresponding nodes
	 */
	int arcNumPerSlice = 2; // 5 areas for each pie slice, in order to get the
							// node set of each click
	double arcInterval = 1.0 / slotNum / arcNumPerSlice; // the similarity
															// interval for
															// every arc
	// Total arc number for the whole pie chart
	int totalArcNumOfPieChart = slotNum * arcNumPerSlice; // add 0 as the first

	enum nodeType {
		Class, Property;
	}

	enum ontologyType {
		Source, Target;
	}

}
