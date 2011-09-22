package am.userInterface.console;

import java.beans.PropertyChangeEvent;

import org.apache.log4j.Logger;

import am.app.mappingEngine.AbstractMatcher;
import am.userInterface.MatchingProgressDisplay;

public class ConsoleProgressDisplay implements MatchingProgressDisplay {
	
	private Logger log;
	private boolean ignoreComplete = false;
	private String progressLabel = "Progress";
	
	private AbstractMatcher matcher;
	
	public ConsoleProgressDisplay() {
		log = Logger.getLogger(getClass());
	}
	
	@Override
	public void clearReport() {	}

	@Override
	public void appendToReport(String report) {
		log.info(report);
	}

	@Override
	public void scrollToEndOfReport() {	}

	@Override
	public void matchingStarted(AbstractMatcher m) { matcher = m; }

	@Override
	public void matchingComplete() {
		if( ignoreComplete ) return;  // not really finished, used for subMatcher support
		
		log.info( matcher.getReport() );
	}

	@Override
	public void setProgressLabel(String label) { progressLabel = label;}

	@Override
	public void ignoreComplete(boolean ignore) { ignoreComplete = ignore; }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            log.info(progressLabel + ": " + progress + "%");
        }
	}

	@Override
	public void setIndeterminate(boolean indeterminate) { }

}
