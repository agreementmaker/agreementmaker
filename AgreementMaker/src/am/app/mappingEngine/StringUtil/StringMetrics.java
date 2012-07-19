package am.app.mappingEngine.StringUtil;

import am.app.similarity.AMSubEditSim;
import am.app.similarity.AMSubstringSim;
import am.app.similarity.ISubSim;
import am.app.similarity.JaroWinklerSim;
import am.app.similarity.LevenshteinEditDistance;
import am.app.similarity.QGramSim;
import am.app.similarity.StringSimilarityMeasure;
import am.app.similarity.SubstringSim;

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
	SUB("Substring metric", SubstringSim.class),
	AMSUB("AM Substring metric", AMSubstringSim.class),
	ISUB("I-SUB", ISubSim.class),
	AMSUB_AND_EDIT("AMsubstring + editDistance", AMSubEditSim.class), //0.6*amsub + 0.4*editdistance
	AMSUB_AND_EDIT_WITH_WORDNET("AMsubstring + editDistance with WordNet Synonyms", AMSubEditSim.class); 

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
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return name;
	}
}
