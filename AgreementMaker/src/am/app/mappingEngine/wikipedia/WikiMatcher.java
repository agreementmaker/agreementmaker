package am.app.mappingEngine.wikipedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.hierarchy.Utilities;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.ontology.Node;

public class WikiMatcher extends AbstractMatcher {
	WikiEngine engine;	
	double threshold = 0.85d;
	
	public WikiMatcher(){
		engine = new WikiEngine();
	}
	
	@Override
	protected void matchStart() {
		super.matchStart();
		//categories.initialize();
	}
	
	
	@Override
	protected SimilarityMatrix alignClasses(ArrayList<Node> sourceClassList,
			ArrayList<Node> targetClassList) throws Exception {
		SimilarityMatrix matrix = new ArraySimilarityMatrix(sourceClassList.size(), targetClassList.size(), alignType.aligningClasses);
		
		for (int i = 0; i < sourceClassList.size(); i++) {
			String name = sourceClassList.get(i).getLocalName();
			name = Utilities.separateWords(name);
			System.out.println(name);
			System.out.println(engine.getPages(name));
			System.out.println(engine.getCategories(name));
		}
		
		/*
		System.out.println("SOURCE CLASSES");
		for (int i = 0; i < sourceClassList.size(); i++) {
			String name = sourceClassList.get(i).getLocalName();
			String result = categories.search(name, threshold);
			if(result !=null){
				System.out.println("name: "+name + " found:"+ result);
				System.out.println(categories.fromIdToValue(categories.getSuperclasses((Integer)categories.categoriesByName.get(result))));
			}
		}
		
		System.out.println("TARGET CLASSES");
		for (int i = 0; i < targetClassList.size(); i++) {
			String name = targetClassList.get(i).getLocalName();
			String result = categories.search(name, threshold);
			if(result != null){
				System.out.println("name: "+name + " found:"+ result);
				System.out.println(categories.fromIdToValue(categories.getSuperclasses((Integer)categories.categoriesByName.get(result))));
			}
		}*/
		
		
		return matrix;
	}
}
