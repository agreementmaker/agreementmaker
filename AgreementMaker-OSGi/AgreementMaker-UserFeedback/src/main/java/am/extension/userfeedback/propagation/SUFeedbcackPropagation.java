/*
 * 	Francesco Loprete December 2013
 */
package am.extension.userfeedback.propagation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.MLutility.NaiveBayes;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.SUExperiment;
import am.matcher.Combination.CombinationMatcher;
public class SUFeedbcackPropagation extends FeedbackPropagation<SUExperiment> {
	
	final double treshold_up=0.6;
	final double treshold_down=0.01;
	final double penalize_ratio=0.9;
	private SUExperiment experiment;
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	

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
	public void propagate( SUExperiment exp ) 
	{
		this.experiment=exp;
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
//				if (experiment.getAlignCardinalityType()==alignCardinality.c1_1)
//					feedbackClassMatrix=(zeroSim(experiment.getUflClassMatrix(), candidateMapping.getSourceKey(), candidateMapping.getTargetKey(),1,1));
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
//				if (experiment.getAlignCardinalityType()==alignCardinality.c1_1)
//					feedbackPropertyMatrix=zeroSim(experiment.getUflPropertyMatrix(), candidateMapping.getSourceKey(), candidateMapping.getTargetKey(),1,1);
				experiment.propertiesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
			}
			else if( userFeedback == Validation.INCORRECT ) 
			{
				feedbackPropertyMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 0.0);
				experiment.propertiesSparseMatrix.setSimilarity(m.getSourceKey(), m.getTargetKey(), 1);
			}
		}
		
//		if (iteration==0)
//		{
//			feedbackClassMatrix=prepareSMforNB(feedbackClassMatrix);
//			feedbackPropertyMatrix=prepareSMforNB(feedbackPropertyMatrix);
//		}
		
		try {
			writeSimilarityMatrix(feedbackClassMatrix, experiment.getIterationNumber(), "Classes");
			writeSimilarityMatrix(feedbackPropertyMatrix, experiment.getIterationNumber(), "Properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	//	if(experiment.getIterationNumber()<10)
//			if( candidateMapping.getAlignmentType() == alignType.aligningClasses )
//			{
//				feedbackClassMatrix=runNBayes(experiment.classesSparseMatrix , feedbackClassMatrix, trainingSet);
//			}
//			else
//			{
//				if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
//				{
//					feedbackPropertyMatrix=runNBayes(experiment.propertiesSparseMatrix, feedbackPropertyMatrix, trainingSet);
//				}
//			}
		
		if( candidateMapping.getAlignmentType() == alignType.aligningClasses )
		{
			feedbackClassMatrix=com(experiment.classesSparseMatrix , feedbackClassMatrix, trainingSet, "classes");
		}
		else
		{
			if( candidateMapping.getAlignmentType() == alignType.aligningProperties ) 
			{
				feedbackPropertyMatrix=com(experiment.propertiesSparseMatrix, feedbackPropertyMatrix, trainingSet, "properties");
			}
		}
		
		AbstractMatcher ufl=new CombinationMatcher();
		ufl.setClassesMatrix(feedbackClassMatrix);
		ufl.setPropertiesMatrix(feedbackPropertyMatrix);
		//ufl.select();

		experiment.setMLAlignment(combineResults(ufl));
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
		
		
		
		
		if(experiment.getIterationNumber()>0)
			writeFinalAligment(experiment.getIterationNumber(),experiment.getMLAlignment());
		done();
	}
	
	
	private Object[][] optimizeTrainingSet(Object[][] set)
	{
		int count=0;
		List<Integer> pos=new ArrayList<Integer>();
		for(int i=0;i<set.length;i++)
		{
			count=0;
			for(int j=0;j<set[0].length;j++)
			{
				if((Double)set[i][j]==0.0)
					count++;
			}
			if (count==set[0].length)
				pos.add(i);
		}
		Object[][] newSet=new Object[set.length-pos.size()][set[0].length];
		int index=0;
		if (pos.size()!=0)
		{
			for(int i=0;i<set.length;i++)
			{
				if (pos.contains(i))
					continue;
				
				for(int j=0;j<set[0].length;j++)
				{
					newSet[index][j]=set[i][j];
				}
				index++;
			}
			set=newSet;
		}
		
		return set;
	}
	
	private void writeFinalAligment(int iteration, Alignment<Mapping> mappings)
	{
		File file = new File("/home/frank/Documents/FinalAligment/finalAligment_"+iteration+".txt");
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
					bw.write(round(sm.getSimilarity(i, j),2)+"");
					if (j<sm.getColumns()-1)
						bw.write("\t");
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
	
	private Alignment<Mapping> combineResults(AbstractMatcher am)
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
	
	

//	private SimilarityMatrix prepareSMforNB(SimilarityMatrix sm)
//	{
//		Mapping mp;
//		Object[] ssv;
//		for(int i=0;i<sm.getRows();i++)
//			for(int j=0;j<sm.getColumns();j++)
//			{
//				mp = sm.get(i, j);
//				ssv=getSignatureVector(mp);
//				if (!validSsv(ssv))
//				{ 
//					sm.setSimilarity(i, j, 0.0);
//				}
//			}
//		
//		return sm;
//	}
//	
	private SimilarityMatrix runNBayes(SparseMatrix forbidden_pos, SimilarityMatrix sm,Object[][] trainingSet)
	{

		
		int max_row=-1;
		int max_col=-1;
		double max_nBayes=treshold_up;
		double tmp; 
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
				if ((tmp<0.2) && (experiment.getIterationNumber()<10))
				{
//					System.out.println(experiment.getIterationNumber());
//					mp=sm.get(i,j);
//					mp.setSimilarity(mp.getSimilarity()*penalize_ratio);
//					sm.setSimilarity(i, j, sm.getSimilarity(i, j)*0.0);
//					sm.set(i, j, mp);
					sm.setSimilarity(i, j, 0.0);
				}
			}
			if (max_nBayes>treshold_up)
			{
				sm.setSimilarity(max_row, max_col, 1);
			}
		}
		return sm;
	}
	
	
	private SimilarityMatrix com(SparseMatrix forbidden_pos, SimilarityMatrix sm,Object[][] trainingSet, String type)
	{
		Mapping mp;
		Object[] ssv;
		double tmp=0;
		double distance=0;
		double min=Double.MAX_VALUE;
		int index=0;
		for(int k=0;k<sm.getRows();k++)
		{
			for(int h=0;h<sm.getColumns();h++)
			{
				if(forbidden_pos.getSimilarity(k, h)==1)
					continue;
				mp = sm.get(k, h);
				ssv=getSignatureVector(mp);
				if (!validSsv(ssv))
					continue;
				min=Double.MAX_VALUE;
				for(int i=0;i<trainingSet.length;i++)
				{
					distance=0;
					for(int j=0;j<ssv.length;j++)
					{
						distance+=Math.pow((double)ssv[j]-(double)trainingSet[i][j],2);
					}
					distance=Math.sqrt(distance);
					if (distance<min)
					{
						min=distance;
						index=i;
					}
				}
				//tmp=(double)trainingSet[index][trainingSet[0].length-1];
				if ((min<=0.5))// & (tmp==1.0))
				{
					sm.setSimilarity(k, h, (double)trainingSet[index][trainingSet[0].length-1]);
					if (type=="classes")
						experiment.classesSparseMatrix.setSimilarity(k, h, 1);
					else
						experiment.propertiesSparseMatrix.setSimilarity(k, h, 1);
					
				}
			}
		}
		return sm;
	}
	

}
