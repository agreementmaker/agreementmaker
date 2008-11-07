package agreementMaker.application.mappingEngine;

public interface Matcher {
	
	public void match();

    public AlignmentSet getAlignmentSet();

    public AlignmentSet getClassAlignmentSet();

    public AlignmentSet getPropertyAlignmentSet();

    public AlignmentSet getInstanceAlignmentSet();

}
