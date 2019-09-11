package am.extension.userfeedback.rankingStrategies;

import static am.extension.userfeedback.utility.UFLutility.extractList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.VarianceMatcherDisagreement;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;

public class DisagreementRanking extends AbstractRankingStrategy {

	private List<SimilarityMatrix> classMatrices=new ArrayList<SimilarityMatrix>();
	private List<SimilarityMatrix> propMatrices=new ArrayList<SimilarityMatrix>();
	private SparseMatrix classPos;
	private SparseMatrix classNeg;
	private SparseMatrix propPos;
	private SparseMatrix propNeg;
	private SimilarityMatrix refMatrixC;
	private SimilarityMatrix refMatrixP;
	
	private MUExperiment experiment;
	
	public DisagreementRanking(MUExperiment experiment)
	{
		super();
		this.experiment = experiment;
		rank();
	}
	
	@Override
	public void rank() {
		classMatrices = extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningClasses);
		propMatrices = extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningProperties); 
		classPos = experiment.getFeedbackMatrix(alignType.aligningClasses, Validation.CORRECT);
		classNeg = experiment.getFeedbackMatrix(alignType.aligningClasses, Validation.INCORRECT);
		propPos = experiment.getFeedbackMatrix(alignType.aligningProperties, Validation.CORRECT);
		propNeg = experiment.getFeedbackMatrix(alignType.aligningProperties, Validation.INCORRECT);
		refMatrixC = experiment.getComputedUFLMatrix(alignType.aligningClasses); 
		refMatrixP = experiment.getComputedUFLMatrix(alignType.aligningProperties);
		
		rankedList = linearCombination(classMatrices, classPos, classNeg, refMatrixC);
		rankedList.addAll(linearCombination(propMatrices, propPos, propNeg,refMatrixP));
		Collections.sort(rankedList, new MappingSimilarityComparator() );
		Collections.reverse(rankedList);
	}
	
	
	//Linear combination of UD and the inverse of AMD
	private List<Mapping> linearCombination(List<SimilarityMatrix>  lMtrx, SparseMatrix mPos, SparseMatrix mNeg, SimilarityMatrix refMatrix)
	{
		Mapping mp=null;
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		VarianceMatcherDisagreement vmd=new VarianceMatcherDisagreement(lMtrx);
		for (int i=0;i<mPos.getRows();i++)
		{
			for (int j=0;j<mPos.getColumns();j++)
			{
				mp=refMatrix.get(i, j);
				
				sim=vmd.getQuality(null, i, j);
				mp.setSimilarity(sim);
				lst.add(mp);
			}
		}
		return lst;
		
	}

}
