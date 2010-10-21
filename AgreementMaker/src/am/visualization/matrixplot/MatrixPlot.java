package am.visualization.matrixplot;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.swing.JPanel;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.evaluation.clustering.Cluster;
import am.visualization.Gradient;
import am.visualization.MatcherAnalyticsPanel.VisualizationType;

public class MatrixPlot extends JPanel {

	private static final long serialVersionUID = -7536491459476626040L;

	private final AbstractMatcher  matcher;
	private final SimilarityMatrix matrix;
	private final VisualizationType type;
	
	private int squareSize = 10;
	private int border = 2;
	private BufferedImage I;
	private Point selected = null;
	
	private MatrixPlotPanel enclosingPanel = null; // set if we are using a MatrixPlotPanel
	private boolean viewAlignmentOnly = false;
	
	private Alignment<Mapping> referenceAlignmentSet = null;
	private Color referenceAlignmentColor = Color.RED;
	
	private Cluster<Mapping> viewCluster = null;
	
	public MatrixPlot(SimilarityMatrix mtx) {
		super();
		I = null;
		matrix = mtx;
		matcher = null;
		
		if( mtx.getAlignType() == alignType.aligningClasses ) type = VisualizationType.CLASS_MATRIX;
		else type = VisualizationType.PROPERTIES_MATRIX;
		
		createImage(false);
	}
	
	public MatrixPlot(AbstractMatcher a, SimilarityMatrix mtx, MatrixPlotPanel mpnl) {
		super();
		I = null;
		matcher = a;
		matrix = mtx;
		
		if( mtx.getAlignType() == alignType.aligningClasses ) type = VisualizationType.CLASS_MATRIX;
		else type = VisualizationType.PROPERTIES_MATRIX;
		
		createImage(false);
		enclosingPanel = mpnl;
	}
	
	public void draw(boolean reCreate) {
		setPlotSize();
		createImage(reCreate);
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
			//Graphics2D g = (Graphics2D)I.getGraphics(); // TODO: Get rid of WritableRaster
			WritableRaster wr = I.getRaster();
			
			if( viewCluster != null ) {
				// we are visualizing a cluster;
				// visualize only the Alignment, using a solid color
				
				Graphics2D g = (Graphics2D)I.getGraphics();
				
				g.setColor( Color.WHITE );
				g.fillRect(0, 0, I.getWidth(), I.getHeight() );
				g.setColor( Color.ORANGE);
		
				for( Mapping map : viewCluster ) {
					int x1 = map.getSourceKey() * squareSize;
					int y1 = map.getTargetKey() * squareSize;
					g.fillRect(x1, y1, squareSize, squareSize);
				}
				
				g.dispose();
				
			} else {
				// we are not visualizing a cluster;
				
				if( !viewAlignmentOnly ) { 
					// visualize the full alignment matrix, using a gradient
					
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
				} else {
					// visualize only the Alignment, using a solid color
					
					Alignment<Mapping> vizAlignment = null;
					
					if( type == VisualizationType.CLASS_MATRIX ) vizAlignment = matcher.getClassAlignmentSet();
					if( type == VisualizationType.PROPERTIES_MATRIX ) vizAlignment = matcher.getPropertyAlignmentSet();
					
					Graphics2D g = (Graphics2D)I.getGraphics();
					
					g.setColor( Color.WHITE );
					g.fillRect(0, 0, I.getWidth(), I.getHeight() );
					g.setColor( Color.BLUE );
					
					if( vizAlignment != null ) {
						
						for( Mapping map : vizAlignment ) {
							int x1 = map.getSourceKey() * squareSize;
							int y1 = map.getTargetKey() * squareSize;
							g.fillRect(x1, y1, squareSize, squareSize);
						}
						
					}
					
					g.dispose();
				}
			}
			
			// add the dots for the reference alignment.
			if ( referenceAlignmentSet != null )
			for( Mapping a : referenceAlignmentSet ) {
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
	
	public void setCluster( Cluster<Mapping> c ) { 
		viewCluster = c;
		createImage(true);
		repaint();
	}
	
	public void selectMapping(int row, int col) {
		if( selected == null ) {
			selected = new Point(row,col);
		} else if( selected.x == row && selected.y == col ) {
			selected = null;
		} else {
			selected = new Point(row,col);
		}
		repaint();
	}
	public void clearSelectedMapping() { selected = null; }
	
	public void setEnclosingPanel( MatrixPlotPanel pnl ) { enclosingPanel = pnl; }
	public MatrixPlotPanel getEnclosingPanel() { return enclosingPanel; }
	
	public void setViewAlignmentOnly( boolean vao ) {
		if( (vao && !viewAlignmentOnly) || (!vao && viewAlignmentOnly) ) {
			// View Alignment Only has been toggled.  Update the drawing.
			viewAlignmentOnly = vao;
			createImage(true);
			repaint();
		}
	}
	public boolean getViewAlignmentOnly() { return viewAlignmentOnly; }
	
	/**
	 * Set the size of the window to the dimensions of the matrix.
	 */
	public void setPlotSize() { setPreferredSize(new Dimension( matrix.getRows() * squareSize, matrix.getColumns() * squareSize) ); }
	public int getSquareSize() { return squareSize; }
	public SimilarityMatrix getMatrix() { return matrix; }
	
	public void setReferenceAlignment( Alignment<Mapping> ref ) { 
		referenceAlignmentSet = ref;
		createImage(true);
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
				
		//if( enclosingPanel.popupMenuActive() ) return;
		
		super.paintComponent(g);
		
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
