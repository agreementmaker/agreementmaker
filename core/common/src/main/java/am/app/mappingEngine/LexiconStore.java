package am.app.mappingEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconBuilder;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.lexicon.ontology.OntologyLexiconBuilder;
import am.app.lexicon.subconcept.STLexiconBuilder;
import am.app.lexicon.wordnet.WordNetLexiconBuilder;
import am.app.ontology.Ontology;
import am.app.ontology.OntologyChangeEvent;
import am.app.ontology.OntologyChangeEvent.EventType;
import am.app.ontology.OntologyChangeListener;

public class LexiconStore implements OntologyChangeListener {
	
	/**
	 * Lexicon Registry
	 * @author cosmin
	 *
	 */
	public static enum LexiconRegistry {
		ONTOLOGY_LEXICON		("Ontology Lexicon", "ONT", OntologyLexiconBuilder.class),
		WORDNET_LEXICON			("WordNet Lexicon" , "WNet", WordNetLexiconBuilder.class);
		
		/* Don't change anything below this line .. unless you intend to. */
		private String name;
		private String shortName;
		private Class<? extends LexiconBuilder> lexBuilder;
		
		LexiconRegistry( String n, String sn, Class<? extends LexiconBuilder> b ) { name = n; shortName = sn; lexBuilder = b;}
		public String getLexiconName() { return name; }
		public String getShortName() { return shortName; }
		public Class<? extends LexiconBuilder> getLexiconBuilderClass() { return lexBuilder; }
		public String toString() { return name; }
	}
	
	/**
	 * Lexicon Store
	 */
	
	public LexiconStore() {
		// TODO Auto-generated constructor stub
	}
	
	// Logger
//	private static Logger log = Logger.getLogger(LexiconStore.class);
	
	List<Lexicon> lexList = new ArrayList<Lexicon>();

	private LexiconBuilderParameters params;
	
	public void clear() {
		params = null;
		lexList = new ArrayList<Lexicon>();
	}
	
	/**
	 * This method build an ontology lexicon.
	 * @param whichOne A lexicon defined in the LexiconRegistry.
	 * @param ont The ontology for which this lexicon is built.
	 * @return The lexicon that was built.
	 * @throws Exception An exception is thrown if no parameters have been set.
	 */
	public Lexicon build(LexiconRegistry whichOne, Ontology ont) throws Exception {

		if( params == null )
			throw new Exception("No parameters have been set for the lexicon builder.\nPlease use the Lexicon Builder Dialog to configure the parameters.\n\nThe Lexicon Builder Dialog can be accessed from the \"Lexicons -> Build all ...\" menu entry.");
		
		switch( whichOne ) {
		case ONTOLOGY_LEXICON:
		{
			if( params.sourceOntology.equals(ont) ) {  // source
				OntologyLexiconBuilder sourceOLB;
				if( params.sourceUseSCSLexicon ) {
					sourceOLB = new STLexiconBuilder(ont, params.sourceUseLocalname, 
							params.sourceSynonyms, params.sourceDefinitions);
				} else {
					sourceOLB = new OntologyLexiconBuilder(ont, params.sourceUseLocalname,
							params.sourceSynonyms, params.sourceDefinitions);
				}
	
				Lexicon sourceOntologyLexicon = sourceOLB.buildLexicon();
				sourceOntologyLexicon.setOntologyID(ont.getID());
	
				return sourceOntologyLexicon;
			} else {  // target
				OntologyLexiconBuilder targetOLB;
				if( params.targetUseSCSLexicon ) {
					targetOLB = new STLexiconBuilder(ont, params.targetUseLocalname,
						params.targetSynonyms, params.targetDefinitions);
				} else {
					targetOLB = new OntologyLexiconBuilder(ont, params.targetUseLocalname,
						params.targetSynonyms, params.targetDefinitions);
				}
				
				Lexicon targetOntologyLexicon = targetOLB.buildLexicon();
				targetOntologyLexicon.setOntologyID(ont.getID());

				return targetOntologyLexicon;
			}
		}	
		case WORDNET_LEXICON:
		{
			WordNetLexiconBuilder sourceOLB = new WordNetLexiconBuilder(ont, getLexicon(ont, LexiconRegistry.ONTOLOGY_LEXICON));

			Lexicon sourceWordNetLexicon = sourceOLB.buildLexicon();
			sourceWordNetLexicon.setOntologyID(ont.getID());
			
			return sourceWordNetLexicon;
		}
		default:
			throw new Exception("Invalid lexicon type.");
		}
		
	}
	public void buildAll() throws Exception {
		
		if( params == null ) {
			throw new NullPointerException("You must set parameters to the lexicon store.");
		}
		
		Lexicon lexSourceOnt = build(LexiconRegistry.ONTOLOGY_LEXICON, params.sourceOntology );
		registerLexicon(lexSourceOnt); // register for reuse by other matchers
		
		Lexicon lexTargetOnt = build(LexiconRegistry.ONTOLOGY_LEXICON, params.targetOntology );
		registerLexicon(lexTargetOnt); // register for reuse by other matchers
		
		Lexicon lexSourceWN = build(LexiconRegistry.WORDNET_LEXICON, params.sourceOntology );
		registerLexicon(lexSourceWN); // register for reuse by other matchers
		
		Lexicon lexTargetWN = build(LexiconRegistry.WORDNET_LEXICON, params.targetOntology );
		registerLexicon(lexTargetWN); // register for reuse by other matchers
		
	}
	
	public void buildAll( LexiconBuilderParameters params ) throws Exception{
		setParameters(params);
		buildAll();
	}
		

	// FIXME: This method should return null, not try to automatically build the lexicon.
	public Lexicon getLexicon(Ontology ont, LexiconRegistry type ) throws Exception  {
		for( Lexicon lex : lexList ) {
			if( lex.getOntologyID() == ont.getID() && lex.getType() == type ) return lex;
		}
		// lexicon was not found.
		return build(type, ont);
	}
	
	public List<Lexicon> getLexicons( int ontologyID ) {
		ArrayList<Lexicon> ret = new ArrayList<Lexicon>();
		for( Lexicon lex : lexList ) {
			if( lex.getOntologyID() == ontologyID ) ret.add(lex);
		}
		return ret;
	}


	public void registerLexicon(Lexicon sourceLexicon) {
		
		// check to make sure that we dont have the same type of lexicon for an ontology
		for( Lexicon l : lexList ) {
			if( l.getOntologyID() == sourceLexicon.getOntologyID() && 
					l.getType() == sourceLexicon.getType() ) {
				lexList.remove(l);
				break;
			}
		}
		
		lexList.add(sourceLexicon);
		
	}

	public void unregisterLexicon(Lexicon lexicon) {
		for( Lexicon l : lexList ) {
			if( l.getOntologyID() == lexicon.getOntologyID() && 
					l.getType() == lexicon.getType() ) {
				lexList.remove(l);
				break;
			}
		}
	}
	
	@Override
	public void ontologyChanged(OntologyChangeEvent e) {
		if( e.getEvent() == EventType.ONTOLOGY_REMOVED ) {
			Iterator<Lexicon> lexIter = lexList.iterator();
			while( lexIter.hasNext() ) {
				Lexicon currentLexicon = lexIter.next();
				if( currentLexicon.getOntologyID() == e.getOntologyID() ) {
					// this ontology was removed, remove its lexicon.
					lexIter.remove();
				}
			}
		}
	}

	public void setParameters(LexiconBuilderParameters params) { this.params = params; }
	public LexiconBuilderParameters getParameters() { return this.params; }
}