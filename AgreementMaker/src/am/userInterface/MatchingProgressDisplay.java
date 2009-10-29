package am.userInterface;

import java.beans.PropertyChangeListener;

public interface MatchingProgressDisplay extends PropertyChangeListener {

	public void appendToReport( String report );
	public void matchingComplete();
}
