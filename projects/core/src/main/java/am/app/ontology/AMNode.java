package am.app.ontology;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents an element of the ontology to be aligned.
 * we could use the Resource class of Jena directly, but accessing information would be slower
 * so we use our own structure, even though we keep the reference to the Jena structure
 *
 */
public class AMNode extends AbstractNode implements Serializable {
	
	private static final long serialVersionUID = -7629984078559964658L;

	private static final Logger LOG = LogManager.getLogger(AMNode.class);
	
	/** The OWL/RDF uri identifier, that is namespace#localname.
	 * This info is kept in the resource variable but we keep them separate to access them easily. TODO - Should we really be duplicating this information? - Cosmin.
	 */
	private String uri = ""; // GET RID OF THIS! - Cosmin.

	/**
	 * Another string which can be used to compare nodes
	 * This should be a human readable version of localname. In a RDF or XML ontologies there are no labels
	 * In OWL ontology we can have a label, even though often is the same of name.
	 * rdfs:label
	 */
	private transient String label = null;
	/**
	 * Another string which can be used to compare nodes
	 * This is a longer description (more than one word) for this resource.In a RDF or XML ontologies there are no comments
	 * In OWL ontology we can have a comment.
	 * rdfs:comment
	 */
	private transient String comment = null;
	
	//SOME MORE INFORMATIONS THAT MY BE USED -- // TODO - Avoid duplication of information.  All the info can be gotten from the Jena Resource. - Cosmin.
	private transient String isDefinedByLabel = null;
	private transient String isDefinedByURI = null;
	private transient String isDefinedByComment = null;
	private transient String seeAlsoLabel = null;
	private transient String seeAlsoURI = null;
	private transient String seeAlsoComment = null;
	
	/** If the node is a prop node then it this list contains the list of classes localnames which declare this property.
		If the node is a class node then this list contains the list of properties declared by this class.
	*/
	private transient ArrayList<String> propOrClassNeighbours = new ArrayList<String>();
	
	/**
	 * A list of the individuals associated with this node.
	 */
	private transient ArrayList<String> individuals = new ArrayList<String>();
	
	
	/**
	 * A Class or Property in a OWL hierarchy may have more than one father. In this case all nodes in the subtree of the node with more fathes will be represented with duplicates in the hierarchy tree
	 * but it will be aligned only once. We won't be able to access the vertex of this node with getVertex(), because this node is represetend by more than one vertex
	 * so we will have to scan a the list of vertex of this node, unless we want to access any of his vertex.
	 *List of vertex representing the node in the graphical tree hierarchy
	 * Usually each node is represeted only by one vertex, but if the node hasDuplicate, it's represented more times
	 */
	//private transient ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
	/**RDF-node, OWL-classnode, OWL-propnode, OWL-unsatconcept, XML-node*/
	private transient String type;
	public final static String RDFNODE = "rdf-node";
	public final static String OWLCLASS = "owl-classnode";
	public final static String OWLPROPERTY = "owl-propertynode";
    public final static String XMLNODE = "xml-node";
	/**UNIQUE KEY IN THE RANGE OF TYPE to be used as index to retrieve this node from the list and from the matrix*/
	protected transient int index;
	protected transient int ontID;  // the ID of the ONTOLOGY to which this node belongs

	private transient int color;
	private transient List<Node> parents = new ArrayList<Node>();
	private transient List<Node> children = new ArrayList<Node>();
	
	/***************************************** METHODS *************************************************/
	
	/**
	 * XML constructor
	 * @param key
	 * @param name
	 * @param desc
	 * @param type
	 */
	public AMNode(Resource res, int key, String name, String type, int ontID) {
		super(res);
		this.type = type;
		index = key;
		this.ontID = ontID;
	}
	
	public AMNode(Node a){
		super(a.getResource());
		type = a.getType();
		index = a.getIndex();
		//String language = "EN";
		uri = a.getUri();
		//vertexList.addAll(a.getVertexList());
		ontID = a.getOntologyID();
	}
	
	/**RDF OWL Constructor*/
	public AMNode(int key, Resource r, String type, int ontID ) {
		super(r);
		final String language = "EN";
		uri = r.getURI();
		this.ontID = ontID;
		this.type = type;
		if(r.canAs(OntResource.class)) {
			OntResource or = (OntResource)r.as(OntResource.class);
			
			// Set the label. (Why are we duplicating this information?)
			label = or.getLabel(language);
			if(label == null || label == "")
				label = or.getLabel(null);
			if(label == null)
				label = "";

			ExtendedIterator<RDFNode> commentsIter = or.listComments(language);
			if(!commentsIter.hasNext() ) commentsIter = or.listComments(null);
			
			while( commentsIter.hasNext() ) {
				final Literal l = (Literal) commentsIter.next();
				if( l != null ) comment += l + " ";
			}

			//ANNOTATIONS: isDefBy and seeAlso I'm not considering "sameAs" "differentFrom" "disjointWith"
			ExtendedIterator<RDFNode> definedByIter = or.listIsDefinedBy();
			
			OntResource or2;
			if(definedByIter.hasNext()) {
				
				RDFNode ol = (RDFNode) definedByIter.next();
				
				//OntResourceImpl ol = (OntResourceImpl)it.next();
				
				if(ol!= null && ol.canAs(OntResource.class)){
					or2 = (OntResource)ol.as(OntResource.class);
					isDefinedByLabel = or2.getLabel(language);
					if(isDefinedByLabel == null || isDefinedByLabel == "")
						isDefinedByLabel = or2.getLabel(null);
						if(isDefinedByLabel == null)
							isDefinedByLabel = "";
					isDefinedByComment = or2.getComment(language);
					if(isDefinedByComment == null || isDefinedByComment == "")
						isDefinedByComment = or2.getLabel(null);
						if(isDefinedByComment == null)
							isDefinedByComment = "";
					isDefinedByURI = or2.getURI();
				} else if( ol != null &&  ol.canAs(Literal.class) ) {
					Literal l1 = (Literal) ol.as(Literal.class);
					isDefinedByLabel = l1.toString();
				}
			}

			ExtendedIterator<RDFNode> seeAlsoIter = or.listSeeAlso();

			if(seeAlsoIter.hasNext()) {
				
				RDFNode ol = (RDFNode)seeAlsoIter.next();
				if(ol!= null && ol.canAs(OntResource.class)){
					or2 = (OntResource)ol.as(OntResource.class);
					seeAlsoLabel = or2.getLabel(language);
					if(seeAlsoLabel == null || seeAlsoLabel == "")
						seeAlsoLabel = or2.getLabel(null);
						if(seeAlsoLabel == null)
							seeAlsoLabel = "";
					seeAlsoComment = or2.getComment(language);
					if(seeAlsoComment == null || seeAlsoComment == "")
						seeAlsoComment = or2.getLabel(null);
						if(seeAlsoComment == null)
							seeAlsoComment = "";
						seeAlsoURI = or2.getURI();
				} else if( ol != null &&  ol.canAs(Literal.class) ) {
					Literal l1 = (Literal) ol.as(Literal.class);
					seeAlsoLabel = l1.toString();
				}
			}
			//properties and invidviduals lists only for classes
			if(!or.canAs(OntProperty.class)) {//remember is important to check on prop instead of class to avoid a jena bug that a prop canAs ontClass
				try {
					OntClass cls = (OntClass)or.as(OntClass.class);
					ExtendedIterator<OntProperty> propIter = cls.listDeclaredProperties(true);

					while(propIter.hasNext()) {
						OntProperty op = (OntProperty)propIter.next();
						if(!op.isAnon()) {
							final String localname = op.getLocalName();
							propOrClassNeighbours.add(localname);
						}
	
					}
					ExtendedIterator<? extends OntResource> instanceIter = cls.listInstances(true);
					while(instanceIter.hasNext()) {
						Individual ind = (Individual)instanceIter.next();
						final String localname = ind.getLabel(null);
						if(localname != null && !localname.equals(""))
							individuals.add(localname);
					}
				}
				catch ( Exception e ) {
					LOG.error("Cannot determine neighbors/individuals of the OntClass.", e);
				}
			}
			else {
				try{ 
					OntProperty prop = (OntProperty)or.as(OntProperty.class);
					ExtendedIterator<? extends OntResource> classesIter = prop.listDeclaringClasses(true);
					while(classesIter.hasNext()) {
						OntClass op = (OntClass)classesIter.next();
						if(!op.isAnon()) {
							final String localname = op.getLocalName();
							propOrClassNeighbours.add(localname);
						}
					}
				} catch ( Exception e ) {
					LOG.error("Cannot determine neighbor nodes of the OntProperty.", e);
				}
			}
		}
		index = key;
	}
	
	@Override
	public Resource getResource() { return resource; }
	
	@Override
	public String getLocalName() { 
		if( resource == null ) return null; // cannot use Jena API.
		return resource.getLocalName();
	}
	
	/**
	 * @return The label of this node.  null if a label is not defined.
	 */
	public String getLabel() { 
		if( label != null ) return label;
		
		// if a label has not been already set, use the Jena API.
		return getPropertyLiteral(RDFS.label);
	}
	
	/**
	 * Set the label of this node. This method is used for compatability with
	 * ontology formats not recognized by Jena.
	 * 
	 * @param label
	 */
	public void setLabel(String label) { this.label = label; }
	
	public String getComment() {
		if( comment != null ) return comment;
		
		// if a comment has not been set already, use the jena API.
		return getPropertyLiteral(RDFS.comment);
	}
	
	public void setComment(String comment) { this.comment = comment; }
		
	/**
	 * @param property
	 *            A Jena datatype property. If this is not a datatype property,
	 *            behavior is undefined.
	 * @return The string value of the literal defined by the datatype property.
	 */
	public String getPropertyLiteral( Property property ) {
		if( resource == null ) return null; // no resource, cannot use Jena API.
		
		Statement st = resource.getProperty(property);
		if( st == null ) {
			return null;
		}
		
		return st.getObject().asLiteral().getString();
	}
	
	/*@Deprecated
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
		if( vertexList != null && !vertexList.isEmpty() )
			return vertexList.get(0);
		else
			return null;
	}*/

	public int getIndex() { return index; }
	public void setIndex(int index) { this.index = index; }
	
	/** @return The ID of the ontology to which this Node belongs. */
	@Override
	public int getOntologyID() { return ontID; }
	
	/**Owl classes or all rdf nodes or all xml nodes their are considered classes, so nodes in the first of the two trees*/
	public boolean isClass() {
		if( resource == null ) {
			//System.out.println("Null resource in Node object: " + this);
			return false;
		}
		return resource.canAs(OntClass.class);
		//return false;
	}
	
	public boolean isProp() {
		if( resource == null ) {
			//System.out.println("Null resource in Node object: " + this);
			return false;
		}
		if( resource.canAs(OntProperty.class) ) return true;
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof Node ) {
			return equals((Node)obj);
		}
		else 
			return false;
	}
	
	// equality checker --- VERY IMPORTANT
	public boolean equals( Node o) {

		/**
		 *  For a node to be equal to a second node
		 *  they must both be
		 *  1. of equal type (classes, properties, individuals )
		 *  2. of equal ontology ( source ontology nodes can only be equal with source ontology nodes, and the same for target ontology)
		 *  3. of equal index  ( the index is unique, and if the other two points are equal, equality of indices will indicate equality )
		 */
 

		// 1. Equal Type
		if( o.isProp() != isProp() ) return false;
		if( o.isClass() != isClass() ) return false;
		
		// 2. equal ontology
		// we use ontology indices here, if the indices are equal, the nodes are from the same ontology
		if( o.getOntologyID() != ontID ) return false;
		
		// 3. equal index
		// because the nodes we are comparing are from the same ontology, we can now check for index equality
		if( o.getIndex() != index ) return false;
		
		return true;
	}
	/*
	public int hashCode() {
		return index;
	}
	*/
	
	public String toString() {
		String result = Integer.toString(index);
		if(label != null && label.length() > 0){
			result+= " "+label;
		}
		else{
			result += " "+ getLocalName();
		}
		return result;
	}

	@Override
	public String getUri() {
		if( uri != null ) return uri;
		else if( getResource() != null ) {
			return getResource().getURI();
		}
		return null;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getIsDefinedByLabel() {
		return isDefinedByLabel;
	}

	public void setIsDefinedByLabel(String isDefinedBy) {
		this.isDefinedByLabel = isDefinedBy;
	}

	public String getSeeAlsoLabel() {
		return seeAlsoLabel;
	}
	
	public void setSeeAlsoLabel(String seeAlso) {
		this.seeAlsoLabel = seeAlso;
	}

	public String getSeeAlsoURI() {
		return seeAlsoURI;
	}
	
	/**
	 * Return the domain of a property as an OntResource. As per the Jena specifications, if there are more than one domains, return an arbitrary domain out of them.
	 * @return OntResource representation of the domain, or null if none exists.  
	 */
	public OntResource getPropertyDomain() {
		if( isProp() ) {
			try {
				if( resource.canAs(ObjectProperty.class) ) {
					ObjectProperty p = (ObjectProperty) resource.as(ObjectProperty.class);
					OntResource dom = p.getDomain();
					return dom;
				}
				
				if( resource.canAs(DatatypeProperty.class) ) {					
					DatatypeProperty p = (DatatypeProperty) resource.as(DatatypeProperty.class);
					OntResource dom = p.getDomain();
					return dom;
				}
				
				if( resource.canAs(AnnotationProperty.class) ) {
					AnnotationProperty p = (AnnotationProperty) resource.as(AnnotationProperty.class);
					OntResource dom = p.getDomain();
					return dom;
				}
			} catch( Exception e ) {
				// cannot find the domain of this property.
				//e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	/**
	 * Return the domain of a property as an OntResource. As per the Jena specifications, if there are more than one domains, return an arbitrary domain out of them.
	 * @return OntResource representation of the domain, or null if none exists.  
	 */
	public OntResource getPropertyRange() {
		if( isProp() ) {
			try {
				if( resource.canAs(ObjectProperty.class) ) {
					ObjectProperty p = (ObjectProperty) resource.as(ObjectProperty.class);
					OntResource dom = p.getRange();
					return dom;
				}
				
				if( resource.canAs(DatatypeProperty.class) ) {					
					DatatypeProperty p = (DatatypeProperty) resource.as(DatatypeProperty.class);
					OntResource dom = p.getRange();
					return dom;
				}
				
				if( resource.canAs(AnnotationProperty.class) ) {
					AnnotationProperty p = (AnnotationProperty) resource.as(AnnotationProperty.class);
					OntResource dom = p.getRange();
					return dom;
				}
			} catch( Exception e ) {
				// cannot find the domain of this property.
				//e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	
	/**
	 * TODO: This method should throw and exception if called on a property Node.
	 * @return the properties of a class.
	 */
	@Override
	public ArrayList<Node> getClassProperties() {

		if (isClass()) {
			ArrayList<Node> returnList = new ArrayList<Node>();

			Ontology currentOntology = Core.getInstance().getOntologyByIndex(
					ontID);
			OntClass thisclass = (OntClass) resource.as(OntClass.class);
			StmtIterator sIter = thisclass.listProperties();

			while (sIter.hasNext()) {
				Statement currentStatement = sIter.nextStatement();
				//System.out.println("getClassProperties: " + currentStatement+"\n");

				Property property = (Property) currentStatement.getPredicate()
						.as(Property.class);
			
				if (property.isAnon())
					continue; // skip anonymous property values (for now)

				if (property.isLiteral())
					continue; // skip literal values (since there is no Node
								// representation for them)

				if (property.canAs(OntResource.class)) { // need OntResource to map to Node.
					OntResource c = (OntResource) property.as(OntResource.class);
					Node n = null;
					try {
						n = currentOntology.getNodefromOntResource(c, alignType.aligningClasses);
					} catch (Exception e) {
						// if this OntResource does not have a Node
						// representation, go on to the next one.
						//e.printStackTrace();
						continue;
					}
					
					returnList.add(n);
						
					
				}
			}

			return returnList;
		}
		return null;
	}
	
	public ArrayList<String> getpropOrClassNeighbours() {
		return propOrClassNeighbours;
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

	public String getPropOrClassString() {
		String result = "Properties or Classes declared by this concept:\n\n";
		if(propOrClassNeighbours.size() == 0) {
			result+="The list is empty";
		}
		for(int i = 0; i < propOrClassNeighbours.size(); i++) {
			result+= propOrClassNeighbours.get(i)+"\n";
		}
		return result;
	}
	

	/**
	 * Do not use this for matching.  More used for display.
	 * @return
	 */
	public String getAnnotationsString() {
		String result = "";
		result+= "Comments: (rdfs:comment)\n";
		result += comment+"\n\n";
		
		result += "SeeAlso: (owl:seeAlso)\n";
		result += seeAlsoLabel+"\n\n";
		
		result += "IsDefinedBy: (owl:isDefinedBy)\n";
		result += isDefinedByLabel+"\n\n";
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
	
	
	//**********************Methods for managing the node as a DAG node see also TreeToDagConverter********************************************
	//TO BE DONE isRoot, getSiblings, getParents, getAllDescendants, getAllAncestors, getSiblingsOfAParent(Node parent
	/**
	 * NOTE: This method computes the deepest level of a node.  This only makes a difference for concepts that have multiple parents.
	 */
	public int getLevel() {
		int level = 0;
		Node n = this;
		boolean isRoot = ( n.getIndex() == -1 );
		if( !isRoot ) {
			int deepestLevel = 0;
			for( int i = 0; i < n.getParentCount(); i++ ) {
				Node currentParent = (Node) n.getParents().get(i);
				int currentLevel = currentParent.getLevel() + 1;
				if( currentLevel > deepestLevel ) deepestLevel = currentLevel;
			}
			return deepestLevel;
		}
		
		return level;
	}
	
	public boolean isLeaf() {
		return children == null || children.size() == 0;
	}
	
	/**
	 * FIXME: There should be a better way to find out if the node is the root node or not.
	 */
	public boolean isRoot() {
		if( index == -1 ) return true; // roots have an index of -1
		return false;
	}
	
	/** @return A list of all the leaf nodes in the hierarchy under this node. */
	public List<Node> getLeaves() { return NodeUtility.getLeaves(this); }
	
	/** Return a list of the descentants of this node.  Nodes are not duplicated. */
	public Set<Node> getDescendants() {
		Set<Node> descendants = new HashSet<Node>(); // using a hashset to avoid duplicates. 
		
		for( int i = 0; i < children.size(); i++ ) {
			Node currentChild = children.get(i);
			descendants.add(currentChild);
			descendants.addAll(currentChild.getDescendants());
		}
		
		return descendants;
	}
	
	/** @return the ancestors of this node, not including the node itself. Nodes are not duplicated. */
	public Set<Node> getAncestors() {
		Set<Node> ancestors = new HashSet<Node>();
		
		if( getIndex() == -1 ) return ancestors;
		
		for( int i = 0; i < parents.size(); i++ ) {
			Node currentParent = parents.get(i);
			if( currentParent.getIndex() != -1 ) {
				ancestors.add( currentParent );
				ancestors.addAll( currentParent.getAncestors() );
			}
		}
		
		return ancestors;
	}
	
	
	
/*	// TODO: This is a bug fix for getChildren().  Will replace the getChildren() with this one once it has been confirmed.
	public static ArrayList<Node> getChildren(Node n) {
		ArrayList<Node> result = new ArrayList<Node>();
		
		ArrayList<Vertex> vertexList = n.getVertexList();
		for( Vertex v : vertexList ) {
			if( !v.isLeaf() ) {
				Enumeration<Vertex> c = v.children();
				while( c.hasMoreElements() ) {
					Vertex child = (Vertex)c.nextElement();
					result.add(child.getNode());
				}
			}
		}
		return result;
	}*/
	
	
	public void addChild(Node child) { 
		if( !children.contains(child) ) {
			children.add(child);
			//Collections.sort(children, new NodeNameComparator());  // why is this call here?
		}
		
	}
	public List<Node> getChildren() { return children; }
	public int getChildCount() { if( children == null ) return 0; return children.size(); }
	public Node getChildAt(int i) { return children.get(i); }
	public void addParent(Node parent) {
		if( parent.getIndex() < 0 ) return; // do not add the fake nodes.
		if( !parents.contains(parent) ) parents.add(parent);
	}
	@Override
	public List<Node> getParents(){	return parents;	}
	public int getParentCount() { if( parents == null ) return 0; return parents.size(); }
	
	/**
	 * getRoot(): return the root node of the calling Node
	 * @author michele
	 */
	public Node getRoot(){
		//System.out.println(this + " " + this.isRoot());
		if(!this.isRoot()){
			if( this.getParentCount() != 0 ) {
				return this.getParents().get(0).getRoot();
			}
			else {
				return this;
			}
		}
		return this;
	}
	
	/** @return True if Node n is a descendant of this node. */
	public boolean hasDescendant(Node n){
		
		for( int i = 0; i < children.size(); i++ ) {
			Node currentChild = children.get(i);
			if( currentChild.equals(n) ) return true;
			boolean childDescendant = currentChild.hasDescendant(n);
			if( childDescendant ) return true;
		}		
		return false;
	}

	public String getIsDefinedByURI() { return isDefinedByURI; }

	public void setIsDefinedByURI(String isDefinedByURI) { this.isDefinedByURI = isDefinedByURI; }

	public String getIsDefinedByComment() { return isDefinedByComment; }

	public void setIsDefinedByComment(String isDefinedByComment) { this.isDefinedByComment = isDefinedByComment; }


	/**
	 * @param color the color to set
	 * FIXME: This does not belong here, but in OntologyConceptGraphics.
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	public int getDepth() {
		return getMaxDepth();
	}
	
	/**
	 * @return the maximum depth
	 */
	public int getMaxDepth() {
		int maxDepth = 0;
		for( Node parent : getParents() ) {
			int depth = parent.getMaxDepth() + 1;
			if( maxDepth < depth ) maxDepth = depth;
		}
		return maxDepth;
	}
	
	/**
	 * method created for the Mappings Candidate Selection in the User Feedback Loop
	 * @return index + label, if label is meaningful else index + localname
	 */
/*	public String getCandidateString() {
		String result = index+"";
		if(label != null && label.length() > 0){
			result+= " "+label;
		}
		else{
			result += " "+localName;
		}
		return result;
	}*/


	/** ****************** Serialization methods *******************/
	
	  /**
	   * readObject: gets the state of the object.
	   * @author michele
	   */
	  protected AMNode readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		  AMNode thisClass = (AMNode) in.readObject();
		  in.close();
		  return thisClass;
	  }

	   /**
	    * writeObject: saves the state of the object.
	    * @author michele
	    */
	  protected void writeObject(ObjectOutputStream out) throws IOException {
		  out.writeObject(this);
		  out.close();
	  }

	  protected void testSerialization(){
		  AMNode n = null;
			try {
				writeObject(new ObjectOutputStream(new FileOutputStream("testFile")));
				n = readObject(new ObjectInputStream(new FileInputStream("testFile")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			LOG.info(n.getLocalName());
	  }

	  @Override
	  public String getType() {  return type;  }

	  @Override
	  public int compareTo(Node n) {
		  if( this.getResource() == null ) {
			  if( n.getResource() == null )
				 
				  return uri.compareTo(n.getUri());
			  else
				  return uri.compareTo(n.getResource().getURI());
		  }
		  return this.getResource().getURI().compareTo(n.getResource().getURI());
	  }

	

}
