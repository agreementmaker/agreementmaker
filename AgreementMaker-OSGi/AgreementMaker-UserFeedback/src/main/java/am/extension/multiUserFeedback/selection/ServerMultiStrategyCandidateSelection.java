package am.extension.multiUserFeedback.selection;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.extension.multiUserFeedback.experiment.MUExperiment;
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
	List<Mapping> mqList;
	List<Mapping> drList;
	List<Mapping> rrList;
	
	@Override
	public void rank(MUExperiment exp) {
		// TODO Auto-generated method stub
		this.experiment=exp;
		
		
		this.count=exp.data.count;
		this.total=exp.data.total;
		
		List<Mapping> toRank=exp.correctMappings;
		if (exp.incorrectMappings!=null)
			toRank.addAll(exp.incorrectMappings);
		
		DisagreementRanking dr=new DisagreementRanking(extractList(exp.initialMatcher.getComponentMatchers(), alignType.aligningClasses),
				extractList(exp.initialMatcher.getComponentMatchers(), alignType.aligningProperties), exp.getUflStorageClassPos(), 
				exp.getUflStorageClass_neg(), exp.getUflStoragePropertyPos(), exp.getUflStorageProperty_neg(), exp.getUflClassMatrix(), exp.getUflPropertyMatrix());
		MappingQualityRanking mqr=new MappingQualityRanking(exp.getUflClassMatrix(), exp.getUflPropertyMatrix());
		RevalidationRanking rr=new RevalidationRanking(extractList(exp.initialMatcher.getComponentMatchers(), alignType.aligningClasses),
				extractList(exp.initialMatcher.getComponentMatchers(), alignType.aligningProperties), exp.getUflStorageClassPos(), 
				exp.getUflStorageClass_neg(), exp.getUflStoragePropertyPos(), exp.getUflStorageProperty_neg(),
				exp.getUflClassMatrix(), exp.getUflPropertyMatrix(),toRank);
		mqList=mqr.rank();
		drList=dr.rank();
		rrList=rr.rank();
		revalidationRate=exp.setup.parameters.getDoubleParameter(Parameter.REVALIDATION_RATE);
		dRate=(1-revalidationRate)/2;
		mqRate=(1-revalidationRate)/2;
		
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
		double c1=(total!=0)? count[0]/total:0.0;
		double c2=(total!=0)? count[1]/total:0.0;
		double c3=(total!=0)? count[2]/total:0.0;
		total++;
		if (c1<mqRate)
		{
			count[0]++;
			experiment.data.count=count;
			experiment.data.total=total;
			return getCandidateMappingFromList(mqList);
			
		}
		if (c2<dRate)
		{
			count[1]++;
			experiment.data.count=count;
			experiment.data.total=total;
			return getCandidateMappingFromList(drList);
		}
		if (c3<revalidationRate)
		{
			count[2]++;
			experiment.data.count=count;
			experiment.data.total=total;
			return getCandidateMappingFromList(rrList);
		}
		
		return null;
	}
	
	
	public Mapping getCandidateMappingFromList(List<Mapping> lst) 
	{

		for( int i = 0; i < lst.size(); i++ ){
			if( experiment.correctMappings == null && experiment.incorrectMappings == null )
				return lst.get(i);
			
			Mapping m = lst.get(i);
			if( experiment.correctMappings != null && (experiment.correctMappings.contains(m.getEntity1(),Ontology.SOURCE) != null ||
				experiment.correctMappings.contains(m.getEntity2(),Ontology.TARGET) != null) ) 
			{
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
		experiment.selectedMapping=getCandidateMapping();
		return experiment.selectedMapping;
	}


}
