package am.app.mappingEngine.mediatingMatcher;

import am.app.mappingEngine.DefaultMatcherParameters;

public class MediatingMatcherParameters extends DefaultMatcherParameters {

	private static final long serialVersionUID = 3481333025573474720L;
	
	public MediatingMatcherParameters() { super(); }
	public MediatingMatcherParameters( double threshold, int sourceMaxAlign, int targetMaxAlign ) {
		super(threshold, sourceMaxAlign, targetMaxAlign);
	}

	public String mediatingOntology;
	
	public boolean loadSourceBridge = false;
	public String sourceBridge;
	
	public boolean loadTargetBridge = false;
	public String targetBridge;
	
}
