package am.app.mappingEngine.StringUtil;

import am.app.mappingEngine.oaei2010.OAEI2010MatcherParameters.Track;

public class NormalizerParameter {
	//Normalization operations
	public boolean stem = true; //dogs --> dog, saying --> say
	public boolean removeStopWords = true;  //to a in..
	public boolean normalizeBlank = true;  // \t \n _ - and uppercase char becomes standard blank
	public boolean normalizeDigit = false; //remove numbers
	public boolean normalizeDiacritics = true; // �,�...--> a, o
	public boolean normalizePunctuation = true; //. , ! ? ' " becomes blank
	private boolean normalizeSlashes = false;
	//lowercase is always done
	
	public void setAllTrue(){
		stem = true; //dogs --> dog, saying --> say
		removeStopWords = true;  //to a in..
		normalizeBlank = true;  // \t \n _ - and uppercase char becomes standard blank
		normalizeDigit = true; //remove numbers
		normalizeDiacritics = true; // �,�...--> a, o
		normalizePunctuation = true;
	}

	public void setAllfalse() {
		// TODO Auto-generated method stub
		stem = false; //dogs --> dog, saying --> say
		removeStopWords = false;  //to a in..
		normalizeBlank = false;  // \t \n _ - and uppercase char becomes standard blank
		normalizeDigit = false; //remove numbers
		normalizeDiacritics = false; // �,�...--> a, o
		normalizePunctuation = false;
	}
	
	public void setForOAEI2009(){
		stem = true; //dogs --> dog, saying --> say
		removeStopWords = true;  //to a in..
		normalizeBlank = true;  // \t \n _ - and uppercase char becomes standard blank
		normalizeDigit = false; //remove numbers
		normalizeDiacritics = true; // �,�...--> a, o
		normalizePunctuation = true;
	}

	public void setForOAEI2010(Track t) {
		stem = false;
		removeStopWords = true;
		normalizeBlank = true;
		normalizeDigit = false;
		normalizeDiacritics = true;
		normalizePunctuation = true;
		normalizeSlashes  = true;
		
	}
}
