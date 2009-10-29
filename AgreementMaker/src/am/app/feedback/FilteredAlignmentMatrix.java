package am.app.feedback;

import java.util.TreeSet;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.AlignmentSet;

public class FilteredAlignmentMatrix extends AlignmentMatrix {

	protected TreeSet<Integer> filteredRows;  // a sorted list (TreeSet) of the row numbers that have been filtered from this matrix
	protected TreeSet<Integer> filteredCols;  // a TreeSet of the column numbers that have been filtered from this matrix
	
	public FilteredAlignmentMatrix( AlignmentMatrix am_new ) {
		super( am_new );

		// initialize list of filtered rows and columns
		filteredRows = new TreeSet<Integer>();
		filteredCols = new TreeSet<Integer>();
		
	}
	
	
	public boolean isRowFiltered( int row ) {
		return filteredRows.contains( new Integer(row) );
	}
	
	public boolean isColFiltered( int col ) {
		return filteredCols.contains( new Integer(col) );
	}
	
	public boolean isCellFiltered( int row, int col ) {
		return filteredRows.contains( new Integer(row)) || filteredCols.contains( new Integer(col));
	}

	
	/**
	 * This function will run the zeroing out of the rows and columns on the matrix for the given selectedAlignments
	 * @param simMatrix	- the similarity matrix where rows/cols will be zeroed out
	 * @param selectedAlignments - the mappings which will be in the final alignment (sim will be set to 1.0)
	 */
	
	public void filter( AlignmentSet<Alignment> selectedAlignments ) throws IndexOutOfBoundsException {
		
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
			}
			filteredRows.add(row);
			
			// zero out the column.
			for( int h = 0; h < rows; h++ ) {
				if( h == row ) continue; // do not zero out if it is the row of the alignment
				setSimilarity( h, col, 0.00d);
			}
			filteredCols.add(col);
			
			
			// set the alignment similarity to 1
			setSimilarity(row, col, 1.00d);
		}
	}
	
}
