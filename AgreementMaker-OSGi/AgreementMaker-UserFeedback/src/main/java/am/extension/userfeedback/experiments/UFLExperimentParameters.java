package am.extension.userfeedback.experiments;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Unified experiment parameters.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public class UFLExperimentParameters extends Properties {

	private static final long serialVersionUID = -1375888271615206058L;

	public enum Parameter {
		VALIDATION_OVERLAP,
		NUM_ITERATIONS,  // how many iterations are we doing
		ERROR_RATE,
		NUM_USERS;
	}
	
	public void setParameter(Parameter p, String value) {
		setProperty(p.name(), value);
	}
	
	public void setIntParameter(Parameter p, int value) {
		setProperty(p.name(), Integer.toString(value));
	}

	public void setDoubleParameter(Parameter p, double value) {
		setProperty(p.name(), Double.toString(value));
	}

	public void setBooleanParameter(Parameter p, boolean value) {
		setProperty(p.name(), Boolean.toString(value));
	}
	
	public String getParameter(Parameter p) {
		return getProperty(p.name());
	}
	
	public int getIntParameter(Parameter p) {
		return Integer.parseInt(getProperty(p.name()));
	}
	
	public double getDoubleParameter(Parameter p) {
		return Double.parseDouble(getProperty(p.name()));
	}
	
	public boolean getBooleanParameter(Parameter p) {
		return Boolean.parseBoolean(getProperty(p.name()));
	}
	
	public String[] toStringList() {
		List<String> params = new LinkedList<>();
		
		for( Object o : keySet() ) {
			String value = getProperty((String) o);
			params.add( o.toString() + ": " + value);
		}
		
		Collections.sort(params);
		
		return params.toArray(new String[0]);
	}
}
