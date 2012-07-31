package am.userInterface.canvas2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.OntologyChangeEvent;
import am.app.ontology.OntologyChangeListener;
import am.userInterface.AppPreferences;
import am.userInterface.Colors;
import am.userInterface.VisualizationChangeEvent;
import am.userInterface.VisualizationChangeListener;
import am.userInterface.VisualizationPanel;
import am.userInterface.canvas2.graphical.GraphicalData.NodeType;
import am.userInterface.canvas2.graphical.MappingData;
import am.userInterface.canvas2.layouts.LegacyLayout;
import am.userInterface.canvas2.nodes.LegacyMapping;
import am.userInterface.canvas2.utility.Canvas2Edge;
import am.userInterface.canvas2.utility.Canvas2Layout;
import am.userInterface.canvas2.utility.Canvas2Vertex;
import am.userInterface.canvas2.utility.CanvasGraph;
import am.userInterface.canvas2.utility.GraphLocator;
import am.userInterface.canvas2.utility.GraphLocator.GraphType;


/**
 * The next version of the Canvas.
 * 
 * Sleeker, Faster, Easier to code for, and more Object Oriented
 * 
 * 
 * 	Requirements:
 * 		- Vertex objects that support multiple inheritance ( DO NOT EXTEND TREENODE! *facepalm* )
 * 		- FAST repaint(), i.e. only repaint the objects that are being currently viewed. (instead of the whole canvas everytime)
 * 		- NEW Views!
 * 			- leaf to leaf view
 * 		
 * 		- Order the Vertices so that mapping lines are shortest!
 * 		- Different ordering of vertices (alphabetical (asc/desc), by number of children, by "weight")
 * 
 * @author Cosmin Stroe
 * @datestarted 11/3/2009
 *
 */

public class Canvas2 extends VisualizationPanel implements OntologyChangeListener, MatcherChangeListener, VisualizationChangeListener {

	private static final Logger sLog = Logger.getLogger(Canvas2.class);
	
	private static final long serialVersionUID = 1544849142971067136L;


/*******************************************************************************
 **************************** CANVAS2 VARIABLES ********************************
 *******************************************************************************/
	
	// The list of the layout graphs for each of the ontologies.
	private ArrayList<CanvasGraph> graphs;  // holds all the ACTIVE graphs, those graphs that are drawn on the canvas
	
	private ArrayList<Canvas2Vertex> visibleVertices;  // everytime we paint the canvas, we keep a list of the visible nodes. (used in mouse movement functions)
	private ArrayList<Canvas2Edge>   visibleEdges;  // also keep a list of visible edges
	
	private Canvas2Layout layout; // the Canvas2Listener is the muscle of the operation, i.e. it does everything related to layout.
	
	public int Xpadding = 20;
	public int Ypadding = 20;


	private boolean painting = false;
	
	/* ************************************************* CONSTRUCTORS ********************************* */
	
	public Canvas2() { super(); initialize(null,null); }
	public Canvas2(JScrollPane s) { super(s); initialize(s,null); }
	public Canvas2(Canvas2Layout layout) { super(); initialize(null, layout); } // allows the use of a custom layout
	public Canvas2(JScrollPane s, Canvas2Layout layout) { super(s); initialize(s, layout); } // allows the use of a custom layout
	
	/** Shared method called from the constructors. */
	private void initialize(JScrollPane scrollPane, Canvas2Layout layout) {
		
		if( scrollPane != null ) setScrollPane(scrollPane);
		
		if( layout == null ) layout = new LegacyLayout(this);
		
		setCanvasLayout(layout);
		
		/* Setup the Listeners */
		setLayout(new BorderLayout());
		//addMouseWheelListener(layout);  // TODO: Add this later.  For now let the jscrollpane listen for mouse wheel events.
		Core.getInstance().addOntologyChangeListener(this);
		Core.getInstance().addMatcherChangeListener(this);
		Core.getInstance().addVisualizationChangeListener(this);
	}
	
	public ArrayList<Canvas2Vertex> getVisibleVertices() { return visibleVertices; }
	public ArrayList<Canvas2Edge>   getVisibleEdges()    { return visibleEdges; }
	
	/**
	 * Ontology Change Listener methods 
	 */
	public synchronized void ontologyChanged(OntologyChangeEvent e) { 
		/**
		 *  This function gets called when an ontology is added or removed to/from the Core.
		 *  When this happens, we need to create the layout graph for the ontologies. 
		 */
		if( e.getEvent() == OntologyChangeEvent.EventType.ONTOLOGY_ADDED ) {
			// an ontology was added.
			layout.displayOntology(graphs,  e.getOntologyID() );
			updateSize();
			
			final Canvas2 canvas = this;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					canvas.repaint();
				}
			});
			
		} else if( e.getEvent() == OntologyChangeEvent.EventType.ONTOLOGY_REMOVED ) {
			layout.removeOntology( graphs, e.getOntologyID() );
			updateSize();
			
			final Canvas2 canvas = this;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					canvas.repaint();
				}
			});
		}
		
	}
	
	/**
	 * This function is called from within OntoTreeBuilder, when the OntoTreeBuilder first gets its hands on an ontology.
	 * 
	 * We had to do this in order to unify all the actions done when an ontology is loaded.  Otherwise, actions that get
	 * done when an ontology is loaded are called outside of the "Loading Ontology" progress display.
	 */
	@Override
	public void buildLayoutGraphs(Ontology ontology) {
		// build the graph and put it in the repository, because we are not necessarily going to display it now
		List<CanvasGraph> gr = layout.buildGlobalGraph(ontology);
		Iterator<CanvasGraph> graphIter = gr.iterator();
		while( graphIter.hasNext() ) { graphIter.next().setVisible(false); } // initially the graphs should be invisible.  
																			 // they will be displayed when displayOntology() is called.
		graphs.addAll( gr );  // add them to our list of graphs.
	}
	
	
	public ArrayList<CanvasGraph> getGraphs() { return graphs; }
	
	// return a matcher graph with the given id (used in deleting mappings)
	public CanvasGraph getMatcherGraph( int matcherID ) {
		Iterator<CanvasGraph> graphIter = graphs.iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph gr = graphIter.next();
			if( gr.getGraphType() == GraphType.MATCHER_GRAPH && gr.getID() == matcherID ) {
				return gr;
			}
		}
		
		return null;
	}
	
	/**
	 * Update the size of the Canvas after graphs were changed.
	 */
	private void updateSize() {
		Logger log = null;  // used to display debugging messages
		
		
		Iterator<CanvasGraph> graphsIter = graphs.iterator();
		int width = 0;
		int height = 0;
		
		if( Core.DEBUG ) {
			log = Logger.getLogger(this.getClass());
			log.setLevel(Level.DEBUG);
		}
		
		while( graphsIter.hasNext() ) {
			CanvasGraph gr = graphsIter.next();
			if( gr.getGraphType() == GraphLocator.GraphType.LAYOUT_GRAPH_IGNORE_BOUNDS ) continue;  // ignoring this type of graph
			Rectangle grBounds = gr.getBounds();
			if( Core.DEBUG ) log.debug("updateSize: considering graph bounds: " + grBounds );
			if( width < grBounds.x + grBounds.width ) 
				width = grBounds.x + grBounds.width;
			if( height < grBounds.y + grBounds.height ) 
				height = grBounds.y + grBounds.height; 
		}
		
		
		setPreferredSize(new Dimension( width + Xpadding, height + Ypadding));
		revalidate();
		if( Core.DEBUG ) log.debug("New Canvas dimensions: w = " + Integer.toString(width) + ", h = " + Integer.toString(height));
		
	}

	/**
	 * The paint function tries to paint only those graphical elements that are in the current viewport bounds.
	*/
	public synchronized void paintComponent(Graphics g ) {
		if( painting ) return;  // avoid concurrent paintings
		painting = true;
		super.paintComponent(g);
		
		Rectangle currentView = viewport.getViewRect();
		visibleVertices = new ArrayList<Canvas2Vertex>();  // throw away the old list on every repaint
		visibleEdges = new ArrayList<Canvas2Edge>();  // same as above comment
		
		/* Get ready for a paint() */
		layout.getReadyForRepaint(currentView);
		
		/* The background color */
		g.setColor( Colors.background );
		g.fillRect(currentView.x, currentView.y, currentView.width, currentView.height);
		
		
		int nodeNum = 0;  // used for debugging
		int edgeNum = 0;
		int graphsnotVis = 0;
		int nodeNotVis = 0;
		int edgeNotVis = 0;
		
		
		
		// draw the edges before we draw the vertices
		// TODO: FIND A BETTER WAY TO DO THIS: CHANGE THE WAY THE ITERATOR ITERATES, -> GRAPH PRIORITY.  Currently TIME IS WASTED ITERATING TWICE THROUGH THE GRAPHS!!!!
		Iterator<CanvasGraph> graphIter = graphs.iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph graph = graphIter.next();
			if( !graph.isVisible(currentView) ) { // if the whole graph is not visible, skip redrawing its elements 
				graphsnotVis++; 
				continue; } 
			
			
			
			// draw the edges before we draw the vertices
			Iterator<Canvas2Edge> edgeIter = graph.edges();
			while( edgeIter.hasNext() ) {
				Canvas2Edge edge = edgeIter.next();
				if( !edge.isVisible(currentView) ) {
					edgeNotVis++;
					continue;
				} else {
					edgeNum++;
					edge.draw(g);
					visibleEdges.add(edge);  // keep track of which edges are visible (used in the mouse handler in the layout class)
				}
			}
		}
		
		
		// Draw the vertices.
		graphIter = graphs.iterator();
		while( graphIter.hasNext() ) {
			CanvasGraph graph = graphIter.next();
			if( !graph.isVisible(currentView) ) { // if the whole graph is not visible, skip redrawing its elements 
				//graphsnotVis++; 
				continue; } 
			
			// draw the vertices
			Iterator<Canvas2Vertex> nodeIter = graph.vertices();
			while( nodeIter.hasNext() ) {
				Canvas2Vertex node = nodeIter.next();
				if( !node.isVisible(currentView) ) { 
					nodeNotVis++; // used for debugging
					continue; }
				else { 
					node.draw(g); 
					nodeNum++; // used for debugging
					visibleVertices.add(node); // keep track of which nodes are visible (used in the mouse handler in the layout class)
				}
			}
			
		}
		
		painting = false;
		
		sLog.debug("paint(): viewport( "+currentView +") - " + Integer.toString(nodeNum) + " nodes and " +
				  Integer.toString(edgeNum) +" edges visible. (" + Integer.toString(nodeNotVis) +
									" nodes not visible, " + Integer.toString(edgeNotVis) +" edges not visible, " + Integer.toString(graphsnotVis) + " graphs not visible.)");
		
		return;
	}

	
	public synchronized void matcherChanged(MatchingTaskChangeEvent e) {

		final MatchingTask task = Core.getInstance().getMatchingTaskByID( e.getTaskID() );
		
		if( task == null ) {
			sLog.warn("Matcher Change Event fired with invalid task id: " + e.getTaskID() + ". Ignoring." );
			return;
		}
		
		switch( e.getEvent() ) {
		case MATCHER_ADDED:
			graphs.add( layout.buildMatcherGraph(task) );
			repaint();
			break;
			
		case MATCHER_ALIGNMENTSET_UPDATED:
			
			// first, remove the matcher graph from the list of graphs.
			int matcherID = e.getTaskID();
			Iterator<CanvasGraph> graphIter = graphs.iterator();
			while( graphIter.hasNext() ) {
				CanvasGraph gr = graphIter.next();
				if( gr.getID() == matcherID ) {
					gr.detachEdges();  // because this is a matcher graph, we only have to detach the edges
					graphs.remove(gr);
					break;
				}
			}
			
			graphs.add( layout.buildMatcherGraph(task) );
			repaint();
			break;
			
		case MATCHER_REMOVED:
			// first, remove the matcher graph from the list of graphs.
			int matcherID1 = e.getTaskID();
			Iterator<CanvasGraph> graphIter1 = graphs.iterator();
			while( graphIter1.hasNext() ) {
				CanvasGraph gr = graphIter1.next();
				if( gr.getID() == matcherID1 ) {
					gr.detachEdges();  // because this is a matcher graph, we only have to detach the edges
					graphs.remove(gr);
					break;
				}
			}
			repaint();
			break;
			
		case MATCHER_VISIBILITY_CHANGED:
			int matcherID11 = e.getTaskID();
			Iterator<CanvasGraph> graphIter11 = graphs.iterator();
			final boolean visible = task.visData.isShown;
			while( graphIter11.hasNext() ) {
				CanvasGraph gr = graphIter11.next();
				if( gr.getID() == matcherID11 ) {
					gr.setVisible(visible);
					for( Canvas2Edge edge : gr.getEdges() ) {  // added the for loop because of the filtering changes! - Cosmin.
						edge.getObject().visible = visible;
					}
					break;
				}
			}
			repaint();
			break;
			
		case MATCHER_COLOR_CHANGED:
			int matcherID11c = e.getTaskID();
			sLog.debug("Color change event from task ID: " + e.getTaskID());
			Iterator<CanvasGraph> graphIter11c = graphs.iterator();
			
			final Color taskColor = task.visData.color;
			
			while( graphIter11c.hasNext() ) {
				CanvasGraph gr = graphIter11c.next();
				if( gr.getID() == matcherID11c ) {
					Iterator<Canvas2Edge> edgesIter = gr.edges();
					while( edgesIter.hasNext() ) {
						Canvas2Edge edge = edgesIter.next();
						if( edge instanceof LegacyMapping ) {
							MappingData mapdata = (MappingData)edge.getObject();
							mapdata.color = taskColor;
						}
					}
					
					break;
				}
			}
			repaint();
			break;
		
		case REMOVE_ALL:
			// remove all the matcher graphs from the graphs list
			for( int i = graphs.size() - 1; i >= 0; i-- ) {
				CanvasGraph gr = graphs.get(i);
				if( gr.getGraphType() == GraphLocator.GraphType.MATCHER_GRAPH ) {
					gr.detachEdges(); // because this is a matcher graph, we only have to detach the edges
					graphs.remove(gr);
				}
			}
			break;
			
		}
		
	}
		
	
	@Override public void setShowLocalName(boolean showLocalname) { layout.setShowLocalName(showLocalname); }
	@Override public void setShowLabel( boolean showLabel ) { layout.setShowLabel(showLabel); }
	@Override public boolean getShowLocalName() { return layout.getShowLocalName(); }
	@Override public boolean getShowLabel() { return layout.getShowLabel(); }

	@Override
	public void visualizationSettingChanged(VisualizationChangeEvent e) {
		// TODO: This code should be part of the Canvas2Layout.
		
		switch( e.getEvent() ) {
		case TOGGLE_SHOWMAPPINGSSHORTNAME: {
			// check to see if we are dealing with a JCheckBoxMenuItem as the source (should aways be the case unless the code is changed)
			if( e.getSource() instanceof JCheckBoxMenuItem ) {
				// the source of the event is the checkbox menu item itself
				boolean showMapName = ((JCheckBoxMenuItem)e.getSource()).isSelected();
				
				
/*				
				// ok, we have to search through all the graphs, looking for MATCHER_GRAPHs.
				// then, for each graph, we update the mappings to include the shortName of the matcher.
				for( CanvasGraph gr : graphs ) {
					if( gr.getGraphType() == GraphLocator.GraphType.MATCHER_GRAPH ) {
						AbstractMatcher m = Core.getInstance().getMatcherByID( gr.getID() );
						String shortName = MatcherFactory.getMatchersRegistryEntry( m.getClass() ).getMatcherShortName();
						// update all the Mapping edges in this graph to include the shortName
						for( Iterator<Canvas2Edge> edgesIter = gr.edges(); edgesIter.hasNext(); ) {
							Canvas2Edge edge = edgesIter.next();
							
							if( edge.getObject().type == NodeType.MAPPING ) {
								// we only care about mappings.
								MappingData mdata = (MappingData) edge.getObject();
								if( mdata.alignment != null ) {
									String sim = Utility.getNoDecimalPercentFromDouble(mdata.alignment.getSimilarity());
									
									if( showMapName ) {
										mdata.label = sim + " - " + shortName; 
									} else {
										mdata.label = sim;
									}
								}
							}
						}
						
					}
				}
*/	
								
				// we only care about the currently visible mappings.
				for( Canvas2Edge edge : visibleEdges ) {
					if( edge.getObject().type == NodeType.MAPPING ) {
						// we only care about mappings.
						MappingData mdata = (MappingData) edge.getObject();
						if( mdata.alignment != null ) {
							MatchingTask m = Core.getInstance().getMatchingTaskByID( mdata.matcherID );
							
							final String shortName = m.shortLabel;
							
							String sim = Utility.getNoDecimalPercentFromDouble(mdata.alignment.getSimilarity());
							
							if( showMapName && shortName != null) {
								mdata.label = sim + " - " + shortName; 
							} else {
								mdata.label = sim;
							}
						}
					}
				}
			}
			
			repaint();
		}
		break;
		case CONCEPT_SELECTED: {
			// a concept was selected
			AppPreferences prefs = Core.getAppPreferences();
			if( !prefs.getSynchronizedViews() ) break;
			
			// select the concept that was selected by the other visualizations
			if( !e.getSource().equals(layout) && e.getPayload() instanceof Node )  {
				Node n = (Node)e.getPayload();
				layout.selectNode(n);
			}
		}
		break;
		
		}
		
	}
	
	/**
	 * The layout is responsible for placing graphical elements on the screen.
	 */
	public void setCanvasLayout(Canvas2Layout layout) {
		this.layout = layout;
		addMouseMotionListener(layout);
		addMouseListener(layout);
		
		// when the layout changes, the old graphs are discarded.  TODO: Check for memory leaks.
		graphs = new ArrayList<CanvasGraph>();	// this is our master list of graphs to be displayed
		
		CanvasGraph artifacts = layout.getArtifactsGraph();  // The layout has its own artifacts.
		if( artifacts != null ) {
			graphs.add(artifacts);  // if there are artifacts that the layout uses, add that to the graph list so it's displayed
		}
	}
	
	/** The canvas layout must not be null in order for this method to work properly. 
	 * (A null pointer exception will be thrown otherwise.) 
	 */
	@Override
	public void setScrollPane(JScrollPane jsp) {
		super.setScrollPane(jsp);
		viewport.addChangeListener(layout);
	}

}
