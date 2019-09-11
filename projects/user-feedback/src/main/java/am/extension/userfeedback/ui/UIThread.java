package am.extension.userfeedback.ui;

import am.Utility;

public class UIThread extends Thread {

	public UIThread(Runnable target) {
        super(target);
    }
	
	@Override
	public void run() {
		try {
			super.run();
		} catch (Exception e) {
			e.printStackTrace();
			Utility.displayMessagePane(e.getMessage(), null);
		}
	}
	
}
