package am.app.mappingEngine.referenceAlignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SparseMatrix;
import am.app.ontology.Node;
import am.output.OutputController;

public class ReferenceAlignmentMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -1688117047019381847L;

	//Constants
	/**Formats for reference files*/
    public final static String REF5 = "AM exported file format";
	public final static String OAEI = "OAEI standard format";
	public final static String OLD_OAEI = "OAEI-2007 i.e. weapons, wetlands... (Deprecated)";
	public final static String REF2a = "TXT: sourcename(tab)targetname";
	public final static String REF2b = "TXT: sourcename(tab)relation(tab)targetname";
	public final static String REF2c = "TXT: sourcename(tab)relation(tab)targetname(tab)similarity";
	public final static String REF3= "TXT: sourceDesc(tab)sourceName(tab)targetName(tab)targetDesc(tab)";

	/**Formats for output files*/
	public final static String OUTF1 = "TXT-1";
	
	private ArrayList<MatchingPair> referenceListOfPairs;
	private ArrayList<MatchingPair> nonEquivalencePairs;
	
	public ReferenceAlignmentMatcher() {
		super();
		needsParam = true;
		performSelection = false; // we don't need to do this, we will build the alignment by hand.
		setMaxSourceAlign(ANY_INT);
		setMaxTargetAlign(ANY_INT);
		setThreshold(0.01d);
	}
	
	@Override
	protected void beforeAlignOperations()throws Exception{
		super.beforeAlignOperations();
		
		// 1. Read the alignment file.
		referenceListOfPairs = readReferenceFile();  
		
		nonEquivalencePairs = new ArrayList<MatchingPair>();
		
		// 2. If the user selected only equivalence pairs, filter out any mappings that are not equivalence.
		// 2b. Save the non equivalence pairs into another array.
		if(((ReferenceAlignmentParameters)param).onlyEquivalence){
			Iterator<MatchingPair> it = referenceListOfPairs.iterator();
			while (it.hasNext()){
				MatchingPair mp = it.next();
				if(!mp.relation.equals(MappingRelation.EQUIVALENCE)){
					nonEquivalencePairs.add(mp);
					it.remove();
				}
			}
		}
		boolean b = ((ReferenceAlignmentParameters)param).displayPaneEmptyAlignment;
		if((referenceListOfPairs == null || referenceListOfPairs.size() == 0)&&b) {
			Utility.displayMessagePane("The reference file selected doen not contain any alignment.\nPlease check the format.", null);
		}
		else {
			stepsDone = 0;
			stepsTotal = referenceListOfPairs.size() * 2; // twice, once for classes, and another for properties
		}
		
		classesAlignmentSet = new Alignment<Mapping>( getSourceOntology().getID(), getTargetOntology().getID() );
		propertiesAlignmentSet = new Alignment<Mapping>( getSourceOntology().getID(), getTargetOntology().getID() );
		
		alignClass = !((ReferenceAlignmentParameters)param).skipClasses; // if we skipClasses, then we should not align them
		alignProp  = !((ReferenceAlignmentParameters)param).skipProperties; // same as above
	}
	
	
	/**
	 * The traversal will be based on the referenceListOfPairs, instead of traversing the ontologies.
	 * 
	 * First, we pick a pair from the list of pairs.  Then we look through the onotologies for that pair,
	 * looking in the source ontology first, then in the target.  If we find a match, we add it to the matrix.
	 * 
	 * The run time will be proportional to the number of pairs in the file, 
	 * instead of being proportional to the multiplicative size of both ontologies like it was before.
	 *
	 * Like before, equivalence is tested based on the name of the node.
	 *
	 * -- cosmin 20090826
	 *
	 * Updated this method to use HashMaps for a fast lookup, 
	 * instead of iterating through the source/target lists.
	 * 
	 * -- Cosmin Sept 12, 2011
	 *
	 */
	
	@Override
	protected SimilarityMatrix alignNodesOneByOne( List<Node> sourceList, List<Node> targetList, alignType typeOfNodes) throws Exception {
		SimilarityMatrix matrix = new SparseMatrix(sourceOntology, targetOntology, typeOfNodes); // Sparse Matrix instead of Array Matrix.

		if( referenceListOfPairs == null || referenceListOfPairs.size() == 0 ) 
			return matrix; // nothing to do		
			
		Iterator<MatchingPair> it = referenceListOfPairs.iterator();
		
		// create HashMap lookups by URI.
		HashMap<String,Node> sourceNodeURIMap = new HashMap<String,Node>();
		HashMap<String,Node> targetNodeURIMap = new HashMap<String,Node>();
		
		// fill in the URI maps.
		for( Node sourceNode : sourceList ) { sourceNodeURIMap.put(sourceNode.getUri(), sourceNode); }
		for( Node targetNode : targetList ) { targetNodeURIMap.put(targetNode.getUri(), targetNode); }
		
		//in this case the reference file contains URIs and that's what will be used for comparisons
		// Iterate over the list of pairs from the file
		while( it.hasNext() ) {
			MatchingPair mp = it.next(); // get the first matching pair from the list
			
			Mapping m = null;
			if( sourceNodeURIMap.containsKey( mp.sourceURI ) && targetNodeURIMap.containsKey( mp.targetURI ) ) {
				// concepts found
				Node sourceNode = sourceNodeURIMap.get( mp.sourceURI );
				Node targetNode = targetNodeURIMap.get( mp.targetURI );
				
				m = new Mapping( sourceNode, targetNode, mp.similarity, mp.relation, typeOfNodes, mp.provenance );
				
			} else if ( sourceNodeURIMap.containsKey( mp.targetURI ) && targetNodeURIMap.containsKey( mp.sourceURI ) ) {
				// concepts found, but with source and target switched
				Node sourceNode = sourceNodeURIMap.get( mp.targetURI );
				Node targetNode = targetNodeURIMap.get( mp.sourceURI );
				
				m = new Mapping( sourceNode, targetNode, mp.similarity, mp.relation, typeOfNodes, mp.provenance );
			}
			
			// add the mapping to the right alignment set
			if( m != null ) {
				if( typeOfNodes == alignType.aligningClasses ) {
					classesAlignmentSet.add(m);
				}
				else if ( typeOfNodes == alignType.aligningProperties ) {
					propertiesAlignmentSet.add(m);
				}
				
				matrix.set(m.getSourceKey(), m.getTargetKey(), m);
			}
			
			if( isProgressDisplayed() ) {
				stepDone();
				updateProgress();
			}
		}
		
		return matrix;
	}
	
	
	/**
	 * Parse the reference file: the file gets opened, depending on fileformat the file gets parsed differently, invoking a specific parse for that format
	 * If a developer is going to add a new format file, should add an if case and invokes the specific new parser.
	 * @param filename
	 * @return ArrayList of refpairs
	 */
	public ArrayList<MatchingPair> readReferenceFile() throws Exception{
		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
		ReferenceAlignmentParameters parameters = (ReferenceAlignmentParameters)param;
		    //Open the reference file
			BufferedReader input;
			input = new BufferedReader(new FileReader(parameters.fileName));
			//depending on file format a different parser is invoked
			if(parameters.format.equals(OAEI)) {
				result = parseStandardOAEI();
			}
			else if(parameters.format.equals(OLD_OAEI)) {
				result = parseOldOAEIFormat(input);
			}
			else if(parameters.format.equals(REF2a) || 
					parameters.format.equals(REF2b) || 
					parameters.format.equals(REF2c) ) {
				result = parseRefFormat2(input);
			}
			else if(parameters.format.equals(REF3)) {
				result = parseRefFormat3(input);
			}
			else if(parameters.format.equals(REF5)) {
				result = parseRefFormat4(input);
			}
			else {
				//development error, this exception can also be printed only in the console because is for developer users.
				//if the method is not developed the user shouldn't be able to select that format in the formatlist menu.
				throw new RuntimeException("No parsing method has been developed for this reference file format");
			}
		return result;
	}
	
	//Parsing reference file methods
	
	/**
	 * Parsing OAEI 2008 Ontology testcase onto101.rdf and so on
	 * @throws DocumentException 
	 */
	public ArrayList<MatchingPair> parseStandardOAEI() throws IOException, DocumentException{
		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
		File file = new File(((ReferenceAlignmentParameters)param).fileName);
        SAXReader reader = new SAXReader();
        Document doc = reader.read(file);   // TODO: FIX PARSE ERROR if using UTF-8 Characters!!!!!!
        Element root = doc.getRootElement();
        
        String matcherName = root.attributeValue("matcherName");
        if( matcherName != null && !matcherName.isEmpty() ) setName(StringEscapeUtils.unescapeHtml(matcherName));
        
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

	    return result;
	}
	
	/*public MappingRelation getRelationFromFileFormat(String relation) {
		MappingRelation result = MappingRelation.EQUIVALENCE;
		String format = ((ReferenceAlignmentParameters)param).format;
		if(format.equals(OAEI)){//Right now only this format has the relation string
			//TODO i don't actually know symbols different from equivalence used in this format, so i will put the symbol itself as relation
			if(relation == null || relation.equals("") || relation.equals(MappingRelation.EQUIVALENCE)) {
				result = MappingRelation.EQUIVALENCE;
			}
			else result = MappingRelation.parseRelation(relation); //if it is another symbol i'll put it directly in the alignment so that is displayed on the AM we can actually see it, and maybe use it to represent those relations in our Alignment class
		}
		return result;
	}*/


	/**
	 * This method is taken from the Read_Compare tool developed by William Sunna
	 * This method parse a reference file in OAEI format like weapons, networks, russia...
	 * The lines containing ao:elementA contain the source name
	 * Each lines after that one contains the target name.
	 * EXAMPLE
	 * :Alignment27
	 *a ao:Alignment;
	 *ao:elementA a:NodeA ;
	 * ao:elementB b:NodeA ;
	 *ao:alignmentConfidence "1". 
	 *
	 */
	public ArrayList<MatchingPair> parseOldOAEIFormat(BufferedReader br) throws IOException{
		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
	    
	    String line;
	    while((line = br.readLine()) !=null){
	    	if(line.indexOf("ao:elementA") != -1) {
	        	String source = line.substring(15);
	        	source = source.substring(0,source.length()-2);
	        	//this further control has been introduced for the Wine ontologies
	        	if((source.charAt(source.length()-1)+"").equals(";")){
	        		source = source.substring(0,source.length()-1);
	        	}
	        	if((source.charAt(source.length()-1)+"").equals(" ")){
	        		source = source.substring(0,source.length()-1);
	        	}
	            line = br.readLine();
	            String target = line.substring(15);
	            target = target.substring(0,target.length()-2);
	            MatchingPair r = new MatchingPair(source,target);
	            r.similarity = 1;
	            r.relation = MappingRelation.EQUIVALENCE;
	            result.add(r);
	    	}
	    }
	    
	    return result;
	}
		
		/**
		 * Format used for the simplest txt format.
		 * This method parse a reference  file in the formats:
		 * 
		 * a) sourceName(tab)targetName
		 * b) sourceName(tab)relation(tab)targetName
		 * c) sourceName(tab)relation(tab)targetName(tab)similarity 
		 */
		public ArrayList<MatchingPair> parseRefFormat2(BufferedReader br) throws IOException{
			ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
		    String line;
		    String source;
		    String target;
		    int linenum = 0;
		    while((line = br.readLine()) !=null){	
		    	linenum++;
		    	String[] split = line.split("\t");
		    	if(split.length == 2) {
		        	source = split[0].trim();
		        	target = split[1].trim();
		            MatchingPair r = new MatchingPair(source,target);
		            r.similarity = 1d;
		            r.relation = MappingRelation.EQUIVALENCE;
		            result.add(r);
		    	}
		    	else if(split.length == 3) {
		        	source = split[0].trim();
		        	target = split[2].trim();
		            MatchingPair r = new MatchingPair(source,target);
		            r.similarity = 1d;
		            r.relation = MappingRelation.parseRelation(split[1]);
		            result.add(r);
		    	}
		    	else if(split.length == 4) {
		    		source = split[0].trim();
		    		target = split[2].trim();
		    		MatchingPair r = new MatchingPair(source,target);
		    		r.similarity = Double.parseDouble(split[3]);
		    		r.relation = MappingRelation.parseRelation(split[1]);
		    	}
		    	else {
		    		Logger log = Logger.getLogger(this.getClass());
		    		log.setLevel(Level.ERROR);
		    		log.error("Reference file parse error (line " + linenum + "): " + line);
		    	}
		    	//else System.out.println("Some lines in the reference are not in the correct format. Check result please");
		    	 
		    	 
		    }
		    return result;
		}
	
	/**
	 * Format used for Madison Dane test case.
	 * This method parse a reference txt file in the format sourceDesc(tab)sourceName(tab)--->(tab)targetName(tab)targetDesc(tab) or sourceDesc(tab)sourceName(tab)targetName(tab)targetDesc(tab)
	 * for for the first comparison method only source name and target name are needed.
	 */
	public ArrayList<MatchingPair> parseRefFormat3(BufferedReader br) throws IOException{
		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
	    
	    String line;
	    String source;
	    String target;
	    while((line = br.readLine()) !=null){
	    	String[] split = line.split("\t");
	    	if(split.length == 5) {
	        	source = split[1];
	        	target = split[3];
	            MatchingPair r = new MatchingPair(source,target);
	            r.similarity = 1;
	            r.relation = MappingRelation.EQUIVALENCE;
	            result.add(r);
	    	}
	    	else if(split.length == 4) {
	        	source = split[1];
	        	target = split[2];
	            MatchingPair r = new MatchingPair(source,target);
	            r.similarity = 1;
	            r.relation = MappingRelation.EQUIVALENCE;
	            result.add(r);
	    	}
	    	//else System.out.println("Some lines in the reference are not in the correct format. Check result please");
	    }
	    return result;
	}

	public ArrayList<MatchingPair> parseRefFormat4(BufferedReader br) throws IOException{
		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
	    
	    String line;
	    String source;
	    String target;
	    double similarity;
	    while((line = br.readLine()) !=null){
	    	String[] split = line.split("\t");
	    	if(split.length == 5 || split.length == 6 && split[1].equals(OutputController.arrow)) {
	        	source = split[0];
	        	target = split[2];
	        	try {
	        		similarity = Double.parseDouble(split[3]);
	        	}
	        	catch(Exception e) {
	        		similarity = 1;
	        	}
	        	String relationString = split[4];
	        	if(relationString == null || relationString.equals("")) {
	        		relation = MappingRelation.EQUIVALENCE;
	        	}
	        	MatchingPair r = new MatchingPair(source,target);
	            r.similarity = similarity;
	            r.relation = MappingRelation.parseRelation(relationString);
	            if(split.length == 6){
	            	r.provenance = split[5];    
	            }
	            result.add(r);
	    	}
	    }
	    return result;
	}
	
	/**These 3 methods are invoked any time the user select a matcher in the matcherscombobox. Usually developers don't have to override these methods unless their default values are different from these.*/
	@Override
	public double getDefaultThreshold() {
		return 0.01;
	}
	
	/**These 3 methods are invoked any time the user select a matcher in the matcherscombobox. Usually developers don't have to override these methods unless their default values are different from these.*/
	@Override
	public int getDefaultMaxSourceRelations() {
		return ANY_INT;
	}

	/**These 3 methods are invoked any time the user select a matcher in the matcherscombobox. Usually developers don't have to override these methods unless their default values are different from these.*/
	@Override
	public int getDefaultMaxTargetRelations() {
		return ANY_INT;
	}
	
	@Override
	public String getDescriptionString() {
		String result = "Allows user to display a reference alignment, which is a set of mappings that has been determined by domain experts.\n";
		result += "It's used to determine the quality, in terms of precision and recall, of a matching method.\n";
		result += "When available reference alignments are contained in a reference file.\n";
		result += "The most simple format read by the AgreementMaker is a txt file which contains alignments in the form: sourceLocalName(TAB)targetLocalName\n";
		result += "While using sample ontologies, each testcase has a different reference file, read the readme.txt file to find the location of the reference file of each testcase.\n";
		return result;
	}
	
	@Override
	public AbstractMatcherParametersPanel getParametersPanel() {
		if(parametersPanel == null){
			parametersPanel = new ReferenceAlignmentParametersPanel();
		}
		return parametersPanel;
	}
	
	//useful for debug
	public void printAllPairs(){
		System.out.println("REFERENCE (size = "+referenceListOfPairs.size()+")");
		for(MatchingPair pair: referenceListOfPairs){
			System.out.println("P:" + pair.getTabString());
		}
		System.out.println("NON EQUIVALENCE (size = "+nonEquivalencePairs.size()+")");
		for(MatchingPair pair: nonEquivalencePairs){
			System.out.println("P:" + pair.getTabString());
		}
	}
}
