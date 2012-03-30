package lexicalmatcherjaws.internal;

/** Work in progress */

public class StringAnalyzer {

	public static enum StringProperties {
		case_normalization,  // converting each string into all lower case
		diacritics_suppression, // converting accented letters into their non-accented versions
		blank_normalization, // normalizing all blank characters ( blank, tab, CR, or any sequence of these )
		link_stripping, // removing dashes and apostrophes
		digit_suppression, // when dealing with mixed words and numbers, remove the numbers ( book2341 -> book )
		punctuation_elimination, // remove punctuation signs ( C.D. -> CD )
		
		camel_type_separation // ( CammelNotationWord -> Camel Notation Word )
	}
	
	public static enum StringSimilarities {
		camel_type_notation		
	}
	
	// This will be used later.
	public static enum CharacterProperties {
		capitalized,
		lowercase,
		alpha,
		digit,
		punctuation,
		computer_used
	}
	
	public StringAnalyzer( String input, String input2) {
		
		// character analysis
		
		
		
		
		
	}
	
	public boolean isCamelType( String in ) {
		
		
		return false;
	}
	
}
