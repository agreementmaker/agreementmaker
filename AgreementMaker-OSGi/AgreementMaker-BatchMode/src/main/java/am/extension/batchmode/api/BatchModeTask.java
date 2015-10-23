package am.extension.batchmode.api;

import am.extension.batchmode.internal.BatchModeTaskImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = BatchModeTaskImpl.class)
public interface BatchModeTask {
    public String getSourceOntology();
    public String getTargetOntology();
    public BatchModeMatcherProvider getMatcher();
    public BatchModeSelectorProvider getSelector();
    public BatchModeOutputProvider getOutput();
}
