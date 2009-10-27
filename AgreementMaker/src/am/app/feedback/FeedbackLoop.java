package am.app.feedback;

/**
 * This class is implemented to run the user feedback loop.
 * 
 * The execution follows exactly from the diagram:
 * 
 * 1. Initial Matchers
 * 2. Filter Mappings
 * 3. Run automatic matchers to try to extrapolated/expand the alignment by finding new mappings.
 * 4. Select candidate mappings to present to the user for alignment.
 * 5. User validation (accept/reject) of the candidates.  
 * 		5a. If user says STOP, loop is ended, and final alignment presented.
 * 6. GOTO 2.
 * 
 * @author Cosmin Stroe
 *
 */

public class FeedbackLoop {
	
	public FeedbackLoop() {
		
		
		// the user has to load the ontologies.
		
		// run the initial matchers.
		InitialMatchers im = new InitialMatchers();
		im.run();
		
		
		do {

			// filter out the unnecessary alignments
			FilterSelectedMappings fsm = new FilterSelectedMappings();
			fsm.runFilter(im);
			
			// Select candidate mappings to be selected to the user using a relevance ranking approach.
			// TODO: Write this part.
			
			// Show the user the interface, and candidate mappings, and have her validate them.
			// TODO: Write this part.

			// run the extrapolating/expanding matchers
			// TODO: Write this part.
			
		} while( true );  // TODO: Change the while condition to reflect the user's choice to stop or keep going.
		
		
		// Prepare final alignment.
		// TODO: Write this part.
		
	}

}
