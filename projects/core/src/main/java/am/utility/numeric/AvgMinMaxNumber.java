package am.utility.numeric;

import java.text.DecimalFormat;
import java.util.Arrays;

public class AvgMinMaxNumber {

	public String label;
	public double average;
	public double min;
	public int minCount;
	public double max;
	public int maxCount;
	public double median;
	public int totalCount;

	
	public AvgMinMaxNumber( String label, int[] numbersList ) {
		this.label = label;
		computeStatistics(numbersList);
	}
	
	public AvgMinMaxNumber(int[] numbersList) {
		computeStatistics(numbersList);
	}	
	
	public AvgMinMaxNumber( String label, double[] numbersList ) {
		this.label = label;
		computeStatistics(numbersList);
	}
	
	public AvgMinMaxNumber(double[] numbersList) {
		computeStatistics(numbersList);
	}
	
	// list of doubles.
	private void computeStatistics( double[] numbersList ) {
		// sort in ascending numerical order
		Arrays.sort(numbersList);

		double sum = 0;
		min = numbersList[0];
		minCount = 1;
		max = 0;
		maxCount = 0;
		for( double n : numbersList ) {
			sum += n;
			if( n < min ) {
				min = n;
				minCount = 1;
			} else if ( n == min ) {
				minCount++;
			}
			
			if( n > max ) {
				max = n;
				maxCount = 1;
			} else if ( n == max ) {
				maxCount++;
			}
		}

		average = (double) sum / (double) numbersList.length;

		if( numbersList.length % 2 == 0 ) {
			median = (double) ( numbersList[numbersList.length/2] + numbersList[(numbersList.length/2)-1] ) / 2d; 
		} else {
			median = (double) ( numbersList[ (numbersList.length-1)/2 ]);
		}
		
		totalCount = numbersList.length;
	}
	
	// handle a list of integers
	private void computeStatistics( int[] numbersList ) {
		if( numbersList.length == 0) return;
		
		// sort in ascending numerical order
		Arrays.sort(numbersList);

		long sum = 0;
		min = numbersList[0];
		minCount = 1;
		max = 0;
		maxCount = 0;
		for( int n : numbersList ) {
			sum += n;
			if( n < min ) {
				min = n;
				minCount = 1;
			} else if ( n == min ) {
				minCount++;
			}
			
			if( n > max ) {
				max = n;
				maxCount = 1;
			} else if ( n == max ) {
				maxCount++;
			}
		}

		average = (double) sum / (double) numbersList.length;

		if( numbersList.length % 2 == 0 ) {
			median = (double) ( numbersList[numbersList.length/2] + numbersList[(numbersList.length/2)-1] ) / 2d; 
		} else {
			median = (double) ( numbersList[ (numbersList.length-1)/2 ]);
		}
		
		totalCount = numbersList.length;
	}
	
	@Override
	public String toString() {
		DecimalFormat fmt = new DecimalFormat("###.###");
		
		String stats = new String( totalCount + " items.  Average: " + fmt.format(average) + 
				", Min: " + fmt.format(min) + " (" + (int)minCount + " items)" +
				", Max: " + fmt.format(max) + " (" + (int)maxCount + " items)" +
				", Median: " + fmt.format(median));
		
		if( label == null )
			return stats;
		else
			return label + " -- " + stats; 
	}
}
