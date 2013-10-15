package am.extension.userfeedback;
/*
 * This is a test class for the NaiveBayes class.
 * 
 * Francesco Loprete October 2013
 */
public class Test_NaiveBayes extends NaiveBayes{
	
	static Object[][] trainingTest1={{0.224535,0.124435,0.84532,1.0},{1.0,0.824435,0.44532,0},{0.224535,0.924435,0.84532,1.0}};
	static Object[][] trainingTest2={{"rainy", "cool", "normal", "TRUE", "no"},
		{"rainy", "mild", "high", "TRUE", "no"},{"sunny", "hot", "high", "FALSE", "no"},
		{"sunny", "hot", "high", "TRUE", "no"},{"sunny", "mild", "high", "FALSE", "no"},
		{"overcast", "cool", "normal", "TRUE", "yes"},{"overcast", "hot", "high", "FALSE", "yes"},
		{"overcast", "hot", "normal", "FALSE", "yes"}, {"overcast", "mild", "high", "TRUE", "yes"},
		{"rainy", "cool", "normal", "FALSE", "yes"},{"rainy", "mild", "high", "FALSE", "yes"},
		{"rainy", "mild", "normal", "FALSE", "yes"},{"sunny", "cool", "normal", "FALSE", "yes"},
		{"sunny", "mild", "normal", "TRUE", "yes"}};
	static Object[][] dataTest1={{0.23535,0.13644,0.82213},
		{0.96644,0.84567,0.42222},
		{0.21244,0.547747,0.84566}};
	static Object[][] dataTest2={{"sunny", "cool", "high", "TRUE"}};

	public Test_NaiveBayes(Object[][] trainingSet, Object[][] dataSet) {
		super(trainingSet, dataSet);
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws Exception 
	{
	    Test_NaiveBayes tNB01=new Test_NaiveBayes(trainingTest1, dataTest1);
	    Test_NaiveBayes tNB02=new Test_NaiveBayes(trainingTest2, dataTest2);
	    testPrecision(tNB01,1);
	    testPrecision(tNB02,1);
	    tNB01.run();
	    tNB02.run();
	    testLabel(tNB01, true, 0);
	    testLabel(tNB01, false, 1);
	    try{
	    	testLabel(tNB01, true, 2); //should fail
	    }
	    catch (Exception e)
	    {
	    	System.out.println("Test Fail");
	    	System.out.println("Correct label: "+tNB01.getLabel(2));
	    }
	    testLabel(tNB02, false, 0);
	}
	/* test the precision used for the training set. The precision range [1,infinite]
	* The precision is the log10 of the number of elements in the trainingSet
	*/
	public static void testPrecision(Test_NaiveBayes tnb, int value) throws Exception
	{
		System.out.println("Test precision");
		assertEquals(value,tnb.getPrecision(),"Precision");
	}
	
	private static void assertEquals(Object a, Object b,String m) throws Exception {
		if (a.equals(b)) System.out.println("Test OK");
		else
			throw new Exception("Error "+m);
		
	}
	
	/*
	 * Test if the label created by the NB algorithm is the same we expected
	 */
	public static void testLabel(Test_NaiveBayes tnb, Boolean s, int index) throws Exception 
	{
		System.out.println("Test Label");
		assertEquals(s,tnb.getLabel(index),"Label");
	}
	
}
