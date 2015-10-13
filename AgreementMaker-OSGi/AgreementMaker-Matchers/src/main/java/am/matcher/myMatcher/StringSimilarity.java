package am.matcher.myMatcher;

/*import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;*/
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.WordNetDatabase;



public class StringSimilarity {
	
	/*public double semanticSimilarity(String s1,String s2)
	{
		final ILexicalDatabase db = new NictWordNet();
		WS4JConfiguration.getInstance().setMFS(true);
		double s = new WuPalmer(db).calcRelatednessOfWords(s1, s2);
		return s;
	
		
	}*/
	public Boolean isSynonym(String s1,String s2)
	{
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(s1);
		String[] wordForms;
		  if (synsets.length > 0) {
		       for (int i = 0; i < synsets.length; i++)
		       {
		    	   wordForms = synsets[i].getWordForms();
		    	   
		    	   
		       }
		  	}
		  return false;
	}

	  /**
	   * Calculates the similarity (a number within 0 and 1) between two strings.
	   */
	  public  double similarity(String s1, String s2) {
	    String longer = s1, shorter = s2;
	    
	    if (s1.length() < s2.length()) { // longer should always have greater length
	      longer = s2; shorter = s1;
	    }
	    int longerLength = longer.length();
	    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
	    /* // If you have StringUtils, you can use it to calculate the edit distance:
	    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
	                               (double) longerLength; */
	    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	  }

	  // Example implementation of the Levenshtein Edit Distance
	  // See http://rosettacode.org/wiki/Levenshtein_distance#Java
	  private int editDistance(String s1, String s2) {
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();

	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	      int lastValue = i;
	      for (int j = 0; j <= s2.length(); j++) {
	        if (i == 0)
	          costs[j] = j;
	        else {
	          if (j > 0) {
	            int newValue = costs[j - 1];
	            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	              newValue = Math.min(Math.min(newValue, lastValue),
	                  costs[j]) + 1;
	            costs[j - 1] = lastValue;
	            lastValue = newValue;
	          }
	        }
	      }
	      if (i > 0)
	        costs[s2.length()] = lastValue;
	    }
	    return costs[s2.length()];
	  }
}
