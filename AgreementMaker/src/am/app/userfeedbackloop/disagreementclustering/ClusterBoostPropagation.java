package am.app.userfeedbackloop.disagreementclustering;

import java.text.NumberFormat;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.ontology.Ontology;
import am.app.userfeedback.FeedbackPropagation;
import am.app.userfeedback.UFLExperiment;
import am.app.userfeedback.UserFeedback.Validation;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.localByThreshold.LocalByThresholdMethod;
import am.evaluation.clustering.localByThreshold.LocalByThresholdParameters;

public class ClusterBoostPropagation extends FeedbackPropagation {

	@Override
	public void propagate( UFLExperiment experiment ) {
		
		//Logger log = Logger.getLogger(this.getClass());
		
		UFLExperiment log = experiment;
		
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
		
		log.info("The cluster of the candidate mapping contains " + cluster.size() + " mappings.");
		if( userFeedback == Validation.CORRECT ) {
			log.info("The user's validation is CORRECT. The mappings in the cluster will be REWARDED.");
		}
		else if( userFeedback == Validation.INCORRECT ) {
			log.info("The user's validation is INCORRECT. The mappings in the cluster will be PENALIZED.");
		}
		// for every mapping in the cluster, penalize or reward the mappings depending on the user's feedback
		int mappingNumber = 0;
		
		int correctlyPropagated = 0;
		int totalPropagated = 0;
		
		for( Mapping clusterMapping : cluster ) {
			int i = clusterMapping.getSourceKey();
			int j = clusterMapping.getTargetKey();
			
			// do not propagate to excluded mappings (first if statement checks assume 1-1 cardinality)
			if( experiment.correctMappings != null && (experiment.correctMappings.contains( clusterMapping.getEntity1(), Ontology.SOURCE) != null ||
				experiment.correctMappings.contains( clusterMapping.getEntity2(), Ontology.TARGET) != null ) )
				continue;
			if( experiment.incorrectMappings != null && experiment.incorrectMappings.contains(i, j) ) {
				continue;
			}
			
			if(userFeedback == Validation.CORRECT) {
				// reward
				if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) {
					Mapping m = feedbackClassMatrix.get(i, j);
					if( m == null ) { m = new Mapping(clusterMapping); }
					double oldSimilarity = m.getSimilarity();
					double newSimilarity = (1.0d - e) * m.getSimilarity() + e;
					if( newSimilarity > 1.0d ) newSimilarity = 1.0d;
					m.setSimilarity( newSimilarity );
					feedbackClassMatrix.set(i,j,m);
					
					String inRef = " (in reference = no) ";
					boolean binRef = false;
					if( experiment.getReferenceAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation()) ) {
						inRef = " (in reference = yes) ";
						binRef = true;
					}
						
					String inAlignment = " (in alignment = no) ";
					if( experiment.getFinalAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation())) {
						inAlignment = " (in alignment = yes) ";
					}
					
					// propagation quality
					if( binRef ) correctlyPropagated++;
					totalPropagated++;
					
					log.info(mappingNumber + ". Rewarding " + m + inRef + inAlignment + ".  Similarity updated from " + oldSimilarity + " to " + newSimilarity + ".");
					mappingNumber++;
				} else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) {
					Mapping m = feedbackPropertyMatrix.get(i, j);
					if( m == null ) { m = new Mapping(clusterMapping); }
					double oldSimilarity = m.getSimilarity();
					double newSimilarity = (1.0d - e) * m.getSimilarity() + e;
					if( newSimilarity > 1.0d ) newSimilarity = 1.0d;
					m.setSimilarity(newSimilarity);
					feedbackPropertyMatrix.set(i,j,m);
					
					String inRef = " (in reference = no) ";
					boolean binRef = false;
					if( experiment.getReferenceAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation()) ) { 
						inRef = " (in reference = yes) ";
						binRef = true;
					}
					
					String inAlignment = " (in alignment = no) ";
					if( experiment.getFinalAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation())) {
						inAlignment = " (in alignment = yes) ";
					}
					
					if( binRef ) correctlyPropagated++;
					totalPropagated++;
					
					log.info(mappingNumber + ". Rewarding " + m + inRef + inAlignment + ".  Similarity updated from " + oldSimilarity + " to " + newSimilarity + ".");
					mappingNumber++;
				}
			} else if( userFeedback == Validation.INCORRECT ) {
				// penalize
				if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) {
					Mapping m = feedbackClassMatrix.get(i, j);
					if( m == null ) { m = new Mapping(clusterMapping); }
					double oldSimilarity = m.getSimilarity();
					double newSimilarity = (1.0d - e) * m.getSimilarity();
					m.setSimilarity( newSimilarity );
					feedbackClassMatrix.set(i,j,m);
					
					String inRef = " (in reference = no) ";
					boolean binRef = false;
					if( experiment.getReferenceAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation()) ) { 
						inRef = " (in reference = yes) ";
						binRef = true;
					}
					
					String inAlignment = " (in alignment = no) ";
					if( experiment.getFinalAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation())) {
						inAlignment = " (in alignment = yes) ";
					}
					
					if( !binRef ) correctlyPropagated++;
					totalPropagated++;
					
					log.info(mappingNumber + ". Penalizing " + m + inRef + inAlignment + ".  Similarity updated from " + oldSimilarity + " to " + newSimilarity + ".");
					mappingNumber++;
				} else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) {
					Mapping m = feedbackPropertyMatrix.get(i, j);
					if( m == null ) { m = new Mapping(clusterMapping); }
					double oldSimilarity = m.getSimilarity();
					double newSimilarity = (1.0d - e) * m.getSimilarity();
					m.setSimilarity(newSimilarity);
					feedbackPropertyMatrix.set(i,j,m);
					
					String inRef = " (in reference = no) ";
					boolean binRef = false;
					if( experiment.getReferenceAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation()) ) { 
						inRef = " (in reference = yes) ";
						binRef = true;
					}
					
					String inAlignment = " (in alignment = no) ";
					if( experiment.getFinalAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2(), candidateMapping.getRelation())) {
						inAlignment = " (in alignment = yes) ";
					}
					
					if( !binRef ) correctlyPropagated++;
					totalPropagated++;
					
					log.info(mappingNumber + ". Penalizing " + m + inRef + inAlignment + ".  Similarity updated from " + oldSimilarity + " to " + newSimilarity + ".");	
					mappingNumber++;
				}
			}
		} // for
		
		if( cluster.size() > 0 ) {
			log.info("");
			double propagationQuality = (new Integer(correctlyPropagated)).doubleValue() / (new Integer(cluster.size())).doubleValue();
			NumberFormat numForm = NumberFormat.getPercentInstance();
			numForm.setMaximumFractionDigits(2);
			NumberFormat numFormDec = NumberFormat.getNumberInstance();
			numFormDec.setMaximumFractionDigits(4);
			log.info("Iteration " + experiment.getIterationNumber() + " . "+
			         "Propagation quality (correctly propagated / total propagated): " + correctlyPropagated + "/" + totalPropagated + " = " +  numForm.format(propagationQuality) + " " + numFormDec.format(propagationQuality) );
		} else {
			log.info("Iteration " + experiment.getIterationNumber() + " . "+
					 "Propagation quality (correctly propagated / total propagated): " + correctlyPropagated + "/" + totalPropagated + " = 0.0% 0.0" );
		}
		
		log.info("");
		
		Alignment<Mapping> finalAlignment = experiment.getFinalAlignment();
		
		Alignment<Mapping> finalAlignmentCopy = (Alignment<Mapping>)finalAlignment.clone();
		
		log.info("Propagation is done.  Creating the new alignment.");
		
		experiment.initialMatcher.getFinalMatcher().select();
		
		finalAlignment = experiment.getFinalAlignment();
		
		log.info("");
		
		log.info("Added mappings: ");
		
		int mappingNumber1 = 0;
		for( Mapping finalAlignmentMapping : finalAlignment ) {
			if( !finalAlignmentCopy.contains(finalAlignmentMapping) ) {
				log.info( mappingNumber1 + ". + " + finalAlignmentMapping );
				mappingNumber1++;
			}
		}
		
		log.info("");
		
		log.info("Deleted mappings: ");
		int mappingNumber2 = 0;
		for( Mapping previousAlignmentMapping : finalAlignmentCopy ) {
			if( !finalAlignment.contains(previousAlignmentMapping) ) {
				log.info( mappingNumber2 + ". - " + previousAlignmentMapping );
				mappingNumber2++;
			}
		}
		
		log.info("");
		
		done();
	}

	
	
	
	
	
}
