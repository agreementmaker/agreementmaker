package am.app.similarity;

import uk.ac.shef.wit.simmetrics.similaritymetrics.QGramsDistance;

public class QGramSim implements StringSimilarityMeasure {

	@Override
	public double getSimilarity(String s1, String s2) {
		QGramsDistance q = new QGramsDistance();
		return q.getSimilarity(s1, s2);
	}

}
