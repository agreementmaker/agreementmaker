package am.app.mappingEngine.oaei.oaei2011;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.oaei.oaei2011.OAEI2011MatcherParameters.OAEI2011Configuration;

public class OAEI2011MatcherParametersPanel extends AbstractMatcherParametersPanel implements ActionListener {

	private static final long serialVersionUID = -7652636660460034435L;

	private JRadioButton radAutomatic, radManual;
	private JComboBox cmbConfiguration;
	private JCheckBox chkShowAll;
	private JCheckBox chkParallelExecution;
	
	/*
	 * The constructor creates the GUI elements and adds 
	 * them to this panel.  It also creates the parameters object.
	 * 
	 */
	public OAEI2011MatcherParametersPanel() {
		super();
		
		// initialize the GUI elements.
		
		radAutomatic = new JRadioButton("Automatic configuration.");
		radAutomatic.addActionListener(this);
		radAutomatic.setSelected(true);
		radManual = new JRadioButton("Manual override.");
		radManual.addActionListener(this);
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(radAutomatic);
		btnGroup.add(radManual);
		
		cmbConfiguration = new JComboBox();
		for( OAEI2011Configuration config : OAEI2011Configuration.values() ) {
			cmbConfiguration.addItem(config);
		}
		cmbConfiguration.setEnabled(false);
		
		chkShowAll = new JCheckBox("Show intermediate matchers.");
		chkParallelExecution = new JCheckBox("Parallel Execution.");
		
		// layout
		
		GroupLayout lay = new GroupLayout(this);
		
		lay.setAutoCreateContainerGaps(true);
		lay.setAutoCreateGaps(true);
		
		lay.setHorizontalGroup( lay.createParallelGroup()
				.addComponent(radAutomatic)
				.addComponent(radManual)
				.addComponent(cmbConfiguration)
				.addComponent(chkShowAll)
				.addComponent(chkParallelExecution)
		);
		
		lay.setVerticalGroup( lay.createSequentialGroup()
				.addComponent(radAutomatic)
				.addGap(5)
				.addComponent(radManual)
				.addComponent(cmbConfiguration)
				.addGap(10)
				.addComponent(chkShowAll)
				.addComponent(chkParallelExecution)
		);
		
		setLayout(lay);
		
	}
	
		
	public AbstractParameters getParameters() {
		OAEI2011MatcherParameters parameters = new OAEI2011MatcherParameters();
		
		parameters.automaticConfiguration = radAutomatic.isSelected();
		if( !parameters.automaticConfiguration ) {
			parameters.selectedConfiguration = (OAEI2011Configuration) cmbConfiguration.getSelectedItem();
		}
		
		parameters.showIntermediateMatchers = chkShowAll.isSelected();
		parameters.parallelExecution = chkParallelExecution.isSelected();
		
		return parameters;
	}
	
	public String checkParameters() {
		return null;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == radAutomatic ) {
			cmbConfiguration.setEnabled(false);
		}
		if( e.getSource() == radManual ) {
			cmbConfiguration.setEnabled(true);
		}
		
	}
}
