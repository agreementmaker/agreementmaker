package am.extension.collaborationClient.restful;

import am.extension.collaborationClient.api.CollaborationCandidateMapping;

public class RESTfulCandidateMapping implements CollaborationCandidateMapping {

	private long id;
	private String sourceURI;
	private String targetURI;
	
	@Override
	public long getId() {
		return this.id;
	}

	public void setId(long id) { this.id = id; }
	
	@Override
	public String getSourceURI() {
		return this.sourceURI;
	}
	
	public void setSourceURI(String sourceURI) { this.sourceURI = sourceURI; }

	@Override
	public String getTargetURI() {
		return this.targetURI;
	}

	public void setTargetURI(String targetURI) { this.targetURI = targetURI; }
	
}
