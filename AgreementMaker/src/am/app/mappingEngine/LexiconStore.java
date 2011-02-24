package am.app.mappingEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import am.app.Core;
import am.app.lexicon.Lexicon;
import am.app.lexicon.LexiconBuilder;
import am.app.lexicon.LexiconBuilderParameters;
import am.app.lexicon.ontology.OntologyLexiconBuilder;
import am.app.lexicon.wordnet.WordNetLexiconBuilder;
import am.app.ontology.Ontology;
import am.app.ontology.OntologyChangeEvent;
import am.app.ontology.OntologyChangeListener;
import am.app.ontology.OntologyChangeEvent.EventType;

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
	
	public Lexicon build(LexiconRegistry whichOne, Ontology ont) throws Exception {

		if( params == null )
			throw new Exception("No parameters have been set for the lexicon builder.\nPlease use the Lexicon Builder Dialog to configure the parameters.\n\nThe Lexicon Builder Dialog can be accessed from the \"Lexicons -> Build all ...\" menu entry.");
		
		switch( whichOne ) {
		case ONTOLOGY_LEXICON:
		{
			if( Core.getInstance().getSourceOntology() == ont ) {  // source
				OntologyLexiconBuilder sourceOLB = new OntologyLexiconBuilder(ont, params.sourceUseLocalname, 
						params.sourceSynonyms, params.sourceSynonyms, params.sourceDefinitions);
	
				Lexicon sourceOntologyLexicon = sourceOLB.buildLexicon();
				sourceOntologyLexicon.setOntologyID(ont.getID());
	
				Core.getLexiconStore().registerLexicon(sourceOntologyLexicon); // register for reuse by other matchers
				return sourceOntologyLexicon;
			} else {  // target
				OntologyLexiconBuilder targetOLB = new OntologyLexiconBuilder(ont, params.targetUseLocalname, 
						params.targetSynonyms, params.targetSynonyms, params.targetDefinitions);
	
				Lexicon targetOntologyLexicon = targetOLB.buildLexicon();
				targetOntologyLexicon.setOntologyID(ont.getID());
	
				Core.getLexiconStore().registerLexicon(targetOntologyLexicon); // register for reuse by other matchers
				return targetOntologyLexicon;
			}
		}	
		case WORDNET_LEXICON:
		{
			WordNetLexiconBuilder sourceOLB = new WordNetLexiconBuilder(ont, getLexicon(ont.getID(), LexiconRegistry.ONTOLOGY_LEXICON));

			Lexicon sourceWordNetLexicon = sourceOLB.buildLexicon();
			sourceWordNetLexicon.setOntologyID(ont.getID());

			Core.getLexiconStore().registerLexicon(sourceWordNetLexicon);
			return sourceWordNetLexicon;
		}
		default:
			throw new Exception("Invalid lexicon type.");
		}
		
	}
	public void buildAll() throws Exception {
		if( !Core.getInstance().ontologiesLoaded() ) {
			throw new RuntimeException("You must load the source and target ontologies before you can build lexicons.");
		}
		
		build(LexiconRegistry.ONTOLOGY_LEXICON, Core.getInstance().getSourceOntology() );
		build(LexiconRegistry.ONTOLOGY_LEXICON, Core.getInstance().getTargetOntology() );
		
		build(LexiconRegistry.WORDNET_LEXICON, Core.getInstance().getSourceOntology() );
		build(LexiconRegistry.WORDNET_LEXICON, Core.getInstance().getTargetOntology() );
	}
		

	public Lexicon getLexicon( int ontologyID, LexiconRegistry type ) throws Exception {
		for( Lexicon lex : lexList ) {
			if( lex.getOntologyID() == ontologyID && lex.getType() == type ) return lex;
		}
		// lexicon was not found.
		return build(type, Core.getInstance().getOntologyByID(ontologyID));
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

	public void setParameters(LexiconBuilderParameters params) {
		this.params = params;	
	}
}