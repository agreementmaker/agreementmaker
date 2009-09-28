package am.batchMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import am.GlobalStaticVariables;
import am.application.Core;
import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.AbstractParameters;
import am.application.mappingEngine.Alignment;
import am.application.mappingEngine.AlignmentSet;
import am.application.mappingEngine.MatcherFactory;
import am.application.mappingEngine.MatchersRegistry;
import am.application.ontology.Ontology;
import am.application.ontology.ontologyParser.OntoTreeBuilder;
import am.application.ontology.ontologyParser.TreeBuilder;
import am.batchMode.conflictResolution.ConflictsResolution;
import am.batchMode.conflictResolution.VotedMapping;
import am.batchMode.conflictResolution.VotedMappingSet;
import am.userInterface.OntologyLoadingProgressDialog;

public abstract class Track {
	
	protected String subTrack;
	protected long executionTime;
	
	public long getExecutionTime() {
		return executionTime;
	}

	public Track(){
		subTrack = "";
	}
	
	public Track(String subTrack){
		this.subTrack = subTrack;
	}
	
	public void launch() throws Exception{
		long milliSec = 1000000;
		long startTime = System.nanoTime()/milliSec;
		execute();
		long endTime = System.nanoTime()/milliSec;
		long diffTime = endTime - startTime;
		executionTime = diffTime;
	}

	protected void execute() throws Exception{
		// To be implemented in the subClass
	}
	
	//compute the alignment between any two ontologies, given their filepath using the specified matcher
	protected AlignmentSet computeAlignment(String sourcePath, String targetPath, String languageS, String syntaxS, boolean skip, MatchersRegistry matcher, double threshold, int sourceRel, int targetRel, AbstractParameters parameters) throws Exception{
		System.out.println("The matching process is started between:\nSource Ontology: "+sourcePath+"\nTarget Ontology: "+targetPath);
		
		//LOADING ONTOLOGIES
		//Source Ontology
		//Ontotype = GlobalstaticVariable.SOURCENODE is irrelevant
		System.out.println("Loading source ontology");
		OntoTreeBuilder sourceBuilder = new OntoTreeBuilder(sourcePath, GlobalStaticVariables.SOURCENODE, languageS, syntaxS, skip);
		sourceBuilder.build();
		//System.out.println(sourceBuilder.getReport());
		//Target Ontology
		System.out.println("Loading target ontology");
		OntoTreeBuilder targetBuilder = new OntoTreeBuilder(targetPath, GlobalStaticVariables.TARGETNODE, languageS, syntaxS, skip);
		targetBuilder.build();
		//System.out.println(sourceBuilder.getReport());
		
		AbstractMatcher result = matchTwoOntologies(sourceBuilder.getOntology(), targetBuilder.getOntology(), matcher, threshold, sourceRel, targetRel, parameters);
		return result.getAlignmentSet();
		
		/*Modified so in order to have this part of the code in a separate method: matchTwoOntologies()
		//Set the ontologies in the Core structure which is common to all matchers
		Core.getInstance().setSourceOntology(sourceBuilder.getOntology());
		Core.getInstance().setTargetOntology(targetBuilder.getOntology());
		
		//Invoke the matcher, any index is fine
		System.out.println("Running the matching method: "+matcher.getMatcherName());
		AbstractMatcher currentMatcher = MatcherFactory.getMatcherInstance(matcher, 0);
		currentMatcher.setThreshold(threshold);
		currentMatcher.setMaxSourceAlign(sourceRel);
		currentMatcher.setMaxTargetAlign(targetRel);
		currentMatcher.setParam(parameters);
		currentMatcher.match();
		System.out.println("Matching method completed in "+currentMatcher.getExecutionTime());
		
		return currentMatcher.getAlignmentSet();
		*/
		
	}

	private AbstractMatcher matchTwoOntologies(Ontology sourceOntology,
			Ontology targetOntology, MatchersRegistry matcher,
			double threshold, int sourceRel, int targetRel,
			AbstractParameters parameters) throws Exception {
		
		//Set the ontologies in the Core structure which is common to all matchers
		Core.getInstance().setSourceOntology(sourceOntology);
		Core.getInstance().setTargetOntology(targetOntology);
		
		//Invoke the matcher, any index is fine
		System.out.println("Running the matching method: "+matcher.getMatcherName());
		AbstractMatcher currentMatcher = MatcherFactory.getMatcherInstance(matcher, 0);
		currentMatcher.setThreshold(threshold);
		currentMatcher.setMaxSourceAlign(sourceRel);
		currentMatcher.setMaxTargetAlign(targetRel);
		currentMatcher.setParam(parameters);
		currentMatcher.match();
		System.out.println("Matching method completed in "+currentMatcher.getExecutionTime());
		
		return currentMatcher;
	}
	
	
	// wrapper function
	protected ArrayList<AbstractMatcher> computeMultipleAlignment(boolean solveConflicts, File[] ontologyFiles, String languageS, String syntaxS, boolean skip, MatchersRegistry matcher, double threshold, int sourceRel, int targetRel, AbstractParameters parameters  ) throws Exception{
		return computeMultipleAlignment(solveConflicts, ontologyFiles, languageS, syntaxS, skip, matcher, threshold, sourceRel, targetRel, parameters, OntoTreeBuilder.Profile.defaultProfile );  // if no profile is specified, it uses the default profile
	}
	
	//compute the alignment between any two ontologies, given their filepath using the specified matcher
	protected ArrayList<AbstractMatcher> computeMultipleAlignment(boolean solveConflicts, File[] ontologyFiles, String languageS, String syntaxS, boolean skip, MatchersRegistry matcher, double threshold, int sourceRel, int targetRel, AbstractParameters parameters, OntoTreeBuilder.Profile loadingProfile) throws Exception{
		int numOntologies = ontologyFiles.length;
		int numAlignments = (numOntologies * (numOntologies - 1))/2;
		System.out.println("The matching process is started between "+numOntologies+" ontologies.\nExpected "+numAlignments+" different sets of mappings.");
		
		//LOADING ONTOLOGIES
		//Ontologies will be both source and target so Ontotype = GlobalstaticVariable.SOURCENODE is irrelevant
		Ontology[] ontologies = new Ontology[numOntologies];
		for(int i= 0; i< numOntologies; i++){
			File f = ontologyFiles[i];
			OntoTreeBuilder builder = new OntoTreeBuilder(f.getAbsolutePath(), GlobalStaticVariables.SOURCENODE, languageS, syntaxS, skip);
			builder.build( loadingProfile );
			Ontology o = builder.getOntology();
			o.setIndex(i);//used to identify the ontology to print the alignment file
			ontologies[i] = o;
			//System.out.println(sourceBuilder.getReport());
		}
		
		
		// compare each ontology to every other ontology
		ArrayList<AbstractMatcher> finalMatchers = new ArrayList<AbstractMatcher>(numAlignments);
		for( int i = 0; i < numOntologies - 1; i++ ) {		
			for( int j = i+1; j < numOntologies; j++ ) {
				AbstractMatcher a = matchTwoOntologies(ontologies[i], ontologies[j], matcher, threshold, sourceRel, targetRel, parameters);
				finalMatchers.add(a);
			}
		}
		
		if(solveConflicts){
			ConflictsResolution conf = new ConflictsResolution();
			//First solve on classes then on properties
			finalMatchers = conf.solveConflicts(finalMatchers, ontologies, true);//classes
			finalMatchers = conf.solveConflicts(finalMatchers, ontologies, false);//properties
		}
		return finalMatchers;
	}

	
	
	
	
	

}
