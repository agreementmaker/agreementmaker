package am.extension.multiUserFeedback.login;

import java.util.ArrayList;

import am.app.mappingEngine.Mapping;
import am.extension.multiUserFeedback.experiment.MUExperiment;

public class ServerLogin extends UFLLogin{
	MUExperiment experiment;
	@Override
	public void login(MUExperiment exp, String id) {
		// TODO Auto-generated method stub
		this.experiment=exp;
		exp.usersMappings.put(id, new ArrayList<Mapping>());
		exp.usersGroup.put(id, getGroup());
	}

	
	private int getGroup()
	{
		int size=experiment.usersMappings.size();
		return size%3;
	}

}
