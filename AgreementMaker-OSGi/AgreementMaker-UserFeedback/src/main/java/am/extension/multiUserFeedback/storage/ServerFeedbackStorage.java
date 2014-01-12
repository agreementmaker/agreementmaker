package am.extension.multiUserFeedback.storage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;

public class ServerFeedbackStorage extends FeedbackAgregation<MUExperiment>{
	MUExperiment experiment;
	Mapping lastAdded;
	double correctionLabel=0;
	Boolean flag=false;
	
	public ServerFeedbackStorage(){}
	
	
	@Override
	public void addFeedback(MUExperiment exp) {
		this.experiment=exp;
		Mapping candidateMapping=exp.selectedMapping;
		
		int mappingRow = candidateMapping.getSourceKey();
		int mappingCol = candidateMapping.getTargetKey();
		
		Validation userFeedback = experiment.userFeedback.getUserFeedback();
		
		if(candidateMapping.getAlignmentType() == alignType.aligningClasses)
		{
			if (flag)
			{
				correctionLabel=labelizeSingleTS(exp.getUflStorageClassPos().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()),exp.getUflStorageClass_neg().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()));
			}
			
			if (userFeedback.equals(Validation.CORRECT))
			{
				SparseMatrix m = exp.getUflStorageClassPos();
				int count = (int) m.getSimilarity(mappingRow, mappingCol);
				count++;
				m.setSimilarity(mappingRow, mappingCol, (double)count);
				//exp.setUflStorageClassPos(m);
			}
			if (userFeedback.equals(Validation.INCORRECT))
			{
				SparseMatrix m = exp.getUflStorageClass_neg();
				int count = (int) m.getSimilarity(mappingRow, mappingCol);
				count++;
				m.setSimilarity(mappingRow, mappingCol, (double)count);
				//exp.setUflStorageClass_neg(m);
			}
			
		}
		else if(candidateMapping.getAlignmentType() == alignType.aligningProperties)
		{
			if (flag)
			{
				correctionLabel=labelizeSingleTS(exp.getUflStoragePropertyPos().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()),exp.getUflStorageProperty_neg().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()));
			}
			
			if (userFeedback.equals(Validation.CORRECT))
			{
				SparseMatrix m = exp.getUflStoragePropertyPos();
				int count = (int) m.getSimilarity(mappingRow, mappingCol);
				count++;
				m.setSimilarity(mappingRow, mappingCol, (double)count);
				//exp.setUflStoragePropertyPos(m);
			}
			if (userFeedback.equals(Validation.INCORRECT))
			{
				SparseMatrix m = exp.getUflStorageProperty_neg();
				int count = (int) m.getSimilarity(mappingRow, mappingCol);
				count++;
				m.setSimilarity(mappingRow, mappingCol, (double)count);
				//exp.setUflStorageProperty_neg(m);
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
		int x=experiment.userFeedback.equals("CORRECT")?1:-1;
		int sum=(int)pos-(int)neg+x;
		if (sum>0) return 1;
		if (sum<0) return 0;
		if (x>0) return 1;
		return 0;
		
	}

	/**
	 * FIXME: Move the duplicated code into its own private method.
	 */
	@Override
	public void getTrainingSet() {
		List<Object[]> class_trn = new ArrayList<Object[]>();
		SparseMatrix sparsePos = experiment.getUflStorageClassPos();
		SparseMatrix sparseNeg = experiment.getUflStorageClass_neg();
		for(int i=0;i<sparsePos.getRows();i++)
		{
			for(int j=0;j<sparsePos.getColumns();j++)
			{
				int sum = (int)sparsePos.getSimilarity(i, j) - (int)sparseNeg.getSimilarity(i, j);
				if (sum != 0) {
					Mapping m = sparsePos.get(i, j) == null ? sparseNeg.get(i, j) : sparsePos.get(i, j);
					class_trn.add(getLabeledMapping(m,sum));
				}
			}
		}

		Object[][] cl_training=new Object[class_trn.size()][experiment.initialMatcher.getComponentMatchers().size()+1];
		class_trn.toArray(cl_training);
		experiment.setTrainingSet_classes(cl_training);
		
		List<Object[]> prop_trn = new ArrayList<Object[]>();
		sparsePos=experiment.getUflStoragePropertyPos();
		sparseNeg=experiment.getUflStorageProperty_neg();
		for(int i=0;i<sparsePos.getRows();i++)
		{
			for(int j=0;j<sparsePos.getColumns();j++)
			{
				int sum = (int)sparsePos.getSimilarity(i, j) - (int)sparseNeg.getSimilarity(i, j);
				if (sum!=0) {
					Mapping m = sparsePos.get(i, j) == null ? sparseNeg.get(i, j) : sparsePos.get(i, j);
					prop_trn.add(getLabeledMapping(m, sum));
				}
			}
		}
		
		Object[][] pr_training=new Object[prop_trn.size()][experiment.initialMatcher.getComponentMatchers().size()+1];
		prop_trn.toArray(pr_training);
		experiment.setTrainingSet_property(pr_training);

	}
	
	/**
	 * @param diff
	 *            The difference between the number of positive and negative
	 *            validations.
	 */
	private Object[] getLabeledMapping(Mapping mp, double diff)
	{
		if( diff == 0d ) {
			throw new RuntimeException("The difference cannot be zero.");
		}
		
		Object[] sv = getSignatureVector(mp);
		
		Object[] trainingElement = Arrays.copyOf(sv, sv.length+1);
		if (diff > 0)
			trainingElement[trainingElement.length-1] = 1.0;
		if (diff < 0)
			trainingElement[trainingElement.length-1] = 0.0;
		
		return trainingElement;

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
