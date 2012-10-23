package test.instanceHiding;

public class testHiding {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		A a = new A();
		A ab = new B();
		B b = new B();
		System.out.println(a);
		System.out.println(ab);
		System.out.println(b);
		System.out.println(b.who);
	}
	
}
