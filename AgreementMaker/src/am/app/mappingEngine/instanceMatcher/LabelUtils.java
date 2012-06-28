package am.app.mappingEngine.instanceMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import am.app.ontology.instance.Instance;

import com.hp.hpl.jena.rdf.model.Statement;

public class LabelUtils {
	
	public static String[] organizationKeywords = { "Corporation", "Corp", "Organization", "Inc", "Company", "Co", "Incorporated", "Assn", "LP", "Theater",
			"Newspaper", "Association", "Group" }; 	
	
	//Default method for cleaning labels
	public static String processLabel(String label) {
		label = label.toLowerCase();
		
		//remove stuff between parentheses
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
			
		String[] split = label.split(" ");
		
		StringBuilder cleanString = new StringBuilder();
		for (int i = 0; i < split.length; i++) {
			if(split[i].length() == 1) continue; // discard any words less than 2 characters
			cleanString.append(split[i].trim()).append(" ");
		}

		return cleanString.toString(); 
	}
	
	
	
	//Default method for cleaning labels
	/**
	 * Almost identical to {@link #processLabel(String)}, except we also ignore
	 * everything past the first comma in the string.
	 */
	public static String processLabelWithComma(String label) {
		
		String returnString = processLabel(label);
		
		if(returnString.contains(",")){
			String[] returnSplit = returnString.split(",");
			return returnSplit[0];
		}
		
		return returnString; 
	}

	public static String processOrganizationLabel(String label){
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
		
		String[] blackList = { "Corporation", "Corp", "Inc", "Company", "Co", "Incorporated", "Assn", "LP", "Theater" };
		String[] split = label.split(" ");
		
		label = "";		
		for (int i = 0; i < split.length; i++) {
			boolean toBlock = false;			
			
			for (int j = 0; j < blackList.length; j++) {
				if(split[i].equalsIgnoreCase(blackList[j])){
					toBlock = true;
					break;
				}
			}
			if(!toBlock) label += split[i] + " ";
		}
		return label;
	}
	
	//TODO fix this method
	// Fix what exactly? -- Cosmin, Jun 28, 2012.
	public static String processPersonLabel(String label) {
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
		
		if(label.contains(",")){
			String[] split = label.split(",");
			StringBuilder temp = new StringBuilder();
			for( String s : split ) {
				temp.append(s.trim());
			}
			label = temp.toString();
		}
				
		String[] blackList = { "Jr" };
		
		String[] split = label.split(" ");
		
		label = "";		
		for (int i = 0; i < split.length; i++) {
			boolean toBlock = false;			
			
			for (int j = 0; j < blackList.length; j++) {
				if(split[i].equalsIgnoreCase(blackList[j])){
					toBlock = true;
					break;
				}
			}
			
			if(!toBlock) label += split[i] + " ";
		}
		return label;
	}

	public static String processTypes(String type){
		List<String> orgList = new ArrayList<String>();
		List<String> perList = new ArrayList<String>();
		List<String> locList = new ArrayList<String>();
		String standardType = new String();
		type = type.toLowerCase();
		
		// add to the lists if there are more representations
		
		String[] orgArray = { "corporation", "corp", "org","inc", "company", "co", 
							  "incorporated", "assn", "lp", "theater", "committee", "institute", "institution",
							  "association"};
		orgList.addAll( Arrays.asList(orgArray));
		
		String[] perArray = { "person", "per" }; 
		perList.addAll( Arrays.asList(perArray));
		
		String[] locArray = { "location", "loc" }; 
		locList.addAll( Arrays.asList(locArray));
		
		//remove not required tokens like Infobox
		type = type.replaceAll("infobox_", "");
		type = type.replaceAll("infobox", "");
			
		//check for containment
		if(orgList.contains(type))
			standardType = "organization";
		else if(perList.contains(type))
			standardType = "person";
		else if(locList.contains(type))
			standardType = "location";
		else
			standardType = type; //"ukn";
		
		return standardType; //return the standard type name
	}
	
	public static Set<String> getLabelsFromStatements(Instance instance){
		
		List<Statement> stmts = instance.getStatements();
		Set<String> labels = new HashSet<String>();

		for(Statement s: stmts){
			String prop = s.getPredicate().getURI();
	
			// prop.endsWith("/name") || prop.endsWith("#name")
			// || prop.endsWith("/label") || prop.endsWith("#label")
			
			if (prop.endsWith("name") || prop.endsWith("label")) {
				// System.out.println(prop);
				labels.add(s.getObject().toString());
			}
		}
		
		return labels;
	}
	
	public static String getLabelFromStatements(Instance instance){

		List<Statement> stmts = instance.getStatements();

		for(Statement s: stmts){
			String prop = s.getPredicate().getURI();

			// prop.endsWith("/name") || prop.endsWith("#name")
			// || prop.endsWith("/label") || prop.endsWith("#label")

			if (prop.endsWith("name") || prop.endsWith("label")) {
				// System.out.println(prop);
				return s.getObject().toString();
			}
		}

		return null;
	}
	
	public static List<String> getAliasesFromStatements(Instance instance){
		List<Statement> stmts = instance.getStatements();
		List<String> aliases = new ArrayList<String>();
		for(Statement s: stmts){
			String prop = s.getPredicate().getURI();
			if(prop.endsWith("name") || prop.endsWith("label")){
				//System.out.println(prop);
				aliases.add(s.getObject().toString());	
			}
		}
		return aliases;
	}
	
	public static Set<String> getTypeFromStatements(Instance instance){
		
		//get the type of the label from statements
		List<Statement> listofStmt = instance.getStatements();
		List<String> typeList = new ArrayList<String>();
		String targetClassFacts = new String();
				
		for(Statement stmt: listofStmt){
			String prop = stmt.getPredicate().toString().toLowerCase();
			
			//type
			if(prop.contains("type")){
				String type = stmt.getObject().toString();
				typeList.add(type);
				
				//System.out.println("target:" + type);
			}
			
			//class facts
			if(prop.contains("classfacts")){
				targetClassFacts = stmt.getObject().toString();
				typeList.add(targetClassFacts);
				
				//System.out.println("class facts:" + targetClassFacts);
			}
		}
		
		//check whether the target type has 'person', 'org' or 'location'.
		Set<String> standardTypeList = new HashSet<String>();
		for(String type: typeList){
			
			//process into standard naming format
			String standardType = processTypes(type);
			standardTypeList.add(standardType);
			
			/*if(standardType.equalsIgnoreCase("organization")){
				standardTypeList.add("organization");
			}
			else if(standardType.equalsIgnoreCase("person")){
				standardType = "person";
			}
			else if(standardType.equalsIgnoreCase("location")){
				standardType = "location";
				break;
			}
			else { //for ukn
				//check for class facts if 'ukn'
				standardType = "ukn"; 	
			}*/
		}
		
		//check for class facts if 'ukn'
		/*if(standardType.equalsIgnoreCase("ukn"))
			standardType = processTypes(targetClassFacts);*/
				
		return standardTypeList;
	}
	
	public static String processLocationLabel(String label) {
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
		return label;
	}
	
	public static String getBetweenParentheses(String label) {
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			if(end == -1) return null;
			return label.substring(beg + 1, end);
		}
		return null;
	}
	
	public static boolean isAcronym(String string) {
		if (string.length() < 5 && string.equals(string.toUpperCase()))
			return true;
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println(LabelUtils.processOrganizationLabel("Omnicom Group Incorporated"));
		System.out.println(LabelUtils.processOrganizationLabel("Ese Corporation"));
		System.out.println(LabelUtils.processOrganizationLabel("International Shipholding Corporation"));
		System.out.println(LabelUtils.processOrganizationLabel("Signature Bank"));
		System.out.println(LabelUtils.processOrganizationLabel("Roundabout Theater Co"));
		System.out.println(LabelUtils.processOrganizationLabel("Protective Life Corporation"));
		System.out.println(LabelUtils.processPersonLabel("Federico Jr Caimi"));
		String prova = "Federico_Caimi-123";
		//System.out.println(Arrays.toString(prova.split("(\\s|_|-)")));
	}
}
