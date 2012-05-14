package am.app.mappingEngine.instanceMatchers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import am.Utility;
import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;
import edu.smu.tspell.wordnet.impl.file.synset.AdjectiveSatelliteReferenceSynset;

public class WordNetUtils {
	WordNetDatabase wordNet; 
	//Map<String, Boolean> isSynonym = new ConcurrentHashMap<String, Boolean>();
	
	public WordNetUtils(){
		initWordnet();
	}
	
	private void initWordnet() {
		// Initialize the WordNet interface.
		String cwd = System.getProperty("user.dir");
		String wordnetdir = cwd + "/wordnet-3.0";
		System.setProperty("wordnet.database.dir", wordnetdir);
		// Instantiate 
		try {
			wordNet = WordNetDatabase.getFileInstance();
		}
		catch( Exception e ) {
			Utility.displayErrorPane(e.getMessage(), "Cannot open WordNet files.\nWordNet should be in the following directory:\n" + wordnetdir);
		}
	}

	public boolean areSynonyms(String source, String target) {
		String key = source + ":" + target;
		//Boolean answer = isSynonym.get(key);
		//if(answer != null) return answer;
		
		Synset[] sourceSynsets = wordNet.getSynsets(source);
		Synset[] targetSynsets = wordNet.getSynsets(target);
		
		for (int i = 0; i < sourceSynsets.length; i++) {
			for (int j = 0; j < targetSynsets.length; j++) {
				if(sourceSynsets[i] == targetSynsets[j]){
					//System.out.println(source + " " + target + " synonyms!!");
					//isSynonym.put(key, true);
					return true;
				}
			}
		}
		//isSynonym.put(key, false);
		return false;
	}
	
	public boolean areSynonyms(String source, String target, SynsetType type) {
		String key = source + ":" + target;
		//Boolean answer = isSynonym.get(key);
		//if(answer != null) return answer;
		
		Synset[] sourceSynsets = wordNet.getSynsets(source, type);
		Synset[] targetSynsets = wordNet.getSynsets(target, type);
		
		for (int i = 0; i < sourceSynsets.length; i++) {
			for (int j = 0; j < targetSynsets.length; j++) {
				if(sourceSynsets[i] == targetSynsets[j]){
					//System.out.println(source + " " + target + " synonyms!!");
					//isSynonym.put(key, true);
					return true;
				}
			}
		}
		//isSynonym.put(key, false);
		return false;
	}
	
	public boolean areAntonyms(String source, String target, SynsetType type) {

		Synset[] sourceSynsets = wordNet.getSynsets(source, type);
		Synset[] targetSynsets = wordNet.getSynsets(target, type);

		for (int i = 0; i < sourceSynsets.length; i++) {
			for (int j = 0; j < targetSynsets.length; j++) {
				WordSense[] targetAntonymsSense = targetSynsets[j].getAntonyms(target);
				for (int k = 0; k < targetAntonymsSense.length; k++){
					Synset tarAntSynset = targetAntonymsSense[k].getSynset();
					if(sourceSynsets[i] == tarAntSynset){
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean areinstanceHypernyms(String source, String target, SynsetType type) {

		NounSynset[] hypernymNounSynset;
		Synset[] srcSynsets = wordNet.getSynsets(source, type );
		Synset[] targetSynsets = wordNet.getSynsets(target, type );

		//selectively choose the synset depending on the type
		if(type == SynsetType.NOUN ){
			for (int i = 0; i < srcSynsets.length; i++) {
				hypernymNounSynset = ((NounSynset) srcSynsets[i]).getInstanceHypernyms();
				if( bCompareTwoSynsetArray( hypernymNounSynset, targetSynsets ) )
					return true;

				//get the hypernym synset of the hypernymsynset
				Queue<Synset> queue = new LinkedList<Synset>();
				queue.addAll(Arrays.asList(hypernymNounSynset));
				while(!queue.isEmpty()){
					hypernymNounSynset = ((NounSynset) queue.remove()).getHypernyms();

					if( bCompareTwoSynsetArray( hypernymNounSynset, targetSynsets) )
						return true;

					queue.addAll(Arrays.asList(hypernymNounSynset));
				}
			}
		}
		return false;
	}

	public boolean areHypernyms(String source, String target, SynsetType type) {

		NounSynset[] hypernymNounSynset;
		VerbSynset[] hypernymVerbSynset;
		Synset[] srcSynsets = wordNet.getSynsets(source, type );
		Synset[] targetSynsets = wordNet.getSynsets(target, type );

		//selectively choose the synset depending on the type
		if(type == SynsetType.NOUN ){
			for (int i = 0; i < targetSynsets.length; i++) {
				hypernymNounSynset = ((NounSynset) targetSynsets[i]).getHypernyms();
				if( bCompareTwoSynsetArray( srcSynsets, hypernymNounSynset) )
					return true;
			}
		}
		else if(type == SynsetType.VERB){
			for (int i = 0; i < targetSynsets.length; i++) {
				hypernymVerbSynset = ((VerbSynset)targetSynsets[i]).getHypernyms();
				if( bCompareTwoSynsetArray(srcSynsets, hypernymVerbSynset ))
					return true;
			}
		}

		return false;
	}

	public boolean areAncestorHypernyms(String source, String target, SynsetType type) {

		NounSynset[] hypernymNounSynset;
		VerbSynset[] hypernymVerbSynset;
		Synset[] srcSynsets = wordNet.getSynsets(source, type );
		Synset[] targetSynsets = wordNet.getSynsets(target, type );

		Queue<Synset> queue = new LinkedList<Synset>();
		queue.addAll(Arrays.asList(targetSynsets));
		int nOldCount = 0;
		int nNewCount = 0;
		
		//bfs traversal
		if(type == SynsetType.NOUN ){
			while(!queue.isEmpty()){
				hypernymNounSynset = ((NounSynset) queue.remove()).getHypernyms();

				if( bCompareTwoSynsetArray( srcSynsets, hypernymNounSynset) )
					return true;

				queue.addAll(Arrays.asList(hypernymNounSynset));
			}
		}
		else if(type == SynsetType.VERB){
			
			// Do not add all synsets: the verb synsets do not have a common parent 'entity'
			// these might have inter-related and might yield to deadlock
			HashSet<Synset> verbset = new HashSet<Synset>();
						
			while(!queue.isEmpty()){
				hypernymVerbSynset = ((VerbSynset) queue.remove()).getHypernyms();

				if( bCompareTwoSynsetArray( srcSynsets, hypernymVerbSynset) )
					return true;
				
				//add only if not present earlier
				nOldCount = verbset.size();
				verbset.addAll(Arrays.asList(hypernymVerbSynset));
				nNewCount = verbset.size();
				
				if( nOldCount != nNewCount)
					queue.addAll(Arrays.asList(hypernymVerbSynset));
			}
		}
		
		return false;
	}

	public boolean areinstanceHyponyms(String source, String target, SynsetType type) {

		NounSynset[] hyponymNounSynset;
		Synset[] srcSynsets = wordNet.getSynsets(source, type );
		Synset[] targetSynsets = wordNet.getSynsets(target, type );

		//selectively choose the synset depending on the type
		if(type == SynsetType.NOUN ){
			for (int i = 0; i < srcSynsets.length; i++) {
				hyponymNounSynset = ((NounSynset) srcSynsets[i]).getInstanceHyponyms();
				if( bCompareTwoSynsetArray( hyponymNounSynset, targetSynsets) )
					return true;

				//get the hypernym synset of the hypernymsynset
				Queue<Synset> queue = new LinkedList<Synset>();
				queue.addAll(Arrays.asList(hyponymNounSynset));
				while(!queue.isEmpty()){
					hyponymNounSynset = ((NounSynset) queue.remove()).getHypernyms();

					if( bCompareTwoSynsetArray( hyponymNounSynset, targetSynsets) )
						return true;

					queue.addAll(Arrays.asList(hyponymNounSynset));
				}
			}
		}

		return false;
	}

	public boolean areHyponyms(String source, String target, SynsetType type) {

		NounSynset[] hyponymNounSynset;
		Synset[] srcSynsets = wordNet.getSynsets(source, type );
		Synset[] targetSynsets = wordNet.getSynsets(target, type );

		//selectively choose the synset depending on the type
		if(type == SynsetType.NOUN ){
			for (int i = 0; i < srcSynsets.length; i++) {
				hyponymNounSynset = ((NounSynset) srcSynsets[i]).getHyponyms();
				if( bCompareTwoSynsetArray( hyponymNounSynset,targetSynsets) )
					return true;
			}
		}

		return false;
	}

	public boolean areAncestorHyponyms(String source, String target, SynsetType type) {

		NounSynset[] hyponymNounSynset;
		Synset[] srcSynsets = wordNet.getSynsets(source, type );
		Synset[] targetSynsets = wordNet.getSynsets(target, type );

		Queue<Synset> queue = new LinkedList<Synset>();
		queue.addAll(Arrays.asList(targetSynsets));

		//bfs traversal
		while(!queue.isEmpty()){
			hyponymNounSynset = ((NounSynset) queue.remove()).getHyponyms();

			if( bCompareTwoSynsetArray( srcSynsets, hyponymNounSynset) )
				return true;

			queue.addAll(Arrays.asList(hyponymNounSynset));
		}

		return false;
	}

	public boolean arePertainyms(String source, String target, SynsetType type){
		Synset[] sourceSynsets = wordNet.getSynsets(source, type);

		if( type == SynsetType.ADVERB){ //adverb pertainyms gives adjective, so need to get target adj synset
			Synset[] targetSynsets = wordNet.getSynsets(target, SynsetType.ADJECTIVE);
			for (int i = 0; i < sourceSynsets.length; i++) {
				WordSense[] relatedSensewords = ((AdverbSynset)sourceSynsets[i]).getPertainyms(source);
				for(int j = 0; j < relatedSensewords.length; j++){
					Synset senseSynset = relatedSensewords[j].getSynset();
					for(int k = 0; k < targetSynsets.length; k++)
						if( bCompareTwoSynsets( senseSynset, targetSynsets[k]))
							return true;
				}
			}	
		}
		else if( type == SynsetType.ADJECTIVE){//adjective pertainyms gives noun, so need to get target noun synset
			Synset[] targetSynsets = wordNet.getSynsets(target, SynsetType.NOUN);
			for (int i = 0; i < sourceSynsets.length; i++) {
				WordSense[] relatedSensewords = ((AdjectiveSynset)sourceSynsets[i]).getPertainyms(source);
				for(int j = 0; j < relatedSensewords.length; j++){
					Synset senseSynset = relatedSensewords[j].getSynset();
					for(int k = 0; k < targetSynsets.length; k++)
						if( bCompareTwoSynsets( senseSynset, targetSynsets[k]))
							return true;
				}
			}	
		}

		return false;
	}

	public boolean areTopicMembers(String source, String target){

		Synset[] sourceSynsets = wordNet.getSynsets(source, SynsetType.NOUN );
		Synset[] targetSynsets = wordNet.getSynsets(target, SynsetType.NOUN);

		for (int i = 0; i < sourceSynsets.length; i++) {
			Synset[] topicMembersSynset = ((NounSynset)sourceSynsets[i]).getTopicMembers();
			if( bCompareTwoSynsetArray( topicMembersSynset, targetSynsets) )
				return true;
		}

		return false;
	}

	public boolean areOfTopics(String source, String target, SynsetType type) {

		NounSynset[] hypernymNounSynset;
		Synset[] sourceSynsets = wordNet.getSynsets(source, type);
		Synset[] targetSynsets = wordNet.getSynsets(target, type);

		//selectively choose the synset depending on the type
		if(type == SynsetType.NOUN ){
			for (int i = 0; i < sourceSynsets.length; i++) {
				hypernymNounSynset = ((NounSynset)sourceSynsets[i]).getTopics();
				if( bCompareTwoSynsetArray( hypernymNounSynset, targetSynsets ))
					return true;
			}
		}
		else if(type == SynsetType.VERB){
			for (int i = 0; i < sourceSynsets.length; i++) {
				hypernymNounSynset = ((VerbSynset)sourceSynsets[i]).getTopics();
				if(bCompareTwoSynsetArray( hypernymNounSynset, targetSynsets ))
					return true;
			}
		}
		else if(type == SynsetType.ADJECTIVE){
			for (int i = 0; i < sourceSynsets.length; i++) {
				hypernymNounSynset = ((AdjectiveSynset)sourceSynsets[i]).getTopics();
				if( bCompareTwoSynsetArray( hypernymNounSynset, targetSynsets ))
					return true;
			}
		}
		else if(type == SynsetType.ADVERB){
			for (int i = 0; i < sourceSynsets.length; i++) {
				hypernymNounSynset = ((AdverbSynset)sourceSynsets[i]).getTopics();
				if( bCompareTwoSynsetArray( hypernymNounSynset, targetSynsets ))
					return true;
			}
		}

		return false;
	}

	boolean areTroponyms(String source, String target){

		VerbSynset[] troponymVerbSynset;
		Synset[] sourceSynsets = wordNet.getSynsets(source, SynsetType.VERB);
		Synset[] targetSynsets = wordNet.getSynsets(target, SynsetType.VERB);

		//selectively choose the synset depending on the type
		for (int i = 0; i < sourceSynsets.length; i++) {
			troponymVerbSynset = ((VerbSynset)sourceSynsets[i]).getTroponyms();
			if( bCompareTwoSynsetArray( troponymVerbSynset, targetSynsets ))
				return true;
		}	

		return false;
	}

	boolean areSimilarAdj(String source, String target){
		
		AdjectiveSynset[] similarSynset;
		Synset[] sourceSynsets = wordNet.getSynsets(source, SynsetType.ADJECTIVE);
		Synset[] targetSynsets = wordNet.getSynsets(target, SynsetType.ADJECTIVE);

		//selectively choose the synset depending on the type
		for (int i = 0; i < sourceSynsets.length; i++) {
			similarSynset = ((AdjectiveSynset)sourceSynsets[i]).getSimilar();
			if( bCompareTwoSynsetArray( similarSynset, targetSynsets ))
				return true;
		}
		
		return false;
	}
	
	boolean areAdjofSameSatelliteHead(String source, String target){
	
		AdjectiveSynset srcHeadSynset;
		AdjectiveSynset tarHeadSynset;
		Synset[] sourceSynsets = wordNet.getSynsets(source, SynsetType.ADJECTIVE_SATELLITE);
		Synset[] targetSynsets = wordNet.getSynsets(target, SynsetType.ADJECTIVE_SATELLITE);

		//selectively choose the synset depending on the type
		for (int i = 0; i < sourceSynsets.length; i++) {
			srcHeadSynset = ((AdjectiveSatelliteReferenceSynset)sourceSynsets[i]).getHeadSynset();
			for (int j = 0; j < targetSynsets.length; j++){
				tarHeadSynset = ((AdjectiveSatelliteReferenceSynset)targetSynsets[j]).getHeadSynset();
				if( bCompareTwoSynsets( srcHeadSynset, tarHeadSynset ))
					return true;
			}
		}
		
		return false;
	}
	
	boolean areEntailments(String source, String target){

		VerbSynset[] troponymVerbSynset;
		Synset[] sourceSynsets = wordNet.getSynsets(source, SynsetType.VERB);
		Synset[] targetSynsets = wordNet.getSynsets(target, SynsetType.VERB);

		//selectively choose the synset depending on the type
		for (int i = 0; i < sourceSynsets.length; i++) {
			troponymVerbSynset = ((VerbSynset)sourceSynsets[i]).getEntailments();
			if( bCompareTwoSynsetArray( troponymVerbSynset, targetSynsets ))
				return true;
		}	

		return false;
	}
	
	boolean areofSameVerbGroup(String source, String target){
		
		Synset[] sourceSynsets = wordNet.getSynsets(source, SynsetType.VERB );
		Synset[] targetSynsets = wordNet.getSynsets(target, SynsetType.VERB);

		for (int i = 0; i < sourceSynsets.length; i++) {
			VerbSynset[] verbMembersSynset = ((VerbSynset)sourceSynsets[i]).getVerbGroup();
			if( bCompareTwoSynsetArray( verbMembersSynset, targetSynsets) )
				return true;
		}

		return false;
	}

	private 	boolean bCompareTwoSynsetArray(Synset[] srcSynset, Synset[] tarSynset){

		for (int j = 0; j < srcSynset.length; j++) {
			for (int k = 0; k < tarSynset.length; k++) {
				if( bCompareTwoSynsets( srcSynset[j], tarSynset[k] ) )
					return true;
			}
		}

		return false;
	}
private 
	boolean bCompareTwoSynsets(Synset srcSynset, Synset tarSynset){
		return srcSynset == tarSynset ? true : false;
	}

	public boolean isaAttribute(String word/*eg: height: table attribute*/, String/*high, low  of the question*/ attribute){
		Synset[] syn =  wordNet.getSynsets(word);

		//check whether the words has synsets in wordnet
		if(syn == null || syn.length <= 0)
			return false;

		for(int i = 0; i < syn.length; i++){		

			if(SynsetType.NOUN == syn[i].getType()){
				AdjectiveSynset[] attrsyn = ((NounSynset)syn[i]).getAttributes();

				//check whether the word has attributes in the wordnet
				if(attrsyn == null || attrsyn.length <= 0)
					continue;

				//check whether present
				for(int j = 0; j < attrsyn.length; j++){
					List<String> attrList = Arrays.asList(attrsyn[j].getWordForms());
					if( attrList.contains(attribute) ){
						return true;
					}

					//or whether synonyms or antonyms : attribute of a noun is adj
					for(String attr: attrList){
						if( areSynonyms(attr, attribute, SynsetType.ADJECTIVE) ||
								areAntonyms(attr, attribute, SynsetType.ADJECTIVE) ||
								arePertainyms(attr, attribute, SynsetType.ADJECTIVE)){
							return true;
						}
					}
				}

				//or word's instance = attribute or topic members
				for(String attr: syn[i].getWordForms()){
					if( areinstanceHypernyms(attr, attr, SynsetType.NOUN) ||
							areTopicMembers(attribute, attr) )
						return true;
				}
			}
			else if(SynsetType.ADJECTIVE == syn[i].getType()){
				NounSynset[] attrsyn = ((AdjectiveSynset)syn[i]).getAttributes();

				//check whether the word has attributes in the wordnet
				if(attrsyn == null || attrsyn.length <= 0)
					continue;

				//check whether present
				for(int j = 0; j < attrsyn.length; j++){
					List<String> attrList = Arrays.asList(attrsyn[j].getWordForms());
					if( attrList.contains(attribute) ){
						return true;
					}

					//or whether synonyms or antonyms or word's instance = attribute
					//or topic members : attribute of a noun is ad
					for(String attr: attrList){
						if( areSynonyms(attr, attribute, SynsetType.NOUN) ||
								areAntonyms(attr, attribute, SynsetType.NOUN) ||
								arePertainyms(attr, attribute, SynsetType.NOUN) ){
							return true;
						}
					}	
				}

				//or word's instance = attribute or topic members
				for(String attr: syn[i].getWordForms()){
					if( areinstanceHypernyms(attr, attr, SynsetType.ADJECTIVE) ||
							areTopicMembers(attribute, attr) )
						return true;
				}
			}
		}

		return false;
	}
	
	public static void main(String[] args){
		WordNetUtils utils = new WordNetUtils();

		boolean bStatus = utils.isaAttribute("height", "high");
		System.out.println(bStatus);

		/*boolean bIsRelated = utils.areAntonyms("good", "bad", SynsetType.NOUN);
		System.out.println("0 )" + bIsRelated);

		bIsRelated = utils.areSynonyms("good", "bad", SynsetType.NOUN);
		System.out.println("1 ) " + bIsRelated);

		bIsRelated = utils.areSynonyms("beautiful", "ugly", SynsetType.ADJECTIVE);
		System.out.println("2 )" + bIsRelated);

		bIsRelated = utils.areHypernyms("vehicle", "car", SynsetType.NOUN);
		System.out.println("3 )" +bIsRelated);

		bIsRelated = utils.areHyponyms("vehicle", "car", SynsetType.NOUN);
		System.out.println("4 )" +bIsRelated);

		bIsRelated = utils.areAncestorHypernyms("vehicle", "lorry", SynsetType.NOUN);
		bIsRelated = utils.areAncestorHypernyms("feline", "lion", SynsetType.NOUN);
		System.out.println("5 )" +bIsRelated);

		bIsRelated = utils.areAncestorHyponyms("lorry", "vehicle", SynsetType.NOUN);
		System.out.println("6 )" +bIsRelated);

		bIsRelated = utils.areinstanceHypernyms("Mississippi", "river", SynsetType.NOUN);
		System.out.println("7 )" +bIsRelated);*/

		//boolean bIsRelated = utils.areinstanceHypernyms("France", "country", SynsetType.NOUN); 
		//System.out.println("7 )" +bIsRelated);

		//String[] lemmas = utils.wordNet.getBaseFormCandidates("beautiful", SynsetType.NOUN);

		//System.out.print(lemmas[0]);

/*		boolean bIsRelated = utils.areAncestorHypernyms("frisson", "brain", SynsetType.NOUN);
		System.out.println("5 )" +bIsRelated);

		bIsRelated = utils.areTroponyms("verbalize", "shout");
		System.out.println("6 )" +bIsRelated);

		bIsRelated = utils.areEntailments("snore", "sleep");
		System.out.println("7 )" +bIsRelated);

		bIsRelated = utils.arePertainyms("academic", "academia", SynsetType.ADJECTIVE);
		System.out.println("8 )" +bIsRelated);
		
		bIsRelated = utils.areofSameVerbGroup("talk", "write");
		System.out.println("10 )" +bIsRelated);
		
		bIsRelated = utils.areinstanceHypernyms("Tunisia", "country", SynsetType.NOUN);
		System.out.println("11 )" +bIsRelated);
		
		bIsRelated = utils.areAncestorHypernyms("verbalize", "shout", SynsetType.VERB);
		System.out.println("12 )" +bIsRelated);*/
		
		boolean bIsRelated = utils.areAncestorHypernyms("organization", "country", SynsetType.NOUN);
		System.out.println("12 )" + bIsRelated);
		
		bIsRelated = utils.areAncestorHypernyms("country", "organization", SynsetType.NOUN);
		System.out.println("13 )" + bIsRelated);
	}
}
