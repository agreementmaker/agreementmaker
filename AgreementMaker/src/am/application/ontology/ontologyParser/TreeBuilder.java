package am.application.ontology.ontologyParser;

import java.io.File;
import java.util.Enumeration;

import am.GlobalStaticVariables;
import am.application.ontology.Ontology;
import am.userInterface.vertex.Vertex;

public abstract class TreeBuilder {

	// instance variables  

	protected int treeCount;
	protected Vertex treeRoot;
	protected Ontology ontology;  
	protected int uniqueKey = 0;
	
	public TreeBuilder(String filename,  int sourceOrTarget, String language, String format) {
		ontology = new Ontology();
		ontology.setFilename(filename);
		ontology.setSourceOrTarget(sourceOrTarget);
		ontology.setLanguage(language);
		ontology.setFormat(format);
        File f = new File(ontology.getFilename());
        ontology.setTitle(f.getName()); 
	}
	
	public static TreeBuilder buildTreeBuilder(String fileName, int ontoType, int langIndex, int syntaxIndex){
		
		
		String languageS = GlobalStaticVariables.getLanguageString(langIndex);
		String syntaxS = GlobalStaticVariables.getSyntaxString(syntaxIndex);
		TreeBuilder treeBuilder;
		if(langIndex == GlobalStaticVariables.XMLFILE){
			treeBuilder = new XmlTreeBuilder(fileName, ontoType, languageS, syntaxS);
		}
		else if(langIndex == GlobalStaticVariables.RDFSFILE)
			treeBuilder = new RdfsTreeBuilder(fileName, ontoType, languageS, syntaxS);
		else treeBuilder = new OntoTreeBuilder(fileName, ontoType, languageS, syntaxS);
		
		//TO BE CHANGED IN THE FUTURE
		for (Enumeration e = treeBuilder.getTreeRoot().preorderEnumeration(); e.hasMoreElements() ;) 
		{
			Vertex node = (Vertex) e.nextElement();
			node.setName(node.toString());
			node.setNodeType(ontoType);
			node.setVerticalHorizontal();
		}
		
		System.out.println("Total number of nodes in the tree hierarchy: "+treeBuilder.getTreeCount());
		System.out.println("Total number of classes to be aligned: "+treeBuilder.getOntology().getClassesList().size());
		System.out.println("Total number of properties to be aligned: "+treeBuilder.getOntology().getPropertiesList().size());
		
		return treeBuilder;
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
