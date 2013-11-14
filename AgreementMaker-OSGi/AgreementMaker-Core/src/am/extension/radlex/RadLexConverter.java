package am.extension.radlex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class RadLexConverter {

	public void convertRadLex(File radlexOntology) {
		Ontology ont = OntoTreeBuilder.loadOWLOntology(radlexOntology.getAbsolutePath());
		
		OntModel m = ont.getModel();
		
		
		/*
		ExtendedIterator<Individual> indiIter = m.listIndividuals();
		int i = 0;
		
		while( indiIter.hasNext() ) {
			Individual indi = indiIter.next();
			if( indi.isAnon() ) {
				System.out.println(i + ". " + indi.getId());
			} else {
				if( indi.getRDFType().getLocalName().equals("radlex_synonym")) { 
				
					System.out.println(i + ". " + indi.getLocalName() + " rdf:type " + indi.getRDFType().getURI());
				
					StmtIterator indiProps = indi.listProperties();
					while( indiProps.hasNext() ) {
						Statement currentIndiProp = indiProps.nextStatement();
						
						
						
						//System.out.println(indiProps.nextStatement());
					}
					i++;
				}
			}
		}*/
		
		BufferedWriter buf = null;
		try {
		
			FileWriter fr = new FileWriter(new File("/home/cosmin/radlex_synonyms.txt"));
			buf = new BufferedWriter(fr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		// query the model for all the property relations
		String queryString  = "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n";
	       queryString += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";
	       queryString += "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n";
	       queryString += "PREFIX radlex: <http://bioontology.org/projects/ontologies/radlex/radlexOwl#> \n";
	       queryString += "SELECT ?synLabel ?ofLabel \n { \n";
	       queryString += "?syn rdf:type radlex:radlex_synonym .\n";
	       queryString += "?syn rdfs:label ?synLabel .\n";
	       queryString += "?syn radlex:Synonym_Of ?synOf .\n";
	       queryString += "?synOf rdfs:label ?ofLabel .\n";
	       
	       queryString += "}";
		
     Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, m);
		try {
			ResultSet results = qexec.execSelect() ;
			
			//System.out.println(ResultSetFormatter.asText(results, query));
			
			while( results.hasNext() ) {
				QuerySolution solution = results.next();
				String synLabel = solution.get("synLabel").asLiteral().getLexicalForm();
				String ofLabel = solution.get("ofLabel").asLiteral().getLexicalForm();
				buf.write(synLabel + "\n" + ofLabel);
			}
			
			buf.flush();
			buf.close();
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		
		RadLexConverter converter = new RadLexConverter();
		
		converter.convertRadLex(new File("/home/cosmin/Downloads/radlex_owl.owl"));
		
	}
}
