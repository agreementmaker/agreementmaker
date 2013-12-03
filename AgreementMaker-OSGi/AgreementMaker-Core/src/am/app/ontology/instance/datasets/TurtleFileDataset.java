package am.app.ontology.instance.datasets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import am.AMException;
import am.app.mappingEngine.instance.EntityTypeMapper.EntityType;
import am.app.ontology.instance.Instance;
import am.app.ontology.instance.InstanceDataset;
import am.utility.RunTimer;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * This dataset is for reading in a TURTLE File that contains raw triples. This
 * was originally developed to read in the OAEI 2013 Instance Matching Track
 * datasets.
 * 
 * We assume that all triples are only assertional, which means that only
 * instances will be the subject of the triples.
 * 
 * @author cosmin
 * 
 */
public class TurtleFileDataset implements InstanceDataset {

	private static Logger LOG = Logger.getLogger(TurtleFileDataset.class);
	
	private HashMap<String,List<Statement>> uriMap = new HashMap<>();
	
	private HashMap<String,Instance> instances = new HashMap<>();
	
	public TurtleFileDataset(OntModel model) {
		
		RunTimer timer = new RunTimer().start();
		
		StmtIterator stmtIter = model.listStatements();
		while( stmtIter.hasNext() ) {
			Statement s = stmtIter.next();
			
			String sURI = s.getSubject().getURI();
			
			if( uriMap.containsKey(sURI) ) {
				uriMap.get(sURI).add(s);
			}
			else {
				List<Statement> list = new LinkedList<>();
				list.add(s);
				uriMap.put(sURI, list);
			}
			
		}
		
		for( String currentURI : uriMap.keySet() ) {
			List<Statement> stmts = uriMap.get(currentURI);
			
			Instance i = new Instance(currentURI, EntityType.UNKNOWN);
			i.addStatements(stmts);
			for( Statement currentStmt : stmts ) {
				Property p = currentStmt.getPredicate();
				RDFNode n = currentStmt.getObject();
				i.setProperty(p.getLocalName(), n.toString());
			}
			
			instances.put(currentURI,i);
		}
		
		uriMap.clear();
		uriMap = null;
		
		timer.stop();
		LOG.debug("Loaded file in " + timer.getFormattedRunTime());
		System.out.println("Loaded file in " + timer.getFormattedRunTime()); // get rid of this when the Logger is working property.
	}
	
	@Override public boolean isIterable() { return true; }

	@Override
	public Collection<Instance> getInstances(String type, int limit)
			throws AMException {
		throw new AMException("This method is not implemented.");
	}

	@Override
	public Collection<Instance> getCandidateInstances(String keyword, String type)
			throws AMException {
		return instances.values();
	}

	@Override
	public Iterator<Instance> getInstances() throws AMException {
		return instances.values().iterator();
	}

	@Override
	public Instance getInstance(String uri) throws AMException {
		return instances.get(uri);
	}

	@Override
	public long size() {
		return instances.size();
	}

}
