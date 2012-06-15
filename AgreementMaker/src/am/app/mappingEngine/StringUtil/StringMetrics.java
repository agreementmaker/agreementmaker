package am.app.mappingEngine.StringUtil;

/**
 * This class contains some of the string metrics used by the AM not found in library
 * @author marco
 *
 */
public enum StringMetrics {
	
	EDIT("Levenshtein Edit Distance"),
	JARO("Jaro Winkler"),
	QGRAM("Q-Gram"),
	SUB("Substring metric"),
	AMSUB("AM Substring metric"),
	ISUB("I-SUB"),
	AMSUB_AND_EDIT("AMsubstring + editDistance"), //0.6*amsub + 0.4*editdistance
	AMSUB_AND_EDIT_WITH_WORDNET("AMsubstring + editDistance with WordNet Synonyms"); 
	
	private String name;
	
	private StringMetrics(String name) {
		this.name = name;
	}
	
	public String getLongName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	//Derived from FALCON AO return the sum of common characters in all substrings of the two words normalized by the number of total chars
	//So this is only the commonality part is not the whole I-Sub method
	public static double substringScore(String s1, String s2)
    {
		
		int l1 = s1.length(), l2 = s2.length();
        int L1 = l1, L2 = l2;
        double common = 0;
        int best = 2;
        while (s1.length() > 0 && s2.length() > 0 && best != 0) {
            best = 0;
            l1 = s1.length();
            l2 = s2.length();
            int i = 0, j = 0;
            int startS1 = 0, endS1 = 0;
            int startS2 = 0, endS2 = 0;
            int p = 0;
            for (i = 0; (i < l1) && (l1 - i > best); i++) {
                j = 0;
                while (l2 - j > best) {
                    int k = i;
                    for (; (j < l2) && (s1.charAt(k) != s2.charAt(j)); j++) {}
                    if (j != l2) {
                        p = j;
                        for (j++, k ++; (j < l2) && (k < l1)
                                && (s1.charAt(k) == s2.charAt(j)); j++, k++) {}
						if (k - i > best) {
			                            best = k - i;
			                            startS1 = i;
			                            endS1 = k;
			                            startS2 = p;
			                            endS2 = j;
						}
		            }
                }
            }
            char[] newString = new char[s1.length() - (endS1 - startS1)];
            j = 0;
            for (i = 0; i < s1.length(); i++) {
                if (i >= startS1 && i < endS1) {
                    continue;
                }
                newString[j++] = s1.charAt(i);
            }
            s1 = new String(newString);
            newString = new char[s2.length() - (endS2 - startS2)];
            j = 0;
            for (i = 0; i < s2.length(); i++) {
                if (i >= startS2 && i < endS2) {
                    continue;
                }
                newString[j++] = s2.charAt(i);
            }
            s2 = new String(newString);
            if (best > 2) {
                common += best;
            } else {
                best = 0;
            }
        }
        double commonality = 0;
        double scaledCommon = (double) (2 * common) / (L1 + L2);
        commonality = scaledCommon;

		return commonality;
    }
	
	
	//We derived this method from the substring metric below.
	//THe formula is commonality - max(0, (numOfSubstrings - 1 / numOfMaximumPossibleSubstrings)
	//we could say commonality - fragmentation
	public static double AMsubstringScore(String s1, String s2)
    {
		//added
		double numOfSubstring = 0;
		double maxNumberOfSubstrings = Math.min(s1.length(),s2.length()); //at most each letter in the smaller string can be a substring of the bigger string
		double fragmentationWeight = 1; //this allow parametrization, to increase or reduce fragmentation relevance, if it's one it doesn't effect the formula
		
		int l1 = s1.length(), l2 = s2.length();
        int L1 = l1, L2 = l2;
        double common = 0;
        int best = 2;
        while (s1.length() > 0 && s2.length() > 0 && best != 0) {
            best = 0;
            l1 = s1.length();
            l2 = s2.length();
            int i = 0, j = 0;
            int startS1 = 0, endS1 = 0;
            int startS2 = 0, endS2 = 0;
            int p = 0;
            for (i = 0; (i < l1) && (l1 - i > best); i++) {
                j = 0;
                while (l2 - j > best) {
                    int k = i;
                    for (; (j < l2) && (s1.charAt(k) != s2.charAt(j)); j++) {}
                    if (j != l2) {
                        p = j;
                        for (j++, k ++; (j < l2) && (k < l1)
                                && (s1.charAt(k) == s2.charAt(j)); j++, k++) {}
						if (k - i > best) {
			                            best = k - i;
			                            startS1 = i;
			                            endS1 = k;
			                            startS2 = p;
			                            endS2 = j;
						}
		            }
                }
            }
            char[] newString = new char[s1.length() - (endS1 - startS1)];
            j = 0;
            for (i = 0; i < s1.length(); i++) {
                if (i >= startS1 && i < endS1) {
                    continue;
                }
                newString[j++] = s1.charAt(i);
            }
            s1 = new String(newString);
            newString = new char[s2.length() - (endS2 - startS2)];
            j = 0;
            for (i = 0; i < s2.length(); i++) {
                if (i >= startS2 && i < endS2) {
                    continue;
                }
                newString[j++] = s2.charAt(i);
            }
            s2 = new String(newString);
            if (best > 2) {
                common += best;
                //added: we have found a new substring
                numOfSubstring++;
            } else {
                best = 0;
            }
        }
        double commonality = 0;
        double scaledCommon = (double) (2 * common) / (L1 + L2);
        commonality = scaledCommon;
		
        //Added
        double fragmentation = (numOfSubstring - 1); //if we find only one substring there is no fragmentantion, in fact when 2 strings are the same the final measure must be 1, commonality is one so fragmentation must be 0.
        double scaledFragmentation = fragmentation / maxNumberOfSubstrings;
        double weightedFragmentation = fragmentationWeight * scaledFragmentation;
        double winklerImprovement = winklerImprovement(s1, s2, commonality);
        //double winklerImprovement = 0; //RIGHT NOW WE FORCE THIS TO 0
        if(weightedFragmentation > 0) {
        	return commonality - weightedFragmentation + winklerImprovement;
        }
        else return commonality + winklerImprovement; //only when no substrings are found the fragmentation is negative
		
    }
	
    private static double winklerImprovement(String s1, String s2, double commonality)
    {
        int i, n = Math.min(s1.length(), s2.length());
        for (i = 0; i < n; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
        }
        double commonPrefixLength = Math.min(4, i);
        double winkler = commonPrefixLength * 0.1 * (1 - commonality);
        return winkler;
    }
	
	
	


	
}
