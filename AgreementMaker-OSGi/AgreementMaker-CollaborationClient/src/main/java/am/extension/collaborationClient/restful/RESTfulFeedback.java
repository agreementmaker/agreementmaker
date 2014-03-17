package am.extension.collaborationClient.restful;

import am.extension.collaborationClient.api.CollaborationFeedback;

public class RESTfulFeedback implements CollaborationFeedback {

	private String id;
	
	private FeedbackValue value;
	
	@Override
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public FeedbackValue getValue() {
		return value;
	}
	
	public void setValue(FeedbackValue value) {
		this.value = value;
	}

}
