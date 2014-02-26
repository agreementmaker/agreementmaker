package am.extension.userfeedback.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.extension.userfeedback.common.UFLExperimentRunner;
import am.extension.userfeedback.preset.ExperimentPreset;
import am.extension.userfeedback.preset.MatchingTaskPreset;
import am.ui.UICore;
import am.ui.api.impl.AMTabSupportPanel;
import am.ui.utility.SettingsDialog;
import am.utility.Pair;
import am.utility.RunTimer;

public class UFLBatchModeGUI extends AMTabSupportPanel implements UFLProgressDisplay, ActionListener, ChangeListener {

	private static final long serialVersionUID = 5505967642655168076L;

	private static final Logger LOG = Logger.getLogger(UFLBatchModeGUI.class);
	
	private JLabel lblRuns = new JLabel("Batch mode runs: ");
	private JLabel lblRunsDefined = new JLabel("0 runs defined");
	private JButton btnSetup = new JButton("Select Runs");
	private JButton btnRun = new JButton("Run Experiments");
	
	private List<Pair<MatchingTaskPreset,ExperimentPreset>> runs;
	
	private JProgressBar proBar = new JProgressBar();
	
	private int runStatus = 0; // 0 stopped, 1 = running
	private int runNumber = 0;
	
	private RunTimer timer = new RunTimer();

	private String logLabel;
	private int progress;

	private AbstractMatcher matcherRunning;
	
	public UFLBatchModeGUI() {
		super("UFL Batch Mode");
		
		btnSetup.addActionListener(this);
		btnRun.addActionListener(this);
		
		setLayout(new GridBagLayout());
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			add(lblRuns, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 0;
			add(btnSetup, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			add(lblRunsDefined, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 1;
			add(btnRun, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 2;
			add(proBar, c);
		}
	}

	public void nextRun() {
		runStatus = 1; // running
		
		if( runs != null && runNumber < runs.size() ) {
			final Pair<MatchingTaskPreset,ExperimentPreset> currentRun = runs.get(runNumber);
			UFLExperimentRunner runner = new UFLExperimentRunner(currentRun);
			runner.addChangeListener(this);
			
			String run = (runNumber+1) + " of " + runs.size();
			LOG.info("Starting run " + run);
			proBar.setStringPainted(true);
			proBar.setString(run);
			proBar.setMaximum(runs.size());
			proBar.setValue(runNumber);
			
			runNumber++;
			Thread thread = new Thread(runner);
			thread.start();
		}
		else {
			runNumber = 0;
			runStatus = 0;
			btnRun.setEnabled(true);
			if( runs != null ) {
				proBar.setValue(runs.size());
				proBar.setMaximum(runs.size());
				LOG.info("Completed " + runs.size() + " runs in " + timer);
			}
			else {
				LOG.info("No runs selected.");
			}
			timer.stop();
			
		}
		
		
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// these events are only sent by the ExperimentRunner
		// it means it's time for the next run.
		nextRun();
	}
	
	@Override public void matchingStarted(AbstractMatcher matcher) {
		this.matcherRunning = matcher;
	}
	@Override public void matchingComplete() {
		LOG.info(logLabel + " finished in " + Utility.getFormattedTime(matcherRunning.getRunningTime()));
	}
	@Override public void clearReport() {}
	@Override public void appendToReport(String report) {
		//LOG.info(report);
	}
	@Override public void scrollToEndOfReport() {}
	@Override public void setProgressLabel(String label) {
		this.logLabel = label;
	}
	@Override public void setIndeterminate(boolean indeterminate) {}
	@Override public void ignoreComplete(boolean ignore) {}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if( evt.getPropertyName().equals("progress") ) {
			LOG.info(logLabel + " progress: " + evt.getNewValue().toString() );
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnSetup ) {
			
			boolean fakeFrame = false;
			JFrame frame = null;
			if( UICore.getUI() != null ) {
				frame = UICore.getUI().getUIFrame();
			}
			if( frame == null ) {
				frame = new JFrame("Run UFL Experiment");
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				fakeFrame = true;
			}
			
			UFLBatchModePanel panel = new UFLBatchModePanel();
			SettingsDialog dialog = new SettingsDialog(frame, panel);
			dialog.setVisible(true);
			if( dialog.getStatus() == SettingsDialog.OK ) {
				runs = panel.getRuns();
				lblRunsDefined.setText(runs.size() + " runs defined.");
			}
			
			if( fakeFrame ) {
				frame.setVisible(false);
				frame.dispose();
			}
		}
		
		if( e.getSource() == btnRun ) {
			if( runStatus == 0 ) {
				btnRun.setEnabled(false);
				runNumber = 0;
				timer.resetAndStart();
				nextRun(); 
			}
		}
	}
	
	/* Test entrypoint */
	public static void main(String[] args)
	{
		JDialog newFrame = new JDialog();
		newFrame.setModal(true); // stop execution on setVisible(true) until the dialog is closed.
		newFrame.setLayout(new BorderLayout());
		newFrame.add(new UFLBatchModeGUI(), BorderLayout.CENTER);
		newFrame.pack();  newFrame.setLocationRelativeTo(null); newFrame.setVisible(true);
		
		System.exit(0);
	}

	@Override
	public void displayPanel(JPanel panel) {
		throw new RuntimeException("Not implemented.");
	}
}
