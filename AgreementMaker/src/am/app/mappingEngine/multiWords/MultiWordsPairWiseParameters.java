package am.app.mappingEngine.multiWords;

import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.oaei.OAEI_Track;

public class MultiWordsPairWiseParameters extends AbstractParameters {

	public final static String DICE  = "Dice�s Coefficient";
	public final static String JACCARD  = "Jaccard Similarity";
	public final static String EUCLIDEAN  = "Euclidean distance";
	public final static String COSINE  = "Cosine similarity";
	public final static String TFIDF  = "TF IDF";
	public final static String Overlap = "Overlap Coefficient";
	
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
	
	// use the definitions in the lexicons
	public boolean useLexiconDefinitions = false;
	public boolean useLexiconSynonyms = false;
	public boolean considerSuperClass = false;
	//extend synnyms
	public boolean extendSynonyms=false;
	
	

	
	//I put the constructor to init default values when we run this method batch mode
	//and because it has some parameters that are not in input by user.
	public MultiWordsPairWiseParameters() { super(); initVariables(); }
	public MultiWordsPairWiseParameters(double th, int maxS, int maxT) { super(th, maxS, maxT); initVariables(); }
	
	private void initVariables() {
		measure = TFIDF;
		considerInstances = false;
		considerNeighbors = false;
		considerConcept = true;
		considerClasses = false;
		considerProperties = false;
		ignoreLocalNames = true;
		normParameter = new NormalizerParameter();
		normParameter.normalizeBlank = true;
		normParameter.normalizeDiacritics = true;
		normParameter.normalizeDigit = false;
		normParameter.normalizePunctuation = true;
		normParameter.removeStopWords = true;
		normParameter.stem = true;
	}
	
	public MultiWordsPairWiseParameters initForOAEI2009() {
		measure = TFIDF;
		//only on concepts right now because it should be weighted differently
		considerInstances = false;
		considerNeighbors = false;
		considerConcept = true;
		considerClasses = false;
		considerProperties = false;
		ignoreLocalNames = true;
		normParameter = new NormalizerParameter();
		normParameter.setForOAEI2009();
		return this;
	}


	public MultiWordsPairWiseParameters initForOAEI2010(OAEI_Track currentTrack) throws Exception {
		
		switch( currentTrack ) {
		case Anatomy:
			measure = TFIDF;
			considerInstances = true;
			considerNeighbors = false;  // figure out if this helps.
			considerConcept = true;
			considerClasses = false;
			considerProperties = false;
			ignoreLocalNames = true; 
			
			useLexiconSynonyms = true; // May change later.
			considerSuperClass = true;
			break;
		
		case Benchmarks:
			measure = TFIDF;
			//only on concepts right now because it should be weighted differently
			considerInstances = true;
			considerNeighbors = false;
			considerConcept = true;
			considerClasses = false;
			considerProperties = false;
			ignoreLocalNames = true; 
			
			useLexiconSynonyms = true; // May change later.
			break;
			
		case Conference:
			throw new Exception("VMM is not used in Conference track for OAEI2010.");
			
		default:
			measure = TFIDF;
			//only on concepts right now because it should be weighted differently
			if( currentTrack == OAEI_Track.Benchmarks ) considerInstances = true;
			else { considerInstances = true; }
			considerNeighbors = false;
			considerConcept = true;
			considerClasses = false;
			considerProperties = false;
			if( currentTrack == OAEI_Track.Benchmarks ) ignoreLocalNames = false;
			else { ignoreLocalNames = true; } 
			
			useLexiconSynonyms = true; // May change later.
			break;
		}
				
		normParameter = new NormalizerParameter();
		normParameter.setForOAEI2009();
		return this;
	}
}
