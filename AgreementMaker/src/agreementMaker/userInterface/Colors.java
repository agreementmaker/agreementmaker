package agreementMaker.userInterface;

import java.awt.Color;

/**
 * Colors  class contains global Color variables.
 *
 * @author ADVIS Research Laboratory
 * @version 11/27/2004
 */
public class Colors
{
	// background color of the canvas
	//public static Color background = new Color(25,25,112);
	public static Color background = new Color(255,255,255);
	
	// foreground of the the canvas
	//public static Color foreground = new Color(220,220,220);
	public static Color foreground = new Color(0,0,0);
	
	// dividers color
	//public static Color dividers = new Color(187,187,187);
	public static Color dividers = new Color(0,0,0);
	
	// vertex selection color
	public static Color selected  = new Color (53,53,222); 
	public static Color highlighted  = selected;
	//public static Color highlighted = new Color(52, 85, 119);
	public static Color mapped = new Color(240,128,128);
	
	// mapping line color
	public static Color lineColor = new Color (255,0,0);
	
	public static Color[] matchersColors = {
		Color.GREEN,
		Color.MAGENTA,
		Color.ORANGE,
		Color.PINK,
		Color.YELLOW,
		Color.CYAN,
	};
	
	
	
	//OLD COLORS 
	
	
	// vertex mapped color
	public static Color mappedByUser = new Color (153,0,255); // manual map

	// vertex mapped by context color
	public static Color mappedByContext = new Color (0,204,0);

	// vertex mapped by defn color
	public static Color mappedByDefn = new Color(240,128,128);
	
	// vertex mapped and selected color
	public static Color mappedByUserAndSelected = new Color(204,000,000);
	
	// vertex mapped by context and selected color
	public static Color mappedByContextAndSelected = new Color(25,25,25);


	
	// mapped line color (after mapping is done)
	public static Color mappedByUserLineColor = new Color(255,255,0);

	// mapped by context line color
	public static Color mappedByContextLineColor = new Color (0,255,0);

	// mapped by Definition line color
	public static Color mappedByDefnLineColor = new Color (34,139,34);
	
	//highlighted mapping lines
	public static Color mappedHighlightedLineColor = new Color(0,0,255);
}
