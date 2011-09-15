package batch;

import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;

public class IMBatch {
	
	public void singleTest(String sourceFile, String targetFile, String alignmentFile) throws Exception{
		OntologyDefinition sourceDef = new OntologyDefinition();
		sourceDef.loadOntology = false;
		sourceDef.loadInstances = true;
		sourceDef.instanceSourceFile = sourceFile;
		sourceDef.instanceSourceFormat = 0;
		sourceDef.loadSchemaAlignment = true;
		sourceDef.schemaAlignmentURI = alignmentFile;
		sourceDef.schemaAlignmentFormat = 0;
		sourceDef.sourceOrTarget = Ontology.SOURCE;
		
		OntologyDefinition targetDef = new OntologyDefinition();
		targetDef.loadOntology = false;
		targetDef.loadInstances = true;
		targetDef.instanceSourceFormat = 0;
		targetDef.loadSchemaAlignment = true;
		targetDef.schemaAlignmentURI = alignmentFile;
		targetDef.schemaAlignmentFormat = 0;
		targetDef.sourceOrTarget = Ontology.SOURCE;
		
		OntoTreeBuilder builder = new OntoTreeBuilder(sourceDef);
		builder.build();
		Ontology sourceOnt = builder.getOntology();
		
	}
	
	public static void main(String[] args) {
		
	}

}
