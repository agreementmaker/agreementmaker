package am.app.similarity;


public class SubstringSim implements StringSimilarityMeasure {

	@Override
	public double getSimilarity(String s1, String s2) {
		if(s1.equals(s2)) {
			return 1d;
		}

		return substringScore(s1,s2);
	}

	// Derived from FALCON AO 
	// return the sum of common characters in all
	// substrings of the two words normalized by the number of total chars
	// So this is only the commonality part is not the whole I-Sub method
	public static double substringScore(final String string1, final String string2) {
		double common = 0;
		int best = 2;

        String currentString1 = string1;
        String currentString2 = string2;
		while (currentString1.length() > 0 && currentString2.length() > 0 && best != 0) {
			best = 0;
			final int currentLength1 = currentString1.length();
			final int currentLength2 = currentString2.length();
			int startS1 = 0, endS1 = 0;
			int startS2 = 0, endS2 = 0;
			for (int i = 0, j; i < currentLength1 && currentLength1 - i > best; i++) {
				j = 0;
				while (currentLength2 - j > best) {
					int k = i;
					while(j < currentLength2 && currentString1.charAt(k) != currentString2.charAt(j)) {
					    j++;
					}
					if (j != currentLength2) {
						int p = j;
                        j++;
                        k++;
						while (j < currentLength2 && k < currentLength1 &&
                                currentString1.charAt(k) == currentString2.charAt(j)) {
						    j++;
                            k++;
                        }
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

            currentString1 = excludeRange(currentString1, startS1, endS1);
            currentString2 = excludeRange(currentString2, startS2, endS2);

            if (best > 2) {
				common += best;
			} else {
				best = 0;
			}
		}

        return (2 * common) / (string1.length() + string2.length());
	}

	static String excludeRange(String currentString1, int startS1, int endS1) {
	    return currentString1.substring(0, startS1) + currentString1.substring(endS1);
    }
}