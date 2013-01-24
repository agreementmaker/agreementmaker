package am.userInterface;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import am.app.mappingEngine.AbstractMatcher;

public interface MatchingProgressDisplay extends PropertyChangeListener {

	// please NOTE that this class extends PropertyChangeListener
	// Every AbstractMatcher sends a PropertyChangeEvent (via Swingworker!) whenever the "progress" variable is changed.
	
	public void clearReport();
	public void appendToReport( String report );
	public void scrollToEndOfReport(); // used to scroll to the end of the report text area after appending text.
	public void matchingStarted(AbstractMatcher m); // used to let the MatchingProgressDisplay know that the matcher has started working.
	public void matchingComplete(); // used to tell the MatchingProgressDisplay when the matching is complete, and it's safe to close the progress display.
	public void setProgressLabel( String label );  // set the label of the progress
	public void setIndeterminate(boolean indeterminate); // set the progress display in an indeterminate state. 
	public void ignoreComplete(boolean ignore); // ignore the matchingComplete() call.  Used for submatchers.
	
	@Override
	public void propertyChange(PropertyChangeEvent evt);	
	
	// Inherited from PropertyChangeListener.  Used by Swingworker to send progress updates.
	// public void propertyChange(PropertyChangeEvent evt);
}
