package am.ui.canvas2.graphical;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import am.ui.canvas2.utility.Canvas2Layout;

import com.kitfox.svg.app.beans.SVGIcon;

public class SVGIconElement extends TextElement {

	private SVGIcon icon;
	public static final int ICON_BOTTOM_MARGIN = 3; // the space between the icon and the text (in pixels);

	private JPanel container;
	
	private BufferedImage I;
	
	public SVGIconElement(int x1, int y1, int width, int height, Canvas2Layout l, int ontID, SVGIcon icon, String iconText) {
		super(x1, y1, width, height, l, ontID);
		
		this.icon = icon;
		this.text = iconText;
		
		this.icon.setPreferredSize(new Dimension(width,height));
		this.type = NodeType.ICON_ELEMENT;
		
		container = l.getVizPanel();
		
		if( icon != null && container != null ) {
			I = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			icon.paintIcon(null, I.getGraphics(), 0, 0);
		}
		
		if( getTextWidth() > width ) this.width = getTextWidth();
		this.height = height + ICON_BOTTOM_MARGIN + getTextHeight();
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
	public SVGIconElement(int x1, int y1, int width, int height, JPanel comp, int ontID, SVGIcon icon, String iconText) throws Exception {
		super(x1, y1, width, height, null, ontID);
		
		this.icon = icon;
		this.text = iconText;
		
		this.icon.setPreferredSize(new Dimension(width,height));
		this.type = NodeType.ICON_ELEMENT;
		
		if( icon != null && comp != null ) {
			I = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D)I.getGraphics();
			icon.paintIcon(null, g, 0, 0);
		}
		
		if( getTextWidth() > width ) this.width = getTextWidth();
		this.height = height + ICON_BOTTOM_MARGIN + getTextHeight();
		
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
		if( g == null ) return;
		
		Graphics2D gPlotArea = (Graphics2D)g;

		
		
		// icon.paintIcon(container, g, x, y);

		if( hover ) {
			Composite c = gPlotArea.getComposite();
			gPlotArea.setComposite(AlphaComposite.SrcAtop);
			gPlotArea.drawImage(I, x, y, null);
			gPlotArea.setColor(new Color(0,255,0,128));
			gPlotArea.fillRect(x, y, icon.getIconWidth(), icon.getIconHeight());
			gPlotArea.setComposite(c);
			gPlotArea.setColor(Color.BLACK);
			g.drawRect(x-5, y-5, width+10, height+10);
		} else {
			gPlotArea.drawImage(I, x, y, null);
		}
		
		gPlotArea.setColor(container.getBackground());
		g.fillRect( x, y + icon.getIconHeight() + ICON_BOTTOM_MARGIN + 4, getTextWidth(), getTextHeight());
		
		if( selected ) {
			gPlotArea.setColor(new Color(255,255,0,64));
			g.fillRect( x-2, y + icon.getIconHeight() + ICON_BOTTOM_MARGIN + 4, getTextWidth()+4, getTextHeight());
			gPlotArea.setColor(Color.BLACK);
			g.drawRect( x-2, y + icon.getIconHeight() + ICON_BOTTOM_MARGIN + 4, getTextWidth()+4, getTextHeight());
		}
		
		gPlotArea.setColor(Color.BLACK);
		g.drawString( text, x, y + getTextHeight() + icon.getIconHeight() + ICON_BOTTOM_MARGIN);
			
	}
}
