package am.extension.batchmode.internal;

import am.extension.batchmode.api.BatchModeMatcherProvider;
import am.extension.batchmode.api.BatchModeOutputProvider;
import am.extension.batchmode.api.BatchModeSelectorProvider;
import am.extension.batchmode.api.BatchModeTask;

public class BatchModeTaskImpl implements BatchModeTask {
    private String sourceOntology;
    private String targetOntology;
    private BatchModeMatcherProvider matcher;
    private BatchModeSelectorProvider selector;
    private BatchModeOutputProvider output;

    @Override
    public String getSourceOntology() {
        return sourceOntology;
    }

    public void setSourceOntology(String sourceOntology) {
        this.sourceOntology = sourceOntology;
    }

    @Override
    public String getTargetOntology() {
        return targetOntology;
    }

    public void setTargetOntology(String targetOntology) {
        this.targetOntology = targetOntology;
    }

    @Override
    public BatchModeMatcherProvider getMatcher() {
        return matcher;
    }

    public void setMatcher(BatchModeMatcherProvider matcher) {
        this.matcher = matcher;
    }

    @Override
    public BatchModeSelectorProvider getSelector() {
        return selector;
    }

    public void setSelector(BatchModeSelectorProvider selector) {
        this.selector = selector;
    }

    @Override
    public BatchModeOutputProvider getOutput() {
        return output;
    }

    public void setOutput(BatchModeOutputProvider output) {
        this.output = output;
    }
}
