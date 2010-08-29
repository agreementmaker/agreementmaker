package am.app.mapEngine.instance;

import java.util.ArrayList;

public class Instance {

	private String concept;			//Class name that the instance belongs to
	private ArrayList<InstanceProperty> IP;	//List of Data and Object Properties of instance
	
	public Instance(){
		concept = "";
		IP = new ArrayList<InstanceProperty>();
	}
	
	@Override
	public String toString() {
		return "Instance [IP=" + IP + ", concept=" + concept + "]";
	}

	//SETTERS & GETTERS
	public void setConcept(String concept) {
		this.concept = concept;
	}
	public String getConcept() {
		return concept;
	}
	public void setDP(ArrayList<InstanceProperty> iP) {
		IP = iP;
	}
	public ArrayList<InstanceProperty> getIP() {
		return IP;
	}

	//END OF SETTERS & GETTERS
}
