package am.app.userfeedbackloop.disagreementclustering;

import java.util.List;

import org.apache.log4j.Logger;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.userfeedbackloop.FeedbackPropagation;
import am.app.userfeedbackloop.UFLExperiment;
import am.app.userfeedbackloop.UserFeedback.Validation;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.localByThreshold.LocalByThresholdMethod;
import am.evaluation.clustering.localByThreshold.LocalByThresholdParameters;

public class ClusterBoostPropagation extends FeedbackPropagation {

	@Override
	public void propagate( UFLExperiment experiment ) {
		
		Logger log = Logger.getLogger(this.getClass());
		
		double e = 0.1d; // TODO: Figure out how to get this from the interface.
		double clTh = 0.1d;
		
		Mapping candidateMapping = experiment.userFeedback.getCandidateMapping();
		
		// setup the clustering method
		List<AbstractMatcher> availableMatchers = experiment.initialMatcher.getComponentMatchers();
		LocalByThresholdMethod clusteringMethod = new LocalByThresholdMethod(availableMatchers);
		
		LocalByThresholdParameters clusteringParameters = new LocalByThresholdParameters();
		clusteringParameters.setMatchers(availableMatchers);
		clusteringParameters.clusteringThreshold = clTh;
		clusteringMethod.setParameters(clusteringParameters);
		
		// compute the cluster of the validated mapping
		Cluster<Mapping> cluster = clusteringMethod.getCluster(candidateMapping);
		
		// get the user's validation
		Validation userFeedback = experiment.userFeedback.getUserFeedback();
		
		SimilarityMatrix feedbackClassMatrix = experiment.initialMatcher.getFinalMatcher().getClassesMatrix();
		SimilarityMatrix feedbackPropertyMatrix = experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix();
		
		// set the candidate mapping's similarity
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) {
			Mapping m = feedbackClassMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) { m.setSimilarity(1.0d); }
			else if( userFeedback == Validation.INCORRECT ) { m.setSimilarity(0.0d); }
			
			feedbackClassMatrix.set(candidateMapping.getSourceKey(), candidateMapping.getTargetKey(), m);
		} 
		else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) {
			Mapping m = feedbackPropertyMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) { m.setSimilarity(1.0d); }
			else if( userFeedback == Validation.INCORRECT ) { m.setSimilarity(0.0d); }
			
			feedbackPropertyMatrix.set(candidateMapping.getSourceKey(), candidateMapping.getTargetKey(), m);
		}
		
		
		log.info("Propagating to " + cluster.size() + " mappings.");
		// for every mapping in the cluster, penalize or reward the mappings depending on the user's feedback
		for( Mapping clusterMapping : cluster ) {
			int i = clusterMapping.getSourceKey();
			int j = clusterMapping.getTargetKey();
			if(userFeedback == Validation.CORRECT) {
				// reward
				if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) {
					Mapping m = feedbackClassMatrix.get(i, j);
					if( m == null ) { m = new Mapping(clusterMapping); }
					double newSimilarity = (1.0d - e) * m.getSimilarity() + e;
					if( newSimilarity > 1.0d ) newSimilarity = 1.0d;
					m.setSimilarity( newSimilarity );
					feedbackClassMatrix.set(i,j,m);
				} else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) {
					Mapping m = feedbackPropertyMatrix.get(i, j);
					if( m == null ) { m = new Mapping(clusterMapping); }
					double newSimilarity = (1.0d - e) * m.getSimilarity() + e;
					if( newSimilarity > 1.0d ) newSimilarity = 1.0d;
					m.setSimilarity(newSimilarity);
					feedbackPropertyMatrix.set(i,j,m);
				}
			} else if( userFeedback == Validation.INCORRECT ) {
				// penalize
				if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) {
					Mapping m = feedbackClassMatrix.get(i, j);
					if( m == null ) { m = new Mapping(clusterMapping); }
					double newSimilarity = (1.0d - e) * m.getSimilarity();
					m.setSimilarity( newSimilarity );
					feedbackClassMatrix.set(i,j,m);
				} else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) {
					Mapping m = feedbackPropertyMatrix.get(i, j);
					if( m == null ) { m = new Mapping(clusterMapping); }
					double newSimilarity = (1.0d - e) * m.getSimilarity();
					m.setSimilarity(newSimilarity);
					feedbackPropertyMatrix.set(i,j,m);
				}
			}
		}
		
		experiment.initialMatcher.getFinalMatcher().select();
		
		done();
	}

	
}
