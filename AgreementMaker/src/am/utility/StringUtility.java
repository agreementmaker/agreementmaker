package am.utility;

public class StringUtility {
	public static String separateWords(String string){
		if(string == null) return "";
		
		string = string.replaceAll("-", " ");
		string = string.replaceAll("_", " ");
		string = string.replaceAll("/", " ");
		
		while(string.contains("  "))
			string = string.replace("  ", " ");
		
		for (int i = 0; i < string.length()-1; i++) {
			if(Character.isUpperCase(string.charAt(i)) && Character.isLowerCase(string.charAt(i+1)))
				if(i>0 && string.charAt(i-1)!=' '){
					string = string.substring(0, i) + " " + string.substring(i);
					i++;
				}
		}
		return string;
	}
}
