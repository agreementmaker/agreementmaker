package am.api.config;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public interface IsConfigurable {
    List<Enum> getConfigurationKeys();
    void configure(Map<Enum, String> properties);
}
