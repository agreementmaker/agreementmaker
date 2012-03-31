package am.app.ontology.ontologyParser;

import com.hp.hpl.jena.util.LocationMapper;

import am.app.ontology.Ontology.DatasetType;
import am.app.ontology.instance.endpoint.EndpointRegistry;

/**
 * This data structure holds all the information required
 * for loading an ontology.
 *  
 * @author Cosmin Stroe - Sep 8, 2011
 *
 */
public class OntologyDefinition {
	
	public boolean loadOntology = true;
	public String ontologyURI;
	public int ontologyLanguage;
	public int ontologySyntax;
	
	public boolean onDiskStorage = false;
	public boolean onDiskPersistent = false;
	public String  onDiskDirectory;
	
	public boolean loadInstances = false;
	public DatasetType instanceSourceType;
	
	public String instanceSourceFile;
	public int instanceSourceFormat;  // 0 = RDF
	public EndpointRegistry instanceEndpointType;
	
	public boolean loadSchemaAlignment = false;
	public String  schemaAlignmentURI;
	public int  schemaAlignmentFormat;
	
	public LocationMapper locationMapper;
	
	// TODO: Get rid of this variable, it does not make sense in a multi-ontology framework.
	public int sourceOrTarget; // GlobalStaticVariables.SOURCENODE or GlobalStaticVariables.TARGETNODE
	
	@Override
	public boolean equals(Object obj) {
		if( obj instanceof OntologyDefinition ) {
			OntologyDefinition ontDef = (OntologyDefinition) obj;
			
			if( loadOntology != ontDef.loadOntology ) return false;
			if( loadOntology == true ) {
				if( !ontologyURI.equals(ontDef.ontologyURI) ) return false;
				if( ontologyLanguage != ontDef.ontologyLanguage ) return false;
				if( ontologySyntax != ontDef.ontologySyntax ) return false;
			}
			
			if( onDiskStorage != ontDef.onDiskStorage ) return false;
			if( onDiskStorage == true ) {
				if( !onDiskDirectory.equals(ontDef.onDiskDirectory) ) return false;
				if( onDiskPersistent != ontDef.onDiskPersistent ) return false;
			}
			
			if( loadInstances != ontDef.loadInstances ) return false;
			if( loadInstances == true ) {
				if( !instanceSourceFile.equals(ontDef.instanceSourceFile) ) return false;
				if( instanceSourceFormat != ontDef.instanceSourceFormat ) return false;
				if( instanceSourceType != ontDef.instanceSourceType ) return false;
				if( instanceEndpointType != ontDef.instanceEndpointType ) return false;
				
				if( loadSchemaAlignment != ontDef.loadSchemaAlignment ) return false;
				if( loadSchemaAlignment == true ) {
					if( !schemaAlignmentURI.equals(ontDef.schemaAlignmentURI) ) return false;
					if( schemaAlignmentFormat != ontDef.schemaAlignmentFormat ) return false;
				}
			}
			
			// the definitions are equal.
			return true;
		}
		return false;
	}
}
