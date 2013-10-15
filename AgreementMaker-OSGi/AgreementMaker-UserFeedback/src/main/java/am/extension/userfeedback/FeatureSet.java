package am.extension.userfeedback;

import java.util.HashMap;

public class FeatureSet {
	private HashMap<Object, Integer> hm_true=new HashMap<Object,Integer>();
	private HashMap<Object, Integer> hm_false=new HashMap<Object,Integer>();
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
				hm_true.put(value, new Integer(++tmp));
			}
			else
			{
				hm_true.put(value, new Integer(1));
			}
		}
		else
		{
			if(hm_false.containsKey(value))
			{
				tmp=hm_false.get(value).intValue();
				hm_false.put(value, new Integer(++tmp));
			}
			else
			{
				hm_false.put(value, new Integer(1));
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
