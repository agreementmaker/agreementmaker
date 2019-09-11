package am.extension.userfeedback.rankingStrategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.qualityEvaluation.metrics.InverseOf;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.CrossCountQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.SimilarityScoreDefinitness;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.extension.multiUserFeedback.experiment.MUExperiment;

public class IntrinsicQualityRanking extends AbstractRankingStrategy {

	private SimilarityMatrix classMatrix;
	private SimilarityMatrix propMatrix;
	private SimilarityMatrix forbiddenClass;
	private SimilarityMatrix forbiddnProp;
	private double alpha=1.0;
	private double beta=1.0;
	private MUExperiment experiment;
	
	
	public IntrinsicQualityRanking(MUExperiment experiment)
	{
		this.experiment = experiment;
		rank();
	}
	
	@Override
	public void rank() {
		classMatrix = experiment.getComputedUFLMatrix(alignType.aligningClasses);
		propMatrix = experiment.getComputedUFLMatrix(alignType.aligningProperties);
		forbiddenClass = experiment.getForbiddenPositions(alignType.aligningClasses);
		forbiddnProp = experiment.getForbiddenPositions(alignType.aligningProperties);
		
		rankedList=linearCombination(classMatrix, forbiddenClass);
		rankedList.addAll(linearCombination(propMatrix,forbiddnProp));
		Collections.sort(rankedList, new MappingSimilarityComparator() );
		Collections.reverse(rankedList);
	}
	
	
	//Linear combination of CCQ and the inverse of SSH
	private List<Mapping> linearCombination(SimilarityMatrix mtrx, SimilarityMatrix forbidden)
	{
		Mapping mp=null;
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		CrossCountQuality ccq=new CrossCountQuality(mtrx);
		SimilarityScoreDefinitness ssh=new SimilarityScoreDefinitness(mtrx);
		InverseOf invSSD=new InverseOf(ssh);
		for(int i=0;i<mtrx.getRows();i++)
		{
			for(int j=0;j<mtrx.getColumns();j++)
			{
				if (forbidden.getSimilarity(i, j)==1.0)
					continue;
				mp=mtrx.get(i, j);
				sim=alpha*ccq.getQuality(null, i, j)+beta*(invSSD.getQuality(null, i, j));
				mp.setSimilarity(sim);
				lst.add(mp);

			}
		}
		return lst;
		
	}


}
