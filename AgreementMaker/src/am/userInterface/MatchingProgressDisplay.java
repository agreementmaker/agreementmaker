package am.userInterface;

import java.beans.PropertyChangeListener;

public interface MatchingProgressDisplay extends PropertyChangeListener {

	// please NOTE that this class extends PropertyChangeListener
	// Every AbstractMatcher sends a PropertyChangeEvent (via Swingworker!) whenever the "progress" variable is changed.
	
	public void appendToReport( String report );
	public void matchingStarted(); // used to let the MatchingProgressDisplay know that the matcher has started working.
	public void matchingComplete(); // used to tell the MatchingProgressDisplay when the matching is complete, and it's safe to close the progress display.
	
	// Inherited from PropertyChangeListener.  Used by Swingworker to send progress updates.
	// public void propertyChange(PropertyChangeEvent evt);
}
