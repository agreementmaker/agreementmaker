package am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher;

import java.util.List;

import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.TokenInstanceMatcher.Modality;

public class TokenInstanceMatcherParameters extends AbstractParameters{
	Modality modality;
	List<String> selectedProperties;
}
