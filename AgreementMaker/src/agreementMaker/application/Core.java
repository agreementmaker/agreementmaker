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
	private Ontology sourceOntology;
	private Ontology targetOntology;
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
	
	

}
