package am.extension.userfeedback.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import am.app.Core;
import am.extension.userfeedback.preset.MatchingTaskPreset;
import am.ui.utility.SettingsPanel;

/**
 * A panel used to create a {@link MatchingTaskPreset}.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public class MatchingTaskPresetCreatorPanel extends SettingsPanel implements ActionListener {

	private static final long serialVersionUID = -6768698980954641825L;
	
	private JLabel lblTaskName = new JLabel("Task name:");
	private JLabel lblSourceOntologyFile = new JLabel("Source ontology:");
	private JLabel lblTargetOntologyfile = new JLabel("Target ontology:");
	
	private JTextField txtTaskName = new JTextField();
	private JTextField txtSourceOnt = new JTextField();
	private JTextField txtTargetOnt = new JTextField();
	private JTextField txtReference = new JTextField();
	
	private JButton btnSelectSource = new JButton("Browse ...");
	private JButton btnSelectTarget = new JButton("Browse ...");
	private JButton btnSelectReference = new JButton("Browse ...");
	
	private JCheckBox chkReference = new JCheckBox("Reference alignment:");
	
	public MatchingTaskPresetCreatorPanel(MatchingTaskPreset preset) {
		super();
		
		if( preset != null ) {
			if( preset.getName() != null ) {
				txtTaskName.setText(preset.getName());
			}
			if( preset.getSourceOntology() != null ) {
				txtSourceOnt.setText(preset.getSourceOntology());
			}
			if( preset.getTargetOntology() != null ) {
				txtTargetOnt.setText(preset.getTargetOntology());
			}
			chkReference.setSelected(preset.hasReference());
			if( chkReference.isSelected() ) {
				txtReference.setText(preset.getReference());
			}
		}
		
		txtSourceOnt.setPreferredSize(new Dimension(300, btnSelectSource.getPreferredSize().height));
		txtTargetOnt.setPreferredSize(new Dimension(300, btnSelectTarget.getPreferredSize().height));
		txtReference.setPreferredSize(new Dimension(300, btnSelectReference.getPreferredSize().height));
		
		btnSelectSource.addActionListener(this);
		btnSelectTarget.addActionListener(this);
		btnSelectReference.addActionListener(this);
		chkReference.addActionListener(this);
		
		txtReference.setEnabled(chkReference.isSelected());
		btnSelectReference.setEnabled(chkReference.isSelected());
		
		setBorder(new EmptyBorder(10,10,10,10));
		setLayout(new GridBagLayout());
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			add(lblTaskName, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 0;
			c.gridwidth = 2;
			add(txtTaskName, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			add(lblSourceOntologyFile, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(5, 10, 5, 10);
			add(txtSourceOnt, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 1;
			add(btnSelectSource, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			add(lblTargetOntologyfile, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 2;
			c.insets = new Insets(5, 10, 5, 10);
			add(txtTargetOnt, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 2;
			add(btnSelectTarget, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 3;
			add(chkReference, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 3;
			c.insets = new Insets(5, 10, 5, 10);
			add(txtReference, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 2;
			c.gridy = 3;
			add(btnSelectReference, c);
		}
		
	}
	
	/**
	 * @return The matching task defined by this panel. Can return null if
	 *         <ul>
	 *         <li>The task name is empty after removing whitespace.</li>
	 *         <li>The source or target ontology path doesn't point to existing
	 *         file.</li>
	 *         <li>If the task is designated as having a reference file and the
	 *         file is missing.</li>
	 *         </ul>
	 */
	public MatchingTaskPreset getPreset() {
		String name = txtTaskName.getText().trim();
		String sourceOnt = txtSourceOnt.getText();
		String targetOnt = txtTargetOnt.getText();
		String reference = txtReference.getText();
		
		if( name.isEmpty() ) return null;
		File sourceOntFile = new File(sourceOnt);
		if( !sourceOntFile.exists() )
			return null;
		
		File targetOntFile = new File(targetOnt);
		if( !targetOntFile.exists() )
			return null;
		
		File referenceFile = new File(reference);
		if( chkReference.isSelected() && !referenceFile.exists() )
			return null;
		
		if( chkReference.isSelected() ) {
			return new MatchingTaskPreset(name, sourceOnt, targetOnt, reference);
		}
		else {
			return new MatchingTaskPreset(name, sourceOnt, targetOnt);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnSelectSource ) {
			JFileChooser fc = new JFileChooser(Core.getInstance().getRoot());
			int result = fc.showOpenDialog(this);
			if( result == JFileChooser.APPROVE_OPTION ) {
				txtSourceOnt.setText(fc.getSelectedFile().getAbsolutePath());
			}
		}
		if( e.getSource() == btnSelectTarget ) {
			JFileChooser fc = new JFileChooser(Core.getInstance().getRoot());
			int result = fc.showOpenDialog(this);
			if( result == JFileChooser.APPROVE_OPTION ) {
				txtTargetOnt.setText(fc.getSelectedFile().getAbsolutePath());
			}
		}
		if( e.getSource() == chkReference ) {
			txtReference.setEnabled(chkReference.isSelected());
			btnSelectReference.setEnabled(chkReference.isSelected());
		}
		if( e.getSource() == btnSelectReference ) {
			JFileChooser fc = new JFileChooser(Core.getInstance().getRoot());
			int result = fc.showOpenDialog(this);
			if( result == JFileChooser.APPROVE_OPTION ) {
				txtReference.setText(fc.getSelectedFile().getAbsolutePath());
			}
		}
	}
	
	/* This entry point is used to preview the panel layout */
	public static void main(String[] args) {
		JPanel c = new MatchingTaskPresetCreatorPanel(null);
		
		JFrame fra = new JFrame("Create Matching Task Preset");
		fra.add(c);
		fra.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fra.pack();
		fra.setLocationRelativeTo(null);
		fra.setVisible(true);
	}
}
