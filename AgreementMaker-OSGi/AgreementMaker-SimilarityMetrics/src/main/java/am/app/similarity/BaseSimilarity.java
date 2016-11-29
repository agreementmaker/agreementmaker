package am.app.similarity;

import am.api.config.IsConfigurable;
import am.stringutil.Normalizer;
import am.stringutil.NormalizerParameter;
import am.stringutil.PorterStemmer;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class BaseSimilarity implements StringSimilarityMeasure, IsConfigurable {
    public enum Config {
        USE_DICTIONARY
    }

    private WordNetDatabase wordnet;
    private Map<Enum, String> config;

    @Override
    public List<Enum> getConfigurationKeys() {
        return Arrays.asList(Config.values());
    }

    @Override
    public void configure(Map<Enum, String> properties) {
        this.config = properties;
    }

    private transient NormalizerParameter param1;
    private transient NormalizerParameter param2;
    private transient NormalizerParameter param3;
    private transient Normalizer norm1;
    private transient Normalizer norm2;
    private transient Normalizer norm3;


    public BaseSimilarity() {
        param1 = new NormalizerParameter();
        param1.setAllTrue();
        param1.normalizeDigit = false;
        param1.stem = false;
        norm1 = new Normalizer(param1);


        param2 = new NormalizerParameter();
        param2.setAllTrue();
        param2.normalizeDigit = false;
        norm2 = new Normalizer(param2);

        param3 = new NormalizerParameter();
        param3.setAllTrue();
        norm3 = new Normalizer(param3);
    }

    @Override
    public double getSimilarity(String s1, String s2) {
        return calculateSimilarity(s1, s2);
    }

    /**
     * This only calculates the similarity.
     * @param sourceName
     * @param targetName
     * @return
     */
    private double calculateSimilarity(String sourceName, String targetName ) {

        // Step 0:		If they are exactly equal, 1.0 similarity.

        if( sourceName.equalsIgnoreCase(targetName) ) return 1.0d;

        // Step 1:		run treatString on each name to clean it up
        //              treatString removes (and replaces them with a space): _ , .
        sourceName = treatString(sourceName);
        targetName = treatString(targetName);

        if( sourceName.equalsIgnoreCase(targetName) ) return 0.99d;

        if(config.containsKey(Config.USE_DICTIONARY)) {

            // if we haven't initialized our wordnet database, do it
            if( wordnet == null )
                wordnet = WordNetDatabase.getFileInstance();

            // The user wants us to use a dictionary to find related words

            Synset[] sourceNouns = wordnet.getSynsets(sourceName, SynsetType.NOUN );
            Synset[] targetNouns = wordnet.getSynsets(targetName, SynsetType.NOUN );

            float nounSimilarity = getSensesComparison(sourceNouns, targetNouns);

            Synset[] sourceVerbs = wordnet.getSynsets(sourceName, SynsetType.VERB);
            Synset[] targetVerbs = wordnet.getSynsets(targetName, SynsetType.VERB);

            float verbSimilarity = getSensesComparison(sourceVerbs, targetVerbs);

            //String rel = MappingRelation.EQUIVALENCE;

            // select the best similarity found. (either verb or noun)
            if( nounSimilarity > verbSimilarity ) {
                return nounSimilarity;
            }
            else {
                return verbSimilarity;
            }

        } else {
            // all normalization without stemming and digits return 0.95
            String sProcessed = norm1.normalize(sourceName);
            String tProcessed= norm1.normalize(targetName);
            if(sProcessed.equals(tProcessed)) return 0.95d;

            // all normalization without digits return 0.90
            sProcessed = norm2.normalize(sourceName);
            tProcessed= norm2.normalize(targetName);
            if(sProcessed.equals(tProcessed)) return 0.9d;

            // all normalization return 0.85
            sProcessed = norm3.normalize(sourceName);
            tProcessed = norm3.normalize(targetName);
            if(sProcessed.equals(tProcessed)) return 0.85d;

            // none of the above
            return 0.0d;
        }
    }

    /**
     * This function treats a string to make it more comparable:
     * 1) Removes dashes and underscores
     * 2) Separates capitalized words, ( "BaseSimilarity" -> "Base Similarity" )
     */

    public static String treatString(String s) {


        String s2 = s.replace("_", " ");
        s2 = s2.replace("-", " ");
        s2 = s2.replace(".", " ");


        for (int i = 0; i < s2.length() - 1; i++) {
            if (Character.isLowerCase(s2.charAt(i)) && Character.isUpperCase(s2.charAt(i + 1))) {
                s2 = s2.substring(0, i + 1) + " " + s2.substring(i + 1);
            }
        }

        return s2;
    }


	/*
	 * Input: Two synsets of words.
	 *
	 * This function calculates the similarity between both synsets
	 *
	 */

    private float getSensesComparison(Synset[] senses1, Synset[] senses2) {


        if (senses1.length == 0 || senses2.length == 0) {
            // one of the words had no definition
            return 0.0f;
        }

        String s1 = "", s2 = "";

        float[] results = new float[senses1.length * senses2.length];

        // Explore related words.
        for (int i = 0; i < senses1.length; i++) {
            Synset sense1 = senses1[i];

            // Print Synset Description
            //   System.out.println((i+1) + ". " + sense1.getLongDescription());
            s1 += sense1.getDefinition();

            for (int j = 0; j < senses2.length; j++) {
                Synset sense2 = senses2[j];
                //     System.out.println((j+1) + ". " + sense2.getLongDescription());
                s2 += sense2.getDefinition();

                results[i + j] = calculateWordSimilarity(removeNonChar(s1), removeNonChar(s2));
            }

        } // end-outer-for

        Arrays.sort(results);

	    	/*
	    	for(int k=0; k<results.length; k++)
	    		System.out.println(results[k]);
	    	*/


        return results[senses1.length * senses2.length - 1];
    }


    /*
     * Remove anything from a string that isn't a Character or a space
     */
    private String removeNonChar(String s){

        String result = "";
        for(int i=0; i<s.length(); i++)
            if(Character.isLetter(s.charAt(i)) || s.charAt(i)==' ')
                result += s.charAt(i);

        return result;
    }


    /*
     * This function takes two word DEFINITIONS, stems them,
     * removes non-content and repeated words, then determines how many words
     * are in common between the definitions, and calculates a similarity
     * based on the number of common words found.
     *
     */
    protected float calculateWordSimilarity(String d1, String d2){

        if(d1.equalsIgnoreCase(d2)) return 1;

        // treat the long descriptions
        d1 = treatString(d1);
        d2 = treatString(d2);

        if(d1.equalsIgnoreCase(d2)) return 1; // the definitions are exactly equal

        ArrayList<String> d1Tokens = new ArrayList<>();
        ArrayList<String> d2Tokens = new ArrayList<>();
        PorterStemmer ps = new PorterStemmer();

        String word;

        // Tokenize the first description, using space as the token separator
        // then remove non-content and repeated words.
        StringTokenizer st = new StringTokenizer(d1);

        while(st.hasMoreTokens()){
            word = st.nextToken();
            word = ps.stem(word);
            if(!isNonContent(word) && isNotRepeated(word,d1Tokens) && !word.equalsIgnoreCase("Invalid term"))
                d1Tokens.add(word);
        }

        st = new StringTokenizer(d2);

        while(st.hasMoreTokens()){
            word = st.nextToken();
            word = ps.stem(word);
            if(!isNonContent(word) && isNotRepeated(word,d2Tokens) && !word.equalsIgnoreCase("Invalid term"))
                d2Tokens.add(word);
        }

		/*
		 for(int i=0; i< d1Tokens.size(); i++)
		    System.out.println(d1Tokens.get(i));

		 for(int i=0; i< d2Tokens.size(); i++)
		    System.out.println(d2Tokens.get(i));

		 */

        String [] def1 = new String[ d1Tokens.size()];
        String [] def2 = new String[d2Tokens.size()];

        for(int i=0; i<d1Tokens.size(); i++)
            def1[i] = d1Tokens.get(i);



        for(int i=0; i<d2Tokens.size(); i++)
            def2[i] = d2Tokens.get(i);

        if(def1.length == 0 || def2.length == 0)
            return 0;


        int counter =0;

        // count how many words the lists has in common
        for(int i=0; i<def1.length; i++)
            for(int j=0; j<def2.length; j++)
                if(def1[i].equalsIgnoreCase(def2[j]) )
                    counter++;

        //printStringArray(def1);
        //printStringArray(def2);


        // return the computed similarity (based on the common words)
        return ((float)counter /((float) (def1.length + def2.length )/2.0f));

    }

    /*
     * Determine whether this is a non-content word
     */
    public static boolean isNonContent(String s){

        if(s.equalsIgnoreCase("the") ||
                s.equalsIgnoreCase("is") ||
                s.equalsIgnoreCase("this") ||
                s.equalsIgnoreCase("are") ||
                s.equalsIgnoreCase("to") ||
                s.equalsIgnoreCase("a") ||
                s.equalsIgnoreCase("e") ||
                s.equalsIgnoreCase("an") ||
                s.equalsIgnoreCase("in") ||
                s.equalsIgnoreCase("or") ||
                s.equalsIgnoreCase("and") ||
                s.equalsIgnoreCase("for") ||
                s.equalsIgnoreCase("that") )
        {
            return true;
        }

        return false;

    }

    /*
     * Determine if this word is already in the sentence array.
     */
    private boolean isNotRepeated(String word,ArrayList<String> sentence){

        for(int i=0; i<sentence.size(); i++)
            if(word.equalsIgnoreCase( sentence.get(i) ))
                return false;


        return true;
    }

}
