package am.extension.batchmode.api;

public interface BatchModeTask {
    public BatchModeOntologyProvider getSourceOntology();
    public BatchModeOntologyProvider getTargetOntology();
    public BatchModeMatcherProvider getMatcher();
    public BatchModeSelectorProvider getSelector();
    public BatchModeOutputProvider getOutput();
}
