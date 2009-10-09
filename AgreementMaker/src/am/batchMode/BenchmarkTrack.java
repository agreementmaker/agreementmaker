package am.batchMode;

import java.io.File;
import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AlignmentSet;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.oaei2009.OAEI2009parameters;
import am.output.AlignmentOutput;

public class BenchmarkTrack extends Track{
	
	
	//Needs to be at most 8 digit lower case
	//For Anatomy, it is 6 digits
	//We went for "amaker" in our submission.
	public final static String AM_NAME = "amaker";
	
	//DIRECTORIES
	public final static String OAEI_DIR = "./OAEI09/";
	public final static String OAEI_OUTPUT_DIR = OAEI_DIR+AM_NAME+"/";
	public final static String TRACK_DIR = "benchmarks/";
	public final static String TRACK_INPUT_DIR = OAEI_DIR+TRACK_DIR;
	public final static String TRACK_OUTPUT_DIR = OAEI_OUTPUT_DIR+TRACK_DIR;
	
	//ONTOLOGIES
	public final static String ONTO_NAME = "onto.rdf";
	public final static String SOURCE_ONTOLOGY = TRACK_INPUT_DIR+"101/"+ONTO_NAME;	
	
	public final static String[] TARGET_ONTOLOGIES = {
		//DOWNLOADED in a ZIP file in the OAEI2009 benchmarks web site
		//Some cases opened and "saved as" in Protege, so that they can be loaded without Pellet error.
		//Those cases marked with (*).
		"101",
		"102",
		"103",
		"104",
		"201",
		"201-2",
		"201-4",
		"201-6",
		"201-8",
		"202",
		"202-2",
		"202-4",
		"202-6",
		"202-8",
		"203",//On the web site it's written that it doesn't exist, but it does in the zip file.
		"204",
		"205",
		"206",
		"207",
		"208",
		"209",
		"210",
		"221",
		"222",
		"223",
		"224",//*
		"225",
		//"226",//Doesn't exist in the track yet
		//"227",//Doesn't exist in the track yet
		"228",
		//"229",//Doesn't exist in the track yet
		"230",
		"231",//In the web site it's written that it doesn't exist, but it does in the zip file.
		"232",//*
		"233",
		"236",
		"237",//*
		"238",//*
		"239",
		"240",
		"241",
		"246",
		"247",
		"248",
		"248-2",
		"248-4",
		"248-6",
		"248-8",
		"249",//*
		"249-2",//*
		"249-4",//*
		"249-6",//*
		"249-8",//*
		"250",
		"250-2",
		"250-4",
		"250-6",
		"250-8",
		"251",
		"251-2",
		"251-4",
		"251-6",
		"251-8",
		"252",
		"252-2",
		"252-4",
		"252-6",
		"252-8",
		"253",	//*
		"253-2",//*
		"253-4",//*
		"253-6",//*
		"253-8",//*
		"254",
		"254-2",
		"254-4",
		"254-6",
		"254-8",
		"257",
		"257-2",
		"257-4",
		"257-6",
		"257-8",
		"258",	//*
		"258-2",//*
		"258-4",//*
		"258-6",//*
		"258-8",//*
		"259",	//*
		"259-2",//*
		"259-4",//*
		"259-6",//*
		"259-8",//*
		"260",
		"260-2",
		"260-4",
		"260-6",
		"260-8",
		"261",
		"261-2",
		"261-4",
		"261-6",
		"261-8",
		"262",
		"262-2",
		"262-4",
		"262-6",
		"262-8",
		"265",
		"266",
		"301",
		"302",
		"303",
		"304"
	};
	
	
	public BenchmarkTrack(){
		super();
	}
	
	public BenchmarkTrack(String subTrack){
		super(subTrack);
	}
	
	//The LAUNCH method in the superclass invokes this method
	//remember to invoke launch() to invoke this method
	protected void execute() throws Exception{
		//TRACK PARAMETERS
		//TH and cardinality have to  be set later for each track
		MatchersRegistry matcher = MatchersRegistry.OAEI2009;
		//the parameters are only used in the forth subtrck of the anatomy to keep the name of the partial reference file
		OAEI2009parameters param = new OAEI2009parameters(OAEI2009parameters.BENCHMARKS);
		
		double threshold = 0.6;
		int sourceCardinality = 1;
		int targetCardinality = 1;
		
		
		//decide if running only on one subtracks or all of them
		String[] whichTracks;
		if(subTrack == null || subTrack.equals("")){
			whichTracks = TARGET_ONTOLOGIES;
		}
		else{
			whichTracks = new String[1];
			whichTracks[0] = subTrack;
		}
		
		String currentTarget;
		String targetOntology;
		String sourceUri;
		String targetUri;
		AlignmentSet as;
		AlignmentOutput ao;
		String outputFileDir;
		long startTime = System.nanoTime()/1000000;
		for(int i = 0; i< whichTracks.length; i++){
			currentTarget = whichTracks[i];
			targetOntology = TRACK_INPUT_DIR+currentTarget+"/"+ONTO_NAME;
			//all ontologies are RDF/XML and the TRUE value is because in the benchmark the concepts with different namespace have to be skipped
			//matcher and threshold have to be defined yet
			//the last object are the parameters specific for the choosen matcher, however it may not be needed. Right now as example, I put the base similarity parameters.
			as = computeAlignment(SOURCE_ONTOLOGY, targetOntology, GlobalStaticVariables.LANG_OWL, GlobalStaticVariables.SYNTAX_RDFXML, true, matcher, threshold, sourceCardinality, targetCardinality, param);
			sourceUri = Core.getInstance().getSourceOntology().getURI();
			targetUri = Core.getInstance().getTargetOntology().getURI();
			
			//we need to create the directory first
			outputFileDir = TRACK_OUTPUT_DIR+currentTarget;//the last / is not needed for mkdirs but is needed later
			(new File(outputFileDir)).mkdirs();//create directories
			ao = new AlignmentOutput(as, outputFileDir+"/"+AM_NAME+".rdf");
			ao.write(sourceUri, targetUri, sourceUri, targetUri);
		}
		long endTime = System.nanoTime()/1000000;
		long totTime = endTime - startTime;
		System.out.println("Total execution time in h.m.s.ms: "+Utility.getFormattedTime(totTime));
		String timeFileName = TRACK_OUTPUT_DIR+"executionTime.txt";
		TrackDispatcher.printExecutionTime(totTime, timeFileName);
	}
}
