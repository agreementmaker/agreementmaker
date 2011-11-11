package am.app.mappingEngine.LinkedOpenData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityParameters;
import am.app.mappingEngine.hierarchy.HierarchyMatcherModified;
import am.app.mappingEngine.hierarchy.HierarchyMatcherModifiedParameters;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

import com.hp.hpl.jena.util.LocationMapper;

public class LODBatch {
	String report = "";
	
	Logger log;
	
	LocationMapper mapper;
	
	public LODBatch(){
		log = Logger.getLogger(LODBatch.class);
		Logger.getRootLogger().setLevel(Level.DEBUG);	
		
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

	public void singleRun(LODOntology source, LODOntology target, String testName){
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
		
		AdvancedSimilarityMatcher asm = new AdvancedSimilarityMatcher();
		asm.setSourceOntology(sourceOntology);
		asm.setTargetOntology(targetOntology);
		
		AdvancedSimilarityParameters asmParam = new AdvancedSimilarityParameters();
		asmParam.useDictionary = true;
		asm.setParam(asmParam);
		
		log.info("ASM matching");				
		try {
			asm.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HierarchyMatcherModified hmm = new HierarchyMatcherModified();
		hmm.setSourceOntology(sourceOntology);
		hmm.setTargetOntology(targetOntology);
		hmm.addInputMatcher(asm);
		
		HierarchyMatcherModifiedParameters param = new HierarchyMatcherModifiedParameters();
		param.mapper = mapper;
		hmm.setParam(param);
		
		log.info("HMM matching");
		
		try {
			hmm.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			printDocument(testName, hmm.getAlignmentsStrings(true, false, false), source.getUri(), target.getUri());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		long end = System.nanoTime();
		long executionTime = (end-start)/1000000;
		report += "Execution times:\t" + asm.getExecutionTime() + "\t" + hmm.getExecutionTime() + "\t" + executionTime + "\n";
		log.info("Total time: " + executionTime);
		
	}
	
	public void printDocument(String name, String alignmentStrings, String source, String target) throws Exception{
		Date d = new Date();
		String toBePrinted = "AGREEMENT DOCUMENT\n\n";
		toBePrinted += "Date: "+d+"\n";
		toBePrinted += "Source Ontology: "+source+"\n";
		toBePrinted += "Target Ontology: "+target+"\n\n";
		toBePrinted += alignmentStrings;
		
		FileOutputStream out = new FileOutputStream("LOD/batch/" + name + ".txt");
	    PrintStream p = new PrintStream( out );
	    String[] lines = toBePrinted.split("\n");
	    for(int i = 0; i < lines.length; i++)
	    	p.println(lines[i]);
	    p.close();
	    out.close();
	}
	
	
	
	public void run(){
		singleRun(LODOntology.MUSIC_ONTOLOGY, LODOntology.BBC_PROGRAM, "music-bbc");
		singleRun(LODOntology.MUSIC_ONTOLOGY, LODOntology.DBPEDIA, "music-dbpedia");
		singleRun(LODOntology.FOAF, LODOntology.DBPEDIA, "foaf-dbpedia");
		singleRun(LODOntology.GEONAMES, LODOntology.DBPEDIA, "geonames-dbpedia");
		singleRun(LODOntology.SIOC, LODOntology.FOAF, "sioc-foaf");
		singleRun(LODOntology.SW_CONFERENCE, LODOntology.AKT_PORTAL, "swc-akt");
		singleRun(LODOntology.SW_CONFERENCE, LODOntology.DBPEDIA, "swc-dbpedia");
		log.info(report);
	}
	
	public void runOldVersion(){
		singleRun(LODOntology.MUSIC_ONTOLOGY_OLD, LODOntology.BBC_PROGRAM_OLD, "music-bbc");
		singleRun(LODOntology.MUSIC_ONTOLOGY_OLD, LODOntology.DBPEDIA_OLD, "music-dbpedia");
		singleRun(LODOntology.FOAF, LODOntology.DBPEDIA_OLD, "foaf-dbpedia");
		singleRun(LODOntology.GEONAMES_OLD, LODOntology.DBPEDIA_OLD, "geonames-dbpedia");
		singleRun(LODOntology.SIOC, LODOntology.FOAF, "sioc-foaf");
		singleRun(LODOntology.SW_CONFERENCE, LODOntology.AKT_PORTAL, "swc-akt");
		singleRun(LODOntology.SW_CONFERENCE, LODOntology.DBPEDIA_OLD, "swc-dbpedia");
		log.info(report);
	}
	
	public static void main(String[] args) {
		LODBatch batch = new LODBatch();
		batch.runOldVersion();
	}
}
	

