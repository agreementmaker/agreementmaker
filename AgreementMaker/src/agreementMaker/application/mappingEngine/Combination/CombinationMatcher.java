package agreementMaker.application.mappingEngine.Combination;

import java.util.ArrayList;


import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.AlignmentMatrix;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluationData;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluator;
import agreementMaker.application.ontology.Node;

public class CombinationMatcher extends AbstractMatcher {

	
	public CombinationMatcher() {
		super();
		

		needsParam = true; // we need to set the MCP before running DSI
		
		
		minInputMatchers = 2;
		maxInputMatchers = ANY_INT;
		
		//I can't initialize the parametersPanel in here because i need to pass the inputmatchers as parameters but the input matchers will be set later so I will initialize the panel in the getParametersPanerl() method
	}

	
	protected void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		CombinationParameters cp = (CombinationParameters)param;
		
		//if weights are manually assigned then they are already created in the parameters
		//if not, we have to run the quality evaluation
		if(cp.qualityEvaluation) {
			AbstractMatcher a;
			String quality = cp.quality;
			QualityEvaluationData q;
			for(int i = 0; i < inputMatchers.size(); i++) {
				a = inputMatchers.get(i);
				q = QualityEvaluator.evaluate(matcher, quality);
				cp.matchersWeights.add(q);
			}
		}
	}
	
	
	// overriding the abstract method in order to keep track of what kind of nodes we are aligning
    protected AlignmentMatrix alignProperties(ArrayList<Node> sourcePropList, ArrayList<Node> targetPropList) {
		return alignNodesOneByOne(sourcePropList, targetPropList, alignType.aligningProperties );
	}

	// overriding the abstract method in order to keep track of what kind of nodes we are aligning
    protected AlignmentMatrix alignClasses(ArrayList<Node> sourceClassList, ArrayList<Node> targetClassList) {
		return alignNodesOneByOne(sourceClassList, targetClassList, alignType.aligningClasses);
	}
	
	// this method is exactly similar to the abstract method, except we pass one extra parameters to the alignTwoNodes function
    protected AlignmentMatrix alignNodesOneByOne(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes) {
		AlignmentMatrix matrix = new AlignmentMatrix(sourceList.size(), targetList.size());
		Node source;
		Node target;
		Alignment alignment; //Temp structure to keep sim and relation between two nodes, shouldn't be used for this purpose but is ok
		for(int i = 0; i < sourceList.size(); i++) {
			source = sourceList.get(i);
			for(int j = 0; j < targetList.size(); j++) {
				target = targetList.get(j);
				alignment = alignTwoNodes(source, target, typeOfNodes);
				matrix.set(i,j,alignment);
			}
		}
		return matrix;
	}
    

	protected Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		return null;
		/*
		CombinationParameters parameters = (CombinationParameters)param;
		int sourceindex = source.getIndex();
		int targetindex = target.getIndex();
		double max = 0;// keep the max sim between all input matrix for the cell (sourceindex, targetindex)
		double min = 1;// keep the min sim between all input matrix for the cell (sourceindex, targetindex)
		double sum = 0;// keep the sum of sims between all input matrix for the cell (sourceindex, targetindex)
		double weight;   
		double weightedSum = 0;// keep the weighted sum of sims between all input matrix for the cell (sourceindex, targetindex)
		double sumOfWeights = 0; //sum of weights
		double sim; 
		AbstractMatcher a;
		for(int i = 0; i < inputMatchers.size();i++) {
			//for each input matcher...
			a = inputMatchers.get(i);
			//get the sim for this two nodes in the input matcher matrix
			if(typeOfNodes == alignType.aligningClasses && a.areClassesAligned())
				sim = a.getClassesMatrix().get(sourceindex, targetindex).getSimilarity();
			else if(typeOfNodes == alignType.aligningProperties && a.arePropertiesAligned()) {
				sim = a.getPropertiesMatrix().get(sourceindex, targetindex).getSimilarity();
			}
			else sim = 0;
			//calculate sum for average combination
			sum += sim;
			//calculate max for max combination
			if(sim > max)
				max = sim;
			//calculate min for min evaluation
			if(sim < min)
				min = sim;
			//calculate weighted sum
			weight = parameters.matchersWeights[i];
			sumOfWeights+= weight;
			weightedSum += (weight * sim); 
		}
		//select the final similarity combined value depending on the user selected combination type.
		if(parameters.combinationType.equals(CombinationParameters.MAXCOMB)) {
			sim = max;
		}
		else if(parameters.combinationType.equals(CombinationParameters.MINCOMB)) {
			sim = min;
		}
		else if(parameters.combinationType.equals(CombinationParameters.AVERAGECOMB)) {
			sim =  sum/ (double)inputMatchers.size();
		}
		else if(parameters.combinationType.equals(ManualCombinationParameters.CombinationParameters)) {
			if(sumOfWeights != 0)
				sim = weightedSum/ sumOfWeights;
			else sim = 0;
		}
		else throw new RuntimeException("DEVELOPMENT ERROR: combination type selected is not implemented");
		return new Alignment(source, target, sim, Alignment.EQUIVALENCE);
		*/
	}

	public AbstractMatcherParametersPanel getParametersPanel() {
		return new CombinationParametersPanel(inputMatchers);
	}
	
	public String getDescriptionString() {
		String result = "Allows user to combine different matchings through different operations to be selected as parameters.\n";
		result += "For each pair (sourcenode, targetnode) a new similarity value is calculated with selected operation.\n";
		result += "Maximum value between similarity values for that pair in all input matchings.\n";
		result += "Minimum value between similarity values for that pair in all input matchings.\n";
		result += "Average of similarity values for that pair in all input matchings.\n";
		result += "Weighted average of similarity values for that pair in all input matchings. In this case user manually defines weights.\n";
		result += "When the similarity matrix is built mapping are choosen with the same selection process \n";
		result += "which considers threshold and maximum number of relations for each concept.\n";
		return result;
	}
}
