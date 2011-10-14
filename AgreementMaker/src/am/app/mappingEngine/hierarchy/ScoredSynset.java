package am.app.mappingEngine.hierarchy;

import java.util.List;

import edu.smu.tspell.wordnet.NounSynset;

public class ScoredSynset {
	NounSynset synset;
	List<List<NounSynset>> hypernymsByLevel;
	double score;
	
	public ScoredSynset(NounSynset synset, double score) {
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

	public List<List<NounSynset>> getHypernymsByLevel() {
		return hypernymsByLevel;
	}

	public void setHypernymsByLevel(List<List<NounSynset>> hypernymsByLevel) {
		this.hypernymsByLevel = hypernymsByLevel;
	}
	
	@Override
	public String toString() {
		return score + " " + synset;
	}
}
