package am.app.ontology;

import java.util.EventObject;

/**
 * OntologyChangeEvent
 * 
 * This event describes how an ontology changed. 
 * The source is the ontology that changed.
 * @author cosmin
 *
 */

public class OntologyChangeEvent extends EventObject {

	private static final long serialVersionUID = -6513370159773720996L;
	
	public enum EventType {
		ONTOLOGY_ADDED,		// used when an ontology is added to the Core   
		ONTOLOGY_UPDATED,	// used when the contents of an ontology are changed
		ONTOLOGY_REMOVED,	// used when an ontology is removed from the Core
		SOURCE_ONTOLOGY_CHANGED,	// used when the ontology that is called the "source" is changed
		TARGET_ONTOLOGY_CHANGED,
		NOT_SET
	}
	
	// The Details of this event.
	private EventType typeOfEvent;
	private int ontologyID = 0;
	
	public OntologyChangeEvent( Object s ) {
		super(s);
		typeOfEvent = EventType.NOT_SET;
	}
	
	public OntologyChangeEvent( Object s, EventType t ) {
		super(s);
		typeOfEvent = t;
	}
	
	public OntologyChangeEvent( Object s, EventType t, int id ) {
		super(s);
		typeOfEvent = t;
		ontologyID = id;
	}
	
	public EventType getEvent()      { return typeOfEvent; }
	public int       getOntologyID() { return ontologyID; }

	
}
