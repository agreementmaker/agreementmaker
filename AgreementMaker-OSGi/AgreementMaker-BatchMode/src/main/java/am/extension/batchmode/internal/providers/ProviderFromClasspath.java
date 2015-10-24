package am.extension.batchmode.internal.providers;

import am.extension.batchmode.Activator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProviderFromClasspath {
    private final String canonicalClassName;

    public ProviderFromClasspath(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
    }

    @JsonIgnore
    public Object getObject() {
        try {
            Class c = Activator.getContext().getBundle().loadClass(canonicalClassName);
            return c.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getCanonicalClassName() {
        return canonicalClassName;
    }
}
