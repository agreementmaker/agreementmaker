package am.app.mappingEngine.parametricStringMatcher;

import am.app.mappingEngine.AbstractParameters;
import am.app.mappingEngine.StringUtil.NormalizerParameter;
import am.app.mappingEngine.oaei.OAEI_Track;

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
	public boolean lexExtendSynonyms = false; // use the Subconcept Synonyms to extend the synonyms.
	public double lexOntSynonymWeight = 0.90;
	public double lexOntDefinitionWeight = 0.10;
	public double lexWNSynonymWeight = 0.90;
	public double lexWNDefinitionWeight = 0.10;
	
	//Normalization operations
	public NormalizerParameter normParameter = new NormalizerParameter();
	
	public ParametricStringParameters() { super(); }
	
	public ParametricStringParameters(double threshold, int maxSourceAlign,
			int maxTargetAlign) {
		super(threshold, maxSourceAlign, maxTargetAlign);
	}	

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
		measure = AMSUB_AND_EDIT;
		normParameter = new NormalizerParameter();
		normParameter.setForOAEI2009();
		
		useLexicons = true;
		useBestLexSimilarity = true;
		
		redistributeWeights = true;
		
	}
	
	public AbstractParameters initForOAEI2010(OAEI_Track t) {
		
		switch( t ) {
		case Anatomy:
			localWeight = 0;
			labelWeight = 0.65d;
			commentWeight = 0.25d;
			seeAlsoWeight = 0.05d;
			isDefinedByWeight = 0.05d;
			
			useLexicons = true;
			useBestLexSimilarity = true;
			measure = AMSUB_AND_EDIT;
			normParameter = new NormalizerParameter();
			normParameter.setForOAEI2009();
			redistributeWeights = true;
			break;
		case Benchmarks:
			localWeight = 0.33;
			labelWeight = 0.34d;
			commentWeight = 0.33d;
			seeAlsoWeight = 0.00d;
			isDefinedByWeight = 0.00d;
			
			useLexicons = false;
			useBestLexSimilarity = false;
			measure = AMSUB_AND_EDIT;
			normParameter = new NormalizerParameter();
			normParameter.setForOAEI2009();
			redistributeWeights = true;
			break;
		case Conference:
			localWeight = 0.5d;
			labelWeight = 0.5d;
			commentWeight = 0.5d;
			seeAlsoWeight = 0.0d;
			isDefinedByWeight = 0.0d;
			
			useLexicons = false;
			useBestLexSimilarity = false;
			
			measure = AMSUB_AND_EDIT;
			normParameter = new NormalizerParameter();
			normParameter.setForOAEI2009();
			redistributeWeights = true;
			break;
		default:
			localWeight = 0;
			labelWeight = 0.65d;
			commentWeight = 0.25d;
			seeAlsoWeight = 0.05d;
			isDefinedByWeight = 0.05d;
			
			useLexicons = true;
			useBestLexSimilarity = true;
			measure = AMSUB_AND_EDIT;
			normParameter = new NormalizerParameter();
			normParameter.setForOAEI2009();
			redistributeWeights = true;
		}
		
		return this;
	}
	
}
