package am.matcher.lod.wikipedia;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.matcher.lod.hierarchy.Utilities;

public class WikiMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -522989313702239347L;
	
	WikiEngine engine;	
	double threshold = 0.85d;
	
	public WikiMatcher(){
		engine = new WikiEngine();
	}
	
	@Override
	public void matchStart() {
		super.matchStart();
		//categories.initialize();
	}
	
	
	@Override
	protected SimilarityMatrix alignClasses( List<Node> sourceClassList,
			List<Node> targetClassList) throws Exception {
		SimilarityMatrix matrix = new ArraySimilarityMatrix(sourceOntology, targetOntology, alignType.aligningClasses);
		
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
