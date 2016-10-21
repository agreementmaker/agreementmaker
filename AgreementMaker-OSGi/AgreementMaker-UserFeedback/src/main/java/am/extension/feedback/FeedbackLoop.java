package am.extension.feedback;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherFactory;
import am.app.mappingEngine.MatchingProgressListener;
import am.app.mappingEngine.ReferenceEvaluationData;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentMatcher;
import am.app.mappingEngine.referenceAlignment.ReferenceAlignmentParameters;
import am.app.mappingEngine.referenceAlignment.ReferenceEvaluator;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.extension.feedback.matchers.ExtrapolatingDSI;
import am.extension.feedback.matchers.ExtrapolatingFS;
import am.extension.feedback.ui.SelectionPanel;
import am.matcher.dsi.DescendantsSimilarityInheritanceParameters;
import am.ui.MatchingProgressDisplay;

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
	
	private static final long serialVersionUID = -5492225632641036980L;
	
	private static final Logger sLog = Logger.getLogger(FeedbackLoop.class);
	
	//FeedBackLoop private variables used in the iterations
	private CandidateSelection candidateSelection;
	ArrayList<CandidateConcept> topConceptsAndAlignments;
	private executionStage currentStage;
	private AbstractMatcher referenceAlignmentMatcher;
	private Alignment<Mapping> classesToBeFiltered;
	private Alignment<Mapping> propertiesToBeFiltered;
	private int iteration;
	private boolean stop;//when this is set to true it means that the user has clicked the stop button
	private boolean userContinued;//this is set to false during user validation until the user performs an actions
	private int new_mapping;
	int lastFirstPhaseIteration;
	//User variables
	Mapping userMapping;  // this is the correct mapping chosen by the user
	CandidateConcept userConcept; //this is either the concept of the validated mapping, or the unvalidated concept
	String userAction; 
	//these structures are used to keep track of partial evaluations
	// with spin equal to 5 it means that measure the performances of the alignment every 5 iterations
	private ArrayList<FeedbackIteration> partialEvaluations;
	private ReferenceEvaluationData initialEvaluation;//the initial matcher evaluation to be used for comparison of performances
	private int evaluationSpin;
	private int EDSIcorrect;
	private int EDSIwrong;
	private int EFScorrect;
	private int EFSwrong;
	
	
	//configurations
	public final static String MANUAL = "Manual";	
	public final static String  AUTO_101_301 = "Auto 101-301";
	public final static String  AUTO_101_302 = "Auto 101-302";
	public final static String  AUTO_101_303 = "Auto 101-303";
	public final static String  AUTO_101_304 = "Auto 101-304";
	public final static String  AUTO_animals = "Auto animals";
	public final static String  AUTO_basketball_soccer = "Auto basketball_soccer";
	public final static String  AUTO_comsci = "Auto comsci";
	public final static String  AUTO_hotel = "Auto hotel";
	public final static String  AUTO_network = "Auto network";
	public final static String  AUTO_people_pets = "Auto people+pets";
	public final static String  AUTO_russia = "Auto russia";
	public final static String  AUTO_weapons = "Auto weapons";
	public final static String  AUTO_wine = "Auto wine";
	
	public final static String PRINT_LINE = "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=";
	
	public enum executionStage {
		notStarted,
		
		runningInitialMatchers,
		afterInitialMatchers,
		
		runningFilter,
		afterFilter,
		
		runningCandidateSelection,
		afterCandidateSelection,
		
		runningUserInterface,
		afterUserInterface,
		
		runningAutomaticValidation,
		afterAutomaticValidation,
		
		runningExtrapolatingMatchers,
	    afterExtrapolatingMatchers,
	    
		presentFinalMappings
		
	}
	

	
	public FeedbackLoop() {
		needsParam = true;
		currentStage = executionStage.notStarted;
		iteration = 0;
		evaluationSpin = 1;
		stop = false;
		new_mapping = 0;
		partialEvaluations = new ArrayList<FeedbackIteration>();
		EDSIcorrect =0;
		EDSIwrong=0;
		EFScorrect=0;
		EFSwrong=0;
	}
	
	public void setExectionStage( executionStage st ) {
		currentStage = st;
	}
	
	@Override
	public void match() throws Exception{
		matchStart();
		FeedbackLoopParameters param = (FeedbackLoopParameters)this.param;
		SelectionPanel progressDisplay = null;
		for( MatchingProgressListener mpd : progressDisplays )
			if( mpd instanceof SelectionPanel ) {
				progressDisplay = (SelectionPanel)mpd;
			}
		//the first time we write in the report we use the DISPLAY method, the other times we use the APPEND method
		progressDisplay.displayReportText("User Feedback Loop is started.");
		param.print();
		progressDisplay.appendNewLineReportText(param.getParameterString());
		
		//Just for the Experiment Purpose, if the user selects the automatic configuration we need to import the reference
		//the reference name is built-in the code right now.
		initAutomaticConfiguration();
		
		//************** INITIAL MATCHER********************///
		runInitialMatcher();

		//**************************LOOP*******************************
		candidateSelection = new CandidateSelection(this, param.measure);
		new_mapping = 0 - (classesToBeFiltered.size() + propertiesToBeFiltered.size());
		boolean firstPhase = true;
		do {
			System.out.println("");
			iteration++;
			progressDisplay.appendNewLineReportText(PRINT_LINE+"\nStarting iteration "+iteration);

			//********************** FILTER STAGE *********************///
			filtering();
			
			//**********************  CANDIDATE SELECTION ***********///
			if( isCancelled() || stop  ) break;
			candidateSelection(firstPhase);
			
			//********************* USER FEEDBACK INTERFACE OR AUTOMATIC VALIDATION **********//
			if(topConceptsAndAlignments.size() == 0){//no more candidates
				if(firstPhase){
					//no more candidates for the first phase, switch to the second
					firstPhase = false;
					lastFirstPhaseIteration = iteration;
					if(isInAutomaticMode()){
						evaluate();
					}
					System.out.println("Switch to second phase: No more candidates in first phase");
					progressDisplay.appendNewLineReportText("No more candidates in first phase (similarity-based)");
					continue;
				}
				else{
					//stop
					progressDisplay.appendNewLineReportText("Stop: no more candidates in second phase (relevance-based)");
					System.out.println("Stop: No more candidates in second phase");
					break;
				}
			}

			if( isCancelled() || stop  ) break;
			if(isInAutomaticMode()){
				automaticValidation();
			}
			else{
				userValidation();
			}
			
			//************************* AFTER USER VALIDATION ***********************
			if( isCancelled() || stop  ) break;	
			//The user pressed the continue button
			extrapolatingMatchers();
						
			//****************************END OF THE ITERATION*****************************
			iterationEnd();
			
			if( iteration+1 > param.iterations ) {
				progressDisplay.appendNewLineReportText("Stopping the process because the maximum iteration is reached.");
				System.out.println( ">>> Stopping loop after " + Integer.toString(param.iterations) + " iterations." );
				break;
			}
			if( isCancelled() || stop ) break;
			
		} while( !stop );
		//****************************AFTER THE LOOP*****************************
		setStage(executionStage.presentFinalMappings);
		printEvaluation();
		System.out.println("The Feedback loop completed in "+iteration+" iterations, first phase completed in "+lastFirstPhaseIteration+" iterations.");
		progressDisplay.appendNewLineReportText(PRINT_LINE+"\nThe Feedback loop completed in "+iteration+" iterations, first phase completed in "+lastFirstPhaseIteration+" iterations.");
		matchEnd();
	}

	private void printEvaluation() {
		if(isInAutomaticMode()){
			FeedbackLoopParameters param = (FeedbackLoopParameters)this.param;
			System.out.println("////////Parameters////////////");
			System.out.println("Configuration: "+param.configuration);
			System.out.println("Max iteration: "+param.iterations);
			System.out.println("Hth: "+param.highThreshold);
			System.out.println("Lth: "+param.lowThreshold);
			System.out.println("K: "+param.K);
			System.out.println("M: "+param.M);
			//Present the evaluations
			Alignment<Mapping> fblextrapolationSet = getAlignment();
			
			ReferenceEvaluationData rd_end = ReferenceEvaluator.compare(fblextrapolationSet, referenceAlignmentMatcher.getAlignment());
			FeedbackIteration finalIteration = new FeedbackIteration(iteration);
			finalIteration.evaluationData = rd_end;
			finalIteration.EDSIcorrect = EDSIcorrect;
			finalIteration.EDSIwrong = EDSIwrong;
			finalIteration.EFScorrect = EFScorrect;
			finalIteration.EFSwrong = EFSwrong;
			
			System.out.println( "/////////MEASURES TABLE//////////////////////////////////////////////////////");
			if(partialEvaluations.size() >0 && partialEvaluations.get(partialEvaluations.size()-1).iteration != iteration)
				partialEvaluations.add(finalIteration); //the final iteration could have been evaluated in the loop or not if the looped finished with an intermediate iteration
			double initialPrecision = initialEvaluation.getPrecision();
			double initialRecall = initialEvaluation.getRecall();
			double initialFmeasure = initialEvaluation.getFmeasure();
			int initialFound = initialEvaluation.getFound();
			int exist = initialEvaluation.getExist();
			int initialCorrect = initialEvaluation.getCorrect();
			int initialWrong = initialFound - initialCorrect;
			int initialMissing = exist - initialCorrect;
			System.out.println("Iteration,\tPrecision,\tRecall,\tF-measure,\tPrecision improvement,\tRecall improvement,\tF-measure improvement,\tCorrect,\tWrong,\tMissing,\tCorrect improvement,\tWrong improvement,\tMissing improvement,\tEDSI correct,\tEDSI wrong,\tEFS correct,\tEFS wrong,\tCorrect extrapolating,\tWrong extrapolating");
			System.out.println( 0+",\t"+Utility.getOneDecimalPercentFromDouble(initialPrecision)+",\t"+Utility.getOneDecimalPercentFromDouble(initialRecall)+",\t"+Utility.getOneDecimalPercentFromDouble(initialFmeasure)+",\t0.00%,\t0.00%,\t0.00%,\t"+initialCorrect+",\t"+initialWrong+",\t"+initialMissing+",\t"+0+",\t"+0+",\t"+0+",\t"+0+",\t"+0+",\t"+0+",\t"+0+",\t"+0+",\t"+0);
			for(int i = 0; i < partialEvaluations.size(); i++){
				FeedbackIteration partialIteration = partialEvaluations.get(i);
				ReferenceEvaluationData partialData = partialIteration.evaluationData;
				double p = partialData.getPrecision();
				double impP = p - initialPrecision;
				double r = partialData.getRecall();
				double impR = r - initialRecall;
				double f = partialData.getFmeasure();
				double impF = f - initialFmeasure;
				int found = partialData.getFound();
				int correct = partialData.getCorrect();
				int wrong = found - correct;
				int missing = exist - correct;
				int impCorrect = correct - initialCorrect;
				int impWrong = wrong - initialWrong;
				int impMissing = missing - initialMissing;
				String precision = Utility.getOneDecimalPercentFromDouble(p);
				String recall = Utility.getOneDecimalPercentFromDouble(r);
				String fmeasure = Utility.getOneDecimalPercentFromDouble(f);
				String impPrecision = Utility.getOneDecimalPercentFromDouble(impP);
				String impRecall = Utility.getOneDecimalPercentFromDouble(impR);
				String impFmeasure = Utility.getOneDecimalPercentFromDouble(impF);
				System.out.println( partialIteration.iteration+",\t"+precision+",\t"+recall+",\t"+fmeasure+",\t"+impPrecision+",\t"+impRecall+",\t"+impFmeasure+",\t"+correct+",\t"+wrong+",\t"+missing+",\t"+impCorrect+",\t"+impWrong+",\t"+impMissing+",\t"+partialIteration.EDSIcorrect+",\t"+partialIteration.EDSIwrong+",\t"+partialIteration.EFScorrect+",\t"+partialIteration.EFSwrong+",\t"+(partialIteration.EDSIcorrect+partialIteration.EFScorrect)+",\t"+(partialIteration.EDSIwrong+partialIteration.EFSwrong));
			}
			//understanding why some mappings are not mapped
			Alignment<Mapping> losts = rd_end.getLostAlignments();
			System.out.println("Missing are : "+losts.size()+" or "+(rd_end.getExist() - rd_end.getCorrect()));
			for(int i = 0; i< losts.size(); i++){
				Mapping lost = losts.get(i);
				boolean isProp = lost.getEntity1().isProp();
				System.out.println("---------Lost alignment "+i+": "+lost+" isProp? "+isProp);
				FilteredAlignmentMatrix matrix = (FilteredAlignmentMatrix)classesMatrix;
				Alignment<Mapping> aset = classesAlignmentSet;
				if(isProp){
					matrix = (FilteredAlignmentMatrix)propertiesMatrix;
					aset = propertiesAlignmentSet;
				}
				boolean isRow = matrix.isRowFiltered(lost.getEntity1().getIndex());
				boolean isCol = matrix.isColFiltered(lost.getEntity2().getIndex());
				boolean isCell = matrix.isCellFiltered(lost.getEntity1().getIndex(), lost.getEntity2().getIndex());
				System.out.println("is Row: "+isRow+", is Col: "+isCol+", is Cell: "+isCell);
				Mapping source = aset.contains(lost.getEntity1(), Ontology.SOURCE);
				Mapping target = aset.contains(lost.getEntity2(), Ontology.TARGET);
				System.out.println("Alternative source: "+source);
				System.out.println("Alternative target: "+target);
			}
		}	
		
	}

	private void extrapolatingMatchers() throws Exception {
		//**********************  EXTRAPOLATIING MATCHERS ********/////
		FeedbackLoopParameters param = (FeedbackLoopParameters)this.param;
		FilteredAlignmentMatrix classesMatrix = (FilteredAlignmentMatrix)this.classesMatrix;
		FilteredAlignmentMatrix propertiesMatrix = (FilteredAlignmentMatrix)this.propertiesMatrix;
		SelectionPanel progressDisplay = null;
		for( MatchingProgressListener mpd : progressDisplays )
			if( mpd instanceof SelectionPanel ) {
				progressDisplay = (SelectionPanel)mpd;
			}
		
		if(userAction.equals(SelectionPanel.A_MAPPING_CORRECT)){		
			setStage(executionStage.runningExtrapolatingMatchers);				
			progressDisplay.appendNewLineReportText("1 mapping validated by the user: "+userMapping);
			
			Alignment<Mapping> userSet = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());
			
			userSet.add( userMapping );
			
			if(userConcept.whichType == alignType.aligningClasses ) {
				classesMatrix.validateAlignments( userSet );
				classesAlignmentSet.add(userMapping);
			} else {
				propertiesMatrix.validateAlignments( userSet );
				propertiesAlignmentSet.add(userMapping);
			}
			progressDisplay.appendNewLineReportText("Running extrapolation matchers: ");
			ExtrapolatingFS eFS = new ExtrapolatingFS();
			eFS.getParam().threshold = param.highThreshold;
			eFS.setMaxSourceAlign( param.sourceNumMappings );
			eFS.setMaxTargetAlign( param.targetNumMappings );
			eFS.addInputMatcher(this);
			
			//AlignmentSet<Alignment> userMappings = new AlignmentSet<Alignment>();
			//userMappings.addAlignment(userMapping);
			

			eFS.match(userSet);
			

			eFS.select(); // will contain values that already filtered.

			// choose only alignments that are not filtered
			Alignment<Mapping> newClassAlignments_eFS = getNewClassAlignments( eFS );
			Alignment<Mapping> newPropertyAlignments_eFS = getNewPropertyAlignments( eFS );
			progressDisplay.appendNewLineReportText("\tFamilial Similarity method found "+newClassAlignments_eFS.size()+" class mappings.");
			progressDisplay.appendNewLineReportText("\tFamilial Similarity method found "+newPropertyAlignments_eFS.size()+" property mappings.");
			// report on the eDSI
			System.out.println( "Extrapolating Matcher: eFS, found " + Integer.toString(newClassAlignments_eFS.size()) + " new class alignments, and " +
					Integer.toString( newPropertyAlignments_eFS.size() ) + " new property alignments.");
			
			printAlignments( newClassAlignments_eFS, alignType.aligningClasses );
			printAlignments( newPropertyAlignments_eFS, alignType.aligningProperties );
			
			// add any new mappings
			classesToBeFiltered.addAll( newClassAlignments_eFS );
			propertiesToBeFiltered.addAll( newPropertyAlignments_eFS );
			
			//evaluate EFS
			Alignment<Mapping> set = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());
			set.addAll(newClassAlignments_eFS);
			set.addAll(newPropertyAlignments_eFS);
			ReferenceEvaluationData EFSdata = ReferenceEvaluator.compare(set, referenceAlignmentMatcher.getAlignment());
			EFScorrect += EFSdata.getCorrect();
			EFSwrong += (EFSdata.getFound() - EFSdata.getCorrect());
		}
		else if(userAction.equals(SelectionPanel.A_ALL_MAPPING_WRONG)){ //All mappings are wrong
			progressDisplay.appendNewLineReportText("\tAll candidate mappings are incorrect and filtered out.");
			System.out.println( "All candidate mappings are wrong, filtering out these mappings in the similarity matrix.");
			
			// when all the mappings are wrong they should be filtered from the similarity matrix (on a cell by cell basis)
			for(int i=0; i < topConceptsAndAlignments.size(); i++){
				CandidateConcept c = topConceptsAndAlignments.get(i);
				ArrayList<Mapping> candidateMappings = c.getCandidateMappings();
				if(candidateMappings!= null){
					for(int j = 0; j < candidateMappings.size(); j++){
						Mapping a = candidateMappings.get(j);
						if(c.whichType.equals(alignType.aligningProperties)){
							propertiesMatrix.filterCell(a.getSourceKey(), a.getTargetKey());
						}
						else{
							classesMatrix.filterCell(a.getSourceKey(), a.getTargetKey());
						}
					}
				}
			}
		}
		else if(userAction.equals(SelectionPanel.A_CONCEPT_WRONG)){
			progressDisplay.appendNewLineReportText("\tSelected candidate concept "+userConcept.toString()+" has not to be mapped and is filterd out.");
			System.out.println("User selected to "+SelectionPanel.A_CONCEPT_WRONG);
			ArrayList<CandidateConcept> toBeFiltered = new ArrayList<CandidateConcept>(1);
			toBeFiltered.add(userConcept);
			filterCandidateConcepts(toBeFiltered);
			
			//In the automatic validation, if a candidate concepts is unvalidated it means that all other mappings have to be wrong, or else a correct mapping would have been found
			//this can't be done in the user mode, because the user may select a candidate concept as wrong even if another of the candidate mappings is correct.
			if(isInAutomaticMode()){
				// when all the mappings are wrong they should be filtered from the similarity matrix (on a cell by cell basis)
				for(int i=0; i < topConceptsAndAlignments.size(); i++){
					CandidateConcept c = topConceptsAndAlignments.get(i);
					if(!c.equals(userConcept)){
						ArrayList<Mapping> candidateMappings = c.getCandidateMappings();
						if(candidateMappings!= null){
							for(int j = 0; j < candidateMappings.size(); j++){
								Mapping a = candidateMappings.get(j);
								if(c.whichType.equals(alignType.aligningProperties)){
									propertiesMatrix.filterCell(a.getSourceKey(), a.getTargetKey());
								}
								else{
									classesMatrix.filterCell(a.getSourceKey(), a.getTargetKey());
								}
							}
						}
					}
				}
			}
		}
		else if(userAction.equals(SelectionPanel.A_ALL_CONCEPT_WRONG)){
			progressDisplay.appendNewLineReportText("\tAll candidate concepts has not to be mapped and are filterd out.");
			System.out.println("User selected to "+SelectionPanel.A_ALL_CONCEPT_WRONG);
			filterCandidateConcepts(topConceptsAndAlignments);
		}
		
	    // EXTRAPOLATING DSI
		//DSI should be executed at each iteration for each user action
		ExtrapolatingDSI eDSI = new ExtrapolatingDSI();
		DescendantsSimilarityInheritanceParameters params = new DescendantsSimilarityInheritanceParameters();
		params.MCP = 0.75;
		eDSI.setParameters(params);
		eDSI.getParam().threshold = getParam().threshold;
		eDSI.setMaxSourceAlign(getMaxSourceAlign());
		eDSI.setMaxTargetAlign(getMaxTargetAlign());
		eDSI.addInputMatcher(this);
		
		eDSI.match();
		eDSI.select(); // will contain values that already filtered.

		// choose only alignments that are not filtered
		Alignment<Mapping> newClassAlignments_eDSI = getNewClassAlignments( eDSI );
		Alignment<Mapping> newPropertyAlignments_eDSI = getNewPropertyAlignments( eDSI );
		progressDisplay.appendNewLineReportText("\tDSI found "+newClassAlignments_eDSI.size()+" class mappings.");
		progressDisplay.appendNewLineReportText("\tDSI found "+newPropertyAlignments_eDSI.size()+" property mappings.");
		
		// report on the eDSI
		System.out.println( "Extrapolating Matcher: eDSI, found " + Integer.toString(newClassAlignments_eDSI.size()) + " new class alignments, and " +
				Integer.toString( newPropertyAlignments_eDSI.size() ) + " new property alignments.");

		printAlignments( newClassAlignments_eDSI, alignType.aligningClasses );
		printAlignments( newPropertyAlignments_eDSI, alignType.aligningProperties );

		
		// add any new mappings
		classesToBeFiltered.addAll( newClassAlignments_eDSI );
		propertiesToBeFiltered.addAll( newPropertyAlignments_eDSI );	
		
		//evaluate EFS
		Alignment<Mapping> set = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());
		set.addAll(newClassAlignments_eDSI);
		set.addAll(newPropertyAlignments_eDSI);
		if( referenceAlignmentMatcher != null ) {
			ReferenceEvaluationData EDSIdata = ReferenceEvaluator.compare(set, referenceAlignmentMatcher.getAlignment());
			EDSIcorrect += EDSIdata.getCorrect();
			EDSIwrong += (EDSIdata.getFound() - EDSIdata.getCorrect());
		}
		currentStage = executionStage.afterExtrapolatingMatchers;	
		
	}

	private void userValidation() {
		
		SelectionPanel progressDisplay = null;
		for( MatchingProgressListener mpd : progressDisplays )
			if( mpd instanceof SelectionPanel ) {
				progressDisplay = (SelectionPanel)mpd;
			}
		
		setStage( executionStage.runningUserInterface );
		//MANUAL: Display the mappings in the User Interface
		progressDisplay.displayMappings(topConceptsAndAlignments);
		userContinued = false;
		while(!userContinued && !stop) {
			// sleep while the user is using the interface
			try {
				Thread.sleep(500);  // sleep for .5 seconds at a time 
			} catch (InterruptedException e) {
				// our sleep was interrupted, so just continue
				//sLog.error(e, e);
				break;
			} 
		}
		userMapping = progressDisplay.getUserMapping();
		userConcept = progressDisplay.getUserConcept();
		userAction = progressDisplay.getUserAction();
		setStage( executionStage.afterUserInterface );
		
	}

	private void automaticValidation() {
		userMapping = null;
		userConcept = null;
		userAction= null;
		setStage(executionStage.runningAutomaticValidation);
		if(isInAutomaticMode()){  // AUTOMATIC USER VALIDATION: check with the reference if a mapping is correct
			//no more candidates --> we stop
			//validate mappings against the reference, only one mapping can be validated therefore we take the first correct
			for(int i=0; i < topConceptsAndAlignments.size(); i++){
				CandidateConcept c = topConceptsAndAlignments.get(i);
				ArrayList<Mapping> candidateMappings = c.getCandidateMappings();
				if(candidateMappings!= null){
					for(int j = 0; j < candidateMappings.size(); j++){
						Mapping a = candidateMappings.get(j);
						if(c.whichType.equals(alignType.aligningProperties)){
							// we are looking at a property alignment
							if(referenceAlignmentMatcher.getPropertyAlignmentSet().contains(a.getEntity1(), a.getEntity2()) != null){
								userMapping = a;
								userConcept = c;
								break;
							}
						}
						else{
							// we are looking at a class alignment
							if(referenceAlignmentMatcher.getClassAlignmentSet().contains(a.getEntity1(), a.getEntity2()) != null){
								userMapping = a;
								userConcept = c;
								break;
							}
							
						}
					}
				}
			}
			
			if( userMapping == null ) {
				//None of the candidate mapping is correct
				//we need to see if the all of any of the candidate concepts is not in the reference
				//3 cases:
				//a) All candidate concepts are not in the reference (filter them all)
				//b) at least one candidate concept is not in the reference (filter one)
				//c) all candidate concepts are in the reference, but the mappings are all wrong (filter all mappings)
				ArrayList<CandidateConcept> wrongConcepts = new ArrayList<CandidateConcept>();
				for(int i=0; i < topConceptsAndAlignments.size(); i++){
					CandidateConcept c = topConceptsAndAlignments.get(i);
					if(!isInReferenceAlignment(c)){
						wrongConcepts.add(c);
					}
				}
				if(wrongConcepts.size() == topConceptsAndAlignments.size()){
					userAction = SelectionPanel.A_ALL_CONCEPT_WRONG;
					System.out.println( "Automatic User Validation: all candidate concepts are not in the reference.");
				}
				else if(wrongConcepts.size() > 0){
					userAction = SelectionPanel.A_CONCEPT_WRONG;
					userConcept = wrongConcepts.get(0);
					System.out.println( "Automatic User Validation: wrong candidate concept: "+userConcept.toString());
				}
				else{//all candidate concepts are in the reference, but all of their candidate mappings are wrong
					userAction = SelectionPanel.A_ALL_MAPPING_WRONG;
					System.out.println( "Automatic User Validation: None of the mappings presented to the user were in the reference alignment.");
				}
			}
			else { //user selected a single mapping correct
				userAction = SelectionPanel.A_MAPPING_CORRECT;
				System.out.println( "Automatic User Validation: CORRECT mapping: " + 
						userMapping.getEntity1().toString() + " -> " +
						userMapping.getEntity2().toString() );
			}
			setStage(executionStage.afterAutomaticValidation);
		}
	}

	private void candidateSelection(boolean firstPhase) {
		SelectionPanel progressDisplay = null;
		for( MatchingProgressListener mpd : progressDisplays )
			if( mpd instanceof SelectionPanel ) {
				progressDisplay = (SelectionPanel)mpd;
			}
		FeedbackLoopParameters param = (FeedbackLoopParameters)this.param;
		setStage( executionStage.runningCandidateSelection );
		if(firstPhase){
			progressDisplay.appendNewLineReportText("Similarity-Based Candidates Selection:");
			SimilarityBasedCandidateSelection simBasedCandidateSelection = new SimilarityBasedCandidateSelection(this);
			topConceptsAndAlignments = simBasedCandidateSelection.getCandidateAlignments(param.K, param.M);
		}
		else{
			progressDisplay.appendNewLineReportText("Relevance-Based Candidates Selection:");
			//the candidateSelection is now initialized in the UFL constructor
			//because we want to keep track of the relevance evaluations to avoid computing them at each itearation
			//but candidateSelection variable is not the similarityBasedCandidateSelection because that one has to be computed at each iteration
			candidateSelection.runMeasures();
			topConceptsAndAlignments = candidateSelection.getCandidateAlignments( param.K, param.M);

		}

		
		//PRINTING
		System.out.println("Printing Candidate Mappings: ");
		progressDisplay.appendToReport("Printing Candidate Mappings: ");
		int numCandidateMappings = 0;//this value is not only used for printing
		for(int i= 0; i<topConceptsAndAlignments.size(); i++){
			CandidateConcept c = topConceptsAndAlignments.get(i);
			ArrayList<Mapping> candidateMappings = c.getCandidateMappings();
			if(candidateMappings!= null){
				numCandidateMappings += candidateMappings.size();
				for( int aCandidate = 0; aCandidate < candidateMappings.size(); aCandidate++ ) {
					Mapping candidateAlignment = candidateMappings.get(aCandidate);
					progressDisplay.appendToReport(" Candidate Concept: "+i+" "+c.getNode()+ ", Candidate Mapping: " + Integer.toString(aCandidate) + ". " + candidateAlignment.toString() );
					System.out.println( " Candidate Concept: "+i+" "+c.getNode()+ ", Candidate Mapping: " + Integer.toString(aCandidate) + ". " + candidateAlignment.toString() );	
				}
			}
		}
		progressDisplay.appendNewLineReportText("Found "+ numCandidateMappings + " candidate alignments.");
		System.out.println( "Candidate Selection: Found " + numCandidateMappings + " candidate alignments.");
		setStage( executionStage.afterCandidateSelection );
	}

	private void filtering() {
		setStage( executionStage.runningFilter );
		SelectionPanel progressDisplay = null;
		for( MatchingProgressListener mpd : progressDisplays )
			if( mpd instanceof SelectionPanel ) {
				progressDisplay = (SelectionPanel)mpd;
			}
		FilteredAlignmentMatrix classesMatrix = (FilteredAlignmentMatrix)this.classesMatrix;
		FilteredAlignmentMatrix propertiesMatrix = (FilteredAlignmentMatrix)this.propertiesMatrix;
		progressDisplay.appendNewLineReportText("Mappings filtering:");
		classesMatrix.validateAlignments(classesToBeFiltered);
		propertiesMatrix.validateAlignments(propertiesToBeFiltered);
		classesAlignmentSet.addAllNoDuplicate( classesToBeFiltered );
		propertiesAlignmentSet.addAllNoDuplicate( propertiesToBeFiltered);
		// double threshold filtering (dtf)
		//int classesBelowTh = classesMatrix.filterCellsBelowThreshold( param.lowThreshold );
		//int propertiesBelowTh = propertiesMatrix.filterCellsBelowThreshold( param.lowThreshold );
		//System.out.println( "Double Threshold Filtering: filtered " + Integer.toString(classesBelowTh) + " classes.");
		//System.out.println( "Double Threshold Filtering: filtered " + Integer.toString(propertiesBelowTh) + " properties.");
		//progressDisplay.appendNewLineReportText("\tFiltered "+Integer.toString(classesBelowTh) + " classes.");
		//progressDisplay.appendNewLineReportText("\tFiltered "+Integer.toString(propertiesBelowTh) + " properties.");
		classesToBeFiltered = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID()); // create a new, empty set
		propertiesToBeFiltered = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID()); // create a new, empty set
		setStage( executionStage.afterFilter );
	}

	private void iterationEnd() {
		if(isInAutomaticMode()){
			if(iteration % evaluationSpin == 0){//This is to print results every evaluationSpin iterations
				evaluate();
			}
		}
		new_mapping += (classesToBeFiltered.size() + propertiesToBeFiltered.size());
		System.out.println("Current iteration: "+iteration+" - Mappings found this iteration: "+
				(classesToBeFiltered.size() + propertiesToBeFiltered.size()) +
				"   Total new mappings found: " + Integer.toString(new_mapping)	);
		
	}
	
	private void evaluate(){
		FeedbackIteration partialIteration = new FeedbackIteration(iteration);
		Alignment<Mapping> partialSet = getAlignment();
		ReferenceEvaluationData partialRD = ReferenceEvaluator.compare(partialSet, referenceAlignmentMatcher.getAlignment());
		partialIteration.iteration = iteration;
		partialIteration.evaluationData = partialRD;
		partialIteration.EDSIcorrect = EDSIcorrect;
		partialIteration.EDSIwrong = EDSIwrong;
		partialIteration.EFScorrect = EFScorrect;
		partialIteration.EFSwrong = EFSwrong;
		partialEvaluations.add(partialIteration);
		System.out.println("Iteration: "+iteration);
		System.out.println( "///////////////////////////////////////////////////////////////");
		System.out.println( "Inital Matchers:"	);
		System.out.println( initialEvaluation.getReport() );
		System.out.println( "" );
		System.out.println( "Feedback Loop + Extrapolating Matchers: ");
		System.out.println( partialRD.getReport() );
		System.out.println("Improvement F-measure from initial: "+Utility.getOneDecimalPercentFromDouble(partialRD.getFmeasure()-initialEvaluation.getFmeasure()));
		System.out.println( "////////////////////////////////////////////////////////////////");
	}

	private void runInitialMatcher() throws Exception {
		FeedbackLoopParameters param = (FeedbackLoopParameters)this.param;
		SelectionPanel progressDisplay = null;
		for( MatchingProgressListener mpd : progressDisplays )
			if( mpd instanceof SelectionPanel ) {
				progressDisplay = (SelectionPanel)mpd;
			}
		//Initial Matcher is initialized in the SelectionPanel.getParameters()
		//we just have to make it run here
		setStage(executionStage.runningInitialMatchers);
		AbstractMatcher im = param.initialMatcher;
		im.getParam().threshold = ( param.initialMatchersThreshold );
		progressDisplay.appendNewLineReportText("Running the initial automatic matcher: "+im.getName());
		//parameters are set in the SelectionPanel.getParameters()
		//im.setProgressDisplay(progressDisplay);
		im.match();
		progressDisplay.appendNewLineReportText("The initial matcher completed the matching process successfully. ");
		progressDisplay.appendNewLineReportText("\tClasses alignments found: "+im.getClassAlignmentSet().size());
		progressDisplay.appendNewLineReportText("\tProperties alignments found: "+im.getPropertyAlignmentSet().size());
		classesMatrix = new FilteredAlignmentMatrix( im.getClassesMatrix() );
		propertiesMatrix = new FilteredAlignmentMatrix( im.getPropertiesMatrix() );
		classesAlignmentSet = im.getClassAlignmentSet();
		propertiesAlignmentSet = im.getPropertyAlignmentSet();
		classesToBeFiltered = classesAlignmentSet;
		propertiesToBeFiltered = propertiesAlignmentSet;
		if(isInAutomaticMode()){
			initialEvaluation = ReferenceEvaluator.compare(im.getAlignment(), referenceAlignmentMatcher.getAlignment());
		}
		setStage( executionStage.afterInitialMatchers );		
	}

	private void initAutomaticConfiguration() throws Exception {
		String conf = ((FeedbackLoopParameters)param).configuration;
		if(conf.equals(AUTO_101_301)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./OAEI09/benchmarks/301/refalign.rdf";
			refParam.format = ReferenceAlignmentMatcher.OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
			referenceAlignmentMatcher.match();
			
			
		}
		else if(conf.equals(AUTO_101_302)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./OAEI09/benchmarks/302/refalign.rdf";
			refParam.format = ReferenceAlignmentMatcher.OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);			
				referenceAlignmentMatcher.match();
		}
		else if(conf.equals(AUTO_101_303)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./OAEI09/benchmarks/303/refalign.rdf";
			refParam.format = ReferenceAlignmentMatcher.OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();

		}
		else if(conf.equals(AUTO_101_304)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./OAEI09/benchmarks/304/refalign.rdf";
			refParam.format = ReferenceAlignmentMatcher.OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();	
		}
		else if(conf.equals(AUTO_animals)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./I3CON2004/animals/animalsAB.n3.txt";
			refParam.format = ReferenceAlignmentMatcher.OLD_OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();
		}
		else if(conf.equals(AUTO_basketball_soccer)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./I3CON2004/basketball_soccer/basketball_soccer.n3.txt";
			refParam.format = ReferenceAlignmentMatcher.OLD_OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();
		}
		else if(conf.equals(AUTO_comsci)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./I3CON2004/comsci/csAB.n3.txt";
			refParam.format = ReferenceAlignmentMatcher.OLD_OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();
		}
		else if(conf.equals(AUTO_hotel)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./I3CON2004/hotel/hotelAB.n3.txt";
			refParam.format = ReferenceAlignmentMatcher.OLD_OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();
		}
		else if(conf.equals(AUTO_network)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./I3CON2004/network/networkAB.n3.txt";
			refParam.format = ReferenceAlignmentMatcher.OLD_OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();
		}
		else if(conf.equals(AUTO_people_pets)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./I3CON2004/people+pets/people+petsAB.n3.txt";
			refParam.format = ReferenceAlignmentMatcher.OLD_OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();
		}
		else if(conf.equals(AUTO_russia)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./I3CON2004/russia/russiaAB.n3.txt";
			refParam.format = ReferenceAlignmentMatcher.OLD_OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();
		}
		else if(conf.equals(AUTO_weapons)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./I3CON2004/Weapons/WeaponsAB.n3.txt";
			refParam.format = ReferenceAlignmentMatcher.OLD_OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();
		}
		else if(conf.equals(AUTO_wine)){
			System.out.println("Automatic User Validation:" + conf + ", Loading reference alignment.");
			ReferenceAlignmentParameters refParam = new ReferenceAlignmentParameters();
			refParam.onlyEquivalence = true;
			refParam.fileName = "./I3CON2004/Wine/WineAB.n3.txt";
			refParam.format = ReferenceAlignmentMatcher.OLD_OAEI;
			referenceAlignmentMatcher = MatcherFactory.getMatcherInstance(ReferenceAlignmentMatcher.class);
			referenceAlignmentMatcher.setParameters(refParam);
				referenceAlignmentMatcher.match();	
		}
		else{//MANUAL
			//isAutomaticMode() returns false because referenceAlignmentMatcher is null
			System.out.println("Running manually.");
		}
		
	}

	private void filterCandidateConcepts(ArrayList<CandidateConcept> toBeFiltered) {
		Iterator<CandidateConcept> it = toBeFiltered.iterator();
		FilteredAlignmentMatrix workingMatrix;
		CandidateConcept c;
		while(it.hasNext()){
			c = it.next();
			if(c.whichType.equals(alignType.aligningClasses)){
				workingMatrix = (FilteredAlignmentMatrix)classesMatrix;
			}
			else{
				workingMatrix = (FilteredAlignmentMatrix)propertiesMatrix;
			}
			workingMatrix.filterConcept(c);
		}
	}

	private void printAlignments(Alignment<Mapping> mappings, alignType atype) {
		
		Iterator<Mapping> iter = mappings.iterator();
		while( iter.hasNext() ) {
			Mapping itha = iter.next();
			
			if( referenceAlignmentMatcher != null ) {
				if( atype == alignType.aligningClasses ) {
					if( referenceAlignmentMatcher.getClassAlignmentSet().contains(itha)) {
						System.out.println( "\t% " + itha.toString() + " (correct!)");
					} else {
						System.out.println( "\t% " + itha.toString() + " (wrong!)");
					}
				} else {
					if( referenceAlignmentMatcher.getPropertyAlignmentSet().contains(itha)) {
						System.out.println( "\t% " + itha.toString() + " (correct!)");
					} else {
						System.out.println( "\t% " + itha.toString() + " (wrong!)");
					}

				}
			} else {
				System.out.println( "\t% " + itha.toString());
			}		
		}
		
	}
	
	public executionStage getStage() { return currentStage; }
	public boolean isStage( executionStage s ) { return s == currentStage; }
	public void setStage(executionStage stage) {
		if(!isStage(executionStage.presentFinalMappings) ){
			currentStage = stage;
			System.out.println(">> Changing execution stage to: " + currentStage.name() );
		}
	}
	
	
	public FilteredAlignmentMatrix getClassesMatrix() {
		return (FilteredAlignmentMatrix)classesMatrix;
	}

	public FilteredAlignmentMatrix getPropertiesMatrix() {
		return (FilteredAlignmentMatrix)propertiesMatrix;
	}
    
	
	
	private Alignment<Mapping> getNewClassAlignments( AbstractMatcher a ) {
		return getNewMappings( a.getClassAlignmentSet(), (FilteredAlignmentMatrix)classesMatrix );
	}
	
	private Alignment<Mapping> getNewPropertyAlignments( AbstractMatcher a ) {
		return getNewMappings( a.getPropertyAlignmentSet(), (FilteredAlignmentMatrix)propertiesMatrix );
	}

	// look through all the alignments found by the matcher and choose only those that are not already filtered, this way
	// we are choosing only the new alignments to be filtered
	private Alignment<Mapping> getNewMappings( Alignment<Mapping> extrapolatedMatchings, FilteredAlignmentMatrix matrix ) {
				
		Alignment<Mapping> newAlignments = new Alignment<Mapping>(sourceOntology.getID(), targetOntology.getID());	
		
		Iterator<Mapping> clsIter = extrapolatedMatchings.iterator();
		while( clsIter.hasNext() ) {
			Mapping extrapolatedAlignment = clsIter.next();
			if( !matrix.isCellFiltered( extrapolatedAlignment.getEntity1().getIndex(), extrapolatedAlignment.getEntity2().getIndex() ) ) {
				// the extrapolated alignment is a new alignment
				newAlignments.add( extrapolatedAlignment );
			}
		}
		return newAlignments;
	}
	
	public boolean isCandidateConceptValidated(CandidateConcept c){
		return isNodeValidated(c.getNode(), c.whichType, c.whichOntology);
	}
	
	public boolean isNodeValidated(Node n, alignType align, int ontType){
		FilteredAlignmentMatrix classesMatrix = (FilteredAlignmentMatrix)this.classesMatrix;
		FilteredAlignmentMatrix propertiesMatrix = (FilteredAlignmentMatrix)this.propertiesMatrix;
		
		if(align.equals(alignType.aligningClasses)){
			if(ontType == Ontology.SOURCE){
				return classesMatrix.isRowFiltered(n.getIndex());
			}
			else{
				return classesMatrix.isColFiltered(n.getIndex());
			}
		}
		else{
			if(ontType == Ontology.SOURCE){
				return propertiesMatrix.isRowFiltered(n.getIndex());
			}
			else{
				return propertiesMatrix.isColFiltered(n.getIndex());
			}
		}
	}

	
	public void setParam( FeedbackLoopParameters p ) {
		this.param = p;
	}
	
	// check to see if a Candidate concept is in the reference alignment
	public boolean isInReferenceAlignment( CandidateConcept nc ) {
		if( referenceAlignmentMatcher == null ) { return false; } // we cannot check the reference alignment if it not loaded
		Alignment<Mapping> cset = referenceAlignmentMatcher.getClassAlignmentSet();
		Alignment<Mapping> pset = referenceAlignmentMatcher.getPropertyAlignmentSet();
		
		if( nc.whichType == alignType.aligningClasses) {
			return cset.contains(nc.getNode(), nc.whichOntology) != null;
		} else {
			return pset.contains(nc.getNode(), nc.whichOntology) != null;
		}
		
	}
	
	public boolean isInAutomaticMode(){
		return referenceAlignmentMatcher != null;
	}

	public void stop() {
		stop = true;
	}
	
	public void userContinued(){
		userContinued = true;
	}

}
