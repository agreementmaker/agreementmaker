/*
 * 	Francesco Loprete October 2013
 */
package am.extension.userfeedback.MLFeedback;

import java.util.ArrayList;
import java.util.List;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.extension.userfeedback.FeedbackPropagation;
import am.extension.userfeedback.UserFeedback.Validation;

public class MLFeedbackPropagation extends FeedbackPropagation<MLFExperiment> {
	
	
	Object[][] trainingSet;
	//Object[][] dataSet;
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	//Alignment<Mapping> finalMappings;
	
	//genero il data set nella classe principale UFLExperiment
	//- il dataSet deve avere dei campi per il nodo source e traget così da ricostruire i mappings
	// salvo il training set in UFLExperiment e lo recupero ogni volta

	private Object[] addToSV(Mapping mp, Boolean label)
	{
		//initialMatcher.
		int size=inputMatchers.size();
		Node sourceOnto=mp.getEntity1();
		Node targetOnto=mp.getEntity2();
		AbstractMatcher a;
		Object[] ssv=new Object[size+1];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			ssv[i]=a.getMapping(sourceOnto, targetOnto).getSimilarity();
			
		}
		if (label)
			ssv[size]=1;
		else
			ssv[size]=0;
		//signatureVector[getIterationNumber()]=ssv;
		return ssv;
		
	}
	
	private Object[] getSignatureVector(Mapping mp)
	{
		int size=inputMatchers.size();
		Node sourceOnto=mp.getEntity1();
		Node targetOnto=mp.getEntity2();
		AbstractMatcher a;
		Object[] ssv=new Object[size];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			ssv[i]=a.getMapping(sourceOnto, targetOnto).getSimilarity();
			
		}
		//signatureVector[getIterationNumber()]=ssv;
		return ssv;
	}
	
	private void clone(Object[][] vector)
	{
		
		for(int i=0;i<vector.length;i++)
			for(int j=0;j<vector[0].length;j++)
			{
				trainingSet[i][j]=vector[i][j];
			}
	}
	

//	private void addFinalMapping(SimilarityMatrix sm, NaiveBayes nBayes)
//	{
//		for(int i=0;i<sm.getRows();i++)
//		{
//			for(int j=0;j<sm.getColumns();j++)
//			{
//				Mapping mp = sm.get(i, j);
//				if(nBayes.computeElement(getSignatureVector(mp)))
//				{
//					mp.setSimilarity(1.0);
//					finalMappings.add(mp);
//				}
//			}
//		}
//	}
	
	private SimilarityMatrix zeroSim(SimilarityMatrix sm,int source_index,int target_index)
	{
		Mapping mp;
		for(int i=0;i<sm.getRows();i++)
		{
			if (i==source_index) continue;
			mp=sm.get(i, target_index);
			mp.setSimilarity(0.0);
			sm.set(i, target_index, mp);
		}
		for(int j=0;j<sm.getColumns();j++)
		{
			if (j==target_index) continue;
			mp=sm.get(source_index, 1);
			mp.setSimilarity(0.0);
			sm.set(source_index,j, mp);
		}
		return sm;
	}

	@Override
	public void propagate( MLFExperiment experiment ) {
		
		//UFLExperiment log = experiment;
		
		int iteration=experiment.getIterationNumber();
		Mapping candidateMapping = experiment.userFeedback.getCandidateMapping();
		List<AbstractMatcher> availableMatchers = experiment.initialMatcher.getComponentMatchers();
		trainingSet=new Object[iteration][availableMatchers.size()+1];
		clone(experiment.getTrainingSet());
		//dataSet=experiment.getDataSet();

		Validation userFeedback = experiment.userFeedback.getUserFeedback();
		if( userFeedback == Validation.CORRECT )
		{
			trainingSet[iteration]=addToSV(candidateMapping, true);
		}
		else
		{
			trainingSet[iteration]=addToSV(candidateMapping, false);
		}
		/*if (iteration<5){
			done();
			return;
		}*/
		
		
		//Alignment<Mapping> am= experiment.initialMatcher.getAlignment();
		NaiveBayes nBayes=new NaiveBayes(trainingSet);
		
		
		
		SimilarityMatrix feedbackClassMatrix = experiment.initialMatcher.getFinalMatcher().getClassesMatrix();
		SimilarityMatrix feedbackPropertyMatrix = experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix();
		
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) {
			Mapping m = feedbackClassMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 
				m.setSimilarity(1.0d); 
				zeroSim(feedbackClassMatrix, candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
				experiment.forbidden_row.add(candidateMapping.getSourceKey());
				experiment.forbidden_column.add(candidateMapping.getTargetKey());
			}
			else if( userFeedback == Validation.INCORRECT ) { m.setSimilarity(0.0d); }
			
			feedbackClassMatrix.set(candidateMapping.getSourceKey(), candidateMapping.getTargetKey(), m);
		} 
		else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) {
			Mapping m = feedbackPropertyMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 
				m.setSimilarity(1.0d);
				zeroSim(feedbackClassMatrix, candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
				experiment.forbidden_row.add(candidateMapping.getSourceKey());
				experiment.forbidden_column.add(candidateMapping.getTargetKey());
			}
			else if( userFeedback == Validation.INCORRECT ) { m.setSimilarity(0.0d); }
			
			feedbackPropertyMatrix.set(candidateMapping.getSourceKey(), candidateMapping.getTargetKey(), m);
		}
		int max_row=-1;
		int max_col=-1;
		double max_nBayes=0;
		double tmp; 
		SimilarityMatrix sm= feedbackClassMatrix;
		Mapping mp;
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses )
		{
			sm=feedbackClassMatrix;
		}
		else
			if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
				sm=feedbackPropertyMatrix;
				
		for(int i=0;i<feedbackClassMatrix.getRows();i++)
		{
			if (experiment.forbidden_row.contains(i)) continue;
			max_nBayes=-1;
			for(int j=0;j<feedbackClassMatrix.getColumns();j++)
			{
				if(experiment.forbidden_column.contains(j)) continue;
				mp = sm.get(i, j);
				tmp=nBayes.computeElement(getSignatureVector(mp));
				if (tmp>max_nBayes)
				{
					max_nBayes=tmp;
					max_row=i;
					max_col=j;
				}
			}
			if (max_nBayes>0)
			{
				mp=sm.get(max_row,max_col);
				mp.setSimilarity(1.0);
				sm.set(max_row, max_col, mp);
			}
		}
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses )
		{
			experiment.initialMatcher.getFinalMatcher().setClassesMatrix(sm);
		}
		else
			if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
				experiment.initialMatcher.getFinalMatcher().setPropertiesMatrix(sm);
		
		experiment.initialMatcher.getFinalMatcher().select();
		done();
	}
	

}
