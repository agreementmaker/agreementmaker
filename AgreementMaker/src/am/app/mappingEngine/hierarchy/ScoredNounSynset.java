package am.app.mappingEngine.hierarchy;

import edu.smu.tspell.wordnet.NounSynset;

public class ScoredNounSynset {
	NounSynset synset;
	double score;
	
	public ScoredNounSynset(NounSynset synset, double score) {
		this.synset = synset;
		this.score = score;
	}

	public NounSynset getSynset() {
		return synset;
	}
	
	public void setSynset(NounSynset synset) {
		this.synset = synset;
	}
	
	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	
}
