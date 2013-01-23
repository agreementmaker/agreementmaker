package am.app.mappingEngine.IterativeInstanceStructuralMatcher;

import java.util.ArrayList;
import java.util.List;

public class ClassSimilarity {
	double syntactic;
	double subclasses;
	double superclasses;
	double restrictions;
	List<Double> doubles;
	
	public double getSyntactic() {
		return syntactic;
	}
	
	public void setSyntactic(double syntactic) {
		this.syntactic = syntactic;
	}
	
	public double getSubclasses() {
		return subclasses;
	}
	
	public void setSubclasses(double subclasses) {
		this.subclasses = subclasses;
	}
	
	public double getSuperclasses() {
		return superclasses;
	}
	
	public void setSuperclasses(double superclasses) {
		this.superclasses = superclasses;
	}
	
	public double getRestrictions() {
		return restrictions;
	}
	
	public void setRestrictions(double restrictions) {
		this.restrictions = restrictions;
	}
	
	@Override
	public String toString() {
		return "SYN:"+syntactic+" SUB:"+subclasses+" SUP:"+superclasses+" RES:"+restrictions;
	}
	
	public double getSimilarity(){
		doubles = new ArrayList<Double>();
		Utils.addOrdered(doubles,syntactic);
		Utils.addOrdered(doubles,subclasses);
		Utils.addOrdered(doubles,superclasses);
		Utils.addOrdered(doubles,restrictions);
		return (doubles.get(0)+doubles.get(1))/2;
	}
}
