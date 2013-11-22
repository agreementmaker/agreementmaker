package am.extension.userfeedback.clustering.disagreement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Ontology;
import am.evaluation.disagreement.variance.VarianceDisagreement;
import am.evaluation.disagreement.variance.VarianceDisagreementParameters;
import am.extension.userfeedback.CandidateSelection;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.MLFeedback.MLFExperiment;

public class MaxInformationRanking extends CandidateSelection<MLFExperiment> {
	
	private List<Mapping> rankedClassMappings;
	private List<Mapping> rankedPropertyMappings;
	private List<Mapping> allRanked;
	//private List<RankedList> propertyRanked;
	//private boolean useProperty=false;
	private UFLExperiment experiment;
	//weight for the Uncertain Mappings discovered in the system
	private final double weight_um=1.0;
	//weight for the suspected Missing Mapping in the system
	private final double weight_mm=0.3;
	
	@Override public List<Mapping> getRankedMappings(alignType t) { 
		if( t == alignType.aligningClasses ) { return rankedClassMappings; }
		if( t == alignType.aligningProperties ) { return rankedPropertyMappings; }

		return null;
	}
	
	@Override
	public Mapping getCandidateMapping() 
	{

		for( int i = 0; i < allRanked.size(); i++ ){
			if( experiment.correctMappings == null && experiment.incorrectMappings == null ) 
				return allRanked.get(i);
			Mapping m = allRanked.get(i);
			if( experiment.correctMappings != null && (experiment.correctMappings.contains(m.getEntity1(),Ontology.SOURCE) != null ||
				experiment.correctMappings.contains(m.getEntity2(),Ontology.TARGET) != null) ) {
				// assume 1-1 mapping, skip already validated mappings.
				continue;
			}
			if( experiment.incorrectMappings != null && experiment.incorrectMappings.contains(m) ) 
				continue; // we've validated this mapping already.
				
			return m;
		}
		
		return null;
	}

	@Override
	public List<Mapping> getRankedMappings() {
		return allRanked;
	}
	
	@Override
	public void rank(MLFExperiment ex) {
		this.experiment = ex;
		if (ex.getIterationNumber()==0)
			initializeRankedMatrix(ex);
		// get the matchers from the execution semantics
		Alignment<Mapping> mappings= ex.getFinalAlignment();
		List<AbstractMatcher> matchers = ex.initialMatcher.getComponentMatchers();
		


		rank(matchers, mappings, ex);
		
		done();
	}
	
	private void initializeRankedMatrix(MLFExperiment ex)
	{
		SimilarityMatrix smClass=ex.initialMatcher.getFinalMatcher().getClassesMatrix().clone();
		SimilarityMatrix smProperty=ex.initialMatcher.getFinalMatcher().getPropertiesMatrix().clone();
		for(int i=0;i<smClass.getRows();i++)
			for(int j=0;j<smClass.getColumns();j++)
				smClass.setSimilarity(i, j, 0.5);
		for(int i=0;i<smProperty.getRows();i++)
			for(int j=0;j<smProperty.getColumns();j++)
				smProperty.setSimilarity(i, j, 0.5);
		ex.setUflClassMatrix(smClass);
		ex.setUflPropertyMatrix(smProperty);
	}
	
	
	/*
	private void initializeLists(List<AbstractMatcher> matchers)
	{
		propertyRanked=new ArrayList<RankedList>(matchers.size()+1);
		for(int i=0; i<matchers.size()+1;i++)
		{
			propertyRanked.add(new RankedList());// .get(i).mList=new ArrayList<Mapping>();
		}
	}
	*/
	/*
	public void propertyRank(List<Mapping> rankedList, List<AbstractMatcher> matchers)
	{
		int matchersSize=matchers.size();
		AbstractMatcher am=null;
		int sourceKey=0;
		int targetKey=0;
		int count=0;
		int index=-1;
		for(int i=0;i<rankedList.size();i++)
		{
			count=0;
			index=-1;
			for(int j=0;j<matchersSize;j++)
			{
				am=matchers.get(j);
				sourceKey=rankedList.get(i).getSourceKey();
				targetKey=rankedList.get(i).getTargetKey();
				if(rankedList.get(i).getAlignmentType()==alignType.aligningClasses)
				{
					if(am.getClassesMatrix().getSimilarity(sourceKey, targetKey)>0.0)
					{
						count++;
						index=j;
					}
				}
				else
				{
					if(am.getPropertiesMatrix().getSimilarity(sourceKey, targetKey)>0.0)
					{
						count++;
						index=j;
					}
				}
			}
			if (count==1)
			{
				propertyRanked.get(index).mList.add(rankedList.get(i));
			}
			else
			{
				propertyRanked.get(matchersSize).mList.add(rankedList.get(i));
			}
		}
	}
	*/
	private SimilarityMatrix addPropagationWeight(SimilarityMatrix sm, SimilarityMatrix ufl)
	{
		Alignment<Mapping> missedAlignment=retriveUnconfidentMapping(ufl);
		int row=sm.getRows();
		int col=sm.getColumns();
		double sim=0;
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				if (missedAlignment.contains(sm.get(i, j)))
				{
					sim=sm.getSimilarity(i, j)+missedAlignment.getSimilarity(sm.get(i, j).getEntity1(), sm.get(i, j).getEntity2());
					sm.setSimilarity(i, j, sim);
				}
			}
		}
		return sm;
	}
	
	
	private SimilarityMatrix addPropagationWeight(SimilarityMatrix sm, MLFExperiment experiment, alignType type)
	{
		//double rand=0;
		double zero=0;
		double uno=0;
		double altri=0;
		int count_zero=0;
		int count_uno=0;
		int count_altri=0;
		TreeSet<Integer> forbidden_row=new TreeSet<Integer>();
		TreeSet<Integer> forbidden_col=new TreeSet<Integer>();
		
		SimilarityMatrix rankedMatrix=null;
		if (type==alignType.aligningClasses)
		{
			rankedMatrix=experiment.getUflClassMatrix();
			//forbidden_col=experiment.forbidden_column_classes;
			//forbidden_row=experiment.forbidden_row_classes;
		}
		else
		{
			rankedMatrix=experiment.getUflPropertyMatrix();
			//forbidden_col=experiment.forbidden_column_properties;
			//forbidden_row=experiment.forbidden_row_properties;
		}
		//Alignment<Mapping> missedAlignment=missedMappingRetriver(rankedMatrix, forbidden_row, forbidden_col);
		
		//Alignment<Mapping> missedAlignmentProperties=retriveUnconfidentMapping(experiment.getRankedPropertyMatrix());
		
		for (int i=0;i<sm.getRows();i++)
		{
			if (forbidden_row.contains(i))
				continue;
			for(int j=0;j<sm.getColumns();j++)
			{
				if (forbidden_col.contains(j))
					continue;
				//rand=1;//Math.random()*100;
				double x=rankedMatrix.getSimilarity(i, j);
				if (rankedMatrix.getSimilarity(i, j)==0.0)
				{
					zero+=sm.getSimilarity(i, j);
					count_zero++;
					sm.setSimilarity(i, j, sm.getSimilarity(i, j)*0.8);
				}
				else if (rankedMatrix.getSimilarity(i, j)==1.0)
				{
					sm.setSimilarity(i, j, sm.getSimilarity(i, j)*0.3);
					uno+=sm.getSimilarity(i, j);
					count_uno++;
				}
				else
				if ((rankedMatrix.getSimilarity(i, j)!=1.0) && (rankedMatrix.getSimilarity(i, j)!=0.0))
				{
					sm.setSimilarity(i, j, sm.getSimilarity(i, j)*1.5);
					count_altri++;
					altri+=sm.getSimilarity(i, j);
				}
			}
		}
		double fin_zero=zero/count_zero;
		double fin_uno=uno/count_uno;
		double fin=altri=altri/count_altri;
		return sm;
	}
	
	
	private Alignment<Mapping> retriveUnconfidentMapping(SimilarityMatrix sm)
	{
		Alignment<Mapping> mpng=new Alignment<Mapping>(0,0);
		Mapping m=null;
		int row=sm.getRows();
		int col=sm.getColumns();
		double weight=0;
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				weight=0.5;
				if (sm.getSimilarity(i, j)==0.0)
				{
					continue;
//					if (sm.getRowMaxValues(i, 1)[0].getSimilarity()==0.0)
//					{
//						if (sm.getColMaxValues(j, 1)[0].getSimilarity()==0.0)
//						{
//							weight=weight_mm;
//						}
//						else
//						{
//							continue;
//						}
//					}
//					else
//					{
//						continue;
//					}
				}
				else
				{
					if (sm.getSimilarity(i, j)==1.0)
						continue;
					weight+=weight_um*(numOfMapping(sm.getColMaxValues(j, row))-1);
					weight+=weight_um*(numOfMapping(sm.getRowMaxValues(i, col))-1);
					m=sm.get(i, j);
					m.setSimilarity(weight);
					mpng.add(m);
				}
			}
		}
		
		return mpng;
	}
	
	private int numOfMapping(Mapping[] map)
	{
		int count=0;
		for(Mapping m :map)
		{
			if(m.getSimilarity()!=0.0)
				count++;
		}
		return count;
	}
	
	private Alignment<Mapping> missedMappingRetriver(SimilarityMatrix sm, TreeSet<Integer> forbidden_row, TreeSet<Integer> forbidden_col)
	{
		Alignment<Mapping> missed=new Alignment<Mapping>(0, 0);
		int row=sm.getRows();
		int col=sm.getColumns();
		double row_sim=0;
		double col_sim=0;
		for(int i=0;i<row;i++)
		{
			row_sim=sm.getRowMaxValues(i, 1)[0].getSimilarity();
			if (row_sim!=0)
				continue;
			if (forbidden_row.contains(i)) 
				continue;
			for(int j=0;j<col;j++)
			{
				if(forbidden_col.contains(j))
					continue;
				
				col_sim=sm.getColMaxValues(j, 1)[0].getSimilarity();
				if (col_sim!=0.0)
					continue;
				
				missed.add(sm.get(i, j));
			}
		}
		return missed;
	}
	
	public void rank(List<AbstractMatcher> matchers, Alignment<Mapping> mappings, MLFExperiment experiment)
	{
		

		// setup the variance disagreement calculation
		VarianceDisagreementParameters disagreementParams = new VarianceDisagreementParameters();
		disagreementParams.setMatchers(matchers);
		
		VarianceDisagreement disagreementMetric = new VarianceDisagreement();
		disagreementMetric.setParameters(disagreementParams, mappings);
		
		// run the disagreement calculations
		SimilarityMatrix classDisagreement = disagreementMetric.getDisagreementMatrix(alignType.aligningClasses);
		
		//add the weight coming from the ranking matrix produced in the UFL propagation phase
		//classDisagreement=addPropagationWeight(classDisagreement, experiment, alignType.aligningClasses);
		classDisagreement=addPropagationWeight(classDisagreement, experiment.getUflClassMatrix());
		try {
			rankedClassMappings = classDisagreement.toList();
			Collections.sort(rankedClassMappings, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		classDisagreement = null;  // release the memory used by this
		
		SimilarityMatrix propertyDisagreement = disagreementMetric.getDisagreementMatrix(alignType.aligningProperties);
		
		//add the weight coming from the ranking matrix produced in the UFL propagation phase
		//propertyDisagreement=addPropagationWeight(propertyDisagreement, experiment, alignType.aligningProperties);
		propertyDisagreement=addPropagationWeight(propertyDisagreement, experiment.getUflPropertyMatrix());
		try {
			rankedPropertyMappings = propertyDisagreement.toList();
			Collections.sort(rankedPropertyMappings, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		propertyDisagreement = null;
		
		
		allRanked = new ArrayList<Mapping>();
		
		allRanked.addAll(rankedClassMappings);
		allRanked.addAll(rankedPropertyMappings);
		Collections.sort(allRanked, new MappingSimilarityComparator() );
		Collections.reverse(allRanked);
//		if(useProperty)
//			propertyRank(allRanked, matchers);
		
	}

}
