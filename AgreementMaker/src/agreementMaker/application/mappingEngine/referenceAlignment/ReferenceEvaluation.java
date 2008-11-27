package agreementMaker.application.mappingEngine.referenceAlignment;

import java.io.*;
import java.text.NumberFormat;
import java.util.*;

import agreementMaker.AMException;

public class ReferenceEvaluation {
	
	//Constants
	/**Formats for reference files*/
	public final static String REF1 = "OAEI-N3";
	public final static String REF2 = "TXT: sourcename(tab)targetname";
	public final static String REF3= "TXT: sourceDesc(tab)sourceName(tab)targetName(tab)targetDesc(tab)";
	
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

	
	
	//Evaluation measures
	/**number of total relations discovered by AM*/
	public int discovered = 0;
	/**number of total relations in the reference*/
	public int ref = 0;
	/**number of correct relations discovered by AM*/
	public int correct = 0;
	/**CORRECT / DISCOVERED*/
	public double precision = 0;
	/**CORRECT / REF*/
	public double recall;
	/**2(PRECISION*RECALL)/(PRECISION+RECALL)*/
	public double Fmeasure = 0;
	
	//Report Structures, this structures contains more details information to analyze the result of evaluation
	/**Contains the list of matchings relations in the reference not found by the algorithm,
	 * Each string is in the format SourceName\refTargetName\tArelation found by the algorithm with that source but different target or NOT FOUND
	 * A developer can check this list to see which relations are missing. Looking at the third part of the string he can understand what has been found by the algorithm instead
	 * the third part of this string makes sense only if the algorithm run with max 1 relation per each source node, so that the wrong one is only one.
	 * */
	public ArrayList<String> missedRelations = new ArrayList<String>();
	/**Contains the list of matchings relations found by the algorithm but wrong,
	 * Each string is in the format SourceName\TargetName\tThe correct relation in the reference
	 * */
	public ArrayList<String> wrongRelations = new ArrayList<String>();
	/**source\ttarget*/
	public ArrayList<String> correctRelations = new ArrayList<String>();
	

    /**
     * Initialization of the reference evaluation
     * @param oc OntologyController taken from UI
     * @param refN reference file name
     * @param refF ref file format
     * @param outN output file name
     * @param outF output file format
     */
	public ReferenceEvaluation(String refN,String refF, String outN, String outF) {
		refFileName = refN;
		refFormat = refF;
		outFileName = outN;
		outFormat = outF;
		System.out.println("refFileName: "+refFileName);
		System.out.println("refFormat: "+refFormat);
		System.out.println("outFileName: "+outFileName);
		System.out.println("outFormat: "+outFormat);
	}
	
	/**
	 * The evaluation algorithm
	 * @throws AMException expected errors to be catched and noticed to the user
	 * @throws Exception all other unexpected errors
	 */
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
		//ArrayList<MatchingPair> matchingsFound = ontologyController.getDefnMatchingsList(); 
		//comparison(refPairsList,matchingsFound);
		printOutputFile();
	}
	
	private void printOutputFile() throws FileNotFoundException {
		//Create the output final result file
		FileOutputStream out = new FileOutputStream(outFileName);
	    PrintStream p = new PrintStream( out );
	    
	    //measures
	    p.println("RELATIONS DISCOVERED BY THE AGREEMENTMAKER\t"+discovered);
	    p.println("RELATIONS IN REFERENCE\t"+ref);
	    p.println("CORRECT RELATIONS\t"+correct);
	    //p.println("PRECISION\t"+getPercent(precision));
	    //p.println("RECALL\t"+getPercent(recall));
	    //p.println("FMEASURE\t"+getPercent(Fmeasure));
	    p.println("");
	    p.println("");
	    
	    //missed relations table
	    p.println("LIST OF REFERENCED RELATIONS MISSED BY THE ALGORITHM");
	    p.println("SOURCE NAME\tTARGET NAME\tTARGET NAME FOUND INSTEAD BY THE ALGORITHM");
	    p.println("");
	    Iterator it = missedRelations.iterator();
	    String line;
	    while(it.hasNext()){
	    	line = (String)it.next();
	    	p.println(line);
	    }
	    p.println("");
	    p.println("");
	    
	    //wrong relations table
	    p.println("LIST OF WRONG RELATIONS FOUND BY THE ALGORITHM");
	    p.println("SOURCE NAME\tTARGET NAME\tTARGET NAME CORRECT IN THE REFERENCE");
	    p.println("");
	    it = wrongRelations.iterator();
	    while(it.hasNext()){
	    	line = (String)it.next();
	    	p.println(line);
	    }
	    p.println("");
	    p.println("");
	    
	    //correct relations table
	    p.println("LIST OF CORRECT RELATIONS FOUND BY THE ALGORITHM");
	    p.println("SOURCE NAME\tTARGET NAME");
	    p.println("");
	    it = correctRelations.iterator();
	    while(it.hasNext()){
	    	line = (String)it.next();
	    	p.println(line);
	    }
	    p.println("");
	    p.println("");
	    
	    p.close();
	}

	/**
	 * this method execute the real comparison. 
	 * It scan the reference list, comparing each reference pair with all found pairs to count number of correct.
	 * It also builds the list of correct, missed and wrong relations.
	 * There is a problem with duplicate. In fact duplicate have the same name. 
	 * In this algorithm comparison is only based on name, so the only way to distinguish duplicate nodes is to add to their name the definition of the father for example.
	 * this is because the OAEI reference doesn't have distinguish duplicates so it's not possible to evaluate that cases.
	 * Having a reference able to distinguish nodes with same name but different hierarchy, 
	 * A developer user can add a different readReferenceMethod adding hierarchies to names and also a different getMatchingList.
	 * 
	 * @param refPairsList
	 * @param matchingsFound
	 * @throws AMException
	 */
	private void comparison(ArrayList<MatchingPair> refPairsList, ArrayList<MatchingPair> matchingsFound) throws AMException {
	    Iterator<MatchingPair> itref;
	    MatchingPair mref;
	    Iterator<MatchingPair> itmatch;
	    MatchingPair mmatch;
	    boolean isCorrect;
	    
	    //FIRST ITERATION: to find correct and missed relations in the ref not found by the alg
	    //scan the reference list and then match list
	    //CORRECT = Number of correct discovered matchings relations by the algorithm
	    itref = refPairsList.iterator();
	    String correctS;
	    String missedS;
	    while(itref.hasNext()) {
	    	isCorrect = false;
	    	mref = itref.next();
	    	correctS = mref.getNameTabName();
	    	missedS = mref.getNameTabName()+"\tNOTHING FOUND";
	    	itmatch = matchingsFound.iterator();
	    	while(itmatch.hasNext()&& !isCorrect) {
	    		mmatch = itmatch.next();
	    		if(mref.equals(mmatch) ) {//source and target are the same
	    			correct++;
	    			correctRelations.add(correctS);
	    			isCorrect = true;
	    		}
	    		else if(mref.sameSource(mmatch)) {//only the source is the same, i keep track of the wrong target found by AM at the end if i don't find any correct rel for this one i will add it to missed rel 
	    			missedS = mref.getNameTabName()+"\t"+mmatch.targetname;
	    		}
	    	}
	    	if(!isCorrect) {//if i didn't find a relation in the matchings equals to the reference one i add this ref relations to the missed ones
	    		missedRelations.add(missedS);
	    	}
	    }
	    
	   //SECOND ITERATION: to find wrong relations found by the alg not in the ref
	    //scan match list and then reference list
	    itmatch = matchingsFound.iterator();
	    String wrongs;
	    while(itmatch.hasNext()) {
	    	isCorrect = false;
	    	mmatch = itmatch.next();
	    	wrongs = mmatch.getNameTabName()+"\tNOT IN REFERENCE";
	    	itref = refPairsList.iterator();
	    	while(itref.hasNext() && !isCorrect) {
	    		mref = itref.next();
	    		if(mref.equals(mmatch)) {//source and target are the same
	    			isCorrect = true;
	    		}
	    		else if(mref.sameSource(mmatch)) {//only the source is the same, i keep track of the different target in the ref
	    			wrongs = mmatch.getNameTabName()+"\t"+mref.targetname;
	    		}
	    	}
	    	if(!isCorrect) {//if i didn't find a relation in the matchings equals to the reference one i add this ref relations to the wrong ones
	    		wrongRelations.add(wrongs);
	    	}
	    }
	    
	    //REF = Total number of matchings relations in the reference file
	    ref = refPairsList.size();
		//DISCOVERED = Number of discovered matchings relations by the algorithm
	    discovered = matchingsFound.size();
	    //precision = CORRECT / TOTAL
	    if(discovered == 0)
	    	throw new AMException("The AgreementMaker couldn't find any definition matching realtions.\nCheck if the algorithm run.");
	    precision = ((double)correct/(double)discovered);
	    //recall = CORRECT / REF
	    if(ref == 0)
	    	throw new AMException("The reference file contains 0 matching relations.\nPlease check if you read the correct file.");
	    recall = ((double)correct/(double)ref);
	    //F-measure = 2(PRECISION*RECALL)/PRECISION+RECALL
	    Fmeasure = (2*precision*recall)/(precision+recall);
	    
	    //Debugging
	    /*
	    System.out.println(correct+" must be equal to "+correctRelations.size());
	    System.out.println(ref-correct+" must be equal to "+missedRelations.size());
	    //this won't be equal because of duplicates, each matching relation with a duplicate is considered correct only once but the duplicate is not found as wrong so: discovered-correct = wronglist.size()+duplicates
	    System.out.println(discovered-correct+" must be equal to "+wrongRelations.size());
	    */
	    //debugging
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
		else if(refFormat.equals(REF2)) {
			result = parseRefFormat2(input);
		}
		else if(refFormat.equals(REF3)) {
			result = parseRefFormat3(input);
		}
		else {
			//development error, this exception can also be printed only in the console because is for developer users.
			//if the method is not developed the user shouldn't be able to select that format in the formatlist menu.
			throw new Exception("No parsing method has been developed for this reference file format");
		}
		
		return result;

		
	}

	

	
	/**
	 * must be invoked after the evaluation process
	 * @return a user message reporting all calculated measures
	 */
	public String getReport() {
		String result = "Reference Evaluation Complete\n\n";
		result+="Matchings discovered: "+discovered+"\n";
		result+="Matchings in Reference: "+ref+"\n";
		result+="Matchings correct: "+correct+"\n\n";
		//result+="Precision = Correct/Discovered: "+getPercent(precision)+"\n";
		//result+="Recall = Correct/Reference: "+getPercent(recall)+"\n";
		//result+="Fmeasure = 2(precision*recall)/(precision+recall): "+getPercent(Fmeasure)+"\n";
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





