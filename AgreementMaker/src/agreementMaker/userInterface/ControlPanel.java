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

	/**
	 * 
	 */
	private static final long serialVersionUID = -2258009700001283026L;
	
	private Canvas canvas;
	private JComboBox displayLines;
	private JButton generateAgreementDocument;
	private JLabel thre = new JLabel("Similarity Threshold Value             ");
	private JLabel lines = new JLabel("Maximum Relations per Concept ");
	private JButton mappingByConsolidationButton;
	private JCheckBox mappingByConsolidationCheckBox;
	private JButton mappingByContextButton;
	private JCheckBox mappingByContextCheckBox;
	private JButton mappingByDefinitionButton;
	private JButton clearmappingByDefinitionButton;
	private JCheckBox mappingByDefinitionCheckBox;
	private JButton mappingByUserButton;
	private JCheckBox mappingByUserCheckBox;
	private JComboBox thresholdList;
	private UIMenu uiMenu;
	private JCheckBox useOfDict;
	private JLabel userLabel; 
	
	private JComboBox showDefMap;
	private JComboBox showContextMap;
	private JComboBox showConsMap;
	private JComboBox showUserMap;
	
	
	ControlPanel(UIMenu uiMenu, Canvas canvas) {
		this.uiMenu = uiMenu;
		this.canvas = canvas;
		init();
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == mappingByUserButton) {
			if (mappingByUserButton.getText() == "Show Mapping by User") {
				mappingByUserButton.setText("Hide Mapping by User");
				uiMenu.mapByUserSetSelected(true);
				mappingByUserCheckBox.setSelected(true);
				canvas.setMapByUser(true);
			} else if (mappingByUserButton.getText() == "Hide Mapping by User") {
				mappingByUserButton.setText("Show Mapping by User");
				uiMenu.mapByUserSetSelected(false);
				mappingByUserCheckBox.setSelected(false);
				canvas.setMapByUser(false);

			}
		} else if (obj == mappingByDefinitionButton) {
			// run mapping by definition
			int th = (thresholdList.getSelectedIndex() + 1) * 5;
			int line = displayLines.getSelectedIndex() + 1;
			boolean dictUse = useOfDict.isSelected();

			Date myDate = new Date();
			long start = myDate.getTime();
			canvas.mapByDefn(th, line, dictUse);
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
			
			// Refresh the Canvas with the results, if the user wants to see them.
			if(showDefMap.getSelectedIndex() == 0) { // the user has selected "Show Results"
				canvas.selectedDefnMapping();
				// canvas.repaint(); this is called by the selectedDefnMapping() function
			}
			
			// enable the clear button since we have run a mapping by definition
			clearmappingByDefinitionButton.setEnabled(true);
			
			
			
		} else if( obj == clearmappingByDefinitionButton ) {
			// user clicked the clear by definition button
			canvas.clearDefinitionMapping();
			clearmappingByDefinitionButton.setEnabled(false);
			canvas.repaint();
			
		} else if (obj == mappingByContextButton) {
			if (mappingByContextButton.getText() == "Show Mapping by Context") {
				mappingByContextButton.setText("Hide Mapping by Context");
				uiMenu.mapByContextSetSelected(true);
				mappingByContextCheckBox.setSelected(true);
				canvas.mapByContext();
			} else if (mappingByContextButton.getText() == "Hide Mapping by Context") {
				mappingByContextButton.setText("Show Mapping by Context");
				uiMenu.mapByContextSetSelected(false);
				mappingByContextCheckBox.setSelected(false);
				canvas.deselectedContextMapping();
			}

		} else if (obj == mappingByConsolidationButton) {
			if (mappingByConsolidationButton.getText() == "Show Mapping by Consolidation") {
				mappingByConsolidationButton
						.setText("Hide Mapping by Consolidation");
				uiMenu.mapByConsolidationSetSelected(true);
				mappingByConsolidationCheckBox.setSelected(true);
			} else if (mappingByConsolidationButton.getText() == "Hide Mapping by Consolidation") {
				uiMenu.mapByConsolidationSetSelected(false);
				mappingByConsolidationCheckBox.setSelected(false);
				mappingByConsolidationButton
						.setText("Show Mapping by Consolidation");
			}
		} else if (obj == generateAgreementDocument) {
			String fileName = JOptionPane
					.showInputDialog("Please enter a file name to save\nthe agreement document to: ");
			new DocumentProducer(canvas.getGlobalTreeRoot(), fileName);
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

		mappingByDefinitionCheckBox = new JCheckBox("");
		mappingByUserCheckBox = new JCheckBox("");
		mappingByContextCheckBox = new JCheckBox("");
		mappingByConsolidationCheckBox = new JCheckBox("");
		useOfDict = new JCheckBox("Consult Dictionary                                         ");//ends here
		mappingByDefinitionButton = new JButton("Run Mapping by Definition         ");
		clearmappingByDefinitionButton = new JButton("Clear"); // clear button next to "run mapping by definition" button
		clearmappingByDefinitionButton.setEnabled(false); // when the program starts, we have not computed any definition, so there is nothing to clear
		mappingByUserButton =       new JButton("Show Mapping by User         ");
		mappingByContextButton =  new JButton("Run Mapping by Context           ");
		mappingByConsolidationButton = new JButton(
				"Run Mapping by Consolidation");
		generateAgreementDocument = new JButton("View Agreement Document");

		userLabel = new JLabel("       Manual Mapping Layer   ");
		String[] thresholdStrings = new String[20];
		for (int ii = 1; ii <= 20; ii++)
			thresholdStrings[ii - 1] = ii * 5 + "";
		thresholdList = new JComboBox(thresholdStrings);

		String[] disLines = new String[100];
		for (int ii = 1; ii <= 100; ii++)
			disLines[ii - 1] = ii + "";
		displayLines = new JComboBox(disLines);

		String[] showHideDetails = new String[2];
		showHideDetails[0] = "Show Details";
		showHideDetails[1] = "Hide Details";
		
		showDefMap = new JComboBox(showHideDetails);
		showContextMap = new JComboBox(showHideDetails);
		showConsMap = new JComboBox(showHideDetails);
		showUserMap = new JComboBox(showHideDetails);
		
		
		
		
		// add a listener for the check boxes
		showDefMap.addItemListener(this);
		showContextMap.addItemListener(this);
		showConsMap.addItemListener(this);
		showUserMap.addItemListener(this);
		
		
		mappingByDefinitionCheckBox.addItemListener(this);
		mappingByUserCheckBox.addItemListener(this);
		mappingByContextCheckBox.addItemListener(this);
		mappingByConsolidationCheckBox.addItemListener(this);

		mappingByDefinitionButton.addActionListener(this);
		clearmappingByDefinitionButton.addActionListener(this);
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
		
		
		
		JPanel panelContextSet = new JPanel();
		panelContextSet.setLayout(new FlowLayout());
		//panelContextSet.add(mappingByContextCheckBox);
		panelContextSet.add(mappingByContextButton);
		panelContextSet.add(showContextMap);
		
		JPanel panelConsolidateSet = new JPanel();
		panelConsolidateSet.setLayout(new FlowLayout());
		//panelConsolidateSet.add(mappingByConsolidationCheckBox);
		panelConsolidateSet.add(mappingByConsolidationButton);
		panelConsolidateSet.add(showConsMap);

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
		panel9.add(useOfDict);

		JPanel panelDefSettings = new JPanel();
		panelDefSettings.setLayout(new BoxLayout(panelDefSettings, BoxLayout.Y_AXIS));
		panelDefSettings.add(panel7);
		panelDefSettings.add(panel8);
		panelDefSettings.add(panel9);
		panelDefSettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Definition Layer Settings"));
		
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
		//		displayOptionPane("action genated","action");
		Object obj = e.getItemSelectable();
		if (obj == mappingByConsolidationCheckBox) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				mappingByConsolidationCheckBox.setSelected(false);
				uiMenu.mapByConsolidationSetSelected(false);
				displayOptionPane("Mapping by Consolidation DESELECTEDs",
						"Mapping by Consolidation");
			} else {
				mappingByConsolidationCheckBox.setSelected(true);
				uiMenu.mapByConsolidationSetSelected(true);
				displayOptionPane("Mapping by Consolidation SELECTEDs",
						"Mapping by Consolidation");
			}
		} else if (obj == mappingByUserCheckBox) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				mappingByUserCheckBox.setSelected(false);
				uiMenu.mapByUserSetSelected(false);
				canvas.setMapByUser(false);
			} else {
				mappingByUserCheckBox.setSelected(true);
				uiMenu.mapByUserSetSelected(true);
				canvas.setMapByUser(true);
			}
		}

		else if (obj == mappingByContextCheckBox) {
			if (e.getStateChange() == ItemEvent.DESELECTED) {
				mappingByContextCheckBox.setSelected(false);
				uiMenu.mapByContextSetSelected(false);
				canvas.deselectedContextMapping();
			} else {
				mappingByContextCheckBox.setSelected(true);
				uiMenu.mapByContextSetSelected(true);
				// perform mapping by context by calling method in myCanvas class
				canvas.mapByContext();

			}
		} else if (obj == mappingByDefinitionCheckBox) {
			if (e.getStateChange() == ItemEvent.DESELECTED) ///hide lines
			{
				mappingByDefinitionCheckBox.setSelected(false);
				uiMenu.mapByDefinitionSetSelected(false);
				canvas.deselectedDefnMapping();

				
			} else {
				mappingByDefinitionCheckBox.setSelected(true);
				uiMenu.mapByDefinitionSetSelected(true);
				canvas.selectedDefnMapping();

			}
		}
		
		else if(obj==showContextMap) {
			int selection;
			selection = showContextMap.getSelectedIndex();
			if(selection ==0) {
				mappingByContextCheckBox.setSelected(true);
				uiMenu.mapByContextSetSelected(true);
			}
			else {
				mappingByContextCheckBox.setSelected(false);
				uiMenu.mapByContextSetSelected(false);
				canvas.deselectedContextMapping();
			}
			
		}
		
		else if(obj == showUserMap) {
			int selection;
			selection = showUserMap.getSelectedIndex();
			if(selection == 0) {
				mappingByUserCheckBox.setSelected(true);
				uiMenu.mapByUserSetSelected(true);
				canvas.setMapByUser(true);
				
			}
			else {
				mappingByUserCheckBox.setSelected(false);
				uiMenu.mapByUserSetSelected(false);
				canvas.setMapByUser(false);
				
			}
		}
		
		else if(obj == showDefMap) {
			int selection;
			selection = showDefMap.getSelectedIndex();
			if(selection==0) {
				
				mappingByDefinitionCheckBox.setSelected(true);
				uiMenu.mapByDefinitionSetSelected(true);
				canvas.selectedDefnMapping();

				
			}
			else { 
				mappingByDefinitionCheckBox.setSelected(false);
				uiMenu.mapByDefinitionSetSelected(false);
				canvas.deselectedDefnMapping();
			}
		}
		
		
	}

	/**
	 * @param consolidationMapping
	 */
	public void mappingByConsolidationCheckBoxSetSelected(
			boolean consolidationMapping) {
		mappingByConsolidationCheckBox.setSelected(consolidationMapping);
	}

	/**
	 * @param contextMapping
	 */
	public void mappingByContextCheckBoxSetSelected(boolean contextMapping) {
		mappingByContextCheckBox.setSelected(contextMapping);
	}

	/**
	 * @param definitionMapping
	 */
	public void mappingByDefinitionCheckBoxSetSelected(boolean definitionMapping) {
		mappingByDefinitionCheckBox.setSelected(definitionMapping);
	}

	/**
	 * @param userMapping
	 */
	public void mappingByUserCheckBoxSetSelected(boolean userMapping) {
		mappingByUserCheckBox.setSelected(userMapping);
	}

	/** 
	 * This method checks/unchecks the mapping by user checkbox based on the arugment
	 * @param userMapping boolean indicating if the mapping is done by the user
	 */
	public void setMappingByUser(boolean userMapping) {
		mappingByUserCheckBox.setSelected(userMapping);
		uiMenu.mapByUserSetSelected(userMapping);
	}
	
}
