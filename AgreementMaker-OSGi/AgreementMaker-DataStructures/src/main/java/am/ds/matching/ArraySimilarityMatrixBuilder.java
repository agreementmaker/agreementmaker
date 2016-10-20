package am.ds.matching;

import am.api.ontology.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArraySimilarityMatrixBuilder<I extends Entity> {
    private Map<I, Integer> sourceIndexMap = new HashMap<>();
    private Map<I, Integer> targetIndexMap = new HashMap<>();
    private double[][] similarityValues;

    public ArraySimilarityMatrixBuilder(List<I> sourceList, List<I> targetList) {
        for(int i = 0; i < sourceList.size(); i++) {
            sourceIndexMap.put(sourceList.get(i), i);
        }

        for(int i = 0; i < targetList.size(); i++) {
            targetIndexMap.put(targetList.get(i), i);
        }

        similarityValues = new double[sourceList.size()][targetList.size()];
    }

    public void set(I sourceEntity, I targetEntity, double value) {
        int sourceIndex = sourceIndexMap.get(sourceEntity);
        int targetIndex = targetIndexMap.get(targetEntity);
        similarityValues[sourceIndex][targetIndex] = value;
    }

    public ArraySimilarityMatrix<I> build() {
        return new ArraySimilarityMatrix<>(sourceIndexMap, targetIndexMap, similarityValues);
    }
}
