package am.app.similarity.api;

public interface Metric<I,O> {
    O calculate(I s1, I s2);
}
