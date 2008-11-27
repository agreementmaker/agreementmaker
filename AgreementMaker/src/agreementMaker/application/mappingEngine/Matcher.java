package agreementMaker.application.mappingEngine;

import agreementMaker.AMException;

public interface Matcher {
	
	public void match() throws AMException;

    public AlignmentSet getAlignmentSet();

    public AlignmentSet getClassAlignmentSet();

    public AlignmentSet getPropertyAlignmentSet();

    public AlignmentSet getInstanceAlignmentSet();

}
