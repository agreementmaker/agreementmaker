package am.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.app.ontology.Node;
import am.ui.canvas2.utility.Canvas2Vertex;

/**
 * This class will help in the transition of the UI code from AgreementMaker-Core to AgreementMaker-UI.
 * It's meant to replace calls to Core and make things work until we implement a better designed solution.
 * 
 * FIXME: Get rid of this class!!!!!
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 * 
 * @version Oct. 20, 2013
 */
public class UICore {

	private static final UICore core = new UICore();
	
	private HashMap<Node,Canvas2Vertex> graphicalRepresentations = new HashMap<>();

	private List<VisualizationChangeListener> visualizationListeners = new ArrayList<>();
	
	private static UI ui;
	
	private UICore() {}
	
	public static UICore getInstance() {
		return core;
	}
	
	public static void setUI(UI ui) {
		UICore.ui = ui;
	}
	
	public static UI getUI() { return ui; }
	
	
	public void addVisualizationChangeListener( VisualizationChangeListener l )  { visualizationListeners .add(l); }
	public void removeVisualizationChangeListener( VisualizationChangeListener l ) { visualizationListeners.remove(l); }
	
	public void fireEvent( final VisualizationChangeEvent event ) {
		for( int i = visualizationListeners.size()-1; i >= 0; i-- ) {  // count DOWN from max (for a very good reason, http://book.javanb.com/swing-hacks/swinghacks-chp-12-sect-8.html )
			// execute each event in its own thread.
			
			final int finalI = i; // declared final so that it may be used in the anonymous Thread class.
			Thread t = new Thread() {
				public void run() {
					visualizationListeners.get(finalI).visualizationSettingChanged(event);
				};
			};
			
			t.start();
			
		}
	}
	
}
