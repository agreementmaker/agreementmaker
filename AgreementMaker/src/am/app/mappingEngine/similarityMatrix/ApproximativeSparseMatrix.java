package am.app.mappingEngine.similarityMatrix;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Ontology;

public class ApproximativeSparseMatrix extends SparseMatrix {

	private static final long serialVersionUID = -1578488061395125831L;

	public ApproximativeSparseMatrix(Ontology sourceOntology,
			Ontology targetOntology, alignType typeOfMatrix, double threshold) {
		super(sourceOntology, targetOntology, typeOfMatrix);
		// TODO Auto-generated constructor stub
	}


}
