package am.extension;

import java.awt.Dimension;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;

public class SubTreeLayout extends TreeLayout<String, String> {

	public SubTreeLayout(Forest<String, String> g) {
		super(g);
	}
	
	public void setSize(Dimension size) { 
		
	}
}
