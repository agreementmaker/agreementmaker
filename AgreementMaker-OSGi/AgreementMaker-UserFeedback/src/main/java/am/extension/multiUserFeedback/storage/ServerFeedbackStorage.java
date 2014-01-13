package am.extension.multiUserFeedback.storage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.extension.multiUserFeedback.experiment.MUExperiment;
import am.extension.userfeedback.UserFeedback.Validation;

public class ServerFeedbackStorage extends FeedbackAgregation<MUExperiment>{
	
	private MUExperiment experiment;
	
	//Boolean flag=false;
	
	@Override
	public void addFeedback(MUExperiment exp) {
		this.experiment=exp;
		Mapping candidateMapping = exp.selectedMapping;
		
		int mappingRow = candidateMapping.getSourceKey();
		int mappingCol = candidateMapping.getTargetKey();
		
		final Validation userFeedback = experiment.userFeedback.getUserFeedback();

		/*double correctionLabel = 0;
		if (flag)
		{
			correctionLabel = labelizeSingleTS(exp.getUflStorageClassPos().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()),exp.getUflStorageClass_neg().getSimilarity(candidateMapping.getSourceKey(), candidateMapping.getTargetKey()));
		}*/
		
		// update the validation matrices
		SparseMatrix validationMatrix = exp.getFeedbackMatrix(candidateMapping.getAlignmentType(), userFeedback);
		int count = (int) validationMatrix.getSimilarity(mappingRow, mappingCol);
		count++;
		validationMatrix.setSimilarity(mappingRow, mappingCol, (double)count);
		
		// update the forbidden positions
		updateForbiddenPositions(candidateMapping, userFeedback);
		
		// print out the forbidden positions
		printForbiddenPositions(alignType.aligningClasses);
		printForbiddenPositions(alignType.aligningProperties);
		
		experiment.setTrainingSet_classes(getTrainingSet(alignType.aligningClasses));
		experiment.setTrainingSet_property(getTrainingSet(alignType.aligningProperties));
		
		done();
	}
	
	/*public void getSingleTrainingSet()
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
		
	}*/

	@Override
	public Object[][] getTrainingSet(alignType type) {
		List<Object[]> trainingSet = new ArrayList<Object[]>();
		SparseMatrix positiveFeedback = experiment.getFeedbackMatrix(type, Validation.CORRECT);
		SparseMatrix negativeFeedback = experiment.getFeedbackMatrix(type, Validation.INCORRECT);
		
		for(int i=0;i < positiveFeedback.getRows();i++)
		{
			for(int j=0;j < positiveFeedback.getColumns();j++)
			{
				int sum = (int)positiveFeedback.getSimilarity(i, j) - (int)negativeFeedback.getSimilarity(i, j);
				if (sum != 0) {
					Mapping m = positiveFeedback.get(i, j) == null ? negativeFeedback.get(i, j) : positiveFeedback.get(i, j);
					trainingSet.add(getLabeledMapping(m,sum));
				}
			}
		}

		return trainingSet.toArray(new Object[0][0]);
	}
	
	/**
	 * Update the forbidden position matrix.  We will unforbid mappings that are ambiguous.
	 */
	private void updateForbiddenPositions(Mapping candidateMapping, Validation userFeedback) {
		SimilarityMatrix positiveFeedback = experiment.getFeedbackMatrix(candidateMapping.getAlignmentType(), Validation.CORRECT);
		SimilarityMatrix negativeFeedback = experiment.getFeedbackMatrix(candidateMapping.getAlignmentType(), Validation.INCORRECT);
		
		int i = candidateMapping.getSourceKey();
		int j = candidateMapping.getTargetKey();
		
		int diff = (int)positiveFeedback.getSimilarity(i, j) - (int)negativeFeedback.getSimilarity(i, j);
		
		SparseMatrix forbiddenPositionsMatrix = experiment.getForbiddenPositions(candidateMapping.getAlignmentType());
		if( diff == 0 ) {
			forbiddenPositionsMatrix.setSimilarity(i, j, 0.0);
		}
		else {
			forbiddenPositionsMatrix.setSimilarity(i, j, 1.0);
		}
		
		// update the computed matrix with the appropriate similarity
		SimilarityMatrix uflMatrix = experiment.getComputedUFLMatrix(candidateMapping.getAlignmentType());
		if( diff > 0 ) {
			uflMatrix.setSimilarity(i, j, 1.0d);
		}
		else if( diff < 0 ){
			uflMatrix.setSimilarity(i, j, 0.0d);
		}
		
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
		int numMatchers = experiment.initialMatcher.getComponentMatchers().size();
		Node sourceNode=mp.getEntity1();
		Node targetNode=mp.getEntity2();
		AbstractMatcher a;
		Object[] signatureVector = new Object[numMatchers];
		for (int i=0; i < numMatchers;i++)
		{
			a = experiment.initialMatcher.getComponentMatchers().get(i);
			signatureVector[i] = a.getAlignment().getSimilarity(sourceNode, targetNode);
		}
		return signatureVector;
	}

	private void printForbiddenPositions(alignType type) {
		SimilarityMatrix m = experiment.getForbiddenPositions(type);
		
		List<Mapping> forbiddenMappings = new LinkedList<>();
		
		for( int i = 0; i < m.getRows(); i++ ) {
			for( int j = 0; j < m.getColumns(); j++ ) {
				if( m.getSimilarity(i, j) != 0d ) {
					forbiddenMappings.add(m.get(i, j));
				}
			}
		}
		
		Collections.sort(forbiddenMappings, new Comparator<Mapping>() {
			@Override
			public int compare(Mapping o1, Mapping o2) {
				if( o1.getSourceKey() == o2.getSourceKey() ) 
					return Integer.compare(o1.getTargetKey(), o2.getTargetKey());
				else
					return Integer.compare(o1.getSourceKey(), o2.getSourceKey());
			}
		});
		
		
		experiment.info(
				"Forbidden " + 
				(type == alignType.aligningClasses ? "classes" : "properties") + ": " + forbiddenMappings.size() + " mappings.");
		
		for(Mapping fm : forbiddenMappings) {
			experiment.info(fm.toString());
		}
	}

}
