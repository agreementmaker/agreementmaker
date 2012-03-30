package hierarchymatcher.internal;

import com.hp.hpl.jena.util.LocationMapper;

import am.app.mappingEngine.DefaultMatcherParameters;

public class HierarchyMatcherModifiedParameters extends DefaultMatcherParameters {
	
	public LocationMapper mapper;
	
	public HierarchyMatcherModifiedParameters() {
		super();
	}
	
	public HierarchyMatcherModifiedParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign){
		super(threshold, maxSourceAlign, maxTargetAlign);
	}
	

}
