package am.app.mappingEngine.similarityMatrix;

import static org.junit.Assert.assertTrue;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.ontology.AMNode;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class ApproximativeSparseMatrixTest {
	SparseMatrix sMatrix;
	ApproximativeSparseMatrix aMatrix;

	private OntModel m;
	
	private static final int NUM_CELLS = 20;
	
	@Before
	public void setUp() throws Exception {
		
		m = ModelFactory.createOntologyModel();
		
		final OntClass[] classes = new OntClass[NUM_CELLS];
		for( int i = 0; i < NUM_CELLS; i++ ) {
			classes[i] = m.createClass("Item " + i );
		}
		
		Ontology testOntology = new Ontology() {
			
			@Override
			public List<Node> getClassesList() {
				
				List<Node> classesList = new ArrayList<Node>();
				for( int i = 0; i < NUM_CELLS; i++ ) {
					classesList.add(new AMNode(i, classes[i], AMNode.OWLCLASS, 0));
				}
				return classesList;
			}
		};
		
		sMatrix=new SparseMatrix(testOntology, testOntology, alignType.aligningClasses);
		aMatrix=new ApproximativeSparseMatrix(testOntology, testOntology, alignType.aligningClasses);
		//System.out.println(sMatrix.getRows());
		//System.out.println(aMatrix.getRows());
		
		Random rand = new Random();
		
		for(int i=0;i<NUM_CELLS;i++){
			for(int j=0;j<NUM_CELLS;j++){
				double d=rand.nextDouble();
				int trunk=(int)(100000.0D*d);
				d=((double)trunk)/100000.0D;
				//System.out.println(d);
				sMatrix.set(i, j, new Mapping(
						new AMNode(i, classes[i], AMNode.OWLCLASS, 0),
						new AMNode(j, classes[j], AMNode.OWLCLASS, 0),d));
				aMatrix.set(i, j, new Mapping(
						new AMNode(i, classes[i], AMNode.OWLCLASS, 0),
						new AMNode(i, classes[j], AMNode.OWLCLASS, 0),d));
			}
		}
	}

	@Test
	public void testGetRows() {
		assertTrue(sMatrix.getRows()==aMatrix.getRows());
	}

	@Test
	public void testGetColumns() {
		assertTrue(sMatrix.getColumns()==aMatrix.getColumns());
	}

	@Test
	public void testGetColMaxValues() {
		for(int i=0;i<20;i++){
			Mapping[] s=sMatrix.getColMaxValues(i, 20);
			Mapping[] a=aMatrix.getColMaxValues(i, 20);
			for(int j=0;j<a.length;j++){
				//System.out.println(a[j].getSimilarity()+", "+s[j].getSimilarity());
				assertTrue(a[j].getSimilarity()==+s[j].getSimilarity());
			}
		}
	}

	@Test
	public void testGetRowMaxValues() {
		for(int i=0;i<20;i++){
			Mapping[] s=sMatrix.getRowMaxValues(i, 20);
			Mapping[] a=aMatrix.getRowMaxValues(i, 20);
			for(int j=0;j<a.length;j++){
				//System.out.println(a[j].getSimilarity()+", "+s[j].getSimilarity());
				assertTrue(a[j].getSimilarity()==s[j].getSimilarity());
			}
		}
	}

	@Test
	public void testGetRowSum() {
		for(int i=0;i<20;i++){
			DecimalFormat d=new DecimalFormat();
			d.applyPattern("###.#####");
			String a=d.format(aMatrix.getRowSum(i));
			String s=d.format(sMatrix.getRowSum(i));
			System.out.println(a+", "+s);
			assertTrue(a.equalsIgnoreCase(s));
		}
	}

	@Test
	public void testGetColSum() {
		for(int i=0;i<20;i++){
			DecimalFormat d=new DecimalFormat();
			d.applyPattern("###.#####");
			String a=d.format(aMatrix.getColSum(i));
			String s=d.format(sMatrix.getColSum(i));
			System.out.println(a+", "+s);
			assertTrue(a.equalsIgnoreCase(s));
		}
			
	}

	@Test
	public void testGetMaxValue() {
		assertTrue(aMatrix.getMaxValue()==sMatrix.getMaxValue());
	}
}
