package parallel;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.json.JSONException;

import am.app.mappingEngine.StringUtil.StringMetrics;

import freebase.Freebase;

import matching.Instance;
import matching.NYTInstanceMatcher;

public class MQLSearchThread implements Runnable{
	NYTInstanceMatcher matcher;
	String search;
	String sourceURI;
	String type;
	int n;
	
	public MQLSearchThread(NYTInstanceMatcher matcher, int n, String sourceURI, String type, String search){
		this.matcher = matcher;
		this.search = search;
		this.type = type;
		this.sourceURI = sourceURI;
		this.n = n;
	}
	
	@Override
	public void run() {
		//List<Individual> candidates = null;
		List<Instance> instances = null;
			
		try {
			instances = Freebase.freeTextQueryOnline(search, type, n);
		} catch (JSONException e) {
			e.printStackTrace();
			matcher.deleteRunningThread(n);
			return;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			matcher.deleteRunningThread(n);
			return;
		}
		
		if(instances == null){
			matcher.deleteRunningThread(n);
			return;
		}
		
		if(instances.size() == 1){
			matcher.addSingleResult();
			Instance instance = instances.get(0);
			
			String label = instance.getLabel();
			//System.out.println(label);
			
			double sim = StringMetrics.AMsubstringScore(label, search);
			
			if(sim > matcher.AMSubstringThreshold){
				System.out.println("Creating mapping: " + sourceURI + " " + instance.getUri());
				matcher.addMapping(sourceURI, instance.getUri());
			}
		}
		else if(instances.size() > 1){
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

