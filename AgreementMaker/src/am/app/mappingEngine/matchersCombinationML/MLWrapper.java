package am.app.mappingEngine.matchersCombinationML;

/**
 * Wrapper class that calls the entier Machine learning process of matching
 */
import java.io.IOException;

public class MLWrapper {
	
	public static void main(String args[])throws IOException
	{
		MLTrainerWrapper trainer=new MLTrainerWrapper();
		MLTestingWrapper tester=new MLTestingWrapper();
		try {
			trainer.callProcess();
			tester.callProcess();
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		
	}
}
