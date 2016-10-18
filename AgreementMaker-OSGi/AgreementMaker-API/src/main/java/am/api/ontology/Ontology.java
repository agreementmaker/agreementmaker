package am.api.ontology;

import java.util.List;

/**
 * <p>
 * This interface represents an Ontology that is loaded in the AgreementMaker
 * system. It's meant to be a thin layer over an existing OWL library (Jena, OWL
 * API, etc.) and it encapsulates the ontology object from the specific library.
 * </p>
 * 
 * <p>
 * The methods defined in this interface are meant to be common across all the
 * different underlying implementations.
 * </p>
 */
public interface Ontology {

	/**
	 * @return A list of ontology classes defined in this ontology.
	 */
	List<Class> getClasses();
	
	/**
	 * @return A list of the ontology properties defined in this ontology.
	 */
	List<Property> getProperties();
	
	/**
	 * @return A list of the ontology instances defined in this ontology.
	 */
	List<Instance> getInstances();
}