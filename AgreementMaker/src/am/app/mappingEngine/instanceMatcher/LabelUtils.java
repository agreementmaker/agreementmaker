package am.app.mappingEngine.instanceMatcher;

public class LabelUtils {
	
	public static String processLabel(String label) {
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
		
		if(label.contains(",")){
			String[] splitted = label.split(",");
			label = splitted[1].trim() + " " + splitted[0].trim();
		}
			
		String[] splitted = label.split(" ");
		
		label = "";
		for (int i = 0; i < splitted.length; i++) {
			if(splitted[i].length() == 1) continue;
			label += splitted[i] + " ";
		}
		label = label.trim();
		return label; 
	}

}
