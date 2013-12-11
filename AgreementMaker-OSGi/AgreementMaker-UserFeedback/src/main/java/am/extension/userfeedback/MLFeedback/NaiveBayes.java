/*
 * 	Francesco Loprete October 2013
 */
package am.extension.userfeedback.MLFeedback;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;


/*
 * Naive Bayes algorithm
 * 
 * 
 * Francesco Loprete October 2013
 */

public class NaiveBayes {
	private Object [][] trainingSet;	//contains the knowledge needed for the inference process
	/*
	trainingSet example:
	f1	f2	f3	f4	f5	label
	0.4	0.2	0.3	0.5	0.9		1
	0.3	0.7	0.8	0.2	0.2		1
	0.1	0.6	0.2	0.4	0.1		0
	0.3	0.1	0.9	1.0	1.0		0
	the last element for every row represents the label, 0 (false) for the discard ones, 1 (true) for the accepted ones.
	*/
	
	private Object[][] dataSet;			//contains all the data without labels
	public Object[][] unlabeledTrainingSet;
	/*
	 example of unlabeledTrainingSet compute by the algorithm
	 f1	f2	f3	f4	f5	label
	0.4	0.2	0.3	0.5	0.9		0.75
	0.3	0.7	0.8	0.2	0.2		0.87
	0.1	0.6	0.2	0.4	0.1		0.23
	0.3	0.1	0.9	1.0	1.0		0.17
	the labels are computed by the algorithm based on the labeled training set instance
	 */
	private FeatureSet[] fs;
	boolean unlabeled=false;  //if you want use the EM algorithm the labels must be numeric [0,1]
	//indicate the number of decimal places to consider in the NB algorithm
	int precision=1;
	final double smooth_c=0.5;
	final double smooth_ce=0.2;
	private Object[] element;
	private int count_true;		//number of elements added in the HM with the label "true"
	private int count_false;	//number of elements added in the HM with the label "false"
	private Object [] labels;	//labels computed from the NB algorithms
	//Constructor of the class, initialize the training and data Set
	public NaiveBayes(Object[][] trainingSet, Object[][] dataSet)
	{
		updatePrecision(trainingSet);

		this.trainingSet=dataOptimization(trainingSet);
		this.dataSet=dataOptimization(dataSet);
		this.labels=new Object[dataSet.length];
		fs=new FeatureSet[dataSet[0].length];
		inizialization();
		if (unlabeled) loadUnlabeledTS();
		
	}
	
	private void setElement(Object[] sv)
	{
		element=new Object[sv.length];
		for(int i=0;i<sv.length;i++)
		{
			if (sv[i] instanceof Double) {
				element[i]=(int)(round((double)sv[i],precision)*Math.pow(10,precision));
			}
			else
			{
				element[i]=sv[i];
			}
		}
	}
	
	public double interfaceComputeElement(Object[] sv)
	{
		setElement(sv);
		return computeElement(element);
	}
	
	public NaiveBayes(Object[][] trainingSet)
	{
		updatePrecision(trainingSet);
		
		this.trainingSet=dataOptimization2(trainingSet);
		//this.labels=new Object[dataSet.length];
		fs=new FeatureSet[trainingSet[0].length-1];
		inizialization();
		if (unlabeled) loadUnlabeledTS();
		
	}
	
	private void loadUnlabeledTS()
	{
		
		int size=trainingSet.length/(precision*2);
		if (size>dataSet.length) size=dataSet.length;
		int length=trainingSet[0].length-1;
		unlabeledTrainingSet=new Object[size][length+1];
		double index=0;
		for (int i=0;i<size;i++ )
		{
			//System.out.println((int)(Math.random()*dataSet.length));
			for(int j=0;j<length;j++)
			{
				unlabeledTrainingSet[i][j]=dataSet[i][j];
			}
			unlabeledTrainingSet[(int)index][length]=0;
		}
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
		List<Object[]> obj=new ArrayList<Object[]>();
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
	
	private Object[][] dataOptimization2(Object[][] set)
	{
		List<Object[]> obj=new ArrayList<Object[]>();
		int count=0;
		
		for (int i=0;i<set.length;i++)
		{
			Object[] tmp=new Object[set[0].length];
			for (int j=0;j<set[0].length;j++)
			{
				if (set[i][j] instanceof Double) {
					tmp[j]=(int)(round((double)set[i][j],precision)*Math.pow(10,precision));
				}
				else
				{
					tmp[i]=set[i][j];
				}
				
			}
			if (listContains(obj,tmp)<3)
			{
				obj.add(tmp);
				count++;
			}
		}
		Object[][] o=new Object[count][set[0].length];
		return obj.toArray(o);
	}
	
	private int listContains(List<Object[]> lst, Object[] vct)
	{
		int count=0;
		int num=0;
		for (Object[] o :lst)
		{
			count=0;
			for (int i=0;i<o.length;i++)
			{
				if (o[i]==vct[i])
					count++;
			}
			if (count==vct.length)
				num++;
		}
		return num;
	}
	
	public void nBayes_eMaximization(int length)
	{
		Object o_true=10;
		double tmp=0;
		Object o_false=0;
		for (Object[] obj : unlabeledTrainingSet)
		{
			tmp=computeElement(signatureVectorExtraction(obj, length));
			obj[length-1]=tmp;
			for(int i=0;i<length-1;i++)
			{
				fs[i].addProbFSet(obj[i], tmp);
				count_false+=1;
				count_true+=1;
			}
		}
		trainingSet=(Object[][]) ArrayUtils.addAll(trainingSet, unlabeledTrainingSet);
		//inizialization();
		for (int k=0; k<5;k++)
			for (Object[] obj: trainingSet)
			{
				//System.out.println(obj[length-1]);
				if ((obj[length-1].equals(o_true))||(obj[length-1].equals(o_false))) continue;
				else
					obj[length-1]=computeElement(signatureVectorExtraction(obj, length));
				//System.out.println(obj[length-1]);
			}
		
	}
	
	private Object[] signatureVectorExtraction(Object[] obj, int length)
	{
		Object[] tmp=Arrays.copyOfRange(obj,0,length-1);
		return tmp;
	}
	

	private static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	private double computeElement(Object[] sv)
	{
		double tmp=0;
		double featureValue=0;
		double prob_true=1.0;
		double prob_false=1.0;
		double p_cond_true=0;
		double p_cond_false=0;
		for (int i=0;i<sv.length;i++)
		{
			featureValue=fs[i].getHMvalue(sv[i], true);
			p_cond_true=(featureValue+smooth_ce);
			tmp=(p_cond_true/(count_true+smooth_ce));
			prob_true*=tmp;
			featureValue=fs[i].getHMvalue(sv[i], false);
			p_cond_false=(featureValue+smooth_ce);
			tmp=(p_cond_false/(count_false+smooth_ce));
			prob_false*=tmp;
		}
		if ((prob_true==0)&&(prob_false==0))
			return 0;
		//prob_true*=(((double)count_true+smooth_c)/(count_true+count_false+smooth_c));
		//prob_false*=(((double)count_false+smooth_c)/(count_true+count_false+smooth_c));
		prob_true=(count_true!=0)?prob_true*(((double)count_true+smooth_c)/(count_true+count_false+smooth_c)):0;
		prob_false=(count_false!=0)? prob_false*(((double)count_false+smooth_c)/(count_true+count_false+smooth_c)):0;
		tmp=prob_true+prob_false;
		prob_true=prob_true/tmp;
		prob_false=prob_false/tmp;
		//if(prob_true<prob_false)
		//	return 0;
		return prob_true;
	}
	
	
	
	public void run()
	{
		for (int i=0; i< dataSet.length;i++)
		{
			if(computeElement(dataSet[i])>0.9)
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
	
	public Boolean getLabel(int index)
	{
		Object o=true;
		if (labels[index].equals(o)) return true;
		return false;
	}
	
	public int getPrecision()
	{
		return precision;
	}
}