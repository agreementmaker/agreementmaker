package controllers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import models.Client;
import models.MatchingTask;
import models.Ontology;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import views.html.index;
import am.extension.collaborationClient.restful.RESTfulUser;
import am.extension.collaborationClient.restful.RESTfulTask;

import com.fasterxml.jackson.databind.JsonNode;

public class Application extends Controller {

	/**
	 * Initialize the server.
	 * Create the ontologies and the matching tasks.
	 */
	@Transactional
	public static Result initServer() {
		
		final Request r = request();
		
		Chunks<String> chunks = new StringChunks() {
			@Override
			public void onReady(play.mvc.Results.Chunks.Out<String> out) {
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
				
				Ontology[] onts = new Ontology[ontologies.length];
				
				int i = 1;
				
				for( String currentOntology : ontologies ) {
					Ontology o = new Ontology();
					o.ontologyURL = controllers.routes.Assets.at(currentOntology).absoluteURL(r);
					o.save();
					onts[i-1] = o;
					out.write(i++ + " Created ontology " + o.id + ": " + o.ontologyURL + "\n");
				}
				
				MatchingTask m1 = new MatchingTask();
				m1.name = "cmt-Cocus";
				m1.sourceOntologyURL = onts[0].ontologyURL;
				m1.targetOntologyURL = onts[1].ontologyURL;
				
				m1.save();
				
				out.write(i++ + " Created matching task " + m1.id + ": " + m1.name + "\n");
				out.close();
			}
		};
		
		return ok(chunks);
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
			restfulTasks.add(t);
		}
		
		JsonNode taskJson = Json.toJson(restfulTasks);
		return ok(taskJson);
	}
	
}
