package am.matcher.lod.instanceMatchers.combination;

import java.util.List;


/**
 * A combination function takes the similarities computed by many matchers and
 * combines them into a final similarity. The combination can be a Linear
 * Weighted Combination with manually or automatically set weights, or a
 * learning algorithm which learns the weights, or some other approach.
 * 
 * @author Federico Caimi
 */
public abstract class CombinationFunction {
	
	enum Type { LOCAL, GLOBAL };
	
	protected final Type type;
	
	public CombinationFunction(Type type){
		this.type = type;
	}
	
	public abstract double combine(List<Double> similarities);
		
}
