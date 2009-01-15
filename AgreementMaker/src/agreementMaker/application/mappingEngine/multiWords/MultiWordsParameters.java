package agreementMaker.application.mappingEngine.multiWords;

import agreementMaker.application.mappingEngine.AbstractParameters;
import agreementMaker.application.mappingEngine.StringUtil.NormalizerParameter;

public class MultiWordsParameters extends AbstractParameters {

	public final static String DICE  = "Dice’s Coefficient";
	public final static String JACCARD  = "Jaccard Similarity";
	public final static String EUCLIDEAN  = "Euclidean distance";
	public final static String COSINE  = "Cosine similarity";
	public final static String TFIDF  = "TF IDF";
	
	//selected measure
	public String measure;
	
	//localname, label, comment, seeAlso, isDefBy of the concept itself
	public boolean considerConcept; 
	//localname and label of neighbors will be added to multiword string: fathers, siblings, sons
	public boolean considerNeighbors;
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
	public boolean ignoreLocalNames;

	
	//I put the constructor to init default values when we run this method batch mode
	//and because it has some parameters that are not in input by user.
	public MultiWordsParameters() {
		super();
		measure = TFIDF;
		considerInstances = true;
		considerNeighbors = false;
		considerConcept = false;
		considerClasses = false;
		considerProperties = false;
		ignoreLocalNames = false
		
		;
		normParameter = new NormalizerParameter();
		normParameter.normalizeBlank = true;
		normParameter.normalizeDiacritics = true;
		normParameter.normalizeDigit = false;
		normParameter.normalizePunctuation = true;
		normParameter.removeStopWords = true;
		normParameter.stem = true;
	}
}
