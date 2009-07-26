package am.batchMode;

import java.io.File;

import sun.awt.windows.ThemeReader;
import am.GlobalStaticVariables;
import am.application.Core;
import am.application.mappingEngine.AlignmentSet;
import am.application.mappingEngine.MatchersRegistry;
import am.application.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.output.AlignmentOutput;

public class BenchmarkTrack extends Track{
	
	//DIRECTORIES
	public final static String AM_NAME = "AgreementMaker";
	public final static String OAEI_DIR = "./OAEI09/";
	public final static String OAEI_OUTPUT_DIR = OAEI_DIR+AM_NAME+"/";
	public final static String TRACK_DIR = "benchmarks/";
	public final static String TRACK_INPUT_DIR = OAEI_DIR+TRACK_DIR;
	public final static String TRACK_OUTPUT_DIR = OAEI_OUTPUT_DIR+TRACK_DIR;
	
	//ONTOLOGIES
	public final static String ONTO_NAME = "onto.rdf";
	public final static String SOURCE_ONTOLOGY = TRACK_INPUT_DIR+"101/"+ONTO_NAME;
	
	/*
	public final static String[] TARGET_ONTOLOGIES = {
		"101",
		"102"
	};
	*/
	
	
	public final static String[] TARGET_ONTOLOGIES = {
		//I DOWNLOADED this from the zip file in the OAEI2009 benchmarks website
		"101",
		"102",
		"103",
		"104",
		"201",
		"202",
		"203",//in the website it's written that it doesn't exist, but it does in the zip file.
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
		//"224", I don't why but it doesn't want to open this
		"225",
		//"226",  doesn't exist
		//"227",  doesn't exist
		"228",
		"229",
		"230",
		"231",  //in the website it's written that it doesn't exist, but it does in the zip file.
		"232",
		"233",
		"236",
		"237",
		"238",
		"239",
		"240",
		"241",
		"246",
		"247",
		"248",
		"249",
		"250",
		"251",
		"252",
		"253",
		"254",
		"257",
		"258",
		"259",
		"260",
		"261",
		"262",
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
		MatchersRegistry matcher = MatchersRegistry.BaseSimilarity;
		double threshold = 0.7;
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
		for(int i = 0; i< whichTracks.length; i++){
			currentTarget = whichTracks[i];
			targetOntology = TRACK_INPUT_DIR+currentTarget+"/"+ONTO_NAME;
			//all ontologies are OWL - RDF/XML and the TRUE value is because in the benchmark the concepts with different namespace have to be skipped
			//matcher and threshold have to be defined yet
			//the last object are the parameters specific for the choosen matcher, however it may not be needed. Right now as example, I put the base similarity parameters.
			as = computeAlignment(SOURCE_ONTOLOGY, targetOntology, GlobalStaticVariables.LANG_OWL, GlobalStaticVariables.SYNTAX_RDFXML, true, matcher, threshold, sourceCardinality, targetCardinality, new BaseSimilarityParameters());
			sourceUri = Core.getInstance().getSourceOntology().getURI();
			targetUri = Core.getInstance().getTargetOntology().getURI();
			
			//we need to create the directory first
			outputFileDir = TRACK_OUTPUT_DIR+currentTarget;//the last / is not needed for mkdirs but is needed later
			(new File(outputFileDir)).mkdirs();//create directories
			ao = new AlignmentOutput(as, outputFileDir+"/"+AM_NAME+".rdf");
			ao.write(sourceUri, targetUri, sourceUri, targetUri);
		}
	}
}
