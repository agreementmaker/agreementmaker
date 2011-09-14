package am.utility.numeric;

import java.text.DecimalFormat;
import java.util.Arrays;

public class AvgMinMaxNumber {

	public String label;
	public double average;
	public double min;
	public double max;
	public double median;

	
	public AvgMinMaxNumber( String label, int[] numbersList ) {
		this.label = label;
		computeStatistics(numbersList);
	}
	
	public AvgMinMaxNumber(int[] numbersList) {
		computeStatistics(numbersList);
	}	
	
	private void computeStatistics( int[] numbersList ) {
		// sort in ascending numerical order
		Arrays.sort(numbersList);

		long sum = 0;
		min = 0;
		max = 0;
		for( int i : numbersList ) {
			sum += i;
			if( i < min ) min = i;
			if( i > max ) max = i;
		}

		average = (double) sum / (double) numbersList.length;

		if( numbersList.length % 2 == 0 ) {
			median = (double) ( numbersList[numbersList.length/2] + numbersList[(numbersList.length/2)-1] ) / 2d; 
		} else {
			median = (double) ( numbersList[ (numbersList.length-1)/2 ]);
		}
	}
	
	@Override
	public String toString() {
		DecimalFormat fmt = new DecimalFormat("###.###");
		if( label == null )
			return new String("Average: " + fmt.format(average) + 
							",\tMin: " + fmt.format(min) + 
							",\tMax: " + fmt.format(max) + 
							",\tMedian: " + fmt.format(median));
		else
			return new String(label + " -- " + 
					  "Average: " + fmt.format(average) + 
					",\tMin: " + fmt.format(min) + 
					",\tMax: " + fmt.format(max) + 
					",\tMedian: " + fmt.format(median));
	}
}
