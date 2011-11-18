package am.app.mappingEngine.hierarchy;

public class Utilities {
	public static String separateWords(String string){
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
	
	
	public static void main(String[] args) {
		System.out.println(separateWords("CiaoBello"));
		System.out.println(separateWords("PhDStudent"));
		System.out.println(separateWords("SWConference"));
		System.out.println(separateWords("ciao_bello"));
		System.out.println(separateWords("PhD_student"));
		System.out.println(separateWords("SWC-            conference"));
		System.out.println(separateWords("Ciao_Bello"));
		System.out.println(separateWords("PhD_Student"));
		System.out.println(separateWords("SWC-Conference"));
	}

}
