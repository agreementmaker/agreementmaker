package am.app.ontology.profiling;

import java.util.Iterator;
import java.util.List;

import am.app.ontology.Node;
import am.utility.Pair;


/**
 * This interface represents an ontology profiling algorithm.
 * 
 * @author Cosmin Stroe @date January 25, 2011
 * 
 */
public interface OntologyProfiler {

	public enum ParamType {
		
		/**
		 * These are parameters that are required to create the OntologyProfiler object.
		 */
		INITIAL_PARAMETERS,
		
		/**
		 * These are parameters that are required after the ontologies have been profiled, but before
		 * the ontology profiler can provide values for {@link #getAnnotationIterator(Node,Node)}.
		 */
		MATCHING_PARAMETERS;
	}
	
	/**
	 * Determine whether this ontology profiling method needs parameters.
	 * There can be two kinds of parameters.  Initial parameters and matching parameters.
	 * Initial parameters are required when the ontology profiling algorithm is first initialized.
	 * Matching parameters are required before running matchers that use ontology profiling.
	 * 
	 * If this method returns true, calling the getProfilerPanel(initial) method should return the panel.
	 * 
	 * @param initial If true, check if the algorithm needs <b>initial</b> parameters.  If false, check if the algorithm needs <b>matching</b> parameters.
	 * @return
	 */
	public boolean needsParams(ParamType type);

	/**
	 * Get the ontology profiling panel.  There can be two panels.
	 * 
	 * Initial panel - is displayed once two ontologies are loaded.
	 * Matching panel - is displayed in the matching dialog.
	 * 
	 * @param initial If true, return the initial panel.  Otherwise return the matching panel.
	 * @return One of two panels: the initial ontology profiling panel, and the matching profiling panel.
	 */
	public OntologyProfilerPanel getProfilerPanel(ParamType type);

	/**
	 * Return an iterator over all the possible combinations of annotations for the two nodes.
	 * @param n1
	 * @param n2
	 * @return
	 */
	public Iterator<Pair<String,String>> getAnnotationIterator(Node n1, Node n2);
	
	public List<String> getAnnotations( Node node );
	
	public void setParams(ParamType type, OntologyProfilerParameters param);
	
	public void setName( ProfilerRegistry name ); // used by the ontology profiler factory
	public ProfilerRegistry getName();
	
	/** @return The configuration of the matchers computed by the ontology profiling algorithm. */
	public MatcherStack getMatcherStack();
}
