package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints;
import play.db.ebean.Model;

@Entity
public class Ontology extends Model {
	
	private static final long serialVersionUID = -4724784290719292299L;

	@Id
	public Long id;
	
	@Constraints.Required
	public String ontologyURL;
	
	public static Model.Finder<Long,Ontology> find = 
    		new Model.Finder<Long,Ontology>(Long.class, Ontology.class);
}
