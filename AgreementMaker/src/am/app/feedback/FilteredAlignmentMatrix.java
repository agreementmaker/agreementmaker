package am.app.feedback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import am.app.Core;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.AlignmentSet;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;


// TODO: This FilteredAlignmentMatrix only works for 1-1 alignments.  To extend this, there requires some work.
public class FilteredAlignmentMatrix extends AlignmentMatrix {
	// TODO: Add intializeVariables() method for the constructors to use (added to AlignmentMatrix)

	protected TreeSet<Integer> filteredRows;  // a sorted list (TreeSet) of the row numbers that have been filtered from this matrix
	protected TreeSet<Integer> filteredCols;  // a TreeSet of the column numbers that have been filtered from this matrix
	
	private boolean[][] isFiltered;
	
	public FilteredAlignmentMatrix( AlignmentMatrix am_new ) {
		super( am_new );

		filteredRows = new TreeSet<Integer>();
		filteredCols = new TreeSet<Integer>();
		
		int numRows = am_new.getRows();
		int numCols = am_new.getColumns();

		// matrix of filtered cells
		isFiltered = new boolean[numRows][numCols];
		
		// initialize the isFiltered matrix to false (at the beginning, nothing is filtered)
		for( int i = 0; i < numRows; i++ ) {
			for( int j = 0; j < numCols; j++ ) {
				isFiltered[i][j] = false;
			}
		}
		
		
	}
	
	
	public FilteredAlignmentMatrix(int numRows, int numCols, alignType typeOfNodes, String relation) {
		super( numRows, numCols, typeOfNodes, relation);
		
		// initialize list of filtered rows and columns
		filteredRows = new TreeSet<Integer>();
		filteredCols = new TreeSet<Integer>();
		
		
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
		return filteredRows.contains( new Integer(row) );
	}
	
	public boolean isColFiltered( int col ) {
		return filteredCols.contains( new Integer(col) );
	}
	
	public boolean isCellFiltered( int row, int col ) {
		return isFiltered[row][col];		
	}
	
	public TreeSet<Integer> getFilteredRows() {
		return filteredRows;
	}
	public TreeSet<Integer> getFilteredCols() {
		return filteredCols;
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
				setSimilarity(row, j, 0.00d);
				isFiltered[row][j] = true;
			}
			filteredRows.add(row);
			
			// zero out the column.
			for( int h = 0; h < rows; h++ ) {
				if( h == row ) continue; // do not zero out if it is the row of the alignment
				setSimilarity( h, col, 0.00d);
				isFiltered[h][col] = true;
			}
			filteredCols.add(col);
			
			
			// set the alignment similarity to 1
			setSimilarity(row, col, 1.00d);
			isFiltered[row][col] = true;
		}
	}


	// Copies the filtered rows/columns from another matrix
	// TODO: Only works for 1-1 alignments.
	public void validateAlignments(FilteredAlignmentMatrix fam) {
		
		// copy rows
		TreeSet<Integer> frows = fam.getFilteredRows();
		Iterator<Integer> iRows = frows.iterator();
		while( iRows.hasNext() ) {
			Integer i = iRows.next();
			if( !filteredRows.contains(i) ) filteredRows.add(i);
			for( int j = 0; j < fam.getColumns(); j++ ) {
				data[i][j] = fam.get(i, j);
			}
		}
		
		// copy columns
		TreeSet<Integer> fcols = fam.getFilteredCols();
		Iterator<Integer> iCols = fcols.iterator();
		while( iCols.hasNext() ) {
			Integer i = iCols.next();
			if( !filteredCols.contains(i) ) filteredCols.add(i);
			for( int h = 0; h < fam.getRows(); h++ ) {
				data[h][i] = fam.get(h, i);
			}
		}
	}
	
	
	// made this method work with the filtered matrix
    public Alignment[] getRowMaxValues(int row, int numMaxValues) {
    	
    	if( isRowFiltered(row) ) { return null; } // this row is filtered
    	
		//remember to check to have numMaxValues lower than matrix columns before
    	Alignment[] maxAlignments = new Alignment[numMaxValues];
    	
    	Node entity1 = null;
    	Node entity2 = null;
		for(int h = 0; h<maxAlignments.length;h++) {
			Alignment currentAlignment = new Alignment(-1);
			
			//
			
			switch ( typeOfMatrix ) {			
			case aligningClasses:
				entity1 = Core.getInstance().getSourceOntology().getClassesList().get(row);
				break;
			case aligningProperties:
				entity1 = Core.getInstance().getSourceOntology().getPropertiesList().get(row);
				break;
			}
			currentAlignment.setEntity1( entity1 );
			
			maxAlignments[h] = currentAlignment; //intial max alignments have sim equals to -1, don't put 0 could create problem in the next for
		}
		
		Alignment currentValue;
		Alignment currentMax;
		for(int j = 0; j<getColumns();j++) {
			if(!isCellFiltered(row, j)){
				switch( typeOfMatrix ) {
				case aligningClasses:
					entity2 = Core.getInstance().getTargetOntology().getClassesList().get(j);
					break;
				case aligningProperties:
					entity2 = Core.getInstance().getTargetOntology().getPropertiesList().get(j);
					break;
				}
				currentValue = get(row,j);
				currentValue.setEntity1( entity1 );
				currentValue.setEntity2( entity2 );
				if( currentValue == null ) continue;
				//maxAlignments contains the ordered list of max alignments, the first is the best max value
				for(int k = 0;k<maxAlignments.length; k++) {
					currentMax = maxAlignments[k];
					if(currentValue.getSimilarity() >= currentMax.getSimilarity()) { //if so switch the new value with the one in array and then i have to continue scanning the array to put in the switched value	
						
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
    	
    	Node entity1 = null;
    	Node entity2 = null;

    	
		for(int h = 0; h<maxAlignments.length;h++) {
			Alignment currentAlignment = new Alignment(-1); //intial max alignments have sim equals to -1
			
			switch( typeOfMatrix ) {
			case aligningClasses:
				entity2 = Core.getInstance().getTargetOntology().getClassesList().get(col);
				break;
			case aligningProperties:
				entity2 = Core.getInstance().getTargetOntology().getPropertiesList().get(col);
				break;
			}
			

			currentAlignment.setEntity1( entity1 );
			
			maxAlignments[h] = currentAlignment;
		}
		
		Alignment currentValue;
		Alignment currentMax;
		for(int j = 0; j<getRows();j++) {
			if(!isCellFiltered(j, col)){
				switch ( typeOfMatrix ) {			
				case aligningClasses:
					entity1 = Core.getInstance().getSourceOntology().getClassesList().get(j);
					break;
				case aligningProperties:
					entity1 = Core.getInstance().getSourceOntology().getPropertiesList().get(j);
					break;
				}
							
				currentValue = get(j, col);
				currentValue.setEntity1( entity1 );
				currentValue.setEntity2( entity2 );
				
				if( currentValue == null ) continue;
				//maxAlignments contains the ordered list of max alignments, the first is the best max value
				for(int k = 0;k<maxAlignments.length; k++) {
					currentMax = maxAlignments[k];
					if(currentValue.getSimilarity() >= currentMax.getSimilarity()) { //if so switch the new value with the one in array and then i have to continue scanning the array to put in the switched value
						maxAlignments[k] = currentValue;
						currentValue = currentMax;
					}
				}
			}
		}

		return maxAlignments;
	}


	public void filterCells(AlignmentSet<Alignment> topAlignments) {
		
		for(int i=0; i<topAlignments.size(); i++){
			Alignment a = topAlignments.getAlignment(i);
			
			int row = a.getSourceKey();
			int col = a.getTargetKey();
			
			data[row][col].setSimilarity(0);
			isFiltered[row][col] = true;
			
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
		
		if( data[row][col] != null ) {
			data[row][col].setSimilarity(0.0d);
		}
	}
    
    
	
}
