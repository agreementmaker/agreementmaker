package agreementMaker.userInterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import agreementMaker.agreementDocument.DocumentProducer;
import agreementMaker.userInterface.table.MatchersTablePanel;

public class ControlPanelTry extends JPanel implements ActionListener,
		ItemListener {

	private static final long serialVersionUID = -2258009700001283026L;
	
	private JComboBox displayLines;
	private JButton button1;
	private JButton button2;
	private JButton button3;
	private JLabel thre = new JLabel("Display similarity values >= than:  ");
	private UI ui;
	
	public final static int SHOW = 0;
	public final static int HIDE = 1;
	
	
	ControlPanelTry(UI ui, UIMenu uiMenu, Canvas canvas) {
		this.ui = ui;
		init();
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == button1) {
			String fileName = JOptionPane
					.showInputDialog("Please enter a file name to save\nthe agreement document to: ");
			new DocumentProducer(ui.getCanvas().getGlobalTreeRoot(), fileName);
		}
	}

	/**
	 * This function displays the JOptionPane with title and descritpion
	 *
	 * @param desc 		thedescription you want to display on option pane
	 * @param title 	the tile you want to display on option pane
	 */
	public void displayOptionPane(String desc, String title) {

		JOptionPane.showMessageDialog(null, desc, title,
				JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * 
	 */
	void init() {

				
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		//setAlignmentX(LEFT_ALIGNMENT);
		//setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);


		button1 = new JButton("View Agreement Document");
		button2 = new JButton("Button2");
		button3 = new JButton("Button3");

		String[] disLines = new String[100];
		for (int ii = 1; ii <= 100; ii++)
			disLines[ii - 1] = ii + "";
		displayLines = new JComboBox(disLines);
		displayLines.setSelectedIndex(disLines.length-1);
		displayLines.addItemListener(this);
		
		String[] showHideDetails = new String[2];
		showHideDetails[SHOW] = "Show Details";
		showHideDetails[HIDE] = "Hide Details";

		
		
		
		button1.addActionListener(this);


		button1.setPreferredSize(new Dimension(210, 20));

		button1.setMaximumSize(new Dimension(210, 20));

		JPanel panel1 = new JPanel();
		//panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS ));
		panel1.setLayout(new FlowLayout(FlowLayout.LEADING));
		//panel1.setAlignmentX(LEFT_ALIGNMENT);
		panel1.add(displayLines);
		panel1.add(button1);


		MatchersTablePanel panel2 = new MatchersTablePanel();
		
		JPanel panel3 = new JPanel();
		panel3.setLayout(new FlowLayout(FlowLayout.LEADING));
		//panel3.setAlignmentX(LEFT_ALIGNMENT);
		panel3.add(button2);
		panel3.add(button3);
		add(panel1);
		add(panel2);
		add(panel3);
		
	}

	/**
	 * This method takes care of the action perfromed by one of the check boxes
	 *
	 * @param e ActionEvent object
	 */
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getItemSelectable();
		
		if(obj == displayLines) {
			ui.getCanvas().setDisplayedLines(Integer.parseInt(displayLines.getSelectedItem().toString()));
			ui.getCanvas().repaint();
		}
	}
}
