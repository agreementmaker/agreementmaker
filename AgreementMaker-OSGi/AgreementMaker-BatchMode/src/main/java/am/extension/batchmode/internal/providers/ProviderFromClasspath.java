package am.extension.batchmode.internal.providers;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProviderFromClasspath {
    private final String canonicalClassName;

    public ProviderFromClasspath(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
    }

    @JsonIgnore
    public Object getObject() {
        try {
            Class c = Class.forName(canonicalClassName);
            return c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCanonicalClassName() {
        return canonicalClassName;
    }
}
