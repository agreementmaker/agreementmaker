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
import am.app.ontology.ontologyParser.TreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;
import am.extension.collaborationClient.restful.RESTfulCandidateMapping;
import am.extension.collaborationClient.restful.RESTfulCollaborationServer;
import am.extension.collaborationClient.restful.RESTfulTask;
import am.extension.collaborationClient.restful.RESTfulUser;
import am.extension.multiUserFeedback.MUExperiment;
import am.extension.multiUserFeedback.ServerCandidateSelection;
import am.extension.userfeedback.CandidateSelection;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UFLExperimentSetup;
import am.extension.userfeedback.MLFeedback.MLFeedbackPropagation;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.LoopInizializationRegistry;
import am.extension.userfeedback.UFLRegistry.SaveFeedbackRegistry;

import com.fasterxml.jackson.databind.JsonNode;


public class Application extends Controller {

	private static MUExperiment[] experiments;
	
	private static ServerCandidateSelection[] cs;
	
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
						"ontologies/edas.owl",
						"ontologies/ekaw.owl",
						"ontologies/iasted.owl",
						"ontologies/MICRO.owl",
						"ontologies/MyReview.owl",
						"ontologies/OpenConf.owl",
						"ontologies/paperdyne.owl",
						"ontologies/PCS.owl",
						"ontologies/sigkdd.owl"
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
						"references/edas-iasted.rdf",
						"references/cmt-sigkdd.rdf",
						"references/confOf-edas.rdf",
						"references/edas-sigkdd.rdf"
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
				m1.name = "conference-ekaw";
				m1.sourceOntologyURL = onts[2].ontologyURL;
				m1.targetOntologyURL = onts[7].ontologyURL;
				m1.referenceURL = controllers.routes.Assets.at(references[9]).absoluteURL(r);
				
				m1.save();
				out.write(i++ + " Created matching task " + m1.id + ": " + m1.name + "\n");
				System.out.println(i + " Created matching task " + m1.id + ": " + m1.name + "\n");
				
				experiments = new MUExperiment[1];
				cs = new ServerCandidateSelection[1];
				
				experiments[0] = new MUExperiment();
				
				experiments[0].setup = new UFLExperimentSetup();
				UFLExperimentSetup setup = experiments[0].setup;
				
				setup.im  = InitialMatcherRegistry.OrthoCombination;
				setup.fli = LoopInizializationRegistry.MUDataInizialization;
				setup.cs  = CandidateSelectionRegistry.ServerCandidateSelection;
				setup.cse = null;
				setup.uv  = null;
				setup.fp  = FeedbackPropagationRegistry.MUFeedbackPropagation;
				setup.pe  = null;
				setup.sf  = SaveFeedbackRegistry.MultiUserSaveFeedback; 
				
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
					System.out.println("Loading the source ontology...");
					File targetOntFile = RESTfulCollaborationServer.downloadFile(m1.targetOntologyURL, "ont", ".owl");
					OntologyDefinition targetOntDef = new OntologyDefinition(true, targetOntFile.getAbsolutePath(), OntologyLanguage.OWL, OntologySyntax.RDFXML);
					TreeBuilder t = TreeBuilder.buildTreeBuilder(targetOntDef);
					try {
						t.build();
					} catch (Exception e1) {
						e1.printStackTrace();
						out.write("Exception while loading the source ontology.");
						System.out.println("Exception while loading the target ontology.");
					}
					am.app.ontology.Ontology targetOnt = t.getOntology();
					Core.getInstance().setTargetOntology(targetOnt);
				}
							
				out.write("Running the initial matchers...");
				System.out.println("Running the initial matchers...");
				try {
					experiments[0].initialMatcher = experiments[0].setup.im.getEntryClass().newInstance();
					experiments[0].initialMatcher.run(experiments[0]);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
					out.write("Exception while running initial matchers.");
				}
				
				out.write("Running the data initialization...");
				System.out.println("Running the initial matchers...");
				try {
					experiments[0].dataInizialization = experiments[0].setup.fli.getEntryClass().newInstance();
					experiments[0].dataInizialization.inizialize(experiments[0]);
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
					out.write("Exception while running data initialization.");
					System.out.println("Exception while running data initialization.");
				}
				
				System.out.print("Initialization done!");
				out.close();
			}
		};
		
		return ok(chunks);
	}
	
	public static Result getCandidateMapping(String id) {
		
		synchronized(syncronizeCandidateSelection) {
			if( experiments[0].candidateSelection == null ) {
				cs[0] = new ServerCandidateSelection();
				experiments[0].candidateSelection = (CandidateSelection) cs[0];
			}
			
			cs[0].rank(experiments[0]);
		}
		
		Mapping m = cs[0].getCandidateMapping(id);
		
		ServerCandidateMapping scm = new ServerCandidateMapping();
		scm.sourceURI = m.getEntity1().getUri();
		scm.targetURI = m.getEntity2().getUri();
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
	public static Result register() throws JsonGenerationException, JsonMappingException, IOException {
		Client newClient = new Client();
		newClient.save();
		
		RESTfulUser newUser = new RESTfulUser();
		newUser.setId(Long.toString(newClient.clientID));
		
		experiments[0].login(newUser.getId());
		
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
	
	public static Result setFeedback(String id, String feedback)
	{
		ServerCandidateMapping m = ServerCandidateMapping.find.byId(Long.parseLong(id));

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
			if( experiments[0].feedbackPropagation == null ) {
				try {
					experiments[0].feedbackPropagation = experiments[0].setup.fp.getEntryClass().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			experiments[0].feedback=feedback;
			Node source=experiments[0].getSourceOntology().getNodeByURI(m.sourceURI);
			Node target=experiments[0].getTargetOntology().getNodeByURI(m.targetURI);
			Mapping selectedMapping=new Mapping(source,target,0.0,MappingRelation.EQUIVALENCE);
			experiments[0].selectedMapping=selectedMapping;
			experiments[0].feedbackPropagation.propagate(experiments[0]);
		}

		
		return ok("Feedback received.");
	}
	
	public static Result showMappings() {
		List<ServerCandidateMapping> mappings = ServerCandidateMapping.find.all();
		return ok(views.html.mappings.render(mappings));
	}
	
	
}
