package am.ds.matching;

import am.api.matching.MatcherResult;
import am.api.matching.SimilarityMatrix;
import am.api.ontology.Class;
import am.api.ontology.Instance;
import am.api.ontology.Property;

import java.util.Optional;

public class MatcherResultImpl implements MatcherResult {
    private final SimilarityMatrix<Class> classMatrix;
    private final SimilarityMatrix<Property> propertyMatrix;
    private final SimilarityMatrix<Instance> instanceMatrix;

    public MatcherResultImpl(SimilarityMatrix<Class> classMatrix,
                             SimilarityMatrix<Property> propertyMatrix,
                             SimilarityMatrix<Instance> instanceMatrix) {
        this.classMatrix = classMatrix;
        this.propertyMatrix = propertyMatrix;
        this.instanceMatrix = instanceMatrix;
    }

    @Override
    public Optional<SimilarityMatrix<Class>> getClasses() {
        return Optional.ofNullable(classMatrix);
    }

    @Override
    public Optional<SimilarityMatrix<Property>> getProperties() {
        return Optional.ofNullable(propertyMatrix);
    }

    @Override
    public Optional<SimilarityMatrix<Instance>> getInstances() {
        return Optional.ofNullable(instanceMatrix);
    }
}
