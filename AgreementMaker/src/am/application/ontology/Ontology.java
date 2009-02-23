package am.application.ontology;

import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;

import am.GlobalStaticVariables;
import am.application.mappingEngine.qualityEvaluation.JoslynStructuralQuality;
import am.userInterface.vertex.Vertex;

/**
 * This class contains all information about one of the two ontologies to be compared
 * You get access to it via the Core instance
 *
 */
public class Ontology {
		
	/**It may be SOURCE or TARGET use the final static int values in GSM to set this*/
	private int sourceOrTarget;
	private String filename;//file name with all the path
	private String title;//usually is the name of the file without the path and is the name of the root vertex
	/**It may be XML, OWL, RDF*/
	private String language;
	/**For example RDF/XML for OWL language, in XML lanaguage is null*/
	private String format;
	/**reference to the Jena model class, for an OWL ontology it may be an OntModel, right now we don't use this element,  in XML lanaguage is null*/
	private Model model;
	
	/**List of class nodes to be aligned, IN THE CASE OF AN XML OR RDF ONTOLOGY ALL NODES ARE KEPT IN THIS STRUCTURE, so there will be only classes and no properties*/
	private ArrayList<Node> classesList = new ArrayList<Node>();
	/**List of property nodes to be aligned, IN THE CASE OF AN XML OR RDF ONTOLOGY there are no properties*/
	private ArrayList<Node> propertiesList = new ArrayList<Node>();
	
	/**The root of the classes hierarchy, is not the root of the whole tree but is the second node, the root vertex itself is fake doesn't refers to any node to be aligned, all sons of this node are classes to be aligned*/
	private Vertex classesTree;//in a XML or RDF ontology this will be the only tree
	/**The root of the properties hierarchy, is not the root of the whole tree but is the third node, the root vertex itself is fake doesn't refers to any node to be aligned, all sons of this node are classes to be aligned*/
	private Vertex propertiesTree;//in a XML or RDF ontology this will be null, while in a OWL ontology it contains at least the fake root "prop hierarchy"
	
	private boolean skipOtherNamespaces;
	
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
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
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
	public Vertex getClassesTree() {
		return classesTree;
	}
	public void setClassesTree(Vertex classesTree) {
		this.classesTree = classesTree;
	}
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
		JoslynStructuralQuality q = new JoslynStructuralQuality();
		double LCdiameter = q.getLCDiameter(list, conv);
		
		return concepts+"\t"+depth+"\t"+LCdiameter+"\t"+roots+"\t"+leaves+"\n";
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
	
}
