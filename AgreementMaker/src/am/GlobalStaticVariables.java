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
	public static final String AgreementMakerVersion = "v0.23";
	
	/**
	 * IMPORTANT!:  USE_PROGRESS_BAR determines if the matcher will be sending setProgress() messages to the progress dialog.
	 * 
	 * The progress dialog will always be shown, but whether the progress bar will be used can be set here, using the USE_PROGRESS_BAR variable.
	 * This option has been added because using the progress bar adds a little overhead (stepDone() and setProgress() is called from inside the algorithm).
	 * So in order to allow for the developer users to achieve the best running time, the progress bar can be toggled on and off.   
	 */
	public static final boolean USE_PROGRESS_BAR = false;
	
	// ALL DEPRECATED FIELDS MOVED TO Ontology class
	// local title
	@Deprecated public static final String TARGETTITLE = "Target Ontology";
	// ontology title
	@Deprecated public static final String SOURCETITILE = "Source Ontology";
	//	OWL File type representation
	@Deprecated public static final int SOURCENODE = 0;
	//	OWL File type representation
	@Deprecated public static final int TARGETNODE = 1;
	@Deprecated public static final int XMLFILE = 2;
	@Deprecated public static final int OWLFILE = 1;
	@Deprecated public static final int RDFSFILE = 0;
	@Deprecated public static final int TABBEDTEXT = 3;
	//public static final int DAMLFILE = 3;
	
	@Deprecated public static final int RDFXML = 0;
	@Deprecated public static final int RDFXMLABBREV = 1;
	@Deprecated public static final int NTRIPLE = 2;
	@Deprecated public static final int N3  = 3;
	@Deprecated public static final int TURTLE = 4;

	@Deprecated public final static String SYNTAX_RDFXML = "RDF/XML";
	@Deprecated public final static String SYNTAX_RDFXMLABBREV = "RDF/XML-ABBREV";
	@Deprecated public final static String SYNTAX_NTRIPLE = "N-TRIPLE";
	@Deprecated public final static String SYNTAX_N3 = "N3";
	@Deprecated public final static String SYNTAX_TURTLE = "TURTLE";
	@Deprecated public final static String[] syntaxStrings  = {SYNTAX_RDFXML, SYNTAX_RDFXMLABBREV, SYNTAX_NTRIPLE, SYNTAX_N3, SYNTAX_TURTLE};
	@Deprecated public final static String LANG_RDFS = "RDFS";
	@Deprecated public final static String LANG_OWL = "OWL";
	@Deprecated public final static String LANG_XML = "XML";
	@Deprecated public final static String LANG_TABBEDTEXT = "Tabbed TEXT";
	@Deprecated public static final String[] languageStrings = {LANG_RDFS, LANG_OWL, LANG_XML, LANG_TABBEDTEXT};

	
	
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
    
