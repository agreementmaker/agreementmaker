package am.app.similarity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.JaroWinkler;

public class JaroWinklerSim implements StringSimilarityMeasure {

	@Override
	public double getSimilarity(String s1, String s2) {
		JaroWinkler jv = new JaroWinkler();
		return jv.getSimilarity(s1, s2);
	}

}
