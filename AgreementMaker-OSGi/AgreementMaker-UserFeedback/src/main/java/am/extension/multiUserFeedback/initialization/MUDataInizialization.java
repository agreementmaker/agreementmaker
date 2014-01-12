package am.extension.multiUserFeedback.initialization;

import java.util.ArrayList;
import java.util.List;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.inizialization.FeedbackLoopInizialization;
import am.matcher.Combination.CombinationMatcher;

public class MUDataInizialization  extends FeedbackLoopInizialization<MUExperiment> {
	List<AbstractMatcher> inputMatchers = new ArrayList<AbstractMatcher>();
	public MUDataInizialization()
	{
		super();
	}
	
	@Override
	public void inizialize(MUExperiment exp) {
		// TODO Auto-generated method stub
		inputMatchers=exp.initialMatcher.getComponentMatchers();
		SimilarityMatrix smClass=exp.initialMatcher.getFinalMatcher().getClassesMatrix().clone();
		SimilarityMatrix smProperty=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix().clone();
		
		
		
		SimilarityMatrix am=exp.initialMatcher.getFinalMatcher().getClassesMatrix();
		smClass=prepareSMforNB(smClass, am);
		am=exp.initialMatcher.getFinalMatcher().getPropertiesMatrix();
		smProperty=prepareSMforNB(smProperty, am);
		
		exp.setUflClassMatrix(smClass);
		exp.setUflPropertyMatrix(smProperty);
		
		exp.forbiddenPositionsClasses = 
				new SparseMatrix(
						Core.getInstance().getSourceOntology(),
						Core.getInstance().getTargetOntology(), 
						alignType.aligningClasses);
		
		exp.forbiddenPositionsProperties = 
				new SparseMatrix(
						Core.getInstance().getSourceOntology(),
						Core.getInstance().getTargetOntology(), 
						alignType.aligningProperties);
		
		
		// set the UFL matrices for classes
		SparseMatrix sparseClassPos = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningClasses);
		
		SparseMatrix sparseClassNeg = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningClasses);
		
		exp.setUflStorageClassPos(sparseClassPos);
		exp.setUflStorageClass_neg(sparseClassNeg);
		
		// set the UFL matrices for properties
		SparseMatrix sparsePropPos = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningProperties);
		
		SparseMatrix sparsePropNeg = new SparseMatrix(
				Core.getInstance().getSourceOntology(),
				Core.getInstance().getTargetOntology(), 
				alignType.aligningProperties);
		
		exp.setUflStoragePropertyPos(sparsePropPos);
		exp.setUflStorageProperty_neg(sparsePropNeg);
		
		AbstractMatcher ufl=new CombinationMatcher();
		ufl.setClassesMatrix(smClass);
		ufl.setPropertiesMatrix(smProperty);
		ufl.select();

		exp.setMLAlignment(combineResults(ufl, exp));
		
		done();
	}


	
	private SimilarityMatrix prepareSMforNB(SimilarityMatrix sm, SimilarityMatrix am)
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
				else
				{
					sm.setSimilarity(i, j, am.getSimilarity(i, j));
				}
			}
		
		return sm;
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
	
	private Alignment<Mapping> combineResults(AbstractMatcher am, MUExperiment experiment)
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
}
