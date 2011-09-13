package am.app.mappingEngine.referenceAlignment;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.Ontology;

public class ReferenceEvaluator{

	
	final static double ALPHA = 0.5d; // ALPHA can range from 0 to 1.  When ALPHA = 0, FM = PREC.  When ALPHA = 1, FM = REC. 
								      // The higher ALPHA is, the more importance is given to PREC and less importance to REC. 
									  // When ALPHA = 0.5, this is the harmonic mean of PREC and REC.
	/**
	 * Perform a reference evaluation.
	 * @param evaluationSet The alignment set of the matcher that must be evaluated.
	 * @param referenceSet The reference alignment.
	 * @return
	 */
    public static ReferenceEvaluationData compare(Alignment<Mapping> evaluationSet, Alignment<Mapping> referenceSet)
    {
        int foundMappings = 0; 
        int referenceMappings = 0;
        
        int classesFound = 0, propertiesFound = 0;
        int classesCorrect = 0, propertiesCorrect = 0;
        
        if(evaluationSet != null) {
        	foundMappings = evaluationSet.size();
        }
        if (referenceSet != null) {
            referenceMappings = referenceSet.size();
        }
        
        ReferenceEvaluationData result = new ReferenceEvaluationData();
        
        Alignment<Mapping> errorAlignments = new Alignment<Mapping>(Ontology.ID_NONE, Ontology.ID_NONE);
        Alignment<Mapping> correctAlignments = new Alignment<Mapping>(Ontology.ID_NONE, Ontology.ID_NONE);
        Alignment<Mapping> lostAlignments = new Alignment<Mapping>(Ontology.ID_NONE, Ontology.ID_NONE);
        
        int correctMappings = 0;
        // check all mappings for correctness
        for (int i = 0; i < foundMappings; i++) {
            Mapping evaluationMapping = evaluationSet.get(i);
            if(evaluationMapping.getAlignmentType() == alignType.aligningClasses ) classesFound++;
            if(evaluationMapping.getAlignmentType() == alignType.aligningProperties ) propertiesFound++;
            boolean evaluationMappingIsWrong = true;
            if( referenceSet.contains(evaluationMapping) ) {
            	correctMappings++;
                correctAlignments.add(evaluationMapping);
                evaluationMappingIsWrong = false;
                if(evaluationMapping.getAlignmentType() == alignType.aligningClasses ) classesCorrect++;
                if(evaluationMapping.getAlignmentType() == alignType.aligningProperties ) propertiesCorrect++;
            }
            /*for (int j = 0; j < referenceMappings; j++) {
                Mapping referenceMapping = referenceSet.get(j);
                if (evaluationMapping.equals(referenceMapping)) {
                    correctMappings++;
                    correctAlignments.add(evaluationMapping);
                    evaluationMappingIsWrong = false;
                    if(evaluationMapping.getAlignmentType() == alignType.aligningClasses ) classesCorrect++;
                    if(evaluationMapping.getAlignmentType() == alignType.aligningProperties ) propertiesCorrect++;
                    break;
                }
            }*/
            if (evaluationMappingIsWrong == true) {
                errorAlignments.add(evaluationMapping);
            }
        }
        
        for (int i = 0; i < referenceMappings; i++) {
            Mapping referenceMapping = referenceSet.get(i);
            boolean referenceMappingNotFound = true;
            if( evaluationSet.contains(referenceMapping) ) {
            	referenceMappingNotFound = false;
            }
            /*for (int j = 0; j < foundMappings; j++) {
                Mapping evaluationMapping = evaluationSet.get(j);
                if (referenceMapping.equals(evaluationMapping)) {
                    referenceMappingNotFound = false;
                    break;
                }
            }*/
            if (referenceMappingNotFound == true) {
                lostAlignments.add(referenceMapping);
            }
        }
        //System.out.println("Found: " + found + ", Exist: " + exist + ", Correct: " + correct);
        double prec;
        if(foundMappings == 0.0d) {
        	prec = 1;
        }
        else prec = (double) correctMappings / (double) foundMappings;
        
        double rec;
        if(referenceMappings == 0.0d) {
        	rec = 1;
        }
        else rec = (double) correctMappings / (double) referenceMappings;
        //System.out.println("Precision: " + prec + ", Recall: " + rec);
        // F-measure
        double fm;
        if(prec + rec == 0.0d) {
        	fm = 0.0d;
        }
        //else  fm = (1 + ALPHA) * (prec * rec) / (ALPHA * prec + rec);
        else fm = (prec * rec) / ( ((1-ALPHA) * prec) + (ALPHA * rec) );  // from Ontology Matching book
        //System.out.println("F-Measure: " + fm);

        result.setFound(foundMappings);
        result.setExist(referenceMappings);
        result.setCorrect(correctMappings);
        result.setPrecision(prec);
        result.setRecall(rec);
        result.setFmeasure(fm);
        result.setCorrectAlignments(correctAlignments);
        result.setErrorAlignments(errorAlignments);
        result.setLostAlignments(lostAlignments);
        result.setClasses( classesCorrect, classesFound );
        result.setProperties( propertiesCorrect, propertiesFound );
        return result;
    }
}
