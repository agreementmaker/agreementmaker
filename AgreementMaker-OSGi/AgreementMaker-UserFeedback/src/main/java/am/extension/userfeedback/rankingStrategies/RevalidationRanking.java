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
	private double alpha=1.0;
	private double beta=1.0;
	private double gamma=2.0;
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
			toRank.addAll(experiment.incorrectMappings);
		
		rankedList = linearCombination(alignType.aligningClasses,classMatrices, classPos, classNeg, uflClass,forbiddenClass);
		rankedList.addAll(linearCombination(alignType.aligningProperties,propMatrices, propPos, propNeg,uflProp,forbiddenProp));
		Collections.sort(rankedList, new MappingSimilarityComparator() );
		Collections.reverse(rankedList);
	}
	
	
	//Linear combination of UD and the inverse of AMD
	private List<Mapping> linearCombination(alignType type, List<SimilarityMatrix>  lMtrx, SparseMatrix mPos, SparseMatrix mNeg, SimilarityMatrix mtrx, SimilarityMatrix forbidden)
	{
		//List<Double> ud_norm=new ArrayList<Double>();
		List<Double> ccq_norm=new ArrayList<Double>();
		List<Double> cq_norm=new ArrayList<Double>();
		List<Double> pim_norm=new ArrayList<Double>();
		//List<Double> rr_norm=new ArrayList<Double>();
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		//UserDisagrement ud=new UserDisagrement(mPos, mNeg);
		ConsensusQuality cq=new ConsensusQuality(mPos, mNeg, maxValidation);
		CrossCountQuality ccq=new CrossCountQuality(mtrx);
		PropagationImpactMetric pim=new PropagationImpactMetric(mPos, mNeg, maxValidation);
		//MappingQualityMetric rr=new InverseOf(new RevalidationRate(mPos, mNeg));
		if (toRank==null) return new ArrayList<Mapping>();
		for (Mapping m : toRank)
		{
			int i=m.getSourceKey();
			int j=m.getTargetKey();
			if(m.getAlignmentType() != type) continue;
			if (mPos.getSimilarity(i, j)+mNeg.getSimilarity(i, j)>=maxValidation)
			{
				ccq_norm.add(0d);
				cq_norm.add(0d);
				pim_norm.add(0d);
			}
			else
			{
				//ud_norm.add(ud.getQuality(null, i, j));
				ccq_norm.add(ccq.getQuality(null, i, j));
				cq_norm.add(cq.getQuality(null, i, j));
				pim_norm.add(pim.getQuality(null, i, j));
				//rr_norm.add(rr.getQuality(null, i, j));
			}
		}
		//normalize(ud_norm);
		//normalize(ccq_norm);
		//normalize(rr_norm);
		int count=0;
		for (Mapping m : toRank)
		{
			if(m.getAlignmentType() != type) continue;
			sim=alpha*(1-cq_norm.get(count))+beta*ccq_norm.get(count)+gamma*pim_norm.get(count);
			m.setSimilarity(sim);
			lst.add(m);
			count++;
		}
		
		
		return lst;
		
	}
	
	private void normalize(List<Double> lst)
	{
		double max=Double.MIN_VALUE;
		double min=Double.MAX_VALUE;
		
		for(Double d :lst)
		{
			if (d<min)
				min=d;
			if (d>max) max=d;	
		}
		for(int i=0;i<lst.size();i++)
		{
			double tmp=lst.get(i);
			tmp=(tmp-min)/(max-min);
			lst.set(i, tmp);
		}
		
	}


}
