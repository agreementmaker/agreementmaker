package am.extension.multiUserFeedback;


import java.io.File;

import am.Utility;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.app.ontology.profiling.manual.ManualOntologyProfiler;
import am.extension.multiUserFeedback.experiment.BMexperiment;
import am.extension.userfeedback.UFLRegistry.CSEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.LoopInizializationRegistry;
import am.extension.userfeedback.UFLRegistry.PropagationEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.SaveFeedbackRegistry;
import am.extension.userfeedback.UFLRegistry.UserValidationRegistry;
import am.extension.userfeedback.experiments.UFLExperimentSetup;
import am.extension.userfeedback.logic.UFLControlLogic;
import am.ui.UIUtility;

public class ExperimentMain {
	final static int clientNumber=10;
	private static String ONTOLOGY_BASE_PATH = "/home/frank/Documents/ontologies/benchmarks/"; // Change ONLY IF REQUIRED
	private static String SOURCE_ONTOLOGY = "101";  // Change this for TESTING
	private static String TARGET_ONTOLOGY = ""; // Change this for TESTING
	private static BMexperiment newExperiment;
	
	public static void main(String arg[])
	{
		
		File folder = new File(ONTOLOGY_BASE_PATH);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) 
		{
		    if (file.isDirectory()) 
		    {
		    	
		    	//load ontology
		    	loadOntology(file);
		    	//run exp
				runExp();
				//save results
				
				//repeat
		    }
		}
		
		
	}
	
	static void runExp(){
		
		try{
			UFLExperimentSetup setup = new UFLExperimentSetup();
		
			setup.im = InitialMatcherRegistry.OrthoCombination;
			setup.fli=  LoopInizializationRegistry.ServerDataInizialization;
			setup.cs = CandidateSelectionRegistry.ServerCandidateSelection;
			setup.cse = CSEvaluationRegistry.PrecisionRecallEval;//not used
			setup.uv = UserValidationRegistry.AutomaticReference;//not used
			setup.fp = FeedbackPropagationRegistry.ServerFeedbackPropagation;
			setup.pe = PropagationEvaluationRegistry.ServerPropagationEvaluation;
			setup.sf= SaveFeedbackRegistry.MultiUserSaveFeedback; 

			newExperiment = new BMexperiment(setup);
			newExperiment.usersNumber = clientNumber;
				
			final UFLControlLogic<BMexperiment> logic = newExperiment.getControlLogic();
				
			Thread thread = new Thread(new Runnable(){

				@Override
				public void run() {
					logic.runExperiment(newExperiment);
				}	
			});
			//thread.start();
			newExperiment.initialMatcher.run(newExperiment);
//			newExperiment..run(newExperiment);
//			for(int i=0;newExperiment.getIterationNumber()<1000;i++)
//			{
//			
//			
//			newExperiment.initialMatcher.run(newExperiment);
//			newExperiment.initialMatcher.run(newExperiment);
//			newExperiment.initialMatcher.run(newExperiment);
//			newExperiment.initialMatcher.run(newExperiment);
//			newExperiment.initialMatcher.run(newExperiment);
//			}
			return;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			UIUtility.displayErrorPane(Utility.UNEXPECTED_ERROR + "\n\n" + ex.getMessage(), Utility.UNEXPECTED_ERROR_TITLE);
		}
	}
	
	static void loadOntology(File file)
	{

    	TARGET_ONTOLOGY=file.getName();

    	System.out.println(TARGET_ONTOLOGY);
		Ontology source = OntoTreeBuilder.loadOWLOntology(ONTOLOGY_BASE_PATH
				+ SOURCE_ONTOLOGY + "/onto.rdf");
		Ontology target = OntoTreeBuilder.loadOWLOntology(ONTOLOGY_BASE_PATH
				+ TARGET_ONTOLOGY + "/onto.rdf");
	
		source.setDescription(SOURCE_ONTOLOGY);
		OntologyDefinition oSource = new OntologyDefinition(true, ONTOLOGY_BASE_PATH
				+ SOURCE_ONTOLOGY + "/onto.rdf", OntologyLanguage.OWL, OntologySyntax.RDFXML);
		OntologyDefinition oTarget = new OntologyDefinition(true, ONTOLOGY_BASE_PATH
				+ TARGET_ONTOLOGY + "/onto.rdf", OntologyLanguage.OWL, OntologySyntax.RDFXML);
		source.setDefinition(oSource);
		target.setDefinition(oTarget);
		ManualOntologyProfiler mop=new ManualOntologyProfiler(source, target);
		

		newExperiment.setSourceOntology(source);
		newExperiment.setTargetOntology(target);
	}

}
