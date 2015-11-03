package am.utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * TODO:
 * Will try to find an ontology in an archive.  If it finds the ontology in the archive,
 * it will automatically extract the archive.
 * 
 * @author cosmin
 */
public class ArchiveManager {
	
	public void checkOntologyFile(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new RuntimeException("File " + fileName + " does not exist.  You need to extract the ontologies from the archive.");
		}
	}

	public void decodeFile(String inputFile) {
		File inFile = new File(inputFile);
		File outFile = null;

		BufferedInputStream inStream = null;
		BufferedOutputStream outStream = null;
		try {
			inStream = new BufferedInputStream(new FileInputStream(inFile));
			
			outFile = File.createTempFile("archiveManager", "out");
			outFile.deleteOnExit();
			outStream = new BufferedOutputStream(new FileOutputStream(outFile));
			
			boolean eos = false;
			// if (params.Eos)
			// eos = true;

			// read properties
			int propertiesSize = 5;
			byte[] properties = new byte[propertiesSize];
			if (inStream.read(properties, 0, propertiesSize) != propertiesSize) {
				throw new Exception("input .lzma file isb too short");
			}

			//SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
			//if (!decoder.SetDecoderProperties(properties)) {
			//	throw new Exception("Incorrect stream properties");
			//}

			long outSize = 0;
			for (int i = 0; i < 8; i++) {
				int v = inStream.read();
				if (v < 0) {
					throw new Exception("Can't read stream size");
				}
				outSize |= ((long) v) << (8 * i);
			}

			//if (!decoder.Code(inStream, outStream, outSize)) {
			//	throw new Exception("Error in data stream");
			//}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outStream != null) {
				try {
					outStream.flush();
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
