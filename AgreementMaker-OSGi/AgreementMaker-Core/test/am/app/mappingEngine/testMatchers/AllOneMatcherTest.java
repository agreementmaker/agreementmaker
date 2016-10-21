package am.app.mappingEngine.testMatchers;

import am.api.matcher.MatcherResult;
import am.api.matcher.SimilarityMatrix;
import am.api.ontology.Class;
import am.api.ontology.Ontology;
import am.api.alignment.AlignmentContext;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AllOneMatcherTest {
    private Ontology o1;
    private Ontology o2;

    private List<Class> o1Classes = new ArrayList<>(4);
    private List<Class> o2Classes = new ArrayList<>(4);

    @Before
    public void setUp() {
        o1 = mock(Ontology.class);
        o2 = mock(Ontology.class);

        o1Classes = new ArrayList<>(4);
        o2Classes = new ArrayList<>(4);
        for(int i = 0; i < 4; i++) {
            o1Classes.add(mock(Class.class));
            o2Classes.add(mock(Class.class));
        }

        when(o1.getClasses()).thenReturn(o1Classes);
        when(o2.getClasses()).thenReturn(o2Classes);
    }

    @Test
    public void match_classes() {
        AllOneMatcher matcher = new AllOneMatcher();
        MatcherResult result = matcher.match(mock(AlignmentContext.class));

        SimilarityMatrix<Class> classSimilarityMatrix = result.getClasses().get();

        for (Class o1Class : o1Classes) {
            for (Class o2Class : o2Classes) {
                assertEquals(classSimilarityMatrix.getSimilarity(o1Class, o2Class), 1d, 0.000001);
            }
        }
    }
}