package am.app.mappingEngine.IterativeInstanceStructuralMatcher;

import am.app.mappingEngine.AbstractParameters;

public class IterativeInstanceStructuralParameters extends AbstractParameters {
	boolean considerIndividuals;
	
	double superclassThreshold = 0.6;
	double propertyValuesThreshold = 0.5;
	double rangeDomainThreshold = 0.89;
	double propertyUsageThreshold = 0.6;
	
	boolean useSuperclasses;
	boolean usePropertyValues;
	boolean useRangeDomain;
	boolean usePropertyUsage;
	
	boolean boostSuperclasses;
	boolean boostPropertyValues;
	boolean boostRangeDomain;
	boolean boostPropertyUsage;
	boolean boostSubclassOf;
	
	
	public IterativeInstanceStructuralParameters() { super(); }
	public IterativeInstanceStructuralParameters(double th, int s, int t) { super(th, s, t); }
	
	public boolean isConsiderIndividuals() {
		return considerIndividuals;
	}
	
	public void setConsiderIndividuals(boolean considerIndividuals) {
		this.considerIndividuals = considerIndividuals;
	}
	
	public double getSuperclassThreshold() {
		return superclassThreshold;
	}
	
	public void setSuperclassThreshold(double superclassThreshold) {
		this.superclassThreshold = superclassThreshold;
	}
	
	public double getPropertyValuesThreshold() {
		return propertyValuesThreshold;
	}
	
	public void setPropertyValuesThreshold(double propertyValuesThreshold) {
		this.propertyValuesThreshold = propertyValuesThreshold;
	}
	
	public double getRangeDomainThreshold() {
		return rangeDomainThreshold;
	}
	
	public void setRangeDomainThreshold(double rangeDomainThreshold) {
		this.rangeDomainThreshold = rangeDomainThreshold;
	}
	
	public double getPropertyUsageThreshold() {
		return propertyUsageThreshold;
	}
	
	public void setPropertyUsageThreshold(double propertyUsageThreshold) {
		this.propertyUsageThreshold = propertyUsageThreshold;
	}

	public boolean isUseSuperclasses() {
		return useSuperclasses;
	}

	public void setUseSuperclasses(boolean useSuperclasses) {
		this.useSuperclasses = useSuperclasses;
	}

	public boolean isUsePropertyValues() {
		return usePropertyValues;
	}

	public void setUsePropertyValues(boolean usePropertyValues) {
		this.usePropertyValues = usePropertyValues;
	}

	public boolean isUseRangeDomain() {
		return useRangeDomain;
	}

	public void setUseRangeDomain(boolean useRangeDomain) {
		this.useRangeDomain = useRangeDomain;
	}

	public boolean isUsePropertyUsage() {
		return usePropertyUsage;
	}

	public void setUsePropertyUsage(boolean usePropertyUsage) {
		this.usePropertyUsage = usePropertyUsage;
	}
	
	public IterativeInstanceStructuralParameters setForOAEI2010() {
		//allBoost();
		setConsiderIndividuals(true);
		setPropertyUsageThreshold(0.6);
		setPropertyValuesThreshold(0.5);
		setRangeDomainThreshold(0.89);
		setSuperclassThreshold(0.6);
		setUsePropertyUsage(true);
		setUsePropertyValues(true);
		setUseRangeDomain(true);
		setUseSuperclasses(true);
		return this;
	}
	
	public void allBoost(){
		boostPropertyUsage = true;
		boostPropertyValues = true;
		boostRangeDomain = true;
		boostSubclassOf = true;
		boostSuperclasses = true;
	}
}
