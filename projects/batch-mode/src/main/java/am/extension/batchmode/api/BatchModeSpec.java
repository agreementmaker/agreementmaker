package am.extension.batchmode.api;

import am.extension.batchmode.internal.BatchModeSpecImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(as = BatchModeSpecImpl.class)
public interface BatchModeSpec {
    public List<BatchModeTask> getTasks();
}
