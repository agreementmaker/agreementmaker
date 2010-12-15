package am.app.mappingEngine;

import javax.swing.JPanel;

public abstract class AbstractMatcherParametersPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -127558550285920273L;

	public AbstractMatcherParametersPanel() {
		super();
	}
	
	public AbstractParameters getParameters() {
		throw new RuntimeException("To be implemented in the real parameter class of the specific matcher");
	}
	
	/**
	 * This method is used to check parameters for errors.  If the users enters parameters that are 
	 * out of the range of normal entries (for example, entering letters where only numerical input is
	 * accepted, or entering 2.0 where the value should range from 0.0 - 1.0) this method must return
	 * a string with a descriptive error message.
	 * 
	 * If there are no errors in the parameters, the method must return null, or an empty string ("").
	 * @return Error message if there are errors or null if everything is ok.
	 */
	public String checkParameters() {
		//If there are any constraints to be satisfied by matcher params
		//check them overriding this method
		//if there are no errors in parameters selected then return null or "", 
		//else return the message to be shown to the user to correct errors
		return null;
	}

	/**
	 * This method is used to load preset parameters.
	 * @param presets The parameters that must be displayed by the parameters panel.
	 */
	public void loadParameters( AbstractParameters presets ) {
		throw new RuntimeException("This feature has not been implemented by this matcher's panel.");
	}
}
