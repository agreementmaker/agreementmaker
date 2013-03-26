package am.extension.semanticExplanation.mouseWorks;

/*
 * MyMouseMenus.java
 *
 * Created on March 21, 2007, 3:34 PM; Updated May 29, 2007
 *
 * Copyright March 21, 2007 Grotto Networking
 *
 */


import javax.swing.JPopupMenu;

import am.extension.semanticExplanation.ExplanationNode;

/**
 * A collection of classes used to assemble popup mouse menus for the custom
 * edges and vertices developed in this example.
 * @author Dr. Greg M. Bernstein
 */
public class MyMouseMenus {
    
    public static class VertexMenu extends JPopupMenu {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public VertexMenu() {
            super("Vertex Menu");
            this.add(new MostSignificantPathMenuItem<ExplanationNode>());
            this.addSeparator();
            this.add(new LeastSignificantPathMenuItem<ExplanationNode>());
        }
    }
    
    
    
}