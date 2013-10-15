package am.extension.userfeedback;

import java.math.BigDecimal;

import org.hamcrest.core.IsInstanceOf;
import org.openrdf.query.algebra.IsNumeric;
import org.semanticweb.owlapi.io.SystemOutDocumentTarget;

/*
 * Naive Bayes algorithm
 * 
 * 
 * Francesco Loprete October 2013
 */

public class NaiveBayes {
	private Object [][] trainingSet;	//contains the knowledge needed for the inference process
	private Object[][] dataSet;			//contains all the data without labels
	/*
	trainingSet example:
	f1	f2	f3	f4	f5	label
	0.4	0.2	0.3	0.5	0.9		1
	0.3	0.7	0.8	0.2	0.2		1
	0.1	0.6	0.2	0.4	0.1		0
	0.3	0.1	0.9	1.0	1.0		0
	the last element for every row represents the label, 0 (false) for the discard ones, 1 (true) for the accepted ones.
	*/
	private FeatureSet[] fs;

	//indicate the number of decimal places to consider in the NB algorithm
	int precision=1;
	
	private int count_true;		//number of elements added in the HM with the label "true"
	private int count_false;	//number of elements added in the HM with the label "false"
	private Object [] labels;	//labels computed from the NB algorithms
	//Constructor of the class, initialize the training and data Set
	public NaiveBayes(Object[][] trainingSet, Object[][] dataSet)
	{
		//updatePrecision(trainingSet);

		this.trainingSet=dataOptimization(trainingSet);
		this.dataSet=dataOptimization(dataSet);
		this.labels=new Object[dataSet.length];
		fs=new FeatureSet[dataSet[0].length];
		inizialization();

		
	}
	
	private void inizialization()
	{
		Object refValue=new Object();
		int tmp=trainingSet[0].length-1;
		Object tmpO;
		if (trainingSet[0][tmp] instanceof Integer)
			refValue=(int)Math.pow(10, precision);
		else
			refValue="yes";
		for (int i=0;i<trainingSet[0].length-1;i++)
		{
			fs[i]=new FeatureSet(Integer.toString(i));
			for (int j=0;j<trainingSet.length;j++)
			{
				tmpO=trainingSet[j][tmp];
				if(tmpO.equals(refValue))
				{
					fs[i].updateHM(trainingSet[j][i], true);
					count_true+=1;
				}
				else
				{
					fs[i].updateHM(trainingSet[j][i], false);
					count_false+=1;
				}
			}
		}
		count_true/=trainingSet[0].length-1;
		count_false/=trainingSet[0].length-1;
	}
	

	//change the number of decimal places to consider. Use a logarithm scale
	private void updatePrecision(Object[][] set)
	{
		int tmp=(int)Math.round((float)Math.log10(set.length));
		if (tmp>=1)
			precision=tmp;
		else
			precision=1;
	}
	
	private Object[][] dataOptimization(Object[][] set)
	{
		Object[][] tmp=new Object[set.length][set[0].length];
		for (int i=0;i<set.length;i++)
			for (int j=0;j<set[0].length;j++)
			{
				if (set[i][j] instanceof Double) {
					tmp[i][j]=(int)(round((double)set[i][j],precision)*Math.pow(10,precision));
				}
				else
				{
					tmp[i][j]=set[i][j];
				}
				
			}
		return tmp;
	}
	

	private static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	public void run()
	{
		double tmp=0;
		double prob_true=1.0;
		double prob_false=1.0;
		for (int i=0; i< dataSet.length;i++)
		{
			prob_true=1.0;
			prob_false=1.0;
			for(int j=0;j<dataSet[0].length;j++)
			{
				tmp=(fs[j].getHMvalue(dataSet[i][j], true)/count_true);
				prob_true*=tmp;
				tmp=(fs[j].getHMvalue(dataSet[i][j], false)/count_false);
				prob_false*=tmp;
			}
			prob_true*=((double)count_true/(count_true+count_false));
			prob_false*=((double)count_false/(count_true+count_false));
			tmp=prob_true+prob_false;
			prob_true=prob_true/tmp;
			prob_false=prob_false/tmp;
			if (Double.isNaN(prob_true))
				labels[i]="NaN";
			else
				if (prob_true>prob_false)
					labels[i]=true;
				else
					labels[i]=false;
		}
	}
	
	
	//works??
	public void runLog()
	{
		double tmp=0;
		double prob_true=1.0;
		double prob_false=1.0;
		for (int i=0; i< dataSet.length;i++)
		{
			prob_true=0.0;
			prob_false=0.0;
			for(int j=0;j<dataSet[0].length;j++)
			{
				tmp=Math.log10(fs[j].getHMvalue(dataSet[i][j], true)/count_true);
				prob_true+=tmp;
				tmp=Math.log10(fs[j].getHMvalue(dataSet[i][j], false)/count_false);
				prob_false+=tmp;
			}
			prob_true+=Math.log10((double)count_true/(count_true+count_false));
			prob_false+=Math.log10((double)count_false/(count_true+count_false));
			tmp=prob_true+prob_false;
			prob_true=prob_true/tmp;
			prob_false=prob_false/tmp;
			if (Double.isNaN(prob_true))
				labels[i]="NaN";
			else
				if (prob_true<prob_false)
					labels[i]=true;
				else
					labels[i]=false;
		}
	}
	
	public Object getLabel(int index)
	{
		return labels[index];
	}
	
	public int getPrecision()
	{
		return precision;
	}
}