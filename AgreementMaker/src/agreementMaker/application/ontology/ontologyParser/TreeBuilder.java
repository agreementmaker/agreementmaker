package agreementMaker.application.ontology.ontologyParser;

import java.io.File;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import agreementMaker.application.ontology.Ontology;
import agreementMaker.userInterface.vertex.Vertex;

public abstract class TreeBuilder {

	// instance variables  

	protected int treeCount;
	protected Vertex treeRoot;
	protected Ontology ontology;  
	protected int uniqueKey = 0;
	
	public TreeBuilder(String filename, String language, int sourceOrTarget) {
		ontology = new Ontology();
		ontology.setFilename(filename);
		ontology.setSourceOrTarget(sourceOrTarget);
        File f = new File(ontology.getFilename());
        ontology.setTitle(f.getName()); 
	}

	/**
	 * This function returns the number of nodes created by the tree
	 * @return int the number of nodes created by the tree
	 */
	public int getTreeCount()
	{
		return treeCount;
	}  
	/**
	 * This function returns the tree root
	 * @return treeRoot	root of the tree
	 */
	public Vertex getTreeRoot()
	{
		return treeRoot;
	}


	/********************************************************************************************/
	/**
	 * This function sets the tree root
	 *
	 * @param root root of the tree
	 */
	public void setTreeRoot(Vertex root)    
	{
		treeRoot = root;
	}
	/********************************************************************************************/	
	public Ontology getOntology() {
		return ontology;
	}
}
