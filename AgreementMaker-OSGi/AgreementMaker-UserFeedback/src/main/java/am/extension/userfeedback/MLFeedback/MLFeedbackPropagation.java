/*
 * 	Francesco Loprete October 2013
 */
package am.extension.userfeedback.MLFeedback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;


import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.mappingEngine.utility.OAEI_Track;
import am.app.ontology.Node;
import am.extension.userfeedback.FeedbackPropagation;
import am.extension.userfeedback.UserFeedback.Validation;
import am.matcher.Combination.CombinationMatcher;
import am.matcher.Combination.CombinationParameters;

public class MLFeedbackPropagation extends FeedbackPropagation<MLFExperiment> {
	
	final double treshold_up=0.6;
	final double treshold_down=0.1;
	final double penalize_ratio=0.9;
	
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	//Alignment<Mapping> finalMappings;
	
	//genero il data set nella classe principale UFLExperiment
	//- il dataSet deve avere dei campi per il nodo source e traget così da ricostruire i mappings
	//- salvo il training set in UFLExperiment e lo recupero ogni volta

	private Object[] addToSV(Mapping mp, Boolean label)
	{
		//initialMatcher.
		int size=inputMatchers.size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		AbstractMatcher a;
		Object obj=new Object();
		Object[] ssv=new Object[size+1];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			obj=a.getAlignment().getSimilarity(sourceNode, targetNode);
			if (obj!=null)
				ssv[i]=obj;
			else
				ssv[i]=0.0;
			
		}
		if (label)
			ssv[size]=1.0;
		else
			ssv[size]=0.0;
		return ssv;
		
	}
	
	private Object[] getSignatureVector(Mapping mp)
	{
		int size=inputMatchers.size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		AbstractMatcher a;
		Object[] ssv=new Object[size];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			ssv[i]=a.getAlignment().getSimilarity(sourceNode, targetNode);
			
		}
		return ssv;
	}
	
	private void cloneTrainingSet(Object[][] trainingSet, Object[][] vector)
	{
		
		for(int i=0;i<vector.length;i++)
			for(int j=0;j<vector[0].length;j++)
			{
				trainingSet[i][j]=vector[i][j];
			}
	}
	

	//TODO add cardinality...
	private SimilarityMatrix zeroSim(SimilarityMatrix sm,int source_index,int target_index, int sourceCardinality, int targetCardinality)
	{
		ArrayList<Integer> sourceToKeep=new ArrayList<Integer>();
		ArrayList<Integer> targetToKeep=new ArrayList<Integer>();
		if (sourceCardinality!=1)
		{
			sourceToKeep=topN(sm,-1,target_index,sourceCardinality);
		}
		
		if (targetCardinality!=1)
		{
			targetToKeep=topN(sm,source_index,-1,sourceCardinality);
		}
		sourceToKeep.add(source_index);
		targetToKeep.add(target_index);

		
		for(int i=0;i<sm.getRows();i++)
		{
			if (sourceToKeep.contains(i)) 
				continue;
			sm.setSimilarity(i, target_index, 0.0);		
		}
		for(int j=0;j<sm.getColumns();j++)
		{
			if (targetToKeep.contains(j)) 
				continue;
			sm.setSimilarity(source_index, j, 0.0);	
		}
		return sm;
	}
	
	private ArrayList<Integer> topN (SimilarityMatrix sm, int sourceIndex, int targetIndex, int topNumber)
	{
		ArrayList<Integer> top=new ArrayList<Integer>();
		Mapping[] tmp;
		if (targetIndex==-1)
		{
			tmp=sm.getRowMaxValues(sourceIndex, topNumber);	
			for (Mapping m : tmp)
			{
				top.add(m.getTargetKey());
			}
		}
		else
		{
			tmp=sm.getColMaxValues(targetIndex, topNumber);
			for (Mapping m : tmp)
			{
				top.add(m.getSourceKey());
			}
		}

		return top;
	}
	
	//check if the signature vector is valid. A valid signature vector must have at least one non zero element.
	private boolean validSsv(Object[] ssv)
	{
		Object obj=0.0;
		for(int i=0;i<ssv.length;i++)
		{
			if (!ssv[i].equals(obj))
				return true;
		}
		return false;
	}

	@Override
	public void propagate( MLFExperiment experiment ) 
	{
		int iteration=experiment.getIterationNumber();
		inputMatchers=experiment.initialMatcher.getComponentMatchers();
		Mapping candidateMapping = experiment.userFeedback.getCandidateMapping();
		List<AbstractMatcher> availableMatchers = experiment.initialMatcher.getComponentMatchers();
		Object[][] trainingSet=new Object[1][availableMatchers.size()];
		int trainset_index=0;
		
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses) 
		{
			if (experiment.getTrainingSet_classes() !=null)
			{
				trainingSet=new Object[experiment.getTrainingSet_classes().length+1][availableMatchers.size()+1];
				cloneTrainingSet(trainingSet, experiment.getTrainingSet_classes());
				trainset_index=experiment.getTrainingSet_classes().length;
			}
		}
		else
		{
			if (experiment.getTrainingSet_property() !=null)
			{
				trainingSet=new Object[experiment.getTrainingSet_property().length+1][availableMatchers.size()+1];
				cloneTrainingSet(trainingSet, experiment.getTrainingSet_property());
				trainset_index=experiment.getTrainingSet_property().length;
			}
		}
		Validation userFeedback = experiment.userFeedback.getUserFeedback();
		if( userFeedback == Validation.CORRECT )
		{
			trainingSet[trainset_index]=addToSV(candidateMapping, true);
		}
		else
		{
			trainingSet[trainset_index]=addToSV(candidateMapping, false);
		}
		trainingSet=optimizeTrainingSet(trainingSet);
		experiment.getFinalAlignment();
		
		for (int i=0; i<trainingSet.length; i++) {
			for (int j=0; j<trainingSet[i].length; j++) {
				System.out.print(trainingSet[i][j]  +" ");
			}
			System.out.println();
		}

		SimilarityMatrix feedbackClassMatrix=experiment.getUflClassMatrix();
		SimilarityMatrix feedbackPropertyMatrix=experiment.getUflPropertyMatrix();
		Mapping m=null;
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) 
		{
			m = feedbackClassMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) 
				m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 

				feedbackClassMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1.0);
				//feedbackClassMatrix=(zeroSim(experiment.getUflClassMatrix(), candidateMapping.getSourceKey(), candidateMapping.getTargetKey(),experiment.getSourceCardinality(),experiment.getTargetCardinality()));
				//experiment.forbidden_row_classes.add(candidateMapping.getSourceKey());
				//experiment.forbidden_column_classes.add(candidateMapping.getTargetKey());
				experiment.classesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
				
			}
			else if( userFeedback == Validation.INCORRECT ) 
			{ 
				feedbackClassMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
				experiment.classesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
			}
			
		} 
		else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
		{
			m = feedbackPropertyMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) 
				m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 
				feedbackPropertyMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1.0);
				//feedbackPropertyMatrix=zeroSim(experiment.getUflPropertyMatrix(), candidateMapping.getSourceKey(), candidateMapping.getTargetKey(),experiment.getSourceCardinality(),experiment.getTargetCardinality());
				//experiment.forbidden_row_properties.add(candidateMapping.getSourceKey());
				//experiment.forbidden_column_properties.add(candidateMapping.getTargetKey());
				experiment.propertiesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
			}
			else if( userFeedback == Validation.INCORRECT ) 
			{
				feedbackPropertyMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
				experiment.propertiesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
			}
		}
		
		if (iteration==0)
		{
			feedbackClassMatrix=prepareSMforNB(feedbackClassMatrix);
			feedbackPropertyMatrix=prepareSMforNB(feedbackPropertyMatrix);
		}
		
		if (iteration>-10)
		{
			if( candidateMapping.getAlignmentType() == alignType.aligningClasses )
			{
				//feedbackClassMatrix=runNBayes(experiment.classesSparseMatrix ,experiment.forbidden_row_classes, experiment.forbidden_column_classes, feedbackClassMatrix, trainingSet);
				feedbackClassMatrix=runNBayes(experiment.classesSparseMatrix , feedbackClassMatrix, trainingSet);
			}
			else
				if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
				{
					//feedbackPropertyMatrix=runNBayes(experiment.propertiesSparseMatrix,experiment.forbidden_row_properties, experiment.forbidden_column_properties, feedbackPropertyMatrix, trainingSet);
					feedbackPropertyMatrix=runNBayes(experiment.propertiesSparseMatrix, feedbackPropertyMatrix, trainingSet);
				}
		}

		AbstractMatcher ufl=new CombinationMatcher();
		ufl.setClassesMatrix(feedbackClassMatrix);
		ufl.setPropertiesMatrix(feedbackPropertyMatrix);
		ufl.select();

		experiment.setMLAlignment(combineResults(ufl, experiment));
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) 
		{
			experiment.setTrainingSet_classes(trainingSet);
		}
		else
		{
			experiment.setTrainingSet_property(trainingSet);
		}
		
	
		experiment.setUflClassMatrix(feedbackClassMatrix);
		experiment.setUflPropertyMatrix(feedbackPropertyMatrix);
		try {
			writeSimilarityMatrix(feedbackClassMatrix, experiment.getIterationNumber(), "Classes");
			writeSimilarityMatrix(feedbackPropertyMatrix, experiment.getIterationNumber(), "Properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(experiment.getIterationNumber()>0)
			writeFinalAligment(experiment.getIterationNumber(),experiment.getMLAlignment());
		done();
	}
	
	
	private Object[][] optimizeTrainingSet(Object[][] set)
	{
		List<Object[]> obj=new ArrayList<Object[]>(Arrays.asList(set));
		Object[] invalidSV=new Object[set[0].length];
		for(int i=0;i<invalidSV.length;i++)
			invalidSV[i]=0.0;
		obj.remove(Arrays.asList(invalidSV));
		
		set=obj.toArray(set);
		
		
		return set;
	}
	
	private void writeFinalAligment(int iteration, Alignment<Mapping> mappings)
	{
		File file = new File("C:/Users/GELI/WorkFolder/ML/finalAligment_"+iteration+".txt");
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
	
	private void writeSimilarityMatrix(SimilarityMatrix sm, int iteration, String type) throws IOException
	{
		File file = new File("C:/Users/GELI/WorkFolder/ML/"+type+"_similarityMatrix_"+iteration+".txt");
		// if file doesnt exists, then create it
		if (!file.exists()) 
			file.createNewFile();
		FileWriter fw=null;

		fw = new FileWriter(file.getAbsoluteFile());

		BufferedWriter bw = new BufferedWriter(fw);

		for(int i=-1;i<sm.getRows();i++)
		{

			bw.write(i+"\t");
			for (int j=0;j<sm.getColumns();j++)
			{
				if (i==-1)
				{
					bw.write(j+"\t");
				}
				else
				{
					bw.write(round(sm.getSimilarity(i, j),2)+"\t");
				}
				
			}
			bw.write("\n");
		}

		bw.close();
	}
	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	private Alignment<Mapping> combineResults(AbstractMatcher am, MLFExperiment experiment)
	{
		Alignment<Mapping> alg=new Alignment<Mapping>(0,0);
		int row=am.getClassesMatrix().getRows();
		int col=am.getClassesMatrix().getColumns();
		double ufl_sim=0;
		for (int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				ufl_sim=am.getClassesMatrix().getSimilarity(i, j);
				if (ufl_sim!=0.0)
					alg.add(experiment.initialMatcher.getFinalMatcher().getClassesMatrix().get(i, j));
			}
		}
		row=am.getPropertiesMatrix().getRows();
		col=am.getPropertiesMatrix().getColumns();
		ufl_sim=0;
		for (int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				ufl_sim=am.getPropertiesMatrix().getSimilarity(i, j);
				if (ufl_sim!=0.0)
					alg.add(experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix().get(i, j));
			}
		}
		
		return alg;
	}
	
	
//	private AbstractMatcher combineResults(MLFExperiment experiment,List<AbstractMatcher> lwcInputMatchers)
//	{
//	
//		CombinationParameters		param_lwc= new CombinationParameters();
//		try {
//			param_lwc.initForOAEI2010(OAEI_Track.Benchmarks,true); // use the OAEI 2010 settings for this also (Quality Evaluation = Local Confidence)
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		AbstractMatcher lwc = new CombinationMatcher( param_lwc );
//		lwc.setInputMatchers(lwcInputMatchers);
//
//		param_lwc.combinationType = CombinationParameters.MAXCOMB;
//		param_lwc.qualityEvaluation = true;
//		param_lwc.manualWeighted = false;
//		param_lwc.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
//			
//		lwc.setParameters(param_lwc);
//		
//		lwc.setSourceOntology(experiment.getSourceOntology());
//    	lwc.setTargetOntology(experiment.getTargetOntology());
//
//		try {
//			lwc.match();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return lwc;
//
//	}
	
//	private SimilarityMatrix filteringResults(SimilarityMatrix smUFL, SimilarityMatrix smAM)
//	{
//		int col=smUFL.getColumns();
//		double sim=0;
//		double uflSim=0;
//		double amSim=0;
//		int row=smUFL.getRows();
//		for(int i=0;i<row;i++)
//			for(int j=0;j<col;j++)
//			{
//				uflSim=smUFL.getSimilarity(i, j);
//				amSim=smAM.getSimilarity(i, j);
//				if (uflSim!=0)
//					sim=Math.max(uflSim, amSim);
//				else
//					sim=0;
//				smAM.setSimilarity(i, j, sim);
//			}
//		return smAM;
//	}
	
	private SimilarityMatrix prepareSMforNB(SimilarityMatrix sm)
	{
		Mapping mp;
		Object[] ssv;
		for(int i=0;i<sm.getRows();i++)
			for(int j=0;j<sm.getColumns();j++)
			{
				mp = sm.get(i, j);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
				{ 
					sm.setSimilarity(i, j, 0.0);
				}
			}
		
		return sm;
	}
	
	//private SimilarityMatrix runNBayes(SparseMatrix forbidden_pos, TreeSet<Integer> forbidden_row, TreeSet<Integer> forbidden_column, SimilarityMatrix sm,Object[][] trainingSet)
	private SimilarityMatrix runNBayes(SparseMatrix forbidden_pos, SimilarityMatrix sm,Object[][] trainingSet)
	{

		
		int max_row=-1;
		int max_col=-1;
		double max_nBayes=treshold_up;
		double tmp; 
		//SimilarityMatrix sm= feedbackClassMatrix;
		Mapping mp;

		Object[] ssv;
		NaiveBayes nBayes=new NaiveBayes(trainingSet);
		for(int i=0;i<sm.getRows();i++)
		{
//			if (forbidden_row.contains(i)) 
//				continue;
			max_nBayes=treshold_up;
			for(int j=0;j<sm.getColumns();j++)
			{
//				if(forbidden_column.contains(j)) 
//					continue;
				if(forbidden_pos.getSimilarity(i, j)==1)
					continue;
				mp = sm.get(i, j);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
					continue;
				
				tmp=nBayes.interfaceComputeElement(ssv);
				if (tmp>max_nBayes)
				{
					max_nBayes=tmp;
					max_row=i;
					max_col=j;
				}
//				if (tmp<treshold_down)
//				{
//					mp=sm.get(i,j);
//					mp.setSimilarity(mp.getSimilarity()*penalize_ratio);
//					sm.setSimilarity(i, j, sm.getSimilarity(i, j)*0.0);
//					sm.set(i, j, mp);
//					sm.setSimilarity(i, j, 0.0);
//				}
			}
			if (max_nBayes>treshold_up)
			{
				sm.setSimilarity(max_row, max_col, 1);
			}
		}
		return sm;
	}
	

}
