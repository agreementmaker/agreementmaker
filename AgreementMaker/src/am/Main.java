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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import am.app.Core;
import am.batchMode.simpleBatchMode.SimpleBatchModeRunner;
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
	 * It instantiates the UI.
	 * 
	 * @param args Command line arguments, used for operating AgreementMaker in automatic mode, without a UI.
	 */
	public static void main(String args[])
	{
		
		//BasicConfigurator.configure();
		
		if(args.length == 0 ){
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.brushMetalLook", "true");
			
			Thread mainUI = new Thread() {
					public void run() {
						Core.setUI( new UI() );
					} 
			};
			
			mainUI.start();
		}
		else{
			
			// batch mode here, let's parse the command line arguments
			
			OptionParser parser = new OptionParser() {
				{
					acceptsAll( Arrays.asList( "h", "?" ), "show help" );
					acceptsAll( Arrays.asList( "b" , "batch-mode") , "Enable batch mode." );
					acceptsAll( Arrays.asList( "i", "input"), "Input XML batchmode file.")
						.withRequiredArg().ofType(File.class);
					acceptsAll( Arrays.asList( "o", "output"), "Output directory.")
						.withRequiredArg().ofType(File.class);
				}
			};
			
			OptionSet options = parser.parse(args);
			
			if( options.has( "?" ) ) {
				try {
					parser.printHelpOn( System.out );
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
			
			if( options.has( "batch-mode" ) ) {
				inputFileSanityChecks(options);
				File input = (File) options.valueOf("input");
				File output = (File) options.valueOf("output");

				try {
					SimpleBatchModeRunner bmRunner = null;
					if( options.has( "output" ) ) {
						bmRunner = new SimpleBatchModeRunner(input, output);
						bmRunner.runBatchMode();
					} else {
						bmRunner = new SimpleBatchModeRunner(input);
						bmRunner.runBatchMode();
					}
				} catch (Throwable e) {
					e.printStackTrace();
					System.exit(1);
				}
				System.exit(0);
			}
			
			
			
			// TODO: Reuse the old code for batch mode? -- Cosmin.
/*			String track = args[0];
			String subTrack = "";
			if(args.length > 1){
				subTrack = args[1];
			}
			TrackDispatcher.dispatchTrack(track, subTrack);*/
		}
	}
	
	/**
	 * Sanity checks the input and output files passed in the options.
	 * @param options
	 */
	private static void inputFileSanityChecks(OptionSet options) {
		if( !options.has( "input" ) ) {	System.err.println("You must specify an input XML file for batch mode."); System.exit(1); }
		
		File input = (File) options.valueOf("input");
		if( !input.exists() ) { System.err.println("Input file does not exist."); System.exit(1); }
		
		if( options.has( "output" ) )  {
			File output = (File) options.valueOf("output");
			if( !output.exists() ) { System.err.println("Output directory does not exist"); System.exit(1); }
			if( !output.isDirectory() ) { System.err.println("Output file is not a directory."); System.exit(1); }
		}
	}
}

