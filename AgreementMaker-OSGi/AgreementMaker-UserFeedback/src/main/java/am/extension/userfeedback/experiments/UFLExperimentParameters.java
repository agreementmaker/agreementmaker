package am.extension.userfeedback.experiments;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Unified experiment parameters.
 * 
 * @author <a href="http://cstroe.com">Cosmin Stroe</a>
 *
 */
public class UFLExperimentParameters extends Properties {

	private static final long serialVersionUID = -1375888271615206058L;

	public enum Parameter {
		NUM_ITERATIONS,  // how many iterations are we doing
		ERROR_RATE,
		NUM_USERS,
		REVALIDATION_RATE,
		PRINT_FORBIDDEN_POSITIONS("false"), // whether to print out the forbidden matrices every time
		PROPAGATION_METHOD, // the propagation method we will use in Feedback Propagation.
		STATIC_CANDIDATE_SELECTION("false"), // whether our CS is static (only computed before experiment) or dynamic (computed every new itertation).
		LOGFILE, // the name of the logfile
		IM_THRESHOLD("0.6"), // the initial matchers threshold
		;
		
		private String defaultValue;
		
		private Parameter() { this.defaultValue = null; }
		private Parameter(String def) { this.defaultValue = def; }
		public boolean hasDefaultValue() { return this.defaultValue != null; }
		public String getDefaultValue() { return this.defaultValue; }
		
		/**
		 * @return The parameter with the given name. null if no parameter exists.
		 */
		public static Parameter getParameter(String name) {
			for( Parameter p : Parameter.values() ) {
				if( p.name().equals(name) ) return p;
			}
			return null;
		}
	}
	public UFLExperimentParameters() {}
	
	/** Cloning constructor */
	public UFLExperimentParameters(UFLExperimentParameters p) {
		for(Object k : p.keySet()) {
			this.put(k, p.get(k));
		}
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
		String value = getProperty(p.name());
		if( value == null && p.hasDefaultValue() ) {
			return Integer.parseInt(p.getDefaultValue());
		}
		else if( value != null ) {
			return Integer.parseInt(value);
		}
		throw new RuntimeException("The parameter has not been set and it does not have a default value.");
	}
	
	public double getDoubleParameter(Parameter p) {
		String value = getProperty(p.name());
		if( value == null && p.hasDefaultValue() ) {
			return Double.parseDouble(p.getDefaultValue());
		}
		else if( value != null ) {
			return Double.parseDouble(value);
		}
		throw new RuntimeException("The parameter has not been set and it does not have a default value.");
	}
	
	public boolean getBooleanParameter(Parameter p) {
		String value = getProperty(p.name());
		if( value == null && p.hasDefaultValue() ) {
			return Boolean.parseBoolean(p.getDefaultValue());
		}
		else if( value != null ) {
			return Boolean.parseBoolean(value);
		}
		throw new RuntimeException("The parameter has not been set and it does not have a default value.");
	}
	
	public Parameter[] toParameterArray() {
		Object[] keySet = keySet().toArray();
		Parameter[] p = new Parameter[keySet.length];
		for(int i = 0; i < keySet.length; i++ ) {
			p[i] = Parameter.getParameter((String)keySet[i]);
		}
		return p;
	}
	
	public String[] toStringArray() {
		List<String> params = new LinkedList<>();
		
		for( Object o : keySet() ) {
			String value = getProperty((String) o);
			params.add( o.toString() + ": " + value);
		}
		
		Collections.sort(params);
		
		return params.toArray(new String[0]);
	}
	
	public UFLExperimentParameters clone() {
		return new UFLExperimentParameters(this);
	}
}
