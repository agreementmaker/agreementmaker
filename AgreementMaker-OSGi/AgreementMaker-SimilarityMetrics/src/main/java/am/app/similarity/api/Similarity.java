package am.app.similarity.api;

public interface Similarity<I> {
    /**
     * @return A value between 0.0 and 1.0.
     */
    double calculate(I s1, I s2);
}
