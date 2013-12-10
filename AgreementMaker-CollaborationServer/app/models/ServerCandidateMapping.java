package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ServerCandidateMapping extends Model {

	private static final long serialVersionUID = 184274387042579061L;

	@Id
	public Long id;
	
	@Constraints.Required
	public String sourceURI;
	
	@Constraints.Required
	public String targetURI;
}
