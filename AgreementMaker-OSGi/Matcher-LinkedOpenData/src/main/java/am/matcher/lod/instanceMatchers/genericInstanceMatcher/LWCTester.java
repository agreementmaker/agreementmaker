package am.app.mappingEngine.instanceMatchers.genericInstanceMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import am.Utility;

public class LWCTester {
	
	static double[][] matrix1 = 
		{
			{1.0, 0.3, 0.5},
			{1.0, 0.2, 0.6},
			{1.0, 0.1, 0.3},
		};
	
	static double[][] matrix2 = 
		{
			{1.0, 0.3, 0.5},
			{0.1, 0.2, 0.6},
			{1.0, 0.1, 0.3},
		};
	
	
	public static List<List<Double>> fromDoubleMatrix(double[][] matrix){
		List<List<Double>> similaritiesList = new ArrayList<List<Double>>();
		List<Double> similarities;
		for (int i = 0; i < matrix.length; i++) {
			similarities = new ArrayList<Double>();
			for (int j = 0; j < matrix[i].length; j++) {
				similarities.add(matrix[i][j]);
			}
			similaritiesList.add(similarities);
		}
		return similaritiesList;
	}
	
	public static void analyzeSigmoid(){
		System.out.println("Sigmoid function");
		double[] x = {-1, -0.75, -0.5, -0.25, 0, 0.25, 0.5, 0.75, 1};		
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i] + "\t" + Utility.getSigmoidFunction(x[i]));
		}
		for (int i = 0; i < x.length; i++) {
			System.out.println(x[i] + "\t" + Utility.getModifiedSigmoidFunction(x[i]));
		}
	}
	
	private static double[][] normalizeMatrix(double[][] matrix){
		double[][] normalized = new double[matrix.length][matrix[0].length];
		double[] maximums = new double[matrix[0].length];
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if(matrix[i][j] > maximums[j])
					maximums[j] = matrix[i][j];
			}
		}
		//System.out.println(Arrays.toString(maximums));
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if(maximums[j] == 0) normalized[i][j] = 0;
				else normalized[i][j] = matrix[i][j] / maximums[j]; 
			}
		}
		return normalized;
	}

	private static String printMatrix(double[][] matrix){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				sb.append(matrix[i][j]).append("\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private static void analyzeMatrix(double[][] matrix) {
		List<List<Double>> similaritiesList = fromDoubleMatrix(matrix);
		//System.out.println(similaritiesList.toString().replaceAll("], ", "\n "));
		
		for (int i = 0; i < similaritiesList.size(); i++) {
			double[] weights = GenericInstanceMatcher.computeLwcWeights(similaritiesList, i, matrix1[0].length);
			System.out.println(Arrays.toString(weights));
			double sim = GenericInstanceMatcher.computeLinearWeightedCombination(similaritiesList.get(i), weights);
			System.out.println(sim);
		}
	}
	
	public static void testNormalizeSimilaritiesList(){
		List<List<Double>> similaritiesList = fromDoubleMatrix(matrix1);
		similaritiesList = GenericInstanceMatcher.normalizeSimilaritiesList(similaritiesList);
		System.out.println(similaritiesList);	
		
		double[][] norm1 = normalizeMatrix(matrix1);
		System.out.println(printMatrix(norm1));
	}
	
	public static void main(String[] args) {
		//analyzeMatrix(matrix1);
		//analyzeMatrix(matrix2);	
//		System.out.println(printMatrix(matrix1));
//		analyzeMatrix(matrix1);
//		
//		double[][] norm1 = normalizeMatrix(matrix1);
//		System.out.println(printMatrix(norm1));
//		analyzeMatrix(norm1);
				
		
//		double[][] norm2 = normalizeMatrix(matrix2);
//		System.out.println(printMatrix(norm2));
		
		testNormalizeSimilaritiesList();
		
	}
}
