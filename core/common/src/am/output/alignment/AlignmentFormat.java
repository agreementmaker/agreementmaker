package am.output.alignment;

import java.io.File;
import java.io.Reader;
import java.io.Writer;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;

/**
 * This interface should be implemented by classes that 
 * implement reading and writing of an alignment format.
 * 
 * NOTE: The writeAlignmentToFile and readAlignmentFromFile are just
 *       wrapper functions for the writeAlignment, and readAlignment methods
 *       using a FileReader.
 * 
 * TODO: We should really be using the Alignment API for this functionality.
 */
public interface AlignmentFormat {

	/** @return The human readable name of the format. */ 
	public String getFormatName();
	
	/** @return The file extension of this format when saved to a file. */
	public String getFormatFileExtension();
	
	/**
	 * Write an alignment to an output writer.
	 */
	public void writeAlignment( Writer outputWriter, Alignment<Mapping> alignment );
	
	/**
	 * Write an alignment to a file.
	 */
	public void writeAlignmentToFile( File outputFile, Alignment<Mapping> alignment );
	
	/**
	 * Read an alignment from an input reader.
	 */
	public Alignment<Mapping> readAlignment( Reader inputReader );
	
	/** Read an alignment from a file. */
	public Alignment<Mapping> readAlignmentFromFile( File alignmentFile );
	
}
