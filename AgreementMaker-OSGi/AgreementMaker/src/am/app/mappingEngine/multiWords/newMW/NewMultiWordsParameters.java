package am.app.mappingEngine.multiWords.newMW;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.StringUtil.NormalizerParameter;

public class NewMultiWordsParameters extends DefaultMatcherParameters {

	public final static String DICE  = "Dice�s Coefficient";
	public final static String JACCARD  = "Jaccard Similarity";
	public final static String EUCLIDEAN  = "Euclidean distance";
	public final static String COSINE  = "Cosine similarity";
	public final static String TFIDF  = "TF IDF";
	
	//selected measure
	public String measure;
	
	public boolean includeParents;
	public boolean includeSiblings;
	public boolean includeChildren;
	
	
	//localname, label, comment, seeAlso, isDefBy of the concept itself
	//public boolean considerConcept; 
	//localname and label of neighbors will be added to multiword string: fathers, siblings, sons
	//public boolean considerNeighbors;
	//localname and label of individual will be added to multiword string
	public boolean considerInstances;
	
	//TODO: it could be good to add also this.
	//consider properties (only for classes)
	public boolean considerProperties;
	//consider classes (only for properties
	public boolean considerClasses;
	

	//Normalization operations
	//in the multi words methods normalization is always needed almost
	//So this is not a user parameter but can be used by developers
	NormalizerParameter normParameter;
	
	
	
	//In some ontologies localnames are just codes without meaning in those cases this parameter 
	//must be set to false;
	//it will affect both nodes and neighbors
	//public boolean ignoreLocalNames;
	
	// use the definitions in the lexicons
	public boolean useLexiconDefinitions = false;
	public boolean useLexiconSynonyms = false;
	//public boolean considerSuperClass = false;

	
	//I put the constructor to init default values when we run this method batch mode
	//and because it has some parameters that are not in input by user.
	public NewMultiWordsParameters() { super(); initVariables(); }
	public NewMultiWordsParameters(double th, int maxS, int maxT) { super(th, maxS, maxT); initVariables(); }
	
	private void initVariables() {
		measure = TFIDF;
		includeParents = false;
		includeSiblings = false;
		includeChildren = false;
		considerInstances = false;
		considerClasses = false;
		considerProperties = false;
		//ignoreLocalNames = true;
		normParameter = new NormalizerParameter();
		normParameter.normalizeBlank = true;
		normParameter.normalizeDiacritics = true;
		normParameter.normalizeDigit = false;
		normParameter.normalizePunctuation = true;
		normParameter.removeStopWords = true;
		normParameter.stem = true;
	}
	
}
