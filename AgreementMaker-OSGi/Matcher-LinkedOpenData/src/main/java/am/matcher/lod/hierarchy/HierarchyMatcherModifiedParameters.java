package am.matcher.lod.hierarchy;

import am.app.mappingEngine.DefaultMatcherParameters;

import com.hp.hpl.jena.util.LocationMapper;

public class HierarchyMatcherModifiedParameters extends DefaultMatcherParameters {

	private static final long serialVersionUID = 6922539671804270468L;
	
	public LocationMapper mapper;
	
	public HierarchyMatcherModifiedParameters() {
		super();
	}
	
	public HierarchyMatcherModifiedParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign){
		super(threshold, maxSourceAlign, maxTargetAlign);
	}
	

}
