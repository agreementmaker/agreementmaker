/**
 * 
 */
package am.app.mappingEngine;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import am.app.Core;
import am.app.mappingEngine.LexicalSynonymMatcher.LexicalSynonymMatcher;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityMatcher;
import am.app.mappingEngine.multiWords.MultiWordsMatcher;
import am.app.mappingEngine.parametricStringMatcher.ParametricStringMatcher;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.TreeBuilder;

/**
 * @author Michele Caci
 * @version 0.1
 * This is the basic class testing the abstract matcher, for every new matcher that is created
 * by extending the AbstractMatcher just add a new JUnit class for your matcher tell it to extend
 * AbstractMatcherTest class and implement the setUpBeforeClass method like this:
 * <br/><br/>
 * <code>
 * 	  <pre>@BeforeClass</pre>
 *	  public void setUpBeforeClass() throws Exception {<br/>
 *		AbstractMatcherTest.setUpBeforeClass();<br/>
 *		testMatcher = new YourMatcherClass(); // YourMatcherClass is no real class so you have to put your own<br/>
 *	  }<br/>
 * </code>
 * <br/>
 * 
 * To test code that is not either managed by or overridden from the AbstractMatcher add your own test
 * methods.
 * 
 */
public abstract class AbstractMatcherTest {

	// the matcher to be tested
	protected static AbstractMatcher testMatcher;
	
	// input file that contains the parameters to test
	protected File inputList;
	
	// input parameters to save (useful?)
	protected static double sourceIndex = Math.random();
	protected static double targetIndex = Math.random();
	protected static boolean loaded = false;
	
	protected static String sourceOntologyFilename;
	protected static String targetOntologyFilename;
	
	protected static double thresholdIndex = Math.random();
	protected static double inputThreshold;
	
	protected static double cardinalityIndexS = Math.random();
	protected static double cardinalityIndexT = Math.random();
	protected static int inputSourceCardAlign;
	protected static int inputTargetCardAlign;
	
	protected static double inMatchersIndexMin = Math.random();
	protected static double inMatchersIndexMax = Math.random();
	protected static int inputMinInMatchers;
	protected static int inputMaxInMatchers;
	
	protected static boolean chosenNumber = false;
	protected static boolean inputMatchersNeeded = false;
	protected static int maxInputs = 0;
	protected static Vector<AbstractMatcher> inputInMatchers;
	
	Vector<Vector<String>> input;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		testMatcher = null;
	}

	/**
	 * @author michele
	 * @throws java.lang.Exception
	 * gets all the input from the CSF and randomly chooses a specific subset of the input to create a test case
	 * NOTE: matcher can be instantiated only by the appropriate matcher test class
	 */
	@Before
	public void setUp() throws Exception {
		
		//loading input file
		inputList = new File("/home/nikiforos/Desktop/inputTest"); // TODO: prompt file here
		input = processLineByLine(inputList);
		
		//saving input data from file (depending on the order of the data in the file)
		// TODO: make it independent from the order
		int numberOfOntologies = input.get(0).size();
		sourceOntologyFilename = input.get(0).get((int)(sourceIndex * numberOfOntologies));
		targetOntologyFilename = input.get(0).get((int)(targetIndex * numberOfOntologies));
		
		int possibleThresholds = input.get(1).size();
		inputThreshold = Double.valueOf(input.get(1).get((int)(thresholdIndex * possibleThresholds)));
		
		int sizeCardinalities = input.get(2).size();
		inputSourceCardAlign = Integer.valueOf(input.get(2).get((int)(cardinalityIndexS * sizeCardinalities)));
		inputTargetCardAlign = Integer.valueOf(input.get(2).get((int)(cardinalityIndexT * sizeCardinalities)));
		
		int sizeInMatchers = input.get(3).size();
		inputMinInMatchers = Math.min(
				Integer.valueOf(input.get(3).get((int)(inMatchersIndexMin * sizeInMatchers))),
				Integer.valueOf(input.get(3).get((int)(inMatchersIndexMin * sizeInMatchers)))
				);
		inputMaxInMatchers = Math.max(
				Integer.valueOf(input.get(3).get((int)(inMatchersIndexMin * sizeInMatchers))),
				Integer.valueOf(input.get(3).get((int)(inMatchersIndexMin * sizeInMatchers)))
				);
		
		if(!loaded){
			loadOntologies(); // load ontologies
			loadThresholdAndCardinalities(); // load threshold and cardinalities
			loaded = true;
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#match()}.
	 */
	@Test
	public void testMatch() {
		
		if(!chosenNumber && inputMatchersNeeded){
			inputInMatchers = new Vector<AbstractMatcher>();
			setInuptMatchers(input, inputMinInMatchers, inputMaxInMatchers); // load input matchers
			System.out.println(inputInMatchers.get(0));
			chosenNumber = true;
		}
		
		testMatcher.setSourceOntology(Core.getInstance().getSourceOntology());
		testMatcher.setTargetOntology(Core.getInstance().getTargetOntology());
		try {
			testMatcher.match();
			testSimilarityMatrices();
			testFinalAlignment();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getAlignment()}.
	 */
	@Test
	public final void testGetAlignmentSet() {
		assertNotNull("The alignment is not initialized", testMatcher.getAlignment());
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getClassAlignmentSet()}.
	 */
	@Test
	public final void testGetClassAlignmentSet() {
		assertNotNull("Class alignment is not initialized", testMatcher.getClassAlignmentSet());
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getPropertyAlignmentSet()}.
	 */
	@Test
	public final void testGetPropertyAlignmentSet() {
		assertNotNull("Property alignment is not initialized", testMatcher.getPropertyAlignmentSet());
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#areClassesAligned()}.
	 */
	@Test
	public final void testAreClassesAligned() {
		assertTrue("Classes are not aligned yet", testMatcher.areClassesAligned());
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#arePropertiesAligned()}.
	 */
	@Test
	public final void testArePropertiesAligned() {
		assertTrue("Properties are not aligned yet", testMatcher.arePropertiesAligned());
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#isSomethingAligned()}.
	 */
	@Test
	public final void testIsSomethingAligned() {
		assertTrue("Nothing is aligned yet", testMatcher.isSomethingAligned());
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getNumberClassAlignments()}.
	 */
	@Test
	public final void testGetNumberClassAlignments() {
		// just test for non negative values
		assertTrue("number of class alignments is lower than 0", testMatcher.getNumberClassAlignments() >= 0);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getNumberPropAlignments()}.
	 */
	@Test
	public final void testGetNumberPropAlignments() {
		assertTrue("number of property alignments is lower than 0", testMatcher.getNumberPropAlignments() >= 0);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getTotalNumberAlignments()}.
	 */
	@Test
	public final void testGetTotalNumberAlignments() {
		assertTrue("number of total alignments is lower than 0", testMatcher.getTotalNumberAlignments() >= 0);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getParam()}.
	 */
	@Test
	public final void testGetParam() {
		assertTrue("Matcher needs parameters but they are not instantiated yet", !testMatcher.needsParam() || testMatcher.getParam() != null);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getThreshold()}.
	 */
	@Test
	public final void testGetThreshold() {
		assertTrue("Threshold has to be in the [0.0, 1.0] range", (testMatcher.getThreshold() >= 0) && (testMatcher.getThreshold() <= 1));
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getMaxSourceAlign()}.
	 */
	@Test
	public final void testGetMaxSourceAlign() {
		assertTrue("Max cardinality for the source ontology is not greater or equal than 1", testMatcher.getMaxSourceAlign() >= 1);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getMaxTargetAlign()}.
	 */
	@Test
	public final void testGetMaxTargetAlign() {
		assertTrue("Max cardinality for the target ontology is not greater or equal than 1", testMatcher.getMaxTargetAlign() >= 1);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getMinInputMatchers()}.
	 */
	@Test
	public final void testGetMinInputMatchers() {
		assertTrue("Minimum input matchers number is not positve", testMatcher.getMinInputMatchers() >= 0);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getMaxInputMatchers()}.
	 */
	@Test
	public final void testGetMaxInputMatchers() {
		assertTrue("Maximum input matchers number is not positve", testMatcher.getMaxInputMatchers() >= 0);
		assertTrue("Maximum input matchers number is not greater than the minimum", testMatcher.getMaxInputMatchers() >= testMatcher.getMinInputMatchers());
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getClassesMatrix()}.
	 */
	@Test
	public final void testGetClassesMatrix() {
		assertNotNull("Classes Matrix is null", testMatcher.getClassesMatrix());
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getPropertiesMatrix()}.
	 */
	@Test
	public final void testGetPropertiesMatrix() {
		assertNotNull("Properties Matrix is null", testMatcher.getPropertiesMatrix());
	}
	
	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getDefaultThreshold()}.
	 */
	@Test
	public final void testGetDefaultThreshold() {
		assertTrue("DefaultThreshold is not 0.6", testMatcher.getDefaultThreshold() == 0.6);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getDefaultMaxSourceRelations()}.
	 */
	@Test
	public final void testGetDefaultMaxSourceRelations() {
		assertTrue("Default cardinality for source ontology is not 1", testMatcher.getDefaultMaxSourceRelations() == 1);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getDefaultMaxTargetRelations()}.
	 */
	@Test
	public final void testGetDefaultMaxTargetRelations() {
		assertTrue("Default cardinality for target ontology is not 1", testMatcher.getDefaultMaxTargetRelations() == 1);
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getSourceOntology()}.
	 */
	@Test
	public final void testGetSourceOntology() {
		assertNotNull("Source ontology is null", testMatcher.getSourceOntology());
	}

	/**
	 * Test method for {@link am.app.mappingEngine.AbstractMatcher#getTargetOntology()}.
	 */
	@Test
	public final void testGetTargetOntology() {
		assertNotNull("Target ontology is null", testMatcher.getTargetOntology());
	}

	/* *********************************************************************** */
	/*                        TESTING THE FINAL ALIGNMENT                      */
	/* *********************************************************************** */
	
	/**
	 * @author michele
	 */
	private void testFinalAlignment(){
		assertNotNull("Resulting Aligmnent is null and not usable", testMatcher.getAlignment());
		testClassesAlignmentSubset();
		testPropertyAlignmentSubset();
	}
	
	/**
	 * @author michele
	 */
	private void testClassesAlignmentSubset(){
		Alignment<Mapping> cs = testMatcher.getClassAlignmentSet();
		runAlignmentTest(cs);
	}
	
	/**
	 * @author michele
	 */
	private void testPropertyAlignmentSubset(){
		Alignment<Mapping> ps = testMatcher.getPropertyAlignmentSet();
		runAlignmentTest(ps);
	}
	
	/**
	 * @author michele
	 */
	private void runAlignmentTest(Alignment<Mapping> set){
		Alignment<Mapping> local = set;
		Iterator<Mapping> localSetIterator = local.iterator();
		String errorMessage;
		Mapping current;
		while(localSetIterator.hasNext()){
			current = localSetIterator.next();
			errorMessage = "Mapping: " + current.toString() + " is not instantiated\n";
			assertNotNull(errorMessage, current);
			errorMessage = "Similarity value for " + current.toString() + " not between [0,1] interval\n";
			errorMessage += "Value = " + current.getSimilarity();
			assertTrue(errorMessage, ((current.getSimilarity()) >= 0) && (current.getSimilarity() <= 1));
		}
	}
	
	/* *********************************************************************** */
	/*                      TESTING THE SIMILARITY MATRICES                    */
	/* *********************************************************************** */
	
	/**
	 * @author michele
	 */	
	private void testSimilarityMatrices(){
		testClassesSimilarityMatrix();
		testPropertySimilarityMatrix();
	}
	
	/**
	 * @author michele
	 */
	private void testClassesSimilarityMatrix(){
		SimilarityMatrix cm = testMatcher.classesMatrix;
		runMatrixTest(cm);
	}
	
	/**
	 * @author michele
	 */
	private void testPropertySimilarityMatrix(){
		SimilarityMatrix pm = testMatcher.propertiesMatrix;
		runMatrixTest(pm);
	}
	
	/**
	 * @author michele
	 */
	private void runMatrixTest(SimilarityMatrix matrix){
		SimilarityMatrix local = matrix;
		String errorMessage;
		for(int i = 0; i < local.getRows(); i++){
			for(int j = 0; j < local.getColumns(); j++){
				errorMessage = "Value for " + i + " - " + j + " not between [0,1] interval\n";
				errorMessage += "Value = " + local.getSimilarity(i, j);
				assertTrue(errorMessage, ((local.get(i, j).getSimilarity() >= 0) && (local.get(i, j).getSimilarity() <= 1)));
			}
		}
	}
	
	/* *************************************************** */
	/*     SUPPORT METHODS FOR LOADING INPUT MATCHERS      */
	/* *************************************************** */
	
	private void setInuptMatchers(Vector<Vector<String>> input, int minIn, int maxIn){
		int newInMatcher;
		//int matchersNumber = (int)(Math.random()*(maxIn - minIn)) + minIn;
		//TODO: modify the test if matchers are modified
		for(int i = 0; i < maxInputs; i++){
			newInMatcher = (int)(Math.random() * input.get(4).size());
			if(input.get(4).get(newInMatcher).equals("BSM")){
				inputInMatchers.add(new BaseSimilarityMatcher());
			}
			else if(input.get(4).get(newInMatcher).equals("ASM")){
				inputInMatchers.add(new AdvancedSimilarityMatcher());
			}
			else if(input.get(4).get(newInMatcher).equals("PSM")){
				inputInMatchers.add(new ParametricStringMatcher());
			}
			else if(input.get(4).get(newInMatcher).equals("VMM")){
				inputInMatchers.add(new MultiWordsMatcher());
			}
			else if(input.get(4).get(newInMatcher).equals("LSM")){
				inputInMatchers.add(new LexicalSynonymMatcher());
			}
			else{
				fail("no matchers selected");
			}
		}
		for(int i = 0; i < maxInputs; i++){
			try {
				inputInMatchers.get(i).match();
			} catch (Exception e) {
				e.printStackTrace();
			}
			testMatcher.addInputMatcher(inputInMatchers.get(i));
		}
		
	}
	
	/* *************************************************** */
	/*          SUPPORT METHODS FOR LOADING DATA           */
	/* *************************************************** */
	
	private void loadOntologies(){
		// source ontology
		TreeBuilder tb = TreeBuilder.buildTreeBuilder(sourceOntologyFilename, 0, 1, 0, false, true);
		tb.build();		
		Ontology s = tb.getOntology();
		Core.getInstance().setSourceOntology(s);
		
		// target ontology
		tb = TreeBuilder.buildTreeBuilder(targetOntologyFilename, 1, 1, 0, false, true);
		tb.build();
		Ontology t = tb.getOntology();
		Core.getInstance().setTargetOntology(t);
	}
	
	private void loadThresholdAndCardinalities(){
		testMatcher.setThreshold(inputThreshold);
		testMatcher.setMaxSourceAlign(inputSourceCardAlign);
		testMatcher.setMaxTargetAlign(inputTargetCardAlign);
	}
	
	/* *************************************************** */
	/*          SUPPORT METHODS FOR READING FILE           */
	/* *************************************************** */
	
	/** File reader method  */
	private final Vector<Vector<String>> processLineByLine(File inputFile) throws FileNotFoundException {
	    //Note that FileReader is used, not File, since File is not Closeable
	    Scanner scanner = new Scanner(new FileReader(inputFile));
	    Vector<Vector<String>> outcomes = new Vector<Vector<String>>();
	    try {
	      //first use a Scanner to get each line
	      while ( scanner.hasNextLine() ){
	        outcomes.add(processLine( scanner.nextLine() ));
	      }
	    }
	    finally {
	      //ensure the underlying stream is always closed this only has any effect if the item 
	      //passed to the Scanner constructor implements Closeable (which it does in this case).
	      scanner.close();
	    }
	    return outcomes;
	  }

	 /** 
	  * Lines reader method.
	  * Method for processing lines of a (comma-separated) file.
	  * 
	  * This simple default implementation expects simple name-value pairs, separated by commas <br/> 
	  * Examples of expected content of the file: <tt>"C,F,P"</tt>
	  */
	  private Vector<String> processLine(String line){
		  //use a second Scanner to parse the content of each line 
		  Scanner scanner = new Scanner(line);
		  scanner.useDelimiter(",");
		  Vector<String> outcomes = new Vector<String>();
		  String choice;
		  while(scanner.hasNext()){
			  choice = scanner.next();
			  outcomes.add(choice);
		  }
		  //no need to call scanner.close(), since the source is a String
		  return outcomes;
	  }

}
