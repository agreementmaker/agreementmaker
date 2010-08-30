package am.app.mapEngine.instance;

public class InstanceProperty {
	String subject = null;
	String predicate = null;
	String object = null;
	boolean objectType = true; //True if Object Property, false otherwise.
	
	public InstanceProperty(){
		subject = "";
		predicate = "";
		object = "";
		objectType = true;
	}

	@Override
	public String toString() {
		return "InstanceProperty [object=" + object + ", objectType="
				+ objectType + ", predicate=" + predicate + ", subject="
				+ subject + "]";
	}

	//SETTERS & GETTERS
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}
	
	public boolean isObjectType() {
		return objectType;
	}

	public void setObjectType(boolean objectType) {
		this.objectType = objectType;
	}
	//END OF SETTERS & GETTERS
	
}
