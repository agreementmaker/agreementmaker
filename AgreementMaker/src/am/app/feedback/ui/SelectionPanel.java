package am.app.feedback.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

import com.ibm.icu.lang.UCharacter.JoiningGroup;

import am.Utility;
import am.app.feedback.FeedbackLoop;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentSet;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Node;
import am.userInterface.MatchingProgressDisplay;

public class SelectionPanel extends JPanel implements MatchingProgressDisplay, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -967696425990716259L;
	private FeedbackLoop ufl = null; // pointer to the user feedback loop
	
	
	
	// Start Screen
	JButton btn_start;
	JComboBox cmbHighThreshold;
	JComboBox cmbLowThreshold;
	JComboBox cmbCardinality;
	JComboBox cmbConfigurations;
	
	// Automatic Progress screen.
	JProgressBar progressBar;
	
	ButtonGroup radios;
	
	Alignment selectedMapping;
	AlignmentSet<Alignment> candidateMappings;
	
	
	public SelectionPanel(FeedbackLoop u) {	
		ufl = u;
	}
	
	
	
	public void showScreen_Start() {
		
		removeAll();
		btn_start = new JButton("Start");
		btn_start.addActionListener(this);
		btn_start.setActionCommand("btn_start");
		JLabel lblParameters = new JLabel("Parameters:");
		JLabel lblHighThreshold = new JLabel("High threshold:");
		JLabel lblLowThreshold = new JLabel("Low threshold:");
		JLabel lblCardinality = new JLabel("Cardinality:");
		JLabel lblConfiguration = new JLabel("Run configuration:");
		
		
		cmbHighThreshold = new JComboBox( Utility.getPercentDecimalsList() );
		cmbHighThreshold.setSelectedItem("0.7");
		cmbLowThreshold = new JComboBox( Utility.getPercentDecimalsList() );
		
		cmbCardinality = new JComboBox();
		cmbCardinality.addItem("1-1");
		
		cmbConfigurations = new JComboBox();
		cmbConfigurations.addItem("Manual");
		cmbConfigurations.addItem("Auto 101-303");
		
		
		

		
		setLayout( new FlowLayout() );
		
		add(btn_start);
		add(lblParameters);
		add(lblConfiguration);
		add(cmbConfigurations);
		add(lblHighThreshold);
		add(cmbHighThreshold);
		add(lblLowThreshold);
		add(cmbLowThreshold);
		add(lblCardinality);
		add(cmbCardinality);
		repaint();
		
	}



	public void actionPerformed(ActionEvent arg0) {
		
		// when a button on the user feedback loop java pane is pressed this is the action listener.
		if( arg0.getActionCommand() == "btn_start" ) {
			ufl.setProgressDisplay(this);
			displayProgressScreen();
			ufl.execute();
		} else if( arg0.getActionCommand() == "screen2_cancel" ) {
			ufl.cancel(true);
			showScreen_Start();
		} else if( arg0.getActionCommand() == "btn_correct" ) {
			// the user has selected a correct mapping
			if(radios.getSelection() != null){
				String selectedAlignment = radios.getSelection().getActionCommand();
				selectedMapping = candidateMappings.getAlignment( Integer.parseInt(selectedAlignment));
				displayProgressScreen();
				ufl.setExectionStage( FeedbackLoop.executionStage.afterUserInterface );
			}
		} else if( arg0.getActionCommand() == "btn_incorrect" ) {
			// the user cannot find any correct mappings
			selectedMapping = null;
			displayProgressScreen();
			ufl.setExectionStage( FeedbackLoop.executionStage.afterUserInterface );

		} else if( arg0.getActionCommand() == "btn_stop") {
			// the user has selected to stop the loop
			selectedMapping = null;
			removeAll();
			repaint();
			ufl.setExectionStage( FeedbackLoop.executionStage.presentFinalMappings );
		}
	}



	private void displayProgressScreen() {
		// TODO Auto-generated method stub
		removeAll();
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand("screen2_cancel");
		btnCancel.addActionListener(this);
		JLabel lblim = new JLabel("Running:");
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		setLayout( new FlowLayout() );
		add(lblim);
		add(progressBar);
		add(btnCancel);
		repaint();
	}



	public double getHighThreshold() {
		return Double.parseDouble( cmbHighThreshold.getSelectedItem().toString() );
	}
	
	public double getLowThreshold() {
		return Double.parseDouble( cmbLowThreshold.getSelectedItem().toString() );
	}



	public void appendToReport(String report) {
		// TODO: Add a report display in the panel.
	}



	public void matchingComplete() {
		// TODO Auto-generated method stub
		
		/*
		if( ufl.getStage() == FeedbackLoop.executionStage.runningInitialMatchers ) {
			// this means that the initial matchers have finished, advance the stage, and run the filters
			ufl.setStage(FeedbackLoop.executionStage.afterInitialMatchers);
			ufl.execute();
		}
		*/
		
	}


	/**
	 * Function that is called when the progress of the matchers
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public void displayMappings( AlignmentSet<Alignment> mappings) {
		
		
		candidateMappings = mappings;
		
		removeAll();
		
		
		JButton btn_correct = new JButton("Selected mapping is correct");
		btn_correct.setActionCommand("btn_correct");
		btn_correct.addActionListener(this);
		
		JButton btn_incorrect = new JButton("All the mappings are incorrect");
		btn_incorrect.setActionCommand("btn_incorrect");
		btn_incorrect.addActionListener(this);
		
		JButton btn_stop = new JButton("Stop the user feedback loop");
		btn_stop.setActionCommand("btn_stop");
		btn_stop.addActionListener(this);
		
		
		JLabel lbl_candidate = new JLabel("Candidate Mappings:");
		
		ArrayList<JRadioButton> mappingsRadios = new ArrayList<JRadioButton>();

		for( int i = 0; i < mappings.size(); i++ ) {
			Alignment cA = mappings.getAlignment(i);
			if( cA == null ) {
				System.out.println(" Null entries in the mappings matrix (the ones to display to the user)! SelectionPanel.java line 213");
				continue;
			}
			Node s = cA.getEntity1();
			Node t = cA.getEntity2();
			if( s == null || t == null ) {
				System.out.println("Bad alignments added to the list of candidate alignments.");
				continue;
			}
			JRadioButton jB = new JRadioButton( cA.getEntity1().toString() + " -> " + cA.getEntity2().toString() );
			jB.setActionCommand( Integer.toString(i) );
			mappingsRadios.add( jB );
		}
		
		radios = new ButtonGroup();
		
		for( int i = 0; i < mappingsRadios.size(); i++ ) {
			radios.add( mappingsRadios.get(i));
		}
		add(btn_correct);
		add(btn_incorrect);
		add(btn_stop);
		add(lbl_candidate);
		
		for( int i = 0; i < mappingsRadios.size(); i++ ) {
			add( mappingsRadios.get(i) );
		}
		
		//repaint( 500 );
		repaint();
	}



	public Alignment getUserMapping() {
		return selectedMapping;
	}



	public boolean isUserMappingClass() {
		if( selectedMapping.getAlignmentType() != null && selectedMapping.getAlignmentType() == alignType.aligningClasses ) return true;
		if( selectedMapping.getAlignmentType() == null ) System.out.println("Assertion Failed: isUserMappingClass().");
		return false;
	}



	
}
