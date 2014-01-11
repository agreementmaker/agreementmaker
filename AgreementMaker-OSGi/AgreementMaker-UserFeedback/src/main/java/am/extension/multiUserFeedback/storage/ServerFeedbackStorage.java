package am.extension.multiUserFeedback.storage;


import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;

public class ServerFeedbackStorage extends MUFeedbackStorage<MUExperiment>{
	MUExperiment experiment;
	Mapping lastAdded;
	double correctionLabel=0;
	Boolean flag=false;
	@Override
	public void addFeedback(MUExperiment exp, Mapping candidateMapping,
			String id) {
		this.experiment=exp;
		
		// TODO Auto-generated method stub
		SparseMatrix sparse=exp.getUflStorageClassPos();
		int row=candidateMapping.getSourceKey();
		int col=candidateMapping.getTargetKey();
		double sim=0.0;
		
		

		if(candidateMapping.getAlignmentType()==alignType.aligningClasses)
		{
			if (flag)
			{
				correctionLabel=labelizeSingleTS(exp.getUflStorageClassPos().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()),exp.getUflStorageClass_neg().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()));
			}
			
			if (exp.feedback.equals("CORRECT"))
			{
				sparse=exp.getUflStorageClassPos();
				sim=sparse.getSimilarity(row, col);
				sim++;
				sparse.setSimilarity(row, col, sim);
				exp.setUflStorageClassPos(sparse);
			}
			if (exp.feedback.equals("UNCORRECT"))
			{
				sparse=exp.getUflStorageClass_neg();
				sim=sparse.getSimilarity(row, col);
				sim++;
				sparse.setSimilarity(row, col, sim);
				exp.setUflStorageClass_neg(sparse);
			}
			
		}
		else if(candidateMapping.getAlignmentType()==alignType.aligningProperties)
		{
			if (flag)
			{
				correctionLabel=labelizeSingleTS(exp.getUflStoragePropertyPos().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()),exp.getUflStorageProperty_neg().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()));
			}
			
			if (exp.feedback.equals("CORRECT"))
			{
				sparse=exp.getUflStoragePropertyPos();
				sim=sparse.getSimilarity(row, col);
				sim++;
				sparse.setSimilarity(row, col, sim);
				exp.setUflStoragePropertyPos(sparse);
			}
			if (exp.feedback.equals("UNCORRECT"))
			{
				sparse=exp.getUflStorageProperty_neg();
				sim=sparse.getSimilarity(row, col);
				sim++;
				sparse.setSimilarity(row, col, sim);
				exp.setUflStorageProperty_neg(sparse);
			}
			
		}
		
		getTrainingSet();
		
		done();
	}
	
	public void getSingleTrainingSet()
	{
		Object[][] tmp= new Object[1][experiment.initialMatcher.getComponentMatchers().size()+1];
		tmp[0]=getLabeledMapping(lastAdded,correctionLabel);
		if (lastAdded.getAlignmentType().equals(alignType.aligningClasses))
			experiment.setTrainingSet_classes(tmp);
		else
			experiment.setTrainingSet_property(tmp);
		
	}
	
	private int labelizeSingleTS(double pos, double neg)
	{
		int x=experiment.feedback.equals("CORRECT")?1:-1;
		int sum=(int)pos-(int)neg+x;
		if (sum>0) return 1;
		if (sum<0) return 0;
		if (x>0) return 1;
		return 0;
		
	}

	@Override
	public void getTrainingSet() {
		double sum=0;
		List<Object[]> class_trn=new ArrayList<Object[]>();
		List<Object[]> prop_trn=new ArrayList<Object[]>();
		SparseMatrix sparsePos=experiment.getUflStorageClassPos();
		SparseMatrix sparseNeg=experiment.getUflStorageClass_neg();
		for(int i=0;i<sparsePos.getRows();i++)
		{
			for(int j=0;j<sparsePos.getColumns();j++)
			{
				sum=sparsePos.getSimilarity(i, j)-sparseNeg.getSimilarity(i, j);
				if (sum!=0)
					class_trn.add(getLabeledMapping(sparsePos.get(i, j),sum));
			}
		}

		Object[][] cl_training=new Object[class_trn.size()][experiment.initialMatcher.getComponentMatchers().size()+1];
		class_trn.toArray(cl_training);
		experiment.setTrainingSet_classes(cl_training);
		
		sparsePos=experiment.getUflStoragePropertyPos();
		sparseNeg=experiment.getUflStorageProperty_neg();
		for(int i=0;i<sparsePos.getRows();i++)
		{
			for(int j=0;j<sparsePos.getColumns();j++)
			{
				sum=sparsePos.getSimilarity(i, j)-sparseNeg.getSimilarity(i, j);
				if (sum!=0)
					class_trn.add(getLabeledMapping(sparsePos.get(i, j),sum));
			}
		}
		
		Object[][] pr_training=new Object[prop_trn.size()][experiment.initialMatcher.getComponentMatchers().size()+1];
		class_trn.toArray(pr_training);
		experiment.setTrainingSet_classes(pr_training);

	}
	
	private Object[] getLabeledMapping(Mapping mp, double sim)
	{
		Object[] sv=new Object[experiment.initialMatcher.getComponentMatchers().size()+1];
		sv=getSignatureVector(mp);
		if (sim>0)
			sv[sv.length]=1.0;
		if (sim<0)
			sv[sv.length]=0.0;
		return sv;

	}
	
	private Object[] getSignatureVector(Mapping mp)
	{
		int size=experiment.initialMatcher.getComponentMatchers().size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		AbstractMatcher a;
		Object[] ssv=new Object[size];
		for (int i=0;i<size;i++)
		{
			a = experiment.initialMatcher.getComponentMatchers().get(i);
			ssv[i]=a.getAlignment().getSimilarity(sourceNode, targetNode);
			
		}
		return ssv;
	}


}
