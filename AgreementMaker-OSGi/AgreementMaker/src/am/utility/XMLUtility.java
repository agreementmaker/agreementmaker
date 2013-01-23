package am.utility;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class XMLUtility {

	/**
	 * @param args
	 */
	private final static String QUOTE_BEGIN_TAG = "QUOTE";
	private final static String QUOTE_END_TAG = "</QUOTE>";
	public static void main(String[] args) {

		File rootDirectory = new File("/home/iman/Desktop/SingleDir");
		File outputDirectory = new File("/home/iman/Desktop/Chunks");
		addEndQuoteTagtoFileInDirectory(rootDirectory, outputDirectory);
	}

	private static String addEndQuoteTag(String xmlText){

		StringBuilder fixedXmlText = new StringBuilder();

		String[] splittedText = xmlText.split("<");
		for (int i = 0; i < splittedText.length; i++) {
			if (splittedText[i].length() ==0) continue;
			if (splittedText[i].startsWith(QUOTE_BEGIN_TAG)){

				fixedXmlText.append("<"+splittedText[i]+QUOTE_END_TAG);
			}
			else{
				fixedXmlText.append("<"+splittedText[i]);
			}
		}
		return fixedXmlText.toString();
	}

	public static void addEndQuoteTagtoFile(File inputFile, File outputFile){
		try {
			String fixedFileString = addEndQuoteTag(FileUtils.readFileToString(inputFile));
			FileUtils.write(outputFile, fixedFileString);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void addEndQuoteTagtoFileInDirectory(File rootDirectory, File outputDirectory){
		Collection<File> inputFileList = FileUtils.listFiles(rootDirectory, null, false);
		
		final int numChunks = 12;
		
		int counter = 1;
		for (File inputFile : inputFileList) {
			int chunk = counter % numChunks;
			String chunkDir = outputDirectory + File.separator + "docs" + chunk;
			File outputFile = new File(chunkDir, inputFile.getName());
			System.out.println(counter + " " + outputFile);
			addEndQuoteTagtoFile(inputFile, outputFile);
			
			counter++;
		}

	}

}
