package am.matcher.referenceAlignment;

import java.util.ArrayList;
import java.util.List;

import am.app.mappingEngine.ReferenceEvaluationData;

import com.ibm.icu.text.DecimalFormat;

public class ThresholdAnalysisData {
	private double[] thresholds;
	private List<ReferenceEvaluationData> evaluationData;
	double bestRun;
	String matcherName; 
	String report;
	
	DecimalFormat format = new DecimalFormat("0.000");
	
	public enum Combination { AVG };
	
	public ThresholdAnalysisData(double[] thresholds){
		this.thresholds = thresholds;
		evaluationData = new ArrayList<ReferenceEvaluationData>();
	}
	
	public double getBestRun() {
		return bestRun;
	}
	
	public void setBestRun(double bestRun) {
		this.bestRun = bestRun;
	}
	
	public String getMatcherName() {
		return matcherName;
	}
	
	public void setMatcherName(String matcherName) {
		this.matcherName = matcherName;
	}
	
	public void addEvaluationData(ReferenceEvaluationData data){
		evaluationData.add(data);
	}
		
	public String getReport() {
		return report;
	}
	
	public void setReport(String report) {
		this.report = report;
	}
	
	public static double getBestOverallRun(List<ThresholdAnalysisData> data){
		return getBestOverallRun(data, Combination.AVG);
	}
	
	public double[] getThresholds() {
		return thresholds;
	}
	
	public ReferenceEvaluationData getEvaluationData(int index) {
		return evaluationData.get(index);
	}
	
	/**
	 * We assume that the ThresholdAnalysisData in input have all the same range of thresholds.
	 * 
	 * @param data
	 * @param combination
	 * @return
	 */
	public static double getBestOverallRun(List<ThresholdAnalysisData> data, Combination combination){
		
		if(data.size() < 1) return -1;		
		
		double[] thresholds = data.get(0).getThresholds();
		double[] fMeasures = new double[thresholds.length];
		
		double[] precisions = new double[thresholds.length];
		double[] recalls = new double[thresholds.length];
			
		for (int i = 0; i < thresholds.length; i++) {
			for (int j = 0; j < data.size(); j++) {
				precisions[i] += data.get(j).getEvaluationData(i).getPrecision() / data.size();
				recalls[i] += data.get(j).getEvaluationData(i).getRecall() / data.size();
				//fMeasures[i] += data.get(j).getEvaluationData(i).getFmeasure();
			}	
		}
				
		for (int i = 0; i < thresholds.length; i++) {
			fMeasures[i] = 2 * precisions[i] * recalls[i] / (precisions[i] + recalls[i]);
		}
		
		double max = 0;
		double th = 0;
		for (int i = 0; i < fMeasures.length; i++) {
			if(fMeasures[i] > max){
				max = fMeasures[i];
				th = thresholds[i];
			}
		}
		return th;
	}
}
