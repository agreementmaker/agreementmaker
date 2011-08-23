package am.app.mappingEngine.similarityMatrix;

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
