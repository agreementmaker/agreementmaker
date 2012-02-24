package am.app.mappingEngine.instanceMatchers.combination;

import java.util.List;

public abstract class CombinationFunction {
	enum Type {LOCAL, GLOBAL};
	Type type;
	
	public CombinationFunction(Type type){
		this.type = type;
	}
	
	public abstract double combine(List<Double> similarities);
		
}
