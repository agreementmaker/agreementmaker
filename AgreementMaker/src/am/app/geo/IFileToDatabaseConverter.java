package am.app.geo;

import java.io.File;

public interface IFileToDatabaseConverter {
	
	// GLOBAL OPERATIONS
	public void setUp();
	public void tearDown();
	public void runConversion();

	// FILE OPERATIONS
	public void readFile(File inputFile);
	public void processLine(String inputLine, String delimiter);

}
