package am;

import am.application.Core;
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
	
	/*******************************************************************************************/
	/**
	 * This is the main function
	 * It creates a new instance of UI class
	 *
	 * @param  args array of strings
	 */
	public static void main(String args[])
	{
		if(args.length == 0){
			//UI ui;
			UI ui = new UI();
			Core.getInstance().setUI(ui);
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

