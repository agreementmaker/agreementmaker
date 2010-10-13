package am.app.mappingEngine;

import java.io.Serializable;

public interface Matcher extends Serializable{
	
	public void match() throws Exception;
	
	public void select();

    public AlignmentSet getAlignmentSet();

    public AlignmentSet getClassAlignmentSet();

    public AlignmentSet getPropertyAlignmentSet();

    public AlignmentSet getInstanceAlignmentSet();

}
