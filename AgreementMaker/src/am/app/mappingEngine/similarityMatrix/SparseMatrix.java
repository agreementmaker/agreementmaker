package am.app.mappingEngine.similarityMatrix;

import java.io.FileWriter;
import java.util.List;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;


// TODO: Keep track of matrix bounds.

public class SparseMatrix extends SimilarityMatrix
{
	private RowCell<Mapping> head;// LinkedList<LinkedList<RowCell>> rows;
	
	//private List matrixList;
	
	
	
	public SparseMatrix()
	{
		//rows=new LinkedList<LinkedList<RowCell>>();
		head=new RowCell<Mapping>(-1,-1,null);//create the head of the LL
	}
	@Override
	public void set(int row, int column, Mapping obj)
	{
		//System.out.println(rows.size());
		if(head.nextr == null)//check if the head has any rows in it
		{
			RowCell<Mapping> newRowCell = new RowCell<Mapping>(row,column,obj);
			head.nextr = newRowCell; //if there are no rows then insert a new RowCell object
			newRowCell.prevr=head;//link the head to the new rowCell
		}
		else//if there are rows in the matrix then we need to check the row value and insert the new RowCell object
		{
			RowCell<Mapping> currentCell = head.nextr;//create an object that will be used to iterate through the LL starting with head
			RowCell<Mapping> newCell = new RowCell<Mapping>(row,column,obj);//create a new RowCell object
			boolean notDone=true;
			while(currentCell!=null && notDone)
			{
				if( row < currentCell.row) //insert the new rowcell here if i<the current row
				{
					newCell.nextr=currentCell;
					newCell.prevr=currentCell.prevr;
					if(newCell.prevr!=null)
						newCell.prevr.nextr=newCell; 
					currentCell.prevr=newCell;
					//currentCell.nextr=null;
					
					//notDone=false;
					break;
				}
				else if(row == currentCell.row)//if the rows are equal then we need to start looking at col to insert
				{
					//add to collums here
					while(currentCell != null)
					{
						//System.out.println(1);
						if(column < currentCell.col)//same as i<currentCell.row
						{
							newCell.nextc=currentCell;
							newCell.prevc=currentCell.prevc;
							if(newCell.prevc!=null) {
								newCell.prevc.nextc=newCell;
							} else {
								// we are replacing a row header
								newCell.prevr = currentCell.prevr;
								newCell.nextr = currentCell.nextr;
								
								currentCell.prevr = null;
								currentCell.nextr = null;
								
								if( newCell.prevr != null ) newCell.prevr.nextr = newCell;
								if( newCell.nextr != null ) newCell.nextr.prevr = newCell;
							}
							currentCell.prevc=newCell;
							//currentCell.nextc=null;
							
							
							
							notDone=false;
							break;
						}
						else if( currentCell.nextc==null )//if the next col is null then we know that newCell is 
						{//the largest col so it gets stuck at the end
							//System.out.println(3);
							currentCell.nextc=newCell;
							newCell.prevc=currentCell;
							notDone=false;
							break;
						}
						currentCell=currentCell.nextc;
					}
					
					//break;
				}
				else if(currentCell.nextr==null && notDone)//if i is the biggest number stick it on the end
				{
					currentCell.nextr=newCell;
					newCell.prevr=currentCell;
					//notDone=false;
					break;
				}
				currentCell=currentCell.nextr;//grab the next row in case it needs to
			}
		}
	}
	
	
	public Mapping get(int i, int j)
	{
		boolean done=false;
		RowCell<Mapping> currentCell=head.nextr;
		while(!done)
		{
			if(currentCell==null)
				done=true;
			else
			{
				if(currentCell.row==i)
				{
					while(!done)
					{
						if(currentCell.col==j)
							return currentCell.ob;
						else if(currentCell.nextc!=null)
							currentCell=currentCell.nextc;
						else
							done=true;
					}
				}
				else if(currentCell.nextr!=null)
					currentCell=currentCell.nextr;
				else
					done=true;
			}
		}
		return null;
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String print = new String();
		
		RowCell<Mapping> currentRow = head.nextr;
		while( currentRow != null ) {
			print += currentRow.row + ". ";
			RowCell<Mapping> currentCol = currentRow;
			while(currentCol != null) {
				print += currentCol.col + ", ";
				currentCol = currentCol.nextc;
			}
			print += ".\n";
			currentRow = currentRow.nextr;
		}
		
		return print;

	}
	@Override
	public List<Mapping> chooseBestN() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList,
			List<Integer> colsIncludedList, boolean considerThreshold,
			double threshold) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Mapping> chooseBestN(boolean considerThreshold, double threshold) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList,
			List<Integer> colsIncludedList) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SimilarityMatrix clone() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void fillMatrix(double d, List<Node> sourceList,
			List<Node> targetList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public alignType getAlignType() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Mapping[] getColMaxValues(int col, int numMaxValues) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public double getColSum(int col) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getColumns() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double[][] getCopiedSimilarityMatrix() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public double getMaxValue() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Mapping[] getRowMaxValues(int row, int numMaxValues) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public double getRowSum(int row) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getRows() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double getSimilarity(int i, int j) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Mapping[] getTopK(int k) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Mapping[] getTopK(int k, boolean[][] filteredCells) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void initFromNodeList(List<Node> sourceList, List<Node> targetList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setSimilarity(int i, int j, double d) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public SimilarityMatrix toArraySimilarityMatrix() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Mapping> toMappingArray() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Mapping> toMappingArray(FileWriter fw, int round) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Double> toSimilarityArray(List<Mapping> mapsArray) {
		// TODO Auto-generated method stub
		return null;
	}


}