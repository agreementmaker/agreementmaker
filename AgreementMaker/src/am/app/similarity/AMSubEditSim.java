package am.app.similarity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class AMSubEditSim implements StringSimilarityMeasure {

	@Override
	public double getSimilarity(String s1, String s2) {
		Levenshtein lv = new Levenshtein();
		AMSubstringSim amSS = new AMSubstringSim();

		double lsim = lv.getSimilarity(s1, s2);
		double AMsim = amSS.getSimilarity(s1, s2);
		
		return (0.65 * AMsim) + (0.35 * lsim);
	}

}
