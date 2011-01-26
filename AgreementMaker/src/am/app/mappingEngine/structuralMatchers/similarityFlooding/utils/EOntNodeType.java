package am.app.mappingEngine.structuralMatchers.similarityFlooding.utils;

import am.app.mappingEngine.AbstractMatcher.alignType;

public class EOntNodeType{
	
	public enum EOntologyNodeType {
		CLASS, PROPERTY
	}
	
	public static alignType toAlignType(EOntologyNodeType et){
		if(et == EOntologyNodeType.CLASS){
			return alignType.aligningClasses;
		}
		else{
			return alignType.aligningProperties;
		}
	}
	
	public static EOntologyNodeType toEOntologyNodeType(alignType at){
		if(at == alignType.aligningClasses){
			return EOntologyNodeType.CLASS;
		}
		else{
			return EOntologyNodeType.PROPERTY;
		}
	}
}
