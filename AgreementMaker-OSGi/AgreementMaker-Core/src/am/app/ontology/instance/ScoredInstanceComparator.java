package am.app.ontology.instance;

import java.util.Comparator;

public class ScoredInstanceComparator implements Comparator<ScoredInstance>{

	@Override
	public int compare(ScoredInstance o1, ScoredInstance o2) {
		double score = o1.getScore() - o2.getScore();
		if(score > 0) return -1;
		if(score < 0) return 1;
		return 0;
	}
}
