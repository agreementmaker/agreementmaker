package am.app.lexicon;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntResource;

import am.app.Core;
import am.app.mappingEngine.LexiconStore.LexiconRegistry;
import am.app.mappingEngine.StringUtil.Normalizer;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.baseSimilarity.BaseSimilarityMatcher;

public class GeneralLexiconSynSet implements LexiconSynSet {

	protected String gloss;  // aka Definition
	protected List<String> synonyms = new LinkedList<String>(); // the SynSet
	
	protected OntResource concept;
	protected LexiconRegistry type;
	
	public GeneralLexiconSynSet( LexiconRegistry t ) {
		type = t;
	}
	
	// a list of resources that share this synset 
	//List<OntResource> resourceList = new LinkedList<OntResource>();
	
	List<LexiconSynSet> relatedSynSets = new LinkedList<LexiconSynSet>();
	
	public boolean cleanStringWhenAddingSynonym = true;
	private long id = 0;  // id is initialized by the Lexicon this term is added to. (side effect: synset IDs cannot be shared between lexicons)
	
	//public OntologyLexiconSynSet(long id) {setID(id);}
	
	@Override public boolean isEditable() { return true; } // yes, we can edit this lexicon synset

	@Override 
	public void setGloss(String def) {
		gloss = BaseSimilarityMatcher.removeLines(def); 
	}
	@Override public String getGloss() { return gloss; }

	@Override 
	public void addSynonym(String syn) { 
		// we must check to see if the synonyms are duplicated
		
		if( cleanStringWhenAddingSynonym ) {
			// clean the syn string.
			NormalizerParameter normParam = new NormalizerParameter();
			normParam.normalizeBlank = true;
			normParam.normalizeDiacritics = true;
			normParam.normalizeDigit = false;
			normParam.normalizePunctuation = true;
			normParam.removeStopWords = false;
			normParam.stem = false;
			
			Normalizer norm = new Normalizer(normParam);
			
			syn = norm.normalize(syn);
		}
		
		// search for exact match of synonym.
		if( !hasSynonym(syn) ) {
			synonyms.add(syn);
			if( Core.DEBUG_ONTOLOGYLEXICONSYNSET ) System.out.println("OntologyLexiconSynSet " + id + ": added synonym \"" + syn + "\"");
		}

	}
	
	@Override public List<String> getSynonyms() { return synonyms; }

	@Override public boolean hasSynonym(String syn) { 
		return synonyms.contains( syn ); // exact match checking
	}

	// add a resource that shares this synset
	public void setOntologyConcept( OntResource or ) { concept = or; }
	public OntResource getOntologyConcept() { return concept; }

	@Override
	public void print(PrintStream out) {
		
		// id
		out.println("Synset " + id + ": " + concept.getLocalName() );
		
		/*
		// resources
		if( !resourceList.isEmpty() ) {
			out.print( resourceList.size() + " resources:");
			Iterator<OntResource> rListIter = resourceList.iterator();
			while( rListIter.hasNext() ) {
				out.print(rListIter.next().getLocalName());
				if( rListIter.hasNext() ) out.print(", ");
			}
			out.println(".");
		} */
		
		// definition
		out.println("Definition: " + gloss);
		
		//synonyms
		out.print("Synonyms: ");
		for( Iterator<String> i = synonyms.iterator(); i.hasNext(); ) { 
			out.print( i.next() ); 
			if( i.hasNext() ) out.print(", ");	
		}
		out.println(".");
		
		// related synsets
		if( !relatedSynSets.isEmpty() ) {
			Iterator<LexiconSynSet> synsetIter = relatedSynSets.iterator();
			out.print("Related SynSets: ");
			while( synsetIter.hasNext() ) {
				out.print( synsetIter.next().getID() );
				if( synsetIter.hasNext() ) out.print(", ");
			}
			out.println(".");
		}
	}

	@Override
	public void setID(long id) { this.id  = id; }
	public long getID() { return id; }

	@Override
	public void addRelatedSynSet(LexiconSynSet related) {
		if( !relatedSynSets.contains(related) ) relatedSynSets.add(related);
	}
	@Override public List<LexiconSynSet> getRelatedSynSets() { return relatedSynSets;	}

	@Override
	public LexiconRegistry getType() { return type; }
	
	@Override
	public boolean isEmpty() {
		return gloss == null && synonyms.isEmpty();
	}
	
	@Override
	public String toString() {
		return id + ":" + concept.getLocalName() + " (" + synonyms.size() + " synonyms)" ;
	}
}
