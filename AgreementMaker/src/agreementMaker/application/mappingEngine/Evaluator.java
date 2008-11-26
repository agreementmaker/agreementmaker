package agreementMaker.application.mappingEngine;

public class Evaluator
{
	//Constants
	/**Formats for reference files*/
	public final static String REF0 = "OAEI-2008-testcase";
	public final static String REF1 = "OAEI-2007 i.e. weapons, wetlands...";
	public final static String REF2 = "TXT: sourcename(tab)targetname";
	public final static String REF3= "TXT: sourceDesc(tab)sourceName(tab)targetName(tab)targetDesc(tab)";

	/**Formats for output files*/
	public final static String OUTF1 = "TXT-1";
	
	final static double ALPHA = 1;
	
    public ResultData compare(AlignmentSet as1, AlignmentSet as2)
    {
        if (as1 == null || as2 == null) {
            return null;
        }
        int found = as1.size();
        int exist = as2.size();
        int correct = 0;

        if (found == 0 || exist == 0) {
            return null;
        }
        ResultData result = new ResultData();
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
        System.out.println("Found: " + found + ", Exist: " + exist + ", Correct: " + correct);

        double prec = (double) correct / found;
        double rec = (double) correct / exist;
        System.out.println("Precision: " + prec + ", Recall: " + rec);
        // F-measure
        double fm = (1 + ALPHA) * (prec * rec) / (ALPHA * prec + rec);
        System.out.println("F-Measure: " + fm);

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
