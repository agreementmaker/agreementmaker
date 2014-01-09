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

import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.extension.userfeedback.UFLRegistry.ExperimentRegistry;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentSetup;
import am.extension.userfeedback.logic.UFLControlLogic;
import am.extension.userfeedback.preset.ExperimentPreset;
import am.extension.userfeedback.preset.MatchingTaskPreset;
import am.ui.UICore;
import am.ui.api.impl.AMTabSupportPanel;
import am.ui.utility.SettingsDialog;
import am.utility.Pair;
import am.utility.referenceAlignment.AlignmentUtilities;

public class UFLBatchModeGUI extends AMTabSupportPanel implements UFLProgressDisplay, ActionListener {

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
		
		if( runNumber < runs.size() ) {
			Runnable nextRun = new Runnable() {
				@Override public void run() {
					final Pair<MatchingTaskPreset,ExperimentPreset> currentRun = runs.get(runNumber);
					runNumber++;
										
					UFLExperimentSetup newSetup = currentRun.getRight().getExperimentSetup();
					
					// instantiate the experiment
					ExperimentRegistry experimentRegistryEntry = newSetup.exp;
					UFLExperiment newExperiment = null;
					try {
						newExperiment = experimentRegistryEntry.getEntryClass().newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						// exceptions caught, skip this run
						e.printStackTrace();
						UFLBatchModeGUI.this.nextRun();
						return;
					}
					
					// set experiment variables
					final MatchingTaskPreset task = currentRun.getLeft();
					newExperiment.setSourceOntology( OntoTreeBuilder.loadOWLOntology(task.getSourceOntology()) );
					newExperiment.setTargetOntology( OntoTreeBuilder.loadOWLOntology(task.getTargetOntology()) );
					
					Core.getInstance().setSourceOntology(newExperiment.getSourceOntology());
					Core.getInstance().setTargetOntology(newExperiment.getTargetOntology());
					
					if( task.hasReference() ) {
						Alignment<Mapping> alignment = AlignmentUtilities.getOAEIAlignment(
								task.getReference(), newExperiment.getSourceOntology(), newExperiment.getTargetOntology());
						newExperiment.setReferenceAlignment(alignment);
					}
					
					newExperiment.gui = UFLBatchModeGUI.this;
					newExperiment.setup = newSetup;

					// Step 1.  experiment is starting.  Initialize the experiment setup.
					final UFLControlLogic logic = newExperiment.getControlLogic();
					logic.runExperiment(newExperiment);
					
					while(!newExperiment.experimentHasCompleted()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							break;
						}
					}
					
					UFLBatchModeGUI.this.nextRun();
				}				
			};
			
			String run = (runNumber+1) + " of " + runs.size();
			LOG.info("Starting run " + run);
			proBar.setStringPainted(true);
			proBar.setString(run);
			proBar.setMaximum(runs.size());
			proBar.setValue(runNumber);
			
			Thread thread = new Thread(nextRun);
			thread.start();
		}
		else {
			runNumber = 0;
			runStatus = 0;
			btnRun.setEnabled(true);
			proBar.setValue(runs.size());
			proBar.setMaximum(runs.size());
		}
		
		
	}
	
	@Override public void matchingStarted(AbstractMatcher matcher) {}
	@Override public void matchingComplete() {}
	@Override public void clearReport() {}
	@Override public void appendToReport(String report) {}
	@Override public void scrollToEndOfReport() {}
	@Override public void setProgressLabel(String label) {}
	@Override public void setIndeterminate(boolean indeterminate) {}
	@Override public void ignoreComplete(boolean ignore) {}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
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
