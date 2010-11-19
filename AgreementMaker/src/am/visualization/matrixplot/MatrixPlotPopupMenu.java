package am.visualization.matrixplot;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import am.evaluation.clustering.ClusterFactory;
import am.evaluation.clustering.ClusterFactory.ClusteringType;

public class MatrixPlotPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1229269994872153254L;

	// used instead of string for action commands
	public enum ActionCommands {
		SET_REFERENCE, VIEW_ALIGNMENT, VIEW_CLUSTER, CLEAR_CLUSTER, REMOVE_PLOT, SET_FEEDBACK;
	}
	
	
	public MatrixPlotPopupMenu(MatrixPlotPanel listener) {
		
		// menu layout (Oct 20th, 2010 - Cosmin)
		// 1. Set as Reference
		
		JMenuItem miReference = new JMenuItem("Set as Reference");
	    		  miReference.setActionCommand(ActionCommands.SET_REFERENCE.name());
	    
	    JMenuItem miFeedback = new JMenuItem("Set as Feedback");
	    		  miFeedback.setActionCommand(ActionCommands.SET_FEEDBACK.name());
	    		  
	    JCheckBoxMenuItem miViewAlignment = new JCheckBoxMenuItem("View only Alignment");
	    				  miViewAlignment.setActionCommand(ActionCommands.VIEW_ALIGNMENT.name());
	    				  miViewAlignment.setSelected(listener.getViewAlignmentOnly());
	    JMenu miViewCluster = new JMenu("View cluster...");
	    JMenuItem miClearCluster = new JMenuItem("Clear cluster");
	    		  miClearCluster.setActionCommand(ActionCommands.CLEAR_CLUSTER.name());
	    
	    for(ClusteringType t : ClusterFactory.ClusteringType.values()) {
	    	JMenuItem cType = new JMenuItem(t.getName());
	    	cType.setActionCommand(ActionCommands.VIEW_CLUSTER.name() + ":" + t.name());
	    	cType.addActionListener(listener);
	    	miViewCluster.add(cType);
	    }
	    
	    JMenuItem miRemove = new JMenuItem("Remove plot");
	    		  miRemove.setActionCommand(ActionCommands.REMOVE_PLOT.name());
	    		  miRemove.addActionListener(listener);
	    
	    add(miReference);
	    add(miFeedback);
	    addSeparator();
	    add(miViewAlignment);
	    add(miViewCluster);
	    add(miClearCluster);
	    addSeparator();
	    add(miRemove);
	    
	        miReference.addActionListener(listener);
	         miFeedback.addActionListener(listener);
	    miViewAlignment.addActionListener(listener);
	     miClearCluster.addActionListener(listener);
	}
}
