package am.app.mappingEngine;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

import am.Utility;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;

public abstract class AbstractSelectionAlgorithm extends SwingWorker<Void,Void> implements SelectionAlgorithm {
	
	protected DefaultSelectionParameters params;
	protected SelectionResult result;
	
	// FIXME: Isn't this taken care of by SwingWorker? -- Cosmin.
	protected List<MatchingProgressListener> progressListeners = new ArrayList<MatchingProgressListener>();

    @Override
    public SelectionResult getResult() {
        return result;
    }
	
	/**
	 * Match(), buildSimilarityMatrix() and select() are the only 3 public methods to be accessed by the system other then get and set methods
	 * All other methods must be protected so that only subclasses may access them (can't be private because subclasses wouldn't be able to use them)
	 * match method is the one which perform the alignment. It also invokes the select() to scan and select matchings
	 * the system sometimes may need to invoke only the select method for example when the user changes threshold of an algorithm, it's not needed to invoke the whole matching process but only select
	 * so at least those two methods must be implemented and public
	 * both methods contains some empty methods to allow developers to add other code if needed
	 * In all cases a developer can override the whole match method or use this one and override the methods inside, or use all methods except for alignTwoNodes() which is the one which perform the real aligment evaluation
	 * and it has to be different
	 * It should not be needed often to override the select(), in all cases remember to consider all selection parameters threshold, num relations per source and target.
	 */
	@Override
	public void select() {
    	//this method is also invoked everytime the user change threshold or num relation in the table
    	beforeSelectionOperations();//Template method to allow next developer to add code after selection
    	selectAndSetAlignments();	
    	afterSelectionOperations();//Template method to allow next developer to add code after selection
    }

	@Override 
	protected Void doInBackground() throws Exception {
		select();
		return null;
	}
	
    //RESET ALIGNMENT STRUCTURES,     //TEMPLATE METHOD TO ALLOW DEVELOPERS TO ADD CODE: call super when overriding
    public void beforeSelectionOperations() {
    	result = new SelectionResult();
    	result.setClassAlignmentSet(null);
    	result.setPropertyAlignmentSet(null);
    	result.setInstanceAlignmentSet(null);
    	result.setQualEvaluation(null);
    	result.setRefEvaluation(null);
    	
    	for( MatchingProgressListener mpd : progressListeners ) {
    		mpd.appendToReport("Performing mapping selection ...");
    	}
    }
	
    //TEMPLATE METHOD TO ALLOW DEVELOPERS TO ADD CODE: call super when overriding
    protected void afterSelectionOperations() {
    	for( MatchingProgressListener mpd : progressListeners ) {
    		mpd.appendToReport(" Done.\n");
    	}
    } 
	
    protected void selectAndSetAlignments() {
    	if(params.alignClasses) {
    		if (params.inputResult == null)
    			throw new RuntimeException("Input result is NULL. Mapping selection cannot performed.");
    		Alignment<Mapping> classesAlignmentSet = scanMatrix(params.inputResult.getClassesMatrix());
    		result.setClassAlignmentSet(classesAlignmentSet);
    	}
    	if(params.alignProperties) {
    		Alignment<Mapping> propertiesAlignmentSet = scanMatrix(params.inputResult.getPropertiesMatrix());
    		result.setPropertyAlignmentSet(propertiesAlignmentSet);
    	}
	}
    
    protected Alignment<Mapping> scanMatrix(SimilarityMatrix matrix) {
    	if( matrix == null ) { // there is no matrix, return empty set
    		return new Alignment<Mapping>(params.inputResult.getSourceOntology().getID(), params.inputResult.getTargetOntology().getID());
    	}
    	int columns = matrix.getColumns();
    	int rows = matrix.getRows();
    	// at most each source can be aligned with all targets (columns) it's the same of selecting ANY for source
		int realSourceRelations = Math.min(params.maxSourceAlign, columns);
		// at most each target can be aligned with all sources (rows) it's the same of selecting ANY for target
		int realTargetRelations = Math.min(params.maxTargetAlign, rows);
		
		
		if(realSourceRelations == columns && realTargetRelations == rows) { //ANY TO ANY
			return getThemAll(matrix);
		}
		else if(realSourceRelations != columns && realTargetRelations == rows) { //N - ANY that includes also 1-ANY
			//AT LEAST ONE OF THE TWO CONSTRAINTs IS ANY, SO WE JUST HAVE TO PICK ENOUGH MAX VALUES TO SATISFY OTHER CONSTRAINT 
			return scanForMaxValuesRows(matrix, realSourceRelations);
		}
		else if( realSourceRelations == columns && realTargetRelations != rows) {//ANY-N that includes also ANY-1
			//AT LEAST ONE OF THE TWO CONSTRAINTs IS ANY, SO WE JUST HAVE TO PICK ENOUGH MAX VALUES TO SATISFY OTHER CONSTRAINT 
			return scanForMaxValuesColumns(matrix, realTargetRelations);
    	}
    	else {
			//Both constraints are different from ANY //all cases like 1-1 1-3 or 5-4 or 30-6
			if(realSourceRelations == 1 && realTargetRelations == 1) {//1-1 mapping
				//we can use the hungarian algorithm which provide the optimal solution in polynomial time
				return oneToOneMatching(matrix);
			}
			else { //all cases like 2-2 or 1-3 or 5-4 or 30-6
				//an extension of the stable marriage problem, this is not necesserly optimal but is already more than enough
				return scanWithBothConstraints(matrix, realSourceRelations,realTargetRelations);
			}
		}
	}
    
    protected abstract Alignment<Mapping> oneToOneMatching(SimilarityMatrix matrix);
    
    /**
     * Returns all mappings that have a similarity value >= the matcher threshold.
     * @param matrix Matrix to scan for mappings.
     * @return Alignment set of mappings. 
     */
    protected Alignment<Mapping> getThemAll(SimilarityMatrix matrix) {
		Alignment<Mapping> aset = new Alignment<Mapping>(params.inputResult.getSourceOntology().getID(), params.inputResult.getTargetOntology().getID());
		Mapping currentValue;
		for(int i = 0; i<matrix.getColumns();i++) {
			for(int j = 0; j<matrix.getRows();j++) {		
				currentValue = matrix.get(j,i);
				if(currentValue != null && currentValue.getSimilarity() >= params.threshold)
					aset.add(currentValue);
			}
		}
		return aset;
	}
    
   protected Alignment<Mapping> scanWithBothConstraints(SimilarityMatrix matrix, int sourceConstraint, int targetConstraint) {
    	
    	
    	IntDoublePair fakePair = IntDoublePair.createFakePair();
    	int rows = matrix.getRows();
    	int cols = matrix.getColumns();
    	
    	Alignment<Mapping> aset = new Alignment<Mapping>(params.inputResult.getSourceOntology().getID(), params.inputResult.getTargetOntology().getID());

    	//I need to build a copy of the similarity matrix to work on it, i just need the similarity values
    	//and i don't need values higher than threshold so i'll just set them as fake so they won't be selected
    	double[][] workingMatrix = new double[rows][cols];
    	double sim;
    	for(int i = 0; i < rows; i++) {
    		for(int j = 0; j < cols; j++) {
    			if( matrix.get(i,j) != null ) {
    				sim = matrix.get(i,j).getSimilarity();
    			} else {
    				sim = 0;
    			}
    			if(sim >= params.threshold)
    				workingMatrix[i][j] = sim;
    			else workingMatrix[i][j] = IntDoublePair.fake;
    		}
    	}
    	
    	//for each source (row) i need to find the SourceConstraint best values
    	//for each maxvalue i need to remember the similarity value and the index of the correspondent column
    	//we init it all to (-1,-1)
    	IntDoublePair[][] rowsMaxValues = new IntDoublePair[matrix.getRows()][sourceConstraint];
    	for(int i= 0; i < rows; i++) {
    		for(int j = 0 ; j < sourceConstraint; j++) {
    			rowsMaxValues[i][j] = fakePair;
    		}
    	}
    	
    	//for each target (column) i need to find the targetConstraint best values
    	//for each maxvalue i need to remember the similarity value and the index of the correspondent column
    	//we init it all to (-1,-1)
    	IntDoublePair[][] colsMaxValues = new IntDoublePair[matrix.getColumns()][targetConstraint];
    	for(int i= 0; i < cols; i++) {
    		for(int j = 0 ; j < targetConstraint; j++) {
    			colsMaxValues[i][j] = fakePair;
    		}
    	}
    	
    	IntDoublePair maxPairOfRow = null;
    	IntDoublePair prevMaxPairOfCol = null;
    	IntDoublePair newMaxPairOfCol = null;
    	
    	//we must continue until the situation is stable
    	//if is a 3-4 mapping it means that we can find at most three alignments for each source and 4 for each target, but not always we can find all
    	boolean somethingChanged = true;
    	while(somethingChanged) {
    		somethingChanged = false;
    		
        	for(int i = 0; i < rows; i++) {
        		
        		//if I haven't found all best alignments for this row
        		if(rowsMaxValues[i][0].isFake()) {
        			
        			//I need to get the max of this row, that is ok also for the column
        			// so the max of this row must be higher the the max previously selected for that column
        			//this do while ends if i find one or if I don't find any so all the cells are fake and the maximum selected is fake too
        			do {
        				//get the max value for this row and the associated column index
                		maxPairOfRow = Utility.getMaxOfRow(workingMatrix, i);

                		if(maxPairOfRow.isFake()) {
                			break; //all the value of these lines are fake
                		}
                		else {
                    		//the minimum of the best values for the column corrisponding to this max
                    		prevMaxPairOfCol = colsMaxValues[maxPairOfRow.index][0];
                    		
                			//and i have to set that matrix value to fake so that that row won't select again that value
                			workingMatrix[i][maxPairOfRow.index] = IntDoublePair.fake;
                		}
        			}
                    while(maxPairOfRow.value <= prevMaxPairOfCol.value);
        			
        			//I don't need the workingMatrix anymore
        			//workingMatrix = null;
        			
            		//if my value is higher than than the minimum of the best values for this column
            		//this value becomes one of the best values and the minimum one is discarded
        			//so if the previous while ended because of the while condition not the break one
        			if(!maxPairOfRow.isFake()) {
        			
            			somethingChanged = true;
            			
            			//this value will be one of the best for this column and row, i had to them and update order.
            			//prevMaxPairOfCol is not anymore one of the best values for this column
            			//i'll switch it with the new one, but i also have to remove it from the best values of his row putting a fake one in it.
            			//i also have to modify the matrix so that that row won't select that max again.
            			newMaxPairOfCol = new IntDoublePair(i,maxPairOfRow.value);
            			colsMaxValues[maxPairOfRow.index][0] = newMaxPairOfCol;
            			//reorder that array of best values of this column to have minimum at the beginning
            			//we have to move the first element to get the right position
            			Utility.adjustOrderPairArray(colsMaxValues[maxPairOfRow.index],0);
            			
            			//the max of this row found must be added to the best values for this row, and then order the array,
            			rowsMaxValues[i][0] = maxPairOfRow;
            			//we have to move the first element to get the right position
            			Utility.adjustOrderPairArray(rowsMaxValues[i],0);
            			
            			
            			if(!prevMaxPairOfCol.isFake()) {
                			//the prev best values has to be removed also from that row best values so i have to find it and set it to fake and reorder
            				for(int k = 0; k < rowsMaxValues[prevMaxPairOfCol.index].length; k++) {
            					if(rowsMaxValues[prevMaxPairOfCol.index][k].index == maxPairOfRow.index) {
            						rowsMaxValues[prevMaxPairOfCol.index][k] = fakePair;
                        			Utility.adjustOrderPairArray(rowsMaxValues[prevMaxPairOfCol.index], k);
            						break;
            					}
            				}
            			}
            		}
        		}
        	}
    	}
    	
    	/*FOR DEBUGGING
    	for(int i = 0; i < rows; i++) {
    		for(int j = 0; j < cols; j++) {
    			System.out.print(workingMatrix[i][j]+" ");
    		}
    		System.out.println("");
    	}
    	*/
    	
    	//now we have the alignments into rowMaxValues
    	IntDoublePair toBeAdded;
    	for(int i = 0; i < rows; i++) {
    		for(int j = 0; j < sourceConstraint; j++) {
    			toBeAdded = rowsMaxValues[i][j];
    			if(!toBeAdded.isFake()) {
        			aset.add(matrix.get(i,toBeAdded.index));
    			}
    		}
    	}
    	
  
    	return aset;
    }
    
   
   
   protected Alignment<Mapping> scanForMaxValuesRows(SimilarityMatrix matrix, int numMaxValues) {
		Alignment<Mapping> aset = new Alignment<Mapping>(params.inputResult.getSourceOntology().getID(), params.inputResult.getTargetOntology().getID());
		Mapping toBeAdded;
		//temp structure to keep the first numMaxValues best alignments for each source
		//when maxRelations are both ANY we could have this structure too big that's why we have checked this case in the previous method
		Mapping[] maxAlignments;
		for(int i = 0; i<matrix.getRows();i++) {
			maxAlignments = matrix.getRowMaxValues(i, numMaxValues);
			//get only the alignments over the threshold
			for(int e = 0;e < maxAlignments.length; e++) { 
				toBeAdded = maxAlignments[e];
				if(toBeAdded != null && toBeAdded.getSimilarity() >= params.threshold) {
					aset.add(toBeAdded);
				}
			}
		}
		return aset;
	}
   
   protected Alignment<Mapping> scanForMaxValuesColumns(SimilarityMatrix matrix, int numMaxValues) {
	   Alignment<Mapping> aset = new Alignment<Mapping>(params.inputResult.getSourceOntology().getID(), params.inputResult.getTargetOntology().getID());
	   Mapping toBeAdded;
	   //temp structure to keep the first numMaxValues best alignments for each source
	   //when maxRelations are both ANY we could have this structure too big that's why we have checked this case in the previous method
	   Mapping[] maxAlignments;
	   for(int i = 0; i<matrix.getColumns();i++) {
		   maxAlignments = matrix.getColMaxValues(i, numMaxValues);
		   //get only the alignments over the threshold
		   for(int e = 0;e < maxAlignments.length; e++) { 
			   toBeAdded = maxAlignments[e];
			   if(toBeAdded != null && toBeAdded.getSimilarity() >= params.threshold) {
				   aset.add(toBeAdded);
			   }
		   }
	   }
	   return aset;
   }
   
   @Override
   public void setParameters(DefaultSelectionParameters param) {
		this.params = param;
   }
   
   
}
