package am.va.graph;

class VARange{
	private int startIdx;
	private int endIdx;
	
	public VARange() {
		// TODO Auto-generated constructor stub
		startIdx = -1;
		endIdx = -1;
	}
	
	public VARange( int start, int end ) {
		// TODO Auto-generated constructor stub
		startIdx = start;
		endIdx = end;
	}
	
	public int getStartIdx() {
		return startIdx;
	}
	public void setStartIdx(int startIdx) {
		this.startIdx = startIdx;
	}
	public int getEndIdx() {
		return endIdx;
	}
	public void setEndIdx(int endIdx) {
		this.endIdx = endIdx;
	}
	
	/**
	 * if the range is valid
	 * @return
	 */
	public boolean isValid() {
		return (startIdx >= 0 && endIdx >= 0 && endIdx >= startIdx);
	}
	
	public String toString() {
		return new String("[" + startIdx + "~" + endIdx + "]");
	}
	
	
};