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
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.CrossSumQuality;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.SimilarityScoreDefinitness;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.VarianceMatcherDisagreement;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi.ContentionPoint;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi.MultiMatcherConfidence;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.shi.SimilarityDistance;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;

public class MultiSelectedRanking extends AbstractRankingStrategy{
	
	MUExperiment experiment;
	String[] strategies;
	AbstractQualityMetric[] aqmV_classes;
	AbstractQualityMetric[] aqmV_properties;
	String combinationType;
	
	
	public MultiSelectedRanking(MUExperiment exp, String[] strategies, String combinationType, double[] weights )
	{
		this.experiment =exp;
		this.strategies=strategies;
		this.combinationType=combinationType;
		aqmV_classes=new AbstractQualityMetric[strategies.length];
		aqmV_properties=new AbstractQualityMetric[strategies.length];
		for(int i=0;i<strategies.length;i++)
		{
			if (strategies[i].equals("dis"))
			{
				aqmV_classes[i]=new VarianceMatcherDisagreement(extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningClasses));
				aqmV_properties[i]=new VarianceMatcherDisagreement(extractList(experiment.initialMatcher.getComponentMatchers(), alignType.aligningProperties));
				aqmV_classes[i].setWeight(0.5);
				aqmV_properties[i].setWeight(0.5);
			}
			if (strategies[i].equals("ccq"))
			{
				aqmV_classes[i]=new CrossCountQuality(experiment.getComputedUFLMatrix(alignType.aligningClasses));
				aqmV_properties[i]=new CrossCountQuality(experiment.getComputedUFLMatrix(alignType.aligningProperties));
				aqmV_classes[i].setWeight(0.15);
				aqmV_properties[i].setWeight(0.15);
			}
			if (strategies[i].equals("csq"))
			{
				aqmV_classes[i]=new CrossSumQuality(experiment.getComputedUFLMatrix(alignType.aligningClasses));
				aqmV_properties[i]=new CrossSumQuality(experiment.getComputedUFLMatrix(alignType.aligningProperties));
				aqmV_classes[i].setWeight(0.15);
				aqmV_properties[i].setWeight(0.15);
			}
			if (strategies[i].equals("ssh"))
			{
				aqmV_classes[i]=new SimilarityScoreDefinitness(experiment.getComputedUFLMatrix(alignType.aligningClasses));
				aqmV_properties[i]=new SimilarityScoreDefinitness(experiment.getComputedUFLMatrix(alignType.aligningProperties));
				aqmV_classes[i].setWeight(0.35);
				aqmV_properties[i].setWeight(0.35);
			}
			if (strategies[i].equals("con"))
			{
				aqmV_classes[i]=new ContentionPoint(exp.initialMatcher.getFinalMatcher().getClassesMatrix(),
						experiment.initialMatcher.getComponentMatchers(), alignType.aligningClasses, 
						exp.setup.parameters.getDoubleParameter(Parameter.IM_THRESHOLD));
				aqmV_properties[i]=new ContentionPoint(exp.initialMatcher.getFinalMatcher().getPropertiesMatrix(),
						experiment.initialMatcher.getComponentMatchers(),alignType.aligningProperties,
						exp.setup.parameters.getDoubleParameter(Parameter.IM_THRESHOLD));
			}
			if (strategies[i].equals("mmc"))
			{
				aqmV_classes[i]=new MultiMatcherConfidence(experiment.initialMatcher.getComponentMatchers(),  
						exp.setup.parameters.getDoubleParameter(Parameter.IM_THRESHOLD));
				aqmV_properties[i]=new MultiMatcherConfidence(experiment.initialMatcher.getComponentMatchers(),  
						exp.setup.parameters.getDoubleParameter(Parameter.IM_THRESHOLD));
			}
			if (strategies[i].equals("sim"))
			{
				aqmV_classes[i]=new SimilarityDistance(exp.initialMatcher.getFinalMatcher().getClassesMatrix());
				aqmV_properties[i]=new SimilarityDistance(exp.initialMatcher.getFinalMatcher().getPropertiesMatrix());
			}
			
			
			aqmV_classes[i].setMetricID(strategies[i]);
			aqmV_properties[i].setMetricID(strategies[i]);
		}

	}
	
	
	@Override
	public void rank() {
		// TODO Auto-generated method stub
		rankedList=combination(aqmV_classes, experiment.getForbiddenPositions(alignType.aligningClasses), experiment.initialMatcher.getFinalMatcher().getClassesMatrix());
		rankedList.addAll(combination(aqmV_properties,experiment.getForbiddenPositions(alignType.aligningProperties),experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix()));
		Collections.sort(rankedList, new MappingSimilarityComparator() );
		Collections.reverse(rankedList);
	}
	
	
	private List<Mapping> combination(AbstractQualityMetric[] aqmV, SimilarityMatrix forbidden, SimilarityMatrix mtrx)
	{
		double sim=0;
		List<Mapping> lst=new ArrayList<Mapping>();
		double[] w=new double[3];
		int w_count=0;
		Mapping mp;
		for(int i=0;i<mtrx.getRows();i++)
		{
			for(int j=0;j<mtrx.getColumns();j++)
			{
				List<Double> values=new ArrayList<Double>();
//				if (forbidden.getSimilarity(i, j)==1.0)
//					continue;
				mp=mtrx.get(i, j);
				w_count=0;
				for(int k=0;k<strategies.length;k++)
				{
					if ((aqmV[k].getMetricID().equals("ssh")) 
							|| (aqmV[k].getMetricID().equals("mmc"))
							|| (aqmV[k].getMetricID().equals("sim"))
							|| (aqmV[k].getMetricID().equals("con")))
						values.add(1-aqmV[k].getQuality(mp.getAlignmentType(), i, j));
					else
						values.add(aqmV[k].getQuality(mp.getAlignmentType(), i, j));
					if (combinationType.equals("lwc"))
						w[w_count++]=aqmV[k].getWeight();
				}
				
				
				switch(combinationType)
				{
					case("max"):
						sim=maxCombination(values);
						break;
					case("avg"):
						sim=avgCombination(values);
						break;
					case("min"):
						sim=minCombination(values);
						break;
					case("lwc"):
						sim=linearWeightedCombination(values, w);
						break;
				}
				
				
				mp.setSimilarity(sim);
				lst.add(mp);

			}
		}
		return lst;
	}
	
	
	private double  maxCombination(List<Double> ds)
	{
		double max=0;
		for (double d :ds)
		{
			if (d>max)
				max=d;
		}
		return max;
	}
	
	private double avgCombination(List<Double> ds)
	{
		double avg=0;
		for (double d :ds)
		{
			avg+=d;
		}
		return avg/ds.size();
	}
	
	private double minCombination(List<Double> ds)
	{
		double min=1.0;
		for (double d :ds)
		{
			if (d>min)
				min=d;
		}
		return min;
	}
	
	private double linearWeightedCombination(List<Double> ds, double[] weights)
	{
		double sim=0.0;
		for (int i=0;i<ds.size();i++)
		{
			sim+=ds.get(i)*weights[i];
		}
		return sim;
	}
	
}
