package am.app.mappingEngine.referenceAlignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.ontology.Node;
import am.output.OutputController;

public class ReferenceAlignmentMatcher extends AbstractMatcher {

	private static final long serialVersionUID = -1688117047019381847L;

	//Constants
	/**Formats for reference files*/
    public final static String REF5 = "AM exported file format";
	public final static String REF0 = "OAEI standard format";
	public final static String REF1 = "OAEI-2007 i.e. weapons, wetlands...";
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
		setMaxSourceAlign(ANY_INT);
		setMaxTargetAlign(ANY_INT);
		setThreshold(0.01d);
		
	}
	
	
	protected void beforeAlignOperations()throws Exception{
		super.beforeAlignOperations();
		referenceListOfPairs = readReferenceFile();
		nonEquivalencePairs = new ArrayList<MatchingPair>();
		
		
		//if(((ReferenceAlignmentParameters)param).onlyEquivalence){
		//This has to be done in any case
			
		Iterator<MatchingPair> it = referenceListOfPairs.iterator();
		while (it.hasNext()){
			MatchingPair mp = it.next();
			if(!mp.relation.equals(Mapping.EQUIVALENCE)){//should be equals but sometimes they have spaces together with the =
				nonEquivalencePairs.add(mp);
				it.remove();
			}
		}
		
		if(referenceListOfPairs == null || referenceListOfPairs.size() == 0) {
			Utility.displayMessagePane("The reference file selected doen not contain any alignment.\nPlease check the format.", null);
		}
		else {
			stepsDone = 0;
			stepsTotal = referenceListOfPairs.size() * 2; // twice, once for classes, and another for properties
		}
		
		classesAlignmentSet = null;
		propertiesAlignmentSet = null;
		alignClass = !((ReferenceAlignmentParameters)param).skipClasses; // if we skipClasses, then we should not align them
		alignProp  = !((ReferenceAlignmentParameters)param).skipProperties; // same as above
		
		//printAllPairs();
		
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
	 */
	
	@Override
	protected SimilarityMatrix alignNodesOneByOne(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes) throws Exception {
		SimilarityMatrix matrix = new ArraySimilarityMatrix(sourceList.size(), targetList.size(), typeOfNodes, relation); // TODO: Sparse Matrix instead of Array Matrix!
		Node source;
		Node target;
		Mapping alignment = null; //Temp structure to keep sim and relation between two nodes, shouldn't be used for this purpose but is ok		
	
		MatchingPair mp = null;
		if( referenceListOfPairs != null ) {
			
			//non equivalence pairs have to be considered!!
			if(nonEquivalencePairs!=null && !((ReferenceAlignmentParameters)param).onlyEquivalence)
				referenceListOfPairs.addAll(nonEquivalencePairs);
			
			Iterator<MatchingPair> it = referenceListOfPairs.iterator();
			
			// Iterate over the list of pairs from the file
			
			while( it.hasNext() ) {
				mp = it.next(); // get the first matching pair from the list
				//System.out.println("A:"+mp.sourcename+"- B:"+mp.targetname+".");
				// find the source node in the source ontology
				for( int i = 0; i < sourceList.size(); i++ ) {
					source = sourceList.get(i);
			    	String sname = source.getLocalName();
					if( mp.sourcename.equals(sname) ) {
						// we have found a match for the source node
						
						for( int j = 0; j < targetList.size(); j++ ) {
							target = targetList.get(j);
							String tname = target.getLocalName();
							
							if( this.isCancelled() ) { return matrix; }
							
							if( mp.targetname.equals(tname) ) {
								// we have found a match for the target node, it means a valid alignment
								alignment = new Mapping( source, target, mp.similarity);
								alignment.setRelation(mp.relation);
								if( mp.provenance != null ) alignment.setProvenance(mp.provenance);
								matrix.set(i, j, alignment);
								//it.remove();
							}
						}
					}
				}
				
				if( isProgressDisplayed() ) {
					stepDone();
					updateProgress();
				}
				
				
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
			if(parameters.format.equals(REF0)) {
				result = parseRefFormat0();
			}
			else if(parameters.format.equals(REF1)) {
				result = parseRefFormat1(input);
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
	public ArrayList<MatchingPair> parseRefFormat0() throws IOException, DocumentException{
		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
		File file = new File(((ReferenceAlignmentParameters)param).fileName);
        SAXReader reader = new SAXReader();
        Document doc = reader.read(file);   // TODO: FIX PARSE ERROR if using UTF-8 Characters!!!!!!
        Element root = doc.getRootElement();
        Element align = root.element("Alignment");
        Iterator<?> map = align.elementIterator("map");  // TODO: Fix this hack? (Iterator<?>)
        while (map.hasNext()) {
        	System.out.println(map.toString());
            Element e = ((Element)map.next()).element("Cell");
            if (e == null) {
            	
                continue;
            }
            String s1 = e.element("entity1").attributeValue("resource");
            String s2 = e.element("entity2").attributeValue("resource");
           
            String relation =  e.elementText("relation");
            String sourceid = null;
            String targetid = null;
            String split[];
            if(s1!=null) {
            	split = s1.split("#");
            	if(split.length==2) {
            		sourceid = split[1];
            	}
            	else {
            		split = s1.split("/"); // the URI does not contain a #
            		sourceid = split[  split.length - 1 ]; // use the last token as the name
            	}
            		
            }
            if(s2!=null) {
            	split = s2.split("#");
            	if(split.length==2) {
            		targetid = split[1];
            	} else {
            		split = s1.split("/"); // the URI does not contain a #
            		targetid = split[  split.length - 1 ]; // use the last token as the name
            	}
            }
            //Take the measure, if i can't find a valid measure i'll suppose 1
            String measure = e.elementText("measure");
            double mes=-1;
            if(measure != null) {
            	try {
            		mes = Double.parseDouble(measure);
            	}
            	catch(Exception ex) {};
            }
            if(mes < 0 || mes > 1) mes = 1;
            
            
            String provenance = e.elementText("provenance");
            
            // String correctRelation = getRelationFromFileFormat(relation);
            
            if(sourceid != null && targetid != null) {
            	if( provenance == null ) {
            		MatchingPair mp = new MatchingPair(sourceid, targetid, mes, relation);
            		result.add(mp);
            	} else {
            		MatchingPair mp = new MatchingPair(sourceid, targetid, mes, relation, provenance);
            		result.add(mp);
            	}
            }
        }

	    return result;
	}
	
	public String getRelationFromFileFormat(String relation) {
		String result = Mapping.EQUIVALENCE;
		String format = ((ReferenceAlignmentParameters)param).format;
		if(format.equals(REF0)){//Right now only this format has the relation string
			//TODO i don't actually know symbols different from equivalence used in this format, so i will put the symbol itself as relation
			if(relation == null || relation.equals("") || relation.equals(Mapping.EQUIVALENCE)) {
				result = Mapping.EQUIVALENCE;
			}
			else result = relation; //if it is another symbol i'll put it directly in the alignment so that is displayed on the AM we can actually see it, and maybe use it to represent those relations in our Alignment class
		}
		return result;
	}


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
	public ArrayList<MatchingPair> parseRefFormat1(BufferedReader br) throws IOException{
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
	            r.relation = Mapping.EQUIVALENCE;
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
		            r.relation = Mapping.EQUIVALENCE;
		            result.add(r);
		    	}
		    	else if(split.length == 3) {
		        	source = split[0].trim();
		        	target = split[2].trim();
		            MatchingPair r = new MatchingPair(source,target);
		            r.similarity = 1d;
		            r.relation = Mapping.parseRelation(split[1]);
		            result.add(r);
		    	}
		    	else if(split.length == 4) {
		    		source = split[0].trim();
		    		target = split[2].trim();
		    		MatchingPair r = new MatchingPair(source,target);
		    		r.similarity = Double.parseDouble(split[3]);
		    		r.relation = Mapping.parseRelation(split[1]);
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
	            r.relation = Mapping.EQUIVALENCE;
	            result.add(r);
	    	}
	    	else if(split.length == 4) {
	        	source = split[1];
	        	target = split[2];
	            MatchingPair r = new MatchingPair(source,target);
	            r.similarity = 1;
	            r.relation = Mapping.EQUIVALENCE;
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
	    String relation;
	    while((line = br.readLine()) !=null){
	    	String[] split = line.split("\t");
	    	if(split.length == 5 && split[1].equals(OutputController.arrow)) {
	        	source = split[0];
	        	target = split[2];
	        	try {
	        		similarity = Double.parseDouble(split[3]);
	        	}
	        	catch(Exception e) {
	        		similarity = 1;
	        	}
	        	relation = split[4];
	        	if(relation == null || relation.equals("")) {
	        		relation = Mapping.EQUIVALENCE;
	        	}
	            MatchingPair r = new MatchingPair(source,target);
	            r.similarity = similarity;
	            r.relation = relation;
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
