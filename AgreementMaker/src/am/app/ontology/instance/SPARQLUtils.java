package am.app.ontology.instance;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class SPARQLUtils {
	
	public static ResultSet sparqlQuery(Model model, String queryString, QuerySolutionMap map){
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = null;
		
		if(map == null)
			qexec = QueryExecutionFactory.create(query, model);
		else qexec = QueryExecutionFactory.create(query, model, map);
		
		ResultSet results = qexec.execSelect() ;
		return results;
	}

}
