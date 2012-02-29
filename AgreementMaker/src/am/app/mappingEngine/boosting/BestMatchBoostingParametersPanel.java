package am.app.mappingEngine.boosting;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.DefaultMatcherParameters;

/**
 * @author cpesquita
 * @date Sept 22, 2011
 */
public class BestMatchBoostingParametersPanel extends
		AbstractMatcherParametersPanel {
	
	private static final long serialVersionUID = -5181542248311471158L;
	
	private JLabel lblBoostFactor;
	private JTextField txtBoostFactor;
	private JCheckBox chkDeepCopy;
	
	public BestMatchBoostingParametersPanel(){
		super();
		
		// UI elements
		lblBoostFactor = new JLabel("Boost factor:");
		lblBoostFactor.setToolTipText("The multiplicative boost factor that similarities will be modified by.");
		
		txtBoostFactor = new JTextField("1.1");
		txtBoostFactor.setToolTipText("The multiplicative boost factor that similarities will be modified by.");
		
		chkDeepCopy = new JCheckBox("Deep copy of similarity matrix.");
		chkDeepCopy.setToolTipText("If false, operate on the input matcher's similarity matrix, otherwise allocate a whole new matrix and operate on that.");
		
		// UI Layout
		GroupLayout lay = new GroupLayout(this);
		lay.setAutoCreateContainerGaps(true);
		lay.setAutoCreateGaps(true);
		
		lay.setHorizontalGroup( lay.createParallelGroup() 
				.addGroup( lay.createSequentialGroup()
						.addComponent(lblBoostFactor)
						.addComponent(txtBoostFactor)
				)
				.addComponent(chkDeepCopy)
		);
		
		lay.setVerticalGroup( lay.createSequentialGroup()
				.addGroup( lay.createParallelGroup()
						.addComponent(lblBoostFactor)
						.addComponent(txtBoostFactor)
				)
				.addComponent(chkDeepCopy)
		);
		
		setLayout(lay);
		
	}

	@Override
	public String checkParameters() {
		
		double boostFactor = 1.1;
		try {
			boostFactor = Double.parseDouble(txtBoostFactor.getText());
		} catch ( NumberFormatException ex ) {
			return "Cannot parse the boost factor.  Please make sure it is a positive decimal number.";
		}
		
		if( boostFactor < 0.0d ) {
			return "The boost factor cannot be negative. Please change the boost factor.";
		}
		
		return null; // everything is ok.
	}
	
	@Override
	public DefaultMatcherParameters getParameters() {
		BestMatchBoostingParameters parameters = new BestMatchBoostingParameters();
		
		parameters.boostPercent = Double.parseDouble(txtBoostFactor.getText());
		parameters.deepCopy = chkDeepCopy.isSelected();
		
		return parameters;
	}

	

}
