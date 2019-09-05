package am.api.ontology;

/**
 * <p>
 * This interface represents an entity in an ontology. The entities in an
 * ontology are treated as belonging to a list. There are three separate lists:
 * one for {@link am.api.ontology.AMOntology#getClasses() classes}, one for 
 * {@link am.api.ontology.AMOntology#getProperties() properties}, and one for 
 * {@link am.api.ontology.AMOntology#getInstances() instances}.
 * </p>
 * 
 * <p>
 * For efficient processing, the index of each entity in its list is returned by
 * {@link OntoEntity#getIndex()}.  The lists are created when the ontology is loaded,
 * and therefore the indices are set at that time.
 * </p>
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 */
public interface OntoEntity {

	/**
	 * @return The unique URI of this entity.
	 */
	public String getURI();
	
	/**
	 * @return The index of this entity in its list.
	 */
	public int getIndex();
}
