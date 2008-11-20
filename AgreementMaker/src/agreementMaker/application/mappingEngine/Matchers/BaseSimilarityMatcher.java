package agreementMaker.application.mappingEngine.Matchers;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.ontology.Node;


public class BaseSimilarityMatcher extends AbstractMatcher { 

	
	public BaseSimilarityMatcher(int n, String s) {
		super(n, s);
	}
	
	/**Set all alignment sim to a random value between 0 and 1*/
	public Alignment alignTwoNodes(Node source, Node target) {
		
		
		// if the labels are equal, then return a similarity of 1
		if( source.getLabel() == target.getLabel() ) {
			String relation = Alignment.EQUIVALENCE;
			return new Alignment(source, target, 1, relation);
		}
		
		
		
		double sim = Math.random();
		String rel = Alignment.EQUIVALENCE;
		return new Alignment(source, target, sim, rel);
	}
	
	/**
	 * This function treats a string:
	 * 1) Removes dashes and underscores
	 * 2) Separates capitalized words, ( "BaseSimilarity" -> "Base Similarity" )
	 */
	private void treat_string( String input ) {
		
		String treated = new String();
		
		input.replace('-', ' ');
		input.replace('_', ' ');
		
		char[] current, next;
		current = new char[1];
		next = new char[1];
		for( int i = 0; i < (input.length() - 1); i++ ) {
			input.getChars(i, i+1, current, 0);
			input.getChars(i+1, i+2, next, 0);
			
			if( isLowercase(current[0]) && isUppercase(next[0]) ) {
				
			}
		}
		
		return;
	}

	private boolean isUppercase( char s ) {
		if( s >= 'A' && s <= 'Z') return true;
		return false;
	}
	
	private boolean isLowercase( char s ) {
		if( s >= 'a' && s <= 'z') return true;
		return false;
	}
	
}

