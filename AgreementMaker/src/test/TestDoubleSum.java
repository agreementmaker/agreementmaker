package test;

public class TestDoubleSum {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double step = 0.05;
		System.out.println(step);
		double sum = 0.35;
		System.out.println(sum);
		sum += step;
		System.out.println(sum);
		System.out.println("inizio for");
		for(double sum2 = step; sum2 <= 1; sum2+= step) {
			System.out.println(sum2);
		}
		

	}

}
