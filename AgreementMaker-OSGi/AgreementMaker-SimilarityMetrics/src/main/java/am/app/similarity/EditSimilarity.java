package am.app.similarity;

import am.app.similarity.api.Similarity;

public class EditSimilarity implements Similarity<String> {
    private EditDistanceMeasure editDistance = new EditDistanceMeasure();

    @Override
    public double calculate(String s, String t) {
        final int edit = editDistance.calculate(s, t);
        return 1 / Math.exp(edit / (double) (s.length() + t.length() - edit));
    }
}
