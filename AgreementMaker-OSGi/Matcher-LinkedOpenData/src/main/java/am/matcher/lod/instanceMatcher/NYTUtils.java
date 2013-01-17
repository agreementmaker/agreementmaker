package am.matcher.lod.instanceMatcher;

import am.GlobalStaticVariables;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OldOntoTreeBuilder;

public class NYTUtils {
	
	public static Ontology openOntology(String ontoName){
		Ontology ontology;
		try {
			OldOntoTreeBuilder treeBuilder = new OldOntoTreeBuilder(ontoName, GlobalStaticVariables.SOURCENODE,
			GlobalStaticVariables.LANG_OWL, 
			GlobalStaticVariables.SYNTAX_RDFXML, false, true);
			treeBuilder.build();
			ontology = treeBuilder.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ontology;
	}
	

}
