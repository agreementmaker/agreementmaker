package am.app.ontology;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

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
		return getLocalName().compareTo(arg0.getLocalName());
	}

	@Override
	public Resource getResource() {
		return jenaObject;
	}

	@Override
	public int getParentCount() {
		return jenaObject.listSuperClasses(true).toList().size();
	}

	@Override
	public List<Node> getParents() {
		List<Node> parentsList = new LinkedList<Node>();
		ExtendedIterator<OntClass> parents = jenaObject.listSuperClasses(true);
		while( parents.hasNext() ) {
			parentsList.add(new JenaBackedOntClassNode(parents.next()));
		}
		return parentsList;
	}

	@Override
	public List<Node> getChildren() {
		List<Node> childList = new LinkedList<Node>();
		ExtendedIterator<OntClass> children = jenaObject.listSubClasses(true);
		while( children.hasNext() ) {
			childList.add(new JenaBackedOntClassNode(children.next()));
		}
		return childList;
	}

	@Override
	public int getChildCount() {
		return jenaObject.listSubClasses(true).toList().size();
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
	public String getLabel() {
		RDFNode labelValue = jenaObject.getPropertyValue(RDFS.label);
		if( labelValue == null ) return null;
		else return labelValue.toString();
	}

	@Override
	public String getPropOrClassString() {
		return null;
	}

	@Override
	public String getAnnotationsString() {
		return null;
	}

	@Override
	public String getDescriptionsString() {
		return null;
	}

	@Override
	public String getIndividualsString() {
		return null;
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
