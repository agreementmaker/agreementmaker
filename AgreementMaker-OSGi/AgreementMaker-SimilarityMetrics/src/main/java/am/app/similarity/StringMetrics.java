package am.app.similarity;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * This enumeration contains a list of all the string similarity metrics defined
 * on the AgreementMaker system. Some of them are simply wrapping other library
 * calls.
 * 
 * @author Cosmin Stroe
 * @see {@link StringSimilarityMeasure}
 */
public enum StringMetrics {

	LEVENSHTEIN("Levenshtein Edit Distance", LevenshteinEditDistance.class),
	JAROWINKER("Jaro Winkler", JaroWinklerSim.class),
	QGRAM("Q-Gram", QGramSim.class),
	SUB("Substring metric", SubstringMetric.class),
	AMSUB("AM Substring metric", AMSubstringSim.class),
	ISUB("I-SUB", ISubSim.class),
	AMSUB_AND_EDIT("AMsubstring + editDistance", AMSubEditSim.class), //0.6*amsub + 0.4*editdistance
	AMSUB_AND_EDIT_WITH_WORDNET("AMsubstring + editDistance with WordNet Synonyms", AMSubEditSim.class); 

	private static final Logger sLog = Logger.getLogger(StringMetrics.class);
	
	private String name;
	private Class<? extends StringSimilarityMeasure> clazz;

	private StringMetrics(String name, Class<? extends StringSimilarityMeasure> clazz) {
		this.name = name;
		this.clazz = clazz;
	}

	public String getLongName() {
		return name;
	}

	public StringSimilarityMeasure getMeasure() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			sLog.error("", e);
		} catch (IllegalAccessException e) {
			sLog.error("", e);
		}
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Given a list of source labels and a list of target labels, find the most
	 * similar pair of labels using the given similarity metric.
	 *
	 * Current runtime is O(|sourceLabels|*|targetLabels|)*O(SSM) ~
	 * BigOmega(n^2).
	 */
	public static double getMaxStringSimilarity(
			List<String> sourceLabels, List<String> targetLabels, StringSimilarityMeasure ssm) {

		double maxStringSimilarity = 0.0d;
		for( String sourceLabel : sourceLabels ) {
			for( String targetLabel : targetLabels ) {
				double currentSim = ssm.getSimilarity(sourceLabel, targetLabel);
				maxStringSimilarity = Math.max(currentSim, maxStringSimilarity);
			}
		}

		return maxStringSimilarity;
	}
}
