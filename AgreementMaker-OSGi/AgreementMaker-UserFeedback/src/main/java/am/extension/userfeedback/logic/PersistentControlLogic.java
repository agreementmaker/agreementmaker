package am.extension.userfeedback.logic;

import am.extension.userfeedback.UFLRegistry.UFLStatisticRegistry;
import am.extension.userfeedback.experiments.UFLExperiment;

public abstract class PersistentControlLogic<T extends UFLExperiment> extends UFLControlLogic<T> {
	
	@Override
	protected void runInitialMatchers() {
		// Run the initial matchers in a separate thread.
		try {
			if( experiment.initialMatcher == null ) {
				experiment.initialMatcher = experiment.setup.im.getEntryClass().newInstance();
				experiment.initialMatcher.addActionListener(this);
			}
			startThread(new Runnable(){
				@Override public void run() {
					experiment.initialMatcher.run(experiment);	
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void runInizialization() {
		// Run the initial matchers in a separate thread.
		try {
			if( experiment.dataInizialization == null ) {
				experiment.dataInizialization = experiment.setup.fli.getEntryClass().newInstance();
				experiment.dataInizialization.addActionListener(this);
			}
			startThread(new Runnable(){ 
				@Override public void run() {
					experiment.dataInizialization.inizialize(experiment);	
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void runCandidateSelection() {
		try {
			if( experiment.candidateSelection == null ) {
				experiment.candidateSelection = experiment.setup.cs.getEntryClass().newInstance();
				experiment.candidateSelection.addActionListener(this);
			}
			startThread(new Runnable() {
				@Override public void run() {
					experiment.candidateSelection.rank(experiment);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void runCandidateSelectionEvaluation() {
		try {
			if( experiment.csEvaluation == null ) {
				experiment.csEvaluation = experiment.setup.cse.getEntryClass().newInstance();
				experiment.csEvaluation.addActionListener(this);
			}

			startThread(new Runnable() {
				@Override public void run() {
					experiment.csEvaluation.evaluate(experiment);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void runUserValidation() {
		try {
			if( experiment.userFeedback == null ) {
				// have the user validate the candidate mapping
				experiment.userFeedback = experiment.setup.uv.getEntryClass().newInstance();
				experiment.userFeedback.addActionListener(this);
			}
			startThread(new Runnable() {
				@Override public void run() {
					experiment.userFeedback.validate(experiment);
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void runFeedbackAggregation() {
		try {
			if( experiment.feedbackAggregation == null ) {
				experiment.feedbackAggregation = experiment.setup.fa.getEntryClass().newInstance();
				experiment.feedbackAggregation.addActionListener(this);
			}
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
	
	@Override
	protected void runFeedbackPropagation() {
		try {
			if( experiment.feedbackPropagation == null ) {
				experiment.feedbackPropagation = experiment.setup.fp.getEntryClass().newInstance();
				experiment.feedbackPropagation.addActionListener(this);
			}
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
	
	@Override
	protected void runPropagationEvaluation() {
		try {
			if( experiment.propagationEvaluation == null ) {
				// evaluate the propagation!
				experiment.propagationEvaluation = experiment.setup.pe.getEntryClass().newInstance();
				experiment.propagationEvaluation.addActionListener(this);
			}
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
	
	@Override
	protected void runSaveFeedback() {
		try {
			if( experiment.saveFeedback == null ) {
				experiment.saveFeedback = experiment.setup.sf.getEntryClass().newInstance();
				experiment.saveFeedback.addActionListener(this);
			}
			startThread(new Runnable() {
				@Override public void run() {
					experiment.saveFeedback.save(experiment);	
				}
			});
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void runStatistic() {
		try {
			if( experiment.uflStatistics == null ) {
				experiment.uflStatistics = UFLStatisticRegistry.ServerStatistics.getEntryClass().newInstance();
				experiment.uflStatistics.addActionListener(this);
			}
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
