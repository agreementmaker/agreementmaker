package am.app.mappingEngine.persistance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import am.app.Core;
import am.app.mappingEngine.MatcherResult;

public class PersistanceUtility {

	private static final Logger LOG = LogManager.getLogger(PersistanceUtility.class);
	
	public static void saveMatcherResult(MatcherResult result, String file) {
		if( !file.startsWith(File.separator) ) {
			file = Core.getInstance().getRoot() + file;
		}
		
		File outputFile = new File(file);
		
		try {			
			FileOutputStream fileOut = new FileOutputStream(outputFile);
			BZip2CompressorOutputStream bzOut = new BZip2CompressorOutputStream(fileOut); // bzip the serialized object to save space
			ObjectOutputStream out = new ObjectOutputStream(bzOut);
			
			out.writeObject(result);
			
			out.close();
			bzOut.close();
			fileOut.close();
			
			LOG.debug("Serialized matcher result to: " + outputFile);
		} catch (IOException e) {
			LOG.error(e);
		}
		
	}
	
}
