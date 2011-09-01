package am.app.mappingEngine.similarityMatrix;

import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;
import am.app.ontology.Ontology;


// TODO: Keep track of matrix bounds.

public class SparseMatrix extends SimilarityMatrix
{
	private RowCell<Mapping> head;// LinkedList<LinkedList<RowCell>> rows;
	private int rows;
	//private List matrixList;
	
	
	
	public SparseMatrix()
	{
		//rows=new LinkedList<LinkedList<RowCell>>();
		head=new RowCell<Mapping>(-1,-1,null);//create the head of the LL
		rows=0;
	}
	public SparseMatrix(SparseMatrix cloneme){
		this();
		relation = cloneme.getRelation();
    	typeOfMatrix = cloneme.getAlignType();
    	
    	sourceOntologyID = cloneme.getSourceOntologyID();
    	targetOntologyID = cloneme.getTargetOntologyID();
    	
    	RowCell<Mapping> currentCol, currentRow;//these RowCells are the originals
    	
    	currentRow=cloneme.getHead().nextr;//get the first row
    	
    	while(currentRow!=null){
    		//clone stuff here
    		this.set(currentRow.row, currentRow.col, currentRow.ob);
    		currentCol=currentRow.nextc;
    		
    		while(currentCol!=null){
    			//clone stuff here
    			this.set(currentCol.row, currentCol.col, currentCol.ob);
    			currentCol=currentCol.nextc;
    		}
    		currentRow=currentRow.nextr;
    	}
	}
	public SparseMatrix(Ontology s, Ontology t, alignType type) {
		this();
    	relation = MappingRelation.EQUIVALENCE;
    	typeOfMatrix = type;
    	
    	sourceOntologyID=s.getID();
    	targetOntologyID=t.getID();    	
	}
	public SparseMatrix(alignType type, MappingRelation rel){
		this();
    	relation = rel;
    	typeOfMatrix = type;
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
					rows++;
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
							}else {
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
						else if(column == currentCell.col){//implicit removal
							if(obj==null){//gotta remove this RowCell
								if(currentCell.nextr==null && currentCell.prevr==null){//im in the middle of the row
									if(currentCell.nextc==null){//end of the row
										currentCell.prevc.nextc=null;
										notDone=false;
										break;
									}else{//time to start linking...
										currentCell.nextc.prevc=currentCell.prevc;
										currentCell.prevc.nextc=currentCell.nextc;
										notDone=false;
										break;
									}
								}else{//we are a header more linking..
									if(currentCell.nextc==null){//nothing in this row
										if(currentCell.nextr==null){
											currentCell.prevr=null;
										}
										else{
											currentCell.nextr.prevr=currentCell.prevr;
											currentCell.prevr.nextr=currentCell.nextr;	
										}
										notDone=false;
										break;
									}else{//need to link the row headers to a new row header
										currentCell.nextr.prevr=currentCell.nextc;
										currentCell.prevr.nextr=currentCell.nextc;
										
										currentCell.nextc.prevr=currentCell.prevr;
										currentCell.nextc.nextr=currentCell.nextr;
										currentCell.nextc.prevc=null;
										
										notDone=false;
										break;
									}
								}
							}else{
								//replace whats in the current rowCell
								currentCell.ob=obj;
								notDone=false;
								break;
							}
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
					rows++;
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
	
	public RowCell<Mapping> getHead(){
		return head;
	}
	
	@Override
	public String toString() {
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
		System.err.println("Not implemented");
		return null;
	}
	@Override
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList,
			List<Integer> colsIncludedList, boolean considerThreshold,
			double threshold) {
		System.err.println("Not implemented");
		return null;
	}
	@Override
	public List<Mapping> chooseBestN(boolean considerThreshold, double threshold) {
		System.err.println("Not implemented");
		return null;
	}
	@Override
	public List<Mapping> chooseBestN(List<Integer> rowsIncludedList,
			List<Integer> colsIncludedList) {
		System.err.println("Not implemented");
		return null;
	}
	@Override
	public SimilarityMatrix clone() {
		return new SparseMatrix(this);
	}
	@Override
	public void fillMatrix(double d, List<Node> sourceList,
			List<Node> targetList) {
		// TODO not needed yet
		System.err.println("Not implemented yet");
	}
	@Override
	public alignType getAlignType() { return typeOfMatrix;}
	@Override
	public Mapping[] getColMaxValues(int col, int numMaxValues) {
		//i do not check for maxAlignements < numMaxValues
		Mapping[] maxAlignments= new Mapping[numMaxValues];
		
		for(int h = 0; h<maxAlignments.length;h++) {
			maxAlignments[h] = new Mapping(-1); //intial max alignments have sim equals to -1, don't put 0 could create problem in the next for
		}
		
		Mapping currentValue;
		Mapping currentMax;
		
		RowCell<Mapping> currentRow=head.nextr;
		
		while(currentRow!=null){
			RowCell<Mapping> currentCol=currentRow;
			while(currentCol!=null){
				if(currentCol.col==col){
					currentValue= currentCol.ob;
					for(int k = 0;k<maxAlignments.length; k++) {
						currentMax = maxAlignments[k];
						if(currentValue.getSimilarity() >= currentMax.getSimilarity()) { //if so switch the new value with the one in array and then i have to continue scanning the array to put in the switched value
							maxAlignments[k] = currentValue;
							currentValue = currentMax;
						}
					}
					break;
				}
				currentCol=currentCol.nextc;
			}
			currentRow=currentRow.nextr;
		}
		
		return maxAlignments;
	}
	@Override
	public double getColSum(int col) {
		// returns a sum of similarities for a col
		double sum=0;
		RowCell<Mapping> currentRow;//these RowCells are the originals
    	
    	currentRow=head.nextr;//get the first row
    	
    	while(currentRow!=null){    		
    		Mapping temp=get(currentRow.row,col);
    		if(temp!=null)
    			sum+=temp.getSimilarity();
    		
    		currentRow=currentRow.nextr;
    	}
		return sum;
	}
	@Override
	public int getColumns() {
		//FIXME this method is not used in the sparse matrix because the columns can be different for each row
		System.err.println("This method is not used because the column size differs for each row");
		return -1;
	}
	@Override
	public double[][] getCopiedSimilarityMatrix() {
		// FIXME remove
		System.err.println("to be deleted");
		return null;
	}
	@Override
	public double getMaxValue() {
		// returns the max similarity of the matrix
		double max=0.0;
		RowCell<Mapping> currentCol, currentRow;
    	currentRow=head.nextr;//get the first row
    	
    	while(currentRow!=null){
    		if(currentRow.ob.getSimilarity()>max)
    			max=currentRow.ob.getSimilarity();
 
    		currentCol=currentRow.nextc;
    		while(currentCol!=null){
    			if(currentCol.ob.getSimilarity()>max)
        			max=currentCol.ob.getSimilarity();
    			currentCol=currentCol.nextc;
    		}
    		currentRow=currentRow.nextr;
    	}
		return max;
	}
	@Override
	public Mapping[] getRowMaxValues(int row, int numMaxValues) {
		//i do not check for maxAlignements < numMaxValues
		Mapping[] maxAlignments= new Mapping[numMaxValues];
		
		for(int h = 0; h<maxAlignments.length;h++) {
			maxAlignments[h] = new Mapping(-1); //intial max alignments have sim equals to -1, don't put 0 could create problem in the next for
		}
		
		Mapping currentValue;
		Mapping currentMax;
		
		RowCell<Mapping> currentRow=head.nextr;
		
		while(currentRow!=null){
			if(currentRow.row==row){
				RowCell<Mapping> currentCol=currentRow;
				while(currentCol!=null){
					currentValue= currentCol.ob;
					for(int k = 0;k<maxAlignments.length; k++) {
						currentMax = maxAlignments[k];
						if(currentValue.getSimilarity() >= currentMax.getSimilarity()) { //if so switch the new value with the one in array and then i have to continue scanning the array to put in the switched value
							maxAlignments[k] = currentValue;
							currentValue = currentMax;
						}
					}
					currentCol=currentCol.nextc;
				}
				break;
			}
			currentRow=currentRow.nextr;
		}
		
		return maxAlignments;
	}
	@Override
	public double getRowSum(int row) {
		// gets the sum of the similarities for a row
		double sum=0.0;
		
		RowCell<Mapping> currentCol, currentRow;
		currentRow=head.nextr;//get the first row
		while(currentRow!=null){
			if(currentRow.row==row){
				sum+=currentRow.ob.getSimilarity();
	    		currentCol=currentRow.nextc;
	    		while(currentCol!=null){
	    			sum+=currentCol.ob.getSimilarity();
	    			currentCol=currentCol.nextc;
	    		}
			}
    		currentRow=currentRow.nextr;
    	}
		return sum;
	}
	@Override
	public int getRows() {
		// returns the number of the rows in the matrix
		return rows;
	}
	@Override
	public double getSimilarity(int i, int j) {
		// returns the similarity of the specified row
		Mapping temp=get(i,j);
		if(temp==null)
			return 0.00d;
		return temp.getSimilarity();
	}
	@Override
	public Mapping[] getTopK(int k) {
		// FIXME implemnt
		System.err.println("Not implemented yet topK");
		return null;
	}
	@Override
	public Mapping[] getTopK(int k, boolean[][] filteredCells) {
		// FIXME implement
		System.err.println("Not implemented yet topK2");
		return null;
	}
	@Override
	public void initFromNodeList(List<Node> sourceList, List<Node> targetList) {
		// FIXME remove
		System.err.println("to be deleted");
		
	}
	@Override
	public void setSimilarity(int i, int j, double d) {
		// sets the similarity of a specified entry in the matrix
		Mapping temp=get(i,j);
		if(temp!=null)
			temp.setSimilarity(d);
		
	}
	@Override
	public SimilarityMatrix toArraySimilarityMatrix() {
		//FIXME remove
		System.err.println("to be deleted");
		return null;
	}
	@Override
	public List<Mapping> toMappingArray() {
		ArrayList<Mapping> mappings=new ArrayList<Mapping>();
		RowCell<Mapping> currentCol, currentRow;
		currentRow=head.nextr;//get the first row
		while(currentRow!=null){
			mappings.add(currentRow.ob);
    		currentCol=currentRow.nextc;
    		while(currentCol!=null){
    			mappings.add(currentCol.ob);
    			currentCol=currentCol.nextc;
    		}
    		currentRow=currentRow.nextr;
    	}
		return mappings;
	}
	@Override
	public List<Mapping> toMappingArray(FileWriter fw, int round) {
		ArrayList<Mapping> mappingArray = new ArrayList<Mapping>();
		RowCell<Mapping> currentCol, currentRow;
		currentRow=head.nextr;//get the first row
		while(currentRow!=null){
			mappingArray.add(currentRow.ob);
			if(round == 1)
			try {
				fw.append(currentRow.ob.toString() + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
    		currentCol=currentRow.nextc;
    		while(currentCol!=null){
    			mappingArray.add(currentCol.ob);
    			if(round == 1)
    			try {
    				fw.append(currentCol.ob.toString() + "\n");
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			currentCol=currentCol.nextc;
    		}
    		currentRow=currentRow.nextr;
    	}
		
		
		for(int i = 0; i < getRows(); i++){
			for(int j = 0; j < getColumns(); j++){
				if(this.get(i, j) != null){
					
				}
			}
		}
		return mappingArray;
	}
	@Override
	public List<Double> toSimilarityArray(List<Mapping> mapsArray) {
		ArrayList<Double> similarities=new ArrayList<Double>();
		for(int i=0;i<mapsArray.size();i++)
			similarities.add(mapsArray.get(i).getSimilarity());
		return similarities;
	}

	
	
	public class RowCell <E>
	{
		public int row, col;
		public E ob;
		public RowCell<E> nextr=null, nextc=null, prevr=null, prevc=null;
		public RowCell(int r, int c, E o)
		{
			row=r;
			col=c;
			ob=o;
		}
	}
	
	//junit tests
	/*
	@Test public void replaceTest(){
		SparseMatrix m=new SparseMatrix();
		m.set(0, 0, new Mapping(.0));
		m.set(0, 0, null);
		m.set(0, 1, new Mapping(.2));
		//System.out.println(m.get(0, 1));
		assertTrue(m.get(0, 1).getSimilarity()==.2);
	}
	/*
	@Test public void insertAndGet16000000() {
		long start=System.currentTimeMillis();
		for(int j=0;j<500;j++){
			Random r=new Random();
			ArrayList<Integer> xVals=new ArrayList<Integer>();
			ArrayList<Integer> yVals=new ArrayList<Integer>();
			
			for(int i=0;i<4000;i++){
				xVals.add(new Integer(i));
				yVals.add(new Integer(i));
			}
			
			SparseMatrix m=new SparseMatrix();
			int[] x=new int[16000000];
			int[] y=new int[16000000];
			double[] z=new double[16000000];
			//insert 500 entries in the matrix and
			for(int i=0;i<4000;i++){
				int x1=r.nextInt(xVals.size());
				int y1=r.nextInt(yVals.size());
				double z1=r.nextDouble();
				
				//System.out.println("inserting i="+i+" : ("+xVals.get(x1)+","+yVals.get(y1)+","+z1+")");
				m.set(xVals.get(x1), yVals.get(y1), new Mapping(z1));
				x[i]=xVals.get(x1);
				y[i]=yVals.get(y1);
				z[i]=z1;
				
				xVals.remove(x1);
				yVals.remove(y1);
			}
			/*
			//check the entries
			for(int i=0;i<4000;i++){
				//System.out.println("getting i="+i+" : ("+x[i]+","+y[i]+")");
				Mapping temp=m.get(x[i], y[i]);
				//System.out.println(x[i]+","+ y[i]);
				//System.out.println(temp);
				assertTrue(temp.getSimilarity()==z[i]);
			}
			
		}
		long end=System.currentTimeMillis();
		long total=end-start;
		System.out.println("total time (in seconds): "+(total/100)/500);
	}
	*/
}