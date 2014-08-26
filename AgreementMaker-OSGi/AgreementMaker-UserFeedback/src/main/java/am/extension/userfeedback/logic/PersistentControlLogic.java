package am.extension.userfeedback.logic;

import am.extension.multiUserFeedback.storage.FeedbackAgregation;
import am.extension.userfeedback.ExecutionSemantics;
import am.extension.userfeedback.SaveFeedback;
import am.extension.userfeedback.UFLRegistry.UFLStatisticRegistry;
import am.extension.userfeedback.UFLStatistics;
import am.extension.userfeedback.UserFeedback;
import am.extension.userfeedback.evaluation.CandidateSelectionEvaluation;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.inizialization.FeedbackLoopInizialization;
import am.extension.userfeedback.logic.api.AbstractUFLControlLogic;
import am.extension.userfeedback.propagation.FeedbackPropagation;
import am.extension.userfeedback.selection.CandidateSelection;

public abstract class PersistentControlLogic<T extends UFLExperiment> extends AbstractUFLControlLogic<T> {
	
	protected void runInitialMatchers() {
		// Run the initial matchers in a separate thread.
		try {
			if( experiment.initialMatcher == null ) {
	
				Class<? extends ExecutionSemantics> c = experiment.setup.im.getEntryClass();
				experiment.info("Instantiating InitialMatchers: " + c.getName());
				experiment.initialMatcher = c.newInstance();
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
	
	protected void runInizialization() {
		// Run the initial matchers in a separate thread.
		try {
			if( experiment.dataInizialization == null ) {
				Class<? extends FeedbackLoopInizialization> c = experiment.setup.fli.getEntryClass();
				experiment.info("Instantiating FeedbackLoopInitialization: " + c.getName());
				experiment.dataInizialization = c.newInstance();
				experiment.dataInizialization.addActionListener(this);
			}
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
			if( experiment.candidateSelection == null ) {
				Class<? extends CandidateSelection> c = experiment.setup.cs.getEntryClass();
				experiment.info("Instantiating CandidateSelection: " + c.getName());
				experiment.candidateSelection = c.newInstance();
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
	
	protected void runCandidateSelectionEvaluation() {
		try {
			if( experiment.csEvaluation == null ) {
				Class<? extends CandidateSelectionEvaluation> c = experiment.setup.cse.getEntryClass();
				experiment.info("Instantiating CandidateSelectionEvaluation: " + c.getName());
				experiment.csEvaluation = c.newInstance();
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
	
	protected void runUserValidation() {
		try {
			if( experiment.userFeedback == null ) {
				// have the user validate the candidate mapping
				Class<? extends UserFeedback> c = experiment.setup.uv.getEntryClass();
				experiment.info("Instantiating UserFeedback: " + c.getName());
				experiment.userFeedback = c.newInstance();
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
	
	protected void runFeedbackAggregation() {
		try {
			if( experiment.feedbackAggregation == null ) {
				Class<? extends FeedbackAgregation> c = experiment.setup.fa.getEntryClass();
				experiment.info("Instantiating FeedbackAgregation: " + c.getName());
				experiment.feedbackAggregation = c.newInstance();
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
	
	protected void runFeedbackPropagation() {
		try {
			if( experiment.feedbackPropagation == null ) {
				Class<? extends FeedbackPropagation> c = experiment.setup.fp.getEntryClass();
				experiment.info("Instantiating FeedbackAgregation: " + c.getName());
				experiment.feedbackPropagation = c.newInstance();
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
	
	protected void runPropagationEvaluation() {
		try {
			if( experiment.propagationEvaluation == null ) {
				// evaluate the propagation!
				Class<? extends PropagationEvaluation> c = experiment.setup.pe.getEntryClass();
				experiment.info("Instantiating FeedbackAgregation: " + c.getName());
				experiment.propagationEvaluation = c.newInstance();
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
	
	protected void runSaveFeedback() {
		try {
			if( experiment.saveFeedback == null ) {
				Class<? extends SaveFeedback> c = experiment.setup.sf.getEntryClass();
				experiment.info("Instantiating FeedbackAgregation: " + c.getName());
				experiment.saveFeedback = c.newInstance();
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
	
	protected void runStatistic() {
		try {
			if( experiment.uflStatistics == null ) {
				Class<? extends UFLStatistics> c = UFLStatisticRegistry.ServerStatistics.getEntryClass();
				experiment.info("Instantiating FeedbackAgregation: " + c.getName());
				experiment.uflStatistics = c.newInstance();
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
