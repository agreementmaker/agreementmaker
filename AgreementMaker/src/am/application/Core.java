package am.application;

import java.util.ArrayList;
import java.util.Iterator;


import am.AMException;
import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.Alignment;
import am.application.ontology.Node;
import am.application.ontology.Ontology;
import am.userInterface.UI;

/**
 * SINGLETON JAVA PATTERN
 * There will be only one instance of this class in the whole application
 * All classes can access it without having any reference to it, just invoking the static method
 * Core.getIstance()
 * All model information of the system will be accessible from this class:
 * ontologies, matchings and so on.
 *
 */
public class Core {
	/**List of matchers instances run by the user
	 * Data of the tableModel of the matcherTable is taken from this structure
	 * Can't be null;
	 */
	private ArrayList<AbstractMatcher> matcherInstances = new ArrayList<AbstractMatcher>();
	
	private Ontology sourceOntology;//Null if it's not loaded yet
	private Ontology targetOntology;//Null if it's not loaded yet
	
	/**A reference to the userinterface instance, canvas and table can be accessed anytime. It used often to invoke the method redisplayCanvas()*/
	private UI ui;
	
	/**
	 * Singleton pattern: unique instance
	 */
	private static Core core  = new Core();
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
	}
	
	public Ontology getSourceOntology() {
		return sourceOntology;
	}
	public void setSourceOntology(Ontology sourceOntology) {
		this.sourceOntology = sourceOntology;
	}
	public Ontology getTargetOntology() {
		return targetOntology;
	}
	public void setTargetOntology(Ontology targetOntology) {
		this.targetOntology = targetOntology;
	}
	
	public boolean sourceIsLoaded() {
		return sourceOntology != null;
	}
	
	public boolean targetIsLoaded() {
		return targetOntology != null;
	}
	
	public boolean ontologiesLoaded() {
		return sourceIsLoaded() &&  targetIsLoaded();
	}
	public ArrayList<AbstractMatcher> getMatcherInstances() {
		return matcherInstances;
	}
	
	public void addMatcherInstance(AbstractMatcher a) {
		matcherInstances.add(a);
		
	}
	
	public void removeMatcher(AbstractMatcher a) {
		int myIndex = a.getIndex();
		matcherInstances.remove(myIndex);
		//All indexes must be decreased by one;
		//For this reason whenever you have to delete more then one matcher, it's good to start from the last in the order
		AbstractMatcher next;
		for(int i = myIndex; i<matcherInstances.size(); i++) {
			next = matcherInstances.get(i);
			next.setIndex(i);
		}
	}
	public UI getUI() {
		return ui;
	}
	public void setUI(UI ui) {
		this.ui = ui;
	}
	
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
		updateMatchers(a);

	}
	
	public void matchAndUpdateMatchers(AbstractMatcher a) throws Exception{
		a.match();
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
	public void performUserMatching(int index, ArrayList<Alignment> alignments) {
		AbstractMatcher matcher = matcherInstances.get(index);
		matcher.addManualAlignments(alignments);
	}
	
	
	
	
	
	public Node getNode(String localname, boolean fromSource, boolean fromClasses) {
		Ontology o = sourceOntology;
		Node result = null;;
		if(!fromSource) {
			o = targetOntology;
		}
		ArrayList<Node> list = o.getClassesList();
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
	
	
}
