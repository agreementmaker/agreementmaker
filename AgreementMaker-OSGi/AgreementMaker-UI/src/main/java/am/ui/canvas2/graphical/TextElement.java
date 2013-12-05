package am.ui.canvas2.graphical;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JFrame;

import am.ui.canvas2.utility.Canvas2Layout;

public class TextElement extends GraphicalElement {

	protected String text = "";
	protected boolean bold = false;
	
	public TextElement(int x1, int y1, int width, int height, Canvas2Layout l,
			int ontID) {
		super(x1, y1, width, height, l, ontID);
		type = NodeType.TEXT_ELEMENT;
	}
	
	public void setText( String txt ) { text = txt;	width = calcWidth(); }  // width changes with text is changed
	public String getText() { return text; }
	public void setBold(boolean b) { bold = b; width = calcWidth(); }  // width changes when bold is toggled
	
	/**
	 * Calculate the pixel width of the text associated with this element.
	 * @return Width in pixels.
	 */
	public int calcWidth() {
		Font workingFont;
		if( bold ) workingFont = font.deriveFont( Font.BOLD );
		else workingFont = font;
		
		FontMetrics fontMetrics = new JFrame().getFontMetrics(workingFont);  // maybe there's a better way to do this... we shouldn't need to make a new JFrame just for the metric
        return fontMetrics.stringWidth(text);
	}
	
	@Override
	public void draw(Graphics g) {
		Font workingFont = font;
		if( bold ) 	workingFont = font.deriveFont( Font.BOLD );
		
		g.setFont(workingFont);
		g.drawString( text, x, y );
	}

}
