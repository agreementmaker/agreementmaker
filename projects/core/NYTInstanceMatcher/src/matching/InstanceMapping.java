package matching;

public class InstanceMapping {
	String sourceURI;
	String targetURI;
	
	public InstanceMapping(String sourceURI, String targetURI) {
		super();
		this.sourceURI = sourceURI;
		this.targetURI = targetURI;
	}
	
	public String getSourceURI() {
		return sourceURI;
	}
	
	public String getTargetURI() {
		return targetURI;
	}
}
