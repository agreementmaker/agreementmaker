package am.extension.userfeedback.common;

import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.UserFeedback.Validation;

public class ValidatedMapping extends Mapping {

	private static final long serialVersionUID = 260493073214603481L;
	
	private Validation validation;
	
	public ValidatedMapping(Mapping old, Validation validation) {
		super(old);
		this.validation = validation;
	}
	
	public Validation getValidation() { return this.validation; }

}
