package am.app.mappingEngine;
import am.Utility;

public class ReferenceEvaluationData
{
    private int found = 0;
    private int exist = 0;
    private int correct = 0;
    private double precision = 0;
    private double recall = 0;
    private double fmeasure = 0;
    private Alignment<Mapping> errorAlignments = null;
    private Alignment<Mapping> correctAlignments = null;
    private Alignment<Mapping> lostAlignments = null;
	private int classesFound;
	private int classesCorrect;
	private int propertiesCorrect;
	private int propertiesFound;
  
    public Alignment<Mapping> getCorrectAlignments()
    {
        return correctAlignments;
    }

    public void setCorrectAlignments(Alignment<Mapping> ca)
    {
        correctAlignments = ca;
    }

    public Alignment<Mapping> getLostAlignments()
    {
        return lostAlignments;
    }

    public void setLostAlignments(Alignment<Mapping> la)
    {
        lostAlignments = la;
    }

    public Alignment<Mapping> getErrorAlignments()
    {
        return errorAlignments;
    }

    public void setErrorAlignments(Alignment<Mapping> ea)
    {
        errorAlignments = ea;
    }

	/**
	 * @return The number of mappings in the intersection between the computed
	 *         alignment and the reference alignment. In other words, the number
	 *         of correct mappings in the computed alignment.
	 */
    public int getCorrect()
    {
        return correct;
    }

    public void setCorrect(int c)
    {
        correct = c;
    }

    /**
     * @return The number of mappings in the computed alignment.
     */
    public int getFound()
    {
        return found;
    }

    public void setFound(int f)
    {
        found = f;
    }

    /**
     * @return The number of mappings in the reference alignment.
     */
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
		result+="Class mappings correct/found: "+classesCorrect+"/" + classesFound + ": " +
				(classesFound == 0 ? "0%" : Utility.getOneDecimalPercentFromDouble((double)classesCorrect/(double)classesFound)) + "\n";
		result+="Property mappings correct/found: "+propertiesCorrect+"/" + propertiesFound + ": " + 
				(propertiesFound == 0 ? "0%" : Utility.getOneDecimalPercentFromDouble((double)propertiesCorrect/(double)propertiesFound) ) + "\n";
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

	public void setClasses(int classesCorrect, int classesFound) {
			this.classesFound = classesFound; this.classesCorrect = classesCorrect;
	}
	
	public void setProperties(int propertiesCorrect, int propertiesFound) {
		this.propertiesFound = propertiesFound; this.propertiesCorrect = propertiesCorrect;
	}
    
}
