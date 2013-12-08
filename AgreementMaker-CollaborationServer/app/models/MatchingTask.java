package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

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
	public String sourceOntologyURL;
	
	@Constraints.Required
	@ManyToMany
	public String targetOntologyURL;

    public static Model.Finder<Long,MatchingTask> find = 
    		new Model.Finder<Long,MatchingTask>(Long.class, MatchingTask.class);
	
}
