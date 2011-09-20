/* Concept Matcher.
 * 
 * Written by William (not Sunna). 
 * 
 */


package am.app.mappingEngine.conceptMatcher;

// TODO: Remove the Stanford Parser before distribution, GPL Licensed. - cosmin

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import am.GlobalStaticVariables;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcherParametersPanel;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.ontology.Node;

public class ConceptMatcher extends AbstractMatcher { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 8164676239475978748L;
	
	// JAWS WordNet interface
	//private transient WordNetDatabase wordnet  = null;
	//public LexicalizedParser parser = null;
	Hashtable<String, String> htConcepts = null;
	
	public ConceptMatcher() {
		// warning, param is not available at the time of the constructor
		
		super();
		needsParam = true;
	}
	
	public String getDescriptionString() {
		return "Extracts the longest-defined concept from each node and compares the Jaccard scores of concept sets.\n" +
				"Only Nodes' local-names (XML id) are considered in the process.\n" +
				"String are preprocessed with cleaning, stemming, stop-words removing, and tokenization techniques.\n" +
				"A similarity matrix contains the Jaccard similarity between each pair (sourceNode, targetNode).\n" +
				"A selection algorithm select valid alignments considering threshold and number of relations per node.\n"; 
	}
	
	/* *******************************************************************************************************
	 ************************ Algorithm functions beyond this point*************************************
	 * *******************************************************************************************************
	 */
	
	/**
	 * Set up the parser once before aligning
	 * @see am.app.mappingEngine.AbstractMatcher#beforeAlignOperations()
	 */
	protected void beforeAlignOperations() throws Exception{
		super.beforeAlignOperations();
		
		//parser = new LexicalizedParser("englishPCFG.ser.gz");
	}
	
	
	// overriding the abstract method in order to keep track of what kind of nodes we are aligning
    protected SimilarityMatrix alignProperties(ArrayList<Node> sourcePropList, ArrayList<Node> targetPropList) {
		return alignNodesOneByOne(sourcePropList, targetPropList, alignType.aligningProperties );
	}

	// overriding the abstract method in order to keep track of what kind of nodes we are aligning
    protected SimilarityMatrix alignClasses(ArrayList<Node> sourceClassList, ArrayList<Node> targetClassList) {
		return alignNodesOneByOne(sourceClassList, targetClassList, alignType.aligningClasses);
	}
	
	// this method is exactly similar to the abstract method, except we pass one extra parameters to the alignTwoNodes function
    protected SimilarityMatrix alignNodesOneByOne(ArrayList<Node> sourceList, ArrayList<Node> targetList, alignType typeOfNodes) {
		SimilarityMatrix matrix = new ArraySimilarityMatrix(sourceOntology, targetOntology, typeOfNodes);
		Node source;
		Node target;
		//first go through and extract the longest defined concept for each node
		htConcepts = new Hashtable<String, String>();
		for(int i = 0; i < sourceList.size(); i++)
		{
			String strOriginalText = sourceList.get(i).getLocalName();
			if (!htConcepts.containsKey(strOriginalText))
			{
				//String strTreatedText = treatString(strOriginalText);
				String strConcept = "";
//				if (typeOfNodes == alignType.aligningClasses)
//					strConcept = GetClassConcept(strTreatedText);
//				else
//					strConcept = GetPropertyConcept(strTreatedText);
				htConcepts.put(strOriginalText, strConcept);
			}
		}

		Mapping alignment; //Temp structure to keep sim and relation between two nodes, shouldn't be used for this purpose but is ok
		for(int i = 0; i < sourceList.size(); i++) {
			source = sourceList.get(i);
			for(int j = 0; j < targetList.size(); j++) {
				target = targetList.get(j);
				alignment = alignTwoNodes(source, target, typeOfNodes);
				matrix.set(i,j,alignment);
				if( GlobalStaticVariables.USE_PROGRESS_BAR ) stepDone(); // progress dialog
			}
			if( GlobalStaticVariables.USE_PROGRESS_BAR ) updateProgress(); // progress dialog
		}
		return matrix;
	}
    
    /*private String GetClassConcept(String FullText)
    {
    	 1.  Formulate a grammatical sentence using the node text:  "I described the X"
    	 * 2.  Parse the sentence to extract the direct object
    	 * 3.  Prepend word in the original text until the phrase is no longer defined in WordNet
    	 * Example:  "haulage truck driver:
    	 * Send "I described the haulage truck driver." to the Stanford Parser.
    	 * "driver" is the direct object
    	 * Prepend "truck" to form "truck driver" --> this is defined in WordNet
    	 * Prepend "haulage to form "haulage truck driver" --> this is not defined
    	 * "truck driver" is the longest defined concept
    	 
    	parser.parse("I described the " + FullText + ".");
	    Tree parseTree = parser.getBestParse();
	    TreebankLanguagePack tlp = parser.getOp().tlpParams.treebankLanguagePack();
	    GrammaticalStructureFactory factory = tlp.grammaticalStructureFactory();
	    GrammaticalStructure depTree = factory.newGrammaticalStructure(parseTree);
	    Collection<TypedDependency> list = depTree.typedDependencies();
	    String strConcept = "";
	    for(TypedDependency d : list)
	    {
	    	if (d.reln().getShortName() == "dobj")
	    		strConcept = d.dep().value();
	    }
	    if (strConcept.length() > 0)
	    {
	    	//prepend words until no longer defined
	    	if( wordnet == null )
				wordnet = WordNetDatabase.getFileInstance();
			
	    	String[] arrWords = FullText.split(" ");
	    	int iConceptIndex = 0;
	    	for(int i = arrWords.length - 1; i >= 0; i--)
	    	{
	    		if (arrWords[i].equalsIgnoreCase(strConcept))
	    		{
	    			iConceptIndex = i;
	    			break;
	    		}
	    	}
	    	for (int j = iConceptIndex - 1; j >= 0; j--)
	    	{
	    		String strLongerConcept = arrWords[j] + " " + strConcept;
	    		Synset[] sourceNouns = wordnet.getSynsets(strLongerConcept, SynsetType.NOUN );
	    		if (sourceNouns != null && sourceNouns.length > 0)
	    			strConcept = arrWords[j] + " " + strConcept;
	    		else
	    			break;
	    	}
	    }
	    else
	    	strConcept = FullText;
	    return strConcept;
    }*/
    
/*    private String GetPropertyConcept(String FullText)
    {
    	//for now we use the original string since the desired concept is unclear
    	//in the case of properties
    	return FullText;
    }*/
    
    private ArrayList<String> GetDescendants(Node vert)
    {
    	ArrayList<String> descendants = new ArrayList<String>();
    	for(int i = 0; i<vert.getChildCount(); i++)
    	{
    		Node n = vert.getChildAt(i);
    		descendants.add(htConcepts.get(n.getLocalName()));
    		descendants.addAll(GetDescendants(n));
    	}
    	return descendants;
    }
    
    private ArrayList<String> GetAncestors(Node n)
    {
    	ArrayList<String> ancestors = new ArrayList<String>();

    	List<Node> ancestorList = new ArrayList<Node>(n.getAncestors());
    	for( Node ancestor : ancestorList ) {
    		ancestors.add( ancestor.getLocalName() );
    	}
    	
    	return ancestors;
    }
    
    private double GetJaccardScore(ArrayList<String> List1, ArrayList<String> List2)
    {
    	ArrayList<String> UnionSet = Union(List1, List2);
		if (UnionSet.size() == 0)
			return 0;
		else
			return (double)Intersection(List1, List2).size() / (double)Union(List1, List2).size();
    }
    
    public ArrayList<String> Union(ArrayList<String> L1, ArrayList<String> L2)
	{
    	ArrayList<String> Final = new ArrayList<String>();
		for (String element : L1)
			Final.add(element);
		for (String element : L2)
			Final.add(element);
		return Final;
	}

	public ArrayList<String> Intersection(ArrayList<String> L1, ArrayList<String> L2)
	{
		//need to handle duplicates
		Hashtable<String, Integer> DupCounts1 = new Hashtable<String, Integer>();
		ArrayList<String> AlteredL1 = new ArrayList<String>();
		for (String item : L1)
		{
			if (item != null)
			{
				if (!DupCounts1.containsKey(item))
					DupCounts1.put(item, 1);
				int iCurrentCount = DupCounts1.get(item);
				AlteredL1.add(item + iCurrentCount);
				DupCounts1.put(item, iCurrentCount+1);
			}
		}
		Hashtable<String, Integer> DupCounts2 = new Hashtable<String, Integer>();
		ArrayList<String> AlteredL2 = new ArrayList<String>();
		for (String item : L2)
		{
			if (item != null)
			{
				if (!DupCounts2.containsKey(item))
					DupCounts2.put(item, 1);
				int iCurrentCount = DupCounts2.get(item);
				AlteredL2.add(item + iCurrentCount);
				DupCounts2.put(item, iCurrentCount+1);
			}
		}
		ArrayList<String> Final = new ArrayList<String>();
		for (String element : AlteredL1)
		{
			if (AlteredL2.contains(element))
				Final.add(element);
		}
		return Final;
	}

	/**
	 * @author Will Underwood
	 * @date January 18, 2009
	 * Align Two nodes using concept set similarity.
	 * @see am.app.mappingEngine.AbstractMatcher#alignTwoNodes(am.app.ontology.Node, am.app.ontology.Node)
	 */
	protected Mapping alignTwoNodes(Node source, Node target, alignType typeOfNodes) {

		
		/**
		 * @author Will Underwood
		 * @date January 18, 2009
		 * 
		 * The similarity value is (W1 * DescendantSim + W2 * AncestorSim + W3 * TextSim) / (W1 + W2 + W3)
		 * Where W1, W2, and W3 are user-defined weights
		 * To calculate DescendantSim:
		 * 		1.  D1 = set of all descendant nodes for the source node (children, grandchildren, etc.)
		 * 		2.  D2 = set of all descendant nodes for the target node
		 * 		3.  All nodes are represented by their longest defined concept (see GetClassConcept for details)
		 * 		4.  DescendantSim = JaccardSim(D1, D2) where Jaccard is the size of the set intersection divided by the size of the set union
		 * To calculate AncestorSim:
		 * 		1.  A1 = set of all ancestor nodes for the source node (parent, grandparent, etc.) plus the source node itself
		 * 		2.  A2 = set of all ancestor nodes for the target node plus the target node itself
		 * 		3.  All nodes are represented by their longest defined concept (see GetClassConcept for details)
		 * 		4.  AncestorSim = JaccardSim(A1, A2) where Jaccard is the size of the set intersection divided by the size of the set union
		 * To calculate TextSim:
		 * 		1.  length(LongestCommonSubstring(source, target)) / (length(source) + length(target))
		 */
		
		String strSourceLabel = source.getLocalName();
		String strTargetLabel = target.getLocalName();
		
		//get set of descendants for source node
		ArrayList<String> sourceDescendants = GetDescendants(source);
		ArrayList<String> targetDescendants = GetDescendants(target);
		
		//get set of ancestors
		ArrayList<String> sourceAncestors = GetAncestors(source);
		ArrayList<String> targetAncestors = GetAncestors(target);
		
		double dblDescendantScore = GetJaccardScore(sourceDescendants, targetDescendants);
		double dblAncestorScore = GetJaccardScore(sourceAncestors, targetAncestors);
		int iLCSLength = LongestCommonSubstringLength(strSourceLabel, strTargetLabel);
		double dblLCSScore = (double) iLCSLength / (strSourceLabel.length() + strTargetLabel.length());
		
		double dblAncWeight = ((ConceptMatcherParameters) param).AncestorSetWeight;
		double dblDesWeight = ((ConceptMatcherParameters) param).DescendantSetWeight;
		double dblTxtWeight = ((ConceptMatcherParameters) param).TextSimilarityWeight;
		double dblScore = (dblDescendantScore * dblDesWeight + dblAncestorScore * dblAncWeight + dblLCSScore * dblTxtWeight) / (dblDesWeight + dblAncWeight + dblTxtWeight);
		
		return new Mapping(source, target, dblScore, MappingRelation.EQUIVALENCE);
	}
	
	private int LongestCommonSubstringLength(String str1, String str2)
	{
			char[] s1 = str1.toCharArray();
			char[] s2 = str2.toCharArray();
	        int[][] num = new int[s1.length+1][s2.length+1];
	        for (int i = 1; i <= s1.length; i++)
	                for (int j = 1; j <= s2.length; j++)
	                        if (s1[i-1] == s2[j-1])
	                                num[i][j] = 1 + num[i-1][j-1];
	                        else
	                                num[i][j] = Math.max(num[i-1][j], num[i][j-1]);
	 
	       return num[s1.length][s2.length];
	}
	
	
	/**
	 * This function treats a string to make it more comparable:
	 * 1) Removes dashes and underscores
	 * 2) Separates capitalized words, ( "BaseSimilarity" -> "Base Similarity" )
	 */
	
	 public static String treatString(String s) {
		 
		 
		 String s2 = s.replace("_"," ");
		 s2 = s2.replace("-"," ");
		 s2 = s2.replace("."," ");	
	    
		 int len = s2.length();
	    for(int i=0;i<len-1; i++){
	    	if( Character.isLowerCase(s2.charAt(i)) &&  Character.isUpperCase(s2.charAt(i+1)) ){
		    
	    		s2 = s2.substring(0,i+1) + " " + s2.substring(i+1); 
	    		len++;}

		}
	    return s2;
	 }
	 
		public AbstractMatcherParametersPanel getParametersPanel() {
			if(parametersPanel == null){
				parametersPanel = new ConceptMatcherParametersPanel();
			}
			return parametersPanel;
		}
}
