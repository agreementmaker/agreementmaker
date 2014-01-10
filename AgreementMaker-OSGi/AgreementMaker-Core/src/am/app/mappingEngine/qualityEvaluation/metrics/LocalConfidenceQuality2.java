package am.app.mappingEngine.qualityEvaluation.metrics;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.LocalQualityMetric;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.utility.parameters.AMParameter;
import am.utility.parameters.AMParameterSet;

public class LocalConfidenceQuality2 implements LocalQualityMetric{

	private AMParameterSet params = new AMParameterSet();
	
	private AbstractMatcher matcher;
	
	public LocalConfidenceQuality2(AbstractMatcher matcher) {
		this.matcher = matcher;
	}
	
	@Override
	public void setParameter(AMParameter param) {
		params.put(param);
	}

	@Override
	public void setParameters(AMParameterSet params) {
		this.params.putAll(params);
	}

	@Override
	public double getQuality(alignType type, int i, int j) {
		
		// determine the cardinality
		int numOfRelations = Math.max(matcher.getMaxSourceAlign(), matcher.getMaxTargetAlign());
		
		return 0d;
	}

	private static double[] evaluateMatrix(SimilarityMatrix matrix, boolean localForSource, int numRel, double th) {
		//double threshold = th;
		double threshold = 0.01;
		
		//if sourcerelations are less then targetrelation the matrix will be scanned for row or column.
		//for each node (row or column) the quality will be calculated with this formula:
		// avg(numRelations selected similarities of that nodes) - avg(similarities not choosen for that node) 
		
		double[] localMeasure;
		int numRelations = numRel; //avoiding to modify the input param
		
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
		int totalSelectable;
		double sumOfNonSelected;
		double sumOfSelected;
		int numberOfNonSelected;
		int numberOfSelected;

		Mapping[] maxValues;
		for(int i=0; i < localMeasure.length; i++) {
			double avgOfNonSelected = 0;
			double avgOfSelected = 0;
			double finalAvg = 0;
			
			//get the numRelations max values for this row or column
			if(localForSource) {
				maxValues = matrix.getRowMaxValues(i, numRelations);
				totalSum = matrix.getRowSum(i);			
				totalSelectable = matrix.getColumns(); 

			}
			else {
				maxValues = matrix.getColMaxValues(i, numRelations);
				totalSum = matrix.getRowSum(i);
				totalSelectable = matrix.getRows(); 
			}
			
			
			 //let's start with all of them not selected
			numberOfNonSelected = totalSelectable;
			sumOfNonSelected = totalSum;
			for(int j = 0; j < maxValues.length; j++) {
				if(maxValues[j].getSimilarity() >= threshold) {//this is a selected value, I'm not using the fact that maxValues is ordered because the order may change later so i don't want to risk
					sumOfNonSelected -= maxValues[j].getSimilarity();
					numberOfNonSelected--;
				}
			}
			numberOfSelected = totalSelectable - numberOfNonSelected;
			sumOfSelected = totalSum - sumOfNonSelected;
			
			if(numberOfNonSelected != 0)
				avgOfNonSelected = sumOfNonSelected /(double)numberOfNonSelected; //else is 0
			
			if(numberOfSelected != 0) {
				avgOfSelected =  sumOfSelected /(double)numberOfSelected; //else is 0
			}
			
			finalAvg = avgOfSelected - avgOfNonSelected;
			 //if i haven't selected anything the diffenrce would be negative
			if(finalAvg >= 0) {
				localMeasure[i] = finalAvg;
			}//else is 0
			
			//System.out.println("case i: totsum: "+totalSum+" sumSel: "+sumOfSelected+" sumNonSel: "+sumOfNonSelected+" numOfSel: "+numberOfSelected+" numOfNonSel: "+numberOfNonSelected+" avgSel: "+avgOfSelected+" avgNonSel: "+avgOfNonSelected+" final: "+localMeasure[i]);
		}
		
		return localMeasure;
	}
}
