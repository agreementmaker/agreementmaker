package am.userInterface.canvas2.graphical;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JFrame;

import am.userInterface.canvas2.utility.Canvas2Layout;

import com.kitfox.svg.app.beans.SVGIcon;

public class SVGIconElement extends TextElement {

	private SVGIcon icon;
	private int iconBottomMargin = 3; // the space between the icon and the text (in pixels);

	private Component container;
	
	public SVGIconElement(int x1, int y1, int width, int height, Canvas2Layout l, int ontID, SVGIcon icon, String iconText) {
		super(x1, y1, width, height, l, ontID);
		
		this.icon = icon;
		this.text = iconText;
		
		this.icon.setPreferredSize(new Dimension(width,height));
		this.type = NodeType.ICON_ELEMENT;
		
		container = l.getVizPanel();
	}

	/**
	 * 
	 * @param x1 X location of the icon.
	 * @param y1 Y location of the icon.
	 * @param width
	 * @param height
	 * @param comp The component onto which the icon is drawn.
	 * @param ontID
	 * @param icon
	 * @param iconText
	 * @throws Exception 
	 */
	public SVGIconElement(int x1, int y1, int width, int height, Component comp, int ontID, SVGIcon icon, String iconText) throws Exception {
		super(x1, y1, width, height, null, ontID);
		
		this.icon = icon;
		this.text = iconText;
		
		this.icon.setPreferredSize(new Dimension(width,height));
		this.type = NodeType.ICON_ELEMENT;
		
		if( comp == null ) throw new Exception("Cannot have a null component.");
		if( icon == null ) throw new Exception("Cannot have a null icon.");
		container = comp;
	}
	
	/**
	 * Takes into account the width of the icon.
	 */
	@Override
	public int calcWidth() {
		Font workingFont;
		if( bold ) workingFont = font.deriveFont( Font.BOLD );
		else workingFont = font;
		
		FontMetrics fontMetrics = new JFrame().getFontMetrics(workingFont);  // maybe there's a better way to do this... we shouldn't need to make a new JFrame just for the metric
        int textWidth = fontMetrics.stringWidth(text);
        
        if( icon.getIconWidth() > textWidth ) return icon.getIconWidth();
        else return textWidth;
	}
	
	public int getTextWidth() {
		Font workingFont;
		if( bold ) workingFont = font.deriveFont( Font.BOLD );
		else workingFont = font;
		
		FontMetrics fontMetrics = new JFrame().getFontMetrics(workingFont);  // maybe there's a better way to do this... we shouldn't need to make a new JFrame just for the metric
        return fontMetrics.stringWidth(text);
	}
	
	public int getTextHeight() {
		Font workingFont;
		if( bold ) workingFont = font.deriveFont( Font.BOLD );
		else workingFont = font;
		
		FontMetrics fontMetrics = new JFrame().getFontMetrics(workingFont);  // maybe there's a better way to do this... we shouldn't need to make a new JFrame just for the metric
        return fontMetrics.getHeight();
	}
	
	/**
	 * Returns the icon associated with this element.
	 * @return svgSalamaner icon.
	 */
	public SVGIcon getIcon() {	return icon; }
	
	@Override
	public void draw(Graphics g) {
		
		if( icon != null && container != null ) icon.paintIcon(container, g, x, y);
		else System.out.println("ERROR: Null icon/container.");
		
		//g.drawRect(x, y, icon.getIconWidth(), icon.getIconHeight());
		
		g.drawString( text, x, y + getTextHeight() + icon.getIconHeight() + iconBottomMargin);
			
	}
}
