package am.matcher.Combination;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluationData;
import am.app.mappingEngine.qualityEvaluation.QualityEvaluator;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;

import java.util.HashMap;
import java.util.Map;

public class CombinationMatcher extends AbstractMatcher {
	private Map<AbstractMatcher, QualityEvaluationData> qeMap = new HashMap<>();

	public CombinationMatcher() {
		super();
		needsParam = true; // need the parameters
	}

	public CombinationMatcher(CombinationParameters param_new) {
		super(param_new);
	}

	@Override
	protected void initializeVariables() {
		super.initializeVariables();
		minInputMatchers = 2;
		maxInputMatchers = ANY_INT;
		setName("Linear Weighted Combination");
		setCategory(MatcherCategory.COMBINATION);
	}

	@Override
	protected void beforeAlignOperations() throws Exception {
		super.beforeAlignOperations();
		CombinationParameters cp = (CombinationParameters) param;

		// if weights are manually assigned then they are already created in the
		// parameters we just have to create a fake quality evaluation with that
		// weight
		// if not, we have to run the quality evaluation for each matcher
		for (int i = 0; i < inputMatchers.size(); i++) {
		    final AbstractMatcher a = inputMatchers.get(i);
			QualityEvaluationData manual = null;
			QualityEvaluationData automatic = null;
			QualityEvaluationData finalQuality = null;
			// is important to be if if if without else
			if (cp.qualityEvaluation) {
				automatic = QualityEvaluator.evaluate(a,
						inputMatchers.toArray(new AbstractMatcher[0]),
						QualityEvaluator.getQM(cp.quality));
				finalQuality = automatic; // if it's only manual q will be equal
											// to it else it will be the average
											// of manual and automatics
			}
			if (cp.manualWeighted) {
				// we are in the non-weighted or manual case
				// we have to create fake qualities
				manual = new QualityEvaluationData();
				manual.setLocal(false);// this quality is global because same
										// weight for all nodes
				manual.setGlobalClassMeasure(cp.matchersWeights[i]);
				manual.setGlobalPropMeasure(cp.matchersWeights[i]);
				finalQuality = manual;
			}

			if (cp.manualWeighted && cp.qualityEvaluation) {
				finalQuality = QualityEvaluator.mergeQualities(manual,
						automatic);
			}

			qeMap.put(a,finalQuality);
		}
	}

	@Override
	protected Mapping alignTwoNodes(Node source, Node target,
			alignType typeOfNodes, SimilarityMatrix matrix) {
	
		CombinationParameters parameters = (CombinationParameters) param;
		int sourceindex = source.getIndex();
		int targetindex = target.getIndex();
		double max = 0;// keep the max sim between all input matrix for the cell
						// (sourceindex, targetindex)
		double min = 1;// keep the min sim between all input matrix for the cell
						// (sourceindex, targetindex)
		double sum = 0;// keep the sum of sims between all input matrix for the
						// cell (sourceindex, targetindex)
		double weight;
		double sumOfWeights = 0; // sum of weights
		double sim;
		double weightedSim;
		// to perform sigmoid average
		double sigmoidSim;
		double weightedSigmoidSim;
		double sigmoidSum = 0;
		QualityEvaluationData q;
		for (AbstractMatcher a : inputMatchers) {
			q = qeMap.get(a);
			// get the sim for this two nodes in the input matcher matrix
			if (typeOfNodes != alignType.aligningClasses
					&& typeOfNodes != alignType.aligningProperties)
				throw new RuntimeException(
						"DEVELOPER ERROR: the alignType of node is not prop or class. ("
								+ a + ")");

			if (typeOfNodes == alignType.aligningClasses
					&& a.areClassesAligned()) {
				if (a.getClassesMatrix().get(sourceindex, targetindex) != null) {
	
					sim = a.getClassesMatrix().get(sourceindex, targetindex).getSimilarity();
			
				} else {
					sim = 0.0;
				}
				weight = q.getClassQuality(sourceindex, targetindex);
			} else if (typeOfNodes == alignType.aligningProperties
					&& a.arePropertiesAligned()) {
				if (a.getPropertiesMatrix().get(sourceindex, targetindex) != null) {
					sim = a.getPropertiesMatrix().get(sourceindex, targetindex).getSimilarity();
					
				} else {
					sim = 0.0;
				}
				weight = q.getPropQuality(sourceindex, targetindex);
			} else
				throw new RuntimeException(
						"DEVELOPER ERROR: The input matchers must perform the mapping selection step. ("
								+ a + ")");

			// sigmoid average
			sigmoidSim = Utility.getSigmoidFunction(sim);
			weightedSigmoidSim = weight * sigmoidSim;
			sigmoidSum += weightedSigmoidSim;
			// all other operations are simply weighted, if the user has
			// selected non-weighted then all weights are 1
			weightedSim = weight * sim;
			// calculate sum for average combination
			sum += weightedSim;
			sumOfWeights += weight;
			// calculate max for max combination
			if (weightedSim > max)
				max = sim;
			// calculate min for min evaluation
			if (weightedSim < min)
				min = sim;

		}
		// select the final similarity combined value depending on the user
		// selected combination type.
		if (parameters.combinationType.equals(CombinationParameters.MAXCOMB)) {
			sim = max;
		} else if (parameters.combinationType
				.equals(CombinationParameters.MINCOMB)) {
			sim = min;
		} else if (parameters.combinationType
				.equals(CombinationParameters.AVERAGECOMB)) {
			if (sumOfWeights != 0)
				sim = sum / sumOfWeights;
			else
				sim = 0;
		} else if (parameters.combinationType
				.equals(CombinationParameters.SIGMOIDAVERAGECOMB)) {
			if (sumOfWeights != 0)
				sim = sigmoidSum / sumOfWeights;
			else
				sim = 0;
		} else
			throw new RuntimeException(
					"DEVELOPMENT ERROR: combination type selected is not implemented");
		return new Mapping(source, target, sim, MappingRelation.EQUIVALENCE);
	}

	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if (parametersPanel == null) {
			parametersPanel = new CombinationParametersPanel(inputMatchers);
		}
		return parametersPanel;
	}

	@Override
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
