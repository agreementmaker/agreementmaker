package agreementMaker.userInterface;



import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton; 
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//import com.hp.hpl.jena.query.extension.library.group;
import com.ibm.icu.lang.UCharacter.JoiningGroup;

import agreementMaker.GSM;
import agreementMaker.development.ReferenceEvaluation;
import agreementMaker.mappingEngine.DefnMappingOptions;
import agreementMaker.userInterface.vertex.VertexDescriptionPane;


/**
 * @author Nalin
 *
 */
public class DefnMappingOptionsDialog implements ActionListener{
	
	/**
	 * @param args
	 */
	
	private JButton cancel, run;
	private JLabel baseRadioL, dsiRadioL,sscRadioL, mcpL, thresholdL,numRelationsL, consultDictL;
	private JDialog frame;
	private JComboBox thresholdCombo, numRelationsCombo, mcpCombo;
	private JCheckBox consultDictCheck; 
	private JRadioButton dsiRadio, baseRadio, sscRadio;
	private UI ui;
	private ButtonGroup radios;
	private boolean success = false;
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public DefnMappingOptionsDialog(UI userInterface) {
		     
		ui = userInterface;
		frame = new JDialog();
		frame.setTitle("Run mapping by definition - Options ");
		
		run = new JButton("Run");
		cancel = new JButton("Cancel");

		run.addActionListener(this);
		cancel.addActionListener(this);
		
		baseRadioL = new JLabel ("Base Similarity");
		dsiRadioL = new JLabel ("DSI");
		sscRadioL = new JLabel ("SSC");
		mcpL = new JLabel ("MCP");
		thresholdL = new JLabel ("Threshold");
		numRelationsL = new JLabel ("Num of relation per source");
		consultDictL = new JLabel ("Consult dictionary");
		
		baseRadio = new JRadioButton();
		dsiRadio = new JRadioButton();
		dsiRadio.setSelected(true);
		sscRadio = new JRadioButton();
		radios = new ButtonGroup(); //to set radio button exclusive
		radios.add(baseRadio);
		radios.add(dsiRadio);
		radios.add(sscRadio);
		baseRadio.addActionListener(this);
		dsiRadio.addActionListener(this);
		sscRadio.addActionListener(this);
		
		consultDictCheck = new JCheckBox();
		
		String[] thresholdStrings = new String[20];
		for (int ii = 1; ii <= 20; ii++)
			thresholdStrings[ii - 1] = ii * 5 + "";
		thresholdCombo = new JComboBox(thresholdStrings);
		thresholdCombo.setSelectedIndex(14);//75%
		mcpCombo = new JComboBox(thresholdStrings);
		mcpCombo.setSelectedIndex(14);//75%
		
		String[] disLines = new String[100];
		for (int ii = 1; ii <= 100; ii++)
			disLines[ii - 1] = ii + "";
		numRelationsCombo = new JComboBox(disLines);
		
		
		

		//Make the GroupLayout for this dialog (somewhat complicated, but very flexible)
		// This Group layout lays the items in relation with eachother.  The horizontal
		// and vertical groups decide the relation between UI elements.
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		frame.getContentPane().setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		// Here we define the horizontal and vertical groups for the layout.
		// Both definitions are required for the GroupLayout to be complete.
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
					.addComponent(baseRadio) 
					.addComponent(baseRadioL) 
					.addComponent(dsiRadio) 	
					.addComponent(dsiRadioL)
					.addComponent(sscRadio)
					.addComponent(sscRadioL)
				)
				.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup()
									.addComponent(thresholdL)	
									.addComponent(numRelationsL)
									.addComponent(mcpL)
									.addComponent(consultDictL)
										
							)
							.addGroup(layout.createParallelGroup()
								.addComponent(thresholdCombo)
								.addComponent(numRelationsCombo)	
								.addComponent(mcpCombo)
								.addComponent(consultDictCheck)	
							)
					)
				.addGroup(layout.createSequentialGroup()
						.addComponent(cancel)
						.addComponent(run)
				)
		);

		// the Vertical group is the same structure as the horizontal group
		// but Sequential and Parallel definition are exchanged
		layout.setVerticalGroup(
				layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addComponent(baseRadio) 
						.addComponent(baseRadioL) 
						.addComponent(dsiRadio) 	
						.addComponent(dsiRadioL)
						.addComponent(sscRadio)
						.addComponent(sscRadioL)
					)
					.addGroup(layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(thresholdL)	
									.addComponent(thresholdCombo)	
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(numRelationsL)
								.addComponent(numRelationsCombo)	
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(mcpL)
									.addComponent(mcpCombo)
							)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
									.addComponent(consultDictL)
									.addComponent(consultDictCheck)	
							)
					)					
					.addGroup(layout.createParallelGroup()
							.addComponent(cancel)
							.addComponent(run)
					)
			);
 
		// end of Layout Code
		
		//frame.addWindowListener(new WindowEventHandler());//THIS SHOULD BE CHANGED THE PROGRAM SHOULD NOT CLOSE
		frame.pack(); // automatically set the frame size
		//set the width equals to title dimension
		FontMetrics fm = frame.getFontMetrics(frame.getFont());
		// +100 to allow for icon and "x-out" button
		int width = fm.stringWidth(frame.getTitle()) + 100;
		width = Math.max(width, frame.getPreferredSize().width);
		frame.setSize(new Dimension(width, frame.getPreferredSize().height));
		
		frame.setLocationRelativeTo(null); 	// center the window on the screen
		frame.setModal(true);
		
		frame.setVisible(true);
		
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */

	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if(obj == cancel){
			frame.setModal(false);
			frame.dispose();
		}
		else if(obj == baseRadio){//base similarity doesn't need the MCP value to be set
			mcpCombo.setEnabled(false);
		}
		else if(obj == dsiRadio || obj == sscRadio) {
			mcpCombo.setEnabled(true);
		}
		else if(obj == run){
			run();
		}
	}


	private void run() {
		DefnMappingOptions dmo = new DefnMappingOptions();
		dmo.threshold = Integer.parseInt(thresholdCombo.getSelectedItem().toString());
		dmo.numRel = Integer.parseInt(numRelationsCombo.getSelectedItem().toString());
		dmo.mcp =  Float.parseFloat(mcpCombo.getSelectedItem().toString()) /100;//it has to be in the format 0.75
		dmo.algorithm = DefnMappingOptions.BASE;
		if(dsiRadio.isSelected()) {
			dmo.algorithm = DefnMappingOptions.DSI;
		}
		else if(sscRadio.isSelected()) {
			dmo.algorithm = DefnMappingOptions.SSC;
		}
		dmo.consultDict = consultDictCheck.isSelected();
		ui.getCanvas().mapByDefn(dmo);
		frame.setModal(false);
		frame.dispose();
		success = true;
	}
	
	public boolean mappingSuccessed() {
		return success;
	}
	
}
