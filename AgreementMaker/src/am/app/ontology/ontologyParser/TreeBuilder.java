package am.app.ontology.ontologyParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingWorker;

import am.GlobalStaticVariables;
import am.Utility;
import am.app.Core;
import am.app.mappingEngine.referenceAlignment.MatchingPair;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.instance.FreebaseInstanceDataset;
import am.app.ontology.instance.GeoNamesInstanceDataset;
import am.app.ontology.instance.InstanceDataset;
import am.app.ontology.instance.OntologyInstanceDataset;
import am.app.ontology.instance.SeparateFileInstanceDataset;
import am.app.ontology.instance.SparqlInstanceDataset;
import am.app.ontology.instance.endpoint.EndpointRegistry;
import am.app.ontology.instance.endpoint.FreebaseEndpoint;
import am.app.ontology.instance.endpoint.GeoNamesEndpoint;
import am.app.ontology.instance.endpoint.SparqlEndpoint;
import am.output.alignment.oaei.OAEIAlignmentFormat;
import am.userInterface.OntologyLoadingProgressDialog;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public abstract class TreeBuilder extends SwingWorker<Void, Void> {

	// ALL DEPRECATED FIELDS MOVED TO Ontology class
		// local title
		@Deprecated public static final String TARGETTITLE = "Target Ontology";
		// ontology title
		@Deprecated public static final String SOURCETITILE = "Source Ontology";
		//	OWL File type representation
		@Deprecated public static final int SOURCENODE = 0;
		//	OWL File type representation
		@Deprecated public static final int TARGETNODE = 1;
		@Deprecated public static final int XMLFILE = 2;
		@Deprecated public static final int OWLFILE = 1;
		@Deprecated public static final int RDFSFILE = 0;
		@Deprecated public static final int TABBEDTEXT = 3;
		//public static final int DAMLFILE = 3;
		
		@Deprecated public static final int RDFXML = 0;
		@Deprecated public static final int RDFXMLABBREV = 1;
		@Deprecated public static final int NTRIPLE = 2;
		@Deprecated public static final int N3  = 3;
		@Deprecated public static final int TURTLE = 4;

		@Deprecated public final static String SYNTAX_RDFXML = "RDF/XML";
		@Deprecated public final static String SYNTAX_RDFXMLABBREV = "RDF/XML-ABBREV";
		@Deprecated public final static String SYNTAX_NTRIPLE = "N-TRIPLE";
		@Deprecated public final static String SYNTAX_N3 = "N3";
		@Deprecated public final static String SYNTAX_TURTLE = "TURTLE";
		@Deprecated public final static String[] syntaxStrings  = {SYNTAX_RDFXML, SYNTAX_RDFXMLABBREV, SYNTAX_NTRIPLE, SYNTAX_N3, SYNTAX_TURTLE};
		@Deprecated public final static String LANG_RDFS = "RDFS";
		@Deprecated public final static String LANG_OWL = "OWL";
		@Deprecated public final static String LANG_XML = "XML";
		@Deprecated public final static String LANG_TABBEDTEXT = "Tabbed TEXT";
		@Deprecated public static final String[] languageStrings = {LANG_RDFS, LANG_OWL, LANG_XML, LANG_TABBEDTEXT};
		
		
	public enum OntologyLanguage {
		RDFS("RDFS", 0),
		OWL("OWL", 1),
		XML("XML", 2),
		TABBEDTEXT("Tabbed Text", 3);
		
		String name;
		int ID;
		
		private OntologyLanguage(String name, int ID) {
			this.name = name;
			this.ID = ID;
		}
		
		public int getID() { return ID; }
		
		@Override public String toString() { return name; }
	}
	
	public enum OntologySyntax {
		RDFXML("RDF/XML", 0),
		RDFXMLABBREV("RDF/XML-ABBREV", 1),
		NTRIPLE("N-TRIPLE", 2),
		N3("N3", 3),
		TURTLE("TURTLE", 4);
		
		String name;
		int ID;
		
		private OntologySyntax(String name, int ID) {
			this.name = name;
			this.ID = ID;
		}
		
		public int getID() { return ID; }
		
		@Override public String toString() { return name; }
	}
	
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
	
	protected OntologyDefinition ontDefinition; // All the information needed to load the ontology.

	protected InstanceDataset instances;
	
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
	}
	
	public TreeBuilder( OntologyDefinition def ) {
		this.ontDefinition = def;
		ontology = new Ontology();
		ontology.setIndex( Core.getInstance().numOntologies() );
		ontology.setID( Core.getInstance().getNextOntologyID() );  // get an unique ID for this ontology
		if( def.loadOntology ) {
			ontology.setFilename(def.ontologyURI);
			ontology.setLanguage(Ontology.languageStrings[def.ontologyLanguage]);
			ontology.setFormat(Ontology.syntaxStrings[def.ontologySyntax]);
	        File f = new File(def.ontologyURI);
	        ontology.setTitle(f.getName()); 
		}
		else if( def.loadInstances ) {
			if( def.instanceSource == DatasetType.DATASET ) {
				ontology.setFilename(def.instanceSourceFile);
				ontology.setLanguage(Ontology.LANG_OWL);
				ontology.setFormat(Ontology.SYNTAX_RDFXML);
				File f = new File(def.instanceSourceFile);
		        ontology.setTitle(f.getName()); 
			}
			else if( def.instanceSource == DatasetType.ENDPOINT ){
				ontology.setFilename(def.instanceSourceFile);
				ontology.setLanguage(Ontology.LANG_OWL);
				ontology.setFormat(Ontology.SYNTAX_RDFXML);
				ontology.setTitle("Semantic Web Endpoint");
			}
		}
		ontology.setSourceOrTarget(def.sourceOrTarget);
		
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
	
	public static TreeBuilder buildTreeBuilder( OntologyDefinition odef ) {
		// TODO: Not sure if this method is supposed to take implementation specific variables (ex. DB).
		
		
				String languageS = GlobalStaticVariables.getLanguageString(odef.ontologyLanguage);
				String syntaxS = GlobalStaticVariables.getSyntaxString(odef.ontologySyntax);
				TreeBuilder treeBuilder = null;
				
				if(odef.ontologyLanguage == GlobalStaticVariables.XMLFILE){
					treeBuilder = new XmlTreeBuilder(odef.ontologyURI, odef.sourceOrTarget, languageS, syntaxS);
				}
				else if(odef.ontologyLanguage == GlobalStaticVariables.RDFSFILE) {
					if( odef.onDiskStorage )
						treeBuilder = new TDBOntoTreeBuilder(odef.ontologyURI, odef.sourceOrTarget, languageS, syntaxS, false, true, odef.onDiskStorage, odef.onDiskDirectory, odef.onDiskPersistent);
					else
						treeBuilder = new RdfsTreeBuilder(odef.ontologyURI, odef.sourceOrTarget, languageS, syntaxS, false);
				}
				else if(odef.ontologyLanguage == GlobalStaticVariables.TABBEDTEXT)
					treeBuilder = new TabbedTextBuilder(odef.ontologyURI, odef.sourceOrTarget, languageS, syntaxS);
				else if(odef.ontologyLanguage == GlobalStaticVariables.OWLFILE ) {
					if( odef.onDiskStorage ) 
						treeBuilder= new TDBOntoTreeBuilder(odef.ontologyURI, odef.sourceOrTarget, languageS, syntaxS, false, true, odef.onDiskStorage, odef.onDiskDirectory, odef.onDiskPersistent);
					else 
						treeBuilder = new OntoTreeBuilder(odef);
				}
				
				
				//treeBuilder = new OntoTreeBuilder(odef);
				
				return treeBuilder;
	}
	
	public void build() throws Exception{
		buildTree();//Instantiated in the subclasses
		
		if( ontDefinition != null ) loadInstances();
		
		report = "Ontology loaded succesfully\n\n";
        report += "Total number of classes: "+ontology.getClassesList().size()+"\n";
        report += "Total number of properties: "+ontology.getPropertiesList().size()+"\n";
        report += "Instances source: ";
        if( ontDefinition != null ) {
        	if( !ontDefinition.loadInstances )
        		report += "none.\n\n";
        	else if( ontDefinition.instanceSource == DatasetType.DATASET )
        		report += "dataset.\n\n";
        	else if( ontDefinition.instanceSource == DatasetType.ONTOLOGY )
        		report += "ontology.\n\n";
        	else if( ontDefinition.instanceSource == DatasetType.ENDPOINT )
        		report += "endpoint.\n\n";
        }
        report += "Select the 'Ontology Details' function in the 'Ontology' menu\nfor additional informations.\n";
        report += "The 'Hierarchy Visualization' can be disabled from the 'View' menu\nto improve system performances.\n";
	}
	
	protected void buildTree() throws Exception {
		throw new RuntimeException("This method has to be implemented in the subclass");
	}

	protected void loadInstances() {
		
		if( ontDefinition == null ) return;
		
		if( !ontDefinition.loadInstances ) return;
		
		if( ontDefinition.instanceSource == DatasetType.ONTOLOGY ) {
			instances = new OntologyInstanceDataset(ontology);
		}
		else if ( ontDefinition.instanceSource == DatasetType.DATASET ) {
			
			OntModel instancesModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
			
			if( !(ontDefinition.instanceSourceFile.startsWith("file:///") || 
					ontDefinition.instanceSourceFile.startsWith("http://")) ) {
				ontDefinition.instanceSourceFile = "file:///" + ontDefinition.instanceSourceFile;
			}
			
			instancesModel.read( ontDefinition.instanceSourceFile, null, ontology.getFormat() );
			
			instances = new SeparateFileInstanceDataset(instancesModel);
		}
		else if ( ontDefinition.instanceSource == DatasetType.ENDPOINT &&
				  ontDefinition.instanceEndpointType.equals( EndpointRegistry.FREEBASE ) ) {
			
			FreebaseEndpoint freebase = new FreebaseEndpoint();
			instances = new FreebaseInstanceDataset(freebase);
		}
		else if ( ontDefinition.instanceSource == DatasetType.ENDPOINT &&
				  ontDefinition.instanceEndpointType.equals( EndpointRegistry.GEONAMES ) ) {
			
			GeoNamesEndpoint geoNames = new GeoNamesEndpoint();
			instances = new GeoNamesInstanceDataset(geoNames);
		}
		else if ( ontDefinition.instanceSource == DatasetType.ENDPOINT &&
				  ontDefinition.instanceEndpointType.equals( EndpointRegistry.SPARQL ) ) {
			
			SparqlEndpoint endpoint = new SparqlEndpoint(ontDefinition.instanceSourceFile);
			instances = new SparqlInstanceDataset(endpoint);
		}

		
		ontology.setInstances(instances); // save the instances with this ontology
		
		// load the mapping file
		
		if( ontDefinition.loadSchemaAlignment ) {
			if( ontDefinition.schemaAlignmentFormat == 0 ) { // RDF
				try {
					File file = new File( ontDefinition.schemaAlignmentURI );
					
					FileReader fr = new FileReader(file);
					
					OAEIAlignmentFormat oaeiFormat = new OAEIAlignmentFormat();
					
					HashMap<String,List<MatchingPair>> map = oaeiFormat.readAlignment(fr);
					
					ontology.setInstanceTypeMappings(map);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public InstanceDataset getInstances() { return instances; }
	
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


	
