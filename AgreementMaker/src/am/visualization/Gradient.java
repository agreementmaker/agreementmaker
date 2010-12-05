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
	
	private double maxValue = 1.0;
	
	public Gradient(Color fg, Color bg) {
		foreground = fg;
		background = bg;
		
		if( foreground.getRed() != background.getRed() ){
			diffRed = true;
			colorDiffRed = background.getRed() - foreground.getRed();
		}
		if( foreground.getGreen() != background.getGreen() ) {
			diffGreen = true;
			colorDiffGreen = background.getGreen() - foreground.getGreen();
		}
		
		if( foreground.getBlue() != background.getBlue() ) {
			diffBlue = true;
			colorDiffBlue = background.getBlue() - foreground.getBlue();
		}
	}
	
	/**
	 * Returns the color associated with a certain percentage in the gradient.
	 * @param inputValue A percentage from 0.0 to 1.0;
	 * @return
	 */
	public Color getColor( double inputValue ) {
		if( inputValue < 0d ) { inputValue = 0d; }
		if( inputValue > maxValue ) { inputValue = maxValue; }

		int currentDiffRed = 0, currentDiffGreen = 0, currentDiffBlue = 0;
		
		double ratio = inputValue/maxValue;
		
		//System.out.println("");
		
		if( diffRed) currentDiffRed = (new Double(ratio * ( colorDiffRed ))).intValue();
		if( diffGreen) currentDiffGreen = (new Double(ratio * ( colorDiffGreen ))).intValue();
		if( diffBlue) currentDiffBlue = (new Double(ratio * ( colorDiffBlue ))).intValue();
		
		return new Color(background.getRed() - currentDiffRed , background.getGreen() - currentDiffGreen , background.getBlue() - currentDiffBlue);
	}
	
	public void setMax( double maxValue ) { 
		this.maxValue = maxValue;
	} 
	
	
}
