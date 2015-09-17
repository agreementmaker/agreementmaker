package am.app.ontology.jena;

import am.api.ontology.OntoInstance;

import com.hp.hpl.jena.ontology.Individual;

public class JenaInstance implements OntoInstance<Individual> {

	private Individual individual;
	
	public JenaInstance(Individual individual) {
		this.individual = individual;
	}
	
	@Override
	public Individual getInner() {
		return individual;
	}
	
	@Override
	public String getURI() {
		return individual.getURI();
	}
}
