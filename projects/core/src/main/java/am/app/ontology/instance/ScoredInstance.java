package am.app.ontology.instance;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author federico
 *
 */
public class ScoredInstance {
	Instance instance;
	double score;
	
	public ScoredInstance(Instance instance, double score) {
		super();
		this.instance = instance;
		this.score = score;
	}

	public Instance getInstance() {
		return instance;
	}

	public double getScore() {
		return score;
	}
	
	public static List<ScoredInstance> filter(List<ScoredInstance> list, double threshold){
		List<ScoredInstance> retValue = new ArrayList<ScoredInstance>();
		if(list.size() == 0) return retValue;
		if(list.size() == 1) return list;
		double firstScore = list.get(0).getScore();
		ScoredInstance instance;
		for (int i = 0; i < list.size(); i++) {
			instance = list.get(i);
			if(instance.getScore() > firstScore - threshold)
				retValue.add(instance);
		}
		return retValue;
	}
	
	@Override
	public String toString() {
		return instance.getUri() + " " + score;
	}
}
