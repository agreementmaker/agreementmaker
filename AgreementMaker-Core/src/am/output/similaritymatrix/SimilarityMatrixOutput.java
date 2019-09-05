package am.output.similaritymatrix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

public class SimilarityMatrixOutput {

	
	//private AbstractMatcher matcher;
	private final Ontology sourceOntology;
	private final Ontology targetOntology;
	
	public SimilarityMatrixOutput(Ontology sourceOntology, Ontology targetOntology) {
		this.sourceOntology = sourceOntology;
		this.targetOntology = targetOntology;
	}
	
	
	/**
	 * Save the classes similarity matrix.
	 * @param file The output file to which to save the similarity matrix.
	 */
	public void saveClassesMatrix(SimilarityMatrix matrix, String file) {
		
		List<Node> sourceClassesList = sourceOntology.getClassesList();
		List<Node> targetClassesList = targetOntology.getClassesList();
		
		try {
			BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(file)));
			
			
			bwr.write("@source_classes " + sourceClassesList.size() + "\n");
			
			for( Node sourceClass : sourceClassesList ) {
				bwr.write(sourceClass.getUri() + "\n");
			}
			
			
			bwr.write("@target_classes " + targetClassesList.size() + "\n");
			for( Node targetClass : targetClassesList ) {
				bwr.write(targetClass.getUri() + "\n");
			}

			bwr.write("@similarity_matrix\n");
			
			for( int i = 0; i < matrix.getRows(); i++ ) {
				for( int j = 0; j < matrix.getColumns(); j++ ) {
					double sim = matrix.getSimilarity(i, j);
					bwr.write(Double.toString(sim) + "\n");
				}
			}
			
			bwr.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Read in a similarity matrix file.
	 * @param file
	 * @return
	 */
	public SimilarityMatrix loadClassesMatrix(String file) {
		
		List<Node> sourceClassesList = sourceOntology.getClassesList();
		List<Node> targetClassesList = targetOntology.getClassesList();

		SimilarityMatrix matrix = null;
		
		try {
			BufferedReader brd = new BufferedReader(new FileReader(new File(file)));
			
			String line;
			
			int sourceSize = 0, targetSize = 0;
			
			line = brd.readLine();
			
			if( line.startsWith("@source_classes") ) {
				String[] split = line.split(" ");
				sourceSize = Integer.parseInt(split[1]);
			}
			
			for( int i = 0; i < sourceSize; i++ ) {
				line = brd.readLine();
				
				if( !sourceClassesList.get(i).getUri().equalsIgnoreCase(line) )
					throw new Exception("The file does not match the ontologies! (source classes list)");
			}
			
			line = brd.readLine();
			
			if( line.startsWith("@target_classes") ) {
				String[] split = line.split(" ");
				targetSize = Integer.parseInt(split[1]);
			} else {
				throw new Exception("Format error!");
			}
			
			
			for( int i = 0; i < targetSize; i++ ) {
				line = brd.readLine();

				if( !targetClassesList.get(i).getUri().equalsIgnoreCase(line) )
					throw new Exception("The file does not match the ontologies! (target classes list)");
			}
			
			
			matrix = new ArraySimilarityMatrix(sourceOntology,
					 						   targetOntology, 
					 						   alignType.aligningClasses);
			
			for( int i = 0; i < sourceSize; i++ ) {
				for( int j = 0; j < targetSize; j++ ) {
					line = brd.readLine();
					double sim = Double.parseDouble(brd.readLine());
					
					Mapping m = new Mapping(sourceClassesList.get(i), targetClassesList.get(j), sim);
					matrix.set(i, j, m);
				}
			}
			
			brd.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return matrix;
		
	}

	public static void main(String[] args) {
		
		Logger log = Logger.getLogger(SimilarityMatrixOutput.class.getName());
		log.setLevel(Level.DEBUG);
		
		String prefix = "H:/Work/Eclipse Workspace/Ontologies/OAEI/2011/anatomy/";
		String sourceFile = prefix + "mouse.owl";
		String targetFile = prefix + "human.owl";

		log.debug("Loading source ontology...");
		Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(sourceFile);
		
		log.debug("Loading target ontology...");
		Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(targetFile);

//		log.debug("Matching source and target ontologies ...");
//		try {
//			oaei2011.match();
//		} catch( Exception e ) {
//			e.printStackTrace();
//		}
		
		
//		log.debug("Saving classes matrix ...");
		
		SimilarityMatrixOutput simout = new SimilarityMatrixOutput(sourceOntology, targetOntology);
		
//		simout.saveClassesMatrix("/home/cosmin/Documents/Eclipse/ADVIS-Main/Ontologies/OAEI/2011/test/oaei2011-classmatrix.mtx");	
		
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		
		simout.loadClassesMatrix("H:/Work/Eclipse Workspace/Ontologies/OAEI/2011/test/oaei2011-classmatrix.mtx");
	}
	
}
