package am.visualization.matrixplot;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class MatrixPlotPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1229269994872153254L;

	
	public MatrixPlotPopupMenu(ActionListener listener) {
		
		// menu layout (Oct 20th, 2010 - Cosmin)
		// 1. Set as Reference
		
		JMenuItem miReference = new JMenuItem("Set as Reference");
	    		  miReference.setActionCommand("SET_REFERENCE");
	    
	    add(miReference);
	    
	    miReference.addActionListener(listener);
	}
}
