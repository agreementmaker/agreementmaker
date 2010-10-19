package am.visualization;

/**
 * This interface is for objects that act as a dispatch for messages between MatrixPlot objects.
 * This allows the user to select the same mapping on multiple MatrixPlot objects, just by clicking
 * on one of the MatrixPlot objects.
 * @author cosmin
 *
 */
public interface MatcherAnalyticsEventDispatch {

	public void broadcastEvent( MatcherAnalyticsEvent e );
	
}
