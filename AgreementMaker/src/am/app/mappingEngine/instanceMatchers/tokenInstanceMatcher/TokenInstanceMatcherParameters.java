package am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher;

import java.util.List;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.TokenInstanceMatcher.Modality;

public class TokenInstanceMatcherParameters extends DefaultMatcherParameters{
	Modality modality;
	List<String> selectedProperties;
}
