package am.app.mappingEngine.referenceAlignment;

import am.app.mappingEngine.AbstractParameters;

public class ReferenceAlignmentParameters extends AbstractParameters {

	private static final long serialVersionUID = -565356899065786968L;
	
	public String fileName;
	public String format;
	public boolean onlyEquivalence = false;
	public boolean skipClasses = false;
	public boolean skipProperties = false;
	public boolean displayPaneEmptyAlignment = true; // if false avoid to display a message pane when the alignment is empty
}
