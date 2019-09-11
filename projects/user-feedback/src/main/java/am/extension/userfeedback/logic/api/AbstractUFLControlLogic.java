package am.extension.userfeedback.logic.api;

import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;

import am.extension.userfeedback.experiments.UFLExperiment;

public abstract class AbstractUFLControlLogic<T extends UFLExperiment> implements UFLControlLogic<T>, ActionListener {

	protected T experiment;
	
	/**
	 * Starts a separate thread for running a piece of the experiment.
	 */
	protected void startThread(Runnable runnable) {
		Thread t = new Thread(runnable);
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				e.printStackTrace();
			}
		});
		t.start();
	}
}
