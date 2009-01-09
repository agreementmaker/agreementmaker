package agreementMaker.application.mappingEngine.parametricStringMatcher;

import agreementMaker.application.mappingEngine.AbstractParameters;

public class ParametricStringParameters extends AbstractParameters {

	public final static String EDIT = "Levenshtein Edit Distance";
	public final static String JARO = "Jaro Winkler";
	public final static String QGRAM = "Q-Gram";
	public final static String SUB = "Substring metric";
	public final static String AMSUB = "AM Substring metric";
	
	public String measure = EDIT;
	
	public double localWeight = 0.7;
	public double labelWeight = 0;
	public double commentWeight = 0.2;
	public double seeAlsoWeight = 0.05;
	public double isDefinedByWeight = 0.05;
	
	
	public boolean redistributeWeights = true;
	
	//Normalization operations
	public boolean stem = true; //dogs --> dog, saying --> say
	public boolean removeStopWords = true;  //to a in..
	public boolean normalizeBlank = true;  // \t \n _ - and uppercase char becomes standard blank
	public boolean normalizeDigit = false; //remove numbers
	public boolean normalizeDiacritics = true; // à,ò...--> a, o
	public boolean normalizePunctuation = true; //. , ! ? ' " becomes blank
	//lowercase is always done
	
	public void normalizeWeights() {
		double totWeight = getTotWeight();
		if( totWeight > 1 ) {
			localWeight = localWeight / totWeight;
			labelWeight = labelWeight / totWeight;
			commentWeight = commentWeight / totWeight;
			seeAlsoWeight = seeAlsoWeight / totWeight;
			isDefinedByWeight = isDefinedByWeight / totWeight;
		}
	}
	
	public double getTotWeight() {
		return localWeight+labelWeight+commentWeight+seeAlsoWeight+isDefinedByWeight;
	}
	
}
