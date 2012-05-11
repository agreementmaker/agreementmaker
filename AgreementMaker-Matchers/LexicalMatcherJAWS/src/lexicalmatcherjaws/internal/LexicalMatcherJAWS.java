/*
 * Change log: Please update this. (Same information can also be seen from CVS)
 * 
 * July 6th 2010 - Initial work. - Cosmin Stroe.
 * 
 * This is a wordnet lexical matcher using 
 * Java API for WordNet Searching (JAWS): http://lyle.smu.edu/~tspell/jaws/ 
 * 
 */

package lexicalmatcherjaws.internal;

// JAWS Classes
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Node;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class LexicalMatcherJAWS extends AbstractMatcher {

	@Override 
	public String getDescriptionString() { return "A lexical matcher using WordNet as the lexicon."; }
	
	//  **** Working Variables ****
	
	private WordNetDatabase WordNet;
	
	private HashMap<Node,LogicalDescription> descriptions;
	
	public LexicalMatcherJAWS() {
		super();
		setName("Lexical Matcher: JAWS");
		needsParam = false;  // this is a variable inherited from AbstractMatcher
		
		
		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";

		System.setProperty("wordnet.database.dir", wordnetdir);
		
		
		// Instantiate wordnet.
		WordNet = WordNetDatabase.getFileInstance();
		
		// Instantiate the description map.
		descriptions = new HashMap<Node, LogicalDescription>();
		
		/* reference implementation
		NounSynset nounSynset;
		NounSynset[] hyponyms;

		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets("fly", SynsetType.NOUN);
		for( int i = 0; i < synsets.length; i++ ) {
			String[] words = synsets[i].getWordForms();
			System.out.println("Synset "+ i +": " + synsets[i].getDefinition() +".");
			for( int j = 0; j < words.length; j++ ) {
				if( j > 0 && j < words.length ) System.out.print(", ");
				System.out.print(words[j]);
			}
			System.out.println(".");
		}
		
		System.exit(0);
		*/
	}
	
	
	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		
		
		List<Node> sourceClassList = sourceOntology.getClassesList();
		List<Node> targetClassList = targetOntology.getClassesList();
		
		
		for( Node sourceConcept : sourceClassList ) {
			LogicalDescription sourceLD = new LogicalDescription(sourceConcept);
			descriptions.put(sourceConcept, sourceLD);
		}
		for( Node targetConcept : targetClassList ) {
			LogicalDescription targetLD = new LogicalDescription(targetConcept);
			descriptions.put(targetConcept, targetLD);
		}
		
		
		Iterator<LogicalDescription> classdescriptionIter = descriptions.values().iterator();
		while( classdescriptionIter.hasNext() ) {
			System.out.println( classdescriptionIter.next().toString() );
		}
		
		this.cancel(true);
		
	};
	
	@Override
	public Mapping alignTwoNodes( Node source, Node target, alignType typeOfNodes, SimilarityMatrix matrix ) {
		
		
		LogicalDescription sourceBreakdown = new LogicalDescription( source );
		LogicalDescription targetBreakdown = new LogicalDescription( target );
		
		//System.out.println("Source(" + source.getLabel() + "): " + sourceBreakdown.toString() );
		//System.out.println("Target(" + target.getLabel() + "): " + targetBreakdown.toString() );
		//System.out.println("--------------------------------------");
		

		// identify the direct object
		
		LogicalDescription currentWord_source = descriptions.get(source);
		LogicalDescription currentWord_target = descriptions.get(target);
		
		
		Synset[] a = WordNet.getSynsets( currentWord_source.toString() );
		Synset[] b = WordNet.getSynsets( currentWord_target.toString() );
		
		
		Synset[] a_n_b = getIntersection(a,b);
		
		if( a_n_b == null ) {
			// the very first word is not the same, check to see if they share a common hypernym
			
			Synset[] sourceHypernyms = removeDuplicates( getHypernyms( a ) );
			Synset[] targetHypernyms = removeDuplicates( getHypernyms( b ) );
			
			Synset[] hypernyms_a_n_b = getIntersection(sourceHypernyms, targetHypernyms);
			
			if( hypernyms_a_n_b != null ) {
				// we have a direct ancestor
				
				double sim = 1.0;
				
				return new Mapping(source, target, sim);
			}
			
			
		}
		
		// work with nouns.
		
		return null;

	}
		
	/** This function returns the hypernyms of all of the synsets passed in. */
	private Synset[] getHypernyms(Synset[] a) {
		
		// use an arraylist, easier to work with
		ArrayList<Synset> hypernymList = new ArrayList<Synset>();
		
		for( Synset currentSynset : a ) {
			if( currentSynset.getType() == SynsetType.NOUN ) {
				//  only noun synsets have hypernyms (right????)
				NounSynset currentNounSynset = (NounSynset) currentSynset;
				
				NounSynset[] currentHypernyms = currentNounSynset.getHypernyms();
				
				for( NounSynset currentHypernym : currentHypernyms ) {
					hypernymList.add( (Synset) currentHypernym );
				}
				
			}
		}
		
		if( hypernymList.size() == 0 ) return null;  // no hypernyms found
		
		// we found some hypernyms, return an array
		Synset[] output = new Synset[hypernymList.size()];
		for( int i = 0; i < hypernymList.size(); i++ ) {
			output[i] = hypernymList.get(i);
		}
		
		return output;
	}

	/** This function returns the intersection set between a[] and b[]. */
	private Synset[] getIntersection( Synset[] a, Synset[] b) {
		
		if( a == null || b == null ) return null;
		
		Synset[] smaller;
		Synset[] larger;
		
		if( a.length > b.length ) { smaller = b; larger = a; } 
		else { smaller = a; larger = b; }
		
		Synset[] temp = new Synset[larger.length];
		
		int intersections = 0;  // how many synsets are in both arrays
		for( Synset currentSynset_smaller : smaller ) {
			for( Synset currentSynset_larger : larger ) {
				if( currentSynset_smaller.equals(currentSynset_larger) ) {
					// we have an intersection
					temp[intersections] = currentSynset_smaller;
					intersections++;
				}
			}
		}
		
		
		if( intersections == 0 ) return null; // we found no intersections, return null
		
		// we found intersections, return the proper length array
		Synset[] output = new Synset[intersections];
		for( int i = 0; i < intersections; i++ ) {
			output[i] = temp[i];
		}
		
		return output;
	}
	
	/** This function removes any duplicate items in the input synset. */
	private Synset[] removeDuplicates( Synset[] a ) {
		
		if( a == null ) { return null; }
		
		ArrayList<Synset> noDuplicates = new ArrayList<Synset>();
		
		for( int i = 0; i < a.length; i++ ) {
			
			boolean duplicate = false;
			for( int j = i+1; j < a.length; j++ ) {
				if( a[i].equals(a[j]) ) {
					// oh my, we have a duplicate
					duplicate = true;
					break;
				}
			}
			if( duplicate == false ) noDuplicates.add(a[i]);
			
		}
		
		if( noDuplicates.size() == 0 ) return null;
		
		Synset[] output = new Synset[noDuplicates.size()];
		for(int i = 0; i < noDuplicates.size(); i++ ) {
			output[i] = noDuplicates.get(i);
		}
		
		return output;
		
	}
	
}
