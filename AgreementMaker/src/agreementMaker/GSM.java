package agreementMaker;
import java.io.PrintStream;

/**
 * GSM class contains global static variables that are used through out the program.
 *
 * @author ADVIS Research Laboratory
 * @version 11/27/2004
 */
public class GSM
{
	// local title
	public static final String LOCALTITLE = "Target Ontology";
	// ontology title
	public static final String ONTOTITLE = "Source Ontology";
	//	OWL File type representation
	public static final int SOURCENODE = 0;
	//	OWL File type representation
	public static final int TARGETNODE = 1;
	public static final int XMLFILE = 0;
	public static final int ONTFILE = 1;
	public static final int RDFSFILE = 2;
	//public static final int DAMLFILE = 3;
	public static final PrintStream out = new PrintStream(System.out, true);
}
    
