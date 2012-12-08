package am.extension.partition;

import java.util.ArrayList;


public class CouplingTracker {
	
	public ArrayList<CustomNode> block1 = null;
	public ArrayList<CustomNode> block2 = null;
	public double couplingBetweenBlocks = 0;
	
	public CouplingTracker(ArrayList<CustomNode> block1, ArrayList<CustomNode> block2)
	{
		this.block1 = block1;
		this.block2 = block2;
		
		this.couplingBetweenBlocks = OntoProcessing.calculateCoupling(block1,block2);
	}
}
