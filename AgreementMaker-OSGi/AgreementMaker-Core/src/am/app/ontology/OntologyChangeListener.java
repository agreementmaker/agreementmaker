package am.app.ontology;

import java.util.EventListener;

public interface OntologyChangeListener extends EventListener {

	public void ontologyChanged( OntologyChangeEvent e );
	
}
