package am.app.geo;

import java.io.File;

public interface IFileToDatabaseConverter {
	
	// GLOBAL OPERATIONS
	public void runAll(File inputFile);
	
	public void setUp();
	public void tearDown();
	public void runConversion(File inputFile);
	public void readFile(File inputFile);
	public void processLine(String inputLine, String delimiter);

}
