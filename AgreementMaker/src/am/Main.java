/**    ________________________________________
 * ___/ Copyright Notice / Warranty Disclaimer \________________
 *
 * @copyright { Copyright (c) 2010
 * Advances in Information Systems Laboratory at the
 * University of Illinois at Chicago
 * All Rights Reserved. }
 * 
 * @disclaimer {
 * This work is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. }
 * 
 *     _____________________
 * ___/ Authors / Changelog \___________________________________          
 * 
 *  
 */


package am;

import javax.swing.SwingUtilities;

import am.app.Core;
import am.batchMode.TrackDispatcher;
import am.userInterface.UI;

/**
 * Main class -
 *
 * This class creates an instance of UI class
 *
 * @author ADVIS Research Laboratory
 * @version 11/27/2004
 */
public class Main
{
	/**
	 * This is the application entry point.
	 * It creates instantiates.
	 * 
	 * @param args Command line arguments, used for operating AgreementMaker in automatic mode, without a UI.
	 */
	public static void main(String args[])
	{
		if(args.length == 0 ){
			// Proper way of intializing the UI.
			// Reference: http://java.sun.com/developer/technicalArticles/javase/swingworker/ (Starting off on the Right Thread)
			SwingUtilities.invokeLater( 
					new Runnable() {
						public void run() {
							Core.setUI( new UI() );
						} 
					}
			);
		}
		else{
			String track = args[0];
			String subTrack = "";
			if(args.length > 1){
				subTrack = args[1];
			}
			TrackDispatcher.dispatchTrack(track, subTrack);
		}
	}
}

