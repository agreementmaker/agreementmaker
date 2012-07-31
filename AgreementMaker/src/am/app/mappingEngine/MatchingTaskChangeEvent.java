package am.app.mappingEngine;

import java.util.EventObject;

import am.app.Core;

public class MatchingTaskChangeEvent extends EventObject {

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
	private int taskID = 0;
	
	@Deprecated
	public MatchingTaskChangeEvent( AbstractMatcher s ) {
		super(s);
		typeOfEvent = EventType.NOT_SET;
		taskID = s.getID();
	}
	
	@Deprecated
	public MatchingTaskChangeEvent( AbstractMatcher s, EventType t ) {
		super(s);
		typeOfEvent = t;
		taskID = s.getID();
	}
	
	@Deprecated
	public MatchingTaskChangeEvent( AbstractMatcher s, EventType t, int id ) {
		super(s);
		typeOfEvent = t;
		taskID = id;
	}
	
	@Deprecated
	public MatchingTaskChangeEvent( MatcherResult s ) {
		super(s);
		typeOfEvent = EventType.NOT_SET;
		taskID = s.getMatchingTask().matchingAlgorithm.getID();
	}
	
	@Deprecated
	public MatchingTaskChangeEvent( MatcherResult s, EventType t ) {
		super(s);
		typeOfEvent = t;
		taskID = s.getMatchingTask().matchingAlgorithm.getID();
	}
	
	@Deprecated
	public MatchingTaskChangeEvent( MatcherResult s, EventType t, int id ) {
		super(s);
		typeOfEvent = t;
		taskID = id;
	}
	
	public MatchingTaskChangeEvent( MatchingTask task, EventType t ) {
		super(task);
		typeOfEvent = t;
		taskID = task.ID;
	}
	
	public MatchingTaskChangeEvent( EventType t ) {
		super(new MatchingTask(null, null, null, null));  // source cannot be null
		typeOfEvent = t;
		taskID = Core.ID_NONE;
	}
	
	public EventType getEvent()      { return typeOfEvent; }
	public int       getTaskID() { return taskID; }
	public AbstractMatcher getMatcher() { return (AbstractMatcher) source; }
	

	
}
