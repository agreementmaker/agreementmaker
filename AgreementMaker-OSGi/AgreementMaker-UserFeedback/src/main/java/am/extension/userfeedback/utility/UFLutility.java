package am.extension.userfeedback.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherResult;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.oneToOneSelection.MwbmSelection;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.extension.userfeedback.experiments.UFLExperiment;

public class UFLutility {
	
	public static final Logger LOG = LogManager.getLogger(UFLutility.class);
	
	static public double[] getSignatureVector(Mapping mp, List<MatchingTask> inputMatchers)
	{
		int size=inputMatchers.size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		MatchingTask a;
		double[] ssv=new double[size];
		for (int i=0;i<size;i++)
		{
			a = inputMatchers.get(i);
			ssv[i] = a.selectionResult.getAlignment().getSimilarity(sourceNode, targetNode);
				
		}
		return ssv;
	}
	
	//check if the signature vector is valid. A valid signature vector must have at least one non zero element.
	static public boolean validSsv(double[] ssv)
	{
		double obj=0.0d;
		for(int i=0;i<ssv.length;i++)
		{
			if (!(ssv[i] == obj))
				return true;
		}
		return false;
	}
	
	static public Alignment<Mapping> combineResults(AbstractMatcher am, UFLExperiment experiment)
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
	
	static public double distanceSV(double[] sv1, double[] sv2)
	{
		double distance=0.0d;
		for (int i=0;i<sv1.length;i++)
		{
			distance+=Math.pow(sv1[i] - sv2[i],2);
		}
		distance=Math.sqrt(distance);
		return distance;
	}
	
	
	static public double distanceMP(Mapping m1, Mapping m2, List<MatchingTask> inputMatchers)
	{
		double[] sv1=getSignatureVector(m1, inputMatchers);
		double[] sv2=getSignatureVector(m2, inputMatchers);
		return distanceSV(sv1, sv2);
	}
	
	static public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	static public List<List<Mapping>> getRelations(SimilarityMatrix sm, List<MatchingTask> inputMatchers)
	{
		Mapping mp=null;
		List<List<Mapping>> lst=new ArrayList<>();
		for(int i=0;i<sm.getRows();i++)
		{
			for(int j=0;j<sm.getColumns();j++)
			{
				mp=sm.get(i, j);
				if ((!inList(mp, lst))&&(validSsv(getSignatureVector(mp, inputMatchers))))
				{
					lst.add(addToList(mp, sm, inputMatchers));
				}
			}
		}
		
		return lst;
	}
	
	static public Boolean inList(Mapping mp, List<List<Mapping>> lst)
	{
		if (lst.size()==0) return false;
		for (List l : lst)
		{
			if (l.contains(mp)) return true;
		}
		return false;
	}
	
	static List<Mapping> addToList(Mapping mp, SimilarityMatrix sm, List<MatchingTask> inputMatchers)
	{
		List<Mapping> lst=new ArrayList<>();
		Mapping m;
		lst.add(mp);
		int row=mp.getSourceKey();
		int col=mp.getTargetKey();
		for(int i=0;i<sm.getRows();i++)
		{
			m=sm.get(i, col);
			if ( (validSsv(getSignatureVector(m, inputMatchers))) && (!m.equals(mp)) )
			{
				lst.add(m);
			}
		}
		for(int j=0;j<sm.getColumns();j++)
		{
			m=sm.get(row,j);
			if ( (validSsv(getSignatureVector(m, inputMatchers))) && (!m.equals(mp)) )
			{
				lst.add(m);
			}
		}
		return lst;
	}
	
	static public double ammortizeSimilarity(double sim, double delta)
	{
		sim+=delta;
		if (sim>1.0d) return 1;
		if (sim<0.0d) return 0;
		return sim;
	}
	
	static public List<SimilarityMatrix> extractList(List<MatchingTask> lst, alignType type)
	{
		List<SimilarityMatrix> mList=new ArrayList<SimilarityMatrix>();
		if (type.equals(alignType.aligningClasses))
		{
			for (int i=0;i<lst.size();i++)
				mList.add(lst.get(i).matcherResult.getClassesMatrix());
		}
		else
		{
			for (int i=0;i<lst.size();i++)
				mList.add(lst.get(i).matcherResult.getPropertiesMatrix());
		}
		return mList;
	}
	
	
	/**
	 * Save a similarity matrix to a file.
	 * 
	 * @param fileName
	 *            The name of the file for saving the matrix. If the path is
	 *            relative (no leading slash), AM_ROOT will be prepended to the
	 *            path.
	 * @param sm
	 *            The similarity matrix that will be saved to the file.
	 */
	static public void saveSimilarityMatrix(String fileName, SimilarityMatrix sm)
	{
		if( !fileName.startsWith(File.separator) )
			fileName = Core.getInstance().getRoot() + fileName;
		
		File file = new File(fileName);
		if( !file.getParentFile().exists() ) file.getParentFile().mkdirs();
		
		Writer bw;
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
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
		catch( IOException ioex ) {
			LOG.error(ioex);
			return;
		}
	}
	
	/**
	 * Runs the MwbmSelection algorithm to select the mappings from the
	 * similarity matrices.
	 * 
	 * @param sourceOntology
	 * @param targetOntology
	 * @param classMatrix
	 * @param propertyMatrix
	 * @param threshold
	 * @param maxSourceAlign
	 *            Source cardinality.
	 * @param maxTargetAlign
	 *            Target cardinality.
	 * @return The computed alignment.
	 */
	public static Alignment<Mapping> computeAlignment(
			Ontology sourceOntology, Ontology targetOntology,
			SimilarityMatrix classMatrix, SimilarityMatrix propertyMatrix,
			double threshold, int maxSourceAlign, int maxTargetAlign) {
		
		MatcherResult mr = new MatcherResult((MatchingTask)null);
		mr.setClassesMatrix(classMatrix);
		mr.setPropertiesMatrix(propertyMatrix);
		mr.setSourceOntology(sourceOntology);
		mr.setTargetOntology(targetOntology);
		
		DefaultSelectionParameters selParam = new DefaultSelectionParameters();
		selParam.threshold = threshold;
		selParam.maxSourceAlign = maxSourceAlign;
		selParam.maxTargetAlign = maxTargetAlign;
		selParam.inputResult = mr;
		
		MwbmSelection selection = new MwbmSelection();
		selection.setParameters(selParam);
		
		selection.select();
		
		return selection.getResult().getAlignment();
	}
	
	public static SimilarityMatrix zeroSim(SimilarityMatrix sm,int source_index,int target_index, int sourceCardinality, int targetCardinality)
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
	
	public static ArrayList<Integer> topN (SimilarityMatrix sm, int sourceIndex, int targetIndex, int topNumber)
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
	
	public static List<Double> normalize(List<Double> lst)
	{
		double max=Double.MIN_VALUE;
		double min=Double.MAX_VALUE;
		
		for(Double d :lst)
		{
			if (d<min)
				min=d;
			if (d>max) max=d;	
		}
		for(int i=0;i<lst.size();i++)
		{
			double tmp=lst.get(i);
			tmp=(tmp-min)/(max-min);
			lst.set(i, tmp);
		}
		return lst;
	}
	
	
	public static void logReferenceAlignment(Alignment<Mapping> referenceAlignment, UFLExperiment experiment) {
		if( referenceAlignment != null ) {
			experiment.info("Reference alignment has " + referenceAlignment.size() + " mappings.");
			for( int i = 0; i < referenceAlignment.size(); i++ ) {
				Mapping currentMapping = referenceAlignment.get(i);
				experiment.info((i+1) + ". " + currentMapping.toString() );
			}
			
			experiment.info("");
		}
	}
}
