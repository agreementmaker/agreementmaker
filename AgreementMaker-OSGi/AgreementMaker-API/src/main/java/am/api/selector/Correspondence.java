package am.api.selector;

import am.api.ontology.Entity;

/**
 * <p>
 * This interface represents a single correspondence between two entities
 * defined in an ontology, the first which we name the <i>source</i> entity and
 * the second we name the <i>target</i> entity. The entities can be classes,
 * properties, or instances.
 * </p>
 * 
 * <p>
 * The correspondence describes the strength of a
 * {@link am.api.selector.SemanticRelation relation} that holds between the two
 * entities. The strength of the relation is modeled as a similarity value, a
 * number from 0 to 1, retrieved by {@link #getValue()}.
 * </p>
 * 
 * <p>
 * For relations that have a direction (such as {@link am.api.selector.SemanticRelation#SUBCLASSOF subClassOf})
 * the direction is defined to be from the source entity to the target entity.
 * </p>
 */
public interface Correspondence<I extends Entity> {
	/**
	 * @return The source entity of this correspondence.
	 */
	I getSource();
	
	/**
	 * @return The target entity of this correspondence.
	 */
	I getTarget();
	
	/**
	 * @return The relation we are describing with this correspondence.
	 */
	SemanticRelation getRelation();
	
	/**
	 * @return A number from 0 to 1 which conveys how strong the relationship is.
	 */
	double getValue();
}