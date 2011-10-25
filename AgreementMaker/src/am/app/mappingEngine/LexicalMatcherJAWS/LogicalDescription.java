package am.app.mappingEngine.LexicalMatcherJAWS;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import am.app.ontology.Node;
import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 * Author: Cosmin Stroe.
 * Date: Jul 17th, 2010.
 * 
 * My attempt at parsing concept names/labels for use with WordNet.
 * 
 * Some reading:
 * Wordnet Stop word List: http://www.d.umn.edu/~tpederse/Group01/WordNet/wordnet-stoplist.html
 */
public class LogicalDescription {

	/**
	 * This function cleans up normal clutter characters. 
	 */
	private String treatString(String s) {
		String s2 = s.replace("_"," ");
		s2 = s2.replace("-"," ");
		s2 = s2.replace("."," ");	
		return s2;
	}
	
	/** Returns an array of the current words.
	 * 
	 * @return
	 */
	
	
	private WordNetDatabase WordNet;
	
	ComplexConcept thisConcept;
	Node concept;
	
	public LogicalDescription( Node c ) {

		Logger log = null;
		
		log = Logger.getLogger(this.getClass());
		log.setLevel(Level.DEBUG);
	
		WordNet = WordNetDatabase.getFileInstance();  // setup wordnet
		
		concept = c; // save the Node reference for later use.
		
		// 1. decide if we are breaking down the label or the localname (or both!)
		// TODO: What if we want to use BOTH?
		boolean usingLabel = true;  // hardcoded to use the label
		
		String wordPhrase;
		
		if( usingLabel ) {
			wordPhrase = concept.getLabel();
		} else {
			wordPhrase = concept.getLocalName();
		}
		
		
		
		
		String cleanString = treatString(wordPhrase);  // blank out _ - .

		thisConcept = new ComplexConcept("cleanString", ComplexConcept.ConceptType.Undefined );
		
		// get an array of the words.
		String[] words = cleanString.split("\\s");
		
		// now, we have an array of the words in the wordPhrase
		// how do we decide what is the most general concept being described?
		
		ComplexConcept mostGeneralConcept = null;
		/*
		 
		// 1.  Look for clues at the more general term in the hierarchy
		
		ArrayList<Node> parent = concept.getParent();
		if( parent != null && parent.size() > 0 ) {
			if( parent.size() > 1 ) { 
				log.debug("Multiple Inheritance detected.");
				log.debug(c + " " + c.getLabel());
				for( int i = 0; i < parent.size(); i++ ) {
					log.debug(parent.get(i) + " " + parent.get(i).getLabel());
				}
			}
			
			String parentString;
			if( usingLabel ) { parentString = parent.get(0).getLabel(); } // TODO: FIX THIS TO WORK WITH MULTIPLE INHERITANCE 
			else { parentString = parent.get(0).getLocalName(); } // TODO: FIX THIS TO WORK WITH MULTIPLE INHERITANCE
			String[] parentWords = parentString.split("\\s");
			
			String[] directobject = step1_getBestConceptViaParent( words, parentWords );  // multi word direct object
			
			if( directobject == null ) {
				// need some other 
			} else {
				if( directobject.length == 1 ) {
					mostGeneralConcept = new ComplexConcept( directobject[0], ComplexConcept.ConceptType.Object);
				} else if( directobject.length > 1 ) {
					ArrayList<ComplexConcept> subConcepts = new ArrayList<ComplexConcept>();
					for( int i = 0; i < directobject.length; i++ ) {
						ComplexConcept sub = new ComplexConcept( directobject[i], ComplexConcept.ConceptType.Undefined );
						subConcepts.add(sub);
					}
					mostGeneralConcept = new ComplexConcept( subConcepts, ComplexConcept.ConceptType.Object );
				}
			}
		}
		*/
		
		// 2.  If we did not figure out the most general concept, try now by looking at the order of the words
		if( mostGeneralConcept == null ) {
			
			// The heuristic we use here is that the last word mentioned is the one that defines the most general concept.
			if( words.length == 1 ) {
				// the concept has only one word.  Therefore that single word must be the most general concept.
				mostGeneralConcept = new ComplexConcept(words[0], ComplexConcept.ConceptType.Undefined);
			} else if( words.length > 1 ) {
				// When we have a term with multiple words, figuring out which words define the most general concept is not easy.
				// Looking at the parents of the current node can help.
				
				// 1. Get the strings of the parents (more than one parent in case of multiple hierarchy)
				List<Node> parentNodes = concept.getParents();
				List<String[]> parentStrings = new ArrayList<String[]>();
				for( int i = 0; i < parentNodes.size(); i++ ) {
					String parentTerm;
					if( usingLabel ) {
						parentTerm = parentNodes.get(i).getLabel();
					} else {
						parentTerm = parentNodes.get(i).getLocalName();
					}
					parentTerm = treatString(parentTerm);
					String[] parentLabelWords = parentTerm.split("\\s");
					parentStrings.add( parentLabelWords );
				}
				
				
				// get the common words between the parentStrings and the current Concept string
				ArrayList<String[]> commonWords = new ArrayList<String[]>();
				for( int i = 0; i < parentStrings.size(); i++ ) {
					commonWords.add( step1_getBestConceptViaParent(words, parentStrings.get(i)) );
				}
				
				// now, if we have multiple matches, we have to decide which is the best one to choose.
				String[] mostProbableCandidate = null;
				int longestLength = 0;
				for( int i = 0; i < commonWords.size(); i++ ) {
					if( mostProbableCandidate == null ) {
						mostProbableCandidate = commonWords.get(i);
						if( mostProbableCandidate != null ) longestLength = mostProbableCandidate.length;
					} else if( commonWords.get(i) != null && commonWords.get(i).length > longestLength ) {
						// an array contains most words than the currently chosen array, choose the new one
						mostProbableCandidate = commonWords.get(i);
						longestLength = mostProbableCandidate.length;
					} else if( commonWords.get(i) != null && commonWords.get(i).length == longestLength ) {
						// we have to terms that have the same amount of words...
						// to settle this tie we need to look at the candidates in more detail.
						
						String[] commonWordsBetweenCommon = step1_getBestConceptViaParent(commonWords.get(i), mostProbableCandidate);
						if( commonWordsBetweenCommon == null ) {
							// no common words! still a tie... how to solve?
							// Leave it alone for now.
							// TODO: Make a solution for this later on.
						} else {
							mostProbableCandidate = commonWordsBetweenCommon;
					
							log.setLevel(Level.DEBUG);
							log.debug("------------------------------------------------------");
							for( int j = 0; j < commonWordsBetweenCommon.length; j++ ) {
								log.debug("commonWordsBetweenCommon["+j+"]:"+commonWordsBetweenCommon[j]);
							}
							// NOTE: I do NOT update longestLength to avoid false positives.
						}
					}
				}
				

				
				if( mostProbableCandidate == null ) {
					// looking at the parents has yielded nothing.
					// now we will decide based on word order.
					
					
					// SIMPLE WORD ORDER for now.
					mostGeneralConcept = step2_createSimpleWordOrderConcept( words );
				} else {
					
					log.setLevel(Level.DEBUG);
					log.debug("------------------------------------------------------");
					for( int i = 0; i < mostProbableCandidate.length; i++ ) {
						log.debug("mostProbableCandidate["+i+"]:"+mostProbableCandidate[i]);
					}

					
					// looking at the parents has yielded something!
					ComplexConcept startingConcept = new ComplexConcept( getStringfromStringArray(mostProbableCandidate), ComplexConcept.ConceptType.Undefined );
					String[] restOfWords = getArrayWithoutNeedles(words, mostProbableCandidate);
					if( restOfWords != null ) {
						for( int i = 0; i < restOfWords.length; i++ ) {
							ComplexConcept modifier = new ComplexConcept( restOfWords[i], ComplexConcept.ConceptType.Undefined );
							
							startingConcept.addOperator(modifier);
							modifier.setOperatesOn(startingConcept);
						}
					}
					
					mostGeneralConcept = startingConcept;
				}
				
			}
			
		}
		
		thisConcept = mostGeneralConcept;
		
		/* old way of doing the description 
		
		DirectedGraphVertex<String> previousword = null;
		for( String word : words ) {
			if( previousword == null ) {
				// this is the first word we've seen.
				DirectedGraphVertex<String> currentword = new DirectedGraphVertex<String>(word);
				miniGraph.insertVertex(currentword);
				previousword = currentword;
			} else {
				// we are working down the list of words.
				DirectedGraphVertex<String> currentWord = new DirectedGraphVertex<String>(word);
				miniGraph.insertVertex(currentWord);
				DirectedGraphEdge<String> newEdge = new DirectedGraphEdge<String>(currentWord, previousword, "");
				// we must make the connection, the graph class doesn't
				currentWord.addOutEdge(newEdge);
				previousword.addInEdge(newEdge);
				miniGraph.insertEdge(newEdge);
				previousword = currentWord;
			}
			
		}
		rootVertex = previousword;
		*/
		
		
	}
	
	/**
	 * This function returns an array of string, which is the haystack without the needles.
	 * The function assumes the the needles are in the haystack (errors can happen if this is not true).
	 * @param haystack
	 * @param needle
	 * @return
	 */
	private String[] getArrayWithoutNeedles( String[] haystack, String[] needle ) {
		Logger log = Logger.getLogger(this.getClass());
		log.setLevel(Level.DEBUG);
		log.debug("------------------------------------------------------");
		for( int i = 0; i < haystack.length; i++ ) {
			log.debug("haystack["+i+"]:"+haystack[i]);
		}
		for( int i = 0; i < needle.length; i++ ) {
			log.debug("needle["+i+"]:"+needle[i]);
		}
		
		
		if( haystack == null || needle == null ) { return null; }
		if( haystack.length - needle.length <= 0 ) { return null; }
		
		String[] newHaystack = new String[haystack.length - needle.length];
		int needlesFound = 0;
		for( int i = 0; i < haystack.length; i++ ) {
			boolean breakOut = false;
			for( int j = 0; j < needle.length; j++ ) {
				if( haystack[i].equalsIgnoreCase(needle[j]) ) {
					needlesFound++;
					breakOut = true;
					break;
				}
			}
			if(breakOut) { continue; }
			String sd = haystack[i];
			newHaystack[i-needlesFound] = sd;
		}
		
		return newHaystack;
	}
	
	/**
	 * This function takes an array of words, and returns a string with all the words in the separated by a space.
	 * @param array
	 * @return
	 */
	private String getStringfromStringArray( String[] array ) {
		if( array == null ) { return null; }
		String s = new String();
		for( int i = 0; i < array.length; i++ ) {
			if( i == 0 ) {
				s += array[i];
			} else {
				s += " " + array[i];
			}
		}
		return s;
	}
	
	/**
	 * Turn a word phrase into a ComplexConcept data structure.
	 * @param words
	 * @return
	 */
	private ComplexConcept step2_createSimpleWordOrderConcept(String[] words) {

		// go backwards and create the ComplexConcepts
		ComplexConcept firstConcept = null;
		ComplexConcept lastConcept = null;
		for( int i = words.length - 1; i > 0; i-- ) {
			ComplexConcept c = new ComplexConcept( words[i], ComplexConcept.ConceptType.Undefined );
			if( lastConcept == null ) {
				lastConcept = c;
				firstConcept = c;
			} else {
				lastConcept.addOperator(c);
				c.setOperatesOn(lastConcept);
			}
		}

		return firstConcept;
	}


	private String[] step1_getBestConceptViaParent(String[] child, String[] parent) {
		

		// Which words do the two terms have in common.
		ArrayList<String> commonWords = new ArrayList<String>();
		for( String childWord : child ) {
			for( String parentWord : parent ) {
				if( childWord.equalsIgnoreCase(parentWord) ) {
					// we have these this word in common
					commonWords.add(childWord);
				}
			}
		}
		
		
		if( commonWords.size() == 0 ) {
			// no words in common
			return null;
		} else if( commonWords.size() == 1 ) {
			// the concepts have exactly one word in common, no need to do extra work.
			String[] output = new String[1];
			output[0] = commonWords.get(0);
			return output;
		} else {
			// multiple words in common.  use child word order as a reference.
			String[] output = new String[ commonWords.size() ];
			for( int i = 0; i < commonWords.size(); i++ ) {
				output[i] = commonWords.get(i);
			}
			
			return output;
		}
		
	}
	
	@Override
	public String toString() {
		if( thisConcept == null ) return new String("Null");
		return thisConcept.toString();
	}	

		
	
}
