package agreementMaker.application.mappingEngine.StringUtil;

import com.wcohen.ss.api.StringWrapper;

public class AMStringWrapper implements StringWrapper{
	

	private static final long serialVersionUID = 1L;
	
	String string;
	
	public AMStringWrapper(String s) {
		string = s;
	}
	/** Return the string that is wrapped. */
	public String unwrap() {
		return string;
	}

	/** Return the i-th char of the wrapped string */
	public char charAt(int i) {
		return string.charAt(i);
	}

	/** Return the length of the wrapped string */
	public int length() {
		return string.length();
	}
}
