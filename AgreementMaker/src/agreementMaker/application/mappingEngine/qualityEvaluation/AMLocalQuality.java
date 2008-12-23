package agreementMaker.application.mappingEngine.qualityEvaluation;

import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.Alignment;
import agreementMaker.application.mappingEngine.AlignmentMatrix;

public class AMLocalQuality {
	
	protected static QualityEvaluationData getQuality(AbstractMatcher matcher) {
		QualityEvaluationData q = new QualityEvaluationData();
		q.setLocal(true);
		
		int maxSourceRelations = matcher.getMaxSourceAlign();
		int maxTargetRelations = matcher.getMaxTargetAlign();
		int numOfRelations = maxSourceRelations;
		q.setLocalForSource(true);
		if( maxSourceRelations > maxTargetRelations) {
			q.setLocalForSource(false);
			numOfRelations = maxTargetRelations;
		}
		
		if(matcher.areClassesAligned()) {
			double[] measures = evaluateMatrix(matcher.getClassesMatrix(), q.isLocalForSource(),numOfRelations, matcher.getThreshold() );
			q.setLocalClassMeasures(measures);
		}
		if(matcher.arePropertiesAligned()) {
			double[] measures = evaluateMatrix(matcher.getPropertiesMatrix(), q.isLocalForSource(),numOfRelations,matcher.getThreshold());
			q.setLocalPropMeasures(measures);
		}
		
		return q;
	}
	
	private static double[] evaluateMatrix(AlignmentMatrix matrix, boolean localForSource, int numRelations, double threshold) {
		//if sourcerelations are less then targetrelation the matrix will be scanned for row or column.
		//for each node (row or column) the quality will be calculated with this formula:
		// avg(numRelations selected similarities of that nodes) - avg(similarities not choosen for that node) 
		double[] localMeasure;
		
		//Iocal for source, means that we will have a quality value for each source node
		//local for target means that we will have a quality value for each target node
		if(localForSource) { 
			localMeasure = new double[matrix.getRows()];
			if(numRelations > matrix.getColumns())
				numRelations = matrix.getColumns(); //each source can be aligned at most with each column
		}
		else {
			localMeasure = new double[matrix.getColumns()];
			if(numRelations > matrix.getRows())
				numRelations = matrix.getRows(); //each target can be aligned at most with each row
		}

		
		double totalSum;
		double sumOfNonSelected;
		double sumOfSelected;
		int numberOfNonSelected;
		int numberOfSelected;
		double avgOfNonSelected = 0;
		double avgOfSelected = 0;
		Alignment[] maxValues;
		for(int i=0; i < localMeasure.length; i++) {
			
			//get the numRelations max values for this row or column
			if(localForSource) {
				maxValues = matrix.getRowMaxValues(i, numRelations);
				totalSum = matrix.getRowSum(i);
			}
			else {
				maxValues = matrix.getColMaxValues(i, numRelations);
				totalSum = matrix.getColSum(i);
			}
			
			 //let's start with all of them not selected
			sumOfNonSelected = totalSum;
			numberOfNonSelected = localMeasure.length; 
			for(int j = 0; j < maxValues.length; j++) {
				if(maxValues[j].getSimilarity() >= threshold) {//this is a selected value
					sumOfNonSelected -= maxValues[j].getSimilarity();
					numberOfNonSelected--;
				}
			}
			numberOfSelected = localMeasure.length - numberOfNonSelected;
			sumOfSelected = totalSum - sumOfNonSelected;
			
			if(numberOfNonSelected != 0)
				avgOfNonSelected = sumOfNonSelected /(double)numberOfNonSelected; //else is 0
			
			if(numberOfSelected != 0) {
				avgOfSelected =  sumOfSelected /(double)numberOfSelected; //else is 0
			}
			
			localMeasure[i] = avgOfSelected - avgOfNonSelected;
			
		}
		
		return localMeasure;
	}

}
