package parallel;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.experimental.theories.internal.AllMembersSupplier;

import am.app.mappingEngine.StringUtil.StringMetrics;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

import matching.NYTInstanceMatcher;

public class SPARQLSearchThread implements Runnable{
	NYTInstanceMatcher matcher;
	String endpoint;
	String search;
	String sourceURI;
	int n;
	
	public SPARQLSearchThread(NYTInstanceMatcher matcher, int n, String sourceURI, String search, String endpoint){
		this.matcher = matcher;
		this.endpoint = endpoint;
		this.search = search;
		this.sourceURI = sourceURI;
		this.n = n;
	}
	
	@Override
	public void run() {
		//List<Individual> candidates = null;
		ResultSet set;
		
		try {
			set = NYTInstanceMatcher.freeTextQueryOnline(endpoint, search, n);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		if(set == null){
			matcher.deleteRunningThread(n);
			return;
		}
		
		ArrayList<QuerySolution> results = new ArrayList<QuerySolution>();
		
		while(set.hasNext()){
			results.add(set.nextSolution());
		}
		
				
		if(results.size() == 1){
			matcher.addSingleResult();
			QuerySolution solution = results.get(0);
			RDFNode node = solution.get("p");
			//System.out.println(node);
			RDFNode label = solution.get("name");
			//System.out.println(label);
			
			double sim = StringMetrics.AMsubstringScore(label.asLiteral().getString(), search);
			
			if(sim > matcher.AMSubstringThreshold){
				System.out.println("Creating mapping: " + sourceURI + " " + node.asResource().getURI());
				matcher.addMapping(sourceURI, node.asResource().getURI());
			}
		}
		else if(results.size() > 1){
			matcher.addAmbiguous();
		}
		else{
			matcher.addNoResults();
		}
		//System.out.println("online candidates: " + candidates);
		System.out.println("Thread " + n + " ending");
		matcher.deleteRunningThread(n);
	}

	public Integer getN() {
		return n;
	}

}
