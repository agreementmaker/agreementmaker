package am.matcher.lod.LinkedOpenData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.Utility;
import am.app.Core;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.referenceAlignment.ThresholdAnalysisData;
import am.app.mappingEngine.utility.MatchingPair;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.profiling.classification.OntologyClassifier.OAEI2011Configuration;
import am.matcher.asm.AdvancedSimilarityMatcher;
import am.matcher.asm.AdvancedSimilarityParameters;
import am.matcher.lod.hierarchy.HierarchyMatcherModified;
import am.matcher.lod.hierarchy.HierarchyMatcherModifiedParameters;
import am.matcher.lod.hierarchy.WordnetSubclassMatcher;
import am.matcher.oaei.oaei2011.OAEI2011Matcher;
import am.matcher.oaei.oaei2011.OAEI2011MatcherParameters;
import am.tools.ThresholdAnalysis.ThresholdAnalysis;
import am.utility.RunTimer;
import am.utility.referenceAlignment.AlignmentUtilities;

import com.hp.hpl.jena.util.LocationMapper;

public class LODBatch {
	StringBuilder report;
	
	Logger log;
	
	LocationMapper mapper;
	
	public LODBatch(){
		log = Logger.getLogger(LODBatch.class);
		log.setLevel(Level.INFO);
		
		initMapper();		
	}
	
	private void initMapper() {
		mapper = new LocationMapper();
		final String root = Core.getInstance().getRoot() + File.separator;
		mapper.addAltEntry("http://data.semanticweb.org/ns/swc/swrc", new File(root + "LOD/LocationMappings/ns.swc.swrc.rdf").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/NET/c4dm/timeline.owl", new File(root + "LOD/LocationMappings/timeline.n3").getAbsolutePath());
		mapper.addAltEntry("http://data.semanticweb.org/ns/swc/swrc-topics", new File(root + "LOD/LocationMappings/swrc-topics.owl").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/vocab/bio/0.1/", new File(root + "LOD/LocationMappings/vocab.org.bio.rdf").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/vocab/frbr/core", new File(root + "LOD/LocationMappings/frbr.core.rdf").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/dc/terms/", new File(root + "LOD/LocationMappings/dcterms.rdf").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/ontology/similarity/", new File(root + "LOD/LocationMappings/musim.owl").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/NET/c4dm/keys.owl", new File(root + "LOD/LocationMappings/keys.owl").getAbsolutePath());
		mapper.addAltEntry("http://purl.org/dc/elements/1.1/", new File(root + "LOD/LocationMappings/dcelements.rdf").getAbsolutePath());
		mapper.addAltEntry("http://xmlns.com/foaf/0.1/", new File(root + "LOD/LocationMappings/foaf.rdf").getAbsolutePath());
	}
	
	public ThresholdAnalysisData singleRun(LODOntology source, LODOntology target, String testName, String refAlign){
		log.info("+---------------------------------------------------------------+");
		log.info("Matching " + new File(source.getFilename()).getName() + " with " + new File(target.getFilename()).getName());
		log.info("+---------------------------------------------------------------+");
		
		RunTimer timer = new RunTimer().start();
		
		Ontology sourceOntology = null;
		Ontology targetOntology = null;
		
		//OntDocumentManager.getInstance().setProcessImports(false);
		
		log.info("Opening sourceOntology " + new File(source.getFilename()).getName() + " ...");
		sourceOntology = OntoTreeBuilder.loadOntology(new File(source.getFilename()).getPath(), source.getLang(), source.getSyntax(), mapper);
		//sourceOntology = LODUtils.openOntology(new File(source.getFilename()).getAbsolutePath(), mapper);	
		
		log.debug("OntData:\t" + source.getUri() + "\t" + sourceOntology.getClassesList().size());
				
		log.info("Opening targetOntology " + new File(target.getFilename()).getName() + " ...");
		targetOntology = OntoTreeBuilder.loadOntology(new File(target.getFilename()).getPath(), target.getLang(), target.getSyntax(), mapper);
		//targetOntology = LODUtils.openOntology(new File(targetName).getAbsolutePath());
		
		log.debug("OntData:\t" + target.getUri() + "\tNum classes: " + targetOntology.getClassesList().size());
		
		AdvancedSimilarityMatcher matcher = new AdvancedSimilarityMatcher();
		
		AdvancedSimilarityParameters asmParam = new AdvancedSimilarityParameters();
		//asmParam.useDictionary = true;
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
		
		log.info("Matching the ontologies ...");				
		try {
			matcher.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AbstractMatcher hmm = new HierarchyMatcherModified();
		hmm.setSourceOntology(sourceOntology);
		hmm.setTargetOntology(targetOntology);
		hmm.addInputMatcher(matcher);
		
		List<MatchingPair> reference = AlignmentUtilities.getMatchingPairsTAB(refAlign);
		//hmm.setReferenceAlignment(reference);
		
		HierarchyMatcherModifiedParameters param = new HierarchyMatcherModifiedParameters();
		param.mapper = mapper;
		param.threshold = 0.0;
		hmm.setParameters(param);
		
		hmm = new WordnetSubclassMatcher();
		hmm.setSourceOntology(sourceOntology);
		hmm.setTargetOntology(targetOntology);
		reference = AlignmentUtilities.getMatchingPairsTAB(refAlign);
		//hmm.setReferenceAlignment(reference);
		hmm.setParameters(param);
		
		
		log.info("Hierarchy Matcher Modified (HMM) matching");
		try {
			hmm.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("Saving alignment...");
		try {
			String tempRoot = Core.getInstance().getRoot() + File.separator + "lodOutput";
			File tempRootFile = new File(tempRoot);
			if( !tempRootFile.exists() ) {
				tempRootFile.mkdirs();
			}
			printDocument(testName, hmm.getAlignmentsStrings(true, false, false), source.getUri(), target.getUri(), tempRootFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		report.append("Execution times:\tMatcher: ");
		report.append(matcher.getExecutionTime());
		report.append("\tHMM: ");
		report.append(hmm.getExecutionTime());
		report.append("\tTotal: ");
		report.append(timer.getFormattedRunTime());
		report.append("\n");
		
		log.info("Total time: " + timer.getFormattedRunTime());
		
		boolean smallValues = false;
		
		double[] thresholds = Utility.getDoubleArray(0.0d, 0.01d, 200);
		if(smallValues){
			thresholds = Utility.getDoubleArray(0.0d, 0.01d, 100);
		}
		
		
		log.info("Threshold analysis...");
		ThresholdAnalysisData data = ThresholdAnalysis.thresholdAnalysis(hmm, reference, thresholds, true);
		
		//System.out.println(Arrays.toString(thresholds));
		
		log.info("+-----------+-----------+--------+");
		log.info("| Threshold | Precision | Recall |");
		log.info("+-----------+-----------+--------+");
		final double[] dThresholds = data.getThresholds();
		for (int i = 0; i < data.getThresholds().length; i++) {
			log.info(String.format("|   %1.2f    |   %1.2f    |  %1.2f  |", 
					dThresholds[i], data.getEvaluationData(i).getPrecision(), data.getEvaluationData(i).getRecall()));
		}
		log.info("+-----------+-----------+--------+");

		return data;
	}
	
	public void singleRunEq(AbstractMatcher matcher, LODOntology source, LODOntology target, String testName, String outputFolder){
		RunTimer timer = new RunTimer().start();
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
		
		
		if( Core.getLexiconStore() == null ) {
			LexiconBuilderParameters lexParam = new LexiconBuilderParameters();
			lexParam.sourceOntology = sourceOntology;
			lexParam.targetOntology = targetOntology;
			
			lexParam.sourceUseLocalname = false;
			lexParam.targetUseLocalname = false;
			lexParam.sourceUseSCSLexicon = true;
			lexParam.targetUseSCSLexicon = true;
			
			lexParam.detectStandardProperties();
			
			try {
				Core.getLexiconStore().buildAll(lexParam);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		
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
		
		
		timer.stop();
		report.append("Execution times:\tMatcher: ");
		report.append(matcher.getExecutionTime());
		report.append("\tTotal: ");
		report.append(timer.getFormattedRunTime());
		report.append("\n");
		log.info("Total time: " + timer.getFormattedRunTime());
		
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
		else 
			file = new File(outputFolder + File.separator + name + ".txt");
		
		FileOutputStream out = new FileOutputStream(file); 
		
		log.info("Saving to file: " + file);
		
		PrintStream p = new PrintStream( out );
	    String[] lines = toBePrinted.split("\n");
	    for(int i = 0; i < lines.length; i++)
	    	p.println(lines[i]);
	    p.close();
	    out.close();
	}
	
	
		
	
	public void run(){
		report = new StringBuilder();
		singleRun(LODOntology.MUSIC_ONTOLOGY, LODOntology.BBC_PROGRAM, "music-bbc", LODReferences.MUSIC_BBC.getPath());
		singleRun(LODOntology.MUSIC_ONTOLOGY, LODOntology.DBPEDIA, "music-dbpedia", LODReferences.MUSIC_DBPEDIA.getPath());
		singleRun(LODOntology.FOAF, LODOntology.DBPEDIA, "foaf-dbpedia", LODReferences.FOAF_DBPEDIA.getPath());
		singleRun(LODOntology.GEONAMES, LODOntology.DBPEDIA, "geonames-dbpedia", LODReferences.GEONAMES_DBPEDIA.getPath());
		singleRun(LODOntology.SIOC, LODOntology.FOAF, "sioc-foaf", LODReferences.SIOC_FOAF.getPath());
		singleRun(LODOntology.SW_CONFERENCE, LODOntology.AKT_PORTAL, "swc-akt", LODReferences.SWC_AKT.getPath());
		singleRun(LODOntology.SW_CONFERENCE, LODOntology.DBPEDIA, "swc-dbpedia", LODReferences.SWC_DBPEDIA.getPath());
		log.info(report);
	}
	
	public void runOldVersion(){
		report = new StringBuilder();
		
		List<ThresholdAnalysisData> data = new ArrayList<ThresholdAnalysisData>();
		
		data.add(singleRun(LODOntology.FOAF, LODOntology.DBPEDIA_OLD, "foaf-dbpedia", LODReferences.FOAF_DBPEDIA.getPath()));
		data.add(singleRun(LODOntology.GEONAMES_OLD, LODOntology.DBPEDIA_OLD, "geonames-dbpedia", LODReferences.GEONAMES_DBPEDIA.getPath()));
		data.add(singleRun(LODOntology.MUSIC_ONTOLOGY_OLD, LODOntology.BBC_PROGRAM_OLD, "music-bbc", LODReferences.MUSIC_BBC.getPath()));
		data.add(singleRun(LODOntology.MUSIC_ONTOLOGY_OLD, LODOntology.DBPEDIA_OLD, "music-dbpedia", LODReferences.MUSIC_DBPEDIA.getPath()));
		data.add(singleRun(LODOntology.SW_CONFERENCE, LODOntology.AKT_PORTAL, "swc-akt", LODReferences.SWC_AKT.getPath()));
		data.add(singleRun(LODOntology.SW_CONFERENCE, LODOntology.DBPEDIA_OLD, "swc-dbpedia", LODReferences.SWC_DBPEDIA.getPath()));
		data.add(singleRun(LODOntology.SIOC, LODOntology.FOAF, "sioc-foaf", LODReferences.SIOC_FOAF.getPath()));
		
		log.info("Computing best run...");
		double bestTh = ThresholdAnalysisData.getBestOverallRun(data);
		
		log.info(report);
		log.info("bestTh: " + bestTh);
		
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
	

