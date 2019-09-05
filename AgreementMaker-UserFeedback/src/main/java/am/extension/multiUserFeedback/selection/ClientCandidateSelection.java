package am.extension.multiUserFeedback.selection;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.ontology.Node;
import am.extension.collaborationClient.api.CollaborationCandidateMapping;
import am.extension.userfeedback.experiments.MLFExperiment;
import am.extension.userfeedback.selection.CandidateSelection;

public class ClientCandidateSelection extends CandidateSelection<MLFExperiment>{
	
	protected MLFExperiment experiment;
	
	public ClientCandidateSelection(){
		super();
	}
	
	@Override
	public void rank(MLFExperiment exp) {
		this.experiment=exp;
		
		CollaborationCandidateMapping a = experiment.server.getCandidateMapping(experiment.clientID);
		experiment.candidateMapping = a;
		if( a == null ) {
			selectedMapping = null;
		}
		else {
			Node source=experiment.getSourceOntology().getNodeByURI(a.getSourceURI());
			Node target=experiment.getTargetOntology().getNodeByURI(a.getTargetURI());
			selectedMapping = new Mapping(source,target,0.0,MappingRelation.EQUIVALENCE);
		}
		
		done();
	}

	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking) {
		throw new RuntimeException("Not implemented.");
	}

	@Override
	public List<Mapping> getRankedMappings() {
		throw new RuntimeException("Not implemented.");
	}
}
