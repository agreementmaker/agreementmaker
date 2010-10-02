package am.app.mappingEngine;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;

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
	
	// Logger
	private static Logger log = Logger.getLogger(LexiconStore.class);
	
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

	public Lexicon getSourceOntLexicon(Ontology sourceOntology) {
		Lexicon sourceOntologyLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( sourceOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON );
		
		if( sourceOntologyLexicon == null ) {
			log.info("Building source ontology lexicon...");
			OntModel sourceModel = sourceOntology.getModel();
			
			// the synonym property, label property and definition property
			Property sourceSynonymProperty = sourceModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym");
			Property sourceLabelProperty = sourceModel.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
			Property sourceDefinitionProperty = sourceModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasDefinition");
			
			OntologyLexiconBuilder sourceOLB = new OntologyLexiconBuilder(sourceOntology, sourceLabelProperty, sourceSynonymProperty, sourceDefinitionProperty);
			
			sourceOntologyLexicon = sourceOLB.buildLexicon();
			sourceOntologyLexicon.settOntologyID(sourceOntology.getID());
			
			Core.getLexiconStore().registerLexicon(sourceOntologyLexicon); // register for reuse by other matchers
		}
		
		return sourceOntologyLexicon;
		
	}
	
	public Lexicon getTargetOntLexicon(Ontology targetOntology ) {
		Lexicon targetOntologyLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( targetOntology.getID(), LexiconRegistry.ONTOLOGY_LEXICON);
		
		if( targetOntologyLexicon == null ) {
			log.info("Building target ontology lexicon...");
			OntModel targetModel = targetOntology.getModel();
	
			// the synonym property, label property and definition property
			Property targetSynonymProperty = targetModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym");	
			Property targetLabelProperty = targetModel.getProperty("http://www.w3.org/2000/01/rdf-schema#label");
			Property targetDefinitionProperty = targetModel.getProperty("http://www.geneontology.org/formats/oboInOwl#hasDefinition");
			
			// STEP 1: Let's build the lexicons for the ontology
			OntologyLexiconBuilder targetOLB = new OntologyLexiconBuilder(targetOntology, targetLabelProperty, targetSynonymProperty, targetDefinitionProperty);

			targetOntologyLexicon = targetOLB.buildLexicon();
			targetOntologyLexicon.settOntologyID(targetOntology.getID());
			
			Core.getLexiconStore().registerLexicon(targetOntologyLexicon);
		}
		return targetOntologyLexicon;
	}
	
	public Lexicon getSourceWNLexicon(Ontology sourceOntology, Lexicon sourceOntologyLexicon) {
		Lexicon sourceWordNetLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( sourceOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
		
		if( sourceWordNetLexicon == null ) { // no lexicon available we must build it
			log.info("Building source WordNet lexicon...");
			// STEP 1: Let's build the lexicons for the ontology
			WordNetLexiconBuilder sourceOLB = new WordNetLexiconBuilder(sourceOntology, sourceOntologyLexicon);

			sourceWordNetLexicon = sourceOLB.buildLexicon();
			sourceWordNetLexicon.settOntologyID(sourceOntology.getID());
			
			Core.getLexiconStore().registerLexicon(sourceWordNetLexicon);
		}
		return sourceWordNetLexicon;
	}
	
	public Lexicon getTargetWNLexicon(Ontology targetOntology, Lexicon targetOntologyLexicon) {
		Lexicon targetWordNetLexicon = Core.getLexiconStore().getLexiconByOntIDAndType( targetOntology.getID(), LexiconRegistry.WORDNET_LEXICON);
		if( targetWordNetLexicon == null ) {
			log.info("Building target WordNet lexicon...");
			WordNetLexiconBuilder targetOLB = new WordNetLexiconBuilder(targetOntology, targetOntologyLexicon);
			
			targetWordNetLexicon = targetOLB.buildLexicon();
			targetWordNetLexicon.settOntologyID( targetOntology.getID() );
			
			Core.getLexiconStore().registerLexicon(targetWordNetLexicon);
		}
		return targetOntologyLexicon;
	}
}