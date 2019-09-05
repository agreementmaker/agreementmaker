package am.extension.userfeedback.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import am.extension.userfeedback.preset.ExperimentPreset;
import am.extension.userfeedback.preset.MatchingTaskPreset;
import am.extension.userfeedback.preset.PresetStorage;
import am.ui.utility.SettingsDialog;
import am.ui.utility.SettingsPanel;
import am.utility.Pair;

/**
 * A panel used to select a UFL experiment to run.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public class UFLExperimentRunnerPanel extends SettingsPanel implements ActionListener {

	private static final long serialVersionUID = -731818686187023449L;
	
	private JLabel lblMatchingTask = new JLabel("Matching Task:");
	private JLabel lblExperiemnt = new JLabel("UFL Experiment:");
	private JComboBox<MatchingTaskPreset> cmbMatchingTasks;
	private JComboBox<ExperimentPreset> cmbExperiments;
	
	private JButton btnAddMatchingTask = new JButton("add");
	private JButton btnDelMatchingTask = new JButton("del");
	private JButton btnEditMatchingTask = new JButton("edit");
	private JButton btnAddExperiment = new JButton("add");
	private JButton btnDelExperiment = new JButton("del");
	private JButton btnEditExperiment = new JButton("edit");
	
	public UFLExperimentRunnerPanel() {
		super();
		
		btnAddMatchingTask.setToolTipText("Add new matching task ...");
		btnAddMatchingTask.addActionListener(this);
		
		btnAddExperiment.setToolTipText("Add new experiment ...");
		btnAddExperiment.addActionListener(this);
		
		btnEditMatchingTask.addActionListener(this);
		
		btnDelMatchingTask.setToolTipText("Remove selected matching task");
		btnDelMatchingTask.addActionListener(this);
		
		btnDelExperiment.setToolTipText("Remove selected experiment");
		btnDelExperiment.addActionListener(this);
		
		btnEditExperiment.addActionListener(this);
		
		cmbMatchingTasks = new JComboBox<>(PresetStorage.getMatchingTaskPresets());
		cmbMatchingTasks.setMaximumSize(new Dimension(300,-1));
		cmbExperiments = new JComboBox<>(PresetStorage.getExperimentPresets());
		
		setLayout(new GridBagLayout());
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			add(lblMatchingTask, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 0;
			c.insets = new Insets(5,10,5,10);
			add(cmbMatchingTasks, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 0;
			c.insets = new Insets(0,0,0,5);
			add(btnAddMatchingTask, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 3;
			c.gridy = 0;
			add(btnDelMatchingTask, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 4;
			c.gridy = 0;
			add(btnEditMatchingTask, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			add(lblExperiemnt, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(5,10,5,10);
			add(cmbExperiments,c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 1;
			c.insets = new Insets(0,0,0,5);
			add(btnAddExperiment, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 3;
			c.gridy = 1;
			add(btnDelExperiment, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 4;
			c.gridy = 1;
			add(btnEditExperiment, c);
		}
	}
	
	public Pair<MatchingTaskPreset,ExperimentPreset> getRun() {
		return new Pair<>((MatchingTaskPreset)cmbMatchingTasks.getSelectedItem(), (ExperimentPreset)cmbExperiments.getSelectedItem());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnAddMatchingTask ) {
			// show the matching task dialog
			MatchingTaskPresetCreatorPanel panel = new MatchingTaskPresetCreatorPanel(null);
			SettingsDialog d = new SettingsDialog(getDialog(), panel);
			d.setVisible(true);
			if( d.getStatus() == SettingsDialog.OK ) {
				MatchingTaskPreset preset = panel.getPreset();
				if( preset != null ) {
					PresetStorage.addMatchingTaskPreset(preset);
					cmbMatchingTasks.setModel(new DefaultComboBoxModel<>(PresetStorage.getMatchingTaskPresets()));
					updateSize();
				}
			}
		}
		
		if( e.getSource() == btnDelMatchingTask ) {
			if( JOptionPane.showConfirmDialog(this, "Really delete the selected matching task?", "Delete?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
				PresetStorage.removeMatchingTaskPreset((MatchingTaskPreset)cmbMatchingTasks.getSelectedItem());
				cmbMatchingTasks.setModel(new DefaultComboBoxModel<>(PresetStorage.getMatchingTaskPresets()));
			}
		}
		
		if (e.getSource() == btnEditMatchingTask) {
			// show the matching task dialog
			MatchingTaskPresetCreatorPanel panel = new MatchingTaskPresetCreatorPanel((MatchingTaskPreset)cmbMatchingTasks.getSelectedItem());
			SettingsDialog d = new SettingsDialog(getDialog(), panel);
			d.setVisible(true);
			if (d.getStatus() == SettingsDialog.OK) {
				MatchingTaskPreset preset = panel.getPreset();
				if (preset != null) {
					PresetStorage.removeMatchingTaskPreset(preset);
					PresetStorage.addMatchingTaskPreset(preset);
					cmbMatchingTasks.setModel(new DefaultComboBoxModel<>(PresetStorage.getMatchingTaskPresets()));
				}
			}
		}
		
		if( e.getSource() == btnAddExperiment ) {
			ExperimentPresetCreatorPanel panel = new ExperimentPresetCreatorPanel(null);
			SettingsDialog d = new SettingsDialog(getDialog(), panel);
			d.setVisible(true);
			if( d.getStatus() == SettingsDialog.OK ) {
				ExperimentPreset preset = panel.getPreset();
				if( preset != null ) {
					PresetStorage.addExperimentPreset(preset);
					cmbExperiments.setModel(new DefaultComboBoxModel<>(PresetStorage.getExperimentPresets()));
					updateSize();
				}
			}
		}
		
		if( e.getSource() == btnDelExperiment ) {
			if( JOptionPane.showConfirmDialog(this, "Really delete the selected experiment?", "Delete?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ) {
				PresetStorage.removeExperimentPreset((ExperimentPreset)cmbExperiments.getSelectedItem());
				cmbExperiments.setModel(new DefaultComboBoxModel<>(PresetStorage.getExperimentPresets()));
			}
		}
		
		if( e.getSource() == btnEditExperiment ) {
			ExperimentPresetCreatorPanel panel = new ExperimentPresetCreatorPanel((ExperimentPreset)cmbExperiments.getSelectedItem());
			SettingsDialog d = new SettingsDialog(getDialog(), panel);
			d.setVisible(true);
			if( d.getStatus() == SettingsDialog.OK ) {
				ExperimentPreset preset = panel.getPreset();
				if( preset != null ) {
					PresetStorage.removeExperimentPreset(preset);
					PresetStorage.addExperimentPreset(preset);
					cmbExperiments.setModel(new DefaultComboBoxModel<>(PresetStorage.getExperimentPresets()));
				}
			}
		}
	}
	
	/* This entry point is used to preview the panel layout */
	public static void main(String[] args) {

		final JFrame d = new JFrame("Run UFL Experiment");
		JButton btnSettings = new JButton("Launch Settings ...");
		btnSettings.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				SettingsPanel ufl = new UFLExperimentRunnerPanel();
				SettingsDialog dialog = new SettingsDialog(d, ufl, "Run Experiment ...");
				dialog.setVisible(true);
			}
		});
		d.add(btnSettings);
		d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		d.pack();
		d.setLocationRelativeTo(null);
		d.setVisible(true);
	}
}
