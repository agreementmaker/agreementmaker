package am.utility;

public class StringUtility {
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

	/*
	 * this method is useful whenever we are dealing with unstructured 'dirty'
	 * text eg. before passing the text to Reverb Daniele
	 */
	public static String normalizeString(String string) {
		// hash code of character \uC2A0, which is &nbsp;
		// hash code of character \uC2a7, which is SECTION SIGN, §;
		// hash code of character \uC382, which is LATIN CAPITAL LETTER A WITH  CIRCUMFLEX, Â;
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
		cleanedString = cleanedString.replace("’", "'");
		

		return cleanedString;
	}
}