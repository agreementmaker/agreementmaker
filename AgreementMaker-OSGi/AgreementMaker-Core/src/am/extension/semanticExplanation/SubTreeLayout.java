package am.extension.semanticExplanation;

import java.awt.Dimension;


import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;

public class SubTreeLayout extends TreeLayout<ExplanationNode, String> {

	public SubTreeLayout(Forest<ExplanationNode, String> g) {
		super(g);
	}
	
	public void setSize(Dimension size) { 
		
	}
}
