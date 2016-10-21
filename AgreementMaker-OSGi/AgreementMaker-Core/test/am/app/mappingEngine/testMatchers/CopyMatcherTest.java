package am.app.mappingEngine.testMatchers;

import am.api.matcher.MatcherResult;
import am.api.matcher.SimilarityMatrix;
import am.api.ontology.Class;
import am.api.ontology.Ontology;
import am.ds.matching.ArraySimilarityMatrixBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CopyMatcherTest {
    private Ontology o1;
    private Ontology o2;

    private List<Class> o1Classes = new ArrayList<>(4);
    private List<Class> o2Classes = new ArrayList<>(4);

    private SimilarityMatrix<Class> classesMatrix;
    private MatcherResult matcherResult;

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

        classesMatrix = new ArraySimilarityMatrixBuilder<>(o1Classes, o2Classes)
                .set(o1Classes.get(0), o2Classes.get(1), 1.0)
                .set(o1Classes.get(1), o2Classes.get(1), 0.5)
                .set(o1Classes.get(2), o2Classes.get(1), 0.0)
                .set(o1Classes.get(3), o2Classes.get(2), 0.01234)
                .build();

        matcherResult = new MatcherResult.Builder()
                .setClasses(classesMatrix)
                .build();
    }

    @Test
    public void copy_classes() {

    }
}