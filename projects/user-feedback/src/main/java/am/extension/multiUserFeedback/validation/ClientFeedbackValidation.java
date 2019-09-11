package am.extension.multiUserFeedback.validation;

import am.app.mappingEngine.Mapping;
import am.extension.collaborationClient.api.CollaborationFeedback.FeedbackValue;
import am.extension.collaborationClient.restful.RESTfulFeedback;
import am.extension.userfeedback.UserFeedback;
import am.extension.userfeedback.experiments.MLFExperiment;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.ui.ManualUserValidationPanel;

public class ClientFeedbackValidation extends UserFeedback{

	private Validation userFeedback;
	private Mapping candidateMapping;

	private UFLExperiment experiment;
	
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
		sendToServer(userFeedback);
		done();
	}
	
	@Override
	public void validate(UFLExperiment exp) {
		this.experiment = exp;
		candidateMapping = exp.candidateSelection.getSelectedMapping();
		if (candidateMapping!=null)
		{
			ManualUserValidationPanel validationPanel = new ManualUserValidationPanel(candidateMapping, exp);
			exp.gui.displayPanel(validationPanel);
		}	
	}
	
	/**
	 * Send the feedback back to the server.
	 * @param userFeedback
	 */
	private void sendToServer(Validation userFeedback)
	{
		if( experiment instanceof MLFExperiment ) {
			MLFExperiment e = (MLFExperiment) experiment;
			
			if( e.server == null && e.clientID == null ) {
				return; // no connection to server
			}
			
			RESTfulFeedback feedback = new RESTfulFeedback();
			feedback.setId(Long.toString(e.candidateMapping.getId()));
			switch( userFeedback ) {
			case CORRECT:
				feedback.setValue(FeedbackValue.CORRECT);
				break;
			case INCORRECT:
				feedback.setValue(FeedbackValue.INCORRECT);
				break;
			case SKIP:
				feedback.setValue(FeedbackValue.SKIP);
				break;
			case END_EXPERIMENT:
				feedback.setValue(FeedbackValue.END_EXPERIMENT);
				break;
			}
			
			e.server.putFeedback(e.clientID, feedback);
		}
		else {
			return;
		}
	}
}
