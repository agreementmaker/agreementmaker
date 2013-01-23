package am.app.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import am.GlobalStaticVariables;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.metrics.joslyn.JoslynStructuralQuality;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.instance.InstanceDataset;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.app.ontology.ontologyParser.OntologyDefinition;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologyLanguage;
import am.app.ontology.ontologyParser.OntologyDefinition.OntologySyntax;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * This class contains all information about one of the two ontologies to be compared
 * You get access to it via the Core instance
 *
 */
public class Ontology {
	
	public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	
	public static final int ID_NONE = -1;  // used when there is no ontology id.
	
	// local title
	public static final String TARGETTITLE = "Target Ontology";
	// ontology title
	public static final String SOURCETITILE = "Source Ontology";
	//	OWL File type representation
	public static final int SOURCENODE = 0;
	//	OWL File type representation
	public static final int TARGETNODE = 1;
	public static final int XMLFILE = 2;
	public static final int OWLFILE = 1;
	public static final int RDFSFILE = 0;
	public static final int TABBEDTEXT = 3;
	//public static final int DAMLFILE = 3;
	
	/*public static final int RDFXML = 0;
	public static final int RDFXMLABBREV = 1;
	public static final int NTRIPLE = 2;
	public static final int N3  = 3;
	public static final int TURTLE = 4;*/

	public final static String SYN_RDFXML = "RDF/XML";
	public final static String SYN_RDFXMLABBREV = "RDF/XML-ABBREV";
	public final static String SYN_NTRIPLE = "N-TRIPLE";
	public final static String SYN_N3 = "N3";
	public final static String SYN_TURTLE = "TURTLE";
	public final static String[] syntaxStrings  = {SYN_RDFXML, SYN_RDFXMLABBREV, SYN_NTRIPLE, SYN_N3, SYN_TURTLE};
		
	public final static String LANG_RDFS = "RDFS";
	public final static String LANG_OWL = "OWL";
	public final static String LANG_XML = "XML";
	public final static String LANG_TABBEDTEXT = "Tabbed TEXT";
	public static final String[] languageStrings = {LANG_RDFS, LANG_OWL, LANG_XML, LANG_TABBEDTEXT};
		
	public static final int SOURCE = GlobalStaticVariables.SOURCENODE;
	public static final int TARGET = GlobalStaticVariables.TARGETNODE;
	
	// TODO: Get rid of this enum? Or move it out of this class? - Cosmin, Sept 13, 2011
	public enum DatasetType {
		ENDPOINT,
		ONTOLOGY,
		DATASET;
	}
	
	private InstanceDataset instances;
	
	public InstanceDataset getInstances() {
		return instances;
	}
	public void setInstances(InstanceDataset instances) {
		this.instances = instances;
	}

	/** 
	 * <p>It may be SOURCE or TARGET.  Use the final static int values in GSV to set this. (GlobalStaticVariables.SOURCENODE or GlobalStaticVariables.TARGETNODE)</p>
	 * <p>TODO: Change this to an enum.</p> 
	 * */
	private int sourceOrTarget;
	
	
	private String filename;//file name with all the path
	
	
	private String title;//usually is the name of the file without the path and is the name of the root vertex
	
	private OntologyLanguage language;
	private OntologySyntax format;
	
	/**reference to the Jena model class, for an OWL ontology it may be an OntModel, right now we don't use this element,  in XML lanaguage is null*/
	private OntModel model;
	
	/**List of class nodes to be aligned, IN THE CASE OF AN XML OR RDF ONTOLOGY ALL NODES ARE KEPT IN THIS STRUCTURE, so there will be only classes and no properties*/
	private List<Node> classesList = new ArrayList<Node>();
	/**List of property nodes to be aligned, IN THE CASE OF AN XML OR RDF ONTOLOGY there are no properties*/
	private List<Node> propertiesList = new ArrayList<Node>();
	
	/**The root of the classes hierarchy, is not the root of the whole tree but is the second node, the root vertex itself is fake doesn't refers to any node to be aligned, all sons of this node are classes to be aligned*/
	private Node classesRoot;//in a XML or RDF ontology this will be the only tree
	/**The root of the properties hierarchy, is not the root of the whole tree but is the third node, the root vertex itself is fake doesn't refers to any node to be aligned, all sons of this node are classes to be aligned*/
	private Node propertiesRoot;//in a XML or RDF ontology this will be null, while in a OWL ontology it contains at least the fake root "prop hierarchy"
	
	private Node deepRoot; // for the Canvas
	
	private boolean skipOtherNamespaces;
	
	private String URI;
	
	private List<DatatypeProperty> dataProperties;
	public List<DatatypeProperty> getDataProperties() {
		return dataProperties;
	}
	public void setDataProperties(List<DatatypeProperty> dtps) {
		dataProperties = dtps;
	}
	
	private List<ObjectProperty> objectProperties;
	public List<ObjectProperty> getObjectProperties() {
		return objectProperties;
	}
	public void setObjectProperties(List<ObjectProperty> ops) {
		objectProperties = ops;
	}	
	
	private HashMap<String, Node> uriMap;
	
	//END Instance related fields and functions
	
	/**
	 * This value is not used in the AM system right now, it is only used in the Conference Track when more than two ontologies are involved in the process.
	 */
	private int Index = 0;  // TODO: Maybe get rid of index, and work only with ID?
	private int ontID = 0;  // Index is used in the conference track, ID is used system wide.
	private int treeCount;

	private HashMap<OntProperty, NodeHierarchy> nodeHierachies = new HashMap<OntProperty, NodeHierarchy>();
	
	public int  getIndex()          { return Index;  }
	public void setIndex(int index) { Index = index; }
	public int  getID()             { return ontID;     }
	public void setID(int id)       { ontID = id;       }
	
	public String getURI() {
		return URI;
	}
	public void setURI(String uri) {
		URI = uri;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public OntologyLanguage getLanguage() {
		return language;
	}
	public void setLanguage(OntologyLanguage language) {
		this.language = language;
	}
	public OntologySyntax getFormat() {
		return format;
	}
	public void setFormat(OntologySyntax format) {
		this.format = format;
	}
	public OntModel getModel() {
		return model;
	}
	public void setModel(OntModel model) {
		this.model = model;
	}
	public List<Node> getClassesList() {
		return classesList;
	}
	public void setClassesList(List<Node> classesList) {
		this.classesList = classesList;
	}
	public List<Node> getPropertiesList() {
		return propertiesList;
	}
	public void setPropertiesList(List<Node> propertiesList) {
		this.propertiesList = propertiesList;
	}

	
	public boolean isSource() {
		return sourceOrTarget == Ontology.SOURCE;
	}
	
	public boolean isTarget() {
		return sourceOrTarget == Ontology.TARGET;
	}
	
	public void setSourceOrTarget(int s) {
		sourceOrTarget = s;
	}

	/** @return the root of the classes hierarchy. */
	public Node getClassesRoot()                   { return classesRoot; }
	public void   setClassesRoot(Node classesRoot) { this.classesRoot = classesRoot; }
	
	/** The deep root encompases the classes and properties hierachy */
	public Node getDeepRoot()                      { return deepRoot; }
	public void   setDeepRoot(Node root)           { this.deepRoot = root; }
	
	/** @return the root of the properties hierachy. */
	public Node getPropertiesRoot() { return propertiesRoot; }
	public void setPropertiesRoot(Node propertiesRoot) { this.propertiesRoot = propertiesRoot; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	//used in UImenu.ontologyDetails()
	public String getClassDetails() {	
		return getDetails(classesList, classesRoot);
	}
	
	// FIXME: UNIT TEST THIS METHOD!! I have a feeling that they are INCORRECT! - Cosmin.
	//used in getClassDetails and getPropDetails
	private String getDetails(List<Node> list, Node tree) {
		TreeToDagConverter conv = new TreeToDagConverter(tree);
		
		int concepts = list.size();
		int depth = tree.getDepth()-1;
		int roots = conv.getRoots().size();
		int leaves = conv.getLeaves().size();
		JoslynStructuralQuality q = new JoslynStructuralQuality(); //the first two boolean dont matter here
		q.setParameters(true, true, true);
		double LCdiameter = q.getDiameter(list, conv);
		JoslynStructuralQuality q2 = new JoslynStructuralQuality(); //the first two boolean dont matter here
		q2.setParameters(true, true, true);
		double UCdiameter = q2.getDiameter(list, conv);
		
		return concepts+"\t"+depth+"\t"+UCdiameter+"\t"+LCdiameter+"\t"+roots+"\t"+leaves+"\n";
	}
	
	//used in UImenu.ontologyDetails()
	public String getPropDetails() {
		return getDetails(propertiesList, propertiesRoot);
	}
	public boolean isSkipOtherNamespaces() {
		return skipOtherNamespaces;
	}
	public void setSkipOtherNamespaces(boolean skipOtherNamespaces) {
		this.skipOtherNamespaces = skipOtherNamespaces;
	}
	public int getSourceOrTarget() {
		
		return sourceOrTarget;
	}
	public void setTreeCount(int treeCount) { this.treeCount = treeCount; }
	public int  getTreeCount()              { return treeCount; }
	
	
	// used for mapping from OntResource to Nodes
	private HashMap<OntResource, Node> mapOntResource2Node_Classes = null;
	private HashMap<OntResource, Node> mapOntResource2Node_Properties = null;

	private String description;
	private HashMap<String, List<MatchingPair>> instanceTypeMappings;
	
	public void setOntResource2NodeMap(HashMap<OntResource, Node> processedSubs, alignType atype) {
		if( atype == alignType.aligningClasses ) {
			mapOntResource2Node_Classes = processedSubs;
		} else if( atype == alignType.aligningProperties ) {
			mapOntResource2Node_Properties = processedSubs;
		}
	}
	public Node getNodefromOntResource( OntResource r, alignType nodeType ) throws Exception {
		if( r == null ) {
			throw new Exception("Cannot search for a NULL resource.");
		}
		if( nodeType == alignType.aligningClasses ) {
			if( mapOntResource2Node_Classes.containsKey( r ) ){
				return mapOntResource2Node_Classes.get(r);	
			} else {
				throw new Exception("OntResource (" + r.toString() + ") is not a class in Ontology " + ontID + " (" + title + ").");
			}
		} else if( nodeType == alignType.aligningProperties ) {
			if( mapOntResource2Node_Properties.containsKey( r ) ){
				return mapOntResource2Node_Properties.get(r);	
			} else {
				throw new Exception("OntResource (" + r.toString() + ") is not a property in Ontology " + ontID + " (" + title + ").");
			}
		}
		
		throw new Exception("Cannot search for nodeType == " + nodeType.toString() );
	}
	
	public Node getNodefromIndex( int index, alignType aType ) throws Exception {
		if( aType == alignType.aligningClasses ) {
			if( index < classesList.size() ){
				return classesList.get(index);	
			}
		} else if( aType == alignType.aligningProperties ) {
			if( index < propertiesList.size() ){
				return propertiesList.get(index);	
			}
		}
		
		throw new Exception("Cannot search for nodeType == " + aType.toString() );
	}
	
	public Node getNodeByURI( String uri ) {
		if( uriMap == null ) createURIMap();
		return uriMap.get(uri);
	}
	
	public void setDescription(String desc) { this.description = desc; }
	public String getDescription() { return description; }
	
	public static Ontology openOntology(String fileName){
		Ontology ontology;
		try {
			OntologyDefinition odef = new OntologyDefinition();
			odef.loadOntology = false;
			odef.loadInstances = true;
			odef.instanceSourceType = DatasetType.DATASET;
			odef.instanceSourceFormat = 0;
			odef.instanceSourceFile = fileName;
			
			OntoTreeBuilder treeBuilder = new OntoTreeBuilder(odef);
			
			treeBuilder.build();
			ontology = treeBuilder.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ontology;
	}
	public void setInstanceTypeMappings(HashMap<String, List<MatchingPair>> map) {
		this.instanceTypeMappings = map; 
	}
	
	public HashMap<String, List<MatchingPair>> getInstanceTypeMapping() { return instanceTypeMappings; }
	
	/** Create the URI -> Node map. Used in getNodeByURI() method. */
	public void createURIMap() {
		uriMap = new HashMap<String, Node>();
		for( Node classNode : classesList ) {
			uriMap.put(classNode.getUri(), classNode);
		}
		for( Node propertyNode : propertiesList ) {
			uriMap.put(propertyNode.getUri(), propertyNode);
		}
	}
	
	
	public void addHierarchy( OntProperty property, NodeHierarchy hierarchy ) {
		nodeHierachies.put( property, hierarchy);
	}
	
	public NodeHierarchy getHierarchy( OntProperty property ) {
		return nodeHierachies.get(property);
	}
	
	public Set<OntProperty> getHierarchyProperties() {
		return nodeHierachies.keySet();
	}
	public Node containsClassLocalName(String name) {
		for (int i = 0; i < classesList.size(); i++) {
			if(classesList.get(i).getLocalName().equals(name))
				return classesList.get(i);
		}
		return null;
	}
}
