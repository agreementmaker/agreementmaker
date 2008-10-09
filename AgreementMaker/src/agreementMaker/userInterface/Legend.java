package agreementMaker.userInterface;

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

/**
 * @author Nalin
 *
 */
public class Legend implements ActionListener, ChangeListener{
	
	//buttons to change the color of the components of the user interface
	private JButton	background, selected, mappedByUser, mappedByUserAndSelected;
	//	 boolean values to keep track of which button button in key is changed
	public boolean backgroundChanged, mappedByUserChanged, mappedByContextChanged, selectedChanged, mappedByUserAndSelectedChanged;
	//create the labels 
	JLabel backgroundLabel,selectedLabel,mappedByUserLabel,mappedByUserAndSelectedLabel;
	protected JColorChooser cc;
	
	JFrame frame;
	private JButton	lineColor, mappedByUserLineColor, foreground, dividers;
	public boolean lineColorChanged, mappedByUserLineColorChanged, foregroundColorChanged, dividersColorChanged;
	
	JLabel lineColorLabel, mappedByUserLineColorLabel, foregroundLabel, dividersLabel;
	private JButton	mappedByContext, mappedByContextLineColor, mappedByContextAndSelected;
	public boolean mappedByContextAndSelectedChanged, mappedByContextLineColorChanged;
	
	JLabel mappedByContextLabel,mappedByContextAndSelectedLabel,mappedByContextLineColorLabel;
	JPanel p;
	UI ui;
	
	/**
	 * @param userInterface
	 */
	public Legend(UI userInterface){
		
		ui = userInterface;
		frame = new JFrame("Legend");
		p = new JPanel(new GridLayout(11,11));
		
		// initialize all the labels					
		backgroundLabel = new JLabel("Background");
		foregroundLabel = new JLabel("Foreground");
		dividersLabel = new JLabel("Dividers");
		selectedLabel = new JLabel("Selected");
		mappedByUserLabel = new JLabel("Mapped by user");     
		mappedByContextLabel = new JLabel("Mapped by Context");
		mappedByUserAndSelectedLabel = new JLabel("Mapped & Selected");
		mappedByContextAndSelectedLabel = new JLabel("Mapped by Context & Selected");
		lineColorLabel = new JLabel("Line Color");
		mappedByUserLineColorLabel = new JLabel("Mapped Line Color");
		mappedByContextLineColorLabel = new JLabel("Mapped by Context Line Color");
		
		// initialize all the buttons
		background = new JButton("");
		foreground = new JButton("");
		dividers = new JButton("");
		selected = new JButton("");
		mappedByUser = new JButton("");
		mappedByContext = new JButton("");
		mappedByUserAndSelected = new JButton("");
		mappedByContextAndSelected = new JButton("");
		lineColor = new JButton("");
		mappedByUserLineColor = new JButton("");
		mappedByContextLineColor = new JButton("");
		
		// set the action listeners to all the buttons
		background.addActionListener(this);
		foreground.addActionListener(this);
		dividers.addActionListener(this);
		selected.addActionListener(this);
		mappedByUser.addActionListener(this);
		mappedByContext.addActionListener(this);
		mappedByUserAndSelected.addActionListener(this);
		mappedByContextAndSelected.addActionListener(this);
		lineColor.addActionListener(this);
		mappedByUserLineColor.addActionListener(this);
		mappedByContextLineColor.addActionListener(this);
		
		// set the background color to the appropriate Colors from COLOR class
		background.setBackground(Colors.background);
		foreground.setBackground(Colors.foreground);
		dividers.setBackground(Colors.dividers);
		selected.setBackground(Colors.selected);
		mappedByUser.setBackground(Colors.mappedByUser);
		mappedByContext.setBackground(Colors.mappedByContext);
		mappedByUserAndSelected.setBackground(Colors.mappedByUserAndSelected);
		mappedByContextAndSelected.setBackground(Colors.mappedByContextAndSelected);
		lineColor.setBackground(Colors.lineColor);
		mappedByUserLineColor.setBackground(Colors.mappedByUserLineColor);
		mappedByContextLineColor.setBackground(Colors.mappedByContextLineColor);
		
		// add the labels and buttons to the panel
		p.add(backgroundLabel);
		p.add(background);
		p.add(foregroundLabel);
		p.add(foreground);
		p.add(dividersLabel);
		p.add(dividers);
		p.add(selectedLabel);
		p.add(selected);
		p.add(mappedByUserLabel);
		p.add(mappedByUser);
		p.add(mappedByContextLabel);
		p.add(mappedByContext);
		p.add(mappedByUserAndSelectedLabel);
		p.add(mappedByUserAndSelected);
		p.add(mappedByContextAndSelectedLabel);
		p.add(mappedByContextAndSelected);
		p.add(lineColorLabel);
		p.add(lineColor);
		p.add(mappedByUserLineColorLabel);
		p.add(mappedByUserLineColor);
		p.add(mappedByContextLineColorLabel);
		p.add(mappedByContextLineColor);
		
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
		else if (obj == mappedByUser)
		{
			makeAllFalse();
			mappedByUserChanged = true;
			createColorChooser();
		}
		else if (obj == mappedByContext)
		{
			makeAllFalse();
			mappedByContextChanged = true;
			createColorChooser();
		}
		else if (obj == mappedByUserAndSelected)
		{
			makeAllFalse();
			mappedByUserAndSelectedChanged = true;
			createColorChooser();
		}
		else if (obj == mappedByContextAndSelected)
		{
			makeAllFalse();
			mappedByContextAndSelectedChanged = true;
			createColorChooser();
		}
		else if (obj == lineColor)
		{
			makeAllFalse();
			lineColorChanged = true;
			createColorChooser();
		}
		else if (obj == mappedByUserLineColor)
		{
			makeAllFalse();
			mappedByUserLineColorChanged = true;
			createColorChooser();
		}
		else if (obj == mappedByContextLineColor)
		{
			makeAllFalse();
			mappedByContextLineColorChanged = true;
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
		lineColorChanged = mappedByUserLineColorChanged = foregroundColorChanged = dividersColorChanged = false;
		backgroundChanged = mappedByUserChanged = selectedChanged = mappedByUserAndSelectedChanged = false;
		mappedByContextChanged = mappedByContextAndSelectedChanged = mappedByContextLineColorChanged = false;
	}
	/*******************************************************************************************/
	/**
	 * This function defines stateChanged for JColorChooser
	 * @param e of type ChangeEvent
	 */
	public void stateChanged(ChangeEvent e)
	{
		if(mappedByUserChanged == true)
		{
			Colors.mappedByUser = cc.getColor();
			mappedByUser.setBackground(Colors.mappedByUser);
		}
		else if(selectedChanged == true)
		{
			Colors.selected = cc.getColor();
			selected.setBackground(Colors.selected);
		}
		else if(mappedByUserAndSelectedChanged == true)
		{
			Colors.mappedByUserAndSelected = cc.getColor();
			mappedByUser.setBackground(Colors.mappedByUserAndSelected);
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
		else if (mappedByUserLineColorChanged == true)
		{
			Colors.mappedByUserLineColor = cc.getColor();
			mappedByUserLineColor.setBackground(Colors.mappedByUserLineColor);
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
		else if(mappedByContextChanged == true)
		{
			Colors.mappedByContext = cc.getColor();
			mappedByContext.setBackground(Colors.mappedByContext);
		}
		else if (mappedByContextAndSelectedChanged == true)
		{
			Colors.mappedByContextAndSelected = cc.getColor();
			mappedByContextAndSelected.setBackground(Colors.mappedByContextAndSelected);
		}
		else if (mappedByContextLineColorChanged == true)
		{
			Colors.mappedByContextLineColor = cc.getColor();
			mappedByContextLineColor.setBackground(Colors.mappedByContextLineColor);
		}
		/*else if (mappedHighlightedLineColorChanged == true)
		 {
		 Colors.mappedHighlightedLineColor = cc.getColor();
		 mappedHighlightedLineColor.setBackground(Colors.mappedHighlightedLineColor);
		 }*/
		ui.getCanvas().repaint();
	}
}
