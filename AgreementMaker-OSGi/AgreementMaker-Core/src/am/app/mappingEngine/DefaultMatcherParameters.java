package am.app.mappingEngine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.threaded.AbstractMatcherRunner;

import com.hp.hpl.jena.ontology.OntModel;

/**
 * This class contains all the common matcher parameters:
 * <ul>
 * <li>The {@link #ontologies} to be matched.</li>
 * <li>The {@link #threshold} and cardinality ({@link maxSourceAlign},
 * {@link maxTargetAlign}).</li>
 * <li>Parameters to control threaded execution of the matching algorithm (
 * {@link #threadedExecution}, {@link #threadedOverlap},
 * {@link #threadedExecution}).
 * <li>Other experimental parameters.</li>
 * </ul>
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 * 
 */
public class DefaultMatcherParameters implements Serializable {

	private static final long serialVersionUID = -5278587933670353897L;
	
	/**
	 * <p>
	 * If this list contains two ontologies, the first ontology is taken to be
	 * the source ontology and the second ontology is taken to be the target
	 * ontology. If the list contains more than two ontologies, then it is
	 * assumed that this is a multi-ontology matching task.
	 * </p>
	 * 
	 * <p>
	 * NOTE: We have moded to using the Jena datastructures directly in our
	 * code, instead of hiding them behind the Ontology class.
	 * </p>
	 */
	public OntModel[] ontologies;
	
	// fields are initialized to their defaults.
	public double threshold = 0.6;
	public int maxSourceAlign = 1;
	public int maxTargetAlign = 1;
	
	/**
	 * <p>
	 * Enable "completion mode" which tries to avoid matching concepts that have
	 * been previously matched by an input matcher. The logic for the completion
	 * mode is implemented in {@link AbstractMatcher}.
	 * </p>
	 * 
	 * <p>
	 * <b>Currently, completion mode does not work with threaded execution
	 * mode.</b>
	 * </p>
	 */
	public boolean completionMode = false;
	
	/**
	 * Enable the storing of provenance for each mapping created. The individual
	 * matching algorithm is currently responsible for creating the provenance,
	 * although in the future a uniform way of representing provenance should be
	 * introduced in AgreementMaker.
	 */
	public boolean storeProvenance = false; // whether the matcher stores provenance information for mappings.
	
	/**
	 * Enable certain optimizations to deal with large ontologies. This feature
	 * is still a work in progress.
	 */
	public boolean largeOntologyMode = false;//if true values in the sparse matrix are thrown away to save memory
	
	/**
	 * <p>
	 * Enable multi-threaded execution of the matching algorithm. The logic for
	 * this is implemented in AbstractMatcher and currently only works for
	 * matching algorithms whose {@link AbstractMatcher#alignTwoNodes} method is
	 * <b>stateless</b>.
	 * </p>
	 * <p>
	 * If there are n cores available, the matching space is divided into
	 * n<sup>2</sup> pieces by dividing the source and target concept lists into
	 * n pieces each. The matching then proceeds in n stages, and in each stage
	 * n pieces are matched (each piece is handled by a single thread,
	 * {@link AbstractMatcherRunner}).
	 * </p>
	 * <p>
	 * If you would like to apply threaded execution for stateful
	 * implementations of alignTwoNodes, you are responsible for avoiding
	 * corruption by controlling access to shared fields of your matcher.
	 * </p>
	 */
	public boolean threadedExecution = false;
	
	/**
	 * <p>
	 * Threaded overlap mode allows efficient use of multiple cores by allowing
	 * multiple threads to compute the similarity between the same source or
	 * target concept.
	 * </p>
	 * <p>
	 * For example, assume thread t<sub>1</sub> is computing the similarity
	 * between source concept sc<sub>1</sub> and target concept tc<sub>1</sub>,
	 * written as t<sub>1</sub>(sc<sub>1</sub>,tc<sub>1</sub>). With threaded
	 * overlap mode <b>disabled</b>, no other thread is allowed to attempt to
	 * compute the similarity between concept sc<sub>1</sub> and any other
	 * target concept or between any other source concept and concept
	 * tc<sub>1</sub>. With threaded overlap mode enabled, no such restriction
	 * is assumed which leads to no idle time on cores but the matching
	 * algorithm must be able to handle the overlapping situations.
	 * </p>
	 * <p>
	 * Another way to think about it is that with threaded overlap mode enabled,
	 * the "stages" of matching (see the implementation code in AbstractMatcher
	 * to know what a stage is) are allowed to overlap. Without overlap mode,
	 * all the cores must wait for the slowest core to finish before continuing
	 * to the next stage of the threaded execution.
	 * </p>
	 * <p>
	 * For safety, threaded overlap is defaulted to false. If you know that
	 * overlapping won't cause problems with your algorithm, you may enable this
	 * mode to get more efficient usage of the CPU cores.
	 */
	public boolean threadedOverlap = false;
	
	/**
	 * In threaded mode, you can tell the execution to reserve (i.e., not use) a
	 * certain number of cores. For example, if you have 8 cores available and
	 * reserve 2, the threaded execution will only use up to 6 cores and leave 2
	 * free. You can set this parameter arbitrarily high, but if you exceed the
	 * number of cores, the execution will default to a single core execution.
	 * By default, no cores are reserved and the entire CPU is used for threaded
	 * execution.
	 */
	public int threadedReservedProcessors = 0; // how many processors are reserved (i.e. not used by the threaded mode)
	
	/**
	 * <p>
	 * If the matching algorithm is a refining matcher (i.e., it takes into
	 * account the results of previous matchers), this list contains the results
	 * of the previous matchers.
	 * </p>
	 * <p>
	 * An example of a refining matcher is the Linear Weighted Combination(LWC)
	 * matcher which combines the results of multiple matching algorithms into a
	 * single result.
	 * </p>
	 */
	public List<MatchingTask> inputResults = new ArrayList<MatchingTask>();
	
	
	public DefaultMatcherParameters() { /* work is done by the field initialization; */ }
	
	public DefaultMatcherParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign) {
		this.threshold = threshold;
		this.maxSourceAlign = maxSourceAlign;
		this.maxTargetAlign = maxTargetAlign;
	}

	/**
	 * Helper function to set the ontologies to be matched.
	 * 
	 * @param ontologies
	 *            If setting a source and target ontology, the source ontology
	 *            should be the first argument followed by the target ontology.
	 *            If more than two ontologies, we assume a multi-ontology
	 *            matching task.
	 */
	public void setOntologies(OntModel ... ontologies) {
		this.ontologies = ontologies;
	}
	
	public OntModel getSourceOntology() { return ontologies[0]; }
	public OntModel getTargetOntology() { return ontologies[1]; }
}
