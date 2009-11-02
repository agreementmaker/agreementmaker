package am.app.feedback;

import java.util.Iterator;

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
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.dsi.DescendantsSimilarityInheritanceParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParametersPanel;
import am.app.ontology.Node;
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
	
	FeedbackLoopParameters param = null;
	
	AbstractMatcher referenceAlignmentMatcher;
	
	public FeedbackLoop() {
	
		initializeUserInterface();  // add a new tab, and display the parameters screen to the user
		setStage( executionStage.notStarted );
		needsParam = true;
	
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
		
		if( param == null ) {
			Utility.displayErrorPane("Feedbackloop parameters are not set.", "Parameters not set." );
			return;
		}
		
		//Just for the Experiment Purpose, if the user selects the automatic configuration we need to import the reference
		//the reference name is built-in the code right now.
		referenceAlignmentMatcher = null;
		String conf = param.configuration;
		
		K = param.K;
		M = param.M;
		
		if(conf.equals(AUTO_101_303)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
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
		setStage(executionStage.afterUserInterface);
		InitialMatchers im = new InitialMatchers();
		
		im.setThreshold( param.highThreshold );
		im.setMaxSourceAlign(1);
		im.setMaxTargetAlign(1);
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
		setStage( executionStage.afterInitialMatchers );
		
		
		boolean stop = false; //when this is set to true it means that the user has clicked the stop button
		AlignmentSet<Alignment> classesToBeFiltered = classesAlignmentSet;
		AlignmentSet<Alignment> propertiesToBeFiltered = propertiesAlignmentSet;
		int iteration = 0;

		do {
			System.out.println("");
			
			iteration++;
			System.out.println("Current iteration: "+iteration+" - Mappings found: "+(classesToBeFiltered.size() + propertiesToBeFiltered.size()));

			//********************** FILTER STAGE *********************///
		
			setStage( executionStage.runningFilter );
			
			classesMatrix.validateAlignments(classesToBeFiltered);
			propertiesMatrix.validateAlignments(propertiesToBeFiltered);

			classesAlignmentSet.addAllNoDuplicate( classesToBeFiltered );
			propertiesAlignmentSet.addAllNoDuplicate( propertiesToBeFiltered);
			
			classesMatrix.filterCellsBelowThreshold( param.lowThreshold );
			propertiesMatrix.filterCellsBelowThreshold( param.lowThreshold );
			
			classesToBeFiltered = new AlignmentSet<Alignment>(); // create a new, empty set
			propertiesToBeFiltered = new AlignmentSet<Alignment>(); // create a new, empty set
			
			setStage( executionStage.afterFilter );
			
			
			
			//**********************  CANDIDATE SELECTION ***********///
			setStage( executionStage.runningCandidateSelection );
			
			// TODO: run the candidate selection here
			CandidateSelection cs = new CandidateSelection(this);
			
			cs.runMeasures();
			
			AlignmentSet<Alignment> topAlignments = cs.getCandidateAlignments( K, M);
			
			System.out.println( "Candidate Selection: Found " + Integer.toString(topAlignments.size()) + " candidate alignments.");
			
			for( int aCandidate = 0; aCandidate < topAlignments.size(); aCandidate++ ) {
				Alignment candidateAlignment = topAlignments.getAlignment(aCandidate);
				System.out.println( "  " + Integer.toString(aCandidate) + ". " + candidateAlignment.toString() );
						
			}
			
			setStage( executionStage.afterCandidateSelection );
		
			
			
			
			//********************* USER FEEDBACK INTERFACE OR AUTOMATIC VALIDATION **********//

			setStage( executionStage.runningUserInterface );
			
			Alignment userMapping = null;  // this is the correct mapping chosen by the user
			
			if(automatic){  // AUTOMATIC USER VALIDATION: check with the reference if a mapping is correct
				//no more candidates --> we stop
				if(topAlignments.size() == 0){
					stop = true;
					setStage( executionStage.presentFinalMappings );
					break;  // break out of the feedback loop
				}
				else{  //validate mappings against the reference, only one mapping can be validated therefore we take the first correct
					for(int i=0; i < topAlignments.size(); i++){
						Alignment a = topAlignments.getAlignment(i);
						if(a.getEntity1().isProp()){
							// we are looking at a property alignment
							if(referenceAlignmentMatcher.getPropertyAlignmentSet().contains(a.getEntity1(), a.getEntity2()) != null){
								userMapping = a;
								break;
							}
						}
						else{
							// we are looking at a class alignment
							if(referenceAlignmentMatcher.getClassAlignmentSet().contains(a.getEntity1(), a.getEntity2()) != null){
								userMapping = a;
								break;
							}
							
						}
					}

					if( userMapping == null ) {
						System.out.println( "Automatic User Validation: None of the mappings presented to the user were in the reference alignment.");
					} else {
						System.out.println( "Automatic User Validation: CORRECT mapping: " + 
								userMapping.getEntity1().toString() + " -> " +
								userMapping.getEntity2().toString() );
					}
				}
			}
			
			else{  //MANUAL: Display the mappings in the User Interface
				
				progressDisplay.displayMappings(topAlignments);
				
				while( isStage(executionStage.runningUserInterface) ) {
					// sleep while the user is using the interface
					try {
						Thread.sleep(500);  // sleep for .5 seconds at a time 
					} catch (InterruptedException e) {
						// our sleep was interrupted, so just continue
						//e.printStackTrace();
						break;
					} 
				}
				
				userMapping = progressDisplay.getUserMapping();
			}

			if( isStage(executionStage.presentFinalMappings) ) {
				// the user clicked the stop button.
				stop = true;
				break;  // break out of this while loop
			}
			
			
			//************************* AFTER USER VALIDATION ***********************			
			
			setStage(executionStage.afterUserInterface);
			
			//**********************  EXTRAPOLATIING MATCHERS ********/////
			setStage(executionStage.runningExtrapolatingMatchers);				
				
			
			if( userMapping != null ) {
				AlignmentSet<Alignment> userSet = new AlignmentSet<Alignment>();
				
				userSet.addAlignment( userMapping );
				
				if( userMapping.getAlignmentType() != null && userMapping.getAlignmentType() == alignType.aligningClasses ) {
					classesMatrix.validateAlignments( userSet );
					classesAlignmentSet.addAlignment(userMapping);
				} else {
					propertiesMatrix.validateAlignments( userSet );
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
				

				eFS.select(); // will contain values that already filtered.

				// choose only alignments that are not filtered
				AlignmentSet<Alignment> newClassAlignments_eFS = getNewClassAlignments( eFS );
				AlignmentSet<Alignment> newPropertyAlignments_eFS = getNewPropertyAlignments( eFS );
			
				// report on the eDSI
				System.out.print( "Extrapolating Matcher: eDSI, found " + Integer.toString(newClassAlignments_eFS.size()) + " new class alignments, and " +
						Integer.toString( newPropertyAlignments_eFS.size() ) + " new property alignments.");

				// add any new mappings
				classesToBeFiltered.addAll( newClassAlignments_eFS );
				propertiesToBeFiltered.addAll( newPropertyAlignments_eFS );
				
				
				
				
				
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
				

				eDSI.select(); // will contain values that already filtered.

				// choose only alignments that are not filtered
				AlignmentSet<Alignment> newClassAlignments_eDSI = getNewClassAlignments( eDSI );
				AlignmentSet<Alignment> newPropertyAlignments_eDSI = getNewPropertyAlignments( eDSI );
			
				// report on the eDSI
				System.out.print( "Extrapolating Matcher: eDSI, found " + Integer.toString(newClassAlignments_eDSI.size()) + " new class alignments, and " +
						Integer.toString( newPropertyAlignments_eDSI.size() ) + " new property alignments.");

				// add any new mappings
				classesToBeFiltered.addAll( newClassAlignments_eDSI );
				propertiesToBeFiltered.addAll( newPropertyAlignments_eDSI );	
				
				
			}
			else{ //All mappings are wrong
				
				System.out.println( "All candidate mappings are wrong, filtering out these mappings in the similarity matrix.");
				
				// when all the mappings are wrong they should be filtered from the similarity matrix (on a cell by cell basis)
				for(int i=0; i < topAlignments.size(); i++){
					Alignment a = topAlignments.getAlignment(i);
					if(a.getEntity1().isProp()){
						//This should work for OWL ontologies at least, I think however that classes and properties should have different loops.
						propertiesMatrix.filterCell(a.getSourceKey(), a.getTargetKey());
					}
					else{
						classesMatrix.filterCell(a.getSourceKey(), a.getTargetKey());
					}
				}
			}
			currentStage = executionStage.afterExtrapolatingMatchers;	

			
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
	public boolean isStage( executionStage s ) { return s == currentStage; }
	public void setStage(executionStage stage) {
		currentStage = stage;
		System.out.println(">> Changing execution stage to: " + currentStage.name() );
	}
	
	public FilteredAlignmentMatrix getClassesMatrix() {
		return classesMatrix;
	}

	public FilteredAlignmentMatrix getPropertiesMatrix() {
		return propertiesMatrix;
	}

	
	
	private AlignmentSet<Alignment> getNewClassAlignments( AbstractMatcher a ) {
		return getNewMappings( a.getClassAlignmentSet(), classesMatrix );
	}
	
	private AlignmentSet<Alignment> getNewPropertyAlignments( AbstractMatcher a ) {
		return getNewMappings( a.getPropertyAlignmentSet(), propertiesMatrix );
	}

	// look through all the alignments found by the matcher and choose only those that are not already filtered, this way
	// we are choosing only the new alignments to be filtered
	private AlignmentSet<Alignment> getNewMappings( AlignmentSet<Alignment> extrapolatedMatchings, FilteredAlignmentMatrix matrix ) {
				
		AlignmentSet<Alignment> newAlignments = new AlignmentSet<Alignment>();	
		
		Iterator<Alignment> clsIter = extrapolatedMatchings.iterator();
		while( clsIter.hasNext() ) {
			Alignment extrapolatedAlignment = clsIter.next();
			if( !matrix.isCellFiltered( extrapolatedAlignment.getEntity1().getIndex(), extrapolatedAlignment.getEntity2().getIndex() ) ) {
				// the extrapolated alignment is a new alignment
				newAlignments.addAlignment( extrapolatedAlignment );
			}
		}
		return newAlignments;
	}
	
	// check to see if a Node concept is in the reference alignment  
	public boolean isInReferenceAlignment( Node nc ) {
		if( referenceAlignmentMatcher == null ) { return false; } // we cannot check the reference alignment if it not loaded
		AlignmentSet<Alignment> cset = referenceAlignmentMatcher.getClassAlignmentSet();
		AlignmentSet<Alignment> pset = referenceAlignmentMatcher.getPropertyAlignmentSet();
		
		if( !nc.isProp() ) {
			return cset.contains(nc) != null;
		} else {
			return pset.contains(nc) != null;
		}
		
	}
	
	// an alignment with this node has been validated by the user
	public boolean isValidated( Node nc ) {
		if( referenceAlignmentMatcher == null ) { return false; } // we cannot check the reference alignment if it is not loaded
	
		if( !nc.isProp() ) {
			return classesAlignmentSet.contains(nc) != null;
		} else { 
			return propertiesAlignmentSet.contains(nc) != null;
		}
		
	}
	
	public void setParam( FeedbackLoopParameters p ) {
		this.param = p;
	}
}
