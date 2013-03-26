package am.userInterface;

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
	public static Color selected  = new Color (51,153,255); 
	public static Color highlighted  = new Color (172,205,249); ;
	//public static Color highlighted = new Color(52, 85, 119);
	public static Color mapped = new Color(240,128,128);
	
	// vertex hover color
	public static Color hover = new Color(232,232,232);
	
	// mapping line color
	public static Color lineColor = new Color (255,0,0);
	
	public static Color[] matchersColors = {
		Color.magenta,
		Color.GREEN,
		Color.PINK,
		new  Color(153,153,255),
		Color.CYAN,
		Color.ORANGE,
	};
	
}
