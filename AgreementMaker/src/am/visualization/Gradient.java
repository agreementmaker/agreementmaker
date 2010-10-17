package am.visualization;

import java.awt.Color;

/**
 * Handles the translation from one color to another via a 0.0 to 1.0 scale.
 * @author cosmin
 *
 */
public class Gradient {

	Color color1;
	Color color2;
	
	public Gradient(Color c1, Color c2) {
		color1 = c1;
		color2 = c2;
	}
	
	/**
	 * Returns the color associated with a certain percentage in the gradient.
	 * @param percentage A percentage from 0.0 to 1.0;
	 * @return
	 */
	public Color getColor( double percentage ) {
		if( percentage < 0d ) { percentage = 0d; }
		if( percentage > 1.0d ) { percentage = 1.0d; }
		
		int colorDiffRed = 0;
		int colorDiffGreen = 0;
		int colorDiffBlue = 0;
		boolean diffRed = false;
		boolean diffGreen = false;
		boolean diffBlue = false;
		
		if( color1.getRed() != color2.getRed() ){
			diffRed = true;
			colorDiffRed = color2.getRed() - color1.getRed();
		}
		
		if( color1.getGreen() != color2.getGreen() ) {
			diffGreen = true;
			colorDiffGreen = color2.getRed() - color1.getRed();
		}
		
		if( color1.getBlue() != color2.getBlue() ) {
			diffBlue = true;
			colorDiffBlue = color2.getBlue() - color1.getBlue();
		}
		

		if( diffRed) colorDiffRed = (new Double(percentage * ( colorDiffRed ))).intValue();
		if( diffGreen) colorDiffGreen = (new Double(percentage * ( colorDiffGreen ))).intValue();
		if( diffBlue) colorDiffBlue = (new Double(percentage * ( colorDiffBlue ))).intValue();
		
		return new Color(color1.getRed() + colorDiffRed , color1.getGreen() + colorDiffGreen , color1.getBlue() + colorDiffBlue);
		
	}
	
	
}
