package am.visualization.matrixplot;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.AlignmentSet;
import am.visualization.Gradient;

public class MatrixPlot extends Canvas {

	private static final long serialVersionUID = -7536491459476626040L;

	private final AlignmentMatrix matrix;
	private int squareSize = 10;
	private int border = 2;
	private BufferedImage I;
	private BufferedImage R;
	private Point selected = null;
	
	private MatrixPlotPanel enclosingPanel = null; // set if we are using a MatrixPlotPanel
	
	private AlignmentSet<Alignment> referenceAlignmentSet = null;
	private Color referenceAlignmentColor = Color.RED;
	
	public MatrixPlot(AlignmentMatrix mtx) {
		super();
		I = null;
		matrix = mtx;
		createImage(false);
	}
	
	public MatrixPlot(AlignmentMatrix mtx, MatrixPlotPanel mpnl) {
		super();
		I = null;
		matrix = mtx;
		createImage(false);
		enclosingPanel = mpnl;
	}
	
	/**
	 * Set the size of the window to the dimensions of the matrix.
	 */
	public void setPlotSize() { setSize(matrix.getRows() * squareSize, matrix.getColumns() * squareSize); }
	public int getSquareSize() { return squareSize; }
	public AlignmentMatrix getMatrix() { return matrix; }
	
	public void draw() {
		setPlotSize();
		createImage(false);
	}
	
	/**
	 * 
	 * @param redraw If true, it will redraw the image.  If false, it will use the previously cached image.
	 */
	public void createImage( boolean redraw ) {
		if( I == null || redraw ) {

			int rows = matrix.getRows();
			int cols = matrix.getColumns();

			I = new BufferedImage(rows * squareSize, cols * squareSize, BufferedImage.TYPE_INT_RGB);
			WritableRaster wr = I.getRaster();
			Gradient grad = new Gradient( Color.BLUE, Color.WHITE);

			for( int r = 0; r < rows; r++ ){
				for( int c = 0; c < cols; c++ ) {
					int x1 = r * squareSize;
					int y1 = c * squareSize;
					
					double similarity = matrix.getSimilarity(r, c);
					Color simcolor = grad.getColor(similarity);
					int[] iArray = { simcolor.getRed(), simcolor.getGreen(), simcolor.getBlue() };
					
					if( squareSize == 1 ) {	
						wr.setPixel(x1, y1, iArray );
					} else {
						//g.drawRect(x1, y1, x1+squareSize, y1+squareSize);
						
						for( int i = 0; i < squareSize; i++ ) {
							for( int j = 0; j < squareSize; j++ ) {
								wr.setPixel(x1+i, y1+j, iArray);
							}
						}
						
					}
				}
			}
			
			// add the dots for the reference alignment.
			if ( referenceAlignmentSet != null )
			for( Alignment a : referenceAlignmentSet ) {
				int row = a.getEntity1().getIndex();
				int col = a.getEntity2().getIndex();
				
				int x1 = row * squareSize;
				int y1 = col * squareSize;
				
				int diameter  = squareSize - 2*border;
				
				int[] iArray = { referenceAlignmentColor.getRed(), referenceAlignmentColor.getGreen(), referenceAlignmentColor.getBlue() };
				if( diameter <= 0 ) {
					wr.setPixel(x1, y1, iArray);
				} else {
					for( int i = 0; i < squareSize; i++ ) {
						if( i > border && i < squareSize - border - 1 )
						for( int j = 0; j < squareSize; j++ ) {
							if( j > border && j < squareSize - border - 1 )
								wr.setPixel(x1+i, y1+j, iArray);
						}
					}
				}
				
				
			}
		}

	}
	
	public void selectMapping(int row, int col) { selected = new Point(row,col); repaint();	}
	public void clearSelectedMapping() { selected = null; }
	
	public void setEnclosingPanel( MatrixPlotPanel pnl ) { enclosingPanel = pnl; }
	public MatrixPlotPanel getEnclosingPanel() { return enclosingPanel; }
	
	public void setReferenceAlignment( AlignmentSet<Alignment> ref ) { 
		referenceAlignmentSet = ref;
		createImage(true);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D gPlotArea = (Graphics2D)g;

		gPlotArea.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP , 1.0f));
		gPlotArea.drawImage(I, 0, 0, this);
		
		if( selected != null ) {
			int row = selected.x;
			int col = selected.y;
			int sqMod = squareSize % 2;
			int selectRowCol = (squareSize - sqMod) / 2;
			
			if( selectRowCol > 0 ) { // only draw the lines if we have enough room to do so.


				gPlotArea.setColor(Color.BLACK);
				gPlotArea.drawLine( row * squareSize + selectRowCol , 0, row * squareSize + selectRowCol , getHeight() );
				gPlotArea.drawLine( 0, col * squareSize + selectRowCol, getWidth(), col * squareSize + selectRowCol);
				//g2.drawLine(0,0, getWidth(), getHeight());
			}
		}
	}
	
	
	
}
