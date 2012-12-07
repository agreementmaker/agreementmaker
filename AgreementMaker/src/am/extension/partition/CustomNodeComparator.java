package am.extension.partition;

import java.util.Comparator;

public class CustomNodeComparator implements Comparator<CustomNode>{
	
	
	
	public int compare(CustomNode o1, CustomNode o2) {
        return o1.depth - o2.depth;
    }
	
	
}
