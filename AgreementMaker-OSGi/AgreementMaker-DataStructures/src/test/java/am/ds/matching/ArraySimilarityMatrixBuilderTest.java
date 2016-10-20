package am.ds.matching;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import am.api.ontology.Class;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ArraySimilarityMatrixBuilderTest {
    @Test
    public void builder() {
        List<Class> sourceClasses = new ArrayList<>();
        sourceClasses.add(mock(Class.class));
        sourceClasses.add(mock(Class.class));
        sourceClasses.add(mock(Class.class));
        sourceClasses.add(mock(Class.class));

        List<Class> targetClasses = new ArrayList<>();
        targetClasses.add(mock(Class.class));
        targetClasses.add(mock(Class.class));
        targetClasses.add(mock(Class.class));
        targetClasses.add(mock(Class.class));

        ArraySimilarityMatrixBuilder<Class> builder = new ArraySimilarityMatrixBuilder<>(sourceClasses, targetClasses);
        builder.set(sourceClasses.get(0), targetClasses.get(0), 1.0);
        builder.set(sourceClasses.get(1), targetClasses.get(1), 0.5);
        builder.set(sourceClasses.get(2), targetClasses.get(3), 0.033333);
        builder.set(sourceClasses.get(3), targetClasses.get(2), 0.0);

        ArraySimilarityMatrix<Class> classArraySimilarityMatrix = builder.build();

        assertEquals(classArraySimilarityMatrix.getSimilarity(sourceClasses.get(0), targetClasses.get(0)), 1.0, 0.0000001);
        assertEquals(classArraySimilarityMatrix.getSimilarity(sourceClasses.get(1), targetClasses.get(1)), 0.5, 0.0000001);
        assertEquals(classArraySimilarityMatrix.getSimilarity(sourceClasses.get(2), targetClasses.get(3)), 0.033333, 0.0000001);
        assertEquals(classArraySimilarityMatrix.getSimilarity(sourceClasses.get(3), targetClasses.get(2)), 0.0, 0.0000001);
    }
}