package am.ds.matching;

import am.api.matching.SimilarityMatrix;
import am.api.ontology.Entity;

import java.util.HashMap;
import java.util.Map;

public class ArraySimilarityMatrix<I extends Entity> implements SimilarityMatrix<I> {
    private Map<I, Integer> sourceIndexMap = new HashMap<>();
    private Map<I, Integer> targetIndexMap = new HashMap<>();
    private double[][] similarityValues;

    ArraySimilarityMatrix(
            Map<I, Integer> sourceIndexMap,
            Map<I, Integer> targetIndexMap,
            double[][] similarityValues) {
        this.sourceIndexMap = sourceIndexMap;
        this.targetIndexMap = targetIndexMap;
        this.similarityValues = similarityValues;
    }


    @Override
    public double getSimilarity(I sourceEntity, I targetEntity) {
        int sourceIndex = sourceIndexMap.get(sourceEntity);
        int targetIndex = targetIndexMap.get(targetEntity);
        return similarityValues[sourceIndex][targetIndex];
    }
}
