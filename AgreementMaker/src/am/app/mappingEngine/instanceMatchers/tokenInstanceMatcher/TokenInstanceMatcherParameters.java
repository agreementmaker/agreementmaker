package am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher;

import java.util.List;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.TokenInstanceMatcher.Aggregation;
import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.TokenInstanceMatcher.Modality;

public class TokenInstanceMatcherParameters extends DefaultMatcherParameters{
	public Modality modality;
	public List<String> selectedProperties;
	public Aggregation aggregation;
}
