package am.app.lexicon;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import am.app.mappingEngine.LexiconStore.LexiconRegistry;

import com.hp.hpl.jena.ontology.OntResource;

public interface Lexicon {

	
	public void addSynSet( LexiconSynSet t );
	public LexiconSynSet getSynSet( String wordForm );
	public LexiconSynSet getSynSet( OntResource ontRes );
	public void print( PrintStream out );
	public LexiconRegistry getRegistryEntry();
	
	public Map<OntResource, LexiconSynSet> getSynSetMap(); // should be replaced by an ITERATOR! TODO
	public int getOntologyID();
	public void settOntologyID( int id );
}
