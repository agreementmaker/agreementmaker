package controllers;

import java.util.List;

import models.MatchingTask;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }
    
    /**
     * A user registers themselves.
     * @return
     */
	public static Result register() {
		ObjectNode result = Json.newObject();
		result.put("clientid", "testID_0001");
		return ok(result);
	}
	
	public static Result createTask() {
		MatchingTask newTask = new MatchingTask();
		newTask.name = "TestTask 01";
		newTask.sourceOntology = "source.owl";
		newTask.targetOntology = "target.owl";
		
		newTask.save();
		
		return ok("Created Task " + newTask.id);
	}
	
	public static Result listTasks() {
		ObjectNode result = Json.newObject();
		List<MatchingTask> tasks = MatchingTask.find.all();
		JsonNode taskList = Json.toJson(tasks);
		result.put("tasks",taskList);
		return ok(result);
	}
	
}
