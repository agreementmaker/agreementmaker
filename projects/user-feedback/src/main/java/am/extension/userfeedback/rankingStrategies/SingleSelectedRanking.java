package am.extension.userfeedback.rankingStrategies;

import static am.extension.userfeedback.utility.UFLutility.extractList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.AbstractQualityMetric;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.CrossCountQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.SimilarityScoreDefinitness;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.VarianceMatcherDisagreement;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.extension.multiUserFeedback.experiment.MUExperiment;

public class SingleSelectedRanking extends AbstractRankingStrategy{
	String rankingStrategy;
	MUExperiment experiment;
	boolean inverse;
	AbstractQualityMetric aqm_classes;
	AbstractQualityMetric aqm_properties;
	
	
	public SingleSelectedRanking(MUExperiment exp, String rankingStrategy, boolean inverse)
	{
		this.experiment=exp;
		this.rankingStrategy=rankingStrategy;
		this.inverse =inverse;
	}
	
	@Override
	public void rank() {
		// TODO Auto-generated method stub
		switch(rankingStrategy)
		{
		case ("dis"):
			aqm_classes=new VarianceMatcherDisagreement(extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningClasses));
			aqm_properties=new VarianceMatcherDisagreement(extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningProperties));
			break;
		case ("ccq"):
			aqm_classes=new CrossCountQuality(experiment.getComputedUFLMatrix(alignType.aligningClasses));
			aqm_properties=new CrossCountQuality(experiment.getComputedUFLMatrix(alignType.aligningProperties));
			break;
		case ("ssd"):
			aqm_classes=new SimilarityScoreDefinitness(experiment.getComputedUFLMatrix(alignType.aligningClasses));
			aqm_properties=new SimilarityScoreDefinitness(experiment.getComputedUFLMatrix(alignType.aligningProperties));
			break;
		}
		
		rankedList=linearCombination(aqm_classes, experiment.getForbiddenPositions(alignType.aligningClasses), experiment.initialMatcher.getFinalMatcher().getClassesMatrix());
		rankedList.addAll(linearCombination(aqm_properties,experiment.getForbiddenPositions(alignType.aligningProperties),experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix()));
		Collections.sort(rankedList, new MappingSimilarityComparator() );
		Collections.reverse(rankedList);
	
	}
	
	private List<Mapping> linearCombination(AbstractQualityMetric aqm, SimilarityMatrix forbidden, SimilarityMatrix mtrx)
	{
		Mapping mp=null;
		
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		for(int i=0;i<mtrx.getRows();i++)
		{
			for(int j=0;j<mtrx.getColumns();j++)
			{
				if (forbidden.getSimilarity(i, j)==1.0)
					continue;
				mp=mtrx.get(i, j);
				if (!inverse)
					sim=aqm.getQuality(null, i, j);
				else
					sim=1-aqm.getQuality(null, i, j);
				mp.setSimilarity(sim);
				lst.add(mp);

			}
		}
		return lst;
		
	}

}
