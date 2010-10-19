package am.visualization;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import am.app.mappingEngine.AlignmentMatrix;

public class MatrixPlot extends Canvas {

	private static final long serialVersionUID = -8579270363759208673L;

	private AlignmentMatrix matrix;

	private BufferedImage I;
	
	int squareSize = 10;

	public MatrixPlot(AlignmentMatrix mtx) {
		super();
		setBackground(Color.BLACK);
		setMatrix(mtx);
		draw();
		
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
		g.drawImage(I, 0, 0, this);
	}
	
	/**
	 * Set the size of the window to the dimensions of the matrix.
	 */
	public void setPlotSize() {

		setSize(matrix.getRows() * squareSize, matrix.getColumns() * squareSize);

	}

}
