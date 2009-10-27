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
		
		// filter out the unnecessary alignments
		FilterSelectedMappings fsm = new FilterSelectedMappings();
		fsm.runFilter(im);
		
		
		
		
	}

}
