package am.extension.collaborationEngine;

import java.util.ArrayList;
import java.util.List;

public class UserFeedbackRecord {

	public UserFeedbackRecord(int sourceSize, int targetSize) {
		sourceFeedback = new ArrayList<UserFeedback>(sourceSize);
		targetFeedback = new ArrayList<UserFeedback>(targetSize);
	}
	
	List<UserFeedback> sourceFeedback;
	List<UserFeedback> targetFeedback;

	public void addFeedback(UserFeedback fb) {
		sourceFeedback.add(fb.sourceKey, fb);
		targetFeedback.add(fb.targetKey, fb);
	}
	
}
