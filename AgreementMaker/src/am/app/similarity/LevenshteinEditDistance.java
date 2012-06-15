package am.app.similarity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

public class LevenshteinEditDistance implements StringSimilarityMeasure {

	@Override
	public double getSimilarity(String s1, String s2) {
		Levenshtein lv = new Levenshtein();
		return lv.getSimilarity(s1, s2);
	}

}
