package agreementMaker.application.mappingEngine.Combination;

import java.util.ArrayList;


import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.AlignmentMatrix;
import agreementMaker.application.mappingEngine.AbstractMatcher.alignType;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluationData;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluator;
import agreementMaker.application.ontology.Node;

public class CombinationMatcher extends AbstractMatcher {
	
	private QualityEvaluationData[] qualityWeights;
	
	public CombinationMatcher() {
		super();
		

		needsParam = true; // we need to set the MCP before running DSI
		
		
		minInputMatchers = 2;
		maxInputMatchers = ANY_INT;
		
		//I can't initialize the parametersPanel in here because i need to pass the inputmatchers as parameters but the input matchers will be set later so I will initialize the panel in the getParametersPanerl() method
	}

	
	protected void beforeAlignOperations()  throws Exception{
		super.beforeAlignOperations();
		
		int size = inputMatchers.size();
		CombinationParameters cp = (CombinationParameters)param;
		AbstractMatcher a;
		QualityEvaluationData q;
		qualityWeights = new QualityEvaluationData[size];
		
		//if weights are manually assigned then they are already created in the parameters we just have to create a fake quality evaluation with that weight
		//if not, we have to run the quality evaluation for each matcher
		for(int i = 0; i < size; i++) {
			if(cp.qualityEvaluation) {
				a = inputMatchers.get(i);
				q = QualityEvaluator.evaluate(a, cp.quality);
			}
			else {
				//we are in the non-weighted or manual case
				//we have to create fake qualities
				q = new QualityEvaluationData();
				q.setLocal(false);//this quality is global because same weight for all nodes
				q.setGlobalClassMeasure(cp.matchersWeights[i]);
				q.setGlobalPropMeasure(cp.matchersWeights[i]);
			}
			qualityWeights[i] = q;
		}
	}

    

	protected Alignment alignTwoNodes(Node source, Node target, alignType typeOfNodes) {
		
		CombinationParameters parameters = (CombinationParameters)param;
		int sourceindex = source.getIndex();
		int targetindex = target.getIndex();
		double max = 0;// keep the max sim between all input matrix for the cell (sourceindex, targetindex)
		double min = 1;// keep the min sim between all input matrix for the cell (sourceindex, targetindex)
		double sum = 0;// keep the sum of sims between all input matrix for the cell (sourceindex, targetindex)
		double weight;   
		double sumOfWeights = 0; //sum of weights
		double sim;
		double weightedSim;
		//to perform sigmoid average
		double sigmoidSim;
		double weightedSigmoidSim;
		double sigmoidSum = 0;
		AbstractMatcher a;
		for(int i = 0; i < inputMatchers.size();i++) {
			//for each input matcher...
			a = inputMatchers.get(i);
			
			//get the sim for this two nodes in the input matcher matrix
			if(typeOfNodes == alignType.aligningClasses && a.areClassesAligned()) {
				sim = a.getClassesMatrix().get(sourceindex, targetindex).getSimilarity();
				weight = qualityWeights[i].getClassQuality(sourceindex, targetindex);
			}
			else if(typeOfNodes == alignType.aligningProperties && a.arePropertiesAligned()) {
				sim = a.getPropertiesMatrix().get(sourceindex, targetindex).getSimilarity();
				weight = qualityWeights[i].getPropQuality(sourceindex, targetindex);
			}
			else throw new RuntimeException("DEVELOPER ERROR: the alignType of node is not prop or class");
			
			//sigmoid average
			sigmoidSim = Utility.getSigmoidFunction(sim);
			weightedSigmoidSim = weight * sigmoidSim;
			sigmoidSum += weightedSigmoidSim;
			//all other operations are simply weighted, if the user has selected non-weighted then all weights are 1
			weightedSim = weight * sim;
			//calculate sum for average combination
			sum += weightedSim;
			sumOfWeights+= weight;
			//calculate max for max combination
			if(weightedSim > max)
				max = sim;
			//calculate min for min evaluation
			if(weightedSim < min)
				min = sim;
			
		}
		//select the final similarity combined value depending on the user selected combination type.
		if(parameters.combinationType.equals(CombinationParameters.MAXCOMB)) {
			sim = max;
		}
		else if(parameters.combinationType.equals(CombinationParameters.MINCOMB)) {
			sim = min;
		}
		else if(parameters.combinationType.equals(CombinationParameters.AVERAGECOMB)) {
			if(sumOfWeights != 0)
				sim = sum/ sumOfWeights;
			else sim = 0;
		}
		else if(parameters.combinationType.equals(CombinationParameters.SIGMOIDAVERAGECOMB)) {
			if(sumOfWeights != 0)
				sim = sigmoidSum/ sumOfWeights;
			else sim = 0;
		}
		else throw new RuntimeException("DEVELOPMENT ERROR: combination type selected is not implemented");
		return new Alignment(source, target, sim, Alignment.EQUIVALENCE);
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
