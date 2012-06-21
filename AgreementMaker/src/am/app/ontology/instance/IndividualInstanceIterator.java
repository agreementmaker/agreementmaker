package am.app.ontology.instance;

import java.util.Iterator;

import am.app.ontology.instance.Instance;

import com.hp.hpl.jena.ontology.Individual;

/**
 * This utility class wraps an Individual Iterator, and makes it an Instance
 * Iterator. It's used to make the {@link KnowledgeBaseInstanceDataset} and
 * {@link OntModelInstanceDataset} conform to the {@link InstanceDataset}
 * interface.
 * 
 * @author Cosmin Stroe
 * 
 * @see {@link KnowledgeBaseInstanceDataset}, {@link OntModelInstanceDataset}
 */
public class IndividualInstanceIterator implements Iterator<Instance> {

	private Iterator<Individual> iterator;

	public IndividualInstanceIterator(Iterator<Individual> indiIter) {
		this.iterator = indiIter;
	}
	
	@Override
	public boolean hasNext() { return iterator.hasNext(); }

	@Override
	public Instance next() {
		return new Instance(iterator.next());
	}

	@Override
	public void remove() {
		iterator.remove();
	}

}
