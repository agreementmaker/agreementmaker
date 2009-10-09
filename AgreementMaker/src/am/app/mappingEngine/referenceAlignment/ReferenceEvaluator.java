package am.app.mappingEngine.referenceAlignment;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AlignmentSet;

public class ReferenceEvaluator{

	
	final static double ALPHA = 1;
	
    public static ReferenceEvaluationData compare(AlignmentSet as1, AlignmentSet as2)
    {
        int found = 0; 
        int exist = 0;
        if(as1 != null) {
        	found = as1.size();
        }
        if (as2 != null) {
            exist = as2.size();
        }
        int correct = 0;
        ReferenceEvaluationData result = new ReferenceEvaluationData();
        AlignmentSet errorAlignments = new AlignmentSet();
        AlignmentSet correctAlignments = new AlignmentSet();
        AlignmentSet lostAlignments = new AlignmentSet();
        for (int i = 0; i < found; i++) {
            Alignment alignment1 = (Alignment) as1.getAlignment(i);
            boolean flag = true;
            for (int j = 0; j < exist; j++) {
                Alignment alignment2 = (Alignment) as2.getAlignment(j);
                if (alignment1.equals(alignment2)) {
                    correct++;
                    correctAlignments.addAlignment(alignment1);
                    flag = false;
                    break;
                }
            }
            if (flag == true) {
                errorAlignments.addAlignment(alignment1);
            }
        }
        for (int i = 0; i < exist; i++) {
            Alignment alignment1 = as2.getAlignment(i);
            boolean flag = true;
            for (int j = 0; j < found; j++) {
                Alignment alignment2 = as1.getAlignment(j);
                if (alignment1.equals(alignment2)) {
                    flag = false;
                    break;
                }
            }
            if (flag == true) {
                lostAlignments.addAlignment(alignment1);
            }
        }
        //System.out.println("Found: " + found + ", Exist: " + exist + ", Correct: " + correct);
        double prec;
        if(found == 0) {
        	prec = 1;
        }
        else prec = (double) correct / found;
        
        double rec;
        if(exist == 0) {
        	rec = 1;
        }
        else rec = (double) correct / exist;
        //System.out.println("Precision: " + prec + ", Recall: " + rec);
        // F-measure
        double fm;
        if(prec + rec == 0) {
        	fm = 0;
        }
        else  fm = (1 + ALPHA) * (prec * rec) / (ALPHA * prec + rec);
        //System.out.println("F-Measure: " + fm);

        result.setFound(found);
        result.setExist(exist);
        result.setCorrect(correct);
        result.setPrecision(prec);
        result.setRecall(rec);
        result.setFmeasure(fm);
        result.setCorrectAlignments(correctAlignments);
        result.setErrorAlignments(errorAlignments);
        result.setLostAlignments(lostAlignments);
        return result;
    }
}
