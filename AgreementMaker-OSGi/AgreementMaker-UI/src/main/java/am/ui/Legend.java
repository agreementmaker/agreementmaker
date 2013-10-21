package am.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import am.app.Core;

/**
 * 
 * TODO: Make the settings get saved to AppPreferences.
 * TODO: Refactor code to be more organized. (e.g. Make Legend extend JDialog (is that a good idea?).)
 * TODO: Redesign the UI layout of this dialog to be more user friendly.
 * 
 * @author Nalin
 * 			^-- find out full name of Nalin - Cosmin, Dec 5, 2010
 *
 */
public class Legend implements ActionListener, ChangeListener{
	
	protected JColorChooser cc;
	private JDialog legendFrame;

	//buttons to change the color of the components of the user interface
	private JButton	btnLineColor, btnForeground, btnDividers, btnHoverColor, btnBackground, btnSelected;

	// boolean values to keep track of which button button in key is changed
	// TODO: Find a better way to do this. We don't need these variables.
	private boolean backgroundChanged, selectedChanged, lineColorChanged, foregroundColorChanged, dividersColorChanged, hoverChanged;
	
	private JLabel lblBackground, lblSelected, lblLineColor, lblForeground, lblDividers, lblHoverOver;
	
	JPanel p;
	
	/**
	 * @param userInterface
	 */
	public Legend(){
		
		//ui = userInterface;
		legendFrame = new JDialog( UICore.getUI().getUIFrame(), "Color Legend");
		p = new JPanel(new GridLayout(13,13));  // 13 (instead of 12) to leave a blank space at the bottom
		
		// initialize all the labels					
		lblBackground = new JLabel("Background");
		lblForeground = new JLabel("Foreground");
		lblDividers = new JLabel("Dividers");
		lblSelected = new JLabel("Selected");
		lblLineColor = new JLabel("Line Color");
		lblHoverOver = new JLabel("Hover Over");

		// initialize all the buttons
		btnBackground = new JButton("");
		btnForeground = new JButton("");
		btnDividers = new JButton("");
		btnSelected = new JButton("");
		btnLineColor = new JButton("");
		btnHoverColor = new JButton("");
		
		// set the action listeners to all the buttons
		btnBackground.addActionListener(this);
		btnForeground.addActionListener(this);
		btnDividers.addActionListener(this);
		btnSelected.addActionListener(this);
		btnLineColor.addActionListener(this);
		btnHoverColor.addActionListener(this);
		
		// set the background color to the appropriate Colors from COLOR class
		btnBackground.setBackground(Colors.background);
		btnForeground.setBackground(Colors.foreground);
		btnDividers.setBackground(Colors.dividers);
		btnSelected.setBackground(Colors.selected);
		btnLineColor.setBackground(Colors.lineColor);
		btnHoverColor.setBackground(Colors.hover);
		
		// add the labels and buttons to the panel
		p.add(lblBackground);   	// 1
		p.add(btnBackground);		// 2
		p.add(lblForeground);   	// 3
		p.add(btnForeground);       // 4
		p.add(lblDividers);			// 5
		p.add(btnDividers);			// 6
		p.add(lblSelected);			// 7
		p.add(btnSelected);			// 8
		p.add(lblLineColor);		// 9
		p.add(btnLineColor);		// 10
		p.add(lblHoverOver);		// 11
		p.add(btnHoverColor);		// 12
		
		// add the panel to the frame
		legendFrame.getContentPane().add(p);
		
		// set frame size
		legendFrame.setSize(400,300);
		
		// make sure the frame is visible
		legendFrame.setVisible(true); 
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if(obj == btnBackground)
		{
			makeAllFalse();
			backgroundChanged = true;
			createColorChooser();
		}
		else if (obj == btnSelected)
		{
			makeAllFalse();
			selectedChanged = true;
			createColorChooser();
		}
		else if (obj == btnLineColor)
		{
			makeAllFalse();
			lineColorChanged = true;
			createColorChooser();
		}
		else if (obj == btnForeground)
		{
			makeAllFalse();
			foregroundColorChanged = true;
			createColorChooser();
		}
		else if (obj == btnDividers)
		{
			makeAllFalse();
			dividersColorChanged = true;
			createColorChooser();
		}
		else if ( obj == btnHoverColor ) {
			makeAllFalse();
			hoverChanged = true;
			createColorChooser();
		}
	}
	/*******************************************************************************************/
	/**
	 * This method creates the color chooser
	 */	
	public void createColorChooser()
	{
		// create a color chooser 
		cc = new JColorChooser();
		
		// set item change listener
		cc.getSelectionModel().addChangeListener(this);
		
		// create a frame
		JFrame colorChooserFrame;
		colorChooserFrame = new JFrame("Color Chooser");
		
		// create a panel with grid layout of 1 x 1
		JPanel p;
		p = new JPanel(new GridLayout(1,1));
		
		// add the color chooser to the panel
		p.add(cc);
		
		// add the panel to the frame
		colorChooserFrame.getContentPane().add(p);
		
		// set frame size 
		colorChooserFrame.setSize(500,500);
		
		// make sure the frame is visible
		colorChooserFrame.setVisible(true); 
	}
	/*******************************************************************************************/
	/** 
	 * This method makes all the button selections in the key frame to be false (resets all buttons selection)
	 */
	public void makeAllFalse()
	{
		lineColorChanged  = foregroundColorChanged = dividersColorChanged = false;
		backgroundChanged  = selectedChanged  = hoverChanged =  false;
	}
	/*******************************************************************************************/
	/**
	 * This function defines stateChanged for JColorChooser
	 * @param e of type ChangeEvent
	 */
	public void stateChanged(ChangeEvent e)
	{
		
		if(selectedChanged == true)
		{
			Colors.selected = cc.getColor();
			btnSelected.setBackground(Colors.selected);
		}
		else if (backgroundChanged == true)
		{
			Colors.background = cc.getColor();
			btnBackground.setBackground(Colors.background);
		}
		else if (lineColorChanged == true)
		{
			Colors.lineColor = cc.getColor();
			btnLineColor.setBackground(Colors.lineColor);
		}
		
		else if (foregroundColorChanged == true)
		{
			Colors.foreground = cc.getColor();
			btnForeground.setBackground(Colors.foreground);
		}
		else if (dividersColorChanged == true)
		{
			Colors.dividers = cc.getColor();
			btnDividers.setBackground(Colors.dividers);
		}
		else if (hoverChanged == true )
		{
			Colors.hover = cc.getColor();
			btnHoverColor.setBackground(Colors.hover);
		}
		UICore.getUI().redisplayCanvas();
	}
}
