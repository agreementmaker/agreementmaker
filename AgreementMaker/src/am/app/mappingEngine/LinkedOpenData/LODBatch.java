package am.app.mappingEngine.LinkedOpenData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import am.Utility;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityParameters;
import am.app.mappingEngine.hierarchy.HierarchyMatcherModifiedParameters;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011Matcher;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011MatcherParameters;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011MatcherParameters.OAEI2011Configuration;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.mappingEngine.referenceAlignment.ThresholdAnalysisData;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.utility.referenceAlignment.AlignmentUtilities;

import com.hp.hpl.jena.util.LocationMapper;

public class LODBatch {
	String report = "";
	
	Logger log;
	
	LocationMapper mapper;
	
	public LODBatch(){
		log = Logger.getLogger(LODBatch.class);
		//Logger.getRootLogger().setLevel(Level.DEBUG);	
		
		//log.setLevel(Level.INFO);
		
		initMapper();		
	}
	
	private void initMapper() {
		mapper = new LocationMapper();
		mapper.addAltEntry("http://data.semanticweb.org/ns/swc/swrc", new File("LOD/LocationMappings/ns.swc.swrc.rdf").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/NET/c4dm/timeline.owl", new File("LOD/LocationMappings/timeline.n3").getAbsolutePath());
		mapper.addAltEntry("http://data.semanticweb.org/ns/swc/swrc-topics", new File("LOD/LocationMappings/swrc-topics.owl").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/vocab/bio/0.1/", new File("LOD/LocationMappings/vocab.org.bio.rdf").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/vocab/frbr/core", new File("LOD/LocationMappings/frbr.core.rdf").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/dc/terms/", new File("LOD/LocationMappings/dcterms.rdf").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/ontology/similarity/", new File("LOD/LocationMappings/musim.owl").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/NET/c4dm/keys.owl", new File("LOD/LocationMappings/keys.owl").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/dc/elements/1.1/", new File("LOD/LocationMappings/dcelements.rdf").getAbsolutePath());
		mapper.addAltEntry("http://xmlns.com/foaf/0.1/", new File("LOD/LocationMappings/foaf.rdf").getAbsolutePath());
		
		//http://xmlns.com/foaf/0.1/
		
	}
	
	public ThresholdAnalysisData singleRun(LODOntology source, LODOntology target, String testName, String refAlign){
		long start = System.nanoTime();
		Ontology sourceOntology = null;
		Ontology targetOntology = null;
		OntoTreeBuilder treeBuilder;
		
		//OntDocumentManager.getInstance().setProcessImports(false);
		
		log.info("Opening sourceOntology...");
		sourceOntology = OntoTreeBuilder.loadOntology(new File(source.getFilename()).getPath(), source.getLang(), source.getSyntax(), mapper);
		//sourceOntology = LODUtils.openOntology(new File(sourceName).getAbsolutePath());	
		log.info("Done");	
		
		log.debug("OntData:\t" + source.getUri() + "\t" + sourceOntology.getClassesList().size());
				
		log.info("Opening targetOntology...");
		targetOntology = OntoTreeBuilder.loadOntology(new File(target.getFilename()).getPath(), target.getLang(), target.getSyntax(), mapper);
		//targetOntology = LODUtils.openOntology(new File(targetName).getAbsolutePath());
		log.info("Done");
		
		log.debug("OntData:\t" + target.getUri() + "\t" + targetOntology.getClassesList().size());
		
		AdvancedSimilarityMatcher matcher = new AdvancedSimilarityMatcher();
		
		AdvancedSimilarityParameters asmParam = new AdvancedSimilarityParameters();
		asmParam.useDictionary = true;
		matcher.setParameters(asmParam);
		
//		OAEI2011Matcher matcher = new OAEI2011Matcher();
//		OAEI2011MatcherParameters OAEIparam = new OAEI2011MatcherParameters();
//		//matcher.
//		OAEIparam.threshold = 0.9;
//		OAEIparam.automaticConfiguration = false;
//		OAEIparam.selectedConfiguration = OAEI2011Configuration.GENERAL_MULTI;
//		matcher.setAlignProp(false);
//		matcher.setPerformSelection(true);
//		matcher.setParam(OAEIparam);
		
		matcher.setSourceOntology(sourceOntology);
		matcher.setTargetOntology(targetOntology);
		
		log.info("Matching");				
		try {
			matcher.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AbstractMatcher hmm = MatcherFactory.getMatcherInstance(MatchersRegistry.HierarchyMatcherModified, 0);
		hmm.setSourceOntology(sourceOntology);
		hmm.setTargetOntology(targetOntology);
		hmm.addInputMatcher(matcher);
		
		List<MatchingPair> reference = AlignmentUtilities.getMatchingPairsTAB(refAlign);
		//hmm.setReferenceAlignment(reference);
		
		HierarchyMatcherModifiedParameters param = new HierarchyMatcherModifiedParameters();
		param.mapper = mapper;
		param.threshold = 0.0;
		hmm.setParameters(param);
		
		hmm = MatcherFactory.getMatcherInstance(MatchersRegistry.WSM, 0);
		hmm.setSourceOntology(sourceOntology);
		hmm.setTargetOntology(targetOntology);
		reference = AlignmentUtilities.getMatchingPairsTAB(refAlign);
		//hmm.setReferenceAlignment(reference);
		hmm.setParameters(param);
		
		
		log.info("HMM matching");
		try {
			hmm.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("Printing alignments...");
		try {
			printDocument(testName, hmm.getAlignmentsStrings(true, false, false), source.getUri(), target.getUri());
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("Done");
		
		long end = System.nanoTime();
		long executionTime = (end-start)/1000000;
		report += "Execution times:\t" + matcher.getExecutionTime() + "\t" + hmm.getExecutionTime() + "\t" + executionTime + "\n";
		log.info("Total time: " + executionTime);
		
		boolean smallValues = false;
		
		double[] thresholds = Utility.getDoubleArray(0.0d, 0.01d, 200);
		if(smallValues){
			thresholds = Utility.getDoubleArray(0.0d, 0.01d, 100);
		}
		
		
		log.info("Threshold analysis...");
		/*
		// FIXME: Move this to the ThresholdAnalysis.java.
		ThresholdAnalysisData data = AlignmentUtilities.thresholdAnalysis(hmm, reference, thresholds, true);
		log.info("Done");
		
		System.out.println(Arrays.toString(thresholds));
		
		
		for (int i = 0; i < data.getThresholds().length; i++) {
			System.out.println(data.getThresholds()[i] + " " + data.getEvaluationData(i).getPrecision() 
					+ " " + data.getEvaluationData(i).getRecall());
		}*/
		
		
		return null;
	}
	
	public void singleRunEq(AbstractMatcher matcher, LODOntology source, LODOntology target, String testName, String outputFolder){
		long start = System.nanoTime();
		Ontology sourceOntology = null;
		Ontology targetOntology = null;
		OntoTreeBuilder treeBuilder;
				
		log.info("Opening sourceOntology...");
		sourceOntology = OntoTreeBuilder.loadOntology(new File(source.getFilename()).getPath(), source.getLang(), source.getSyntax(), mapper);
		//sourceOntology = LODUtils.openOntology(new File(sourceName).getAbsolutePath());	
		log.info("Done");	
				
		log.info("Opening targetOntology...");
		targetOntology = OntoTreeBuilder.loadOntology(new File(target.getFilename()).getPath(), target.getLang(), target.getSyntax(), mapper);
		//targetOntology = LODUtils.openOntology(new File(targetName).getAbsolutePath());
		log.info("Done");
		
		matcher.setSourceOntology(sourceOntology);
		matcher.setTargetOntology(targetOntology);
		
		LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
		lexParam.sourceOntology = sourceOntology;
		lexParam.targetOntology = targetOntology;
		
		lexParam.sourceUseLocalname = false;
		lexParam.targetUseLocalname = false;
		lexParam.sourceUseSCSLexicon = true;
		lexParam.targetUseSCSLexicon = true;
		
		lexParam.detectStandardProperties(sourceOntology);
		lexParam.detectStandardProperties(targetOntology);
		
//		try {
//			Core.getInstance().getLexiconStore().buildAll(lexParam);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		
		
		log.info("Matching");				
		try {
			matcher.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			printDocument(testName, matcher.getAlignmentsStrings(true, false, false), source.getUri(), target.getUri(), outputFolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		long end = System.nanoTime();
		long executionTime = (end-start)/1000000;
		report += "Execution times:\t" + matcher.getExecutionTime() + "\t" + matcher.getExecutionTime() + "\t" + executionTime + "\n";
		log.info("Total time: " + executionTime);
		
	}
	
	public void printDocument(String name, String alignmentStrings, String source, String target) throws Exception{
		printDocument(name, alignmentStrings, source, target, null);
	}
	
	/**
	 * @param outputFolder Remember the slash at the end!!!
	 * @throws Exception
	 */
	public void printDocument(String name, String alignmentStrings, String source, String target, String outputFolder) throws Exception{
		Date d = new Date();
		String toBePrinted = "AGREEMENT DOCUMENT\n\n";
		toBePrinted += "Date: "+d+"\n";
		toBePrinted += "Source Ontology: "+source+"\n";
		toBePrinted += "Target Ontology: "+target+"\n\n";
		toBePrinted += alignmentStrings;
		
		File file;
		if(outputFolder == null)
			file = new File("LOD/batch/" + name + ".txt");
		else file = new File(outputFolder + name + ".txt");
		
		FileOutputStream out = new FileOutputStream(file); 
		
		log.info("Printing on file " + file);
		
		PrintStream p = new PrintStream( out );
	    String[] lines = toBePrinted.split("\n");
	    for(int i = 0; i < lines.length; i++)
	    	p.println(lines[i]);
	    p.close();
	    out.close();
	}
	
	
		
	
	public void run(){
		singleRun(LODOntology.MUSIC_ONTOLOGY, LODOntology.BBC_PROGRAM, "music-bbc", LODReferences.MUSIC_BBC);
		singleRun(LODOntology.MUSIC_ONTOLOGY, LODOntology.DBPEDIA, "music-dbpedia", LODReferences.MUSIC_DBPEDIA);
		singleRun(LODOntology.FOAF, LODOntology.DBPEDIA, "foaf-dbpedia", LODReferences.FOAF_DBPEDIA);
		singleRun(LODOntology.GEONAMES, LODOntology.DBPEDIA, "geonames-dbpedia", LODReferences.GEONAMES_DBPEDIA);
		singleRun(LODOntology.SIOC, LODOntology.FOAF, "sioc-foaf", LODReferences.SIOC_FOAF);
		singleRun(LODOntology.SW_CONFERENCE, LODOntology.AKT_PORTAL, "swc-akt", LODReferences.SWC_AKT);
		singleRun(LODOntology.SW_CONFERENCE, LODOntology.DBPEDIA, "swc-dbpedia", LODReferences.SWC_DBPEDIA);
		log.info(report);
	}
	
	public void runOldVersion(){
		
		List<ThresholdAnalysisData> data = new ArrayList<ThresholdAnalysisData>();
		
		data.add(singleRun(LODOntology.FOAF, LODOntology.DBPEDIA_OLD, "foaf-dbpedia", LODReferences.FOAF_DBPEDIA));
		data.add(singleRun(LODOntology.GEONAMES_OLD, LODOntology.DBPEDIA_OLD, "geonames-dbpedia", LODReferences.GEONAMES_DBPEDIA));
		data.add(singleRun(LODOntology.MUSIC_ONTOLOGY_OLD, LODOntology.BBC_PROGRAM_OLD, "music-bbc", LODReferences.MUSIC_BBC));
		data.add(singleRun(LODOntology.MUSIC_ONTOLOGY_OLD, LODOntology.DBPEDIA_OLD, "music-dbpedia", LODReferences.MUSIC_DBPEDIA));
		data.add(singleRun(LODOntology.SW_CONFERENCE, LODOntology.AKT_PORTAL, "swc-akt", LODReferences.SWC_AKT));
		data.add(singleRun(LODOntology.SW_CONFERENCE, LODOntology.DBPEDIA_OLD, "swc-dbpedia", LODReferences.SWC_DBPEDIA));
		data.add(singleRun(LODOntology.SIOC, LODOntology.FOAF, "sioc-foaf", LODReferences.SIOC_FOAF));
		
		log.info("Computing best run...");
		double bestTh = ThresholdAnalysisData.getBestOverallRun(data);
		log.info("Done");
		
		
		log.info(report);
		
		System.out.println("bestTh: " + bestTh);
		
		LODEvaluator evaluator = new LODEvaluator();
		evaluator.setThreshold(bestTh);
		
		try {
			evaluator.evaluateAllTestsOld();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public void runOldEquality(){
		AbstractMatcher matcher = initMatcher();
		String outputFolder = "LOD/batchEq/PURPOSE/";
		singleRunEq(matcher, LODOntology.FOAF, LODOntology.DBPEDIA_OLD, "foaf-dbpedia", outputFolder);
		singleRunEq(matcher, LODOntology.GEONAMES_OLD, LODOntology.DBPEDIA_OLD, "geonames-dbpedia", outputFolder);
		singleRunEq(matcher, LODOntology.MUSIC_ONTOLOGY_OLD, LODOntology.BBC_PROGRAM_OLD, "music-bbc", outputFolder);
		singleRunEq(matcher, LODOntology.MUSIC_ONTOLOGY_OLD, LODOntology.DBPEDIA_OLD, "music-dbpedia", outputFolder);
		singleRunEq(matcher, LODOntology.SW_CONFERENCE, LODOntology.AKT_PORTAL, "swc-akt", outputFolder);
		singleRunEq(matcher, LODOntology.SW_CONFERENCE, LODOntology.DBPEDIA_OLD, "swc-dbpedia", outputFolder);
		singleRunEq(matcher, LODOntology.SIOC, LODOntology.FOAF, "sioc-foaf", outputFolder);
		//log.info(report);
		
		try {
			new LODEvaluator().evaluateAllTestsEq();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static AbstractMatcher initMatcher() {
//		AdvancedSimilarityMatcher matcher = new AdvancedSimilarityMatcher();
//		AdvancedSimilarityParameters asmParam = new AdvancedSimilarityParameters();
//		asmParam.useDictionary = true;
//		asmParam.threshold = 0.8;
//		matcher.setParam(asmParam);
		
		OAEI2011Matcher matcher = new OAEI2011Matcher();
		OAEI2011MatcherParameters param = new OAEI2011MatcherParameters();
		//matcher.
		param.threshold = 0.9;
		param.automaticConfiguration = false;
		param.selectedConfiguration = OAEI2011Configuration.GENERAL_PURPOSE;
		matcher.setAlignProp(false);
		matcher.setPerformSelection(true);
		matcher.setParameters(param);
		
				
//		LexicalSynonymMatcher matcher = new LexicalSynonymMatcher();
//		LexicalSynonymMatcherParameters param = new LexicalSynonymMatcherParameters();
//		param.threshold = 0.6;
//		matcher.setParam(param);
		
		return matcher;
	}
	
	public static void main(String[] args) {
		LODBatch batch = new LODBatch();
		
		//Logger.getRootLogger().setLevel(Level.DEBUG);
		
		
		batch.runOldVersion();
		//batch.runOldEquality();
	}
}
	

