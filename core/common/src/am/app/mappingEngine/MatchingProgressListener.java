package am.app.mappingEngine;

import java.beans.PropertyChangeListener;

/**
 * 
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 * 
 * @version Oct 20, 2013
 *
 */
public interface MatchingProgressListener extends PropertyChangeListener {

	/**
	 * Used to let the MatchingProgressListener that the macthing has started.
	 * 
	 * @param matcher
	 */
	public void matchingStarted( AbstractMatcher matcher );
	
	/**
	 * Used to tell the MatchingProgressDisplay when the matching is complete,
	 * and it's safe to close the progress display.
	 */
	public void matchingComplete();
	
	public void clearReport();
	
	public void appendToReport( String report );
	
	public void scrollToEndOfReport(); // used to scroll to the end of the report text area after appending text.
	
	public void setProgressLabel( String label );  // set the label of the progress
	public void setIndeterminate(boolean indeterminate); // set the progress display in an indeterminate state. 
	public void ignoreComplete(boolean ignore); // ignore the matchingComplete() call.  Used for submatchers.
	
}
