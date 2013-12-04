package am.tools.ThresholdAnalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.mappingEngine.referenceAlignment.ThresholdAnalysisData;
import am.app.mappingEngine.utility.MatchingPair;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.app.osgi.MatcherNotFoundException;
import am.utility.referenceAlignment.AlignmentUtilities;
/**
 * 
 * 
 * Batch File example:
 * 

<?xml version="1.0"?>
<batchmode title="benchmarks">

	<parameters>
		<cardinality source="1" target="1" />
		<threshold start="0.60" increment="0.01" end="1.0" />
	</parameters>

	<run>
		<sourceOntology filename="/home/cosmin/Desktop/benchmarks/101/onto.rdf" name="101" />
		<targetOntology filename="/home/cosmin/Desktop/benchmarks/101/onto.rdf" name="101" />
		<referenceAlignment filename="/home/cosmin/Desktop/benchmarks/101/refalign.rdf" />
	</run>
		
</batchmode>

 * 
 * 
 * @author cosmin
 * TODO: REMOVE ALL THE ROUNDING IN THE SINGLE AND BATCH RUNNING METHODS. !!!!!
 * TODO: COMBINE DUPLICATED CODE BETWEEN SINGLE AND BATCH MODE (if it can be combined).
 */

public class ThresholdAnalysis extends SwingWorker<Void,Void> {

	private static Logger LOG = Logger.getLogger(ThresholdAnalysis.class); // logger
	
	
	private boolean prefBatchMode = false; // are we running a batch mode?
	private String prefBatchFile = ""; // if running in batch mode, we need to have a batch file
	
	private AbstractMatcher matcherToAnalyze = null; // which matcher are we analyzing
	
	private String outputDirectory = ""; // the directory we are outputting to
	private String outputPrefix = ""; // the prefix for the output files
	
	// matcher parameters
	private DefaultMatcherParameters prefParams = null;
	private int prefSourceCardinality = 1;
	private int prefTargetCardinality = 1;
	private double prefStartThreshold = 0.2d;
	private double prefThresholdIncrement = 0.01d;
	private double prefEndThreshold = 1.0d;
	private boolean prefFilenameOntologyNames = true;
	
	public void setStartTh( float startTh ) { prefStartThreshold = startTh; }
	public void setEndTh( float endTh ) { prefEndThreshold = endTh; }
	public void setThIncrement( float incTh ) { prefThresholdIncrement = incTh; }
	
	// single run params
	private String singleRunReferenceAlignment = "";
	
	/**
	 * This is the batchMode constructor
	 * @param matcher
	 * @throws MatcherNotFoundException 
	 */
	public ThresholdAnalysis( AbstractMatcher matcher, boolean batchMode, DefaultMatcherParameters params) throws MatcherNotFoundException {
		super();
		matcherToAnalyze = MatcherFactory.getMatcherInstance(matcher.getClass());;
		prefBatchMode = batchMode;
		this.prefParams = params;
	}
	
	/**
	 * This is the single matcher mode constructor
	 * @param matcher
	 */
	public ThresholdAnalysis( AbstractMatcher matcher ) {
		super();
		matcherToAnalyze = matcher;
		prefBatchMode = false;
	}
	
	
	public void setBatchFile( String filename ) {
		prefBatchFile = filename;
		prefBatchMode = true;
	}
	
	public void setOutputDirectory(String dir ) { outputDirectory = dir; }
	
	
	
	public void runAnalysis() {
		if( prefBatchMode ) { runBatchAnalysis(); }
		else { runSingleAnalysis(); }
	}
	
	/**
	 * This is a single matcher mode.  The matcher should have been already executed. 
	 */
	private void runSingleAnalysis() {

		
		// load the reference file
		ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
		refParam.onlyEquivalence = true;
		refParam.fileName = singleRunReferenceAlignment;
		refParam.format = ReferenceAlignmentMatcher.OAEI;
		AbstractMatcher referenceAlignmentMatcher;
		try {
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
		} catch (MatcherNotFoundException e1) {
			LOG.error("Analysis aborted.", e1);
			return;
		}
		referenceAlignmentMatcher.setParam(refParam);
		try {
			referenceAlignmentMatcher.match();
		} catch (Exception e) {
			LOG.error("Analysis aborted.",e);
			return;
		}
		
		String sourceOntologyName = Core.getInstance().getSourceOntology().getTitle();
		String targetOntologyName = Core.getInstance().getTargetOntology().getTitle();

		File outputPrecision, outputRecall, outputFMeasure, outputMaxFM;
		// open the output files
		if( prefFilenameOntologyNames ) {
			outputPrecision = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-precision.txt");
			outputRecall = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-recall.txt");
			outputFMeasure = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-fmeasure.txt");
			outputMaxFM = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-max-fmeasure.txt");
		} else {
			outputPrecision = new File( outputDirectory + "/" + outputPrefix + "-" + "-precision.txt");
			outputRecall = new File( outputDirectory + "/" + outputPrefix + "-" + "-recall.txt");
			outputFMeasure = new File( outputDirectory + "/" + outputPrefix + "-" + "-fmeasure.txt");
			outputMaxFM = new File( outputDirectory + "/" + outputPrefix + "-" + "-max-fmeasure.txt");			
		}
		
		
		try {
			
			BufferedWriter writerPrecision = new BufferedWriter( new FileWriter(outputPrecision) );
			BufferedWriter writerRecall = new BufferedWriter( new FileWriter(outputRecall) );
			BufferedWriter writerFMeasure = new BufferedWriter( new FileWriter(outputFMeasure) );
			BufferedWriter writerMaxFM = new BufferedWriter( new FileWriter(outputMaxFM) );
			
			
			// ok, we ran the matcher, now do the threshold analysis
			
			double maxFMeasure = 0.0;
			double maxFMTh = 0.0;
			
			for( double currentThreshold = prefStartThreshold; currentThreshold < prefEndThreshold; currentThreshold += prefThresholdIncrement) {

				currentThreshold = Utility.roundDouble(currentThreshold, 4);
				LOG.info("Selecting with threshold = " + currentThreshold );
				matcherToAnalyze.getParam().threshold = currentThreshold;
				matcherToAnalyze.select();
							
				ReferenceEvaluationData currentEvaluation = ReferenceEvaluator.compare(matcherToAnalyze.getAlignment(), referenceAlignmentMatcher.getAlignment());
				
				double th = Utility.roundDouble(currentThreshold*100f, 4);
				writerPrecision.write(th + "," + Utility.roundDouble( currentEvaluation.getPrecision() * 100.0d, 2) + "\n");
				writerRecall.write(th + "," + Utility.roundDouble( currentEvaluation.getRecall() * 100.0d, 2) + "\n");
				writerFMeasure.write(th + "," + Utility.roundDouble( currentEvaluation.getFmeasure()* 100.0d, 2) + "\n");
				LOG.info("Results: (precision, recall, f-measure) = (" + 
						Utility.roundDouble( currentEvaluation.getPrecision() * 100.0d, 2) + ", " + 
						Utility.roundDouble( currentEvaluation.getRecall() * 100.0d, 2) + ", " +
						Utility.roundDouble( currentEvaluation.getFmeasure()* 100.0d, 2) + ")");
				LOG.info("       : (found mappings, correct mappings, reference mappings) = (" + 
							currentEvaluation.getFound() + ", " + currentEvaluation.getCorrect() + ", " + currentEvaluation.getExist() + ")");
				
				
				writerPrecision.flush();
				writerRecall.flush();
				writerFMeasure.flush();
				
				if( maxFMeasure < currentEvaluation.getFmeasure() ) {
					maxFMeasure = currentEvaluation.getFmeasure();
					maxFMTh = Utility.roundDouble(currentThreshold*100f, 4);
				}
				
			}
			
			writerMaxFM.write( maxFMTh + ", " + Utility.roundDouble( maxFMeasure * 100.0d, 2) );
			
			writerPrecision.close();
			writerRecall.close();
			writerFMeasure.close();
			writerMaxFM.close();
			
		} catch (IOException e) {
			// cannot create files
			e.printStackTrace();
			return;
		}
		
	}

	/**
	 * This is batch mode analysis using an XML file for the settings
	 */
	private void runBatchAnalysis() {
		
		// open and parse the benchmark XML file
		try {
			
			LOG.info("Reading batch file.");
			File batchFile = new File( prefBatchFile );
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(batchFile);
			doc.getDocumentElement().normalize();
			LOG.debug("Root element " + doc.getDocumentElement().getNodeName());
			
			outputPrefix = doc.getDocumentElement().getAttribute("title");
			
			// parse the parameters
			Element parameters = (Element) doc.getElementsByTagName("parameters").item(0);
			
			Element cardinality = (Element) parameters.getElementsByTagName("cardinality").item(0);
			String cardinalitySource = cardinality.getAttribute("source");
			if( cardinalitySource.equalsIgnoreCase("any") ) { prefSourceCardinality = AbstractMatcher.ANY_INT; }
			else { prefSourceCardinality = Integer.parseInt(cardinalitySource); }
			String cardinalityTarget = cardinality.getAttribute("target");
			if( cardinalityTarget.equalsIgnoreCase("any") ) { prefTargetCardinality = AbstractMatcher.ANY_INT; }
			else { prefTargetCardinality = Integer.parseInt(cardinalityTarget); }
			
			Element threshold = (Element) parameters.getElementsByTagName("threshold").item(0);
			String thStart = threshold.getAttribute("start");
			prefStartThreshold = Float.parseFloat(thStart);
			String thInc = threshold.getAttribute("increment");
			prefThresholdIncrement = Float.parseFloat(thInc);
			String thEnd = threshold.getAttribute("end");
			prefEndThreshold = Float.parseFloat(thEnd);
			
			// parse the Runs
			NodeList runList = doc.getElementsByTagName("run");
			for( int i = 0; i < runList.getLength(); i++ ) {
				
				Element currentRun = (Element) runList.item(i);
				String runName = currentRun.getAttribute("name");
				
				Element sourceOntology = (Element) currentRun.getElementsByTagName("sourceOntology").item(0);
				
				String sourceOntologyFile = sourceOntology.getAttribute("filename");
				String sourceOntologyName = sourceOntology.getAttribute("name");
				
				
				Element targetOntology = (Element) currentRun.getElementsByTagName("targetOntology").item(0);
				
				String targetOntologyFile = targetOntology.getAttribute("filename");
				String targetOntologyName = targetOntology.getAttribute("name");
				
				
				Element referenceAlignment = (Element) currentRun.getElementsByTagName("referenceAlignment").item(0);
				
				String referenceAlignmentFile = referenceAlignment.getAttribute("filename");
				
				LOG.info("Running analysis for " + sourceOntologyName + " to " + targetOntologyName);
				
				//runAnalysis( sourceOntologyFile, sourceOntologyName, targetOntologyFile, targetOntologyName, referenceAlignmentFile );
				runAnalysis( sourceOntologyFile, runName, targetOntologyFile, "", referenceAlignmentFile );
				
			}
			
			
		} catch(Exception e) {
			LOG.error("Analysis aborted.",e);
		}
		
		
	}
	
	
	private void runAnalysis(String sourceOntologyFile,
			String sourceOntologyName, String targetOntologyFile,
			String targetOntologyName, String referenceAlignmentFile) {

		
		LOG.info("Loading ontology " +  sourceOntologyFile);
		// load source ontology
		Ontology sourceOntology = null;
		try {
			OntologyDefinition sourceOntDef = new OntologyDefinition(true, sourceOntologyFile, OntologyLanguage.OWL, OntologySyntax.RDFXML);
			OntoTreeBuilder sourceBuilder = new OntoTreeBuilder(sourceOntDef);
			sourceBuilder.build();
			
			sourceOntology = sourceBuilder.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		LOG.info("Loading ontology " +  targetOntologyFile);
		// load target ontology
		Ontology targetOntology = null;
		try {
			OntologyDefinition targetOntDef = new OntologyDefinition(true, targetOntologyFile, OntologyLanguage.OWL, OntologySyntax.RDFXML);
			OntoTreeBuilder targetBuilder = new OntoTreeBuilder(targetOntDef);
			targetBuilder.build();
			
			targetOntology = targetBuilder.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
		}
				

		// set the settings for the matcher			
		matcherToAnalyze.setSourceOntology(sourceOntology);
		matcherToAnalyze.setTargetOntology(targetOntology);
		matcherToAnalyze.setPerformSelection(false);
		if( matcherToAnalyze.needsParam() ) matcherToAnalyze.setParam(prefParams);
		matcherToAnalyze.setMaxSourceAlign(prefSourceCardinality);
		matcherToAnalyze.setMaxTargetAlign(prefTargetCardinality);

			
		try {
			matcherToAnalyze.match();
		} catch (Exception e) {
			LOG.error("Analysis aborted.", e);
			return;
		}
		
		// load the reference file
		ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
		refParam.onlyEquivalence = true;
		refParam.fileName = referenceAlignmentFile;
		refParam.format = ReferenceAlignmentMatcher.OAEI;
		AbstractMatcher referenceAlignmentMatcher;
		try {
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
		} catch (MatcherNotFoundException e1) {
			LOG.error("Analysis aborted.", e1);
			return;
		}
		referenceAlignmentMatcher.setParam(refParam);
		referenceAlignmentMatcher.setSourceOntology(sourceOntology);
		referenceAlignmentMatcher.setTargetOntology(targetOntology);
		
		try {
			referenceAlignmentMatcher.match();
		} catch (Exception e) {
			LOG.error("Analysis aborted.",e);
			return;
		}
		
		// open the output files
		File outputPrecision = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-precision.txt");
		File outputRecall = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-recall.txt");
		File outputFMeasure = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-fmeasure.txt");
		File outputMaxFM = new File( outputDirectory + "/" + outputPrefix + "-" + sourceOntologyName + "-" + targetOntologyName + "-max-fmeasure.txt");
		
		
		try {
			
			BufferedWriter writerPrecision = new BufferedWriter( new FileWriter(outputPrecision) );
			BufferedWriter writerRecall = new BufferedWriter( new FileWriter(outputRecall) );
			BufferedWriter writerFMeasure = new BufferedWriter( new FileWriter(outputFMeasure) );
			BufferedWriter writerMaxFM = new BufferedWriter( new FileWriter(outputMaxFM) );
			
			
			// ok, we ran the matcher, now do the threshold analysis
			
			double maxFMeasure = 0.0;
			double maxFMTh = 0.0;
			
			for( double currentThreshold = prefStartThreshold; currentThreshold < prefEndThreshold; currentThreshold += prefThresholdIncrement) {
				
				currentThreshold = Utility.roundDouble(currentThreshold, 4);
				
				LOG.info("Selecting with threshold = " + currentThreshold );
				matcherToAnalyze.getParam().threshold = currentThreshold;
				matcherToAnalyze.select();
							
				ReferenceEvaluationData currentEvaluation = ReferenceEvaluator.compare(matcherToAnalyze.getAlignment(), referenceAlignmentMatcher.getAlignment());
				
				writerPrecision.write(currentThreshold + "," + Utility.roundDouble( currentEvaluation.getPrecision(), 2) + "\n");
				writerRecall.write(currentThreshold + "," + Utility.roundDouble( currentEvaluation.getRecall(), 2) + "\n");
				writerFMeasure.write(currentThreshold + "," + Utility.roundDouble( currentEvaluation.getFmeasure(), 2) + "\n");
				
			 
				LOG.info("Results: (precision, recall, f-measure) = (" + 
					Utility.roundDouble( currentEvaluation.getPrecision() * 100.0d, 2) + ", " + 
					Utility.roundDouble( currentEvaluation.getRecall() * 100.0d, 2) + ", " +
					Utility.roundDouble( currentEvaluation.getFmeasure()* 100.0d, 2) + ")");
				LOG.info("       : (found mappings, correct mappings, reference mappings) = (" + 
							currentEvaluation.getFound() + ", " + currentEvaluation.getCorrect() + ", " + currentEvaluation.getExist() + ")");
				
				if( maxFMeasure < currentEvaluation.getFmeasure() ) {
					maxFMeasure = currentEvaluation.getFmeasure();
					maxFMTh = Utility.roundDouble(currentThreshold*100f, 4);
				}
				
			}
			
			writerMaxFM.write( Utility.roundDouble( maxFMTh, 2) + ", " + Utility.roundDouble( maxFMeasure, 2) );
			
			writerPrecision.close();
			writerRecall.close();
			writerFMeasure.close();
			writerMaxFM.close();
			
		} catch (IOException e) {
			// cannot create files
			e.printStackTrace();
			return;
		}
		

		
		// analysis done
		
		
	}


	@Override
	protected Void doInBackground() throws Exception {
		runAnalysis();
		return null;
	}
	public void setOutputPrefix(String matcherClass) {
		outputPrefix = matcherClass;
	}
	public void setReferenceAlignment(String referenceAlignment) {
		singleRunReferenceAlignment = referenceAlignment;
		
	}

	public void setOntologyNames(boolean names ) { prefFilenameOntologyNames = names;}
	
	
	public static ThresholdAnalysisData thresholdAnalysis(AbstractMatcher toBeEvaluated, Object reference) {
		return thresholdAnalysis(toBeEvaluated, reference, null, false);
	}
	
	/**
	 * TODO: Check to see if this method could be merged with the work done in
	 * the {@link ThresholdAnalysis#runAnalysis()} method.
	 * 
	 * @param reference
	 *            it can be either an Alignment<Mapping> or a List<MatchingPair>
	 */
	public static ThresholdAnalysisData thresholdAnalysis(AbstractMatcher toBeEvaluated, Object reference, double[] thresholds, boolean removeDuplicates) {
		Alignment<Mapping> referenceSet = null;
		List<MatchingPair> referencePairs = null;
		if(reference instanceof Alignment)
			referenceSet = (Alignment<Mapping>) reference;
		else if(reference instanceof List)
			referencePairs = (List<MatchingPair>) reference;
		else return null;
			
		double step = 0.05;
		if(thresholds == null)
			thresholds = Utility.getDoubleArray(0.0d, 0.01d, 101);
		
		ReferenceEvaluationData maxrd = null;
		ReferenceEvaluationData rd;
		Alignment<Mapping> evaluateSet;
	
		double maxTh = step;
		double sumPrecision = 0;
		double sumRecall = 0;
		double sumFmeasure = 0;
		int sumFound = 0;
		int sumCorrect = 0;
		
		String matcherName = toBeEvaluated.getName();
		ThresholdAnalysisData tad = new ThresholdAnalysisData(thresholds);
		tad.setMatcherName(matcherName);
				
		String report = matcherName + "\n\n";
		double th;
		report+="Threshold:\tFound\tCorrect\tReference\tPrecision\tRecall\tF-Measure\n";
		
		// output the info to the console for easy copy/pasting
//		System.out.println("Threshold, " +
//				   "Precision, " +
//				   "Recall, " +
//				   "F-Measure" );
		for(int t = 0; t < thresholds.length; t++) {
			th = thresholds[t];
			toBeEvaluated.getParam().threshold = th;
			toBeEvaluated.select();
			evaluateSet = toBeEvaluated.getAlignment();
			if(referenceSet != null)
				rd = ReferenceEvaluator.compare(evaluateSet, referenceSet);
			else{
				
				Alignment<Mapping> alignment = toBeEvaluated.getAlignment();
				List<MatchingPair> pairs = AlignmentUtilities.alignmentToMatchingPairs(alignment);
										
				AlignmentUtilities.removeDuplicates(referencePairs);
				AlignmentUtilities.removeDuplicates(pairs);
				
				rd = AlignmentUtilities.compare(pairs, referencePairs);
				tad.addEvaluationData(rd);
			}
			
			report += Utility.getNoDecimalPercentFromDouble(th)+"\t"+rd.getMeasuresLine();
			sumPrecision += rd.getPrecision();
			sumRecall += rd.getRecall();
			sumFmeasure += rd.getFmeasure();
			sumFound += rd.getFound();
			sumCorrect += rd.getCorrect();
			
			// output this information to the console for easy copy/pasting  // TODO: make a button to be able to copy/paste this info
//			System.out.println(Double.toString(th) + ", " +
//					   Double.toString(rd.getPrecision()) + ", " +
//					   Double.toString(rd.getRecall()) + ", " +
//					   Double.toString(rd.getFmeasure()) );
			
			if(maxrd == null || maxrd.getFmeasure() < rd.getFmeasure()) {
				maxrd = rd;
				maxTh = th;
			}
		}
		toBeEvaluated.getParam().threshold = maxTh;
		toBeEvaluated.select();
		toBeEvaluated.setRefEvaluation(maxrd);
		report += "Best Run:\n";
		report += Utility.getNoDecimalPercentFromDouble(maxTh)+"\t"+maxrd.getMeasuresLine();
		sumPrecision /= thresholds.length;
		sumRecall /= thresholds.length;
		sumFmeasure /= thresholds.length;
		sumFound /= thresholds.length;
		sumCorrect /= thresholds.length;
		report += "Average:\t"+sumFound+"\t"+sumCorrect+"\t"+maxrd.getExist()+"\t"+Utility.getOneDecimalPercentFromDouble(sumPrecision)+"\t"+Utility.getOneDecimalPercentFromDouble(sumRecall)+"\t"+Utility.getOneDecimalPercentFromDouble(sumFmeasure)+"\n\n";
		tad.setReport(report);
		return tad;
	}
	
}
