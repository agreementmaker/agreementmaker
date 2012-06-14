package am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher;

import java.util.List;

import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.TokenInstanceMatcher.Aggregation;
import am.app.mappingEngine.instanceMatchers.tokenInstanceMatcher.TokenInstanceMatcher.Modality;

public class TokenInstanceMatcherParameters extends DefaultMatcherParameters {

	private static final long serialVersionUID = 3486782614841880143L;
	
	/**
	 * The modality controls which properties the TIM will use. We can use
	 * syntactic properties, semantic properties, or both.
	 * 
	 * @see {@link TokenInstanceMatcher.Modality}
	 */
	public Modality modality;
	
	/**
	 * The type of aggregation the TIM will perform on the contexts.
	 * 
	 * @see {@link TokenInstanceMatcher.Aggregation}
	 */
	public Aggregation aggregation;
	
	/**
	 * 
	 */
	public List<String> selectedProperties;
}
