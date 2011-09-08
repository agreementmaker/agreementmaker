package am.output.alignment.oaei;

import java.io.File;
import java.io.Reader;
import java.io.Writer;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.output.alignment.AlignmentFormat;

/**
 * Implements the OAEI Alignment Format.
 * 
 * TODO: We should really reuse the Alignment API method for this.
 */
public class OAEIAlignmentFormat implements AlignmentFormat {
	
	@Override public String getFormatName() { return "Alignment API Format"; }
	@Override public String getFormatFileExtension() { return ".rdf"; }
	
	@Override
	public void writeAlignment(Writer outputWriter, Alignment<Mapping> alignment) {
		
	}

	@Override
	public void writeAlignmentToFile(File outputFile,
			Alignment<Mapping> alignment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Alignment<Mapping> readAlignment(Reader inputReader,
			Alignment<Mapping> alignment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Alignment<Mapping> readAlignmentFromFile(File alignmentFile) {
		// TODO Auto-generated method stub
		return null;
	}

}
