package am.extension;

import am.GlobalStaticVariables;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.baseSimilarity.advancedSimilarity.AdvancedSimilarityParameters;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.output.AlignmentOutput;

public class MyMain {

	public static void main(String args[]) {
		
		// Step 1.  Load my ontologies.
		
		String sourceOntFile = "/home/cosmin/Desktop/Ontologies/SourceOntology.owl";
		String targetOntFile = "/home/cosmin/Desktop/Ontologies/TargetOntology.owl";
		
		Ontology sourceOntology = readOntology(sourceOntFile);
		Ontology targetOntology = readOntology(targetOntFile);
		
		// Step 2. Instantiate the Advanced Similarity Matcher.
		
		MatchersRegistry mrASM = MatchersRegistry.AdvancedSimilarity;
		AbstractMatcher asm = MatcherFactory.getMatcherInstance(mrASM, 0);
		
		// Step 3. Set the parameters for the matcher.
		
		AdvancedSimilarityParameters asmp = new AdvancedSimilarityParameters();
		asmp.threshold = 0.75;  // set the threshold
		
		asmp.maxSourceAlign = 1; // set the source cardinality
		asmp.maxTargetAlign = 1; // set the target cardinality
		
		asm.setParam(asmp); // set the matcher parameters
		asm.setSourceOntology(sourceOntology);  // set the source ontology for the matcher
		asm.setTargetOntology(targetOntology);  // set the target ontology for the matcher
		
		
		// Step 4.  Align the ontologies.
		
		try {
			asm.match();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Step 5.  Save the alignment.
		try {
			String alignmentFilename = "/home/cosmin/Desktop/Ontologies/source-target-alignment.rdf";
			AlignmentOutput output = new AlignmentOutput(asm.getAlignment(), alignmentFilename);
			String sourceUri = sourceOntology.getURI();
			String targetUri = targetOntology.getURI();
			output.write(sourceUri, targetUri, sourceUri, targetUri, asm.getName());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		
	}

	/**
	 * Method to read in an OWL ontology.
	 * @param sourceOntFile Path of the OWL ontology file.
	 * @return Ontology data structure.
	 */
	private static Ontology readOntology(String sourceOntFile) {
		
		OntoTreeBuilder ontoBuilder = new OntoTreeBuilder( sourceOntFile,
				Ontology.SOURCE, GlobalStaticVariables.LANG_OWL, 
				GlobalStaticVariables.SYNTAX_RDFXML, 
				false, false);
		
		ontoBuilder.build(OntoTreeBuilder.Profile.noReasoner);  // read in the ontology file, create the Ontology object.
		
		return ontoBuilder.getOntology();
	}
	
}
