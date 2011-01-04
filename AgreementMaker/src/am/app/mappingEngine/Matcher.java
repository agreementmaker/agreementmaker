package am.app.mappingEngine;

import java.io.Serializable;

public interface Matcher extends Serializable{
	
	public void match() throws Exception;
	
	public void select();

    public Alignment<Mapping> getAlignment();

    public Alignment<Mapping> getClassAlignmentSet();

    public Alignment<Mapping> getPropertyAlignmentSet();

    public Alignment<Mapping> getInstanceAlignmentSet();

}
