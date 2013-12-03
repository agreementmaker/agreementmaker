package am.output.alignment;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;

public abstract class AbstractAlignmentFormat implements AlignmentFormat {

	@Override
	public void writeAlignmentToFile(File outputFile, Alignment<Mapping> alignment) {
		try (FileWriter fw = new FileWriter(outputFile) ) {
			writeAlignment(fw, alignment);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Alignment<Mapping> readAlignmentFromFile(File alignmentFile) {
		try(FileReader fr = new FileReader(alignmentFile) ) {
			return readAlignment(fr);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
