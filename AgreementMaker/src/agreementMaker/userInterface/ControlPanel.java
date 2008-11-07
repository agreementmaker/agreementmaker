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

public class ControlPanel extends JPanel implements ActionListener,
		ItemListener {

	private static final long serialVersionUID = -2258009700001283026L;
	
	private JComboBox displayLines;
	private JButton generateAgreementDocument;
	private JLabel thre = new JLabel("Display similarity values >= than:  ");
	private JLabel lines = new JLabel("Relations per source node to be displayed: ");
	private JButton mappingByConsolidationButton;
	private JButton clearmappingByConsolidationButton;
	private JButton mappingByContextButton;
	private JButton clearmappingByContextButton;
	private JButton mappingByDefinitionButton;
	private JButton clearmappingByDefinitionButton;
	private JButton mappingByUserButton;
	private JButton clearmappingByUserButton;
	private JComboBox thresholdList;
	private UI ui;
	private JLabel userLabel; 
	
	private JComboBox showDefMap;
	private JComboBox showContextMap;
	private JComboBox showConsMap;
	private JComboBox showUserMap;
	
	public final static int SHOW = 0;
	public final static int HIDE = 1;
	
	
	ControlPanel(UI ui, UIMenu uiMenu, Canvas canvas) {
		this.ui = ui;
		init();
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == mappingByUserButton) {
			if (mappingByUserButton.getText() == "Show Mapping by User") {
				mappingByUserButton.setText("Hide Mapping by User");
				ui.getCanvas().setMapByUser(true);
			} else if (mappingByUserButton.getText() == "Hide Mapping by User") {
				mappingByUserButton.setText("Show Mapping by User");
				ui.getCanvas().setMapByUser(false);
			}
		} else if (obj == mappingByDefinitionButton) {
			// run mapping by definition

			/* TIME INIT
			Date myDate = new Date();
			long start = myDate.getTime();
			*/
			System.out.println("before");
			DefnMappingOptionsDialog dmod = new DefnMappingOptionsDialog(ui);
			System.out.println("after");
			/* TO MEASURE RUNNING TIME
			myDate = new Date();
			long end = myDate.getTime();
			long timeTaken = end - start;
			// Let's format the time to make it look readable.
			double seconds = (double) timeTaken / 1000.00;
			double minutes = seconds / 60.0;
			double hours = minutes / 60.0;
			String timeElapsed = new String("");
			if(hours >= 1){
				timeElapsed += (int)(Math.floor(hours)) + " hour(s) ";
				timeElapsed += (int)(Math.floor(minutes) % 60) + " minute(s) ";
				timeElapsed += Math.floor(seconds) % 60 + " second(s)";
			} else if( minutes >= 1 ) {
				timeElapsed += (int)(Math.floor(minutes) % 60) + " minute(s) ";
				timeElapsed += Math.floor(seconds) % 60 + " second(s) ";
			} else {
				timeElapsed += seconds + " second(s) ";
			}
			JOptionPane.showMessageDialog(null, "Time: " + timeElapsed);
			*/
			if(dmod.mappingSuccessed()) {
				// Refresh the Canvas with the results, if the user wants to see them.
				if(showDefMap.getSelectedIndex() == 0) { // the user has selected "Show Results"
					ui.getCanvas().selectedDefnMapping();
					// canvas.repaint(); this is called by the selectedDefnMapping() function
				}
				JOptionPane.showMessageDialog(ui.getUIFrame(), "Mapping by definition completed");
				// enable the clear button since we have run a mapping by definition
				clearmappingByDefinitionButton.setEnabled(true);
			}

			
		} else if( obj == clearmappingByDefinitionButton ) {
			// user clicked the clear by definition button
			ui.getCanvas().clearDefinitionMapping();
			clearmappingByDefinitionButton.setEnabled(false);
			ui.getCanvas().repaint();
		} else if( obj == clearmappingByContextButton ) {
			// user clicked the clear button for context mappings
			ui.getCanvas().clearContextMapping();
			clearmappingByContextButton.setEnabled(false);
			ui.getCanvas().repaint();
		} else if (obj == mappingByContextButton) {
			// the user has clicked mappingByContextButton, to run the mapping by context
			// run the mapping
			showContextMap.setSelectedIndex(0); // we will automatically show the result
			ui.getCanvas().mapByContext();
			clearmappingByContextButton.setEnabled(true);

		} else if (obj == mappingByConsolidationButton) {
			if (mappingByConsolidationButton.getText() == "Show Mapping by Consolidation") {
				mappingByConsolidationButton
						.setText("Hide Mapping by Consolidation");
			} else if (mappingByConsolidationButton.getText() == "Hide Mapping by Consolidation") {
				mappingByConsolidationButton
						.setText("Show Mapping by Consolidation");
			}
		} else if (obj == generateAgreementDocument) {
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

				
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		mappingByDefinitionButton = new JButton("Run Mapping by Definition         ");
		clearmappingByDefinitionButton = new JButton("Clear"); // clear button next to "run mapping by definition" button
		clearmappingByDefinitionButton.setEnabled(false); // when the program starts, we have not computed any definition, so there is nothing to clear
		mappingByUserButton =       new JButton("Show Mapping by User         ");
		clearmappingByUserButton = new JButton("Clear");
		clearmappingByUserButton.setEnabled(false); // when the program starts, we have not computed any definition, so there is nothing to clear
		mappingByContextButton =  new JButton("Run Mapping by Context           ");
		clearmappingByContextButton = new JButton("Clear");
		clearmappingByContextButton.setEnabled(false); // when the program starts, we have not computed any definition, so there is nothing to clear
		mappingByConsolidationButton = new JButton("Run Mapping by Consolidation");
		clearmappingByConsolidationButton = new JButton("Clear");
		clearmappingByConsolidationButton.setEnabled(false);
		generateAgreementDocument = new JButton("View Agreement Document");

		userLabel = new JLabel("       Manual Mapping Layer   ");
		String[] thresholdStrings = new String[20];
		for (int ii = 1; ii <= 20; ii++)
			thresholdStrings[ii - 1] = ii * 5 + "";
		thresholdList = new JComboBox(thresholdStrings);
		thresholdList.addItemListener(this);

		String[] disLines = new String[100];
		for (int ii = 1; ii <= 100; ii++)
			disLines[ii - 1] = ii + "";
		displayLines = new JComboBox(disLines);
		displayLines.setSelectedIndex(disLines.length-1);
		displayLines.addItemListener(this);
		
		String[] showHideDetails = new String[2];
		showHideDetails[SHOW] = "Show Details";
		showHideDetails[HIDE] = "Hide Details";
		
		showDefMap = new JComboBox(showHideDetails);
		showContextMap = new JComboBox(showHideDetails);
		showConsMap = new JComboBox(showHideDetails);
		showUserMap = new JComboBox(showHideDetails);
		
		
		
		
		// add a listener for the check boxes
		showDefMap.addItemListener(this);
		showContextMap.addItemListener(this);
		showConsMap.addItemListener(this);
		showUserMap.addItemListener(this);
		
		

		mappingByDefinitionButton.addActionListener(this);
		clearmappingByDefinitionButton.addActionListener(this);
		clearmappingByContextButton.addActionListener(this);
		mappingByUserButton.addActionListener(this);
		mappingByContextButton.addActionListener(this);
		mappingByConsolidationButton.addActionListener(this);
		generateAgreementDocument.addActionListener(this);

		mappingByDefinitionButton.setPreferredSize(new Dimension(210, 20));
		mappingByUserButton.setPreferredSize(new Dimension(210, 20));
		userLabel.setPreferredSize(new Dimension(210, 20));
		mappingByContextButton.setPreferredSize(new Dimension(210, 20));
		mappingByConsolidationButton.setPreferredSize(new Dimension(210, 20));
		generateAgreementDocument.setPreferredSize(new Dimension(210, 20));

		mappingByDefinitionButton.setMaximumSize(new Dimension(210, 20));
		mappingByUserButton.setMaximumSize(new Dimension(210, 20));
		userLabel.setMaximumSize(new Dimension(210, 20));
		
		mappingByContextButton.setMaximumSize(new Dimension(210, 20));
		mappingByConsolidationButton.setMaximumSize(new Dimension(210, 20));
		generateAgreementDocument.setMaximumSize(new Dimension(210, 20));

		JPanel panelDefSet = new JPanel();
		panelDefSet.setLayout(new FlowLayout());
		//panelDefSet.add(mappingByDefinitionCheckBox);
		panelDefSet.add(mappingByDefinitionButton);
		panelDefSet.add(showDefMap);
		panelDefSet.add(clearmappingByDefinitionButton);
		
		
		
		JPanel panelUserSet = new JPanel();
		panelUserSet.setLayout(new FlowLayout());
		//panelUserSet.add(mappingByUserCheckBox);
		//panelUserSet.add(mappingByUserButton);
		panelUserSet.add(userLabel);
		panelUserSet.add(showUserMap);
		panelUserSet.add(clearmappingByUserButton);
		
		
		
		JPanel panelContextSet = new JPanel();
		panelContextSet.setLayout(new FlowLayout());
		//panelContextSet.add(mappingByContextCheckBox);
		panelContextSet.add(mappingByContextButton);
		panelContextSet.add(showContextMap);
		panelContextSet.add(clearmappingByContextButton);
		
		JPanel panelConsolidateSet = new JPanel();
		panelConsolidateSet.setLayout(new FlowLayout());
		//panelConsolidateSet.add(mappingByConsolidationCheckBox);
		panelConsolidateSet.add(mappingByConsolidationButton);
		panelConsolidateSet.add(showConsMap);
		panelConsolidateSet.add(clearmappingByConsolidationButton);

		JPanel panelMappingLayers = new JPanel(new GridLayout(4, 1));
		panelMappingLayers.add(panelDefSet);
		//panelMappingLayers.add(panelUserSet);
		panelMappingLayers.add(panelContextSet);
		panelMappingLayers.add(panelConsolidateSet);
		panelMappingLayers.add(panelUserSet);
		panelMappingLayers.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Mapping Layers"));

		JPanel panel7 = new JPanel();
		panel7.setLayout(new FlowLayout());
		panel7.add(thre);
		panel7.add(thresholdList);
		thre.setLabelFor(thresholdList);

		JPanel panel8 = new JPanel();
		panel8.setLayout(new FlowLayout());
		panel8.add(lines);
		panel8.add(displayLines);
		lines.setLabelFor(displayLines);

		JPanel panel9 = new JPanel();
		panel9.setLayout(new FlowLayout());

		JPanel panelDefSettings = new JPanel();
		panelDefSettings.setLayout(new BoxLayout(panelDefSettings, BoxLayout.Y_AXIS));
		panelDefSettings.add(panel7);
		panelDefSettings.add(panel8);
		panelDefSettings.add(panel9);
		panelDefSettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Display Settings"));
		
		JPanel panelAgreementDoc = new JPanel(new FlowLayout());
		panelAgreementDoc.add(generateAgreementDocument);
		panelAgreementDoc.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Agreement Document"));
		panelAgreementDoc.setPreferredSize(new Dimension(230, 60));
		
		//JPanel panelSubRightSide = new JPanel();
		//panelSubRightSide.setLayout(new BoxLayout(panelSubRightSide, BoxLayout.Y_AXIS));
		//panelSubRightSide.add(panelDefSettings);
		//panelSubRightSide.add(panelAgreementDoc);
		
		add(panelDefSettings);
		add(panelMappingLayers);
		//add(panelSubRightSide);

		add(panelAgreementDoc);
		//add(Box.createHorizontalGlue());
		//add(Box.createVerticalGlue());
		
	}

	/**
	 * This method takes care of the action perfromed by one of the check boxes
	 *
	 * @param e ActionEvent object
	 */
	public void itemStateChanged(ItemEvent e) {
		Object obj = e.getItemSelectable();
		if(obj==showContextMap) {
			int selection;
			selection = showContextMap.getSelectedIndex();
			if(selection ==0) {
				ui.getCanvas().selectedContextMapping();
			}
			else {
				ui.getCanvas().deselectedContextMapping();
			}
		}
		
		else if(obj == showUserMap) {
			int selection;
			selection = showUserMap.getSelectedIndex();
			if(selection == 0) {
				ui.getCanvas().setMapByUser(true);
			}
			else {
				ui.getCanvas().setMapByUser(false);
			}
		}
		
		else if(obj == showDefMap) {
			int selection;
			selection = showDefMap.getSelectedIndex();
			if(selection==0) {
				ui.getCanvas().selectedDefnMapping();		
			}
			else { 
				ui.getCanvas().deselectedDefnMapping();
			}
		}
		else if(obj == displayLines) {
			ui.getCanvas().setDisplayedLines(Integer.parseInt(displayLines.getSelectedItem().toString()));
			ui.getCanvas().repaint();
		}
		else if(obj == thresholdList) {
			ui.getCanvas().setDisplayedSimilarity(Integer.parseInt(thresholdList.getSelectedItem().toString()));
			ui.getCanvas().repaint();
		}
	}
}
