package am.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleContext;

import am.AMException;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.LexiconStore;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatcherResult;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.MatchingTaskChangeEvent.EventType;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.OntologyChangeEvent;
import am.app.ontology.OntologyChangeListener;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.osgi.AMHost;
import am.userInterface.AppPreferences;
import am.userInterface.Colors;
import am.userInterface.UI;
import am.userInterface.VisualizationChangeEvent;
import am.userInterface.VisualizationChangeListener;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * SINGLETON JAVA PATTERN
 * There will be only one instance of this class in the whole application
 * All classes can access it without having any reference to it, just invoking the static method
 * Core.getInstance()
 * All model information of the system will be accessible from this class:
 * ontologies, matchings and so on.
 *
 */
public class Core {
	
	// Program wide DEBUG flag.
	public static final boolean DEBUG = false;
	public static final boolean DEBUG_STACK_TRACE_MSG = false;
	public static final boolean DEBUG_NORMALIZER = false;  // debug flag for the am.app.mappingEngine.StringUtil.Normalizer class
	public static final boolean DEBUG_ONTOLOGYLEXICONSYNSET = false;
	public static boolean DEBUG_PSM = true;
	public static boolean DEBUG_VMM = false;
	public static final boolean DEBUG_FCM = false;
	
	/**List of matchers instances run by the user
	 * Data of the tableModel of the matcherTable is taken from this structure
	 * Can't be null;
	 */
	
	private final List<AbstractMatcher> matcherInstances = new ArrayList<AbstractMatcher>();
	private final List<MatcherResult> matcherResults = new ArrayList<MatcherResult>();
	
	/**
	 * Keep a list of completed matching tasks currently in the system.
	 * When two ontologies are to be aligned a matching task is created and then
	 * executed.  The completed matching task is stored in this list, to be used by 
	 * the various components in AgreementMaker (e.g., visualization).
	 */
	private final List<MatchingTask> completedMatchingTasks = new ArrayList<MatchingTask>();
	
	private int IDcounter = 0;  // used in generating IDs for the ontologies and matchers
	public static final int ID_NONE = -1;  // the ID for when no ID has been set	
	
	private Ontology sourceOntology; //Null if it's not loaded yet
	private Ontology targetOntology; //Null if it's not loaded yet
	
	/**
	 * The ArrayList of ontologies that are currently loaded into the AgreementMaker.
	 * This adds support for more than two ontologies to be loaded into the AgreementMaker
	 * 
	 * Also, when an ontology is loaded, we need to let the GUI know, so we have an OntologyChangeListener interface.
	 * Interested parts of our code can register as an OntologyChangeListener of the core, by calling:
	 * 		- addChangeListener()
	 * 		- or removeChangeListener() to remove from the listener list
	 */
	private ArrayList<Ontology> loadedOntologies;
	
	/** Various change listeners. **/
	private ArrayList<OntologyChangeListener>      ontologyListeners;
	private ArrayList<MatcherChangeListener>  	   matcherListeners;
	private ArrayList<VisualizationChangeListener> visualizationListeners;
	
	
	private static AppPreferences prefs;
	
	private static LexiconStore lexstore; // The Lexicon store for these ontologies.
	
	private static UI ui; 	// A reference to the userinterface instance, canvas and table can be accessed anytime. 
							// It used often to invoke the method redisplayCanvas()
	
	private static Core core  = new Core(); // Singleton pattern: unique instance

	private AMHost osgi;
	
	
	/**
	 * 
	 * @return the unique instance of the core
	 */
	public static Core getInstance() {
		return core;
	}
	/**
	 * It's private because it's not possible to create new instances of this class
	 */
	private Core() {
		//System.setProperty("log4j.debug","strue" );  // Use this to see what log4j gets configured to.
	
		loadedOntologies = new ArrayList<Ontology>();  // initialize the arraylist of ontologies.
		ontologyListeners    = new ArrayList<OntologyChangeListener>();  // new list of listeners
		matcherListeners	= new ArrayList<MatcherChangeListener>(); // another list of listeners
		visualizationListeners = new ArrayList<VisualizationChangeListener>();
		
		lexstore = new LexiconStore();
		addOntologyChangeListener(lexstore);
		
		prefs = new AppPreferences();
	}
	
	// deprecated by multiple-ontology interface (TODO: Finish implementing multiple-ontology interface. - Cosmin 10/17/2010)
	public Ontology getSourceOntology() {  return sourceOntology; }
	public Ontology getTargetOntology() { return targetOntology; } // deprecated by multi-ontology interface
	
	public void setSourceOntology(Ontology sourceOntology) {  // deprecated by multi-ontology array
    	this.sourceOntology = sourceOntology;
		addOntology(sourceOntology); // support for more than 2 ontologies
	}

	public void setTargetOntology(Ontology targetOntology) {  // deprecated by multi-ontology interface
		this.targetOntology = targetOntology;
		addOntology(targetOntology); // support for more than 2 ontologies
	}
	
	public boolean sourceIsLoaded() { return sourceOntology != null; }
	public boolean targetIsLoaded() { return targetOntology != null; }
	public boolean ontologiesLoaded() { return sourceIsLoaded() &&  targetIsLoaded(); }  // convenience function
	
	
	/**
	 * This function will return the first Matcher instance of type "matcher" in the AM (ordered by its index).
	 * @return Returns null if a matcher of the specified type does not exist in the system.
	 */
	@Deprecated
	public AbstractMatcher getMatcherInstance( MatchersRegistry matcher ) {
	
		for( int i = 0; i < matcherInstances.size(); i++ ) {
			String m1 = matcherInstances.get(i).getRegistryEntry().getMatcherClass();
			String m2 = matcher.getMatcherClass();
			
			if( Core.DEBUG ) {
				System.out.println("m1: " + m1);
				System.out.println("m2: " + m2);
				System.out.println( m1.equals(m2) );
				System.out.println( m1 == m2 );
			}
			
			if( m1.equals(m2)  ) {
				return matcherInstances.get(i);
			}
			
		}
		return null;	
	}
	
	/**
	 * <p>Returns a list of all the matchers currently registered
	 * in the system. </p>
	 * 
	 * <p>Currently this is implemented to work with the OSGi registry,
	 * iterate through all the registered matching algorithms, and
	 * return them as a list. </p>
	 * 
	 * @return An empty list if no matchers are registered in the system.
	 * 
	 * @see {@link #getFramework()}, {@link AMHost#getRegistry()}
	 */
	public List<AbstractMatcher> getMatchingAlgorithms() {
		List<AbstractMatcher> matchers = null;
		if( getFramework() != null 
				&& getFramework().getRegistry() != null ) {
			matchers = getFramework().getRegistry().getMatchers();
		}
		else {
			matchers = new LinkedList<AbstractMatcher>();
		}
		
		return matchers;
	}
	
	/**
	 * Returns a matching algorithm given its name.
	 * 
	 * @param name The name of the matching algorithm.
	 * @return The first algorithm whose name is exactly the same as name. null if there is no match.
	 * 
	 * @see {@link #getMatchingAlgorithms()}
	 */
	public AbstractMatcher getMatchingAlgorithm(String name) {
		List<AbstractMatcher> matchers = getMatchingAlgorithms();
		
		for( AbstractMatcher matcher : matchers ) {
			if( matcher.getName().equals(name) ) return matcher;
		}
		
		return null;
	}
	
	/** Use {@link #getMatchingAlgorithms()} instead. -- Cosmin. */
	@Deprecated
	public List<AbstractMatcher> getMatcherInstances() { return getMatchingAlgorithms(); }
	
	@Deprecated
	public List<MatcherResult> getMatcherResults(){ return matcherResults;}
	
	public List<MatchingTask> getMatchingTasks() { return completedMatchingTasks; }
	
	/** Matcher IDs no longer make sense. -- Cosmin. */
	@Deprecated
	public AbstractMatcher getMatcherByID( int mID ) {
		Iterator<AbstractMatcher> matchIter = matcherInstances.iterator();
		while( matchIter.hasNext() ) {
			AbstractMatcher a = matchIter.next();
			if( a.getID() == mID ) { return a; }
		}
		return null;
	}
	
	public MatchingTask getMatchingTaskByID( int id ) {
		for( MatchingTask currentTask : completedMatchingTasks ) 
			if( currentTask.getID() == id ) 
				return currentTask; 

		return null;
	}
	
	/**
	 * @return Return a result from the given result ID. Returns null if no results match the id.
	 */
	@Deprecated
	public MatcherResult getResultByID( int id ) {
		for( MatcherResult result : matcherResults ) 
			if( result.getID() == id ) return result; 

		return null;
	}
	
	// this method adds a matcher to the end of the matchers list.
	@Deprecated
	public void addMatcherInstance(AbstractMatcher a) {
		a.setIndex( matcherInstances.size() );
		a.setColor(Colors.matchersColors[a.getIndex()%6]);
		a.setID(getNextMatcherID());
		matcherInstances.add(a);
		fireEvent( new MatchingTaskChangeEvent(a, MatchingTaskChangeEvent.EventType.MATCHER_ADDED, a.getID() ));
	}
	
	@Deprecated
	public void addMatcherResult(AbstractMatcher a){
		a.setIndex(matcherResults.size());
		a.setColor(Colors.matchersColors[a.getID()%6]);
		a.setID(getNextMatcherID());
		matcherResults.add(a.getResult());
		fireEvent( new MatchingTaskChangeEvent(a, MatchingTaskChangeEvent.EventType.MATCHER_ADDED, a.getID() ));
	}
	
	@Deprecated
	public void removeMatcher(AbstractMatcher a) {
		MatchingTaskChangeEvent evt = new MatchingTaskChangeEvent(a, MatchingTaskChangeEvent.EventType.MATCHER_REMOVED, a.getID());
		//int myIndex = a.getIndex();
		matcherInstances.remove(a);
		//All indexes must be decreased by one;
		//For this reason whenever you have to delete more then one matcher, it's good to start from the last in the order
		// TODO: The above behavior should be fixed - cosmin
		AbstractMatcher next;
		for(int i = 0; i<matcherInstances.size(); i++) {
			next = matcherInstances.get(i);
			next.setIndex(i);
		}
		fireEvent(evt);
		
	}
	
	/**
	 * Add a newly completed matching task to the task list.
	 * @param mt The completed matching task.
	 */
	public void addMatchingTask(MatchingTask mt) {
		completedMatchingTasks.add(mt);
		fireEvent( new MatchingTaskChangeEvent( mt, EventType.MATCHER_ADDED) );
	}
	
	/**
	 * Remove the reference to a matching task.  This is usually done
	 * when the matching task is removed the from matchers control panel.
	 * @param mt The task to be removed.
	 */
	public void removeMatchingTask(MatchingTask mt) {
		completedMatchingTasks.remove(mt);
		fireEvent( new MatchingTaskChangeEvent( mt, EventType.MATCHER_REMOVED) );
	}
	
	/**
	 * Called to remove all the matching tasks in the systems.
	 */
	public void removeAllMatchingTasks() {
		completedMatchingTasks.clear();
		fireEvent( new MatchingTaskChangeEvent( EventType.REMOVE_ALL) );
	}
	
	public static UI   getUI()      { return ui;    }
	public static void setUI(UI ui) { Core.ui = ui; }
	
	public static LexiconStore getLexiconStore() { return lexstore; }
	public static AppPreferences getAppPreferences() { return prefs; }
	
	/**
	 * Some selection parameters or some information in the alignMatrix of the matcher a are changed,
	 * so it's needed to invoke a.select() and also to update all other matchers with a as inputMatcher
	 * matchers are kept in chronological order in the list , so any matcher with a as input must be in a next entry of the list
	 * at the beginning there will be only one matcher in the modifiedList that is a
	 * anytime we find another matcher a2 with a as input we have to invoke a2.match() and we have to add a2 to the modifiedmatchings
	 * so that all other matchers with a and/or a2 as input will be updated to and so on.
	 * @param a the matcher that has been modified and generates a chain reaction on other matchings
	 * @throws AMException 
	 */
	public void selectAndUpdateMatchers(AbstractMatcher a) throws Exception{
		a.select();
		MatchingTaskChangeEvent evt = new MatchingTaskChangeEvent(a, MatchingTaskChangeEvent.EventType.MATCHER_ALIGNMENTSET_UPDATED, a.getID() );
		fireEvent(evt);
		updateMatchers(a);

	}
	
	public void matchAndUpdateMatchers(AbstractMatcher a) throws Exception{
		a.match();
		MatchingTaskChangeEvent evt = new MatchingTaskChangeEvent(a, MatchingTaskChangeEvent.EventType.MATCHER_ALIGNMENTSET_UPDATED, a.getID() );
		fireEvent(evt);
		updateMatchers(a);
	}
	
	private void updateMatchers(AbstractMatcher a) throws Exception {
		//Chain update of all matchers after a
		int startingIndex =  a.getIndex()+1;
		ArrayList<AbstractMatcher> modifiedMatchers = new ArrayList<AbstractMatcher>();
		modifiedMatchers.add(a);
		AbstractMatcher current;
		AbstractMatcher modified;
		for(int i = startingIndex; i < matcherInstances.size(); i++) {
			//for each matcher after a: current, we have to check if it contains any modified matchers as input
			current = matcherInstances.get(i);
			if(current.getMaxInputMatchers() > 0) {
				for(int j = 0; j < modifiedMatchers.size(); j++) { //scan modified matchers at the beginning is only a
					modified = modifiedMatchers.get(j);
					if(current.getInputMatchers().contains(modified)) {//if current contains any of the modified matchers as input matcher
						current.match(); //reinvoke() current and add it to the modified list
						MatchingTaskChangeEvent evt = new MatchingTaskChangeEvent(a, MatchingTaskChangeEvent.EventType.MATCHER_ALIGNMENTSET_UPDATED, current.getID() );
						fireEvent(evt);
						modifiedMatchers.add(current);
						break;
					}
				}
			}
		}
	}
	/**
	 * add or update the alignments selected by the user in all the matchers selected in the table
	 * @param alignments selected by the user
	 */
	public void performUserMatching(int index, ArrayList<Mapping> alignments) throws Exception {
		AbstractMatcher matcher = matcherInstances.get(index);
		matcher.addManualAlignments(alignments);
		
		// fire a matcher change event.
		MatchingTaskChangeEvent evt = new MatchingTaskChangeEvent(matcher, MatchingTaskChangeEvent.EventType.MATCHER_ALIGNMENTSET_UPDATED, matcher.getID() );
		fireEvent(evt);
	}
	
	
	
	
	
	public Node getNode(String localname, boolean fromSource, boolean fromClasses) {
		Ontology o = sourceOntology;
		Node result = null;;
		if(!fromSource) {
			o = targetOntology;
		}
		List<Node> list = o.getClassesList();
		if(!fromClasses) {
			list = o.getPropertiesList();
		}
		Iterator<Node> it = list.iterator();
		while(it.hasNext()) {
			Node n = it.next();
			String ln = n.getLocalName();
			if(ln.equalsIgnoreCase(localname)) {
				result = n;
				break;
			}
		}
		return result;
	}
	
	
	

	
	
	/***********************************************************************************************
	 * ************ Multi-ontology Support. ****************** - Cosmin, Nov 11, 2009.
	 */
	
	// this function gives a unique ID to every ontology that's loaded
	// this function is called in the TreeBuilder class, when the ID is set.
	// Ontology and Matcher IDs must be unique, hence they both increment the same variable when a new id is required
	public int getNextOntologyID() { return IDcounter++; }
	public int getNextMatcherID()  { return IDcounter++; }	
	
	
	public int numOntologies() {
		return loadedOntologies.size();
	}
	
	public Ontology getOntologyByIndex( int index ) { // TODO: should check for and throw an IndexOutOfBounds exception
		return loadedOntologies.get(index);
	}
	
	// Returns an ontology by its ID, null if there is no ontology by that ID
	public Ontology getOntologyByID( int id ) {
		Iterator<Ontology> ontIter = loadedOntologies.iterator();
		while( ontIter.hasNext() ) {
			Ontology ont = ontIter.next();
			if( ont.getID() == id ) return ont;
		}
		return null;
	}
	
	// Return the ID of the ontology that has a certain model.
	public int getOntologyIDbyModel(Model model) {
		
		Iterator<Ontology> ontIter = loadedOntologies.iterator();
		while( ontIter.hasNext() ) {
			Ontology ont = ontIter.next();
			if( ont.getModel().equals(model) )
				return ont.getID();
		}
		
		// no ontology exists with this ID.
		return Ontology.ID_NONE;
	}
	
	public Iterator<Ontology> getOntologiesIterator() {
		return loadedOntologies.iterator();
	}
	
	/**
	 * Adds an ontology to the core.
	 */
	public void addOntology( Ontology ont ) {
		if( !loadedOntologies.contains(ont) ) {
				loadedOntologies.add(ont); 
				// We must notify all the listeners that an ontology was loaded into the system.
				OntologyChangeEvent e = new OntologyChangeEvent(this, OntologyChangeEvent.EventType.ONTOLOGY_ADDED, ont.getID() );
				fireEvent(e);
		}
	}
	
	/** 
	 * Removes an ontology from the core.
	 */
	public void removeOntology( Ontology ont ) {
		if( loadedOntologies.contains(ont) ) {
			loadedOntologies.remove(ont);
			if( sourceOntology == ont ) sourceOntology = null;
			if( targetOntology == ont ) targetOntology = null;
			// Notify all the listeners that an ontology was removed from the system
			OntologyChangeEvent e = new OntologyChangeEvent(this, OntologyChangeEvent.EventType.ONTOLOGY_REMOVED, ont.getID() );
			fireEvent(e);
		}
		
		if( !ontologiesLoaded() ) { currentProfiler = null; } // clear the ontology profiler
	}
	
	
	/********************************************************************************************
	 * Ontology Change Events
	 * fireEvent will send the event to all the listeners.
	 * The listeners get updated whenever a new ontology is loaded to the core, or an ontology is removed from the core.
	 */
	public void addOntologyChangeListener( OntologyChangeListener l )    { ontologyListeners.add(l); }
	public void removeOntologyChangeListener( OntologyChangeListener l ) { ontologyListeners.remove(l); }
	
	public void fireEvent( final OntologyChangeEvent event ) {
		for( int i = ontologyListeners.size()-1; i >= 0; i-- ) {  // count DOWN from max (for a very good reason, http://book.javanb.com/swing-hacks/swinghacks-chp-12-sect-8.html )
			// execute each event in its own thread.
			
			final int finalI = i; // declared final so that it may be used in the anonymous Thread class.
			Thread t = new Thread() {
				public void run() {
					ontologyListeners.get(finalI).ontologyChanged(event);
				};
			};
			
			t.start();
		}
	}
	
	/** Same thing as ontology change events, but for Matchers **/
	public void addMatcherChangeListener( MatcherChangeListener l )  { matcherListeners.add(l); }
	public void removeMatcherChangeListener( MatcherChangeListener l ) { matcherListeners.remove(l); }
	
	public void fireEvent( final MatchingTaskChangeEvent event ) {
		for( int i = matcherListeners.size()-1; i >= 0; i-- ) {  // count DOWN from max (for a very good reason, http://book.javanb.com/swing-hacks/swinghacks-chp-12-sect-8.html )
			// execute each event in its own thread.
			
			final int finalI = i; // declared final so that it may be used in the anonymous Thread class.
			Thread t = new Thread() {
				public void run() {
					matcherListeners.get(finalI).matcherChanged(event);
				};
			};
			
			t.start();
			
			
		}
	}
	
	/** Same thing as ontology change events, but for Visualization components **/
	public void addVisualizationChangeListener( VisualizationChangeListener l )  { visualizationListeners.add(l); }
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
	
	/* ***************************** ONTOLOGY PROFILING **************************************** */
	private OntologyProfiler currentProfiler;
	
	/**
	 * Ontology profiler.  Set the profiler by using the Ontology -> Profiling... menu.
	 * @return  The current ontology profiling algorithm set.
	 */
	public OntologyProfiler getOntologyProfiler() { return currentProfiler; }
	
	/**
	 * Set the ontology profiler.
	 * @param p
	 */
	public void setOntologyProfiler( OntologyProfiler p ) { currentProfiler = p; }
	
	@Deprecated
	/* Use getFramework().getContext() instead */
	public BundleContext getContext() {
		return getFramework().getContext();
	}
	
	
	public void setFramework(AMHost host) {
		this.osgi = host;
	}
	
	public AMHost getFramework() { return osgi; }
	
}
