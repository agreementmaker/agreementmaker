package am.app.mapEngine.instance;

public class InstanceProperty {
	String subject = null;
	String predicate = null;
	String object = null;
	PropType propType = PropType.Unknown;
	
	public InstanceProperty(){
		subject = "";
		predicate = "";
		object = "";
		propType = PropType.Unknown;
	}

	public InstanceProperty(String s, String p, String o, PropType pt){
		subject = s;
		predicate = p;
		object = o;
		propType = pt;
	}
	
	@Override
	public String toString() {
		return "InstanceProperty [object=" + object + ", propType="
				+ propType + ", predicate=" + predicate + ", subject="
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
	
	public PropType propType() {
		return propType;
	}

	public void setPropType(PropType propType) {
		this.propType = propType;
	}
	//END OF SETTERS & GETTERS
	
}
