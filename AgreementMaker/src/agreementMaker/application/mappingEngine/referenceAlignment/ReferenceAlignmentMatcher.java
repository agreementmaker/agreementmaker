package agreementMaker.application.mappingEngine.referenceAlignment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import agreementMaker.AMException;
import agreementMaker.application.evaluationEngine.MatchingPair;
import agreementMaker.application.mappingEngine.AbstractMatcher;

public class ReferenceAlignmentMatcher extends AbstractMatcher {

	
	/**
	 * Taken from ReferenceEvaluation.java
	 */
	/**Formats for reference files*/
	public final static String REF1 = "OAEI-N3";
	public final static String REF2 = "TXT: sourcename(tab)targetname";
	public final static String REF3= "TXT: sourceDesc(tab)sourceName(tab)targetName(tab)targetDesc(tab)";
	
	/**Formats for output files*/
	public final static String OUTF1 = "TXT-1";
	
	/** End of Taken */
		
	public ReferenceAlignmentMatcher() {
		super();
		needsParam = true;
		maxSourceAlign = ANY_INT;
		maxTargetAlign = ANY_INT;
		threshold = 0.01;
		parametersPanel = new ReferenceAlignmentParametersPanel();
	}
	
	protected void beforeAlignOperations() {
		ReferenceAlignmentParameters param = (ReferenceAlignmentParameters) parametersPanel.getParameters();
		System.out.println(param.fileName+" "+param.format);
		
	}
	
	/**
	 * Parse the reference file: the file gets opened, depending on fileformat the file gets parsed differently, invoking a specific parse for that format
	 * If a developer is going to add a new format file, should add an if case and invokes the specific new parser.
	 * @param filename
	 * @return ArrayList of refpairs
	 */
	public ArrayList<MatchingPair> readReferenceFile() throws AMException, Exception{
		ArrayList<MatchingPair> result = null;

		ReferenceAlignmentParameters param = (ReferenceAlignmentParameters) parametersPanel.getParameters();
	    //Open the reference file
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(param.fileName));
		}
		catch(FileNotFoundException e) {
			//exception that has to be catched in the user interface class to print a message to the user
			throw new AMException(AMException.FILE_NOT_FOUND+"\n"+param.fileName);
		}
		
		//depending on file format a different parser is invoked
		if(param.format.equals(REF1)) {
			result = parseRefFormat1(input);
		}
		else if(param.format.equals(REF2)) {
			result = parseRefFormat2(input);
		}
		else if(param.format.equals(REF3)) {
			result = parseRefFormat3(input);
		}
		else {
			//development error, this exception can also be printed only in the console because is for developer users.
			//if the method is not developed the user shouldn't be able to select that format in the formatlist menu.
			throw new Exception("No parsing method has been developed for this reference file format");
		}
		
		return result;

		
	}
	
	//Parsing reference file methods
	/**
	 * This method is taken from the Read_Compare tool developed by William Sunna
	 * This method parse a reference file in OAEI format like weapons, networks, russia...
	 * The lines containing ao:elementA contain the source name
	 * Each lines after that one contains the target name.
	 * EXAMPLE
	 * :Alignment27
	 *a ao:Alignment;
	 *ao:elementA a:NodeA ;
	 * ao:elementB b:NodeA ;
	 *ao:alignmentConfidence "1". 
	 *
	 */
	public ArrayList<MatchingPair> parseRefFormat1(BufferedReader br) throws IOException{
		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
	    
	    String line;
	    while((line = br.readLine()) !=null){
	    	if(line.indexOf("ao:elementA") != -1) {
	        	String source = line.substring(15);
	        	source = source.substring(0,source.length()-2);
	            line = br.readLine();
	            String target = line.substring(15);
	            target = target.substring(0,target.length()-2);
	            MatchingPair r = new MatchingPair(source,target);
	            result.add(r);
	    	}
	    }
	    
	    return result;
	}
		
		/**
		 * Format used for the simplest txt format.
		 * This method parse a reference  file in the format sourceName(tab)--->(tab)targetName or sourceName(tab)targetName
		 */
		public ArrayList<MatchingPair> parseRefFormat2(BufferedReader br) throws IOException{
			ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
		    
		    String line;
		    String source;
		    String target;
		    while((line = br.readLine()) !=null){
		    	String[] split = line.split("\t");
		    	if(split.length == 2) {
		        	source = split[0];
		        	target = split[1];
		            MatchingPair r = new MatchingPair(source,target);
		            result.add(r);
		    	}
		    	else if(split.length == 3) {
		        	source = split[0];
		        	target = split[2];
		            MatchingPair r = new MatchingPair(source,target);
		            result.add(r);
		    	}
		    	//else System.out.println("Some lines in the reference are not in the correct format. Check result please");
		    }
		    return result;
		}
	
	/**
	 * Format used for Madison Dane test case.
	 * This method parse a reference txt file in the format sourceDesc(tab)sourceName(tab)--->(tab)targetName(tab)targetDesc(tab) or sourceDesc(tab)sourceName(tab)targetName(tab)targetDesc(tab)
	 * for for the first comparison method only source name and target name are needed.
	 */
	public ArrayList<MatchingPair> parseRefFormat3(BufferedReader br) throws IOException{
		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
	    
	    String line;
	    String source;
	    String target;
	    while((line = br.readLine()) !=null){
	    	String[] split = line.split("\t");
	    	if(split.length == 5) {
	        	source = split[1];
	        	target = split[3];
	            MatchingPair r = new MatchingPair(source,target);
	            result.add(r);
	    	}
	    	else if(split.length == 4) {
	        	source = split[1];
	        	target = split[2];
	            MatchingPair r = new MatchingPair(source,target);
	            result.add(r);
	    	}
	    	//else System.out.println("Some lines in the reference are not in the correct format. Check result please");
	    }
	    return result;
	}
	
	
}
