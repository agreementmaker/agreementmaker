package am.matcher.lod.instanceMatchers.combination;

import java.util.List;

/**
 * A linear weighted combination combination function. If no weights are passed
 * to the constructor, the combination function becomes the average.
 * 
 * @author Federico Caimi
 * 
 */
public class LinearCombination extends CombinationFunction{
	List<Double> weights;	
	
	public LinearCombination(){
		super(Type.LOCAL);
	}
	
	public LinearCombination(List<Double> weights){
		super(Type.LOCAL);
		this.weights = weights;
	}
	
	@Override
	public double combine(List<Double> similarities) {
		if(weights == null){
			// just compute the average
			double sum = 0;
			for (Double sim : similarities) {
				sum += sim;
			}
			return sum / similarities.size(); 
		}
		if(weights.size() != similarities.size()){
			System.err.println("Error: the combination weights are not in the same number as matchers");
			return 0;
		}
		double sum = 0;
		double weightsSum = 0;
		for (int i = 0; i < similarities.size(); i++) {
			sum += similarities.get(i) * weights.get(i);
			weightsSum += weights.get(i);
		}
		return sum / weightsSum;
	}
	
}
