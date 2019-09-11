package am.extension.collaborationClient.api;

public interface CollaborationFeedback {

	public enum FeedbackValue {
		CORRECT,
		INCORRECT,
		SKIP,
		END_EXPERIMENT;
	}
	
	public String getId();
	
	public FeedbackValue getValue();
}
