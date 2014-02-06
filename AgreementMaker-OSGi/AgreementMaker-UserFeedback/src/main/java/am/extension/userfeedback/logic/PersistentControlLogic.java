package am.extension.userfeedback.logic;

import am.extension.userfeedback.UFLRegistry.UFLStatisticRegistry;
import am.extension.userfeedback.experiments.UFLExperiment;

public abstract class PersistentControlLogic<T extends UFLExperiment> extends UFLControlLogic<T> {
	
	public PersistentControlLogic() {
		super();
		
		try {
			experiment.initialMatcher = experiment.setup.im.getEntryClass().newInstance();
			experiment.initialMatcher.addActionListener(this);
			experiment.dataInizialization = experiment.setup.fli.getEntryClass().newInstance();
			experiment.dataInizialization.addActionListener(this);
			experiment.candidateSelection = experiment.setup.cs.getEntryClass().newInstance();
			experiment.candidateSelection.addActionListener(this);
			experiment.csEvaluation = experiment.setup.cse.getEntryClass().newInstance();
			experiment.csEvaluation.addActionListener(this);
			experiment.userFeedback = experiment.setup.uv.getEntryClass().newInstance();
			experiment.userFeedback.addActionListener(this);
			experiment.feedbackAggregation = experiment.setup.fa.getEntryClass().newInstance();
			experiment.feedbackAggregation.addActionListener(this);
			experiment.feedbackPropagation = experiment.setup.fp.getEntryClass().newInstance();
			experiment.feedbackPropagation.addActionListener(this);
			experiment.propagationEvaluation = experiment.setup.pe.getEntryClass().newInstance();
			experiment.propagationEvaluation.addActionListener(this);
			experiment.saveFeedback = experiment.setup.sf.getEntryClass().newInstance();
			experiment.candidateSelection.addActionListener(this);
			experiment.uflStatistics = UFLStatisticRegistry.ServerStatistics.getEntryClass().newInstance();
			experiment.uflStatistics.addActionListener(this);
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void runInitialMatchers() {
		// Run the initial matchers in a separate thread.
		startThread(new Runnable(){
			@Override public void run() {
				experiment.initialMatcher.run(experiment);	
			}
		});
	}
	
	@Override
	protected void runInizialization() {
		// Run the initial matchers in a separate thread.
		startThread(new Runnable(){ 
			@Override public void run() {
				experiment.dataInizialization.inizialize(experiment);	
			}
		});
	}
	
	@Override
	protected void runCandidateSelection() {
		startThread(new Runnable() {
			@Override public void run() {
				experiment.candidateSelection.rank(experiment);
			}
		});
	}
	
	@Override
	protected void runCandidateSelectionEvaluation() {
		startThread(new Runnable() {
			@Override public void run() {
				experiment.csEvaluation.evaluate(experiment);
			}
		});
	}
	
	@Override
	protected void runUserValidation() {
		startThread(new Runnable() {
			@Override public void run() {
				experiment.userFeedback.validate(experiment);
			}
		});
	}
	
	@Override
	protected void runFeedbackAggregation() {
		startThread(new Runnable() {
			@Override
			public void run() {
				experiment.feedbackAggregation.addFeedback(experiment);
			}
		});
	}
	
	@Override
	protected void runFeedbackPropagation() {
		startThread(new Runnable() {
			@Override
			public void run() {
				experiment.feedbackPropagation.propagate(experiment);
			}
		});
	}
	
	@Override
	protected void runPropagationEvaluation() {
		startThread(new Runnable() {
			@Override
			public void run() {
				experiment.propagationEvaluation.evaluate(experiment);
			}
		});
	}
	
	@Override
	protected void runSaveFeedback() {
		startThread(new Runnable() {
			@Override public void run() {
				experiment.saveFeedback.save(experiment);	
			}
		});
	}
	
	@Override
	protected void runStatistic() {
		startThread(new Runnable() {
			@Override public void run() {
				experiment.uflStatistics.compute(experiment);	
			}
		});
	}
}
