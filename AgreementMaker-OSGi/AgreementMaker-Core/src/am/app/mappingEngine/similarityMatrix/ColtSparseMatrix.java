package am.app.mappingEngine.similarityMatrix;

import java.io.FileWriter;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.AbstractSimilarityMatrix;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;

public class ColtSparseMatrix extends AbstractSimilarityMatrix {

	private static final long serialVersionUID = 3307280239624099618L;
	
	private SparseDoubleMatrix2D matrix = null;
	
	public ColtSparseMatrix(Ontology sourceOnt, Ontology targetOnt, alignType type) {
		super(sourceOnt, targetOnt, type);
		
		int rows;
		int cols;
		switch(typeOfMatrix) {
		case aligningClasses:
			rows = sourceOntology.getClassesList().size();
			cols = targetOntology.getClassesList().size();
			break;
		case aligningProperties:
			rows = sourceOntology.getPropertiesList().size();
			cols = targetOntology.getPropertiesList().size();
			break;
		default:
			throw new RuntimeException("Invalid typeOfMatrix.");
		}
		
		this.matrix = new SparseDoubleMatrix2D(rows, cols);
	}
	
	/**
	 * Cloning constructor;
	 */
	public ColtSparseMatrix(ColtSparseMatrix cmatrix) {
		super(cmatrix.sourceOntology, cmatrix.targetOntology, cmatrix.typeOfMatrix);
		this.matrix = (SparseDoubleMatrix2D) cmatrix.matrix.clone();
	}

	@Override public int getRows() { return matrix.rows(); }
	@Override public int getColumns() { return matrix.columns(); }

	@Override
	public Mapping get(int i, int j) {
		Node n1 = null;
		Node n2 = null;
		switch(typeOfMatrix) {
		case aligningClasses:
			n1 = sourceOntology.getClassesList().get(i);
			n2 = targetOntology.getClassesList().get(i);
			break;
		case aligningProperties:
			n1 = sourceOntology.getPropertiesList().get(i);
			n2 = targetOntology.getPropertiesList().get(i);
			break;
		default:
			throw new RuntimeException("Invalid typeOfMatrix.");
		}
		
		return new Mapping(n1, n2, matrix.get(i, j), MappingRelation.EQUIVALENCE, typeOfMatrix);
	}

	@Override
	public void set(int i, int j, Mapping d) {
		if( d.getAlignmentType() != typeOfMatrix )
			throw new RuntimeException("Incompatible node types.  Trying to set similarities for classes in a properties matrix, or vice-versa.");
		matrix.set(i, j, d.getSimilarity());
	}

	@Override
	public void setSimilarity(int i, int j, double similarity) {
		matrix.set(i, j, similarity);
	}

	@Override
	public double getSimilarity(int i, int j) {
		return matrix.get(i, j);
	}

	@Override
	public SimilarityMatrix clone() {
		return new ColtSparseMatrix(this);
	}

	public class MaxValVisitor implements IntIntDoubleFunction {
		public double maxVal = 0d;
		@Override
		public double apply(int arg0, int arg1, double arg2) {
			if( arg2 > maxVal ) maxVal = arg2;
			return arg2;
		}
	}
	
	@Override
	public double getMaxValue() {
		MaxValVisitor mvv = new MaxValVisitor();
		matrix.forEachNonZero(mvv);
		return mvv.maxVal;
	}

	@Override public List<Mapping> chooseBestN() { throw new RuntimeException("Not implemented."); }
	@Override public List<Mapping> chooseBestN(List<Integer> rowsIncludedList, List<Integer> colsIncludedList, boolean considerThreshold, double threshold) {
		throw new RuntimeException("Not implemented.");
	}
	@Override public List<Mapping> chooseBestN(boolean considerThreshold, double threshold) { throw new RuntimeException("Not implemented."); }
	@Override public List<Mapping> chooseBestN(List<Integer> rowsIncludedList, List<Integer> colsIncludedList) { throw new RuntimeException("Not implemented."); }
	@Override public void initFromNodeList(List<Node> sourceList, List<Node> targetList) { throw new RuntimeException("Not implemented."); }
	@Override public Mapping[] getTopK(int k) { throw new RuntimeException("Not implemented."); }
	@Override public Mapping[] getTopK(int k, boolean[][] filteredCells) { throw new RuntimeException("Not implemented."); }
	@Override public List<Mapping> toMappingArray() { throw new RuntimeException("Not implemented."); }
	@Override public List<Mapping> toMappingArray(FileWriter fw, int round) { throw new RuntimeException("Not implemented."); }
	@Override public List<Double> toSimilarityArray(List<Mapping> mapsArray) { throw new RuntimeException("Not implemented."); }
}
