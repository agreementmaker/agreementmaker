package am.app.ontology.profiling;

import javax.swing.JPanel;

/**
 * This class is meant to be extended by an ontology profiling panel.
 * 
 * An ontology profiling panel is meant to allow the user to input parameters 
 * to the profiling algorithm.
 *  
 * @author Cosmin Stroe @date January 25, 2011
 */
public abstract class OntologyProfilerPanel extends JPanel {

	private static final long serialVersionUID = -6063511235640978065L;

	/**
	 * Returns the parameters associated with this panel.
	 * @return
	 */
	public abstract OntologyProfilerParameters getParameters();
}
