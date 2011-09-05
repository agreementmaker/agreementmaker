package parallel;

import java.io.IOException;
import java.util.ArrayList;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import matching.NYTInstanceMatcher;

public class SearchThread implements Runnable{
	NYTInstanceMatcher matcher;
	String endpoint;
	String search;
	String sourceURI;
	
	public SearchThread(NYTInstanceMatcher matcher, String sourceURI, String search, String endpoint){
		this.matcher = matcher;
		this.endpoint = endpoint;
		this.search = search;
	}
	
	@Override
	public void run() {
		//List<Individual> candidates = null;
		ResultSet set;
		
		try {
			set = NYTInstanceMatcher.freeTextQueryOnline(endpoint, search);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		ArrayList<QuerySolution> results = new ArrayList<QuerySolution>();
		
		while(set.hasNext()){
			results.add(set.nextSolution());
		}
		
				
		if(results.size() == 1){
			QuerySolution solution = results.get(0);
			RDFNode node = solution.get("p");
			matcher.addMapping(sourceURI, node.asResource().getURI());
		}
		//System.out.println("online candidates: " + candidates);
	}

}
