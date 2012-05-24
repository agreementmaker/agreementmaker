package am.app.mappingEngine;

import java.util.EventObject;

public class MatcherChangeEvent extends EventObject {

	private static final long serialVersionUID = -6513370159773720996L;
	
	public enum EventType {
		MATCHER_ADDED,		// used when a matcher is added to the core   
		MATCHER_ALIGNMENTSET_UPDATED,	// used when the alignment set has changed
		MATCHER_VISIBILITY_CHANGED, // used when the matcher is toggled on and off from the control panel
		MATCHER_REMOVED,	// used when a matcher is removed from the Core
		MATCHER_COLOR_CHANGED, // used when a matcher's color is changed
		REMOVE_ALL,
		NOT_SET
	}
	
	// The Details of this event.
	private EventType typeOfEvent;
	private int matcherID = 0;
	
	@Deprecated
	public MatcherChangeEvent( AbstractMatcher s ) {
		super(s);
		typeOfEvent = EventType.NOT_SET;
		matcherID = s.getID();
	}
	
	@Deprecated
	public MatcherChangeEvent( AbstractMatcher s, EventType t ) {
		super(s);
		typeOfEvent = t;
		matcherID = s.getID();
	}
	
	@Deprecated
	public MatcherChangeEvent( AbstractMatcher s, EventType t, int id ) {
		super(s);
		typeOfEvent = t;
		matcherID = id;
	}
	
	@Deprecated
	public MatcherChangeEvent( MatcherResult s ) {
		super(s);
		typeOfEvent = EventType.NOT_SET;
		matcherID = s.getID();
	}
	
	@Deprecated
	public MatcherChangeEvent( MatcherResult s, EventType t ) {
		super(s);
		typeOfEvent = t;
		matcherID = s.getID();
	}
	
	@Deprecated
	public MatcherChangeEvent( MatcherResult s, EventType t, int id ) {
		super(s);
		typeOfEvent = t;
		matcherID = id;
	}
	
	public MatcherChangeEvent( MatchingTask task, EventType t ) {
		super(task);
		typeOfEvent = t;
		matcherID = task.ID;
	}
	
	public EventType getEvent()      { return typeOfEvent; }
	public int       getMatcherID() { return matcherID; }
	public AbstractMatcher getMatcher() { return (AbstractMatcher) source; }
	

	
}
