package am.utility;

import java.awt.Point;
import java.util.Comparator;

public class PointComparator implements Comparator<Point> {

	@Override
	public int compare(Point o1, Point o2) {
		
		if( o1.x < o2.x ) return -1;
		if( o1.x > o2.x ) return 1;
		
		// same row
		
		if( o1.y < o2.y ) return -1;
		if( o1.y > o2.y ) return 1;
		
		// same column
		
		// i.e. same Point
		
		return 0;
	}

}
