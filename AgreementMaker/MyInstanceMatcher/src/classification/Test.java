package classification;

public class Test {
	private double score1;
	private double score2; 
	private double score3;
	private String className;
	
	public Test(double score1, double score2, double score3) {
		this.score1 = score1;
		this.score2 = score2;
		this.score3 = score3;
		this.className = "";
	}
	
	public Test(double score1, double score2, double score3, String className) {
		this.score1 = score1;
		this.score2 = score2;
		this.score3 = score3;
		this.className = className;
	}

	public double getScore1() {
		return score1;
	}

	public void setScore1(double score1) {
		this.score1 = score1;
	}

	public double getScore2() {
		return score2;
	}

	public void setScore2(double score2) {
		this.score2 = score2;
	}

	public double getScore3() {
		return score3;
	}

	public void setScore3(double score3) {
		this.score3 = score3;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public String toString() {
		return "Test [score1=" + score1 + ", score2=" + score2 + ", score3="
				+ score3 + ", className=" + className + "]";
	}
	
	public double[] toArray(){
		double [] d = {this.score1,this.score2,this.score3};
		return d;
		
	}
	
	

}
