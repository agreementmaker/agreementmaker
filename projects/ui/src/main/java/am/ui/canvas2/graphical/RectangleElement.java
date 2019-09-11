package am.ui.canvas2.graphical;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import am.ui.canvas2.utility.Canvas2Layout;

public class RectangleElement extends GraphicalElement {

	private boolean filled = true;
	private boolean rounded = true;
	private boolean border = true;
	private Color fillColor = Color.WHITE;
	private int arcWidth = 12;
	private int arcHeight = 12;

	private int padding = 4;
	
	private Color textColor = Color.BLACK;
	private String text;
	
	private Color borderColor = Color.BLACK;
	
	public RectangleElement(int x1, int y1, int width, int height, Canvas2Layout l,
			int ontID) {
		super(x1, y1, width, height, l, ontID);
		type = NodeType.RECTANGLE_ELEMENT;
	}

	public void setFilled( boolean f ) { filled = f; }
	public void setRounded( boolean r ) { rounded = r; }
	public void setBorder( boolean b ) { border = b; }
	public void setFillColor( Color c ) { fillColor = c; }
	public void setTextColor( Color c ) { textColor = c; }
	public void setBorderColor( Color c ) { borderColor = c; }
	public void setArc( int arcWidth, int arcHeight ) {
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
	}
	public void setPadding( int padding ) { this.padding = padding; }
	public void setText(String text, boolean updateWidth, boolean updateHeight) { 
		this.text = text; 
		if(updateWidth) {
			width = getFontWidth(text) + 2*padding + 8; // TODO: WHY THE 20????
		}
		if(updateHeight) {
			height = calcHeight();
		}
	}
	
	private Color hoverColor = new Color(0,255,0,128);
	
	/**
	 * Calculate the pixel width of the text associated with this element.
	 * @return Width in pixels.
	 */
	public int getFontWidth(String text) {
		Font workingFont = font;
		//if( bold ) workingFont = font.deriveFont( Font.BOLD );
		//else workingFont = font;
		
		FontMetrics fontMetrics = new JFrame().getFontMetrics(workingFont);  // maybe there's a better way to do this... we shouldn't need to make a new JFrame just for the metric
        return fontMetrics.stringWidth(text);
	}
	
	public int getFontHeight() {
		Font workingFont = font;
		//if( bold ) workingFont = font.deriveFont( Font.BOLD );
		//else workingFont = font;
		
		FontMetrics fontMetrics = new JFrame().getFontMetrics(workingFont);  // maybe there's a better way to do this... we shouldn't need to make a new JFrame just for the metric
        return fontMetrics.getHeight();
	}
	
	public int calcHeight() {
		return getFontHeight() + 2*padding;
	}
	
	@Override
	public void draw(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		
		if( filled && !hover ) {
			g2d.setColor(fillColor);
			if( rounded )
				g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
			else
				g2d.fillRect(x,y,width,height);
		} else if( hover ) {
			g2d.setColor(hoverColor);
			if( rounded )
				g2d.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
			else
				g2d.fillRect(x,y,width,height);
		}
		
		if( text != null ) {
			g2d.setColor(textColor);
			g2d.drawString( text ,x+padding,y+padding+getFontHeight());
		}
		
		if( border ) {
			g2d.setColor(borderColor);
			if( rounded )
				g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
			else
				g2d.drawRect(x, y, width, height);
		}
			
	}
	
}
