package agreementMaker.development;

public class ReferenceEvaluation {
	
	//Constants
	/**Formats for reference files*/
	public final static String REF1 = "OAEI-N3";
	public final static String REF2 = "MADISON-DANE-txt";
	
	/**Formats for output files*/
	public final static String OUTF1 = "TXT-1";
	
	//Init variables
	/**Name of the reference file to compare the algorithm with.*/
	private String refFileName; 
	/**Format of the reference file to compare the algorithm with.*/
	private String refFormat;
	/**Name of the output file to store the result of the evaluation*/
	private String outFileName;
	/**Format of the output file*/
	private String outFormat;
	


	public ReferenceEvaluation(String refN,String refF, String outN, String outF) {
		refFileName = refN;
		refFormat = refF;
		outFileName = outN;
		outFormat = outF;
		System.out.println("refFileName: "+refFileName);
		System.out.println("refFormat: "+refFormat);
		System.out.println("outFileName: "+outFileName);
		System.out.println("outFormat: "+outFormat);
	}
}
