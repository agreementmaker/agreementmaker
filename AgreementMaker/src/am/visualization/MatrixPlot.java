package am.visualization;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.SwingUtilities;

import am.app.mappingEngine.AlignmentMatrix;
import am.visualization.MatcherAnalyticsEvent.EventType;

public class MatrixPlot extends Canvas implements MouseListener, MatcherAnalyticsEventListener {

	private static final long serialVersionUID = -8579270363759208673L;

	private AlignmentMatrix matrix;
	private final MatcherAnalyticsEventDispatch dispatch;
	
	private BufferedImage I;
	
	private int squareSize = 10;
	private Point selected = null;

	public MatrixPlot(AlignmentMatrix mtx, MatcherAnalyticsEventDispatch d ) {
		super();
		dispatch = d;
		//setBackground(Color.BLACK);
		setMatrix(mtx);
		draw();
		
		addMouseListener(this);
	}

	public void setMatrix(AlignmentMatrix mtx) {
		I = null;
		matrix = mtx;
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
			
			I.getColorModel();
			
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
						for( int i = 0; i < squareSize; i++ ) {
							for( int j = 0; j < squareSize; j++ ) {
								wr.setPixel(x1+i, y1+j, iArray);
							}
						}
					}

				}
			}
		}
	}
	
	public void draw() {
		setPlotSize();
		createImage(false);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		
		g2.drawImage(I, 0, 0, this);
		
		if( selected != null ) {
			int row = selected.x;
			int col = selected.y;
			int sqMod = squareSize % 2;
			int selectRowCol = (squareSize - sqMod) / 2;
			
			if( selectRowCol > 0 ) { // only draw the lines if we have enough room to do so.

				g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_ATOP , 1.0f));
				g2.setColor(Color.BLACK);
				g2.drawLine( row * squareSize + selectRowCol , 0, row * squareSize + selectRowCol , getHeight() );
				g2.drawLine( 0, col * squareSize + selectRowCol, getWidth(), col * squareSize + selectRowCol);
				//g2.drawLine(0,0, getWidth(), getHeight());
			}
		}
	}
	
	/**
	 * Set the size of the window to the dimensions of the matrix.
	 */
	public void setPlotSize() {

		setSize(matrix.getRows() * squareSize, matrix.getColumns() * squareSize);

	}

	public int getSquareSize() { return squareSize; }
	
	/**
	 * Draw a line crosshair for a selected mapping.
	 */
	public void selectMapping(int row, int col) { selected = new Point(row,col); repaint(); }
	public void clearSelectedMapping() { selected = null; }
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// the user has clicked on some part of our canvas.
		
		// Step 1.  Figure out which mapping the user wants to highlight.
		
		Point clickPoint = e.getPoint();
		
		int rowMod = clickPoint.x % squareSize;
		final int row = (clickPoint.x - rowMod) / squareSize;
		
		int colMod = clickPoint.y % squareSize;
		final int col = (clickPoint.y - colMod) / squareSize;
		
		selectMapping(row, col);

		if( dispatch != null ) {
			// broadcast this event to the other MatrixPlot objects.
			Runnable fire = new Runnable() {
				public void run() {
					dispatch.broadcastEvent( new MatcherAnalyticsEvent( this,  EventType.SELECT_MAPPING,  new Point(row,col) ));
				}
			};
			
			SwingUtilities.invokeLater(fire);
		}

	}
	
	/** These mouse event functions are not used yet. **/
	@Override public void mousePressed(MouseEvent e) {  }
	@Override public void mouseReleased(MouseEvent e) {	}
	@Override public void mouseEntered(MouseEvent e) {  }
	@Override public void mouseExited(MouseEvent e) {  }

	@Override
	public void receiveEvent(MatcherAnalyticsEvent e) {
		if( e.getSource() != this && e.type == EventType.SELECT_MAPPING ) {
			Point sel = (Point)e.payload;
			selectMapping(sel.x, sel.y);
		}
		
	}
	
}
