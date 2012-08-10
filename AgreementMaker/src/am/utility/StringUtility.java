package am.utility;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

import am.app.similarity.StringSimilarityMeasure;

/**
 * This class holds various string cleaning or transformation utilities. All of
 * these should be self-contained so they are declared static.
 * 
 * @author Cosmin Stroe
 * @author Daniele Alfarone
 * 
 */
public class StringUtility {

	public static final Pattern DIACRITICS_AND_FRIENDS = 
			Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
	
	/**
	 * Removes all diacritics from a string. It first does a normalization, to
	 * try to perserve as much of the similarity as possible.
	 */
	public static String stripDiacritics(String str) {
	    str = Normalizer.normalize(str, Normalizer.Form.NFD);
	    str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
	    return str;
	}
	
	public static boolean containsUppercaseLetter(String string) {
		// the flag (?s) allows for multi-line reg exps
		return string.matches("(?s).*[A-Z].*");	
	}
	
	public static String separateWords(String string) {
		if (string == null)
			return "";

		string = string.replaceAll("-", " ");
		string = string.replaceAll("_", " ");
		string = string.replaceAll("/", " ");

		while (string.contains("  "))
			string = string.replace("  ", " ");

		for (int i = 0; i < string.length() - 1; i++) {
			if (Character.isUpperCase(string.charAt(i))
					&& Character.isLowerCase(string.charAt(i + 1)))
				if (i > 0 && string.charAt(i - 1) != ' ') {
					string = string.substring(0, i) + " " + string.substring(i);
					i++;
				}
		}
		return string;
	}

	/**
	 * this method is useful whenever we are dealing with unstructured 'dirty'
	 * text eg. before passing the text to Reverb Daniele
	 */
	public static String normalizeString(String string) {
		// hash code of character \uC2A0, which is &nbsp;
		// hash code of character \uC2a7, which is SECTION SIGN, �;
		// hash code of character \uC382, which is LATIN CAPITAL LETTER A WITH  CIRCUMFLEX, �;
		final int hashCodeOfCharC2A0 = 160;
		final int hashCodeOfCharC2a7 = 167;

		final int hashCodeOfCharC382 = 194;
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			Character c = string.charAt(i);
			if (c.hashCode() == hashCodeOfCharC2A0)
				sb.append(" ");
			else if (c.hashCode() == hashCodeOfCharC2a7)
				sb.append("");

			else if (c.hashCode() == hashCodeOfCharC382)
				sb.append("");
			else
				sb.append(c);
		}

		String cleanedString = sb.toString().trim().replaceAll("\\s+", " ");
		cleanedString = cleanedString.replace("�", "'");
		

		return cleanedString;
	}
	
	/**
	 * Extracts the last N words from a string.
	 * In case the string contains less than N words, the whole string is returned.
	 * 
	 * @return last N words of the string.
	 */
	public static String extractLastNWords(String string, int n) {
		String[] words = string.split("\\s+");
		if (words.length <= n) return string;
		
		StringBuilder sb = new StringBuilder();
		for (int i = n; i > 0; i--) {
			sb.append(words[words.length - i]);
			sb.append(" ");
		}
		
		return sb.toString().trim();
	}
	
	/**
	 * Quotations will be attached either on the preceding or on the following word,
	 * depending on the number of preceding quotations.
	 * e.g.
	 * my name is " barack obama "  senior
	 * becomes
	 * my name is "barack obama" senior
	 * 
	 * @param string
	 * @return a string with normalized quotations
	 */
	public static String normalizeQuotations(String string, String quotationChar) {
		String[] tokens = string.split("\\s*" + quotationChar + "\\s*");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			sb.append(tokens[i]);
			
			if (i == tokens.length - 1) break;
			
			if (i % 2 == 0)
				sb.append(" " + quotationChar);
			else
				sb.append(quotationChar + " ");
		}
		
		return sb.toString().trim();
	}
	
	/**
	 * Given a list of source labels and a list of target labels, find the most
	 * similar pair of labels using the given similarity metric.
	 * 
	 * Current runtime is O(|sourceLabels|*|targetLabels|)*O(SSM) ~
	 * BigOmega(n^2).
	 */
	public static double getMaxStringSimilarity(
			List<String> sourceLabels, List<String> targetLabels, StringSimilarityMeasure ssm) {
				
		double maxStringSimilarity = 0.0d;
		for( String sourceLabel : sourceLabels ) {
			for( String targetLabel : targetLabels ) {
				double currentSim = ssm.getSimilarity(sourceLabel, targetLabel);
				maxStringSimilarity = Math.max(currentSim, maxStringSimilarity);
			}
		}
		
		return maxStringSimilarity;
	}
}