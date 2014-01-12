package am.extension.userfeedback.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

import am.extension.userfeedback.UFLRegistry.CSEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.CandidateSelectionRegistry;
import am.extension.userfeedback.UFLRegistry.ExperimentRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackAggregationRegistry;
import am.extension.userfeedback.UFLRegistry.FeedbackPropagationRegistry;
import am.extension.userfeedback.UFLRegistry.InitialMatcherRegistry;
import am.extension.userfeedback.UFLRegistry.LoopInizializationRegistry;
import am.extension.userfeedback.UFLRegistry.PropagationEvaluationRegistry;
import am.extension.userfeedback.UFLRegistry.SaveFeedbackRegistry;
import am.extension.userfeedback.UFLRegistry.UserValidationRegistry;
import am.extension.userfeedback.experiments.UFLExperimentParameters;
import am.extension.userfeedback.experiments.UFLExperimentSetup;
import am.extension.userfeedback.preset.ExperimentPreset;
import am.ui.utility.SettingsDialog;
import am.ui.utility.SettingsPanel;

public class ExperimentPresetCreatorPanel extends SettingsPanel implements ActionListener {

	private static final long serialVersionUID = -5874022061082451870L;
	
	private static final Logger LOG = Logger.getLogger(ExperimentPresetCreatorPanel.class);
	
	private JLabel lblName = new JLabel("Experiment Name:");
	private JTextField txtName = new JTextField();
	
	private JLabel lblParameters = new JLabel("Parameters:");
	
	private JButton btnAddParameter = new JButton("add");
	private JButton btnDelParameter = new JButton("del");
	
	private JList<String> lstParameters = new JList<>();
	private JScrollPane scrParameters;
	
	private JLabel lblExperiment = new JLabel("UFL Experiment:");
	private JComboBox<ExperimentRegistry> cmbExperiment = new JComboBox<>(ExperimentRegistry.values());
	
	private JLabel lblInitialMatcher = new JLabel("Initial matcher:");
	private JComboBox<InitialMatcherRegistry> cmbInitialMatcher = new JComboBox<>(InitialMatcherRegistry.values());
	
	private JLabel lblLoopInit = new JLabel("Loop initialization:");
	private JComboBox<LoopInizializationRegistry> cmbLoopInit = new JComboBox<>(LoopInizializationRegistry.values());
	
	private JLabel lblCS = new JLabel("Candidate Selection:");
	private JComboBox<CandidateSelectionRegistry> cmbCS = new JComboBox<>(CandidateSelectionRegistry.values());
	
	private JLabel lblCSE = new JLabel("CS Evaluation:");
	private JComboBox<CSEvaluationRegistry> cmbCSE = new JComboBox<>(CSEvaluationRegistry.values());
	
	private JLabel lblUV = new JLabel("User Validation:");
	private JComboBox<UserValidationRegistry> cmbUV = new JComboBox<>(UserValidationRegistry.values());
	
	private JLabel lblFA = new JLabel("Feedback Agregation:");
	private JComboBox<FeedbackAggregationRegistry> cmbFA = new JComboBox<>(FeedbackAggregationRegistry.values());
	
	private JLabel lblFP = new JLabel("Feedback Propagation:");
	private JComboBox<FeedbackPropagationRegistry> cmbFP = new JComboBox<>(FeedbackPropagationRegistry.values());
	
	private JLabel lblPE = new JLabel("Propagation Evaluation:");
	private JComboBox<PropagationEvaluationRegistry> cmbPE = new JComboBox<>(PropagationEvaluationRegistry.values());
	
	private JLabel lblSF = new JLabel("Save Feedback:");
	private JComboBox<SaveFeedbackRegistry> cmbSF = new JComboBox<>(SaveFeedbackRegistry.values());
	
	private ExperimentPreset preset;
	
	public ExperimentPresetCreatorPanel(ExperimentPreset preset) {
		super();
		
		this.preset = preset;
		
		if( this.preset == null ) {
			this.preset = new ExperimentPreset("", new UFLExperimentSetup());
		}
		
		txtName.setText(this.preset.getName());
		txtName.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) { updateName(); }
			@Override public void removeUpdate(DocumentEvent e) { updateName(); }
			@Override public void changedUpdate(DocumentEvent e) { updateName(); }
			private void updateName() {
				ExperimentPresetCreatorPanel.this.preset.setName(txtName.getText());
			}
		});
		
		btnAddParameter.addActionListener(this);
		btnDelParameter.addActionListener(this);
		
		lstParameters.setVisibleRowCount(10);
		scrParameters = new JScrollPane(lstParameters);
		scrParameters.setPreferredSize(new Dimension(200, 200));
		
		if( this.preset.getExperimentSetup().parameters == null ) {
			this.preset.getExperimentSetup().parameters = new UFLExperimentParameters();
		}
		lstParameters.setListData(this.preset.getExperimentSetup().parameters.toStringList());
		
		if( this.preset.getExperimentSetup().exp != null ) cmbExperiment.setSelectedItem(this.preset.getExperimentSetup().exp);
		if( this.preset.getExperimentSetup().im != null ) cmbInitialMatcher.setSelectedItem(this.preset.getExperimentSetup().im);
		if( this.preset.getExperimentSetup().fli != null ) cmbLoopInit.setSelectedItem(this.preset.getExperimentSetup().fli);
		if( this.preset.getExperimentSetup().cs != null ) cmbCS.setSelectedItem(this.preset.getExperimentSetup().cs);
		if( this.preset.getExperimentSetup().cse != null ) cmbCSE.setSelectedItem(this.preset.getExperimentSetup().cse);
		if( this.preset.getExperimentSetup().uv != null ) cmbUV.setSelectedItem(this.preset.getExperimentSetup().uv);
		if( this.preset.getExperimentSetup().fa != null ) cmbFA.setSelectedItem(this.preset.getExperimentSetup().fa);
		if( this.preset.getExperimentSetup().fp != null ) cmbFP.setSelectedItem(this.preset.getExperimentSetup().fp);
		if( this.preset.getExperimentSetup().pe != null ) cmbPE.setSelectedItem(this.preset.getExperimentSetup().pe);
		if( this.preset.getExperimentSetup().sf != null ) cmbSF.setSelectedItem(this.preset.getExperimentSetup().sf);
		
		setBorder(new EmptyBorder(10,10,10,10));
		setLayout(new GridBagLayout());
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(5,0,5,0);
			add(lblName, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 0;
			c.gridwidth = 2;
			add(txtName, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(5,0,5,0);
			add(lblParameters, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(0,100,0,0);
			add(btnAddParameter, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 1;
			add(btnDelParameter, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 3;
			add(scrParameters, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 3;
			c.insets = new Insets(10,0,0,0);
			add(lblExperiment, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 3;
			c.gridwidth = 2;
			c.insets = new Insets(10,0,0,0);
			add(cmbExperiment, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 4;
			c.insets = new Insets(10,0,0,0);
			add(lblInitialMatcher, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 4;
			c.gridwidth = 2;
			c.insets = new Insets(10,0,0,0);
			add(cmbInitialMatcher, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 5;
			add(lblLoopInit, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 5;
			c.gridwidth = 2;
			add(cmbLoopInit, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 6;
			add(lblCS, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 6;
			c.gridwidth = 2;
			add(cmbCS, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 7;
			add(lblCSE, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 7;
			c.gridwidth = 2;
			add(cmbCSE, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 8;
			add(lblUV, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 8;
			c.gridwidth = 2;
			add(cmbUV, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 9;
			add(lblFA, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 9;
			c.gridwidth = 2;
			add(cmbFA, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 10;
			add(lblFP, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 10;
			c.gridwidth = 2;
			add(cmbFP, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 11;
			add(lblPE, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 11;
			c.gridwidth = 2;
			add(cmbPE, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 12;
			add(lblSF, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 12;
			c.gridwidth = 2;
			add(cmbSF, c);
		}
	}
	
	/**
	 * @return The experiment preset defined by this panel. The return can be
	 *         null if the name is empty after trimming the string.
	 */
	public ExperimentPreset getPreset() {
		String name = txtName.getText().trim();
		if( name.isEmpty() ) return null;
		
		preset.setName(name);
		
		preset.getExperimentSetup().exp = (ExperimentRegistry) cmbExperiment.getSelectedItem();
		
		preset.getExperimentSetup().im = (InitialMatcherRegistry) cmbInitialMatcher.getSelectedItem();
		preset.getExperimentSetup().fli = (LoopInizializationRegistry) cmbLoopInit.getSelectedItem();
		preset.getExperimentSetup().cs = (CandidateSelectionRegistry) cmbCS.getSelectedItem();
		preset.getExperimentSetup().cse = (CSEvaluationRegistry) cmbCSE.getSelectedItem();
		preset.getExperimentSetup().uv = (UserValidationRegistry) cmbUV.getSelectedItem();
		preset.getExperimentSetup().fa = (FeedbackAggregationRegistry) cmbFA.getSelectedItem();
		preset.getExperimentSetup().fp = (FeedbackPropagationRegistry) cmbFP.getSelectedItem();
		preset.getExperimentSetup().pe = (PropagationEvaluationRegistry) cmbPE.getSelectedItem();
		preset.getExperimentSetup().sf = (SaveFeedbackRegistry) cmbSF.getSelectedItem();
		
		return preset;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnAddParameter ) {
			ParameterCreatorPanel panel = new ParameterCreatorPanel();
			SettingsDialog d = new SettingsDialog(getDialog(), panel, "Add Parameter");
			d.setVisible(true);
			if( d.getStatus() == SettingsDialog.OK ) {
				preset.getExperimentSetup().parameters.setProperty(panel.getParameter().name(), panel.getValue());
				lstParameters.setListData(preset.getExperimentSetup().parameters.toStringList());
			}
		}
		
		if( e.getSource() == btnDelParameter ) {
			String value = lstParameters.getSelectedValue();
			String[] split = value.split(":");
			if( split.length == 0 ) {
				LOG.error("Could not retrieve parameter name using String split. Not deleting anything.");
				return;
			}
			preset.getExperimentSetup().parameters.remove(split[0]);
			lstParameters.setListData(preset.getExperimentSetup().parameters.toStringList());
		}
	}
	
}
