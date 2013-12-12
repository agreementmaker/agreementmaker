package controllers;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import models.Client;
import models.MatchingTask;
import models.Ontology;
import models.ServerCandidateMapping;
import models.ServerCandidateMapping.FeedbackType;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import views.html.index;
import am.app.Core;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.app.ontology.ontologyParser.TreeBuilder;
import am.extension.collaborationClient.restful.RESTfulCandidateMapping;
import am.extension.collaborationClient.restful.RESTfulCollaborationServer;
import am.extension.collaborationClient.restful.RESTfulTask;
import am.extension.collaborationClient.restful.RESTfulUser;
import am.extension.multiUserFeedback.MUExperiment;
import am.extension.multiUserFeedback.ServerCandidateSelection;
import am.extension.userfeedback.CandidateSelection;
import am.extension.userfeedback.UFLExperimentSetup;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.LoopInizializationRegistry;
import am.extension.userfeedback.UFLRegistry.PropagationEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.SaveFeedbackRegistry;

import com.fasterxml.jackson.databind.JsonNode;


public class Application extends Controller {
	
	private final static Object syncronizeCandidateSelection = new Object();
	private final static Object syncronizeFeedbackPropagation = new Object();
	
	/**
	 * Initialize the server.
	 * Create the ontologies and the matching tasks.
	 */
	@Transactional
	public static Result initServer() {
		
		final Request r = request();
		
		Chunks<String> chunks = new StringChunks() {
			@Override
			public void onReady(final play.mvc.Results.Chunks.Out<String> out) {
				out.write("Starting server initialization ...\n");
				
				String[] ontologies = {
						"ontologies/cmt.owl",
						"ontologies/Cocus.owl",
						"ontologies/Conference.owl",
						"ontologies/confious.owl",
						"ontologies/confOf.owl",
						"ontologies/crs_dr.owl",
						"ontologies/edas.owl", //6
						"ontologies/ekaw.owl",
						"ontologies/iasted.owl",
						"ontologies/MICRO.owl",
						"ontologies/MyReview.owl", //10
						"ontologies/OpenConf.owl",
						"ontologies/paperdyne.owl",
						"ontologies/PCS.owl",
						"ontologies/sigkdd.owl",
						"ontologies/101.rdf",
						"ontologies/304.rdf"
				};
				
				String[] references={
						"references/cmt-conference.rdf",       // 0
						"references/conference-confOf.rdf",
						"references/confOf-ekaw.rdf",
						"references/ekaw-iasted.rdf",
						"references/cmt-confOf.rdf",     //4
						"references/conference-edas.rdf",
						"references/confOf-iasted.rdf",
						"references/ekaw-sigkdd.rdf",
						"references/cmt-edas.rdf",
						"references/conference-ekaw.rdf", //9
						"references/confOf-sigkdd.rdf",
						"references/iasted-sigkdd.rdf",
						"references/cmt-ekaw.rdf",
						"references/conference-iasted.rdf",
						"references/edas-ekaw.rdf",
						"references/cmt-iasted.rdf",
						"references/conference-sigkdd.rdf",
						"references/edas-iasted.rdf",  // 17
						"references/cmt-sigkdd.rdf",
						"references/confOf-edas.rdf",
						"references/edas-sigkdd.rdf", //20
						"references/101-304.rdf"
				};
				
				Ontology[] onts = new Ontology[ontologies.length];
				
				int i = 1;
				
				for( String currentOntology : ontologies ) {
					Ontology o = new Ontology();
					o.ontologyURL = controllers.routes.Assets.at(currentOntology).absoluteURL(r);
					o.save();
					onts[i-1] = o;
					out.write(i++ + " Created ontology " + o.id + ": " + o.ontologyURL + "\n");
					System.out.println(i + " Created ontology " + o.id + ": " + o.ontologyURL + "\n");
				}
				
				MatchingTask m1 = new MatchingTask();
				m1.index = 0;
				m1.name = "Task 1";  // conference-ekaw
				m1.sourceOntologyURL = onts[2].ontologyURL;
				m1.targetOntologyURL = onts[7].ontologyURL;
				m1.referenceURL = controllers.routes.Assets.at(references[9]).absoluteURL(r);
				
				MatchingTask m2 = new MatchingTask();
				m2.index = 1;
				m2.name = "Task 2";  // 101-304
				m2.sourceOntologyURL = onts[15].ontologyURL;
				m2.targetOntologyURL = onts[16].ontologyURL;
				m2.referenceURL = controllers.routes.Assets.at(references[21]).absoluteURL(r);
				
				MatchingTask m3 = new MatchingTask();
				m3.index = 2;
				m3.name = "Task 3";  // edas-iasted
				m3.sourceOntologyURL = onts[6].ontologyURL;
				m3.targetOntologyURL = onts[8].ontologyURL;
				m3.referenceURL = controllers.routes.Assets.at(references[17]).absoluteURL(r);
				
				m1.save();
				m2.save();
				m3.save();
				out.write(i++ + " Created matching task " + m1.id + "[" + m1.index + "]: " + m1.name + "\n");
				System.out.println("Created matching task " + m1.id + "[" + m1.index + "]: " + m1.name + "\n");
				
				Experiments.experiments = new MUExperiment[3];
				Experiments.cs = new ServerCandidateSelection[3];
				
				Experiments.experiments[0] = new MUExperiment("conference-ekaw");
				Experiments.experiments[1] = new MUExperiment("101-304");
				Experiments.experiments[2] = new MUExperiment("edas-iasted");
				
				for( int j = 0; j < 3; j++ ) {
					Experiments.experiments[j].setup = new UFLExperimentSetup();
					UFLExperimentSetup setup = Experiments.experiments[j].setup;
					
					setup.im  = InitialMatcherRegistry.OrthoCombination;
					setup.fli = LoopInizializationRegistry.ServerDataInizialization;
					setup.cs  = CandidateSelectionRegistry.ServerCandidateSelection;
					setup.cse = null;
					setup.uv  = null;
					setup.fp  = FeedbackPropagationRegistry.ServerFeedbackPropagation;
					setup.pe  = PropagationEvaluationRegistry.ServerPropagationEvaluation;
					setup.sf  = SaveFeedbackRegistry.MultiUserSaveFeedback; 
				}
				
				
				doMatching(out, m1);
				doMatching(out, m2);
				doMatching(out, m3);
				
				out.close();
			}
		};
		
		return ok(chunks);
	}
	
	private static void doMatching(final play.mvc.Results.Chunks.Out<String> out, MatchingTask m1) {
		
		{
			out.write("Loading the source ontology...");
			System.out.println("Loading the source ontology...");
			File sourceOntFile = RESTfulCollaborationServer.downloadFile(m1.sourceOntologyURL, "ont", ".owl");
			OntologyDefinition sourceOntDef = new OntologyDefinition(true, sourceOntFile.getAbsolutePath(), OntologyLanguage.OWL, OntologySyntax.RDFXML);
			try {
				OntoTreeBuilder t = new OntoTreeBuilder(sourceOntDef);
				t.build();
				am.app.ontology.Ontology sourceOnt = t.getOntology();
				Core.getInstance().setSourceOntology(sourceOnt);
			} catch (Exception e1) {
				e1.printStackTrace();
				out.write("Exception while loading the source ontology.");
				System.out.println("Exception while loading the source ontology.");
			}
		}
		
		{
			out.write("Loading the target ontology...");
			System.out.println("Loading the target ontology...");
			File targetOntFile = RESTfulCollaborationServer.downloadFile(m1.targetOntologyURL, "ont", ".owl");
			OntologyDefinition targetOntDef = new OntologyDefinition(true, targetOntFile.getAbsolutePath(), OntologyLanguage.OWL, OntologySyntax.RDFXML);
			TreeBuilder t = TreeBuilder.buildTreeBuilder(targetOntDef);
			try {
				t.build();
			} catch (Exception e1) {
				e1.printStackTrace();
				out.write("Exception while loading the target ontology.");
				System.out.println("Exception while loading the target ontology.");
			}
			am.app.ontology.Ontology targetOnt = t.getOntology();
			Core.getInstance().setTargetOntology(targetOnt);
		}
		
		{
			out.write("Loading the reference alignment...");
			System.out.println("Loading the reference alignment...");
			Experiments.experiments[m1.index].setReferenceAlignment(RESTfulCollaborationServer.getReferenceAlignmentFromURL(m1.referenceURL));
		}
					
		out.write("Running the initial matchers...");
		System.out.println("Running the initial matchers...");
		try {
			Experiments.experiments[m1.index].initialMatcher = Experiments.experiments[m1.index].setup.im.getEntryClass().newInstance();
			Experiments.experiments[m1.index].initialMatcher.run(Experiments.experiments[m1.index]);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			out.write("Exception while running initial matchers.");
		}
		
		out.write("Running the data initialization...");
		System.out.println("Running inizialization...");
		try {
			Experiments.experiments[m1.index].dataInizialization = Experiments.experiments[m1.index].setup.fli.getEntryClass().newInstance();
			Experiments.experiments[m1.index].dataInizialization.inizialize(Experiments.experiments[m1.index]);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			out.write("Exception while running data initialization.");
			System.out.println("Exception while running data initialization.");
		}
		
		System.out.print("Initialization done!");
	}
	
	public static Result getCandidateMapping(String userid) {
		
		final Client currentUser1 = Client.find.byId(Long.parseLong(userid));
		final MatchingTask currentTask = MatchingTask.find.byId((long)currentUser1.taskID);
		
		synchronized(syncronizeCandidateSelection) {
			if( Experiments.experiments[currentTask.index].candidateSelection == null ) {
				Experiments.cs[0] = new ServerCandidateSelection();
				Experiments.experiments[currentTask.index].candidateSelection = (CandidateSelection) Experiments.cs[currentTask.index];
			}
			
			Experiments.cs[currentTask.index].rank(Experiments.experiments[currentTask.index],userid);
		}
		
		Mapping m = Experiments.cs[currentTask.index].getCandidateMapping(userid);
		
		ServerCandidateMapping scm = new ServerCandidateMapping();
		scm.sourceURI = m.getEntity1().getUri();
		scm.targetURI = m.getEntity2().getUri();
		scm.userId = userid;
		scm.timeSent = new java.util.Date();
		scm.save();
		
		RESTfulCandidateMapping restfulMapping = new RESTfulCandidateMapping();
		restfulMapping.setId(scm.id);
		restfulMapping.setSourceURI(scm.sourceURI);
		restfulMapping.setTargetURI(scm.targetURI);
		
		return ok(Json.toJson(restfulMapping));
	}
	
    public static Result index() {
        return ok(index.render());
    }
    
    /**
     * A user registers themselves.
     */
	public static Result register(String taskid) throws JsonGenerationException, JsonMappingException, IOException {
		Client newClient = new Client();
		newClient.taskID = Integer.parseInt(taskid);
		newClient.save();
		
		RESTfulUser newUser = new RESTfulUser();
		newUser.setId(Long.toString(newClient.clientID));
		
		Experiments.experiments[newClient.taskID].login(newUser.getId());
		
		return ok(Json.toJson(newUser));
	}
	
	public static Result listOntologies() {
		List<Ontology> tasks = Ontology.find.all();
		return ok(Json.toJson(tasks));
	}
	
	public static Result listTasks() throws JsonGenerationException, JsonMappingException, IOException {
		List<MatchingTask> tasks = MatchingTask.find.all();
		
		List<RESTfulTask> restfulTasks = new LinkedList<>();
		for( MatchingTask task : tasks ) {
			RESTfulTask t = new RESTfulTask();
			t.setId(task.id);
			t.setName(task.name);
			t.setSourceOntologyURL(task.sourceOntologyURL);
			t.setTargetOntologyURL(task.targetOntologyURL);
			t.setReferenceAlignmentURL(task.referenceURL);
			restfulTasks.add(t); 
		}
		
		JsonNode taskJson = Json.toJson(restfulTasks);
		return ok(taskJson);
	}
	
	public static Result setFeedback(String mappingid, String feedback)
	{
		ServerCandidateMapping m = ServerCandidateMapping.find.byId(Long.parseLong(mappingid));
		final Client currentUser1 = Client.find.byId(Long.parseLong(m.userId));
		final MatchingTask currentTask = MatchingTask.find.byId((long)currentUser1.taskID);

		switch(feedback) {
		case "CORRECT":
			m.feedback = FeedbackType.CORRECT;
			break;
		case "INCORRECT":
			m.feedback = FeedbackType.INCORRECT;
			break;
		case "SKIP":
			m.feedback = FeedbackType.SKIP;
			break;
		case "END_EXPERIMENT":
			m.feedback = FeedbackType.END_EXPERIMENT;
			break;
		}

		m.timeReceived = new java.util.Date();
		m.save();
		
		synchronized (syncronizeFeedbackPropagation) {
			if( Experiments.experiments[currentTask.index].feedbackPropagation == null ) {
				try {
					Experiments.experiments[currentTask.index].feedbackPropagation = Experiments.experiments[currentTask.index].setup.fp.getEntryClass().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			Experiments.experiments[currentTask.index].feedback=feedback;
			Node source=Experiments.experiments[currentTask.index].getSourceOntology().getNodeByURI(m.sourceURI);
			Node target=Experiments.experiments[currentTask.index].getTargetOntology().getNodeByURI(m.targetURI);
			Mapping selectedMapping=new Mapping(source,target,0.0,MappingRelation.EQUIVALENCE);
			Experiments.experiments[currentTask.index].selectedMapping=selectedMapping;
			Experiments.experiments[currentTask.index].feedbackPropagation.propagate(Experiments.experiments[currentTask.index]);
			try {
				Experiments.experiments[currentTask.index].propagationEvaluation = Experiments.experiments[currentTask.index].setup.pe.getEntryClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			Experiments.experiments[currentTask.index].propagationEvaluation.evaluate(Experiments.experiments[currentTask.index]);
		}

		
		return ok("Feedback received.");
	}
	
	public static Result showMappings() {
		List<ServerCandidateMapping> mappings = ServerCandidateMapping.find.all();
		return ok(views.html.mappings.render(mappings));
	}
	
	
}
