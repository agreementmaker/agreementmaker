package am.app.mappingEngine.referenceAlignment;

import am.app.mappingEngine.DefaultMatcherParameters;

public class ReferenceAlignmentParameters extends DefaultMatcherParameters {

	private static final long serialVersionUID = -565356899065786968L;
	
	public String fileName;
	
	/**
	 * Format strings are static fields of ReferenceAlignmentMatcher.
	 * 
	 * @see {@link ReferenceAlignmentMatcher#OAEI},
	 *      {@link ReferenceAlignmentMatcher#OLD_OAEI},
	 *      {@link ReferenceAlignmentMatcher#REF2a},
	 *      {@link ReferenceAlignmentMatcher#REF2b},
	 *      {@link ReferenceAlignmentMatcher#REF2c},
	 *      {@link ReferenceAlignmentMatcher#REF3},
	 *      {@link ReferenceAlignmentMatcher#REF5}
	 */
	public String format;
	
	public boolean onlyEquivalence = false;
	public boolean skipClasses = false;
	public boolean skipProperties = false;
	public boolean displayPaneEmptyAlignment = true; // if false avoid to display a message pane when the alignment is empty
}
