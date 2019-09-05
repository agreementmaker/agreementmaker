package am.ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import am.app.ontology.Ontology;

/**
 * 
 * All the ontology visualization panels need to implement this interface.
 * 
 */

public class VisualizationPanel extends JPanel {
	
	/**
	 * The viewport is the RECTANGLE that is currently being viewed by the user.
	 * We will repaint() only the Canvas2Nodes that are currently in the viewport coordinates. 
	 */
	protected JViewport 	viewport = null;  	// the viewport of the JScrollPane
	protected JScrollPane scrollpane = null;	// the JScrollPane that allows scrolling around this element.
	
	private static final long serialVersionUID = -6609778504803522544L;
	
	public VisualizationPanel() {
		super();
	}
	
	public VisualizationPanel( JScrollPane s ) {
		super();
		setScrollPane(s);
	}

	/**
	 * New Methods.
	 * @param ontology 
	 */
	
	public void buildLayoutGraphs(Ontology ontology) {};  // called from OntoTreeBuilder, when an ontology is loaded.
	
	/**
	 * Legacy Methods.
	 * We may need these, or they may be taken out.
	 * TODO: Remove these methods.
	 */
	public void clearAllSelections() {};
	public void setDisableVisualization(boolean disableVis) {};
	public void setSMO(boolean smoStatus) {};
	public void setShowLabel(boolean showLabel) {};
	public void setShowLocalName(boolean showLocalname) {};
	public boolean getShowLabel() { return true; }
	public boolean getShowLocalName() { return true; }

	public void setScrollPane( JScrollPane jsp ) {
		scrollpane 	= jsp;
		viewport	= jsp.getViewport();
	}
	public JScrollPane getScrollPane() { return scrollpane; }
	public JViewport getViewport()     { return viewport;   }


	
	
}
