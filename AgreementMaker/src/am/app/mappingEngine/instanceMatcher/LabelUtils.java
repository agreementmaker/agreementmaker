package am.app.mappingEngine.instanceMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import am.app.ontology.instance.Instance;
import am.app.similarity.StringSimilarityMeasure;

import com.hp.hpl.jena.rdf.model.Statement;

public class LabelUtils {
	
	private static Logger log = Logger.getLogger(LabelUtils.class);
	
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
	
	public static String processPersonLabel(String label) {
		if(label.contains("(")){
			int beg = label.indexOf('(');
			int end = label.indexOf(')');
			if( end == -1 ) end = label.length() - 1;
			label = label.substring(0,beg) + label.substring(end + 1);
			label = label.trim();
		}
		
		if(label.contains(",")){
			String[] split = label.split(",");
			if( split.length == 2 ) {
				// the standard [last name], [first name] form
				// so reverse the order
				label = split[1] + " " + split[0];
			}
			else {
				// multiple commas, not sure how to handle this.
				log.warn("Unexpected input: multiple commas in person name.");
				StringBuilder temp = new StringBuilder();
				for( String s : split ) {
					temp.append(s.trim()).append(" ");
				}
				label = temp.toString();
			}
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
	
	/**
	 * <p>
	 * The name string arrays are expected to be in the western format:
	 * <ul>
	 * <li>[first name [middle name]] [last name]</li>
	 * </ul>
	 * If a name string array has only 1 element, we take this to be either the
	 * first or last name, and consider it an ambiguous name. If the name string
	 * array has more than two tokens, the last token is taken to be last name,
	 * the first token is taken to be the first name, and everything else is
	 * taken to be the middle name or initial.
	 * </p>
	 * 
	 * <p>
	 * The matching gives a lot of weight to the last name, so that if the last
	 * name doesn't match, the similarity will be very low no matter what other
	 * parts match.
	 * </p>
	 * 
	 * @param name1
	 *            The tokens of a name in western format.
	 * @param name2
	 *            The tokens of another name in western format.
	 * @param ssm
	 *            The string similarity metric used to do the string comparison.
	 * @return
	 */
	public static double computeWesternPersonNameSimilarity(String[] name1, String[] name2, StringSimilarityMeasure ssm) {		
		
		final int FIRST_NAME = 0;
		final int LAST_NAME = 1;
		final int AMBIGUOUS_NAME = 2;
		final int MIDDLE_NAME = 3;
		
		String[] separatedName1 = separateName(name1);
		String[] separatedName2 = separateName(name2);
		
		if( separatedName1[AMBIGUOUS_NAME] != null ) {
			// the first name is ambiguous
			if( separatedName2[AMBIGUOUS_NAME] != null ) { // case 1
				// so is the second name
				// compare the ambiguous names.  
				// TODO: Return a lower confidence because of the ambiguity? -- Cosmin.
				return ssm.getSimilarity(separatedName1[AMBIGUOUS_NAME], separatedName2[AMBIGUOUS_NAME]);
			}
			else { // case 2
				// the second name isn't ambiguous
				// compare the ambiguous name (1) with the first name and the last name (2).
				// TODO: Return a lower confidence because of the ambiguity? -- Cosmin.
				return Math.max( ssm.getSimilarity(separatedName1[AMBIGUOUS_NAME], separatedName2[LAST_NAME]),
								  ssm.getSimilarity(separatedName1[AMBIGUOUS_NAME], separatedName2[FIRST_NAME]) );
			}
		}
		else {
			// the first name isn't ambiguous
			if( separatedName2[AMBIGUOUS_NAME] != null ) { // case 3
				// but the second name is ambiguous
				// similar to case 2 above.
				// TODO: Return a lower confidence because of the ambiguity? -- Cosmin.
				return Math.max( ssm.getSimilarity(separatedName1[LAST_NAME], separatedName2[AMBIGUOUS_NAME]),
								  ssm.getSimilarity(separatedName1[FIRST_NAME],separatedName2[AMBIGUOUS_NAME]));
			}
			else { // case 4
				// the second name isn't ambiguous
				// so, both names aren't ambiguous
				if( separatedName1[MIDDLE_NAME] == null && separatedName2[MIDDLE_NAME] == null ) {
					// no middle names are specified, compare first and last names.
					return ssm.getSimilarity(separatedName1[LAST_NAME], separatedName2[LAST_NAME]) *
							ssm.getSimilarity(separatedName1[FIRST_NAME], separatedName2[FIRST_NAME]);
				} 
				else if( separatedName1[MIDDLE_NAME] != null && separatedName2[MIDDLE_NAME] != null ) {
					// both middle names are specified
					// we're going to give a very small weight (10% weight) to the middle name similarity
					return 0.9d * (ssm.getSimilarity(separatedName1[LAST_NAME], separatedName2[LAST_NAME]) *
							ssm.getSimilarity(separatedName1[FIRST_NAME], separatedName2[FIRST_NAME])) +
							0.1d * ssm.getSimilarity(separatedName1[MIDDLE_NAME], separatedName2[MIDDLE_NAME]);
				}
				else {
					// one of the middle names are specified, we're going to ignore the middle name, but
					// give a slightly lower (3% lower) confidence
					return ssm.getSimilarity(separatedName1[LAST_NAME], separatedName2[LAST_NAME]) *
							ssm.getSimilarity(separatedName1[FIRST_NAME], separatedName2[FIRST_NAME]) *
							0.97d;
				}
			}
		}
	}

	/**
	 * <p>
	 * Given the tokens of a person's name, attempts to separate them into first
	 * name and last name.
	 * </p>
	 * 
	 * @param name
	 *            A name in western format.
	 * @return <p>
	 *         The returned array has the following indices:
	 *         <ul>
	 *         <li>Index 0: The first name of the person. May be null if not
	 *         specified or if ambiguous.</li>
	 *         <li>Index 1: The last name of the person. May be null if not
	 *         specified or if ambiguous.</li>
	 *         <li>Index 2: The ambiguous name of the person (when the first
	 *         name and the last name cannot be determined). May be null if
	 *         there isn't any ambiguity.</li>
	 *         <li>Index 3: The middle part of the name. Can be a middle
	 *         initial, a single token middle name, or a multi-token middle
	 *         name. May be null if only first and/or last name are specified.</li>
	 *         </ul>
	 *         </p>
	 * 
	 * @see {@link #computeWesternPersonNameSimilarity}
	 */
	private static String[] separateName(String[] name) {
		String[] separatedName = new String[4];
		
		if( name.length == 1 ) {
			// ambiguous name, can be either the first or last name
			separatedName[2] = name[0];
		}
		else if( name.length == 2 ) {
			// [first] [last]
			separatedName[0] = name[0];
			separatedName[1] = name[1];
		}
		else if( name.length >= 3 ) {
			// first [middle ...] last
			separatedName[0] = name[0];
			separatedName[1] = name[name.length-1];
			
			StringBuilder builder = new StringBuilder();
			for( int i = 1; i < name.length-1; i++ ) {
				builder.append(name[i]).append(" ");
			}
			separatedName[3] = builder.toString();
		}
		
		return separatedName;
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
