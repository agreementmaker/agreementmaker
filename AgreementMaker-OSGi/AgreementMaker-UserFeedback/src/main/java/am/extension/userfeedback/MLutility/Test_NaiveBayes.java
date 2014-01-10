package am.extension.userfeedback.MLutility;
/*
 * This is a test class for the NaiveBayes class.
 * 
 * Francesco Loprete October 2013
 */
public class Test_NaiveBayes extends NaiveBayes{
	
	static Object[][] trainingTest1={{0.224535,0.124435,0.84532,1.0},{1.0,0.824435,0.44532,0.0},{0.224535,0.924435,0.84532,1.0}};
	static Object[][] trainingTest2={{"rainy", "cool", "normal", "TRUE", 0.0},
		{"rainy", "mild", "high", "TRUE", 0.0},{"sunny", "hot", "high", "FALSE", 0.0},
		{"sunny", "hot", "high", "TRUE", 0.0},{"sunny", "mild", "high", "FALSE", 0.0},
		{"overcast", "cool", "normal", "TRUE", 1.0},{"overcast", "hot", "high", "FALSE", 1.0},
		{"overcast", "hot", "normal", "FALSE", 1.0}, {"overcast", "mild", "high", "TRUE", 1.0},
		{"rainy", "cool", "normal", "FALSE", 1.0},{"rainy", "mild", "high", "FALSE", 1.0},
		{"rainy", "mild", "normal", "FALSE", 1.0},{"sunny", "cool", "normal", "FALSE", 1.0},
		{"sunny", "mild", "normal", "TRUE", 1.0}};
	static Object[][] trainingTest3={{"new","linux","fast",1.0},{"hollywood","celebrity","fast",0.0},{"fast","linux","quake",1.0},{"hollywood","occurs","quake",0.0}};
	static Object[][] dataTest1={{0.23535,0.13644,0.82213},
		{0.96644,0.84567,0.42222},
		{0.21244,0.547747,0.84566}};
	static Object[][] dataTest2={{"sunny", "cool", "high", "TRUE"}};
	static Object[][] dataTest3={{"linux","on","website"},{"hollywood","fashion","website"}};
	
	static Object[][] regressionTraining={
		{1076,2801,6,0,0,324500},{990,3067,5,1,1,466000},{1229,3094,5,0,1,425900},{731,4315,4,1,0,387120},
		{671,2926,4,0,1,312100},{1078,6094,6,1,1,603000},{909,2854,5,0,1,383400}
	};
	static Object[][] regressionData={{975,2947,5,1,1},{909,2854,5,0,1}};
	public Test_NaiveBayes(Object[][] trainingSet, Object[][] dataSet) {
		super(trainingSet, dataSet);
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) throws Exception 
	{
		WekaNaiveBayes wk=new  WekaNaiveBayes();
		wk.runNBayes(regressionTraining, regressionData);
	    Test_NaiveBayes tNB01=new Test_NaiveBayes(trainingTest1, dataTest1);
	    Test_NaiveBayes tNB02=new Test_NaiveBayes(trainingTest2, dataTest2);
	    Test_NaiveBayes tNB03=new Test_NaiveBayes(trainingTest3, dataTest3);
	    testPrecision(tNB01,1);
	    testPrecision(tNB02,1);
	    System.out.println(tNB02.interfaceComputeElement(dataTest2[0]));
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
	    testExpectationMaximization(tNB01,trainingTest1[0].length);
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
	
	private static void testExpectationMaximization(Test_NaiveBayes tnb, int length) throws Exception 
	{
		System.out.println("Test Expectation Maximization");
		tnb.nBayes_eMaximization(length);
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
