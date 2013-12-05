package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class MatchingTask extends Model {

	private static final long serialVersionUID = 8618480821358853499L;
	
	@Id
	public Long id;
	
	@Constraints.Required
	public String name;
	
	@Constraints.Required
	public String sourceOntology;
	
	@Constraints.Required
	public String targetOntology;

    public static Model.Finder<Long,MatchingTask> find = 
    		new Model.Finder<Long,MatchingTask>(Long.class, MatchingTask.class);
	
}
