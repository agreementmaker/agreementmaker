package models;

/**
 * http://www.playframework.com/documentation/1.2.1/guide2
 */
import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class Client extends Model {
	private static final long serialVersionUID = -2761715617212700508L;
	
	@Id
	public Long clientID;
	
	public Integer taskID;

	public static Model.Finder<Long,Client> find = 
    		new Model.Finder<Long,Client>(Long.class, Client.class);
}
