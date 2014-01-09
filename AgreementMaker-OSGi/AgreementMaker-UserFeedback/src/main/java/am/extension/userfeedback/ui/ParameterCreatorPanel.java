package am.extension.userfeedback.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.ui.utility.SettingsPanel;

public class ParameterCreatorPanel extends SettingsPanel {

	private static final long serialVersionUID = -4802836602740551085L;

	private JComboBox<Parameter> cmbParameters;
	private JTextField txtValue = new JTextField();
	
	public ParameterCreatorPanel() {
		super();
		
		cmbParameters = new JComboBox<>(Parameter.values());
		txtValue.setPreferredSize(new Dimension(200, cmbParameters.getPreferredSize().height));
		
		setLayout(new GridBagLayout());
		
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			add(cmbParameters, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 0;
			add(txtValue, c);
		}
		
	}
	
	public Parameter getParameter() {
		return (Parameter) cmbParameters.getSelectedItem();
	}
	
	public String getValue() {
		return txtValue.getText();
	}
}
