package am.extension.userfeedback.rankingStrategies;

import static am.extension.userfeedback.utility.UFLutility.extractList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.ConsensusQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.CrossCountQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.CrossSumQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.PropagationImpactMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;

public class RevalidationRanking extends AbstractRankingStrategy {

	private List<SimilarityMatrix> classMatrices=new ArrayList<SimilarityMatrix>();
	private List<SimilarityMatrix> propMatrices=new ArrayList<SimilarityMatrix>();
	private Collection<Mapping> toRank;
	private SparseMatrix classPos;
	private SparseMatrix classNeg;
	private SparseMatrix propPos;
	private SparseMatrix propNeg;
	private SimilarityMatrix uflClass;
	private SimilarityMatrix uflProp;
	private SimilarityMatrix forbiddenClass;
	private SimilarityMatrix forbiddenProp;
	private final double alpha=0.4;
	private final double beta=0.2;
	private final double gamma=0.4;
	private int maxValidation;

	private MUExperiment experiment;
	
	public RevalidationRanking( MUExperiment experiment ) {
		this.experiment = experiment;
	}

	@Override
	public void rank() {
		classMatrices = extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningClasses);
		propMatrices = extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningProperties);
		classPos = experiment.getFeedbackMatrix(alignType.aligningClasses, Validation.CORRECT);
		classNeg = experiment.getFeedbackMatrix(alignType.aligningClasses, Validation.INCORRECT);
		propPos = experiment.getFeedbackMatrix(alignType.aligningProperties, Validation.CORRECT);
		propNeg = experiment.getFeedbackMatrix(alignType.aligningProperties, Validation.INCORRECT);
		uflClass = experiment.getComputedUFLMatrix(alignType.aligningClasses); 
		uflProp = experiment.getComputedUFLMatrix(alignType.aligningProperties);
		forbiddenClass = experiment.getForbiddenPositions(alignType.aligningClasses);
		forbiddenProp = experiment.getForbiddenPositions(alignType.aligningProperties);
		maxValidation = experiment.setup.parameters.getIntParameter(Parameter.MAX_VALIDATION);
		toRank = new ArrayList<Mapping>();
		if (experiment.correctMappings!=null)
			toRank.addAll(experiment.correctMappings);
		if (experiment.incorrectMappings!=null)
			for (Mapping m : experiment.incorrectMappings)
				if (!toRank.contains(m))
					toRank.add(m);
		
		rankedList = linearCombination(alignType.aligningClasses,classMatrices, classPos, classNeg, uflClass,forbiddenClass);
		rankedList.addAll(linearCombination(alignType.aligningProperties,propMatrices, propPos, propNeg,uflProp,forbiddenProp));
		Collections.sort(rankedList, new MappingSimilarityComparator() );
		Collections.reverse(rankedList);
	}
	
	
	//Linear combination of UD and the inverse of AMD
	private List<Mapping> linearCombination(alignType type, List<SimilarityMatrix>  lMtrx, SparseMatrix mPos, SparseMatrix mNeg, SimilarityMatrix mtrx, SimilarityMatrix forbidden)
	{
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		ConsensusQuality cq=new ConsensusQuality(mPos, mNeg, maxValidation);
		CrossSumQuality csq=new CrossSumQuality(mtrx);
		PropagationImpactMetric pim=new PropagationImpactMetric(mPos, mNeg, maxValidation);
		if (toRank==null) return new ArrayList<Mapping>();
		for (Mapping m : toRank)
		{
			int i=m.getSourceKey();
			int j=m.getTargetKey();
			if(m.getAlignmentType() != type) continue;

			double csq_v=csq.getQuality(null, i, j);
			double cq_v=1-cq.getQuality(null, i, j);
			double pim_v=pim.getQuality(null, i, j);
	
			sim=alpha*cq_v+beta*csq_v+gamma*pim_v;
			m.setSimilarity(sim);
			lst.add(m);
		}
		
		
		return lst;
		
	}
	



}
