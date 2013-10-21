package am.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import am.app.mappingEngine.MatchingTask;
import am.app.ontology.Node;
import am.ui.matchingtask.MatchingTaskVisData;
import am.ui.ontology.OntologyConceptGraphics;

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
	
	private HashMap<Node,List<OntologyConceptGraphics>> graphicalRepresentations = new HashMap<>();
	
	private HashMap<MatchingTask, MatchingTaskVisData> visualizationData = new HashMap<>();

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
	
	
	public void addGraphicalRepresentation(Node node, OntologyConceptGraphics vertex) {
		if( !graphicalRepresentations.containsKey(node) ) {
			List<OntologyConceptGraphics> list = new LinkedList<>();
			graphicalRepresentations.put(node, list);
		}
		graphicalRepresentations.get(node).add(vertex);
	}
	
	public void removeGraphicalRepresentation(Node node, OntologyConceptGraphics vertex) {
		if( graphicalRepresentations.containsKey(node) ) {
			List<OntologyConceptGraphics> l = graphicalRepresentations.get(node);
			l.remove(vertex);
			if( l.isEmpty() ) {
				graphicalRepresentations.remove(node);
			}
		}
	}
	
	/**
	 * Determine if a certain graphical representation has an object registered with this Node.
	 * @param c
	 * @return
	 */
	public boolean hasGraphicalRepresentation( Node node, Class<?> c ) {
		List<OntologyConceptGraphics> list = graphicalRepresentations.get(node);
		if( list == null ) return false;
		Iterator<OntologyConceptGraphics> gr = list.iterator();
		while( gr.hasNext() ) {
			OntologyConceptGraphics g = gr.next();
			if ( g.getImplementationClass().equals(c) ) {
				return true;
			}
		}
		return false;
	}
	
	public OntologyConceptGraphics getGraphicalRepresentation(Node node, Class<?> c) {
		List<OntologyConceptGraphics> list = graphicalRepresentations.get(node);
		if( list == null ) return null;
		Iterator<OntologyConceptGraphics> gr = list.iterator();
		while( gr.hasNext() ) {
			OntologyConceptGraphics g = gr.next();
			if ( g.getImplementationClass().equals(c) ) {
				return g;
			}
		}
		return null;
	}
	
	public void setVisData(MatchingTask task, MatchingTaskVisData data) {
		visualizationData.put(task, data);
	}
	
	public MatchingTaskVisData getVisData(MatchingTask task) {
		if( !visualizationData.containsKey(task) ) {
			MatchingTaskVisData data = new MatchingTaskVisData();
			visualizationData.put(task, data);
			return data;
		}
		else {
			return visualizationData.get(task);
		}
	}
}
