package am.tools.StatementTranslator;

import java.util.List;

import am.app.mappingEngine.utility.MatchingPair;
import am.utility.Triple;


/**
 * A StatementTranslator translates a set of triples to a new vocabulary via a
 * alignment. The alignment maps concepts from the old ontology to the new
 * ontology.
 * 
 * @author Cosmin Stroe
 */
public class StatementTranslator {

	private List<MatchingPair> mapping;
	
	public StatementTranslator(List<MatchingPair> mapping) {
		this.mapping = mapping;
	}
	
	public List<Triple<String,String,String>> translate(List<Triple<String,String,String>> stmtList) {
		
		return null;
	}
	
}
