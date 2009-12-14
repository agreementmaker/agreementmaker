package am.userInterface;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import am.app.Core;

/**
 * @author Nalin
 *
 */
public class Legend implements ActionListener, ChangeListener{
	
	//buttons to change the color of the components of the user interface
	private JButton	background, selected;
	//	 boolean values to keep track of which button button in key is changed
	public boolean backgroundChanged, selectedChanged;
	//create the labels 
	JLabel backgroundLabel,selectedLabel;
	protected JColorChooser cc;
	
	JFrame frame;
	private JButton	lineColor, foreground, dividers;
	public boolean lineColorChanged, foregroundColorChanged, dividersColorChanged;
	
	JLabel lineColorLabel, foregroundLabel, dividersLabel;
	JPanel p;
	//UI ui;
	
	/**
	 * @param userInterface
	 */
	public Legend(){
		
		//ui = userInterface;
		frame = new JFrame("Legend");
		p = new JPanel(new GridLayout(11,11));
		
		// initialize all the labels					
		backgroundLabel = new JLabel("Background");
		foregroundLabel = new JLabel("Foreground");
		dividersLabel = new JLabel("Dividers");
		selectedLabel = new JLabel("Selected");
		lineColorLabel = new JLabel("Line Color");

		// initialize all the buttons
		background = new JButton("");
		foreground = new JButton("");
		dividers = new JButton("");
		selected = new JButton("");
		lineColor = new JButton("");
		
		// set the action listeners to all the buttons
		background.addActionListener(this);
		foreground.addActionListener(this);
		dividers.addActionListener(this);
		selected.addActionListener(this);
		lineColor.addActionListener(this);
		
		// set the background color to the appropriate Colors from COLOR class
		background.setBackground(Colors.background);
		foreground.setBackground(Colors.foreground);
		dividers.setBackground(Colors.dividers);
		selected.setBackground(Colors.selected);
		lineColor.setBackground(Colors.lineColor);
		
		// add the labels and buttons to the panel
		p.add(backgroundLabel);
		p.add(background);
		p.add(foregroundLabel);
		p.add(foreground);
		p.add(dividersLabel);
		p.add(dividers);
		p.add(selectedLabel);
		p.add(selected);
		p.add(lineColorLabel);
		p.add(lineColor);
		
		// add the panel to the frame
		frame.getContentPane().add(p);
		
		// set frame size (width = 1000 height = 700)
		frame.setSize(400,300);
		
		// make sure the frame is visible
		frame.setVisible(true); 
		
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if(obj == background)
		{
			makeAllFalse();
			backgroundChanged = true;
			createColorChooser();
		}
		else if (obj == selected)
		{
			makeAllFalse();
			selectedChanged = true;
			createColorChooser();
		}
		else if (obj == lineColor)
		{
			makeAllFalse();
			lineColorChanged = true;
			createColorChooser();
		}
		else if (obj == foreground)
		{
			makeAllFalse();
			foregroundColorChanged = true;
			createColorChooser();
		}
		else if (obj == dividers)
		{
			makeAllFalse();
			dividersColorChanged = true;
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
		backgroundChanged  = selectedChanged  = false;
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
			selected.setBackground(Colors.selected);
		}
		else if (backgroundChanged == true)
		{
			Colors.background = cc.getColor();
			background.setBackground(Colors.background);
		}
		else if (lineColorChanged == true)
		{
			Colors.lineColor = cc.getColor();
			lineColor.setBackground(Colors.lineColor);
		}
		
		else if (foregroundColorChanged == true)
		{
			Colors.foreground = cc.getColor();
			foreground.setBackground(Colors.foreground);
		}
		else if (dividersColorChanged == true)
		{
			Colors.dividers = cc.getColor();
			dividers.setBackground(Colors.dividers);
		}
		Core.getUI().redisplayCanvas();
	}
}
