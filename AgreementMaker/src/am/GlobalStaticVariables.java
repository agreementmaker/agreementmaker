package am;
import java.io.PrintStream;

/**
 * GSM class contains global static variables that are used through out the program.
 *
 * @author ADVIS Research Laboratory
 * @version 11/27/2004
 */
public class GlobalStaticVariables
{
	// AgreementMaker Version!
	public static final String AgreementMakerVersion = "v0.2";
	
	/**
	 * IMPORTANT!:  USE_PROGRESS_BAR determines if the matcher will be sending setProgress() messages to the progress dialog.
	 * 
	 * The progress dialog will always be shown, but whether the progress bar will be used can be set here, using the USE_PROGRESS_BAR variable.
	 * This option has been added because using the progress bar adds a little overhead (stepDone() and setProgress() is called from inside the algorithm).
	 * So in order to allow for the developer users to achieve the best running time, the progress bar can be toggled on and off.   
	 */
	public static final boolean USE_PROGRESS_BAR = true;
	
	// local title
	public static final String TARGETTITLE = "Target Ontology";
	// ontology title
	public static final String SOURCETITILE = "Source Ontology";
	//	OWL File type representation
	public static final int SOURCENODE = 0;
	//	OWL File type representation
	public static final int TARGETNODE = 1;
	public static final int XMLFILE = 2;
	public static final int ONTFILE = 1;
	public static final int RDFSFILE = 0;
	//public static final int DAMLFILE = 3;
	
	public static final int RDFXML = 0;
	public static final int RDFXMLABBREV = 1;
	public static final int NTRIPLE = 2;
	public static final int N3  = 3;
	public static final int TURTLE = 4;
	
	
	public final static String[] syntaxStrings  = {"RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "N3", "TURTLE"};
	public static final String[] languageStrings = {"RDFS", "OWL", "XML"};
	
	public static final PrintStream out = new PrintStream(System.out, true);
	
	public static String getSyntaxString(int syntaxIndex) {
		//N3 and TURTLE ARE CONSIDERED SAME SYNTAX FOR THE REASONER
		if(syntaxIndex == 4) {
			syntaxIndex = 3;
		}
		return syntaxStrings[syntaxIndex];
	}
	
	public static String getLanguageString(int l) {

		return languageStrings[l];
	}
}
    
