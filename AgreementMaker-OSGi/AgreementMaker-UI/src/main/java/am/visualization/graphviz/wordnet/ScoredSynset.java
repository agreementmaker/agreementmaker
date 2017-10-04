package am.visualization.graphviz.wordnet;

import java.util.List;

import edu.smu.tspell.wordnet.api.NounSynset;

public class ScoredSynset {
	private NounSynset synset;
	private String name; 
	private List<List<NounSynset>> hypernymsByLevel;
	private double score;
	
	public ScoredSynset(NounSynset synset, String name, double score) {
		this.synset = synset;
		this.score = score;
		this.name = name;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
