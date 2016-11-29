package am.matcher.Combination;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.AMNode;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntologyDefinition;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CombinationMatcherTest {
    private double[][] matcher1ClassesMatrix = {
            {0.5, 0.1, 0.0, 0.1},
            {0.0, 0.9, 0.0, 0.0},
            {0.5, 0.5, 0.5, 0.1},
            {1.0, 1.0, 1.0, 1.0},
    };

    private double[][] matcher2ClassesMatrix = {
            {0.5, 0.9, 0.0, 0.1},
            {0.0, 0.1, 0.0, 0.0},
            {0.1, 0.2, 0.9, 0.0},
            {0.1, 0.1, 0.7, 0.1},
    };

    private AbstractMatcher matcher1;
    private AbstractMatcher matcher2;

    private Ontology sourceOntology;
    private Ontology targetOntology;

    @Before
    public void setUp() {
        List<Node> classesList = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            classesList.add(createClassNode(i));
        }

        OntologyDefinition definition = new OntologyDefinition(false, "", OntologyDefinition.OntologyLanguage.OWL, OntologyDefinition.OntologySyntax.RDFXML);

        sourceOntology = mock(Ontology.class);
        when(sourceOntology.getClassesList()).thenReturn(Collections.unmodifiableList(classesList));
        when(sourceOntology.getDefinition()).thenReturn(definition);
        targetOntology = mock(Ontology.class);
        when(targetOntology.getClassesList()).thenReturn(Collections.unmodifiableList(classesList));
        when(targetOntology.getDefinition()).thenReturn(definition);

        SimilarityMatrix m1Classes = new ArraySimilarityMatrix(sourceOntology, targetOntology,
                AbstractMatcher.alignType.aligningClasses, matcher1ClassesMatrix);
        matcher1 = mock(AbstractMatcher.class);
        when(matcher1.getClassesMatrix()).thenReturn(m1Classes);
        when(matcher1.getMaxSourceAlign()).thenReturn(1);
        when(matcher1.getMaxTargetAlign()).thenReturn(1);
        when(matcher1.areClassesAligned()).thenReturn(true);
        when(matcher1.arePropertiesAligned()).thenReturn(false);


        SimilarityMatrix m2Classes = new ArraySimilarityMatrix(sourceOntology, targetOntology,
                AbstractMatcher.alignType.aligningClasses, matcher2ClassesMatrix);
        matcher2 = mock(AbstractMatcher.class);
        when(matcher2.getClassesMatrix()).thenReturn(m2Classes);
        when(matcher2.getMaxSourceAlign()).thenReturn(1);
        when(matcher2.getMaxTargetAlign()).thenReturn(1);
        when(matcher2.areClassesAligned()).thenReturn(true);
        when(matcher2.arePropertiesAligned()).thenReturn(false);
    }

    private Node createClassNode(int index) {
        Node mockNode = mock(Node.class);
        when(mockNode.getIndex()).thenReturn(index);
        when(mockNode.getType()).thenReturn(AMNode.OWLCLASS);
        return mockNode;
    }

    @Test
    public void averageCombinationWithTwoMatchersAndLocalConfidenceQuality() throws Exception {
        CombinationParameters params = new CombinationParameters();
        params.qualityEvaluation = true;
        params.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
        params.combinationType = CombinationParameters.AVERAGECOMB;
        params.setOntologies(sourceOntology, targetOntology);

        CombinationMatcher lwc = new CombinationMatcher();
        lwc.setParameters(params);
        lwc.setPerformSelection(false);
        lwc.setAlignClass(true);
        lwc.setAlignProp(false);
        lwc.setInputMatchers(Arrays.asList(matcher1, matcher2));
        lwc.match();

        SimilarityMatrix alignedMatrix = lwc.getClassesMatrix();
        /* classMatrix(3,2)
         m1 quality = 0.0
         m2 quality = 0.6
         m1 weight*sim = 0
         m2 weight*sim = 0.6 * 0.7 = 0.42
         sim = (sim1 + sim2) / (weight1 + weight2) = (0 + 0.42) / (0.6) = 0.7
        */
        assertEquals(0.7, alignedMatrix.get(3, 2).getSimilarity(), 0.001);

        /* classMatrix(2,0)
         weight1 = quality(m1(2,1)) = 0.133333333
         weight2 = quality(m2(2,1)) = 0.8
         sim1 = weight1 * sim1 = 0.5 * 0.133333333 = 0.066666667
         sim2 = weight2 * sim2 = 0.1 * 0.8 = 0.08
         sim = (sim1 + sim2) / (weight1 + weight2) = (0.066666667 + 0.08) / (0.133333333 + 0.8) = 0.157142858
        */
        assertEquals(0.157142, alignedMatrix.get(2, 0).getSimilarity(), 0.001);
    }
}