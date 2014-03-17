package am.app.mappingEngine.persistance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
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
	
	public static MatcherResult loadMatcherResult(String file) {
		if( file == null ) return null;
		
		if( !file.startsWith(File.separator) ) {
			file = Core.getInstance().getRoot() + file;
		}
		
		File inputFile = new File(file);
		
		if( !inputFile.exists() ) return null;
		
		try {
			FileInputStream fileIn = new FileInputStream(inputFile);
			BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(fileIn);
			ObjectInputStream in = new ObjectInputStream(bzIn);
			
			MatcherResult res = (MatcherResult) in.readObject();
			
			in.close();
			bzIn.close();
			fileIn.close();
			
			LOG.info("Deserialized matcher result from: " + inputFile);
			return res;
		} 
		catch (IOException | ClassNotFoundException e) {
			LOG.error(e);
			return null;
		}
	}
	
}
