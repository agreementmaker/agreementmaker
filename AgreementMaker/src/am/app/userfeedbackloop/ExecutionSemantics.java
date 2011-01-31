package am.app.userfeedbackloop;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;

public interface ExecutionSemantics {

	
	List<AbstractMatcher> getComponentMatchers();
	Alignment<Mapping> getAlignment();
	
}
