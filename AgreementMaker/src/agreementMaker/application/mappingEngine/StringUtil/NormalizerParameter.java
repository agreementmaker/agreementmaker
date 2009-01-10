package agreementMaker.application.mappingEngine.StringUtil;

public class NormalizerParameter {
	//Normalization operations
	public boolean stem = true; //dogs --> dog, saying --> say
	public boolean removeStopWords = true;  //to a in..
	public boolean normalizeBlank = true;  // \t \n _ - and uppercase char becomes standard blank
	public boolean normalizeDigit = false; //remove numbers
	public boolean normalizeDiacritics = true; // à,ò...--> a, o
	public boolean normalizePunctuation = true; //. , ! ? ' " becomes blank
	//lowercase is always done
	
}
