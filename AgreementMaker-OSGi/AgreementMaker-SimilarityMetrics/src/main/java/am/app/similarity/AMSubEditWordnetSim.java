package am.app.similarity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import am.app.wordnet.WordNetUtils;

public class AMSubEditWordnetSim extends AMSubstringSim implements StringSimilarityMeasure {

	private WordNetUtils wordnet;
	
	public AMSubEditWordnetSim(WordNetUtils utils) {
		super();
		wordnet = utils;
	}
	
	@Override
	public double getSimilarity(String s1, String s2) {

		double sim = 0d;
		
		Levenshtein lv = new Levenshtein();
		double lsim = lv.getSimilarity(s1, s2);
		double AMsim = super.getSimilarity(s1, s2);

		if (wordnet.areSynonyms(s1,s2)) 
			sim=1;
		else 
			sim = (0.65*AMsim)+(0.35*lsim);

		return sim;
	}

}
