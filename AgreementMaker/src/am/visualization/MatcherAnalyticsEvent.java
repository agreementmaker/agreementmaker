package am.visualization;

import java.util.EventObject;

public class MatcherAnalyticsEvent extends EventObject {

	private static final long serialVersionUID = 4508844257680133138L;

	public static enum EventType {
		SELECT_MAPPING,  // implies that the payload is a Point, representing the row, and column of the mapping.
		SET_REFERENCE	 // Set this matcher as the reference matcher.  Implies that the payload is an AbstractMatcher.
	}
	
	public final EventType type;
	public final Object payload;
	
	public MatcherAnalyticsEvent(Object source, EventType type, Object payload) {
		super(source);
		this.type = type;
		this.payload = payload;
	}
	
	
	
}
