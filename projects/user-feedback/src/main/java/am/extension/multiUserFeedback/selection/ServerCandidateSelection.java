package am.extension.multiUserFeedback.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.qualityEvaluation.metrics.ufl.CrossCountQuality;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.evaluation.disagreement.variance.VarianceDisagreement;
import am.evaluation.disagreement.variance.VarianceDisagreementParameters;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;

public class ServerCandidateSelection extends MUCandidateSelection<MUExperiment> {
	
	MUExperiment experiment;
	private SimilarityMatrix classesMatrix;
	private SimilarityMatrix propertiesMatrix;
	private final double weight_um=0.5;
	private final int overlapping=3;
	
	List<Mapping> allRanked=new ArrayList<Mapping>();
	
	public ServerCandidateSelection(){
		super();
	}
	
	@Override
	public void rank(MUExperiment exp, String id) {
		this.experiment=exp;
		inizialization();
		
		uncertainMappingRetrieval();
		//almostCertainMappingRetrieval();
		disagreementRanking();
		
//		Mapping m;
//		List<Mapping> mList=new ArrayList<Mapping>();
		int users=experiment.usersGroup.size();
		
		HashSet<Integer> mpCheck=new HashSet<Integer>();
		if (experiment.getIterationNumber()>0)
			while(mpCheck.size()<overlapping)
				mpCheck.add((int) (Math.random()*users));
		
		done();
	}

	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> getRankedMappings(String id) {
		// TODO Auto-generated method stub
		return null;
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
	
	
	
	
	
	private void inizialization()
	{
		classesMatrix = experiment.getComputedUFLMatrix(alignType.aligningClasses);
		propertiesMatrix = experiment.getComputedUFLMatrix(alignType.aligningProperties);
	}
	
	private SimilarityMatrix userDisagrement(alignType atp)
	{
		double sim=0;
		int numPos=0;
		int numNeg=0;
		
		SimilarityMatrix ud = experiment.getComputedUFLMatrix(atp);
		
		SparseMatrix sparsePos = experiment.getFeedbackMatrix(atp, Validation.CORRECT);
		SparseMatrix sparseNeg = experiment.getFeedbackMatrix(atp, Validation.INCORRECT);
		
		for (int i=0;i<ud.getRows();i++)
		{
			for(int j=0;j<ud.getColumns();j++)
			{
				numPos=(int)sparsePos.getSimilarity(i, j);
				numNeg=(int)sparseNeg.getSimilarity(i, j);
				sim=0-5-(Math.abs(numPos-numNeg)*(0.5/(Math.min(numPos, numNeg)+1)));
				ud.setSimilarity(i, j, sim);
			}
		}
		return ud;
	}
	
	private SimilarityMatrix mergeSM(SimilarityMatrix sm1, SimilarityMatrix sm2)
	{
		SimilarityMatrix merged=sm1;
		for(int i=0; i<merged.getRows();i++)
			for(int j=0;j<merged.getColumns();j++)
				merged.setSimilarity(i, j, (sm1.getSimilarity(i, j)+sm2.getSimilarity(i, j))/2);
		
		return merged;
	}
	
	//Automatic Matcher Disagreement
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
		SimilarityMatrix classDisagreementUsers=userDisagrement(alignType.aligningClasses);
		classDisagreement=mergeSM(classDisagreement,classDisagreementUsers);
		try {
			rankedClassMappings = classDisagreement.toList();
			Collections.sort(rankedClassMappings, new MappingSimilarityComparator() );
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		classDisagreement = null;  // release the memory used by this
		
		SimilarityMatrix propertyDisagreement = disagreementMetric.getDisagreementMatrix(alignType.aligningProperties);
		SimilarityMatrix propertyDisagreementUsers=userDisagrement(alignType.aligningProperties);
		propertyDisagreement=mergeSM(propertyDisagreement, propertyDisagreementUsers);
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
	}

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

//	private void almostCertainMappingRetrieval()
//	{
//		List<Mapping> unClasses=retriveUnconfidentMapping(classesMatrix);
//		List<Mapping> unProperties=retriveUnconfidentMapping(propertiesMatrix);
//		SimilarityMatrix rankedClasses=almostCertainMappingComputation(classesMatrix);
//		SimilarityMatrix rankedProperties=almostCertainMappingComputation(propertiesMatrix);
//		try {
//			unClasses = rankedClasses.toList();
//			Collections.sort(unClasses, new MappingSimilarityComparator() );
//		} catch (Exception e) {
//			e.printStackTrace();
//			return;
//		}
//		
//		try {
//			unProperties = rankedProperties.toList();
//			Collections.sort(unProperties, new MappingSimilarityComparator() );
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		experiment.almostRanking = new ArrayList<Mapping>();
//		
//		experiment.almostRanking.addAll(unClasses);
//		experiment.almostRanking.addAll(unProperties);
//		Collections.sort(experiment.almostRanking, new MappingSimilarityComparator() );
//		Collections.reverse(experiment.almostRanking);
//		
//	}
	
//	private SimilarityMatrix almostCertainMappingComputation(SimilarityMatrix ufl)
//	{
//		SimilarityMatrix sm=ufl.clone();
//		//Alignment<Mapping> unAlignment=retriveUnconfidentMapping(ufl);
//		int row=sm.getRows();
//		int col=sm.getColumns();
//		double sim=0;
//		for(int i=0;i<row;i++)
//		{
//			for(int j=0;j<col;j++)
//			{
//				if (unAlignment.contains(sm.get(i, j)))
//				{
//					sim=unAlignment.getSimilarity(sm.get(i, j).getEntity1(), sm.get(i, j).getEntity2());
//					sm.setSimilarity(i, j, sim);
//				}
//			}
//		}
//		return sm;
//	}
	
	private Alignment<Mapping> retriveUnconfidentMapping(SimilarityMatrix sm, SimilarityMatrix forbidden)
	{
		Alignment<Mapping> mpng=new Alignment<Mapping>(0,0);
		Mapping m=null;
		double maxValue=0.0;
		int row=sm.getRows();
		int col=sm.getColumns();
		CrossCountQuality qm = new CrossCountQuality(sm);
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				if (sm.getSimilarity(i, j)!=0.0)
				{
					double weight = qm.getQuality(null, i, j);
					
					m=sm.get(i, j);
					m.setSimilarity(weight);
					mpng.add(m);
					
					// keep track of the max value in order to normalize (below)
					if(weight > maxValue) maxValue = weight;
				}
			}
		}
		
		// normalize values.
		for(Mapping map :mpng)
		{
			map.setSimilarity(map.getSimilarity()/maxValue);
		}
		
		return mpng;
	}

	@Override
	public Mapping getSelectedMapping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rank(MUExperiment exp) {
		// TODO Auto-generated method stub
		
	}


}
