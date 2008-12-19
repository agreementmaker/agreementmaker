package agreementMaker.application.mappingEngine;

/**This class is used by stable marriage algorithm to perform N-N selection, can be used also by any other class anyway*/
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
		return index == fake || value== fake;
	}

	public boolean equals(Object o) {
		if(o instanceof IntDoublePair) {
			IntDoublePair  i = (IntDoublePair)o;
			return i.index == index && i.value == value;
		}
		return false;
	}
	
	public static IntDoublePair createFakePair() {
		return new IntDoublePair(fake, fake);
	}

}
