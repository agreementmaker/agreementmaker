package am.extension.userfeedback.logic;

import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.logic.api.AbstractUFLControlLogic;

public abstract class NonPersistentUFLControlLogic<T extends UFLExperiment> extends AbstractUFLControlLogic<T> {
	
	protected void runInitialMatchers() {
		// Run the initial matchers in a separate thread.
		try {
			experiment.initialMatcher = experiment.setup.im.getEntryClass().newInstance();
			experiment.initialMatcher.addActionListener(this);
			startThread(new Runnable(){
				@Override public void run() {
					experiment.initialMatcher.run(experiment);	
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void runInizialization() {
		// Run the initial matchers in a separate thread.
		try {
			experiment.dataInizialization = experiment.setup.fli.getEntryClass().newInstance();
			experiment.dataInizialization.addActionListener(this);
			startThread(new Runnable(){
				@Override public void run() {
					experiment.dataInizialization.initialize(experiment);	
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void runCandidateSelection() {
		try {
			experiment.candidateSelection = experiment.setup.cs.getEntryClass().newInstance();
			experiment.candidateSelection.addActionListener(this);
			
			startThread(new Runnable() {
				@Override public void run() {
					experiment.candidateSelection.rank(experiment);	
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	

	protected void runCandidateSelectionEvaluation() {
		try {
			experiment.csEvaluation = experiment.setup.cse.getEntryClass().newInstance();
			experiment.csEvaluation.addActionListener(this);

			startThread(new Runnable() {
				@Override public void run() {
					experiment.csEvaluation.evaluate(experiment);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void runUserValidation() {
		try {
			// have the user validate the candidate mapping
			experiment.userFeedback = experiment.setup.uv.getEntryClass().newInstance();

			experiment.userFeedback.addActionListener(this);
			startThread(new Runnable() {
				@Override public void run() {
					experiment.userFeedback.validate(experiment);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void runFeedbackAggregation() {
		try {
			experiment.feedbackAggregation = experiment.setup.fa.getEntryClass().newInstance();
			experiment.feedbackAggregation.addActionListener(this);

			startThread(new Runnable() {
				@Override
				public void run() {
					experiment.feedbackAggregation.addFeedback(experiment);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void runFeedbackPropagation() {
		try {
			experiment.feedbackPropagation = experiment.setup.fp.getEntryClass().newInstance();
			experiment.feedbackPropagation.addActionListener(this);

			startThread(new Runnable() {
				@Override
				public void run() {
					experiment.feedbackPropagation.propagate(experiment);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void runPropagationEvaluation() {
		try {
			// evaluate the propagation!
			experiment.propagationEvaluation = experiment.setup.pe.getEntryClass().newInstance();
			experiment.propagationEvaluation.addActionListener(this);

			startThread(new Runnable() {
				@Override
				public void run() {
					experiment.propagationEvaluation.evaluate(experiment);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void runSaveFeedback() {
		try {
			experiment.saveFeedback=experiment.setup.sf.getEntryClass().newInstance();
			experiment.candidateSelection.addActionListener(this);
			
			startThread(new Runnable() {
				@Override public void run() {
					experiment.saveFeedback.save(experiment);	
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected void runStatistic() {
		try {
			experiment.uflStatistics = experiment.setup.us.getEntryClass().newInstance();
			experiment.uflStatistics.addActionListener(this);

			startThread(new Runnable() {
				@Override public void run() {
					experiment.uflStatistics.compute(experiment);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
