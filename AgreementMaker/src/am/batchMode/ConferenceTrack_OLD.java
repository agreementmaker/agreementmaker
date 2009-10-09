package am.batchMode;

import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AlignmentSet;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.oaei2009.OAEI2009parameters;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.output.AlignmentOutput;

import java.io.*;
import java.util.Vector;

public class ConferenceTrack_OLD extends Track {

	public final static String AM_NAME = "amaker";
	public final static String OAEI_DIR = "./OAEI09/";
	public final static String OAEI_OUTPUT_DIR = OAEI_DIR+AM_NAME+"/";
	public final static String TRACK_DIR = "conference/";
	public final static String TRACK_INPUT_DIR = OAEI_DIR+TRACK_DIR;
	public final static String TRACK_OUTPUT_DIR = OAEI_OUTPUT_DIR+TRACK_DIR;
	
	private String ontodir; // ontology files directory

	private File[] ontologyFiles;

	
	public ConferenceTrack_OLD(String directory) {
		super();
		
		ontodir = directory;
	}
	
	
	public void execute() throws Exception {

		// get a list of the conference track ontology files.
		ontologyFiles = getOntologyFiles();
		if( ontologyFiles == null ) { 
			System.out.println("Error loading ontology files."); 
			System.exit(1); 
		}
		
		long startTime = System.nanoTime()/1000000;
		
		// compare each ontology to every other ontology
		for( int i = 0; i < ontologyFiles.length - 1; i++ ) {		
			for( int j = i+1; j < ontologyFiles.length; j++ ) {
				computeMatching( i, j );
			}
		}
		
		long endTime = System.nanoTime()/1000000;
		long totTime = endTime - startTime;
		System.out.println("Total execution time in h.m.s.ms: "+Utility.getFormattedTime(totTime));
		String timeFileName = TRACK_OUTPUT_DIR+"executionTime.txt";
		TrackDispatcher.printExecutionTime(totTime, timeFileName);
		
	}
	
	
	private void computeMatching( int sourceIndex, int targetIndex ) throws Exception {
		System.out.println("Matching: " + ontologyFiles[sourceIndex].getName() + " <-> " + ontologyFiles[targetIndex].getName() );
		
		
		MatchersRegistry matcher = MatchersRegistry.OAEI2009;
		OAEI2009parameters param = new OAEI2009parameters(OAEI2009parameters.CONFERENCE);
		
		double threshold = 0.6;
		int sourceCardinality = 1;
		int targetCardinality = 1;
		
		
		String sourceOntology;
		String targetOntology;
		String sourceUri;
		String targetUri;
		AlignmentSet as;
		AlignmentOutput ao;
		String outputFileDir;
		
		sourceOntology = ontologyFiles[sourceIndex].getAbsolutePath();
		targetOntology = ontologyFiles[targetIndex].getAbsolutePath();

		as = computeAlignment(sourceOntology, targetOntology, GlobalStaticVariables.LANG_OWL, GlobalStaticVariables.SYNTAX_RDFXML, true, matcher, threshold, sourceCardinality, targetCardinality, param);
		sourceUri = Core.getInstance().getSourceOntology().getURI();
		targetUri = Core.getInstance().getTargetOntology().getURI();
			
	
		outputFileDir = TRACK_OUTPUT_DIR;//the last / is not needed for mkdirs but is needed later
		(new File(outputFileDir)).mkdirs();//create directories
		
		ao = new AlignmentOutput(as, outputFileDir+"/" + removeFileExtension(ontologyFiles[sourceIndex].getName()) + "-"+ removeFileExtension(ontologyFiles[targetIndex].getName()) +".rdf");
		ao.write(sourceUri, targetUri, sourceUri, targetUri);
		
	}
	
	/**
	 * This method reads the directory provided by the user and finds all the OWL ontology files.
	 * @return Array of File objects, corresponding to the ontology files to be used for the conference track.
	 */
	private File[] getOntologyFiles() {
		
		File[] owlFiles = null;
		
		if( ontodir == "" ) {
			// no directory was provided.
			System.out.println("Please provide the directory where the conference track ontologies reside as the second command line argument.");
			return null;
		}

		File conferenceOntologiesDir = new File(ontodir);
		if( !conferenceOntologiesDir.exists() ) {
			System.out.println(ontodir + ": Directory does not exist.");
			return null;
		}
		
		FilenameFilter owlFileFilter = new FilenameFilter() {
			public boolean accept( File dir, String name) {
				return name.endsWith(".owl");
			}
		};
		
		owlFiles = conferenceOntologiesDir.listFiles( owlFileFilter );
		
		if( owlFiles == null || owlFiles.length == 0 ) {
			System.out.println("Could not find any OWL ontology files (*.owl).");
			return null;
		} 
		else {
			System.out.println("Found " + owlFiles.length + " OWL ontology files. ");
		}
		
		return owlFiles; 
	}
	
	public static String removeFileExtension(String fileName) {
		if(null != fileName && fileName.contains(".")) {
			return fileName.substring(0, fileName.lastIndexOf("."));
		}
		return "unknown";
	}
	
	
}
