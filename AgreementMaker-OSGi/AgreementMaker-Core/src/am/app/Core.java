package am.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import am.AMException;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.LexiconStore;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatcherRegistry;
import am.app.mappingEngine.MatcherResult;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.MatchingTaskChangeEvent.EventType;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.OntologyChangeEvent;
import am.app.ontology.OntologyChangeListener;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.osgi.AMHost;
import am.app.osgi.OSGiRegistry;
import am.utility.AppPreferences;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * SINGLETON JAVA PATTERN
 * There will be only one instance of this class in the whole application
 * All classes can access it without having any reference to it, just invoking the static method
 * Core.getInstance()
 * All model information of the system will be accessible from this class:
 * ontologies, matchings and so on.
 * 
 * TODO: I think this class should be removed.  It's a crutch and a better design is needed. -- Cosmin, Oct. 20, 2013.
 *
 */
public class Core {
	
	private final Logger log = Logger.getLogger(Core.class);
	
	// Program wide DEBUG flag. -- Deprecated, these flags will be removed and replaced with log4j!!! -- Cosmin Aug. 3, 2012.
	@Deprecated public static final boolean DEBUG = false;
	@Deprecated public static final boolean DEBUG_STACK_TRACE_MSG = false;
	@Deprecated public static final boolean DEBUG_NORMALIZER = false;  // debug flag for the am.app.mappingEngine.StringUtil.Normalizer class
	@Deprecated public static final boolean DEBUG_ONTOLOGYLEXICONSYNSET = false;
	@Deprecated public static boolean DEBUG_PSM = true;
	@Deprecated public static boolean DEBUG_VMM = false;
	@Deprecated public static final boolean DEBUG_FCM = false;
	
	/**
	 * The root directory for all of our runtime data files.
	 */
	private String amRoot;
	
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
	//private ArrayList<VisualizationChangeListener> visualizationListeners;
	
	
	private static AppPreferences prefs;
	
	private static LexiconStore lexstore; // The Lexicon store for these ontologies.
	
	//private static UI ui; 	// A reference to the userinterface instance, canvas and table can be accessed anytime. 
							// It used often to invoke the method redisplayCanvas()
	
	private static Core core  = new Core(); // Singleton pattern: unique instance

	private MatcherRegistry registry;
	
	
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
	
		amRoot = System.getenv("AM_ROOT");

		if( amRoot == null ) {
			amRoot = System.getProperty("AM_ROOT");
		}
		
		if( amRoot == null ) {
			
			// First, check if AM_ROOT is a sibling project in the Eclipse Workspace, which is a typical setup
			if( !checkRootDirectory("../AM_ROOT") ) {
				// Second, check if AM_ROOT is a directory in the current working directory.
				if( !checkRootDirectory("AM_ROOT") ) {
					// Give up, and just use the current working directory.
					log.warn("The environment variable AM_ROOT is not set.  Using working directory as our root.");
					amRoot = System.getProperty("user.dir", (new File(".")).getAbsolutePath());
				}
			}
		}
		
		log.info("AgreementMaker root directory: " + amRoot);
		
		
		loadedOntologies = new ArrayList<Ontology>();  // initialize the arraylist of ontologies.
		ontologyListeners    = new ArrayList<OntologyChangeListener>();  // new list of listeners
		matcherListeners	= new ArrayList<MatcherChangeListener>(); // another list of listeners
		//visualizationListeners = new ArrayList<VisualizationChangeListener>();
		
		lexstore = new LexiconStore();
		addOntologyChangeListener(lexstore);
		
		prefs = new AppPreferences();

	}
	
	/**
	 * Check for the existance of the root directory, and set it if it exists.
	 * 
	 * TODO: Should not be setting amRoot here.
	 * 
	 * @param relativePath The path to the AM_ROOT directory
	 * @return true if the amRoot has been set, false otherwise.
	 */
	private boolean checkRootDirectory(String relativePath) {
		final File dir = new File(relativePath);
		if( dir.exists() ) {
			if( !dir.isDirectory() ) {
				log.warn("Cannot use directory for AM_ROOT because it is a file: " + dir.getAbsolutePath());
				return false;
			}
			else if( !dir.canRead() ) {
				log.warn("Cannot use directory for AM_ROOT because it does not have read permissions: " + dir.getAbsolutePath());
				return false;
			}
			else {
				amRoot = dir.getAbsolutePath();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return The root directory for AgreementMaker data files. All code should
	 *         reference this root when accessing configuration files, training
	 *         models, etc...
	 */
	public String getRoot() { return amRoot; }
	
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
	 * 
	 * @deprecated Use {@link #getMatchingAlgorithm(String)} or {@link #getMatchingAlgorithms()}
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
	 * @see {@link AMHost#getRegistry()}
	 */
	public List<AbstractMatcher> getMatchingAlgorithms() {
		List<AbstractMatcher> matchers = null;
		if( getRegistry() != null ) {
			matchers = getRegistry().getMatchers();
		}
		else {
			matchers = new LinkedList<AbstractMatcher>();
		}
		
		return matchers;
	}
	
	/**
	 * Returns a matching algorithm given its name.
	 * 
	 * @param matcherName The (long) name of the matching algorithm.
	 * @return The first algorithm whose name is exactly the same as name. null if there is no match.
	 * 
	 * @see {@link #getMatchingAlgorithms()}
	 */
	public AbstractMatcher getMatchingAlgorithm(String matcherName) {
		List<AbstractMatcher> matchers = getMatchingAlgorithms();
		
		for( AbstractMatcher matcher : matchers ) {
			if( matcher.getName().equals(matcherName) ) return matcher;
		}
		
		return null;
	}
	
	/**
	 * @return A list of all the selection algorithms currently registered with
	 *         the system. If there is an error with the framework or registry,
	 *         an empty list is returned.
	 */
	public List<SelectionAlgorithm> getSelectionAlgorithms() {
		List<SelectionAlgorithm> selectors = null;
		if( getRegistry() != null ) {
			selectors = getRegistry().getSelectors();
		}
		else {
			selectors = new LinkedList<SelectionAlgorithm>();
		}
		
		return selectors;
	}
	
	/** @deprecated Use {@link #getMatchingAlgorithms()} instead. -- Cosmin. */
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
	
	/**
	 * Add a newly completed matching task to the task list.
	 * @param mt The completed matching task.
	 */
	public void addMatchingTask(MatchingTask mt) {
		// update the ID
		mt.ID = getNextMatcherID();
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
	
	//public static UI   getUI()      { return ui;    }
	//public static void setUI(UI ui) { Core.ui = ui; }
	
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
	public void selectAndUpdateMatchers(MatchingTask t) throws Exception{
		t.select();
		MatchingTaskChangeEvent evt = new MatchingTaskChangeEvent(t, MatchingTaskChangeEvent.EventType.MATCHER_ALIGNMENTSET_UPDATED );
		fireEvent(evt);
		updateMatchers(t);
	}
	
	public void matchAndUpdateMatchers(MatchingTask t) throws Exception{
		t.match();
		t.select();
		MatchingTaskChangeEvent evt = new MatchingTaskChangeEvent(t, MatchingTaskChangeEvent.EventType.MATCHER_ALIGNMENTSET_UPDATED);
		fireEvent(evt);
		updateMatchers(t);
	}
	
	private void updateMatchers(MatchingTask t) throws Exception {
		//Chain update of all matchers after a
		throw new RuntimeException ("Implement me :D!!!!!!");
	}
	/**
	 * add or update the alignments selected by the user in all the matchers selected in the table
	 * @param alignments selected by the user
	 */
	public void performUserMatching(int index, ArrayList<Mapping> alignments) throws Exception {
		MatchingTask task = completedMatchingTasks.get(index);
		task.addManualAlignments(alignments);
		
		// fire a matcher change event.
		MatchingTaskChangeEvent evt = new MatchingTaskChangeEvent(task, MatchingTaskChangeEvent.EventType.MATCHER_ALIGNMENTSET_UPDATED );
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
	public synchronized void addOntology( Ontology ont ) {
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
			
			t.setName("OntologyChangeEvent " + t.getId());
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
			
			t.setName("MatchingTaskChangeEvent " + t.getId());
			t.start();
		}
	}
	
	/* ***************************** ONTOLOGY PROFILING **************************************** */
	private OntologyProfiler currentProfiler;

	/**
	 * If we're running an OSGi framework, keep track of the bundle context.
	 */
	private BundleContext bundleContext;
	
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

	public void initializeOSGiRegistry(BundleContext context) throws InstanceAlreadyExistsException {
		this.bundleContext = context;
		if( registry != null )
			throw new InstanceAlreadyExistsException("You've already instantiated an OSGiRegistry object.");
		
		registry = new OSGiRegistry(context);
	}
	
	public MatcherRegistry getRegistry() {
		return registry;
	}
	
	/**
	 * Gracefully shutdown AgreementMaker. This was introduced mainly to
	 * handle OSGi shutdowns.
	 */
	public void shutdown() {
		if( bundleContext != null ) {
			try {
				bundleContext.getBundle(0).stop();
				//EclipseStarter.shutdown();
				
				while( bundleContext.getBundles() != null ) {
					Thread.sleep(100);
				}
				
				
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.exit(0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public BundleContext getBundleContext() {
		return bundleContext;
	}
}
