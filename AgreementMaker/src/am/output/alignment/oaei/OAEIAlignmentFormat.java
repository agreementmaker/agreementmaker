package am.output.alignment.oaei;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.output.alignment.AlignmentFormat;

/**
 * Implements the OAEI Alignment Format.
 * 
 * FIXME: This is a mess. We need to use Alignment API.
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

		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();

		try {
	        SAXReader reader = new SAXReader();
	        Document doc = reader.read(inputReader);   // TODO: FIX PARSE ERROR if using UTF-8 Characters!!!!!!
	        Element root = doc.getRootElement();
	        
	        String matcherName = root.attributeValue("matcherName");
	        //if( matcherName != null && !matcherName.isEmpty() ) setName(StringEscapeUtils.unescapeHtml(matcherName));
	        
	        Element align = root.element("Alignment");
	        Iterator<?> map = align.elementIterator("map");  // TODO: Fix this hack? (Iterator<?>)
	        while (map.hasNext()) {
	            Element e = ((Element)map.next()).element("Cell");
	            if (e == null) {
	            	
	                continue;
	            }
	            String sourceURI = e.element("entity1").attributeValue("resource");
	            String targetURI = e.element("entity2").attributeValue("resource");
	            MappingRelation relation =  MappingRelation.parseRelation( e.elementText("relation") );
	            String measure = e.elementText("measure");
	            String provenance = e.elementText("provenance");
	
	            //Take the measure, if i can't find a valid measure i'll suppose 1
	            
	            double parsedSimilarity = -1;
	            if(measure != null) {
	            	try {
	            		parsedSimilarity = Double.parseDouble(measure);
	            	}
	            	catch(Exception ex) {};
	            }
	            if(parsedSimilarity < 0 || parsedSimilarity > 1) parsedSimilarity = 1;
	            
	            // String correctRelation = getRelationFromFileFormat(relation);
	            
	    		MatchingPair mp = new MatchingPair(sourceURI, targetURI, parsedSimilarity, relation, provenance);
	    		result.add(mp);
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Alignment<Mapping> readAlignmentFromFile(File alignmentFile) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String,List<MatchingPair>> readAlignment( Reader inputReader ) {
		
		HashMap<String,List<MatchingPair>> alignmentMap = new HashMap<String,List<MatchingPair>>();

		try {
	        SAXReader reader = new SAXReader();
	        Document doc = reader.read(inputReader);   // TODO: FIX PARSE ERROR if using UTF-8 Characters!!!!!!
	        Element root = doc.getRootElement();
	        
	        //if( matcherName != null && !matcherName.isEmpty() ) setName(StringEscapeUtils.unescapeHtml(matcherName));
	        
	        Element align = root.element("Alignment");
	        Iterator<?> map = align.elementIterator("map");  // TODO: Fix this hack? (Iterator<?>)
	        while (map.hasNext()) {
	            Element e = ((Element)map.next()).element("Cell");
	            if (e == null) {
	            	
	                continue;
	            }
	            String sourceURI = e.element("entity1").attributeValue("resource");
	            String targetURI = e.element("entity2").attributeValue("resource");
	            MappingRelation relation =  MappingRelation.parseRelation( e.elementText("relation") );
	            String measure = e.elementText("measure");
	            String provenance = e.elementText("provenance");
	
	            //Take the measure, if i can't find a valid measure i'll suppose 1
	            
	            double parsedSimilarity = -1;
	            if(measure != null) {
	            	try {
	            		parsedSimilarity = Double.parseDouble(measure);
	            	}
	            	catch(Exception ex) {};
	            }
	            if(parsedSimilarity < 0 || parsedSimilarity > 1) parsedSimilarity = 1;
	            
	            // String correctRelation = getRelationFromFileFormat(relation);
	            
	    		MatchingPair mp = new MatchingPair(sourceURI, targetURI, parsedSimilarity, relation, provenance);
	    		
	    		if( alignmentMap.containsKey(sourceURI) ) {
	    			alignmentMap.get(sourceURI).add(mp);
	    		} else {
	    			List<MatchingPair> mpl = new ArrayList<MatchingPair>();
	    			alignmentMap.put(sourceURI, mpl);
	    		}
	    		
	    		if( alignmentMap.containsKey(targetURI) ) {
	    			alignmentMap.get(targetURI).add(mp);
	    		} else {
	    			List<MatchingPair> mpl = new ArrayList<MatchingPair>();
	    			alignmentMap.put(targetURI, mpl);
	    		}
	    		
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return alignmentMap;
	}
	
}
