package am.app.mapEngine.instance;

import java.util.ArrayList;

public class InstanceList {
	private ArrayList<Instance> InstanceList;

	public InstanceList() {
		super();
		InstanceList = new ArrayList<Instance>();
	}
	
	public InstanceList(ArrayList<Instance> instanceList) {
		super();
		InstanceList = instanceList;
	}

	public ArrayList<Instance> getInstanceList() {
		return InstanceList;
	}

	public void setInstanceList(ArrayList<Instance> instanceList) {
		InstanceList = instanceList;
	}
	
}
