package am.extension.userfeedback.analysis;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public abstract class AnalysisBase {

	protected static final String[] noisyLoggers = 
		{ "com.hp.hpl.jena.util.FileManager",
		  "org.apache.jena.riot.RDFDataMgr",
		  "org.apache.jena.riot.stream.StreamManager",
		  "am.extension.userfeedback.clustering.disagreement.SestCombinationMatchers",
		  "am.extension.userfeedback.experiments.UFLExperiment",
		  "am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix",
		  "org.apache.jena.riot",
		  "am.app.Core"
		};
	
	protected void silenceNoisyLoggers(String[] loggers) {
		for(String logger : loggers) {
			LogManager.getLogger(logger).setLevel(Level.OFF);
		}
	}
	
	protected void setupLogging() {
		Logger root = Logger.getRootLogger();
	    root.addAppender(new ConsoleAppender(
	           new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN)));
	}
}
