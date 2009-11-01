package am.app.feedback;

import am.Utility;
import am.app.Core;
import am.app.feedback.matchers.ExtrapolatingDSI;
import am.app.feedback.matchers.ExtrapolatingFS;
import am.app.feedback.ui.SelectionPanel;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.AlignmentSet;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchersRegistry;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParametersPanel;
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
	
	private int K = 4;
	private int M = 2;
	
	
	private boolean automatic;
	//configurations
	public final static String MANUAL = "Manual";
	public final static String AUTO_101_303 = "Auto 101-303";
	
	
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
		
		presentFinalMappings
		
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
	
	}
	
	public void setExectionStage( executionStage st ) {
		currentStage = st;
	}
	
	public void match() {
		

		// the user has to load the ontologies.
		if( Core.getInstance().getSourceOntology() == null || Core.getInstance().getTargetOntology() == null ) {
			Utility.displayErrorPane("Two ontologies must be loaded into AgreementMaker before the matching can begin.", "Ontologies not loaded." );
			return;
		}
		
		//Just for the Experiment Purpose, if the user selects the automatic configuration we need to import the reference
		//the reference name is built-in the code right now.
		AbstractMatcher referenceAlignmentMatcher = null;
		String conf = progressDisplay.getConfiguration();
		if(conf.equals(AUTO_101_303)){
			automatic = true;
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.fileName = "./OAEI09/benchmarks/303/refalign.rdf";
			refParam.format = ReferenceAlignmentMatcher.REF0;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(MatchersRegistry.ImportAlignment, 0);
			referenceAlignmentMatcher.setParam(refParam);
			try {
				referenceAlignmentMatcher.match();
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		else{//MANUAL
			automatic = false;
		}
		
		//************** INITIAL MATCHERS ********************///
		InitialMatchers im = new InitialMatchers();
		
		im.setThreshold( progressDisplay.getHighThreshold() );
		im.setMaxSourceAlign(1);
		im.setMaxTargetAlign(1);
		
		currentStage = executionStage.runningInitialMatchers;
		im.setProgressDisplay(progressDisplay);

		try {
			//System.out.println("Before Initial Matchers");
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
		
		
		boolean stop = false;//when this is set to true it means that the user has clicked the stop button
		AlignmentSet<Alignment> classesToBeFiltered = classesAlignmentSet;
		AlignmentSet<Alignment> propertiesToBeFiltered = propertiesAlignmentSet;
		int iteration = 0;

		do {
			iteration++;
			System.out.println("Current iteration: "+iteration+" - Mappings found: "+(classesToBeFiltered.size() + propertiesToBeFiltered.size()));
			//********************** FILTER STAGE *********************///
		
			currentStage = executionStage.runningFilter;
			classesMatrix.filter(classesToBeFiltered);
			propertiesMatrix.filter(propertiesToBeFiltered);
			currentStage = executionStage.afterFilter;
			
			
			//**********************  EXTRAPOLATIING MATCHERS ********/////
			currentStage = executionStage.runningExtrapolatingMatchers;
			
		    // EXTRAPOLATING DSI
			
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
			//classesAlignmentSet = eDSI.getClassAlignmentSet();
			//propertiesAlignmentSet = eDSI.getPropertyAlignmentSet();
	
			
			//**********************  CANDIDATE SELECTION ***********///
			currentStage = executionStage.runningCandidateSelection;
			
			// TODO: run the candidate selection here
			CandidateSelection cs = new CandidateSelection(this);
			
			cs.runMeasures();
			
			AlignmentSet<Alignment> topAlignments = cs.getCandidateAlignments( K, M);
			
			currentStage = executionStage.afterCandidateSelection;
			
			//********************* USER FEEDBACK INTERFACE OR AUTOMATIC VALIDATIO**********//
			currentStage = executionStage.runningUserInterface;
			Alignment userMapping = null;
			
			if(automatic){//check with the reference if a mapping is correct
				//no more candidates --> we stop
				if(topAlignments.size() == 0){
					stop = true;
					currentStage = executionStage.presentFinalMappings;
				}
				else{//validate mappings against the reference, only one mapping can be validated therefore we take the first correct
					for(int i=0; i < topAlignments.size(); i++){
						Alignment a = topAlignments.getAlignment(i);
						if(a.getEntity1().isProp()){
							if(referenceAlignmentMatcher.getPropertyAlignmentSet().contains(a.getEntity1(), a.getEntity2()) == null){
								userMapping = a;
								break;
							}
						}
						else{
							if(referenceAlignmentMatcher.getClassAlignmentSet().contains(a.getEntity1(), a.getEntity2()) == null){
								userMapping = a;
								break;
							}
							
						}
					}
				}
			}
			
			else{//MANUAL: Display the mappings in the User Interface
				progressDisplay.displayMappings(topAlignments);
				
				while( currentStage == executionStage.runningUserInterface ) {
					// sleep while the user is using the interface
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}

			
			//************************* AFTER USER VALIDATION ***********************
			if( currentStage == executionStage.presentFinalMappings ){
				stop = true;
			}
			else{
				currentStage = executionStage.afterUserInterface;
				
				userMapping = progressDisplay.getUserMapping();
				
				if( userMapping != null ) {
					AlignmentSet<Alignment> userSet = new AlignmentSet<Alignment>();
					
					userSet.addAlignment( userMapping );
					
					if( progressDisplay.isUserMappingClass() ) {
						classesMatrix.filter( userSet );
						classesAlignmentSet.addAlignment(userMapping);
					} else {
						propertiesMatrix.filter( userSet );
						propertiesAlignmentSet.addAlignment(userMapping);
					}

					ExtrapolatingFS eFS = new ExtrapolatingFS();
					
					//AlignmentSet<Alignment> userMappings = new AlignmentSet<Alignment>();
					//userMappings.addAlignment(userMapping);
					
					try {
						eFS.match(userSet);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//I Removed the filtering from here because mappings will be filtered at the beginning
					//of the next iteration in the next filtering phase.
					//classesMatrix.filter( eFS.getClassAlignmentSet() );
					//propertiesMatrix.filter( eFS.getPropertyAlignmentSet() );
					classesToBeFiltered = eFS.getClassAlignmentSet();
					propertiesToBeFiltered = eFS.getPropertyAlignmentSet();
				}
				else{ //All mappings are wrong
					for(int i=0; i < topAlignments.size(); i++){
						Alignment a = topAlignments.getAlignment(i);
						if(a.getEntity1().isProp()){
							//This should work for OWL ontologies at least, I think however that classes and properties should have different loops.
							propertiesMatrix.setSimilarity(a.getSourceKey(), a.getTargetKey(), 0);
						}
						else{
							classesMatrix.setSimilarity(a.getSourceKey(), a.getTargetKey(), 0);
						}
					}
					
					//set these to empty because nothing has to be filtered in the next iteration
					classesToBeFiltered = new AlignmentSet<Alignment>();
					propertiesToBeFiltered = new AlignmentSet<Alignment>();
				}
				currentStage = executionStage.afterExtrapolatingMatchers;	
			}
			
		} while( !stop );
		
		// present the final mappings here
		
	}


	private void initializeUserInterface() {
		
		progressDisplay = new SelectionPanel(this);
		progressDisplay.showScreen_Start();
		
		UI ui = Core.getInstance().getUI();
		
		ui.addTab("User Feedback Loop", null, progressDisplay, "User Feedback Loop");
		
		
		
	}

	public executionStage getStage() { return currentStage; }
	public void setStage(executionStage stage) { currentStage = stage; }
	
	public FilteredAlignmentMatrix getClassesMatrix() {
		return classesMatrix;
	}

	public FilteredAlignmentMatrix getPropertiesMatrix() {
		return propertiesMatrix;
	}


}
