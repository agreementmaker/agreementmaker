package am.extension.userfeedback.common;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.evaluation.alignment.AlignmentMetrics;
import am.evaluation.alignment.DeltaFromReference;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.multiUserFeedback.experiment.MUExperiment.csData.MappingSource;
import am.extension.multiUserFeedback.selection.ServerMultiStrategyCandidateSelection;
import am.extension.userfeedback.evaluation.PropagationEvaluation;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.selection.CandidateSelection;
import am.utility.Pair;

public class SelectionRankingEvaluation extends
		PropagationEvaluation<MUExperiment> {

	private static final Logger LOG = Logger
			.getLogger(SelectionRankingEvaluation.class);

	public static final String EVALUATION_DATA = SelectionRankingEvaluation.class
			.toString() + "_EVALUATION_DATA";

	public class EvaluationData {
		List<Pair<MappingSource, Mapping>> validatedMappings = new LinkedList<>();
		double cumulativeRecall = 0;
	}

	@Override
	public void evaluate(MUExperiment exp) {

		EvaluationData data = (EvaluationData) exp
				.getSharedObject(EVALUATION_DATA);
		if (data == null) {
			data = new EvaluationData();
			exp.setSharedObject(EVALUATION_DATA, data);
		}

		// print out the iteration statistics
		DeltaFromReference deltaFromReference = new DeltaFromReference(
				exp.getReferenceAlignment());
		int delta = deltaFromReference.getDelta(exp.getFinalAlignment());

		AlignmentMetrics metrics = new AlignmentMetrics(
				exp.getReferenceAlignment(), exp.getFinalAlignment());

		exp.info("Iteration: " + exp.getIterationNumber()
				+ ", Delta from reference: " + delta + ", Precision: "
				+ metrics.getPrecisionPercent() + ", Recall: "
				+ metrics.getRecallPercent() + ", FMeasure: "
				+ metrics.getFMeasurePercent());
		exp.info("");
		
		if (((CandidateSelection) exp.candidateSelection) instanceof ServerMultiStrategyCandidateSelection) {
			// do the special stuff for ServerMultiStrategyCandidateSelection
			int currentIteration = exp.getIterationNumber();
			int numIterations = exp.setup.parameters
					.getIntParameter(Parameter.NUM_ITERATIONS);
			int iterationsLeft = numIterations - currentIteration;

			// save the validated mapping
			data.validatedMappings.add(new Pair<MappingSource, Mapping>(
					exp.data.mappingSource, exp.candidateSelection
							.getSelectedMapping()));

			data.cumulativeRecall += computeRecall(exp, data, delta, iterationsLeft);
			
			if( iterationsLeft == 0 ) {
				exp.info("Selection Ranking Evaluation: " + data.cumulativeRecall);
			}
		} else
			LOG.debug("Expecting 'ServerMultiStrategyCandidateSelection' but not the case.  Not doing any ranking evaluation.");
		
		done();
	}

	private double computeRecall(MUExperiment exp, EvaluationData data, int deltaFromReference, int iterationsLeft) {
		// create the list
		Alignment<Mapping> mappingsLeft = generateMappingList(exp, iterationsLeft);
		mappingsLeft.removeAll(exp.getValidatedMappings());
		
		if( deltaFromReference == 0 ) return 1d;
		if( mappingsLeft.isEmpty() ) return 0d;
		AlignmentMetrics metrics = new AlignmentMetrics(exp.getReferenceAlignment(), mappingsLeft);
		
		return Math.min( metrics.getNumCorrect() , Math.min(iterationsLeft, deltaFromReference) ) / (double)deltaFromReference;
	}

	private Alignment<Mapping> generateMappingList(MUExperiment exp, int iterationsLeft) {
		Alignment<Mapping> mL = 
				new Alignment<>(exp.getReferenceAlignment().getSourceOntologyID(),exp.getReferenceAlignment().getSourceOntologyID());
		
		double revalidationRate = exp.setup.parameters.getDoubleParameter(Parameter.REVALIDATION_RATE);
		double dRate  = (1-revalidationRate)/2;
		double mqRate = (1-revalidationRate)/2;
		
		int total = exp.data.total;
		int[] count = Arrays.copyOf(exp.data.count, 3);
			
		int dIndex = 0, mqIndex = 0, rIndex = 0;
		for( int i = 0; i < iterationsLeft; i++ ) {
			double dPercent  = (total!=0) ? count[0]/(double)total : 0d;
			double mqPercent = (total!=0) ? count[1]/(double)total : 0d;
			double rPercent  = (total!=0) ? count[2]/(double)total : 0d;			
			total++;
			
			if( dPercent <= dRate ) {
				// we're selecting from the disagreement list
				Mapping m = exp.data.drList.get(dIndex);
				while( mL.contains(m) ) {
					dIndex++;
					m = exp.data.drList.get(dIndex);
				}
				mL.add(m);
				dIndex++;
				count[0]++;
			}
			else if( dPercent <= mqRate ) {
				// we're selecting from the mapping quality list
				Mapping m = exp.data.mqList.get(mqIndex);
				while( mL.contains(m) ) {
					mqIndex++;
					m = exp.data.mqList.get(mqIndex);
				}
				mL.add(m);
				mqIndex++;
				count[1]++;
			}
			else if( rPercent <= revalidationRate ) {
				// ignoring revalidation list.
				count[2]++;
			}
		}
		
		return mL;
	}

}
