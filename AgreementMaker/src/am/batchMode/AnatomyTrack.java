package am.batchMode;

import java.io.File;

import sun.awt.windows.ThemeReader;
import am.GlobalStaticVariables;
import am.application.Core;
import am.application.mappingEngine.AlignmentSet;
import am.application.mappingEngine.MatchersRegistry;
import am.application.mappingEngine.baseSimilarity.BaseSimilarityParameters;
import am.output.AlignmentOutput;

public class AnatomyTrack extends Track{
	
	//DIRECTORIES
	public final static String AM_NAME = "AgreementMaker";
	public final static String OAEI_DIR = "./OAEI09/";
	public final static String OAEI_OUTPUT_DIR = OAEI_DIR+AM_NAME+"/";
	public final static String TRACK_DIR = "anatomy/";
	public final static String TRACK_INPUT_DIR = OAEI_DIR+TRACK_DIR;
	public final static String TRACK_OUTPUT_DIR = OAEI_OUTPUT_DIR+TRACK_DIR;
	
	//ONTOLOGIES
	public final static String SOURCE_ONTOLOGY = TRACK_INPUT_DIR+"mouse_anatomy_2008.txt";
	public final static String TARGET_ONTOLOGY = TRACK_INPUT_DIR+"nci_anatomy_2008.txt";
	//the partial reference name has to be added to the matcher parameters
	public final static String PARTIAL_REFERENCE = TRACK_INPUT_DIR+"reference_partial_2008.rdf";
	
	public final static String TRACK_1 = "1";
	public final static String TRACK_2 = "2";
	public final static String TRACK_3 = "3";
	public final static String TRACK_4 = "4";
	
	
	
	public AnatomyTrack(){
		super();
	}
	
	public AnatomyTrack(String subTrack){
		super(subTrack);
	}
	
	//The LAUNCH method in the superclass invokes this method
	//remember to invoke launch() to invoke this method
	protected void execute() throws Exception{
		//TRACK PARAMETERS
		//TH and cardinality have to  be set later for each track
		MatchersRegistry matcher = MatchersRegistry.BaseSimilarity;
		
		//decide if running only on one subtracks or all of them
		boolean doAll = false;
		if(subTrack == null || subTrack.equals("")){
			doAll = true;
		}
		if(doAll||subTrack.equals(TRACK_1)){
			//subtrack 1 improve F-measure
			double threshold = 0.60;
			int sourceCardinality = 1;
			int targetCardinality = 1;
			AlignmentSet as = computeAlignment(SOURCE_ONTOLOGY, TARGET_ONTOLOGY, GlobalStaticVariables.LANG_OWL, GlobalStaticVariables.SYNTAX_RDFXML, false, matcher, threshold, sourceCardinality, targetCardinality, new BaseSimilarityParameters());
			String sourceUri = Core.getInstance().getSourceOntology().getURI();
			String targetUri = Core.getInstance().getTargetOntology().getURI();
			//we need to create the directory first
			String outputFileDir = TRACK_OUTPUT_DIR+"TRACK_1";//the last / is not needed for mkdirs but is needed later
			(new File(outputFileDir)).mkdirs();//create directories
			AlignmentOutput ao = new AlignmentOutput(as, outputFileDir+"/"+AM_NAME+".rdf");
			ao.write(sourceUri, targetUri, sourceUri, targetUri);
		}
		if(doAll||subTrack.equals(TRACK_2)){
			//subtrack 2 improve Precision
			double threshold = 0.75;
			int sourceCardinality = 1;
			int targetCardinality = 1;
			AlignmentSet as = computeAlignment(SOURCE_ONTOLOGY, TARGET_ONTOLOGY, GlobalStaticVariables.LANG_OWL, GlobalStaticVariables.SYNTAX_RDFXML, false, matcher, threshold, sourceCardinality, targetCardinality, new BaseSimilarityParameters());
			String sourceUri = Core.getInstance().getSourceOntology().getURI();
			String targetUri = Core.getInstance().getTargetOntology().getURI();
			//we need to create the directory first
			String outputFileDir = TRACK_OUTPUT_DIR+"TRACK_2";//the last / is not needed for mkdirs but is needed later
			(new File(outputFileDir)).mkdirs();//create directories
			AlignmentOutput ao = new AlignmentOutput(as, outputFileDir+"/"+AM_NAME+".rdf");
			ao.write(sourceUri, targetUri, sourceUri, targetUri);
		}
		if(doAll||subTrack.equals(TRACK_3)){
			//subtrack 3 improve Recall
			double threshold = 0.40;
			int sourceCardinality = 1;
			int targetCardinality = 1;
			AlignmentSet as = computeAlignment(SOURCE_ONTOLOGY, TARGET_ONTOLOGY, GlobalStaticVariables.LANG_OWL, GlobalStaticVariables.SYNTAX_RDFXML, false, matcher, threshold, sourceCardinality, targetCardinality, new BaseSimilarityParameters());
			String sourceUri = Core.getInstance().getSourceOntology().getURI();
			String targetUri = Core.getInstance().getTargetOntology().getURI();
			//we need to create the directory first
			String outputFileDir = TRACK_OUTPUT_DIR+"TRACK_3";//the last / is not needed for mkdirs but is needed later
			(new File(outputFileDir)).mkdirs();//create directories
			AlignmentOutput ao = new AlignmentOutput(as, outputFileDir+"/"+AM_NAME+".rdf");
			ao.write(sourceUri, targetUri, sourceUri, targetUri);
		}
		if(doAll||subTrack.equals(TRACK_4)){
			//subtrack 1 using partial reference
			double threshold = 0.60;
			int sourceCardinality = 1;
			int targetCardinality = 1;
			//the partial reference filename has to be set in the method parameters
			AlignmentSet as = computeAlignment(SOURCE_ONTOLOGY, TARGET_ONTOLOGY, GlobalStaticVariables.LANG_OWL, GlobalStaticVariables.SYNTAX_RDFXML, false, matcher, threshold, sourceCardinality, targetCardinality, new BaseSimilarityParameters());
			String sourceUri = Core.getInstance().getSourceOntology().getURI();
			String targetUri = Core.getInstance().getTargetOntology().getURI();
			//we need to create the directory first
			String outputFileDir = TRACK_OUTPUT_DIR+"TRACK_4";//the last / is not needed for mkdirs but is needed later
			(new File(outputFileDir)).mkdirs();//create directories
			AlignmentOutput ao = new AlignmentOutput(as, outputFileDir+"/"+AM_NAME+".rdf");
			ao.write(sourceUri, targetUri, sourceUri, targetUri);
		}
	}
}
