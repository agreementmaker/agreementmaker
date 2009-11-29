package am.app.feedback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import am.app.Core;
import am.app.feedback.CandidateConcept.ontology;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.AlignmentSet;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;


// TODO: This FilteredAlignmentMatrix only works for 1-1 alignments.  To extend this, there requires some work.
public class FilteredAlignmentMatrix extends AlignmentMatrix {
	// TODO: Add intializeVariables() method for the constructors to use (added to AlignmentMatrix)
	

	
	
	//The treeSet filteredRows and filteredColumns has been replaced with this arrays
	//each value of the array keeps the number of cells of the correspondent row (column) that have been filtered
	//if this number is equals to the total number of columns (rows) than it means that the row (column) is filtered completely
	//we need this type of structure because when we filter cells singularly, we may indirectly filter an entire row (column)
	//this way both filtering and checking for filtering costs O(1)
	public int[] numCellsFilteredPerRow;
	public int[] numCellsFilteredPerColumn;
	private boolean[][] isFiltered;
	
	public FilteredAlignmentMatrix( AlignmentMatrix am_new ) {
		super( am_new );


		
		int numRows = am_new.getRows();
		int numCols = am_new.getColumns();
		numCellsFilteredPerRow = new int[numRows];//all 0 initially
		numCellsFilteredPerColumn = new int[numCols];//all 0 initially
		
		// matrix of filtered cells
		isFiltered = new boolean[numRows][numCols];
		
		// initialize the isFiltered matrix to false (at the beginning, nothing is filtered)
		for( int i = 0; i < numRows; i++ ) {
			for( int j = 0; j < numCols; j++ ) {
				isFiltered[i][j] = false;
			}
		}
	}
	
	//Cloning constructur
	public FilteredAlignmentMatrix( FilteredAlignmentMatrix am_new ) {
		super( am_new );

		int numRows = am_new.getRows();
		int numCols = am_new.getColumns();
		numCellsFilteredPerRow = new int[numRows];//all 0 initially
		numCellsFilteredPerColumn = new int[numCols];//all 0 initially
		// matrix of filtered cells
		isFiltered = new boolean[numRows][numCols];
		// initialize the isFiltered matrix to false (at the beginning, nothing is filtered)
		for( int i = 0; i < numRows; i++ ) {
			numCellsFilteredPerRow[i] = am_new.numCellsFilteredPerRow[i];
			for( int j = 0; j < numCols; j++ ) {
				isFiltered[i][j] = am_new.isFiltered[i][j];
				if(i == 0){
					numCellsFilteredPerColumn[j] = am_new.numCellsFilteredPerColumn[j];
				}
			}
		}
	}
	
	
	public FilteredAlignmentMatrix(int numRows, int numCols, alignType typeOfNodes, String relation) {
		super( numRows, numCols, typeOfNodes, relation);
		
		// initialize list of filtered rows and columns
		numCellsFilteredPerRow = new int[numRows];//all 0 initially
		numCellsFilteredPerColumn = new int[numCols];//all 0 initially
		
		
		// matrix of filtered cells
		isFiltered = new boolean[numRows][numCols];
		
		// initialize the isFiltered matrix to false (at the beginning, nothing is filtered)
		for( int i = 0; i < numRows; i++ ) {
			for( int j = 0; j < numCols; j++ ) {
				isFiltered[i][j] = false;
			}
		}
		
	}


	public boolean isRowFiltered( int row ) {
		return numCellsFilteredPerRow[row] == getColumns(); 
	}
	
	public boolean isColFiltered( int col ) {
		return numCellsFilteredPerColumn[col] == getRows(); 
	}
	
	public boolean isCellFiltered( int row, int col ) {
		return isFiltered[row][col];		
	}

	
	/**
	 * This function will run the zeroing out of the rows and columns on the matrix for the given selectedAlignments
	 * @param simMatrix	- the similarity matrix where rows/cols will be zeroed out
	 * @param selectedAlignments - the mappings which will be in the final alignment (sim will be set to 1.0)
	 */
	
	public void validateAlignments( AlignmentSet<Alignment> selectedAlignments ) throws IndexOutOfBoundsException {
		
		// for every alignment that was selected, zero out the row and the columns, and set the alignment similarity to 1
		for( int i = 0; i < selectedAlignments.size(); i++ ) {
			
			Alignment currentAlignment = selectedAlignments.getAlignment(i);
			
			int row = currentAlignment.getSourceKey();
			int col = currentAlignment.getTargetKey();
			
			if( row < 0 || row > rows ) {
				throw new IndexOutOfBoundsException("Row Index is " + Integer.toString(row));
			}
			if( col < 0 || col > columns ) {
				throw new IndexOutOfBoundsException("Column Index is " + Integer.toString(col));
			}
			
			// zero out the row.
			for( int j = 0; j < columns; j++ ) {
				if( j == col ) continue;  // do no zero out if it is the column of the alignment
				filterCell(row, j);
			}
			
			// zero out the column.
			for( int h = 0; h < rows; h++ ) {
				if( h == row ) continue; // do not zero out if it is the row of the alignment
				filterCell(h, col);
			}
			
			// set the alignment similarity to 1
			//and validate the cell
			setSimilarity(row, col, 1.00d);
			isFiltered[row][col] = true;
			numCellsFilteredPerColumn[col]++;
			numCellsFilteredPerRow[row]++;
		}
	}
	
	
	// made this method work with the filtered matrix
    public Alignment[] getRowMaxValues(int row, int numMaxValues) {
    	
    	if( isRowFiltered(row) ) { return null; } // this row is filtered
    	
		//remember to check to have numMaxValues lower than matrix columns before
    	Alignment[] maxAlignments = new Alignment[numMaxValues];

    	/*
    	Node entity1 = null;
    	Node entity2 = null;
		for(int h = 0; h<maxAlignments.length;h++) {	
			maxAlignments[h] = new Alignment(-1); //intial max alignments have sim equals to -1, don't put 0 could create problem in the next for
		}
		
		*/
		Alignment currentValue;
		Alignment currentMax;
		for(int j = 0; j<getColumns();j++) {
			if(!isCellFiltered(row, j)){
				currentValue = get(row,j);
				if( currentValue == null ) continue;
				//maxAlignments contains the ordered list of max alignments, the first is the best max value
				for(int k = 0;k<maxAlignments.length; k++) {
					currentMax = maxAlignments[k];
					if(currentMax == null) {
						if(k>0 && currentValue.equals(maxAlignments[k-1]))
							break;
						else{
							maxAlignments[k] = currentValue;
						}
					} else if(currentValue.getSimilarity() >= currentMax.getSimilarity()) { //if so switch the new value with the one in array and then i have to continue scanning the array to put in the switched value							
						maxAlignments[k] = currentValue;
						currentValue = currentMax;
					}
				}
			}
		}

		return maxAlignments;
	}
    
    
    // made this method work with the filtered matrix
	public Alignment[] getColMaxValues(int col, int numMaxValues) {
		
		if( isColFiltered(col) ) return null; // this column is filtered
		
		//remember to check to have numMaxValues lower than matrix rows before
    	Alignment[] maxAlignments = new Alignment[numMaxValues];
    
/*    	
		for(int h = 0; h<maxAlignments.length;h++) {			
			maxAlignments[h] = new Alignment(-1);
		}
*/		
		Alignment currentValue;
		Alignment currentMax;
		for(int j = 0; j<getRows();j++) {
			if(!isCellFiltered(j, col)){
				currentValue = get(j, col);
				
				if( currentValue == null ) continue;
				//maxAlignments contains the ordered list of max alignments, the first is the best max value
				for(int k = 0;k<maxAlignments.length; k++) {
					currentMax = maxAlignments[k];
					if(currentMax == null) {
						maxAlignments[k] = currentValue;
					} else if( currentValue.getSimilarity() >= currentMax.getSimilarity()) { //if so switch the new value with the one in array and then i have to continue scanning the array to put in the switched value)
						maxAlignments[k] = currentValue;
						currentValue = currentMax;
					}
				}
			}
		}

		return maxAlignments;
	}
	
	// return a copy of the matrix
	public Object clone(){
		
		FilteredAlignmentMatrix matrix = new FilteredAlignmentMatrix(this);
		return matrix;
	}


	public void filterCells(AlignmentSet<Alignment> topAlignments) {
		
		for(int i=0; i<topAlignments.size(); i++){
			Alignment a = topAlignments.getAlignment(i);
			
			int row = a.getSourceKey();
			int col = a.getTargetKey();
			filterCell(row, col);
		}
		
	}
	
	public void filterCell( int row, int col ) throws IndexOutOfBoundsException {
		
		if( row < 0 || row > rows ) {
			throw new IndexOutOfBoundsException("Row Index is " + Integer.toString(row));
		}
		if( col < 0 || col > columns ) {
			throw new IndexOutOfBoundsException("Column Index is " + Integer.toString(col));
		}
	
		isFiltered[row][col] = true;
		numCellsFilteredPerColumn[col]++;
		numCellsFilteredPerRow[row]++;
		if( data[row][col] != null ) {
			data[row][col].setSimilarity(0.0d);
		}
	}


	public int filterCellsBelowThreshold(double lowThreshold) {
		
		int numRows = getRows();
		int numCols = getColumns();
		
		int numCells = 0;
		
		for( int row = 0; row < numRows; row++ ) {
			for( int col = 0; col < numCols; col++ ) {
				if( getSimilarity(row, col) < lowThreshold ) {
					filterCell(row, col);
					numCells++;
				}
			}
		}
		return numCells;
		
	}


	public double getRowMinValue_notZero(int row) {
		double min = 1.0d;
		for( int col = 0; col < getColumns(); col++ ) {
			if( getSimilarity(row, col) < min && getSimilarity(row, col) != 0.0d ) min = getSimilarity(row, col);
		}
		return min;
	}
    
	public double getColMinValue_notZero(int col) {
		double min = 1.0d;
		for( int row = 0; row < getRows(); row++ ) {
			if( getSimilarity(row, col) < min && getSimilarity(row, col) != 0.0d ) min = getSimilarity(row, col);
		}
		return min;
	}
	
	
	public int getFrequency( double sim ) {
		int occurs = 0;
		for( int row = 0; row < getRows(); row++ ) {
			for( int col = 0; col < getColumns(); col++ ) {
				if( getSimilarity(row, col) == sim ) occurs++;
			}
		}
		return occurs;
	}


	public void filterConcept(CandidateConcept c) {
		if(c.whichOntology.equals(ontology.source)){
			filterRow(c.getIndex());
		}
		else{
			filterCol(c.getIndex());
		}
	}


	private void filterCol(int col) {
		for(int i = 0; i < getRows(); i++){
			filterCell(i, col);
		}
		
	}


	private void filterRow(int row) {
		for(int j = 0; j < getColumns(); j++){
			filterCell(row, j);
		}
	}

	
}
