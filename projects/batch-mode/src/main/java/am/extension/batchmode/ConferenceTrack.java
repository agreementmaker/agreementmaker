package am.extension.batchmode;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.matcher.oaei.oaei2009.OAEI2009parameters;
import am.parsing.AlignmentOutput;

public class ConferenceTrack extends Track {

	public final static String AM_NAME = "amaker";
	public final static String OAEI_DIR = "C:/dws_workspace/";
	public final static String OAEI_OUTPUT_DIR = OAEI_DIR+AM_NAME+"/";
	public final static String TRACK_DIR = "conference";
	public final static String TRACK_INPUT_DIR = OAEI_DIR+TRACK_DIR+"/";
	public final static String TRACK_OUTPUT_DIR = OAEI_OUTPUT_DIR+TRACK_DIR+"/";
	public final static String TRACK_EXTENDED_OUTPUT = OAEI_OUTPUT_DIR+"conference_ext"+"/";
	
	private String ontodir; // ontology files directory

	private File[] ontologyFiles;
	public boolean solveConflicts = false;
	public boolean loadRDFinstead = false;

	
	public ConferenceTrack(String directory) {
		super();
		
		ontodir = TRACK_INPUT_DIR;
	}
	
	
	public void execute() throws Exception {

		// get a list of the conference track ontology files.
		ontologyFiles = getOntologyFiles();
		if( ontologyFiles == null ) { 
			System.out.println("Error locating ontology files."); 
			System.exit(1); 
		}
		
		String matcher = "OAEI 2009";
		OAEI2009parameters param = new OAEI2009parameters(OAEI2009parameters.CONFERENCE);
		
		double threshold = 0.6;
		int sourceCardinality = 1;
		int targetCardinality = 1;
 
		String sourceUri;
		String targetUri;
		ArrayList<AbstractMatcher> matcherList;
		AlignmentOutput ao;
		String outputFileDir;
		long startTime = System.nanoTime()/1000000;
		OntoTreeBuilder.Profile reasoner = OntoTreeBuilder.Profile.noReasoner;
		if(loadRDFinstead){
			reasoner = OntoTreeBuilder.Profile.defaultProfile;
		}
		matcherList = computeMultipleAlignment(solveConflicts, ontologyFiles, OntologyLanguage.OWL, OntologySyntax.RDFXML, false, matcher, threshold, sourceCardinality, targetCardinality, param, reasoner);
		long endTime = System.nanoTime()/1000000;
		long totTime = endTime - startTime;
		System.out.println("Total execution time in h.m.s.ms: "+Utility.getFormattedTime(totTime));
		
		outputFileDir = TRACK_OUTPUT_DIR;//the last / is not needed for mkdirs but is needed later
		if(solveConflicts){
			outputFileDir = TRACK_EXTENDED_OUTPUT;
		}
		String timeFileName = outputFileDir+"executionTime.txt";
		(new File(outputFileDir)).mkdirs(); //create directories
		TrackDispatcher.printExecutionTime(totTime, timeFileName);
		for(int i=0; i<matcherList.size();i++){
			AbstractMatcher theMatcher = matcherList.get(i);
			Ontology sourceOntology = theMatcher.getSourceOntology();
			Ontology targetOntology = theMatcher.getTargetOntology();
			sourceUri = sourceOntology.getURI();
			targetUri = targetOntology.getURI();	
			ao = new AlignmentOutput(theMatcher.getAlignment(), outputFileDir+"/" + removeFileExtension(ontologyFiles[sourceOntology.getIndex()].getName()) + "-"+ removeFileExtension(ontologyFiles[targetOntology.getIndex()].getName()) +".rdf");
			ao.write(sourceUri, targetUri, sourceUri, targetUri, matcher);
		}
		
		System.out.println("All alignment files have been saved correctly.");
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
				if(loadRDFinstead){
					return name.endsWith(".rdf");
				}
				else{
					return name.endsWith(".owl");
				}

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
		return fileName;
	}
	
	
}
