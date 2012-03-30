package iterativeinstancestructuralmatcher.internal;

import java.util.ArrayList;

public class PropertySimilarity {
	double syntactic;
	double rangeAndDomain;
	double values;
	double subProperties;
	ArrayList<Double> doubles;
	
	public double getSyntactic() {
		return syntactic;
	}
	
	public void setSyntactic(double syntactic) {
		this.syntactic = syntactic;
	}
	
	public double getRangeAndDomain() {
		return rangeAndDomain;
	}
	
	public void setRangeAndDomain(double rangeAndDomain) {
		this.rangeAndDomain = rangeAndDomain;
	}
	
	public double getValues() {
		return values;
	}
	
	public void setValues(double values) {
		this.values = values;
	}
	
	public double getSubProperties() {
		return subProperties;
	}
	
	public void setSubProperties(double subProperties) {
		this.subProperties = subProperties;
	}	
	
	@Override
	public String toString() {
		return "SYN:"+syntactic+" RAD:"+rangeAndDomain+" VAL:"+values+" SUB:"+subProperties;
	}
	
	public double getSimilarity(){
		doubles = new ArrayList<Double>();
		Utils.addOrdered(doubles,syntactic);
		Utils.addOrdered(doubles,rangeAndDomain);
		Utils.addOrdered(doubles,subProperties);
		Utils.addOrdered(doubles,values);
		//System.out.println(doubles);
		return (doubles.get(0)+doubles.get(1))/2;
	}
	
	
}
