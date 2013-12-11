package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class ServerCandidateMapping extends Model {

	private static final long serialVersionUID = 184274387042579061L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) // the id is unique only per type hierarchy
	public Long id;
	
	@Constraints.Required
	public String sourceURI;
	
	@Constraints.Required
	public String targetURI;

	@Temporal(TemporalType.TIMESTAMP)
	public Date timeSent;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date timeReceived;
	
	public enum FeedbackType { CORRECT, INCORRECT, SKIP, END_EXPERIMENT }
	
	@Enumerated(EnumType.STRING)
	public FeedbackType feedback;
	
	public static Model.Finder<Long,ServerCandidateMapping> find = 
    		new Model.Finder<Long,ServerCandidateMapping>(Long.class, ServerCandidateMapping.class);
}
