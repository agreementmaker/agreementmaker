package am.app.similarity.api;

public interface Measure<I> {
    int calculate(I s1, I s2);
}
