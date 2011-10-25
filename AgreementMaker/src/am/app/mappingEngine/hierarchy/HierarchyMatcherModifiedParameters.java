package am.app.mappingEngine.hierarchy;

import com.hp.hpl.jena.util.LocationMapper;

import am.app.mappingEngine.AbstractParameters;

public class HierarchyMatcherModifiedParameters extends AbstractParameters {
	
	public LocationMapper mapper;
	
	public HierarchyMatcherModifiedParameters() {
		super();
	}
	
	public HierarchyMatcherModifiedParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign){
		super(threshold, maxSourceAlign, maxTargetAlign);
	}
	

}
