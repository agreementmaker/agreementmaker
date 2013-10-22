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
	public static final boolean USE_PROGRESS_BAR = true;
	
	
	public static final PrintStream out = new PrintStream(System.out, true);

}
    
