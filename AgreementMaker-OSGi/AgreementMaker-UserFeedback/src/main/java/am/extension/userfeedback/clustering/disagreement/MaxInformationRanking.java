package am.extension.userfeedback.clustering.disagreement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MappingSimilarityComparator;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
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
	
	@Override public List<Mapping> getRankedMappings(alignType t) { 
		if( t == alignType.aligningClasses ) { return rankedClassMappings; }
		if( t == alignType.aligningProperties ) { return rankedPropertyMappings; }

		return null;
	}
	
	@Override
	public Mapping getCandidateMapping() {
		/*
		if (useProperty)
		{
			int propSize=propertyRanked.size();
			for( int i = 0; i < allRanked.size(); i++ ){
				if( experiment.correctMappings == null && experiment.incorrectMappings == null ) 
					return propertyRanked.get(propSize).mList.get(i);
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
		}
		else
		{
		*/
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
			//}
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
		
		if(ex.getIterationNumber()>0)
			writeFinalAligment(ex.getIterationNumber(),ex.getMLAlignment());


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
		ex.setRankedClassMatrix(smClass);
		ex.setRankedPropertyMatrix(smProperty);
	}
	
	private void writeFinalAligment(int iteration, Alignment<Mapping> mappings)
	{
		File file = new File("C:/Users/xulin/Desktop/FinalAligment/finalAligment_"+iteration+".txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter fw=null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			bw.write("Numbers of mappings: "+mappings.size()+"\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(Mapping mp : mappings)
		{
			try {
				bw.write(mp.toString());
				bw.write("\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public SimilarityMatrix addPropagationWeight(SimilarityMatrix sm, MLFExperiment experiment, alignType type)
	{
		//double rand=0;
		SimilarityMatrix rankedMatrix=null;
		if (type==alignType.aligningClasses)
				rankedMatrix=experiment.getRankedClassMatrix();
		else
			rankedMatrix=experiment.getRankedPropertyMatrix();
		
		for (int i=0;i<sm.getRows();i++)
			for(int j=0;j<sm.getColumns();j++)
			{
				//rand=1;//Math.random()*100;
				//double x=rankedMatrix.getSimilarity(i, j);
				if (rankedMatrix.getSimilarity(i, j)==0.0)
					sm.setSimilarity(i, j, -100.0);//sm.getSimilarity(i, j)*0.0);
				else if (rankedMatrix.getSimilarity(i, j)==1.0)
					sm.setSimilarity(i, j, sm.getSimilarity(i, j)*0.0);
				else
					sm.setSimilarity(i, j, sm.getSimilarity(i, j)*1.7);
			}
		return sm;
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
		classDisagreement=addPropagationWeight(classDisagreement, experiment, alignType.aligningClasses);
		
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
		propertyDisagreement=addPropagationWeight(propertyDisagreement, experiment, alignType.aligningProperties);
		
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
