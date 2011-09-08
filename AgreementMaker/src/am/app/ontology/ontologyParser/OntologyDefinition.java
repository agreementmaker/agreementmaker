package am.app.ontology.ontologyParser;

import am.app.ontology.instance.InstanceDataset.DatasetType;
import am.app.ontology.instance.endpoint.EndpointRegistry;

/**
 * This data structure holds all the information required
 * for loading an ontology.
 *  
 * @author Cosmin Stroe - Sep 8, 2011
 *
 */
public class OntologyDefinition {
	
	public String ontologyURI;
	public int ontologyLanguage;
	public int ontologySyntax;
	
	public boolean onDiskStorage = false;
	public boolean onDiskPersistent = false;
	public String  onDiskDirectory;
	
	public boolean loadInstances = false;
	public DatasetType instanceSource;
	
	public int instanceSourceFormat;  // 0 = N3
	public EndpointRegistry instanceEndpointType;
	
	public boolean loadSchemaAlignment = false;
	public String  schemaAlignmentURI;
	public int  schemaAlignmentFormat;
	
	// TODO: Get rid of this variable, it does not make sense in a multi-ontology framework.
	public int sourceOrTarget; // GlobalStaticVariables.SOURCENODE or GlobalStaticVariables.TARGETNODE
}
