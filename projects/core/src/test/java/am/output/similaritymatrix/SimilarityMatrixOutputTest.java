package am.output.similaritymatrix;

import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimilarityMatrixOutputTest {
    @Test
    @DisplayName("")
    public void test01() {
        Ontology sourceOnt = mock(Ontology.class);
        Ontology targetOnt = mock(Ontology.class);

        Node sourceClass1 = mock(Node.class);
        Node targetClass1 = mock(Node.class);
        when(sourceClass1.getUri()).thenReturn("https://agreementmaker.org/classes/c1");
        when(targetClass1.getUri()).thenReturn("https://agreementmaker.org/classes/t1");

        when(sourceOnt.getClassesList()).thenReturn(Collections.singletonList(sourceClass1));
        when(targetOnt.getClassesList()).thenReturn(Collections.singletonList(targetClass1));

        SimilarityMatrix matrix = mock(SimilarityMatrix.class);
        when(matrix.getRows()).thenReturn(1);
        when(matrix.getColumns()).thenReturn(1);
        when(matrix.getSimilarity(0, 0)).thenReturn(0.75);

        SimilarityMatrixOutput output = new SimilarityMatrixOutput(sourceOnt, targetOnt);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        output.saveClassesMatrix(matrix, baos);

        assertEquals("@source_classes 1\nhttps://agreementmaker.org/classes/c1\n@target_classes 1\nhttps://agreementmaker.org/classes/t1\n@similarity_matrix\n0.75\n", baos.toString());
    }
}