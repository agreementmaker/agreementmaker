package am.extension.collaborationClient.restful;

import am.extension.collaborationClient.api.CollaborationTask;

public class RESTfulTask implements CollaborationTask {

	private long id;
	private String name;
	private String sourceOntologyURL;
	private String targetOntologyURL;
	
	@Override
	public long getId() {
		return id;
	}
	
	public void setId(long id) { this.id = id; }

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) { this.name = name; }
	
	@Override
	public String getSourceOntologyURL() {
		return sourceOntologyURL;
	}

	public void setSourceOntologyURL(String url) { this.sourceOntologyURL = url; }
	
	@Override
	public String getTargetOntologyURL() {
		return targetOntologyURL;
	}

	public void setTargetOntologyURL(String url) { this.targetOntologyURL = url; }
	
	@Override
	public String toString() {
		return name;
	}
	
}
