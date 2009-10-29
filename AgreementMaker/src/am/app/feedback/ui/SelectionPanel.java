package am.app.feedback.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import am.Utility;
import am.app.feedback.FeedbackLoop;
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
		
		
		cmbHighThreshold = new JComboBox( Utility.getPercentStringList() );
		cmbLowThreshold = new JComboBox( Utility.getPercentStringList() );
		
		cmbCardinality = new JComboBox();
		cmbCardinality.addItem("1-1");
		
		cmbConfigurations = new JComboBox();
		cmbConfigurations.addItem("Default");
		
		
		

		
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
		
		
	}



	public void actionPerformed(ActionEvent arg0) {
		
		// when a button on the user feedback loop java pane is pressed this is the action listener.
		if( arg0.getActionCommand() == "btn_start" ) {
			// start button was pressed.
			
			displayProgressScreen();
			ufl.setProgressDisplay(this);
			ufl.execute();
		} else if( arg0.getActionCommand() == "screen2_cancel" ) {
			ufl.cancel(true);
			showScreen_Start();
		}
	}



	private void displayProgressScreen() {
		// TODO Auto-generated method stub
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setActionCommand("screen2_cancel");
		
		JLabel lblim = new JLabel("Initial Matchers:");
		
		progressBar = new JProgressBar();
		
		
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
	
	
	
}
