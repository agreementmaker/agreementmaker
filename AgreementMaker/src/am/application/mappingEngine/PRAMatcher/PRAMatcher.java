package am.application.mappingEngine.PRAMatcher;

import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.Alignment;
import am.application.mappingEngine.AbstractMatcher.alignType;
import am.application.mappingEngine.StringUtil.Normalizer;
import am.application.mappingEngine.StringUtil.NormalizerParameter;
import am.application.ontology.Node;

public class PRAMatcher extends AbstractMatcher 
{
	
	
	//Constructor
	public PRAMatcher()
	{
		super();
		minInputMatchers = 1;
	}
	
	/**Set all alignment sim to a random value between 0 and 1*/
	public Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) 
	{
		

		NormalizerParameter param = null;
		Normalizer norm = null;
		
		String sLabel = source.getLabel();
		String tLabel = target.getLabel();
		
			if(sLabel.equalsIgnoreCase(tLabel))
				return new Alignment( source, target, 1, Alignment.EQUIVALENCE);
			//all normalization without stemming and digits return 0.95
			
			param = new NormalizerParameter();
			param.setAllTrue();
			param.normalizeDigit = false;
			param.stem = false;
			
			norm = new Normalizer(param);
			
			String sProcessedLabel = norm.normalize(sLabel);
			String tProcessedLabel = norm.normalize(tLabel);
			
			if(sProcessedLabel.equals(tProcessedLabel))
				return new Alignment( source, target, 0.95d, Alignment.EQUIVALENCE);
			//apply stem return 0.90 
	
			param.setAllfalse();
			param.stem = true;
			
			norm = new Normalizer(param);
			sProcessedLabel = norm.normalize(sLabel);
			tProcessedLabel = norm.normalize(tLabel);
			if(sProcessedLabel.equals(tProcessedLabel))
				return new Alignment( source, target, 0.9d, Alignment.EQUIVALENCE);
			
			//apply normDigits return 0.8
			
			param.setAllfalse();
			param.normalizeDigit = true;
			norm = new Normalizer(param);
			sProcessedLabel = norm.normalize(sLabel);
			tProcessedLabel = norm.normalize(tLabel);
			if(sProcessedLabel.equals(tProcessedLabel))
				return new Alignment( source, target, 0.8d, Alignment.EQUIVALENCE);
		

		
		
		
			return new Alignment(source, target, 0.0d, Alignment.EQUIVALENCE);
	}
}
