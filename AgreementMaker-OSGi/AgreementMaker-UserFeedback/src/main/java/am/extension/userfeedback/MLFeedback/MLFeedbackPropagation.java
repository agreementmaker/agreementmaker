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
import java.util.List;


import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.QualityMetricRegistry;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.utility.OAEI_Track;
import am.app.ontology.Node;
import am.extension.userfeedback.FeedbackPropagation;
import am.extension.userfeedback.UserFeedback.Validation;
import am.matcher.Combination.CombinationMatcher;
import am.matcher.Combination.CombinationParameters;

public class MLFeedbackPropagation extends FeedbackPropagation<MLFExperiment> {
	
	final double treshold_up=0.6;
	final double treshold_down=0.4;
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
		//signatureVector[getIterationNumber()]=ssv;
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
		//signatureVector[getIterationNumber()]=ssv;
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
	private SimilarityMatrix zeroSim(SimilarityMatrix sm,int source_index,int target_index, 
													int sourceCardinality, int targetCardinality)
	{
		ArrayList<Integer> sourceToKeep=new ArrayList<Integer>();
		ArrayList<Integer> targetToKeep=new ArrayList<Integer>();
		if (sourceCardinality!=1)
		{
			topN(sm,-1,target_index,sourceCardinality);
		}
		targetToKeep.add(target_index);
		if (targetCardinality!=1)
		{
			topN(sm,source_index,-1,sourceCardinality);
		}
		sourceToKeep.add(source_index);

		
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
	/*
	private Object[][] fillDataSet(MLFExperiment experiment, alignType type)
	{
		Object[][] dataSet=null;
		Alignment<Mapping> mp=null;
		SimilarityMatrix sm=null;
		int row=0;
		int col=0;
		if (type==alignType.aligningClasses)
		{
			sm=experiment.initialMatcher.getFinalMatcher().getClassesMatrix();
			mp=experiment.initialMatcher.getClassAlignment();//  getFinalAlignment;
			dataSet=new Object[sm.getRows()][inputMatchers.size()];
		}
		else
		{
			sm=experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix();
			mp=experiment.initialMatcher.getPropertyAlignment();
			dataSet=new Object[sm.getRows()][inputMatchers.size()];
		}
		AbstractMatcher a;
		for(int i=0;i<mp.size();i++)
		{
			for (int j=0;j<inputMatchers.size();j++)
			{
				a = inputMatchers.get(j);
				a.getClassesMatrix().getSimilarity(i, j);
				dataSet[i][j]=sm.getSimilarity(mp.get(i).getEntity1().getIndex(), mp.get(i).getEntity2().getIndex());
				
			}
		}
		return dataSet;
	}
	
	private Object[][] fillDataSet(SimilarityMatrix sm)
	{
		List<Object[]> lst=new ArrayList<Object[]>();
		Mapping mp=null;
		Object[] ssv=new Object[inputMatchers.size()];
		int row=sm.getRows();
		int col=sm.getColumns();
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
			{
				mp = sm.get(i, j);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
					continue;
				else
					lst.add(ssv);
			}
		}
		Object[][] obj=new Object[lst.size()][inputMatchers.size()];
		lst.toArray(obj);//o toArray();
		return obj;
	}
	*/
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
	public void propagate( MLFExperiment experiment ) {
		
		//UFLExperiment log = experiment;
		
		int iteration=experiment.getIterationNumber();
		inputMatchers=experiment.initialMatcher.getComponentMatchers();
		Mapping candidateMapping = experiment.userFeedback.getCandidateMapping();
		List<AbstractMatcher> availableMatchers = experiment.initialMatcher.getComponentMatchers();
		Object[][] trainingSet=new Object[1][availableMatchers.size()];
		//Object[][] dataSet;
		int trainset_index=0;
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses) 
		{
			if (experiment.getTrainingSet_classes() !=null)
			{
				trainingSet=new Object[experiment.getTrainingSet_classes().length+1][availableMatchers.size()+1];
				cloneTrainingSet(trainingSet, experiment.getTrainingSet_classes());
				trainset_index=experiment.getTrainingSet_classes().length;
			}
			/*
			if (experiment.getDataSet_classes()!=null)
			{
				
				dataSet=experiment.getDataSet_classes();
			}
			else
			{
				dataSet=fillDataSet(experiment.initialMatcher.getFinalMatcher().getClassesMatrix());
				//dataSet=fillDataSet(experiment, alignType.aligningClasses);
			}
			*/
		}
		else
		{
			if (experiment.getTrainingSet_property() !=null)
			{
				trainingSet=new Object[experiment.getTrainingSet_property().length+1][availableMatchers.size()+1];
				cloneTrainingSet(trainingSet, experiment.getTrainingSet_property());
				trainset_index=experiment.getTrainingSet_property().length;
			}
			/*
			if (experiment.getDataSet_property()!=null)
			{
				dataSet=experiment.getDataSet_property();
			}
			else
			{
				dataSet=fillDataSet(experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix());
				//dataSet=fillDataSet(experiment, alignType.aligningProperties);
			}
			*/
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

		experiment.getFinalAlignment();
		//SimilarityMatrix feedbackClassMatrix = experiment.initialMatcher.getFinalMatcher().getClassesMatrix();
		//SimilarityMatrix feedbackPropertyMatrix = experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix();
		
		SimilarityMatrix feedbackClassMatrix=experiment.getRankedClassMatrix();
		SimilarityMatrix feedbackPropertyMatrix=experiment.getRankedPropertyMatrix();
		Mapping m=null;
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) 
		{
			m = feedbackClassMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) 
				m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 
				feedbackClassMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1.0);
				feedbackClassMatrix=(zeroSim(experiment.getRankedClassMatrix(), candidateMapping.getSourceKey(), candidateMapping.getTargetKey(),experiment.getSourceCardinality(),experiment.getTargetCardinality()));
				experiment.forbidden_row.add(candidateMapping.getSourceKey());
				experiment.forbidden_column.add(candidateMapping.getTargetKey());
			}
			else if( userFeedback == Validation.INCORRECT ) 
			{ 
				//m.setSimilarity(0.0); 
				feedbackClassMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
			}
			
			//feedbackClassMatrix.set(candidateMapping.getSourceKey(), candidateMapping.getTargetKey(), m);
		} 
		else if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
		{
			m = feedbackPropertyMatrix.get(candidateMapping.getSourceKey(), candidateMapping.getTargetKey());
			if( m == null ) 
				m = new Mapping(candidateMapping);
			
			if( userFeedback == Validation.CORRECT ) 
			{ 
				//m.setSimilarity(1.0d);
				feedbackPropertyMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1.0);
				feedbackPropertyMatrix=zeroSim(feedbackPropertyMatrix, candidateMapping.getSourceKey(), candidateMapping.getTargetKey(),experiment.getSourceCardinality(),experiment.getTargetCardinality());
				experiment.forbidden_row.add(candidateMapping.getSourceKey());
				experiment.forbidden_column.add(candidateMapping.getTargetKey());
			}
			else if( userFeedback == Validation.INCORRECT ) 
			{
				feedbackPropertyMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
			}
			
		}
		
		if (iteration==0)
		{
			feedbackClassMatrix=prepareSMforNB(feedbackClassMatrix);
			feedbackPropertyMatrix=prepareSMforNB(feedbackPropertyMatrix);
		}
		
		if (iteration>0)
		{
			if( candidateMapping.getAlignmentType() == alignType.aligningClasses )
			{
				feedbackClassMatrix=runNBayes(experiment, feedbackClassMatrix, trainingSet);
			}
			else
				if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
				{
					feedbackPropertyMatrix=runNBayes(experiment, feedbackPropertyMatrix, trainingSet);
				}
		}
		
		
		
		AbstractMatcher automaticMatchers=experiment.initialMatcher.getFinalMatcher();
		//automaticMatchers.setClassesMatrix(experiment.initialMatcher.getFinalMatcher().getClassesMatrix());
		//automaticMatchers.setPropertiesMatrix(experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix());
		List<AbstractMatcher> lwcInputMatchers = new ArrayList<AbstractMatcher>();
		automaticMatchers.select();
		AbstractMatcher ufl=new CombinationMatcher();
		ufl.setClassesMatrix(feedbackClassMatrix);
		ufl.setPropertiesMatrix(feedbackPropertyMatrix);
		ufl.select();
		AbstractMatcher ufl2=new CombinationMatcher();
		ufl2.setClassesMatrix(feedbackClassMatrix);
		ufl2.setPropertiesMatrix(feedbackPropertyMatrix);
		ufl2.select();
		lwcInputMatchers.add(automaticMatchers);
		lwcInputMatchers.add(ufl);
		lwcInputMatchers.add(ufl2);
		lwcInputMatchers.add(ufl);
		
		AbstractMatcher a=combineResults(experiment, lwcInputMatchers);
		a.setClassesMatrix(filteringResults(feedbackClassMatrix, experiment.initialMatcher.getFinalMatcher().getClassesMatrix()));
		a.setPropertiesMatrix(filteringResults(feedbackPropertyMatrix, experiment.initialMatcher.getFinalMatcher().getPropertiesMatrix()));
		a.select();
		//System.out.println(a.getAlignment().toString());
		experiment.setMLAlignment(a.getAlignment());
		
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses ) 
		{
			//experiment.setDataSet_classes(dataSet);
			experiment.setTrainingSet_classes(trainingSet);
		}
		else
		{
			//experiment.setDataSet_property(dataSet);
			experiment.setTrainingSet_property(trainingSet);
		}
		
		
		experiment.setRankedClassMatrix(feedbackClassMatrix);
		experiment.setRankedPropertyMatrix(feedbackPropertyMatrix);
		try {
			writeSimilarityMatrix(feedbackClassMatrix, experiment.getIterationNumber(), "Classes");
			writeSimilarityMatrix(feedbackPropertyMatrix, experiment.getIterationNumber(), "Properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		done();
	}
	
	private void writeSimilarityMatrix(SimilarityMatrix sm, int iteration, String type) throws IOException
	{
		File file = new File("/home/frank/Documents/SimilarityMatrix"+type+"/similarityMatrix_"+iteration+".txt");
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
	
	
	private AbstractMatcher combineResults(MLFExperiment experiment,List<AbstractMatcher> lwcInputMatchers)
	{
	
		CombinationParameters		param_lwc= new CombinationParameters();
		try {
			param_lwc.initForOAEI2010(OAEI_Track.Benchmarks,true); // use the OAEI 2010 settings for this also (Quality Evaluation = Local Confidence)
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		AbstractMatcher lwc = new CombinationMatcher( param_lwc );
		lwc.setInputMatchers(lwcInputMatchers);

		param_lwc.combinationType = CombinationParameters.AVERAGECOMB;
		param_lwc.qualityEvaluation = true;
		param_lwc.manualWeighted = false;
		param_lwc.quality = QualityMetricRegistry.LOCAL_CONFIDENCE;
			
		lwc.setParameters(param_lwc);
		
		lwc.setSourceOntology(experiment.getSourceOntology());
    	lwc.setTargetOntology(experiment.getTargetOntology());

		try {
			lwc.match();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lwc;

	}
	
	private SimilarityMatrix filteringResults(SimilarityMatrix smUFL, SimilarityMatrix smAM)
	{
		int col=smUFL.getColumns();
		double sim=0;
		double uflSim=0;
		double amSim=0;
		int row=smUFL.getRows();
		for(int i=0;i<row;i++)
			for(int j=0;j<col;j++)
			{
				uflSim=smUFL.getSimilarity(i, j);
				amSim=smAM.getSimilarity(i, j);
				if (uflSim!=0)
					sim=Math.max(uflSim, amSim);
				else
					sim=0;
				smAM.setSimilarity(i, j, sim);
			}
		return smAM;
	}
	
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
	
	private SimilarityMatrix runNBayes(MLFExperiment experiment, SimilarityMatrix sm,Object[][] trainingSet)
	{

		int max_row=-1;
		int max_col=-1;
		double max_nBayes=treshold_up;
		double tmp; 
		//SimilarityMatrix sm= feedbackClassMatrix;
		Mapping mp;
		if (experiment.getIterationNumber()==80)
			System.out.println("Ciao");
		
		Object[] ssv;
		NaiveBayes nBayes=new NaiveBayes(trainingSet);
		for(int i=0;i<sm.getRows();i++)
		{
			if (experiment.forbidden_row.contains(i)) 
				continue;
			max_nBayes=treshold_up;
			for(int j=0;j<sm.getColumns();j++)
			{
				if(experiment.forbidden_column.contains(j)) 
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
				//if (tmp<treshold_down)
				//{
					//mp=sm.get(i,j);
					//mp.setSimilarity(mp.getSimilarity()*penalize_ratio);
					//sm.setSimilarity(i, j, sm.getSimilarity(i, j)*0.0);
					//sm.set(i, j, mp);
				//}
			}
			if (max_nBayes>treshold_up)
			{
				sm.setSimilarity(max_row, max_col, 1);
				//sm=zeroSim(sm, max_row, max_col,0);
			}
		}
		return sm;
	}
	

}
