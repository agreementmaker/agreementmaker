package am.app.mappingEngine;

import java.util.ArrayList;
import java.util.List;

import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconBuilder;
import am.app.lexicon.ontology.OntologyLexiconBuilder;
import am.app.lexicon.wordnet.WordNetLexiconBuilder;
import am.app.ontology.Ontology;

public class LexiconStore {

	/**
	 * Lexicon Registry
	 * @author cosmin
	 *
	 */
	public static enum LexiconRegistry {
		ONTOLOGY_LEXICON		("Ontology Lexicon", OntologyLexiconBuilder.class),
		WORDNET_LEXICON			("WordNet Lexicon" , WordNetLexiconBuilder.class);
		
		/* Don't change anything below this line .. unless you intend to. */
		private String name;
		private Class<? extends LexiconBuilder> lexBuilder;
		
		LexiconRegistry( String n, Class<? extends LexiconBuilder> b ) { name = n; lexBuilder = b;}
		public String getLexiconName() { return name; }
		public Class<? extends LexiconBuilder> getLexiconBuilderClass() { return lexBuilder; }
		public String toString() { return name; }
	}
	
	/**
	 * Lexicon Store
	 */
	
	List<Lexicon> lexList = new ArrayList<Lexicon>();
	
	public void clear() { }
	
	public void build(LexiconRegistry whichOne) {
		
		switch( whichOne ) {
		case ONTOLOGY_LEXICON:
			Ontology source;
			Ontology target;
			if( Core.getInstance().getSourceOntology() != null ) source = Core.getInstance().getSourceOntology();
			if( Core.getInstance().getTargetOntology() != null ) target = Core.getInstance().getTargetOntology();
			//OntologyLexiconBuilder ontLexBuilder = new OntologyLexiconBuilder(source, label, synonym, definition)
		}
		
	}
	public void buildAll() { }
		

	public Lexicon getLexicon( LexiconRegistry whichOne ) {
		for( Lexicon lex : lexList ) {
			if( lex.getRegistryEntry() == whichOne ) return lex;
		}
		return null;
	}
	
	public List<Lexicon> getLexiconsByOntID( int id ) {
		ArrayList<Lexicon> ret = new ArrayList<Lexicon>();
		for( Lexicon lex : lexList ) {
			if( lex.getOntologyID() == id ) ret.add(lex);
		}
		return ret;
	}

	public Lexicon getLexiconByOntIDAndType( int id , LexiconRegistry type ) {
		for( Lexicon lex : lexList ) {
			if( lex.getOntologyID() == id && lex.getRegistryEntry() == type) return lex;
		}
		return null;
	}
	
	public void registerLexicon(Lexicon sourceLexicon) {
		lexList.add(sourceLexicon);
		
	}
	
}