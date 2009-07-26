package am.batchMode;

import am.GlobalStaticVariables;
import am.application.Core;
import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.AbstractParameters;
import am.application.mappingEngine.AlignmentSet;
import am.application.mappingEngine.MatcherFactory;
import am.application.mappingEngine.MatchersRegistry;
import am.application.ontology.ontologyParser.OntoTreeBuilder;
import am.application.ontology.ontologyParser.TreeBuilder;
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
		OntoTreeBuilder sourceBuilder = new OntoTreeBuilder(sourcePath, GlobalStaticVariables.SOURCENODE, languageS, syntaxS, skip);
		sourceBuilder.build();
		//System.out.println(sourceBuilder.getReport());
		//Target Ontology
		OntoTreeBuilder targetBuilder = new OntoTreeBuilder(targetPath, GlobalStaticVariables.TARGETNODE, languageS, syntaxS, skip);
		targetBuilder.build();
		//System.out.println(sourceBuilder.getReport());
		
		//Set the ontologies in the Core structure which is common to all matchers
		Core.getInstance().setSourceOntology(sourceBuilder.getOntology());
		Core.getInstance().setTargetOntology(targetBuilder.getOntology());
		
		//Invoke the matcher, any index is fine
		AbstractMatcher currentMatcher = MatcherFactory.getMatcherInstance(matcher, 0);
		currentMatcher.setThreshold(threshold);
		currentMatcher.setMaxSourceAlign(sourceRel);
		currentMatcher.setMaxTargetAlign(targetRel);
		if(currentMatcher.needsParam()){
			currentMatcher.setParam(parameters);
		}
		currentMatcher.match();
		return currentMatcher.getAlignmentSet();
	}

}
