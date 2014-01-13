package am.extension.multiUserFeedback.selection;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.UFLExperimentParameters.Parameter;
import am.extension.userfeedback.rankingStrategies.DisagreementRanking;
import am.extension.userfeedback.rankingStrategies.MappingQualityRanking;
import am.extension.userfeedback.rankingStrategies.RevalidationRanking;

public class ServerMultiStrategyCandidateSelection extends MUCandidateSelection<MUExperiment> {
	
	MUExperiment experiment;
	int[] count={0,0,0};
	double revalidationRate=0;
	double dRate=0;
	double mqRate=0;
	int total=0;
	
	@Override
	public void rank(MUExperiment exp) {
		// TODO Auto-generated method stub
		this.experiment=exp;
		
		
		this.count=exp.data.count;
		this.total=exp.data.total;
		
		List<Mapping> toRank=new ArrayList<Mapping>();
		if (exp.correctMappings!=null)
			toRank.addAll(exp.correctMappings);
		if (exp.incorrectMappings!=null)
			toRank.addAll(exp.incorrectMappings);
				
		boolean staticCS = experiment.setup.parameters.getBooleanParameter(Parameter.STATIC_CANDIDATE_SELECTION);
		
		// if we're running dynamic OR we're on the first iteration of the static
		// then recompute the metrics
		if( !staticCS || (staticCS && exp.getIterationNumber() == 0) ) {
			
			// NOTE: This is an optimization. Because the disagreement ranking
			// is based only on the Automatic Matchers disagreement, and the
			// automatic matcher outputs don't change, that means we need
			// to run the ranking only once, on the first iteration.
			// Afterwards, the ranking will get a mapping from the pre-computed
			// list. If a mapping is forbidden (in the forbidden matrix), then
			// we choose the next mapping (see getCandidateMappingFromList).
			//if( exp.getIterationNumber() == 0 ) {
				DisagreementRanking dr=new DisagreementRanking(
						extractList(exp.initialMatcher.getComponentMatchers(), alignType.aligningClasses),
						extractList(exp.initialMatcher.getComponentMatchers(), alignType.aligningProperties), 
						exp.getFeedbackMatrix(alignType.aligningClasses, Validation.CORRECT),
						exp.getFeedbackMatrix(alignType.aligningClasses, Validation.INCORRECT),
						exp.getFeedbackMatrix(alignType.aligningProperties, Validation.CORRECT),
						exp.getFeedbackMatrix(alignType.aligningProperties, Validation.INCORRECT),
						exp.getComputedUFLMatrix(alignType.aligningClasses), 
						exp.getComputedUFLMatrix(alignType.aligningProperties));
				
				exp.data.drList = dr.rank();
			//}
			
			MappingQualityRanking mqr=new MappingQualityRanking(
					exp.getComputedUFLMatrix(alignType.aligningClasses),
					exp.getComputedUFLMatrix(alignType.aligningProperties));
			
			exp.data.mqList = mqr.rank();
			
			RevalidationRanking rr = new RevalidationRanking(
					extractList(exp.initialMatcher.getComponentMatchers(), alignType.aligningClasses),
					extractList(exp.initialMatcher.getComponentMatchers(), alignType.aligningProperties),
					exp.getFeedbackMatrix(alignType.aligningClasses, Validation.CORRECT),
					exp.getFeedbackMatrix(alignType.aligningClasses, Validation.INCORRECT),
					exp.getFeedbackMatrix(alignType.aligningProperties, Validation.CORRECT),
					exp.getFeedbackMatrix(alignType.aligningProperties, Validation.INCORRECT),
					exp.getComputedUFLMatrix(alignType.aligningClasses), 
					exp.getComputedUFLMatrix(alignType.aligningProperties),
					toRank);
			
			exp.data.rrList = rr.rank();
		}
		
		revalidationRate = exp.setup.parameters.getDoubleParameter(Parameter.REVALIDATION_RATE);
		dRate  = (1-revalidationRate)/2;
		mqRate = (1-revalidationRate)/2;
		
		done();
	}
	
	private List<SimilarityMatrix> extractList(List<AbstractMatcher> lst, alignType type)
	{
		List<SimilarityMatrix> mList=new ArrayList<SimilarityMatrix>();
		if (type.equals(alignType.aligningClasses))
		{
			for (int i=0;i<lst.size();i++)
				mList.add(lst.get(i).getClassesMatrix());
		}
		else
		{
			for (int i=0;i<lst.size();i++)
				mList.add(lst.get(i).getPropertiesMatrix());
		}
		return mList;
	}

	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> getRankedMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping getCandidateMapping() {
		double c1=(total!=0)? count[0]/(double)total:0.0;
		double c2=(total!=0)? count[1]/(double)total:0.0;
		double c3=(total!=0)? count[2]/(double)total:0.0;
		total++;
		if (c1<=mqRate)
		{
			count[0]++;
			experiment.data.count=count;
			experiment.data.total=total;
			experiment.selectedMapping=getCandidateMappingFromList(experiment.data.mqList);
			return experiment.selectedMapping;
			
		}
		if (c2<=dRate)
		{
			count[1]++;
			experiment.data.count=count;
			experiment.data.total=total;
			experiment.selectedMapping=getCandidateMappingFromList(experiment.data.drList);
			return experiment.selectedMapping;
		}
		if (c3<=revalidationRate)
		{
			count[2]++;
			experiment.data.count=count;
			experiment.data.total=total;
			experiment.selectedMapping=experiment.data.rrList.get(0);
			return experiment.selectedMapping;
		}
		
		return null;
	}
	
	
	public Mapping getCandidateMappingFromList(List<Mapping> lst) 
	{

		for( int i = 0; i < lst.size(); i++ ){
			Mapping m = lst.get(i);
			
			if( experiment.correctMappings == null && experiment.incorrectMappings == null ) {
				//lst.remove(i); // optimization
				return m;
			}
						
			if( experiment.correctMappings != null && (experiment.correctMappings.contains(m.getEntity1(),Ontology.SOURCE) != null ||
				experiment.correctMappings.contains(m.getEntity2(),Ontology.TARGET) != null) ) 
			{
				// assume 1-1 mapping, skip already validated mappings.
				continue;
			}
			if( experiment.incorrectMappings != null && experiment.incorrectMappings.contains(m) ) 
				continue; // we've validated this mapping already.
			
			//lst.remove(i); // optimization.
			return m;
		}
		
		return null;
	}

	@Override
	public void rank(MUExperiment exp, String id) {
		// TODO Auto-generated method stub
		
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

	@Override
	public Mapping getCandidateMapping(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping getSelectedMapping() {
		// TODO Auto-generated method stub
		return experiment.selectedMapping;
	}


}
