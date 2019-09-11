package am.extension.multiUserFeedback.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.evaluation.disagreement.variance.VarianceDisagreement;
import am.evaluation.disagreement.variance.VarianceDisagreementParameters;
import am.extension.multiUserFeedback.experiment.BMexperiment;
import am.extension.userfeedback.selection.CandidateSelection;

public class BMCandidateSelection extends CandidateSelection<BMexperiment>{
	private String id;
	private SimilarityMatrix classesMatrix;
	private SimilarityMatrix propertiesMatrix;
	private final double weight_um=0.5;
	private final double weight_mm=0.3;
	private BMexperiment experiment;
	List<Mapping> allRanked=new ArrayList<Mapping>();
	
	@Override
	public void rank(BMexperiment exp) {
		this.experiment=exp;

		//Inizialization
		classesMatrix = experiment.getUflClassMatrix();
		propertiesMatrix = experiment.getUflPropertyMatrix();
		
		//one time ranking methods
		if(exp.getIterationNumber() == 1)
		{
			uncertainMappingRetrieval();
		}
		//every time ranking methods
		almostCertainMappingRetrieval();
		disagreementRanking();
				
		String id = Integer.toString(experiment.currentUser);
		Mapping m;
		switch (experiment.usersGroup.get(id)) 
		{
    	case 0:  
    		m = getCandidateMapping(id, allRanked);
    		if( m != null ) {
    			selectedMapping = m;
    			break;
    		};
        case 1:  
        	m = getCandidateMapping(id, experiment.uncertainRanking);
            if( m != null ) {
            	selectedMapping = m;
            	break;
            }
        case 2:  
            m = getCandidateMapping(id, experiment.almostRanking);
            if( m != null ) {
            	selectedMapping = m;
            	break;
            }
		}
		
		//end
		done();
	}

	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> getRankedMappings() {
		return experiment.alreadyEvaluated;
	}
	
	private Mapping getCandidateMapping(String id, List<Mapping> lst) 
	{
		Mapping m=null;
		for( int i = 0; i < allRanked.size(); i++ ){
			if(((experiment.usersMappings.get(id).isEmpty()) || 
					(!experiment.usersMappings.get(id).contains(lst.get(i)))) && 
					(!experiment.alreadyEvaluated.contains(lst.get(i))))
			{
				m= lst.get(i);
				experiment.usersMappings.get(id).add(m);
				if(!experiment.alreadyEvaluated.contains(m))
					experiment.alreadyEvaluated.add(m);
				return m;
			}			
		}
		
		return null;
	}
	
	private Mapping notEvaluated(List<Mapping> lst)
	{
		if (experiment.alreadyEvaluated.isEmpty())
			return lst.get(0);
		for(Mapping m : lst)
		{
			if (!experiment.alreadyEvaluated.contains(m))
				return m;
		}
		return null;
	}
	
	@Override
	public Mapping getSelectedMapping() {
		return selectedMapping;
	}
	
	//one time strategy
	private void uncertainMappingRetrieval()
	{
		List<Mapping> unClasses=conflitualMapping(classesMatrix);
		List<Mapping> unProperties=conflitualMapping(propertiesMatrix);
		SimilarityMatrix rankedClasses=addConflitualWeight(classesMatrix,unClasses);
		SimilarityMatrix rankedProperties=addConflitualWeight(propertiesMatrix, unProperties);
		try {
			unClasses = rankedClasses.toList();
			Collections.sort(unClasses, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			unProperties = rankedProperties.toList();
			Collections.sort(unProperties, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		experiment.uncertainRanking = new ArrayList<Mapping>();
		
		experiment.uncertainRanking.addAll(unClasses);
		experiment.uncertainRanking.addAll(unProperties);
		Collections.sort(experiment.uncertainRanking, new MappingSimilarityComparator() );
		Collections.reverse(experiment.uncertainRanking);
		
	}
	
	private SimilarityMatrix addConflitualWeight(SimilarityMatrix sm, List<Mapping> mList)
	{
		Mapping m=null;
		for(int i =0;i<sm.getRows();i++)
		{
			for(int j=0;j<sm.getColumns();j++)
			{
				m=sm.get(i, j);
				if (mList.contains(m))
				{
					sm.setSimilarity(i, j, 1.0);
				}
			}
		}
		return sm;
	}
	
	private List<Mapping> conflitualMapping(SimilarityMatrix sm)
	{
		List<Mapping> lst=new ArrayList<Mapping>();
		Mapping m=null;
		int row=sm.getRows();
		int col=sm.getColumns();
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				m=sm.get(i, j);
				if (m.getSimilarity()!=0)
				{
					if (checkConf(sm,m))
					{
						lst.add(m);
					}
				}
			}
		}
		return lst;
	}
	private boolean checkConf(SimilarityMatrix sm, Mapping m)
	{
		Mapping[] row=sm.getRowMaxValues(m.getSourceKey(), sm.getColumns());
		Mapping[] col=sm.getColMaxValues(m.getTargetKey(), sm.getRows());
		if (numOfMapping(row)>1) return true;
		if (numOfMapping(col)>1) return true;
		return false;
	}
	
	private double numOfMapping(Mapping[] map)
	{
		double count=0;
		for(Mapping m :map)
		{
			if(m.getSimilarity()!=0.0)
				count++;
		}
		return count;
	}

	private void almostCertainMappingRetrieval()
	{
		List<Mapping> unClasses=retriveUnconfidentMapping(classesMatrix);
		List<Mapping> unProperties=retriveUnconfidentMapping(propertiesMatrix);
		SimilarityMatrix rankedClasses=almostCertainMappingComputation(classesMatrix);
		SimilarityMatrix rankedProperties=almostCertainMappingComputation(propertiesMatrix);
		try {
			unClasses = rankedClasses.toList();
			Collections.sort(unClasses, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			unProperties = rankedProperties.toList();
			Collections.sort(unProperties, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		experiment.almostRanking = new ArrayList<Mapping>();
		
		experiment.almostRanking.addAll(unClasses);
		experiment.almostRanking.addAll(unProperties);
		Collections.sort(experiment.almostRanking, new MappingSimilarityComparator() );
		Collections.reverse(experiment.almostRanking);
		
		
		
	}
	
	private SimilarityMatrix almostCertainMappingComputation(SimilarityMatrix ufl)
	{
		SimilarityMatrix sm=ufl.clone();
		Alignment<Mapping> unAlignment=retriveUnconfidentMapping(ufl);
		int row=sm.getRows();
		int col=sm.getColumns();
		double sim=0;
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				if (unAlignment.contains(sm.get(i, j)))
				{
					sim=unAlignment.getSimilarity(sm.get(i, j).getEntity1(), sm.get(i, j).getEntity2());
					sm.setSimilarity(i, j, sim);
				}
			}
		}
		return sm;
	}
	
	private Alignment<Mapping> retriveUnconfidentMapping(SimilarityMatrix sm)
	{
		Alignment<Mapping> mpng=new Alignment<Mapping>(0,0);
		Mapping m=null;
		double maxValue=0.0;
		int row=sm.getRows();
		int col=sm.getColumns();
		double weight=0;
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				weight=0.0;
				if (sm.getSimilarity(i, j)!=0.0)
				{
					weight+=weight_um*(numOfMapping(sm.getColMaxValues(j, row)));
					weight+=weight_um*(numOfMapping(sm.getRowMaxValues(i, col)));
					m=sm.get(i, j);
					m.setSimilarity(weight);
					mpng.add(m);
				}
				if (weight>maxValue)
					maxValue=weight;
			}
		}
		for(Mapping map :mpng)
		{
			map.setSimilarity(map.getSimilarity()/maxValue);
		}
		
		return mpng;
	}
	
	private void disagreementRanking()
	{
		List<MatchingTask> matchers = experiment.initialMatcher.getComponentMatchers();
		List<Mapping> rankedClassMappings=null;
		List<Mapping> rankedPropertyMappings=null;
		
		
		VarianceDisagreementParameters disagreementParams = new VarianceDisagreementParameters();
		disagreementParams.setMatchers(matchers);
		
		VarianceDisagreement disagreementMetric = new VarianceDisagreement();
		disagreementMetric.setParameters(disagreementParams);
		
		// run the disagreement calculations
		SimilarityMatrix classDisagreement = disagreementMetric.getDisagreementMatrix(alignType.aligningClasses);
		

		try {
			rankedClassMappings = classDisagreement.toList();
			Collections.sort(rankedClassMappings, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		classDisagreement = null;  // release the memory used by this
		//relatedMappingRetrival(classDisagreement);
		
		SimilarityMatrix propertyDisagreement = disagreementMetric.getDisagreementMatrix(alignType.aligningProperties);
		
		
		try {
			rankedPropertyMappings = propertyDisagreement.toList();
			Collections.sort(rankedPropertyMappings, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		propertyDisagreement = null;
		
		
		experiment.disRanked = new ArrayList<Mapping>();
		
		experiment.disRanked.addAll(rankedClassMappings);
		experiment.disRanked.addAll(rankedPropertyMappings);
		Collections.sort(experiment.disRanked, new MappingSimilarityComparator() );
		Collections.reverse(experiment.disRanked);
	}


}
