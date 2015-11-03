package am.extension.userfeedback.logic.api;

import am.extension.userfeedback.experiments.UFLExperiment;

public interface UFLControlLogic<T extends UFLExperiment> {

	public void runExperiment(T experiment);
}
