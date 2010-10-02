package am.app.mappingEngine.parametricStringMatcher;

import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.StringUtil.NormalizerParameter;

public class ParametricStringParameters extends AbstractParameters {

	public final static String EDIT = "Levenshtein Edit Distance";
	public final static String JARO = "Jaro Winkler";
	public final static String QGRAM = "Q-Gram";
	public final static String SUB = "Substring metric";
	public final static String AMSUB = "AM Substring metric";
	public final static String ISUB = "I-SUB";
	public final static String AMSUB_AND_EDIT = "AMsubstring + editDistance"; //0.6*amsub + 0.4*editdistance
	
	public String measure = EDIT;
	
	public double localWeight = 0;
	public double labelWeight = 0.7;
	public double commentWeight = 0.2;
	public double seeAlsoWeight = 0.05;
	public double isDefinedByWeight = 0.05;
	
	public boolean redistributeWeights = true;
	
	public boolean useLexicons = false;
	public boolean useBestLexSimilarity = true;
	public double lexOntSynonymWeight = 0.90;
	public double lexOntDefinitionWeight = 0.10;
	public double lexWNSynonymWeight = 0.90;
	public double lexWNDefinitionWeight = 0.10;
	
	//Normalization operations
	NormalizerParameter normParameter = new NormalizerParameter();
	
	public void normalizeWeights() {
		double totWeight = getTotWeight();
		if( totWeight > 1 ) {
			if( useLexicons ) {
				lexOntSynonymWeight = lexOntSynonymWeight / totWeight;
				lexOntDefinitionWeight = lexOntDefinitionWeight / totWeight;
				lexWNSynonymWeight = lexWNSynonymWeight / totWeight;
				lexWNDefinitionWeight = lexWNDefinitionWeight / totWeight;
			} else {
				localWeight = localWeight / totWeight;
				labelWeight = labelWeight / totWeight;
				commentWeight = commentWeight / totWeight;
				seeAlsoWeight = seeAlsoWeight / totWeight;
				isDefinedByWeight = isDefinedByWeight / totWeight;
			}
		}
	}
	
	public double getTotWeight() {
		if( useLexicons ) {
			return lexOntSynonymWeight + lexOntDefinitionWeight + lexWNSynonymWeight + lexWNDefinitionWeight;
		} else {
			return localWeight+labelWeight+commentWeight+seeAlsoWeight+isDefinedByWeight;
		}
	}

	public void initForOAEI2009() {
		localWeight = 0;
		labelWeight = 0.65d;
		commentWeight = 0.25d;
		seeAlsoWeight = 0.05d;
		isDefinedByWeight = 0.05d;
		measure = AMSUB_AND_EDIT;
		normParameter = new NormalizerParameter();
		normParameter.setForOAEI2009();
		redistributeWeights = true;
	}
	
}
