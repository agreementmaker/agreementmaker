package am.app.mappingEngine.qualityEvaluation.metrics;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.AMNode;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalConfidenceQualityTest {
    private double[][] classSimilarities = {
        {0.5, 0.1, 0.0, 0.1},
        {0.0, 0.9, 0.0, 0.0},
        {0.5, 0.5, 0.5, 0.1},
        {0.0, 0.0, 0.4, 0.1},
        {1.0, 0.0, 0.3, 0.1}
    };

    @Test
    public void compute_row_quality() throws Exception {
        List<Node> sourceClasses = new ArrayList<>(4);
        List<Node> targetClasses = new ArrayList<>(4);
        Node mockNode = mock(Node.class);
        when(mockNode.getType()).thenReturn(AMNode.OWLCLASS);
        for(int i = 0; i < 4; i++) {
            sourceClasses.add(mockNode);
            targetClasses.add(mockNode);
        }
        sourceClasses.add(mockNode);

        Ontology sourceOntology = mock(Ontology.class);
        when(sourceOntology.getID()).thenReturn(1);
        when(sourceOntology.getClassesList()).thenReturn(sourceClasses);

        Ontology targetOntology = mock(Ontology.class);
        when(targetOntology.getID()).thenReturn(2);
        when(targetOntology.getClassesList()).thenReturn(targetClasses);

        SimilarityMatrix classesMatrix = new ArraySimilarityMatrix(sourceOntology, targetOntology,
                AbstractMatcher.alignType.aligningClasses, classSimilarities);

        AbstractMatcher matcher = mock(AbstractMatcher.class);
        when(matcher.getClassesMatrix()).thenReturn(classesMatrix);
        when(matcher.getMaxSourceAlign()).thenReturn(1);
        when(matcher.getMaxTargetAlign()).thenReturn(1);
        when(matcher.areClassesAligned()).thenReturn(true);
        when(matcher.arePropertiesAligned()).thenReturn(false);

        LocalConfidenceQuality q = new LocalConfidenceQuality();
        QualityEvaluationData data = q.getQuality(matcher);

        double[] classQualities = data.getLocalClassMeasures();
        assertEquals("0th class quality should be correct", 0.433, classQualities[0], 0.001);
        assertEquals("1st class quality should be correct", 0.900, classQualities[1], 0.001);
        assertEquals("2nd class quality should be correct", 0.133, classQualities[2], 0.001);
        assertEquals("3rd class quality should be correct", 0.366, classQualities[3], 0.001);
        assertEquals("4rd class quality should be correct", 0.866, classQualities[4], 0.001);
    }
}