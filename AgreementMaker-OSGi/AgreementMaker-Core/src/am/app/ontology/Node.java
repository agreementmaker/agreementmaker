package am.app.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import am.userInterface.ontology.OntologyConceptGraphics;

import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;

public interface Node extends Comparable<Node> {

	//void setResource(Resource res);
	Resource getResource();

	String getLocalName();

	int getParentCount();

	List<Node> getParents();
	List<Node> getChildren();
	
	int getLevel();

	int getIndex();

	Set<Node> getAncestors();
	Set<Node> getDescendants();

	boolean isLeaf();

	int getOntologyID();

	boolean isClass();

	boolean isProp();

	String getType();

	String getUri();

	Node getRoot();

	boolean hasDescendant(Node n);

	int getDepth();
	int getMaxDepth();

	ArrayList<Node> getClassProperties();

	OntResource getPropertyDomain();
	OntResource getPropertyRange();

	// TODO: This should be in a separate interface.
	void addGraphicalRepresentation(OntologyConceptGraphics ocg);

	OntologyConceptGraphics getGraphicalRepresentation(Class<?> c);

	String getLabel();

	// Used in VertexDescriptionPane
	// FIXME: Remove these.
	String getPropOrClassString();
	String getAnnotationsString();
	String getDescriptionsString();
	String getIndividualsString();

	int getChildCount();

	void addChild(Node classRoot);

	void addParent(Node root);

	void setIndex(int i);
	void setLabel(String string);
	void setComment(String des);
	void setSeeAlsoLabel(String seeAlso);
	void setIsDefinedByLabel(String isDefBy);
	Node getChildAt(int i);
	boolean isRoot();
	List<String> getIndividuals();
	List<String> getpropOrClassNeighbours();
	String getComment();
	String getSeeAlsoLabel();
	String getIsDefinedByLabel();
}
