package am.app.userfeedbackloop;

import am.app.mappingEngine.Mapping;

public abstract class UserFeedback {

	public enum Validation { CORRECT, INCORRECT; }

	public abstract void validate( CandidateSelection cs );
	public abstract Validation getUserFeedback();
	public abstract Mapping getCandidateMapping();
	
}
