package am.app.ontology;

import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;

import am.GlobalStaticVariables;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.qualityEvaluation.JoslynStructuralQuality;
import am.userInterface.vertex.Vertex;

/**
 * This class contains all information about one of the two ontologies to be compared
 * You get access to it via the Core instance
 *
 */
public class Ontology {
	
	public static final int ID_NONE = -1;  // used when there is no ontology id.
	
	/**It may be SOURCE or TARGET use the final static int values in GSM to set this*/
	private int sourceOrTarget;
	private String filename;//file name with all the path
	private String title;//usually is the name of the file without the path and is the name of the root vertex
	/**It may be XML, OWL, RDF*/
	private String language;
	/**For example RDF/XML for OWL language, in XML lanaguage is null*/
	private String format;
	/**reference to the Jena model class, for an OWL ontology it may be an OntModel, right now we don't use this element,  in XML lanaguage is null*/
	private OntModel model;
	
	/**List of class nodes to be aligned, IN THE CASE OF AN XML OR RDF ONTOLOGY ALL NODES ARE KEPT IN THIS STRUCTURE, so there will be only classes and no properties*/
	private ArrayList<Node> classesList = new ArrayList<Node>();
	/**List of property nodes to be aligned, IN THE CASE OF AN XML OR RDF ONTOLOGY there are no properties*/
	private ArrayList<Node> propertiesList = new ArrayList<Node>();
	
	/**The root of the classes hierarchy, is not the root of the whole tree but is the second node, the root vertex itself is fake doesn't refers to any node to be aligned, all sons of this node are classes to be aligned*/
	private Vertex classesTree;//in a XML or RDF ontology this will be the only tree
	/**The root of the properties hierarchy, is not the root of the whole tree but is the third node, the root vertex itself is fake doesn't refers to any node to be aligned, all sons of this node are classes to be aligned*/
	private Vertex propertiesTree;//in a XML or RDF ontology this will be null, while in a OWL ontology it contains at least the fake root "prop hierarchy"
	
	private Vertex deepRoot; // for the Canvas
	
	private boolean skipOtherNamespaces;
	
	private String URI;
	
	/**
	 * This value is not used in the AM system right now, it is only used in the Conference Track when more than two ontologies are involved in the process.
	 */
	private int Index = 0;  // TODO: Maybe get rid of index, and work only with ID?
	private int ontID = 0;  // Index is used in the conference track, ID is used system wide.
	private int treeCount;
	
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public OntModel getModel() {
		return model;
	}
	public void setModel(OntModel model) {
		this.model = model;
	}
	public ArrayList<Node> getClassesList() {
		return classesList;
	}
	public void setClassesList(ArrayList<Node> classesList) {
		this.classesList = classesList;
	}
	public ArrayList<Node> getPropertiesList() {
		return propertiesList;
	}
	public void setPropertiesList(ArrayList<Node> propertiesList) {
		this.propertiesList = propertiesList;
	}

	
	public boolean isSource() {
		return sourceOrTarget == GlobalStaticVariables.SOURCENODE;
	}
	
	public boolean isTarget() {
		return sourceOrTarget == GlobalStaticVariables.TARGETNODE;
	}
	
	public void setSourceOrTarget(int s) {
		sourceOrTarget = s;
	}
	public Vertex getClassesTree()                   { return classesTree; }
	public void   setClassesTree(Vertex classesTree) { this.classesTree = classesTree; }
	public Vertex getDeepRoot()                      { return deepRoot; }
	public void   setDeepRoot(Vertex root)           { this.deepRoot = root; }
	
	public Vertex getPropertiesTree() {
		return propertiesTree;
	}
	public void setPropertiesTree(Vertex propertiesTree) {
		this.propertiesTree = propertiesTree;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	//used in UImenu.ontologyDetails()
	public String getClassDetails() {	
		return getDetails(classesList, classesTree);
	}
	
	//used in getClassDetails and getPropDetails
	private String getDetails(ArrayList<Node> list, Vertex tree) {
		TreeToDagConverter conv = new TreeToDagConverter(tree);
		
		int concepts = list.size();
		int depth = tree.getDepth()-1;
		int roots = conv.getRoots().size();
		int leaves = conv.getLeaves().size();
		JoslynStructuralQuality q = new JoslynStructuralQuality(true, true, false); //the first two boolean dont matter here
		double LCdiameter = q.getDiameter(list, conv);
		JoslynStructuralQuality q2 = new JoslynStructuralQuality(true, true, true); //the first two boolean dont matter here
		double UCdiameter = q2.getDiameter(list, conv);
		
		return concepts+"\t"+depth+"\t"+UCdiameter+"\t"+LCdiameter+"\t"+roots+"\t"+leaves+"\n";
	}
	
	//used in UImenu.ontologyDetails()
	public String getPropDetails() {
		return getDetails(propertiesList, propertiesTree);
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
				throw new Exception("OntResource (" + r.toString() + ") cannot be found in Ontology " + ontID);
			}
		} else if( nodeType == alignType.aligningProperties ) {
			if( mapOntResource2Node_Properties.containsKey( r ) ){
				return mapOntResource2Node_Properties.get(r);	
			} else {
				throw new Exception("OntResource (" + r.toString() + ") cannot be found in Ontology " + ontID);
			}
		}
		
		throw new Exception("Cannot search for nodeType == " + nodeType.toString() );

	}
	
}
