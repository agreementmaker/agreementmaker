package am.app.ontology.instance;

import java.util.ArrayList;
import java.util.List;

import am.AMException;
import am.app.ontology.instance.endpoint.SemanticWebEndpoint;

public class InstanceDataset {

	public enum DatasetType {
		ENDPOINT,
		ONTOLOGY,
		DATASET;
	}
	
	private Object instanceSource;
	private DatasetType datasetType;
	
	public InstanceDataset( SemanticWebEndpoint endpoint ) {
		instanceSource = endpoint;
		datasetType = DatasetType.ENDPOINT;
	}

	
	public List<Instance> getInstances( String type, int limit ) {
		List<Instance> instanceList = new ArrayList<Instance>();
		
		try {
			switch( datasetType ) {
			case ENDPOINT: {
				SemanticWebEndpoint endpoint = (SemanticWebEndpoint) instanceSource;
				return endpoint.listInstances( type, limit );
			}
			
			case ONTOLOGY: {
				// TODO: Implement the listing of individuals in an ontology.
				throw new AMException("This method is not implemented.");
			}
			
			case DATASET: {
				// TODO: Implement the listing of individuals in a dataset.
				throw new AMException("This method is not implemented.");
			}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return instanceList;
	}
	
	public List<Instance> getCandidateInstances( String searchTerm, String type) {
		List<Instance> instanceList = new ArrayList<Instance>();
		
		try {
			switch( datasetType ) {
			case ENDPOINT: {
				SemanticWebEndpoint endpoint = (SemanticWebEndpoint) instanceSource;
				return endpoint.freeTextQuery(searchTerm, type);
			}
			
			case ONTOLOGY: {
				// TODO: Implement the listing of individuals in an ontology.
				throw new AMException("This method is not implemented.");
			}
			
			case DATASET: {
				// TODO: Implement the listing of individuals in a dataset.
				throw new AMException("This method is not implemented.");
			}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return instanceList;
	}


	public List<Instance> getInstances() {
		List<Instance> instanceList = new ArrayList<Instance>();
		
		try {
			switch( datasetType ) {
			case ENDPOINT: {
				return null;
			}
			
			case ONTOLOGY: {
				// TODO: Implement the listing of individuals in an ontology.
				throw new AMException("This method is not implemented.");
			}
			
			case DATASET: {
				// TODO: Implement the listing of individuals in a dataset.
				throw new AMException("This method is not implemented.");
			}
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return instanceList;
	}
	
	public boolean isIterable() {
		return datasetType == DatasetType.DATASET || datasetType == DatasetType.ONTOLOGY;
	}
}
