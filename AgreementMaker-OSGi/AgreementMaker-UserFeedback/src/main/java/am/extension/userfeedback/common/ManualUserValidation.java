package am.extension.userfeedback.common;

import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UserFeedback;
import am.extension.userfeedback.ui.ManualUserValidationPanel;

public class ManualUserValidation extends UserFeedback {
	
	private Validation userFeedback;
	private Mapping candidateMapping;

	@Override
	public Mapping getCandidateMapping() {
		return candidateMapping;
	}

	@Override
	public Validation getUserFeedback() {
		return userFeedback;
	}
	
	@Override
	public void setUserFeedback(Validation userFeedback) {
		this.userFeedback = userFeedback;
		done();
	}
	
	@Override
	public void validate(UFLExperiment exp) {
		candidateMapping = exp.candidateSelection.getCandidateMapping();
		
		ManualUserValidationPanel validationPanel = new ManualUserValidationPanel(candidateMapping, exp);
		
		exp.gui.displayPanel(validationPanel);
		
	}

}
