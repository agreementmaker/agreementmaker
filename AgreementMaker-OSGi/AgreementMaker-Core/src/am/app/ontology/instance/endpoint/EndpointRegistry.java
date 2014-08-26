package am.app.ontology.instance.endpoint;

/**
 * A list of all the endpoint types.
 * 
 * @author cosmin
 *
 */
public enum EndpointRegistry {
	
	SPARQL   ( SparqlEndpoint.class ),
	GEONAMES ( GeoNamesEndpoint.class ),
	FREEBASE ( FreebaseEndpoint.class );
	
	private Class<? extends SemanticWebEndpoint> cls;
	
	EndpointRegistry( Class<? extends SemanticWebEndpoint> c ) {
		this.cls = c;
	}
	
	public Class<? extends SemanticWebEndpoint> getEndpointClass() { return cls; }
	
}
