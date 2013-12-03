package am.output.alignment.oaei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingPairAlignment;
import am.app.mappingEngine.utility.MatchingPair;
import am.app.ontology.Ontology;
import am.output.alignment.AbstractAlignmentFormat;

/**
 * This format is used to read the IMEI2013 reference alignments.
 * 
 * @author cosmin
 *
 */
public class TSVAlignmentFormat extends AbstractAlignmentFormat {

	private Ontology sourceOnt;
	private Ontology targetOnt;
	
	public TSVAlignmentFormat(Ontology source, Ontology target) {
		this.sourceOnt = source;
		this.targetOnt = target;
	}
	
	@Override public String getFormatName() { return "Tab Separated Values Format"; }
	@Override public String getFormatFileExtension() { return ".tsv"; }
	
	@Override
	public void writeAlignment(Writer outputWriter, Alignment<Mapping> alignment) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public Alignment<Mapping> readAlignment(Reader inputReader) {
		throw new RuntimeException("Not implemented.");
	}
	
	public MatchingPairAlignment readMatchingPairs(Reader inputReader) {
		MatchingPairAlignment mpa = new MatchingPairAlignment();
		
		BufferedReader reader = new BufferedReader(inputReader);
		
		String line;
		try {
			while( (line = reader.readLine()) != null ) {
				String[] splitLine = line.split("\t");
				MatchingPair mp = new MatchingPair(splitLine[0], splitLine[1]);
				mpa.add(mp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mpa;
	}

}
