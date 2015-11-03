package am.extension.userfeedback.clustering.disagreement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Ontology;
import am.evaluation.disagreement.variance.VarianceDisagreement;
import am.evaluation.disagreement.variance.VarianceDisagreementParameters;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.selection.CandidateSelection;

public class DisagreementRanking<T extends UFLExperiment> extends CandidateSelection<T> {

	private List<Mapping> rankedClassMappings;
	private List<Mapping> rankedPropertyMappings;
	private List<Mapping> allRanked;
	
	private UFLExperiment experiment;
	
	@Override public List<Mapping> getRankedMappings(alignType t) { 
		if( t == alignType.aligningClasses ) { return rankedClassMappings; }
		if( t == alignType.aligningProperties ) { return rankedPropertyMappings; }
		return null;
	}
	
	@Override
	public List<Mapping> getRankedMappings() {
		return allRanked;
	}
	
	@Override
	public void rank(UFLExperiment ex) {
		this.experiment = ex;

		// get the matchers from the execution semantics
		List<MatchingTask> matchers = ex.initialMatcher.getComponentMatchers();
		
		rank(matchers);
	}
	
	public void rank(List<MatchingTask> matchers) {
				
		if( allRanked == null ) {
			initializeRankedList(matchers);
		}
		
		for( int i = 0; i < allRanked.size(); i++ ){
			if( experiment.correctMappings == null && experiment.incorrectMappings == null )
			{
				selectedMapping = allRanked.get(i);
				break;
			}
			
			Mapping m = allRanked.get(i);
			if( experiment.correctMappings != null && (experiment.correctMappings.contains(m.getEntity1(),Ontology.SOURCE) != null ||
				experiment.correctMappings.contains(m.getEntity2(),Ontology.TARGET) != null) ) {
				// assume 1-1 mapping, skip already validated mappings.
				continue;
			}
			if( experiment.incorrectMappings != null && experiment.incorrectMappings.contains(m) ) 
				continue; // we've validated this mapping already.
			
			selectedMapping=m;
			break;
		}
		
		done();
	}

	private void initializeRankedList(List<MatchingTask> matchers) {
		if( allRanked == null ) {
			initializeRankedList(matchers);
		}
		
		for( int i = 0; i < allRanked.size(); i++ ){
			if( experiment.correctMappings == null && experiment.incorrectMappings == null )
			{
				selectedMapping = allRanked.get(i);
				break;
			}
			
			Mapping m = allRanked.get(i);
			if( experiment.correctMappings != null && (experiment.correctMappings.contains(m.getEntity1(),Ontology.SOURCE) != null ||
				experiment.correctMappings.contains(m.getEntity2(),Ontology.TARGET) != null) ) {
				// assume 1-1 mapping, skip already validated mappings.
				continue;
			}
			if( experiment.incorrectMappings != null && experiment.incorrectMappings.contains(m) ) 
				continue; // we've validated this mapping already.
			
			selectedMapping=m;
			break;
		}
		
		done();
	}

	private void initializeRankedList(List<AbstractMatcher> matchers) {
		// setup the variance disagreement calculation
		VarianceDisagreementParameters disagreementParams = new VarianceDisagreementParameters();
		disagreementParams.setMatchers(matchers);
		
		VarianceDisagreement disagreementMetric = new VarianceDisagreement();
		disagreementMetric.setParameters(disagreementParams);
		
		// run the disagreement calculations
		SimilarityMatrix classDisagreement = disagreementMetric.getDisagreementMatrix(alignType.aligningClasses);
		//rankedClassMappings = classDisagreement.getOrderedMappingsAboveThreshold(0.0d);
		try {
			rankedClassMappings = classDisagreement.toList();
			Collections.sort(rankedClassMappings, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		classDisagreement = null;  // release the memory used by this
		
		SimilarityMatrix propertyDisagreement = disagreementMetric.getDisagreementMatrix(alignType.aligningProperties);
		
		try {
			rankedPropertyMappings = propertyDisagreement.toList();
			Collections.sort(rankedPropertyMappings, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		propertyDisagreement = null;
		
		allRanked = new ArrayList<Mapping>();
		
		allRanked.addAll(rankedClassMappings);
		allRanked.addAll(rankedPropertyMappings);
		Collections.sort(allRanked, new MappingSimilarityComparator() );
		Collections.reverse(allRanked);
	}
}
