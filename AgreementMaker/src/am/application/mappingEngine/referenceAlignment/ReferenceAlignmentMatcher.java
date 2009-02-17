package am.application.mappingEngine.referenceAlignment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import am.Utility;
import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.Alignment;
import am.application.ontology.Node;
import am.output.OutputController;

public class ReferenceAlignmentMatcher extends AbstractMatcher {

		//Constants
		/**Formats for reference files*/
	    public final static String REF5 = "AM exported file format";
		public final static String REF0 = "OAEI-2008-testcase";
		public final static String REF1 = "OAEI-2007 i.e. weapons, wetlands...";
		public final static String REF2 = "TXT: sourcename(tab)targetname";
		public final static String REF3= "TXT: sourceDesc(tab)sourceName(tab)targetName(tab)targetDesc(tab)";

		/**Formats for output files*/
		public final static String OUTF1 = "TXT-1";
		
		private ArrayList<MatchingPair> referenceListOfPairs;
		
	public ReferenceAlignmentMatcher() {
		super();
		needsParam = true;
		maxSourceAlign = ANY_INT;
		maxTargetAlign = ANY_INT;
		threshold = 0.01;
		parametersPanel = new ReferenceAlignmentParametersPanel();
	}
	
	
	
	protected void beforeAlignOperations()throws Exception{
		super.beforeAlignOperations();
		referenceListOfPairs = readReferenceFile();
		if(referenceListOfPairs == null || referenceListOfPairs.size() == 0) {
			Utility.displayMessagePane("The reference file selected doen not contain any alignment.\nPlease check the format.", null);
		}
	}
	
    protected Alignment alignTwoNodes(Node source, Node target , alignType typeOfNodes) {
    	String sname = source.getLocalName();
    	String tname = target.getLocalName();
    	Alignment a = new Alignment(source, target, 0);//if I don't find any alignment in the reference about this 2 nodes i will create an alignment with 0 similarity
    	MatchingPair mp = null;
    	if(referenceListOfPairs != null) {
    		Iterator<MatchingPair> it = referenceListOfPairs.iterator();
    		while(it.hasNext()) {
    			mp = it.next();
    			if(mp.sourcename.equalsIgnoreCase(sname)&& mp.targetname.equalsIgnoreCase(tname)) {
    				a = new Alignment(source, target, mp.similarity);
    			}
    		}
    	}
		return a;
	}
	
	
	/**
	 * Parse the reference file: the file gets opened, depending on fileformat the file gets parsed differently, invoking a specific parse for that format
	 * If a developer is going to add a new format file, should add an if case and invokes the specific new parser.
	 * @param filename
	 * @return ArrayList of refpairs
	 */
	public ArrayList<MatchingPair> readReferenceFile(){
		ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
		ReferenceAlignmentParameters parameters = (ReferenceAlignmentParameters)param;
		try {
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
			else if(parameters.format.equals(REF2)) {
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
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		catch (DocumentException e) {
			e.printStackTrace();
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
        Document doc = reader.read(file);
        Element root = doc.getRootElement();
        Element align = root.element("Alignment");
        Iterator map = align.elementIterator("map");
        while (map.hasNext()) {
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
            }
            if(s2!=null) {
            	split = s2.split("#");
            	if(split.length==2) {
            		targetid = split[1];
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
            
            // String correctRelation = getRelationFromFileFormat(relation);
            
            if(sourceid != null && targetid != null) {
            	MatchingPair mp = new MatchingPair(sourceid, targetid, mes, relation);
            	result.add(mp);
            }
        }

	    return result;
	}
	
	private String getRelationFromFileFormat(String relation) {
		String result = Alignment.EQUIVALENCE;
		String format = ((ReferenceAlignmentParameters)param).format;
		if(format.equals(REF0)){//Right now only this format has the relation string
			//TODO i don't actually know symbols different from equivalence used in this format, so i will put the symbol itself as relation
			if(relation == null || relation.equals("") || relation.equals(Alignment.EQUIVALENCE)) {
				result = Alignment.EQUIVALENCE;
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
	            line = br.readLine();
	            String target = line.substring(15);
	            target = target.substring(0,target.length()-2);
	            MatchingPair r = new MatchingPair(source,target);
	            r.similarity = 1;
	            r.relation = Alignment.EQUIVALENCE;
	            result.add(r);
	    	}
	    }
	    
	    return result;
	}
		
		/**
		 * Format used for the simplest txt format.
		 * This method parse a reference  file in the format sourceName(tab)--->(tab)targetName or sourceName(tab)targetName
		 */
		public ArrayList<MatchingPair> parseRefFormat2(BufferedReader br) throws IOException{
			ArrayList<MatchingPair> result = new ArrayList<MatchingPair>();
		    String line;
		    String source;
		    String target;
		    while((line = br.readLine()) !=null){	
		    	String[] split = line.split("\t");
		    	if(split.length == 2) {
		        	source = split[0];
		        	target = split[1];
		            MatchingPair r = new MatchingPair(source,target);
		            r.similarity = 1;
		            r.relation = Alignment.EQUIVALENCE;
		            result.add(r);
		    	}
		    	else if(split.length == 3) {
		        	source = split[0];
		        	target = split[2];
		            MatchingPair r = new MatchingPair(source,target);
		            r.similarity = 1;
		            r.relation = Alignment.EQUIVALENCE;
		            result.add(r);
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
	            r.relation = Alignment.EQUIVALENCE;
	            result.add(r);
	    	}
	    	else if(split.length == 4) {
	        	source = split[1];
	        	target = split[2];
	            MatchingPair r = new MatchingPair(source,target);
	            r.similarity = 1;
	            r.relation = Alignment.EQUIVALENCE;
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
	        		relation = Alignment.EQUIVALENCE;
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
	public double getDefaultThreshold() {
		return 0.01;
	}
	
	/**These 3 methods are invoked any time the user select a matcher in the matcherscombobox. Usually developers don't have to override these methods unless their default values are different from these.*/
	public int getDefaultMaxSourceRelations() {
		// TODO Auto-generated method stub
		return ANY_INT;
	}

	/**These 3 methods are invoked any time the user select a matcher in the matcherscombobox. Usually developers don't have to override these methods unless their default values are different from these.*/
	public int getDefaultMaxTargetRelations() {
		// TODO Auto-generated method stub
		return ANY_INT;
	}
	
	public String getDescriptionString() {
		String result = "Allows user to display a reference alignment, which is a set of mappings that has been determined by domain experts.\n";
		result += "It's used to determine the quality, in terms of precision and recall, of a matching method.\n";
		result += "When available reference alignments are contained in a reference file.\n";
		result += "The most simple format read by the AgreementMaker is a txt file which contains alignments in the form: sourceLocalName(TAB)targetLocalName\n";
		result += "While using sample ontologies, each testcase has a different reference file, read the readme.txt file to find the location of the reference file of each testcase.\n";
		return result;
	}
	
	
}
