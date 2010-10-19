package am.visualization;

import java.awt.Color;

/**
 * Handles the translation from one color to another via a 0.0 to 1.0 scale.
 * @author cosmin
 *
 */
public class Gradient {

	Color foreground;
	Color background;
	
	int colorDiffRed = 0;
	int colorDiffGreen = 0;
	int colorDiffBlue = 0;
	boolean diffRed = false;
	boolean diffGreen = false;
	boolean diffBlue = false;
	
	boolean doC1minusC2 = false;
	
	
	public Gradient(Color fg, Color bg) {
		foreground = fg;
		background = bg;
		
		if( foreground.getRed() != background.getRed() ){
			diffRed = true;
			colorDiffRed = background.getRed() - foreground.getRed();
		}
		if( foreground.getGreen() != background.getGreen() ) {
			diffGreen = true;
			colorDiffGreen = background.getRed() - foreground.getRed();
		}
		
		if( foreground.getBlue() != background.getBlue() ) {
			diffBlue = true;
			colorDiffBlue = background.getBlue() - foreground.getBlue();
		}
	}
	
	/**
	 * Returns the color associated with a certain percentage in the gradient.
	 * @param percentage A percentage from 0.0 to 1.0;
	 * @return
	 */
	public Color getColor( double percentage ) {
		if( percentage < 0d ) { percentage = 0d; }
		if( percentage > 1.0d ) { percentage = 1.0d; }

		int currentDiffRed = 0, currentDiffGreen = 0, currentDiffBlue = 0;
		
		if( diffRed) currentDiffRed = (new Double(percentage * ( colorDiffRed ))).intValue();
		if( diffGreen) currentDiffGreen = (new Double(percentage * ( colorDiffGreen ))).intValue();
		if( diffBlue) currentDiffBlue = (new Double(percentage * ( colorDiffBlue ))).intValue();
		
		return new Color(background.getRed() - currentDiffRed , background.getGreen() - currentDiffGreen , background.getBlue() - currentDiffBlue);
	}
	
	
}
