package agreementMaker.application.mappingEngine.referenceAlignment;
import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AlignmentSet;

public class ReferenceEvaluationData
{
    private int found = 0;
    private int exist = 0;
    private int correct = 0;
    private double precision = 0;
    private double recall = 0;
    private double fmeasure = 0;
    private AlignmentSet errorAlignments = null;
    private AlignmentSet correctAlignments = null;
    private AlignmentSet lostAlignments = null;

    public AlignmentSet getCorrectAlignments()
    {
        return correctAlignments;
    }

    public void setCorrectAlignments(AlignmentSet ca)
    {
        correctAlignments = ca;
    }

    public AlignmentSet getLostAlignments()
    {
        return lostAlignments;
    }

    public void setLostAlignments(AlignmentSet la)
    {
        lostAlignments = la;
    }

    public AlignmentSet getErrorAlignments()
    {
        return errorAlignments;
    }

    public void setErrorAlignments(AlignmentSet ea)
    {
        errorAlignments = ea;
    }

    public int getCorrect()
    {
        return correct;
    }

    public void setCorrect(int c)
    {
        correct = c;
    }

    public int getFound()
    {
        return found;
    }

    public void setFound(int f)
    {
        found = f;
    }

    public int getExist()
    {
        return exist;
    }

    public void setExist(int e)
    {
        exist = e;
    }

    public double getFmeasure()
    {
        return fmeasure;
    }

    public void setFmeasure(double fm)
    {
        fmeasure = fm;
    }

    public double getPrecision()
    {
        return precision;
    }

    public void setPrecision(double prec)
    {
        precision = prec;
    }

    public double getRecall()
    {
        return recall;
    }

    public void setRecall(double rec)
    {
        recall = rec;
    }

	/**
	 * must be invoked after the evaluation process
	 * @return a user message reporting all calculated measures
	 */
	public String getReport() {
		String result  = "";
		result+="Matchings discovered: "+getFound()+"\n";
		result+="Matchings in Reference: "+getExist()+"\n";
		result+="Matchings correct: "+getCorrect()+"\n";
		result+="Precision = Correct/Discovered: "+Utility.getOneDecimalPercentFromDouble(getPrecision())+"\n";
		result+="Recall = Correct/Reference: "+Utility.getOneDecimalPercentFromDouble(getRecall())+"\n";
		result+="Fmeasure = 2(precision*recall)/(precision+recall): "+Utility.getOneDecimalPercentFromDouble(getFmeasure())+"\n";
		return result;
	}
	
	public String getMeasuresLine() {
		return found+"\t"+correct+"\t"+exist+"\t"+Utility.getOneDecimalPercentFromDouble(precision)+"\t"+Utility.getOneDecimalPercentFromDouble(recall)+"\t"+Utility.getOneDecimalPercentFromDouble(fmeasure)+"\n";
	}
    
}
