package am.extension.userfeedback.preset;

import java.io.Serializable;

import am.extension.userfeedback.experiments.UFLExperimentSetup;

public class ExperimentPreset implements Serializable {

	private static final long serialVersionUID = -3258782589070978307L;

	private String name;
	
	private UFLExperimentSetup setup;
	
	public ExperimentPreset(String name, UFLExperimentSetup setup) {
		this.name = name;
		this.setup = setup;
	}
	
	/**
	 * Cloning constructor.
	 */
	public ExperimentPreset(ExperimentPreset p) {
		this.name = p.name;
		try {
			this.setup = (UFLExperimentSetup) p.setup.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return The human readable name for this preset.
	 */
	public String getName() {
		return name;
	}
	
	public UFLExperimentSetup getExperimentSetup() {
		return setup;
	}
	
	public void setExperimentSetup(UFLExperimentSetup setup) {
		this.setup = setup;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	protected ExperimentPreset clone() throws CloneNotSupportedException {
		return new ExperimentPreset(this);
	}
}
