package am.app.lexicon;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.tools.LexiconLookup.LexiconLookupPanel;

import com.hp.hpl.jena.ontology.OntResource;

public interface Lexicon {

	
	public void addSynSet( LexiconSynSet t );
	public LexiconSynSet getSynSet( String wordForm );  // requires exact match
	public LexiconSynSet getSynSet( OntResource ontRes );
	
	public List<LexiconSynSet> lookup( String term );  // used to search the lexicon
	
	public void print( PrintStream out );
	public LexiconRegistry getType();
	
	public Map<OntResource, LexiconSynSet> getSynSetMap(); // should be replaced by an ITERATOR! TODO
	public int getOntologyID();
	public void setOntologyID( int id );
	
	public void setLookupPanel(LexiconLookupPanel wnlp);  // used to link this lookup panel with its lexicon.
	public LexiconLookupPanel getLookupPanel();
	
	public int size();  // the number of entries in the lexicon
	
	/**
	 * Return a complete list of synonyms.
	 * 
	 * This list includes synonyms from the related synsets.
	 * Using Set as the return value because we want to avoid duplicate entries in the synonym list.
	 */
	public Set<String> extendSynSet(LexiconSynSet synset);
}
