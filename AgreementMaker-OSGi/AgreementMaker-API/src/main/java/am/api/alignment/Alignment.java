package am.api.alignment;

import am.api.ontology.Entity;

import java.util.Collection;
import java.util.Optional;

/**
 * A collection of correspondences between two ontologies.
 */
public interface Alignment<I extends Entity> {
    /**
     * @return All the correspondences in this alignment.
     */
	Collection<Correspondence<I>> getCorrespondences();

    /**
     * @return Any correspondences that contain the given source entity.
     */
    Optional<Collection<Correspondence<I>>> getForSource(I entity);

    /**
     * @return Any correspondences that contain the given source entity.
     */
    Optional<Collection<Correspondence<I>>> getForTarget(I entity);
}