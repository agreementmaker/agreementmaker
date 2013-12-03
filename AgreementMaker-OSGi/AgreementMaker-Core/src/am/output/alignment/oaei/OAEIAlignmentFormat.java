package am.output.alignment.oaei;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingAlgorithm;
import am.app.mappingEngine.MatchingPairAlignment;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.utility.MatchingPair;
import am.output.alignment.AlignmentFormat;

/**
 * Implements the OAEI Alignment Format.
 * 
 * FIXME: This is a mess. We need to use Alignment API.
 * 
 * TODO: We should really reuse the Alignment API method for this.
 */
public class OAEIAlignmentFormat implements AlignmentFormat {
	
	private static final Logger log = Logger.getLogger(OAEIAlignmentFormat.class);
	
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
	public Alignment<Mapping> readAlignment(Reader inputReader) {

		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();

		try {
	        SAXReader reader = new SAXReader();
	        reader.setEncoding("UTF-8");
	        Document doc = reader.read(inputReader);
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
	
	public static MatchingPairAlignment readAlignmentToMatchingPairAlignment( Reader inputReader ) {
		
		MatchingPairAlignment alignment = new MatchingPairAlignment();
		
		HashMap<String,List<MatchingPair>> alignmentMap = readAlignmentToHashMap(inputReader);
		for( List<MatchingPair> mpl : alignmentMap.values() ) {
			for( MatchingPair mp : mpl ) {
				if( !alignment.contains(mp) ) {
					alignment.add(mp);
				}
			}
		}
		
		return alignment;
	}
	
	/**
	 * Read an OAEI alignment file from a reader.
	 * 
	 * @return A Map of source and target URIs to their corresponding mapping.
	 */
	public static HashMap<String,List<MatchingPair>> readAlignmentToHashMap( Reader inputReader ) {
		
		HashMap<String,List<MatchingPair>> alignmentMap = new HashMap<String,List<MatchingPair>>();

		try {
	        SAXReader reader = new SAXReader();
	        reader.setEncoding("UTF-8");
	        Document doc = reader.read(inputReader);
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
	    			mpl.add(mp);
	    			alignmentMap.put(sourceURI, mpl);
	    		}
	    		
	    		if( alignmentMap.containsKey(targetURI) ) {
	    			alignmentMap.get(targetURI).add(mp);
	    		} else {
	    			List<MatchingPair> mpl = new ArrayList<MatchingPair>();
	    			mpl.add(mp);
	    			alignmentMap.put(targetURI, mpl);
	    		}
	    		
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return alignmentMap;
	}
	
	// TODO: This map needs to be integrated in the Alignment class.
	public static HashMap<String,List<MatchingPair>> convertAlignment( Alignment<Mapping> alignment ) {
		
		HashMap<String,List<MatchingPair>> alignmentMap = new HashMap<String,List<MatchingPair>>();
		
		for( Mapping mapping : alignment ) {

			String sourceURI = mapping.getEntity1().getUri();
			String targetURI = mapping.getEntity2().getUri();
			
			MatchingPair mp = new MatchingPair(sourceURI, targetURI, mapping.getSimilarity(), mapping.getRelation(), mapping.getProvenance());
			
			if( alignmentMap.containsKey(sourceURI) ) {
				alignmentMap.get(sourceURI).add(mp);
			} else {
				List<MatchingPair> mpl = new ArrayList<MatchingPair>();
				mpl.add(mp);
				alignmentMap.put(sourceURI, mpl);
			}
			
			if( alignmentMap.containsKey(targetURI) ) {
				alignmentMap.get(targetURI).add(mp);
			} else {
				List<MatchingPair> mpl = new ArrayList<MatchingPair>();
				mpl.add(mp);
				alignmentMap.put(targetURI, mpl);
			}			
		}
	
		return alignmentMap;
	}
	
}
