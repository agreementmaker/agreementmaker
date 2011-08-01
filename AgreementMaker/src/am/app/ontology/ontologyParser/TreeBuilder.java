package am.app.ontology.ontologyParser;

import java.io.File;

import javax.swing.SwingWorker;

import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.userInterface.OntologyLoadingProgressDialog;

public abstract class TreeBuilder extends SwingWorker<Void, Void> {

	// instance variables 
	protected int treeCount;  // this variable is used in the Canvas visualization.  ( it is the total number of Vertices in the Classes and Properties trees )
	protected Node treeRoot;
	protected Ontology ontology;  
	protected int uniqueKey = 0;
	
	// Progress Monitor Variables
	protected OntologyLoadingProgressDialog progressDialog = null;  // need to keep track of the dialog in order to close it when we're done.  (there could be a better way to do this, but that's for later)
	protected int stepsTotal; // Used by the ProgressDialog.  This is a rough estimate of the number of steps to be done before we finish the matching.
	protected int stepsDone;  // Used by the ProgressDialog.  This is how many of the total steps we have completed.
	protected String report = "";
	
	
	public TreeBuilder(String filename,  int sourceOrTarget, String language, String format) {
		// TODO: Streamline this.
		ontology = new Ontology();
		ontology.setIndex( Core.getInstance().numOntologies() );
		ontology.setID( Core.getInstance().getNextOntologyID() );  // get an unique ID for this ontology
		ontology.setFilename(filename);
		ontology.setSourceOrTarget(sourceOrTarget);
		ontology.setLanguage(language);
		ontology.setFormat(format);
        File f = new File(filename);
        ontology.setTitle(f.getName()); 
        if( Core.DEBUG ) System.out.println(filename);
	}
	
	public static TreeBuilder buildTreeBuilder(String fileName, int ontoType, int langIndex, int syntaxIndex, boolean skip, boolean noReasoner, boolean onDisk, String onDiskDirectory, boolean persistent){
		// TODO: Not sure if this method is supposed to take implementation specific variables (ex. DB).
		
		String languageS = GlobalStaticVariables.getLanguageString(langIndex);
		String syntaxS = GlobalStaticVariables.getSyntaxString(syntaxIndex);
		TreeBuilder treeBuilder = null;
		
		if(langIndex == GlobalStaticVariables.XMLFILE){
			treeBuilder = new XmlTreeBuilder(fileName, ontoType, languageS, syntaxS);
		}
		else if(langIndex == GlobalStaticVariables.RDFSFILE) {
			if( onDisk )
				treeBuilder = new TDBOntoTreeBuilder(fileName, ontoType, languageS, syntaxS, skip, noReasoner, onDisk, onDiskDirectory, persistent);
			else
				treeBuilder = new RdfsTreeBuilder(fileName, ontoType, languageS, syntaxS, skip);
		}
		else if(langIndex == GlobalStaticVariables.TABBEDTEXT)
			treeBuilder = new TabbedTextBuilder(fileName, ontoType, languageS, syntaxS);
		else if(langIndex == GlobalStaticVariables.OWLFILE ) {
			if( onDisk ) 
				treeBuilder= new TDBOntoTreeBuilder(fileName, ontoType, languageS, syntaxS, skip, noReasoner, onDisk, onDiskDirectory, persistent);
			else 
				treeBuilder = new OntoTreeBuilder(fileName, ontoType, languageS, syntaxS, skip, noReasoner);
		}
		
		return treeBuilder;
	}
	
	public void build() throws Exception{
		buildTree();//Instantiated in the subclasses
		report = "Ontology loaded succesfully\n\n";
        report += "Total number of classes: "+ontology.getClassesList().size()+"\n";
        report += "Total number of properties: "+ontology.getPropertiesList().size()+"\n\n";
        report += "Select the 'Ontology Details' function in the 'Ontology' menu\nfor additional informations.\n";
        report += "The 'Hierarchy Visualization' can be disabled from the 'View' menu\nto improve system performances.\n";
	}
	
	protected void buildTree() throws Exception {
		throw new RuntimeException("This method has to be implemented in the subclass");
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
	public Node getTreeRoot() { return treeRoot; }


	/********************************************************************************************/
	/**
	 * This function sets the tree root
	 *
	 * @param root root of the tree
	 */

	public void setTreeRoot(Node root) { treeRoot = root; }
	
	/********************************************************************************************/	
	public Ontology getOntology() {
		return ontology;
	}
	
	//****************** PROGRESS DIALOG METHODS *************************8
	
	
    /**
     * This function is used by the Progress Dialog, in order to invoke the the treebuilder.
     * It's just a wrapper. 
     */
	public Void doInBackground() throws Exception {
		try {
			//without the try catch, the exception got lost in this thread, and we can't debug
			build();
		}
		catch(java.lang.OutOfMemoryError ex2){
			ex2.printStackTrace();
			report = Utility.OUT_OF_MEMORY;
			this.cancel(true);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			report = Utility.UNEXPECTED_ERROR;
			this.cancel(true);
		}
		return null;
	}
    
    /**
     * Function called by the worker thread when the matcher finishes the algorithm.
     */
    public void done() {
    	progressDialog.loadingComplete();  // when we're done, close the progress dialog
    }

	
	public void setProgressDialog(OntologyLoadingProgressDialog ontologyLoadingProgressDialog) {
		progressDialog = ontologyLoadingProgressDialog;
		
	}

	public String getReport() {
		return report;
	}
	
}


	
