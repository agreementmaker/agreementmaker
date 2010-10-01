package am.app.mappingEngine.referenceAlignment;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentSet;

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
    public static ReferenceEvaluationData compare(AlignmentSet<?> evaluationSet, AlignmentSet<?> referenceSet)
    {
        int foundMappings = 0; 
        int referenceMappings = 0;
        if(evaluationSet != null) {
        	foundMappings = evaluationSet.size();
        }
        if (referenceSet != null) {
            referenceMappings = referenceSet.size();
        }
        
        ReferenceEvaluationData result = new ReferenceEvaluationData();
        
        AlignmentSet<Alignment> errorAlignments = new AlignmentSet<Alignment>();
        AlignmentSet<Alignment> correctAlignments = new AlignmentSet<Alignment>();
        AlignmentSet<Alignment> lostAlignments = new AlignmentSet<Alignment>();
        
        int correctMappings = 0;
        // check all mappings for correctness
        for (int i = 0; i < foundMappings; i++) {
            Alignment evaluationMapping = evaluationSet.getAlignment(i);
            boolean evaluationMappingIsWrong = true;
            for (int j = 0; j < referenceMappings; j++) {
                Alignment referenceMapping = referenceSet.getAlignment(j);
                if (evaluationMapping.equals(referenceMapping)) {
                    correctMappings++;
                    correctAlignments.addAlignment(evaluationMapping);
                    evaluationMappingIsWrong = false;
                    break;
                }
            }
            if (evaluationMappingIsWrong == true) {
                errorAlignments.addAlignment(evaluationMapping);
            }
        }
        
        for (int i = 0; i < referenceMappings; i++) {
            Alignment referenceMapping = referenceSet.getAlignment(i);
            boolean referenceMappingNotFound = true;
            for (int j = 0; j < foundMappings; j++) {
                Alignment evaluationMapping = evaluationSet.getAlignment(j);
                if (referenceMapping.equals(evaluationMapping)) {
                    referenceMappingNotFound = false;
                    break;
                }
            }
            if (referenceMappingNotFound == true) {
                lostAlignments.addAlignment(referenceMapping);
            }
        }
        //System.out.println("Found: " + found + ", Exist: " + exist + ", Correct: " + correct);
        double prec;
        if(foundMappings == 0.0d) {
        	prec = 1;
        }
        else prec = (double) correctMappings / foundMappings;
        
        double rec;
        if(referenceMappings == 0.0d) {
        	rec = 1;
        }
        else rec = (double) correctMappings / referenceMappings;
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
        return result;
    }
}
