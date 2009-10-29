package am.app.feedback;

import am.Utility;
import am.app.Core;
import am.app.feedback.matchers.ExtrapolatingDSI;
import am.app.feedback.ui.SelectionPanel;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceParameters;
import am.userInterface.UI;

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

/**
 * This feedback loop also makes use of a filter that filters out mappings from the mapping space.  
 * 
 * The idea behind the filter is that once a mapping (or more in the m-n case) is selected to be in the final alignment for a source-target pair,
 * we want to penalize all the other mappings, as they will not be part of the alignment (this is why their similarity values are set to 0),
 * and since the mapping is selected and it will be in the final alignment, we can give the mapping the highest similarity (1.0) in hopes that
 * this will help find more mappings (because some other mapping's sim. value may depend on the sim. value of this mapping, so if we increase this
 * sim. then the other sim. will improve).
 * 
 * This filter is best for the user feedback loop, since the user is treated as God, therefore, God's word is absolute.
 * 
 * @author Cosmin Stroe
 * @date 10/27/09
 *
 * TODO: Cardinality is assumed to be 1-1 (very important limitation)! Support must be added for all cardinalities.
 * 
 */

public class FeedbackLoop extends AbstractMatcher  {
	
	public enum executionStage {
		notStarted,
		
		runningInitialMatchers,
		afterInitialMatchers,
		
		runningFilter,
		afterFilter,
		
		runningExtrapolatingMatchers,
		afterExtrapolatingMatchers,
		
		runningCandidateSelection,
		afterCandidateSelection,
		
		runningUserInterface,
		afterUserInterface,
		
	}
	
	private executionStage currentStage;
	
	/**
	 * These variables are overridden.
	 */
	SelectionPanel progressDisplay = null;
	
	FilteredAlignmentMatrix classesMatrix;
	FilteredAlignmentMatrix propertiesMatrix;
	
	public FeedbackLoop() {
	
		initializeUserInterface();
		currentStage = executionStage.notStarted;
	
	};
	
	public void match() {
		

		// the user has to load the ontologies.
		if( Core.getInstance().getSourceOntology() == null || Core.getInstance().getTargetOntology() == null ) {
			Utility.displayErrorPane("Two ontologies must be loaded into AgreementMaker before the matching can begin.", "Ontologies not loaded." );
			return;
		}
		
		
		//************** INITIAL MATCHERS ********************///
		InitialMatchers im = new InitialMatchers();
		
		im.setThreshold( progressDisplay.getHighThreshold() );
		im.setMaxSourceAlign(1);
		im.setMaxTargetAlign(1);
		
		currentStage = executionStage.runningInitialMatchers;
		im.setProgressDisplay(progressDisplay);

		try {
			im.match();
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		classesMatrix = new FilteredAlignmentMatrix( im.getClassesMatrix() );
		propertiesMatrix = new FilteredAlignmentMatrix( im.getPropertiesMatrix() );
		
		classesAlignmentSet = im.getClassAlignmentSet();
		propertiesAlignmentSet = im.getPropertyAlignmentSet();

		im = null;
		currentStage = executionStage.afterInitialMatchers;
		
		do {
		
			//********************** FILTER STAGE *********************///
		
			currentStage = executionStage.runningFilter;
			classesMatrix.filter(classesAlignmentSet);
			propertiesMatrix.filter(propertiesAlignmentSet);
			currentStage = executionStage.afterFilter;
			
			
			//**********************  EXTRAPOLATIING MATCHERS ********/////
			currentStage = executionStage.runningExtrapolatingMatchers;
			
			// TODO: run the extrapolating matchers here
			ExtrapolatingDSI eDSI = new ExtrapolatingDSI();
			DescendantsSimilarityInheritanceParameters params = new DescendantsSimilarityInheritanceParameters();
			params.MCP = 0.75;
			eDSI.setParam(params);
			eDSI.setThreshold(threshold);
			eDSI.setMaxSourceAlign(maxSourceAlign);
			eDSI.setMaxTargetAlign(maxTargetAlign);
			eDSI.addInputMatcher(this);
			
			try {
				eDSI.match();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			classesMatrix = (FilteredAlignmentMatrix) eDSI.getClassesMatrix();
			propertiesMatrix = (FilteredAlignmentMatrix) eDSI.getPropertiesMatrix();
			
			
			currentStage = executionStage.afterExtrapolatingMatchers;
			
			
			
			
			
			
			//**********************  CANDIDATE SELECTION ***********///
			currentStage = executionStage.runningCandidateSelection;
			
			// TODO: run the candidate selection here
			
			currentStage = executionStage.afterCandidateSelection;
			
			//********************* USER FEEDBACK INTERFACE **********//
			currentStage = executionStage.runningUserInterface;
			
			// TODO: run the user interface here
			
			// TODO: check if the user said STOP!
			
			currentStage = executionStage.afterUserInterface;
		
		} while( true );
		
	}


	private void initializeUserInterface() {
		
		progressDisplay = new SelectionPanel(this);
		progressDisplay.showScreen_Start();
		
		UI ui = Core.getInstance().getUI();
		
		ui.addTab("User Feedback Loop", null, progressDisplay, "User Feedback Loop");
		
		
		
	}

	public executionStage getStage() { return currentStage; }
	public void setStage(executionStage stage) { currentStage = stage; }


}
