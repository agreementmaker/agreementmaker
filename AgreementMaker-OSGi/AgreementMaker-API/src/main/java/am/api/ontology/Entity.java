package am.api.ontology;

import java.util.Optional;

/**
 * <p>
 * This interface represents an entity in an ontology.
 * </p>
 */
public interface Entity {
	/**
	 * @return The unique URI of this entity.
	 */
	String getURI();

	/**
	 * @return For named entities, their name in the ontology.
     */
	Optional<String> getName();
}
