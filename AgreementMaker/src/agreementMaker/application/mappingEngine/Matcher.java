package agreementMaker.application.mappingEngine;

import agreementMaker.AMException;

public interface Matcher {
	
	public void match() throws Exception;
	
	public void select();

    public AlignmentSet getAlignmentSet();

    public AlignmentSet getClassAlignmentSet();

    public AlignmentSet getPropertyAlignmentSet();

    public AlignmentSet getInstanceAlignmentSet();

}
