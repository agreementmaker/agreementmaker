package am.matcher.lod.instanceMatchers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import am.app.mappingEngine.utility.MatchingPair;
import am.parsing.AlignmentOutput;

public class AlignmentsOutput {
	
	private static final Logger sLog = Logger.getLogger(AlignmentsOutput.class);
	
	public static String alignmentsToOutput(List<MatchingPair> mappings){
		AlignmentOutput ao = new AlignmentOutput(null);
		ao.stringNS();
        ao.stringStart("yes", "0", "11", "onto1", "onto2", "uri1", "uri2");
        
        for (int i = 0, n = mappings.size(); i < n; i++) {
            MatchingPair mapping = mappings.get(i);
            String e1 = StringEscapeUtils.escapeXml(mapping.sourceURI);
            String e2 = StringEscapeUtils.escapeXml(mapping.targetURI);
            //String e1 = mapping.sourceURI;
            //String e2 = mapping.targetURI;
            
            String measure = Double.toString(mapping.similarity);
            ao.stringElement(e1, e2, measure);
        }
        
        ao.stringEnd();
        return ao.getString();
	}
	
	public static void writeMappingsOnDisk(String file, List<MatchingPair> pairs){
		sLog.info("Writing to file: " + file);
		String output = alignmentsToOutput(pairs);
		FileOutputStream fos;
		
		try {
			fos = new FileOutputStream(file);
			fos.write(output.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			sLog.error("", e);
		} catch (IOException e) {
			sLog.error("", e);
		}
		sLog.info("Done");
	}

}
