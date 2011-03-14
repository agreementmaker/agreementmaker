package am.app.mappingEngine.LinkedOpenData;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;

import am.GlobalStaticVariables;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityMatcher;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityParameters;
import am.app.mappingEngine.hierarchy.HierarchyMatcherModified;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

import com.hp.hpl.jena.ontology.OntModel;

public class LODBatch {
	String report = "";
	
	public LODBatch(){
		
	}
	
	public void run(){
		//singleRun(LODOntologies.MUSIC_ONTOLOGY, LODOntologies.BBC_PROGRAM, "music-bbc");
		singleRun(LODOntologies.MUSIC_ONTOLOGY, LODOntologies.DBPEDIA, "music-dbpedia");
		singleRun(LODOntologies.FOAF, LODOntologies.DBPEDIA, "foaf-dbpedia");
		singleRun(LODOntologies.GEONAMES, LODOntologies.DBPEDIA, "geonames-dbpedia");
		singleRun(LODOntologies.SIOC, LODOntologies.FOAF, "sioc-foaf");
		singleRun(LODOntologies.SW_CONFERENCE, LODOntologies.AKT_PORTAL, "swc-akt");
		singleRun(LODOntologies.SW_CONFERENCE, LODOntologies.DBPEDIA, "swc-dbpedia");
		System.out.println(report);
	}
	
	public void singleRun(String sourceName, String targetName, String testName){
		long start = System.nanoTime();
		Ontology sourceOntology;
		Ontology targetOntology;
		OntoTreeBuilder treeBuilder;
		
		System.out.println("Opening sourceOntology");
		treeBuilder = new OntoTreeBuilder(sourceName, GlobalStaticVariables.SOURCENODE,
			GlobalStaticVariables.LANG_OWL, 
			GlobalStaticVariables.SYNTAX_RDFXML, false, true);
			
		treeBuilder.build();
		sourceOntology = treeBuilder.getOntology();
				
		System.out.println("Opening targetOntology");
				
		treeBuilder = new OntoTreeBuilder(targetName, GlobalStaticVariables.SOURCENODE,
			GlobalStaticVariables.LANG_OWL, 
			GlobalStaticVariables.SYNTAX_RDFXML, false, true);
			
		treeBuilder.build();
		targetOntology = treeBuilder.getOntology();
		
		AdvancedSimilarityMatcher asm = new AdvancedSimilarityMatcher();
		asm.setSourceOntology(sourceOntology);
		asm.setTargetOntology(targetOntology);
		
		AdvancedSimilarityParameters asmParam = new AdvancedSimilarityParameters();
		asmParam.useDictionary = true;
		asm.setParam(asmParam);
		
		System.out.println("ASM matching");				
		try {
			asm.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		HierarchyMatcherModified hmm = new HierarchyMatcherModified();
		AbstractParameters hmmParam = new AbstractParameters();
		hmm.setSourceOntology(sourceOntology);
		hmm.setTargetOntology(targetOntology);
		hmm.addInputMatcher(asm);
		
		System.out.println("HMM matching");
		
		try {
			hmm.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			printDocument(testName, hmm.getAlignmentsStrings(), sourceName, targetName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		long end = System.nanoTime();
		
		long executionTime = (end-start)/1000000;
		
		report += "Execution times:\t" + asm.getExecutionTime() + "\t" + hmm.getExecutionTime() + "\t" + executionTime + "\n";
		System.out.println("Total time: " + executionTime);
		
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
	
	

	public static void main(String[] args) {
		LODBatch batch = new LODBatch();
		batch.run();
		
	}
}
	

