package am.app.mappingEngine.abstractMatcherNew;

import java.awt.Color;

import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.userInterface.MatchingProgressDisplay;

/**
 * This class is intended to keep informations that do
 * not affect directly the matching process
 */
public class AbstractInfoParameters {
	
	/**
	 * Unique identifier of the algorithm used in the JTable list as index
	 * if an algorithm gets deleted we have to decrease the index of all others by one
	 */
	protected int index;
	
	/**
	 * Name of the algorithm, there should be also a final static String in the instance class
	 * in the constructor of the non-abstract class should happen "name = FINALNAME"
	 */
	protected MatchersRegistry name;
	
	/**
	 * User mapping should be the only one with this variable equal to false
	 */
	protected boolean isAutomatic;

	/**
	 * indicates whether the user modified the alignment
	 */
	protected boolean modifiedByUser;
	
	/**
	 * This is to indicate if the algorithm calculates class alignment
	 */
	protected boolean alignClass;
	/**
	 * This is to indicate if the algorithm calculates property alignment
	 */
	protected boolean alignProp;

	/**
	 * Variables needed to calculate execution time, executionTime = (end - start)/ unitMeasure</br>
	 * start has to be init in beforeAlign() and end in aferSelect()
	 * TODO: define beforeAlign and afterSelect
	 */
	protected long start;
	protected long end;
	protected long executionTime;
	
	/**
	 * Keeps info about reference evaluation of the matcher</br>
	 * is null until the algorithm gets evaluated
	 */
	protected transient ReferenceEvaluationData refEvaluation;
	/**
	 * Keeps info about the quality evaluation of the matcher</br>
	 * is null if the algorithm is not evaluated
	 */
	protected transient QualityEvaluationData qualEvaluation;
	/**
	 * Graphical color for nodes mapped by this matcher and alignments, this value is set
	 * by the MatcherFactory and modified by the table so a developer just have to pass it as a parameter
	 * for the constructor
	 */
	protected Color color;
	
	/** 
	 * Need to keep track of the dialog in order to close it when we're done.
	 * TODO: there could be a better way to do this, but that's for later
	 */
	protected transient MatchingProgressDisplay progressDisplay = null;
	/**
	 * Used by the ProgressDialog.</br>
	 * This is a rough estimate of the number of steps to be done before we finish the matching.
	 */
	protected long stepsTotal;
	/**
	 * Used by the ProgressDialog.</br>
	 * This is how many of the total steps we have completed.
	 */
	protected long stepsDone;
	/**
	 * Used by the ProgressDialog.</br>
	 * This is to inform the user of the progress/outcome of the matching process
	 */
	protected String report = "";
	
	/**
	 * Right now, matchers only produce equivalence relations
	 * If a matcher computes another type of relation, the relation has to be added
	 * to the Alignment class as a static variable
	 * and it has to be set as a default relation in the constructor of that matcher.
	 * at the moment a matcher is not allowed to compute different relations for different concepts
	 * in that case a matrix of relation has to be kept within the matcher 
	 * and the situation has to be managed properly
	 */
	protected String relation;
	
	/**
	 * Used by the updateProgress() method.
	 */
/*	private long starttime;
	private long lastTime = 0;
	private long lastStepsDone = 0;*/
}
