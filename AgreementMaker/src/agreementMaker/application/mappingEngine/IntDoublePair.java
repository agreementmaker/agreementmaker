package agreementMaker.application.mappingEngine;

public class IntDoublePair{
	public int index;
	public double value;
	
	public static int fake = -1;
	
	public IntDoublePair() {}
	
	public IntDoublePair(int a, double b) {
		index = a;
		value = b;
	}
	
	public boolean isFake() {
		return index == fake;
	}
}
