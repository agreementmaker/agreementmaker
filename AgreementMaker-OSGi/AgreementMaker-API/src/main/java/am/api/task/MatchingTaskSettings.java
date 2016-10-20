package am.api.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Settings for configuring how the matching task is executed via
 * custom settings.
 *
 * These are a mix of Matcher settings, Selector settings, and others.
 */
public interface MatchingTaskSettings {
    /**
     * Retrieve a setting given its key.
     */
    Optional<String> getSetting(String key);

    class Builder {
        private Map<String, String> settings = new HashMap<>();

        public Builder addSetting(String key, String value) {
            settings.put(key, value);
            return this;
        }

        public MatchingTaskSettings build() {
            return key -> Optional.ofNullable(settings.get(key));
        }
    }
}
