package am.app.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import am.userInterface.ontology.OntologyConceptGraphics;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;

public class JenaBackedOntClassNode implements Node {

	private OntClass jenaObject;
	
	public JenaBackedOntClassNode(OntClass cls) {
		jenaObject = cls;
	}

	@Override
	public String getLocalName() {
		return jenaObject.getLocalName();
	}

	@Override
	public int compareTo(Node arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Resource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setResource(Resource owlThing) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getParentCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Node> getParents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Node> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<Node> getAncestors() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Node> getDescendants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getOntologyID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isClass() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUri() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasDescendant(Node n) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getDepth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxDepth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<Node> getClassProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntResource getPropertyDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OntResource getPropertyRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addGraphicalRepresentation(OntologyConceptGraphics ocg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OntologyConceptGraphics getGraphicalRepresentation(Class<?> c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPropOrClassString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAnnotationsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescriptionsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIndividualsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getChildCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addChild(Node classRoot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addParent(Node root) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIndex(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLabel(String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setComment(String des) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSeeAlsoLabel(String seeAlso) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIsDefinedByLabel(String isDefBy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Node getChildAt(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRoot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getpropOrClassNeighbours() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getComment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSeeAlsoLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIsDefinedByLabel() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
