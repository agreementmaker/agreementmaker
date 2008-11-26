package agreementMaker.application.mappingEngine.referenceAlignment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import agreementMaker.AMException;
import agreementMaker.application.evaluationEngine.MatchingPair;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.ontology.Node;
import agreementMaker.userInterface.AbstractMatcherParametersDialog;

public class ReferenceAlignmentMatcher extends AbstractMatcher {
	
	public ReferenceAlignmentMatcher(int n, String s) {
		super(n, s);
		needsParam = true;
		maxSourceAlign = ANY_INT;
		maxTargetAlign = ANY_INT;
		threshold = 0.01;
		parametersPanel = new ReferenceAlignmentParametersPanel();
	}
	
	protected void beforeAlignOperations() {
		ReferenceAlignmentParameters param = (ReferenceAlignmentParameters)this.param;
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
	
}
