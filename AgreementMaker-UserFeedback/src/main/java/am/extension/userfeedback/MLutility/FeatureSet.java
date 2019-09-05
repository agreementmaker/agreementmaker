/*
 * 	Francesco Loprete October 2013
 */

package am.extension.userfeedback.MLutility;

import java.util.HashMap;

public class FeatureSet {
	private HashMap<Object, Double> hm_true=new HashMap<Object,Double>();
	private HashMap<Object, Double> hm_false=new HashMap<Object,Double>();
	private String feature_code;

	
	//private String label;
	int tmp;

	public FeatureSet(String fCode)
	{
		this.feature_code=fCode;
		//this.label=label;
	}
	public void updateHM(Object value, boolean label)
	{
		if (label)
		{
			if(hm_true.containsKey(value))
			{
				tmp=hm_true.get(value).intValue();
				hm_true.put(value, new Double(++tmp));
			}
			else
			{
				hm_true.put(value, new Double(1));
			}
		}
		else
		{
			if(hm_false.containsKey(value))
			{
				tmp=hm_false.get(value).intValue();
				hm_false.put(value, new Double(++tmp));
			}
			else
			{
				hm_false.put(value, new Double(1));
			}
		}
	}
	public double getHMvalue(Object value, boolean label)
	{
		if(label)
		{
			if(hm_true.containsKey(value))
			{
				return (double)hm_true.get(value).intValue();
				
			}
		}
		else
		{
			if(hm_false.containsKey(value))
			{
				return (double)hm_false.get(value).intValue();
				
			}
		}
		return 0.0;
		
	}
	
	public void addProbFSet(Object value, double prob)
	{
		if(hm_true.containsKey(value))
		{
			tmp=hm_true.get(value).intValue();
			hm_true.put(value, new Double(prob+tmp));
		}
		else
		{
			hm_true.put(value, new Double(prob));
		}

		if(hm_false.containsKey(value))
		{
			tmp=hm_false.get(value).intValue();
			hm_false.put(value, new Double((1-prob)+tmp));
		}
		else
		{
			hm_false.put(value, new Double(1-prob));
		}
	}
	
	public void printHM()
	{
		System.out.println("True value");
		System.out.println(hm_true.toString());
		System.out.println("False value");
		System.out.println(hm_false.toString());
		
	}
	
	public String getFCode()
	{
		return feature_code;
	}
//	public String getLabel()
//	{
//		return label;
//	}

}
