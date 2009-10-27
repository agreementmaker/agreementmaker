package am.app.feedback;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.AlignmentSet;


/**
 * This class implements a filter that filters out mappings from the mapping space.  
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

public class FilterSelectedMappings {

	
	public FilterSelectedMappings() {
	}
	
	
	public void runFilter( InitialMatcher im ) {
		
		
		// run one time for the Classes similarity matrix  ...
		AlignmentMatrix cam = im.getClassesAlignmentMatrix(); // get the classes
		AlignmentSet<Alignment> cset = im.getClassesAlignments();
		
		runFilter( cam, cset);
		
		
		//  ... and another time for the properties similarity matrix
		
		AlignmentMatrix pam = im.getPropertiesAlignmentMatrix();
		AlignmentSet<Alignment> pset = im.getPropertiesAlignments();
		
		runFilter( pam, pset);
		
		
			
	}
	
	
	/**
	 * This function will run the zeroing out of the rows and columns on the simMatrix for the given selectedAlignments
	 * @param simMatrix	- the similarity matrix where rows/cols will be zeroed out
	 * @param selectedAlignments - the mappings which will be in the final alignment (sim will be set to 1.0)
	 */
	private void runFilter( AlignmentMatrix simMatrix, AlignmentSet<Alignment> selectedAlignments ) {
		int numRows = simMatrix.getRows(); // number of rows
		int numCols = simMatrix.getColumns(); // number of columns in the alignment matrix
		
		// for every alignment that was selected, zero out the row and the columns, and set the alignment similarity to 1
		for( int i = 0; i < selectedAlignments.size(); i++ ) {
			
			Alignment currentAlignment = selectedAlignments.getAlignment(i);
			
			int row = currentAlignment.getSourceKey();
			int col = currentAlignment.getTargetKey();
			
			// zero out the row.
			for( int j = 0; j < numCols; j++ ) {
				if( j == col ) continue;  // do no zero out if it is the column of the alignment
				simMatrix.setSimilarity(row, j, 0.00d);
			}
			
			// zero out the column.
			for( int h = 0; h < numRows; h++ ) {
				if( h == row ) continue; // do not zero out if it is the row of the alignment
				simMatrix.setSimilarity( h, col, 0.00d);
			}
			
			
			// set the alignment similarity to 1
			simMatrix.setSimilarity(row, col, 1.00d);
		}
	}
	
	
	
}
