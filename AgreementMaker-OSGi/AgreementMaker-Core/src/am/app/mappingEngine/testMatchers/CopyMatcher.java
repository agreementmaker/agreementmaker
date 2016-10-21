package am.app.mappingEngine.testMatchers;


import am.api.matcher.Matcher;
import am.api.matcher.MatcherProperties;
import am.api.matcher.MatcherResult;
import am.api.matcher.SimilarityMatrix;
import am.api.ontology.Class;
import am.api.ontology.Entity;
import am.api.ontology.Instance;
import am.api.ontology.Ontology;
import am.api.ontology.Property;
import am.api.alignment.AlignmentContext;
import am.app.mappingEngine.AbstractMatcher;
import am.ds.matching.ArraySimilarityMatrixBuilder;

import java.util.List;
import java.util.Optional;

public class CopyMatcher extends AbstractMatcher implements Matcher {
    @Override
    public MatcherProperties getProperties() {
        return new MatcherProperties.Builder()
                .setMinInputMatchers(1)
                .setMaxInputMatchers(1)
                .setName("Copy Matcher")
                .setCategory(am.api.matcher.MatcherCategory.UTILITY)
                .build();
    }

    @Override
    public MatcherResult match(AlignmentContext task) {
        Ontology sourceOntology = task.getSourceOntology();
        Ontology targetOntology = task.getTargetOntology();
        MatcherResult previousMatcher = task.getInputs().get(0).getMatcherResult();

        SimilarityMatrix<Class> classMatrix = null;
        Optional<SimilarityMatrix<Class>> previousClassMatrix = previousMatcher.getClasses();
        if(previousClassMatrix.isPresent()) {
            classMatrix = copyMatrix(
                    sourceOntology.getClasses(),
                    targetOntology.getClasses(),
                    previousClassMatrix.get());
        }

        SimilarityMatrix<Property> propertyMatrix = null;
        Optional<SimilarityMatrix<Property>> previousPropertyMatrix = previousMatcher.getProperties();
        if(previousPropertyMatrix.isPresent()) {
            propertyMatrix = copyMatrix(
                    sourceOntology.getProperties(),
                    targetOntology.getProperties(),
                    previousPropertyMatrix.get());
        }

        SimilarityMatrix<Instance> instanceMatrix = null;
        Optional<SimilarityMatrix<Instance>> previousInstanceMatrix = previousMatcher.getInstances();
        if(previousInstanceMatrix.isPresent()) {
            instanceMatrix = copyMatrix(
                    sourceOntology.getInstances(),
                    targetOntology.getInstances(),
                    previousInstanceMatrix.get());
        }

        return new MatcherResult.Builder()
                .setClasses(Optional.ofNullable(classMatrix))
                .setProperties(Optional.ofNullable(propertyMatrix))
                .setInstances(Optional.ofNullable(instanceMatrix))
                .build();
    }

    private <I extends Entity> SimilarityMatrix<I> copyMatrix(List<I> source, List<I> target, SimilarityMatrix<I> inputMatrix) {
        ArraySimilarityMatrixBuilder<I> matrixBuilder = new ArraySimilarityMatrixBuilder<>(source, target);

        for(I sourceEntity : source) {
            for(I targetClass : target) {
                matrixBuilder.set(sourceEntity, targetClass, inputMatrix.getSimilarity(sourceEntity, targetClass));
            }
        }

        return matrixBuilder.build();
    }
}
