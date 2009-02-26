package am.application.mappingEngine.parametricStringMatcher;

import am.application.mappingEngine.AbstractParameters;
import am.application.mappingEngine.StringUtil.NormalizerParameter;

public class ParametricStringParameters extends AbstractParameters {

	public final static String EDIT = "Levenshtein Edit Distance";
	public final static String JARO = "Jaro Winkler";
	public final static String QGRAM = "Q-Gram";
	public final static String SUB = "Substring metric";
	public final static String AMSUB = "AM Substring metric";
	public final static String ISUB = "I-SUB";
	
	public String measure = EDIT;
	
	public double localWeight = 0;
	public double labelWeight = 0.7;
	public double commentWeight = 0.2;
	public double seeAlsoWeight = 0.05;
	public double isDefinedByWeight = 0.05;
	
	
	public boolean redistributeWeights = true;
	
	//Normalization operations
	NormalizerParameter normParameter = new NormalizerParameter();
	
	public void normalizeWeights() {
		double totWeight = getTotWeight();
		if( totWeight > 1 ) {
			localWeight = localWeight / totWeight;
			labelWeight = labelWeight / totWeight;
			commentWeight = commentWeight / totWeight;
			seeAlsoWeight = seeAlsoWeight / totWeight;
			isDefinedByWeight = isDefinedByWeight / totWeight;
		}
	}
	
	public double getTotWeight() {
		return localWeight+labelWeight+commentWeight+seeAlsoWeight+isDefinedByWeight;
	}
	
}
