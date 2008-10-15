package agreementMaker.development;

import java.io.*;
import java.util.*;

import agreementMaker.AMException;

public class ReferenceEvaluation {
	
	//Constants
	/**Formats for reference files*/
	public final static String REF1 = "OAEI-N3";
	public final static String REF2 = "MADISON-DANE-txt";
	
	/**Formats for output files*/
	public final static String OUTF1 = "TXT-1";
	
	//Init variables
	/**Name of the reference file to compare the algorithm with.*/
	private String refFileName; 
	/**Format of the reference file to compare the algorithm with.*/
	private String refFormat;
	/**Name of the output file to store the result of the evaluation*/
	private String outFileName;
	/**Format of the output file*/
	private String outFormat;
	/**OntologyController to access the structures, mappings and so on, it contains a reference to canvas*/
	private OntologyController ontologyController;
	


	public ReferenceEvaluation(OntologyController oc, String refN,String refF, String outN, String outF) throws AMException, Exception{
		refFileName = refN;
		refFormat = refF;
		outFileName = outN;
		outFormat = outF;
		System.out.println("refFileName: "+refFileName);
		System.out.println("refFormat: "+refFormat);
		System.out.println("outFileName: "+outFileName);
		System.out.println("outFormat: "+outFormat);
		ontologyController = oc;
		

		
	}
	
	
	public void evaluate() throws AMException, Exception {
		ArrayList<MatchingPair> refPairsList = readReferenceFile();
		/*
		 //DEBUGGING
		Iterator it = refPairsList.iterator();
		int i = 1;
		while(it.hasNext()) {
			RefPair r = (RefPair)it.next();
			System.out.println(i+" "+r.sourcename+" "+r.targetname);
			i++;
		}
		//EnD DEBUGGING
		 */
		ArrayList<MatchingPair> matchingsFound = ontologyController.getDefnMatchingsList(); 
		comparison(refPairsList,matchingsFound);
	}
	
	private void comparison(ArrayList<MatchingPair> refPairsList, ArrayList<MatchingPair> matchingsFound) {
		
	}


	/**
	 * Parse the reference file: the file gets opened, depending on fileformat the file gets parsed differently, invoking a specific parse for that format
	 * If a developer is going to add a new format file, should add an if case and invokes the specific new parser.
	 * @param filename
	 * @return ArrayList of refpairs
	 */
	public ArrayList<MatchingPair> readReferenceFile() throws AMException, Exception{
		ArrayList<MatchingPair> result = null;
	    	    
	    //Open the reference file
		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(refFileName));
		}
		catch(FileNotFoundException e) {
			//exception that has to be catched in the user interface class to print a message to the user
			throw new AMException(AMException.FILE_NOT_FOUND+"\n"+refFileName);
		}
		
		//depending on file format a different parser is invoked
		if(refFormat.equals(REF1)) {
			result = parseRefFormat1(input);
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
}
