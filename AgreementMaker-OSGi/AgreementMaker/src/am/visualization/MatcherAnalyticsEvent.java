package am.visualization;

import java.util.EventObject;

public class MatcherAnalyticsEvent extends EventObject {

	private static final long serialVersionUID = 4508844257680133138L;

	public static enum EventType {
		SELECT_MAPPING,  // implies that the payload is a Point, representing the row, and column of the mapping.
		SET_REFERENCE,	 // Set this matcher as the reference matcher.  Implies that the payload is an AbstractMatcher.
		MATRIX_UPDATED,   // when a matrix is updated
		DISPLAY_CLUSTER,	  // display a cluster, payload is inferred to be the cluster
		CLEAR_CLUSTER,		// clear the cluster display
		REMOVE_PLOT,			// remove the plot from the analytics panel
		SET_FEEDBACK,		// set the feedback matcher that will be updated when the user generates feedback (payload == the matcher)
		VIEW_ORDERED_PLOT	// event is fired when adding the ordered matrix plot (payload == MatrixPlotPanel of the unordered plot)
	}
	
	public final EventType type;
	public final Object payload;
	
	public MatcherAnalyticsEvent(Object source, EventType type, Object payload) {
		super(source);
		this.type = type;
		this.payload = payload;
	}
	
	
	
}
