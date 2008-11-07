package agreementMaker.application;

import agreementMaker.application.ontology.Ontology;

/**
 * SINGLETON JAVA PATTERN
 * There will be only one instance of this class in the whole application
 * All classes can access it without having any reference to it, just invoking the static method
 * Core.getIstance()
 * All model information of the system will be accessible from this class:
 * ontologies, matchings and so on.
 *
 */
public class Core {
	private Ontology sourceOntology;//Null if it's not loaded yet
	private Ontology targetOntology;//Null if it's not loaded yet
	/**
	 * Singleton pattern: unique instance
	 */
	private static Core core  = new Core();
	/**
	 * 
	 * @return the unique instance of the core
	 */
	public static Core getInstance() {
		return core;
	}
	/**
	 * It's private because it's not possible to create new instances of this class
	 */
	private Core() {
	}
	
	public Ontology getSourceOntology() {
		return sourceOntology;
	}
	public void setSourceOntology(Ontology sourceOntology) {
		this.sourceOntology = sourceOntology;
	}
	public Ontology getTargetOntology() {
		return targetOntology;
	}
	public void setTargetOntology(Ontology targetOntology) {
		this.targetOntology = targetOntology;
	}
	
	public boolean sourceIsLoaded() {
		return sourceOntology != null;
	}
	
	public boolean targetIsLoaded() {
		return targetOntology != null;
	}
	
}
