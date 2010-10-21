package am.app.mappingEngine;

import java.io.Serializable;

public interface Matcher extends Serializable{
	
	public void match() throws Exception;
	
	public void select();

    public Alignment getAlignmentSet();

    public Alignment getClassAlignmentSet();

    public Alignment getPropertyAlignmentSet();

    public Alignment getInstanceAlignmentSet();

}
