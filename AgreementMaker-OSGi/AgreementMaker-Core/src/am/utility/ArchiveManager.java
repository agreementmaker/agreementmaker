package am.utility;

import java.io.File;

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
}
