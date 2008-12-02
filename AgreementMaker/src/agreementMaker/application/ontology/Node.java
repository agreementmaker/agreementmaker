package agreementMaker.application.ontology;

import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.XML;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import agreementMaker.userInterface.vertex.*;
/**
 * This class represents an element of the ontology to be aligned.
 * we could use the Resource class of Jena directly, but accessing information would be slower
 * so we use our own structure, even though we keep the reference to the Jena structure
 *
 */
public class Node {
	
	/**This is the reference of the Resource in the ontology, this is a Jena class, when parsing an XML ontology there will be no resource	
	 *  the node is built taking information from this resource
	 *  if we have loaded an OWL ontology this resource will be instance of OntResource
	 *  if this node is a ClassNode, the resource will be instance of OntClass
	 *  Right now we don't need this information but we may use it in the future to access any other information in the ontology connected to this one
	 *   */
	private Resource resource;
	/**The OWL/RDF uri identifier, that is namespace#localname, all these informations are kept into the resource attribute but we keep them separate to access them easily*/
	private String uri = "";
	/**
	 * One of the string which can be used to compare two nodes
	 * In general the URI = namespace#localname, in the OWL ontology, often the localname is the same of label	
	 * usually is defined with rdf: ID */
	private String localName = "";
	/**
	 * Another string which can be used to compare nodes
	 * This should be a human readable version of localname. In a RDF or XML ontologies there are no labels
	 * In OWL ontology we can have a label, even though often is the same of name.
	 * rdfs:label
	 */
	private String label = "";
	/**
	 * Another string which can be used to compare nodes
	 * This is a longer description (more than one word) for this resource.In a RDF or XML ontologies there are no comments
	 * In OWL ontology we can have a comment.
	 * rdfs:comment
	 */
	private String comment = "";
	//SOME MORE INFORMATIONS THAT MY BE USED
	private String isDefinedBy = "";
	private String seeAlso = "";
	private ArrayList<String> propertiesLocalNames = new ArrayList<String>();
	private ArrayList<String> individuals = new ArrayList<String>();
	
	
	/**
	 * A Class or Property in a OWL hierarchy may have more than one father. In this case all nodes in the subtree of the node with more fathes will be represented with duplicates in the hierarchy tree
	 * but it will be aligned only once. We won't be able to access the vertex of this node with getVertex(), because this node is represetend by more than one vertex
	 * so we will have to scan a the list of vertex of this node, unless we want to access any of his vertex.
	 *List of vertex representing the node in the graphical tree hierarchy
	 * Usually each node is represeted only by one vertex, but if the node hasDuplicate, it's represented more times
	 */
	private ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
	/**RDF-node, OWL-classnode, OWL-propnode, OWL-unsatconcept, XML-node*/
	private String type;
	public final static String RDFNODE = "rdf-node";
	public final static String OWLCLASS = "owl-classnode";
	public final static String OWLPROPERTY = "owl-propertynode";
    public final static String XMLNODE = "xml-node";
	/**UNIQUE KEY IN THE RANGE OF TYPE to be used as index to retrieve this node from the list and from the matrix*/
	int index;
	/**
	 * XML constructor
	 * @param key
	 * @param name
	 * @param desc
	 * @param type
	 */
	public Node(int key, String name, String desc, String type) {
		localName = name;
		this.type = type;
		comment = desc;
		index = key;
	}
	
	/**RDF OWL Constructor*/
	public Node(int key, Resource r, String type) {
		resource = r;
		uri = r.getURI();
		localName = r.getLocalName();
		this.type = type;
		if(r.canAs(OntResource.class)) {
			OntResource or = (OntResource)r.as(OntResource.class);
			label = or.getLabel(null);//null because i don't know if it should be "EN" or "FR"
			if(label == null)
				label = "";
			//COmments
			ExtendedIterator it = or.listComments(null);
			comment = "";
			Literal l = null;
			while(it.hasNext()) {
				l = (Literal)it.next();
				if(l!=null) comment+= l+" ";
			}
			//ANNOTATIONS: isDefBy and seeAlso I'm not considering "sameAs" "differentFrom" "disjointWith"
			it = or.listIsDefinedBy();
			isDefinedBy = "";
			l = null;
			while(it.hasNext()) {
				l = (Literal)it.next();
				if(l!=null) isDefinedBy+= l+" ";
			}

			it = or.listSeeAlso();
			seeAlso = "";
			l = null;
			while(it.hasNext()) {
				l = (Literal)it.next();
				if(l!=null) seeAlso+= l+" ";
			}
			//properties and invidviduals lists only for classes
			if(!or.canAs(OntProperty.class)) {//remember is important to check on prop instead of class to avoid a jena bug that a prop canAs ontClass
				OntClass cls = (OntClass)or.as(OntClass.class);
				it = cls.listDeclaredProperties(true);
				String localname;
				while(it.hasNext()) {
					OntProperty op = (OntProperty)it.next();
					localname = op.getLocalName();
					propertiesLocalNames.add(localname);
				}
				it = cls.listInstances(true);
				while(it.hasNext()) {
					Individual ind = (Individual)it.next();
					localname = ind.getLabel(null);
					if(localname != null && !localname.equals(""))
						individuals.add(localname);
				}
			}
		}
		index = key;
	}
	
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	public String getLocalName() {
		return localName;
	}
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public boolean hasDuplicates() {
		return vertexList.size() > 1;
	}

	public ArrayList<Vertex> getVertexList() {
		return vertexList;
	}
	
	public void addVertex(Vertex v) {
		 vertexList.add(v);
	}
	
	public Vertex getVertex() {
		if(hasDuplicates()) {
			System.out.println("WARNING:it's represented by more than one vertex, use this method only if need informations that are the same in each Vertex");
		}
		return vertexList.get(0);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	/**Owl classes or all rdf nodes or all xml nodes their are considered classes, so nodes in the first of the two trees*/
	public boolean isClass() {
		return type.equals(OWLCLASS) || type.equals(RDFNODE) ||  type.equals(XMLNODE);
	}
	
	public boolean isProp() {
		return type.equals(OWLPROPERTY);
	}
	
	public boolean equals(Object o) {
		if(o instanceof Node) {
			Node n = (Node)o;
			return n.getIndex() == index;
		}
		return false;
	}
	
	public int hashCode() {
		return index;
	}
	
	
	public String toString() {
		return index+" "+localName;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getIsDefinedBy() {
		return isDefinedBy;
	}

	public void setIsDefinedBy(String isDefinedBy) {
		this.isDefinedBy = isDefinedBy;
	}

	public String getSeeAlso() {
		return seeAlso;
	}

	public void setSeeAlso(String seeAlso) {
		this.seeAlso = seeAlso;
	}

	public ArrayList<String> getPropertiesLocalNames() {
		return propertiesLocalNames;
	}

	public ArrayList<String> getIndividuals() {
		return individuals;
	}

	public String getDescriptionsString() {
		String result = "";
		result+= "URI identifier: (namespace#localname)\n";
		result += uri+"\n\n";
		
		result += "Label: (rdfs:label)\n";
		result += label+"\n\n";
		
		return result;
	}

	public String getPropertiesString() {
		String result = "List of properties:\n\n";
		if(propertiesLocalNames.size() == 0) {
			result+="No property relations found for this class";
		}
		for(int i = 0; i < propertiesLocalNames.size(); i++) {
			result+= propertiesLocalNames.get(i)+"\n";
		}
		return result;
	}
	

	public String getAnnotationsString() {
		String result = "";
		result+= "Comments: (rdfs:comment)\n";
		result += comment+"\n\n";
		
		result += "SeeAlso: (owl:seeAlso)\n";
		result += seeAlso+"\n\n";
		
		result += "IsDefinedBy: (owl:isDefinedBy)\n";
		result += isDefinedBy+"\n\n";
		return result;
	}

	public String getIndividualsString() {
		String result = "List of individuals:\n\n";
		if(individuals.size() == 0) {
			result+="No instances found for this class";
		}
		for(int i = 0; i < individuals.size(); i++) {
			result+= individuals.get(i)+"\n";
		}
		return result;
	}
	
	//**********************Methods for printing*************************
	
	

}
