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
	
	static public Object[] getSignatureVector(Mapping mp, List<AbstractMatcher> inputMatchers)
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
	
	//check if the signature vector is valid. A valid signature vector must have at least one non zero element.
	static public boolean validSsv(Object[] ssv)
	{
		Object obj=0.0;
		for(int i=0;i<ssv.length;i++)
		{
			if (!ssv[i].equals(obj))
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
	
	static public double distanceSV(Object[] sv1, Object[] sv2)
	{
		double distance=0.0d;
		for (int i=0;i<sv1.length;i++)
		{
			distance+=Math.pow((double)sv1[i]-(double)sv2[i],2);
		}
		distance=Math.sqrt(distance);
		return distance;
	}
	
	
	static public double distanceMP(Mapping m1, Mapping m2, List<AbstractMatcher> inputMatchers)
	{
		Object[] sv1=getSignatureVector(m1, inputMatchers);
		Object[] sv2=getSignatureVector(m2, inputMatchers);
		return distanceSV(sv1, sv2);
	}
	
	static public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	static public List<List<Mapping>> getRelations(SimilarityMatrix sm, List<AbstractMatcher> inputMatchers)
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
	
	static List<Mapping> addToList(Mapping mp, SimilarityMatrix sm, List<AbstractMatcher> inputMatchers)
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
	
	static public List<SimilarityMatrix> extractList(List<AbstractMatcher> lst, alignType type)
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
}
