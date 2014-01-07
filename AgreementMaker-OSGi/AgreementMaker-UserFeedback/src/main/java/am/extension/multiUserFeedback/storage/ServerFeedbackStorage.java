package am.extension.multiUserFeedback.storage;


import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;

public class ServerFeedbackStorage extends MUFeedbackStorage<MUExperiment>{
	MUExperiment experiment;
	@Override
	public void addFeedback(MUExperiment exp, Mapping candidateMapping,
			String id) {
		this.experiment=exp;
		// TODO Auto-generated method stub
		SparseMatrix sparse;//=exp.getUflStorageClass();
		int row=candidateMapping.getSourceKey();
		int col=candidateMapping.getTargetKey();
		double sim=0.0;
		if(candidateMapping.getAlignmentType()==alignType.aligningClasses)
		{
			sparse=exp.getUflStorageClass();
			sim=sparse.getSimilarity(row, col);
			if (exp.feedback.equals("CORRECT"))
				sim++;
			if (exp.feedback.equals("UNCORRECT"))
				sim--;
			sparse.setSimilarity(row, col, sim);
			exp.setUflStorageClass(sparse);
		}
		else if(candidateMapping.getAlignmentType()==alignType.aligningProperties)
		{
			sparse=exp.getUflStorageProperty();
			sim=sparse.getSimilarity(row, col);
			if (exp.feedback.equals("CORRECT"))
				sim++;
			if (exp.feedback.equals("UNCORRECT"))
				sim--;
			sparse.setSimilarity(row, col, sim);
			exp.setUflStorageProperty(sparse);
		}
		
		getTrainingSet();
		
		done();
	}

	@Override
	public void getTrainingSet() {
		List<Object[]> class_trn=new ArrayList<Object[]>();
		List<Object[]> prop_trn=new ArrayList<Object[]>();
		SparseMatrix sparse=experiment.getUflStorageClass();
		try {
			for(Mapping mp : sparse.toList())
			{
				if (mp.getSimilarity()!=0.0)
				{
					class_trn.add(getLabeledMapping(mp,mp.getSimilarity()));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Object[][] cl_training=new Object[class_trn.size()][experiment.initialMatcher.getComponentMatchers().size()+1];
		class_trn.toArray(cl_training);
		experiment.setTrainingSet_classes(cl_training);
		
		sparse=experiment.getUflStorageProperty();
		try {
			for(Mapping mp : sparse.toList())
			{
				if (mp.getSimilarity()!=0.0)
				{
					prop_trn.add(getLabeledMapping(mp,mp.getSimilarity()));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
