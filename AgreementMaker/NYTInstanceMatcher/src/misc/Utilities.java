package misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import am.GlobalStaticVariables;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;


public class Utilities {

	public static Ontology openOntology(String ontoName){
		Ontology ontology;
		try {
			OntoTreeBuilder treeBuilder = new OntoTreeBuilder(ontoName, GlobalStaticVariables.SOURCENODE,
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
	
	public static String getPage(String pageURL) throws IOException{
		URL myUrl;
		myUrl = new URL(pageURL);
	    
		BufferedReader in = new BufferedReader(
	                        new InputStreamReader(
	                        myUrl.openStream()));

	    String page = "";
	    String line;

	    while ((line = in.readLine()) != null)
	        page += line;

	    in.close();

	    return page;
	}
	
	public static String processLabel(String label){
		if(label.contains(",")){
			String[] splitted = label.split(",");
			return splitted[1].trim() + " " + splitted[0].trim();
		}
		return label; 
	}
}
