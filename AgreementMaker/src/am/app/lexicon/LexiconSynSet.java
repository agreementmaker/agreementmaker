package am.app.lexicon;

import java.io.PrintStream;
import java.util.List;

import am.app.mappingEngine.LexiconStore.LexiconRegistry;

import com.hp.hpl.jena.ontology.OntResource;

public interface LexiconSynSet {
	
	public void setOntologyConcept(OntResource r);
	public OntResource getOntologyConcept();
	
	public boolean isEditable();
	
	public void setGloss(String def);
	public String getGloss();
	
	public void addSynonym( String syn );
	public List<String> getSynonyms();
	public boolean hasSynonym( String syn );
	
	public void addRelatedSynSet( LexiconSynSet related );
	public List<LexiconSynSet> getRelatedSynSets();
	
	public void print( PrintStream out );
	public void setID( long id );
	public long getID();
	
	public LexiconRegistry getType();
}
