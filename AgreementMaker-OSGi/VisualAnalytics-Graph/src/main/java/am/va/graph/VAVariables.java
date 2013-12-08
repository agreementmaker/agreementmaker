package am.va.graph;

public interface VAVariables {
	int slotNum = 5;
	/*
	double threshold[] = { 0.00, 0.10, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70,
			0.80, 0.90, 1.00 };
	String thresholdName[] = { "0-10%", "10-20%", "20-30%", "30-40%", "40-50%",
			"50-60%", "60-70%", "70-80%", "80-90%", "90-100%" };*/
	double threshold[] = { 0.00, 0.20, 0.40, 0.60, 0.80, 1.00 };
	String thresholdName[] = { "0-20%", "20-40%", "40-60%", "60-80%", "80-100%"};
	
	// If the area node number is small, show all of them
	int showAllNodesThresh = 20;
	/**
	 * Here every slice of pie chart corresponds to a set of nodes in a certain similarity range
	 * (defined in array threshold)
	 * Also, for every slice of pie chart, we divide the slice into arcNumPerSlice area
	 * (ascending similarity order from center), when user click the area, we present the corresponding nodes
	 */
	int arcNumPerSlice = 2;	// 5 areas for each pie slice, in order to get the node set of each click
	double arcInterval = 1.0/slotNum/arcNumPerSlice;	// the similarity interval for every arc
	// Total arc number for the whole pie chart
	int totalArcNumOfPieChart = slotNum * arcNumPerSlice;	// add 0 as the first

	enum ontologyType {
		Source, Target;
	}
}
