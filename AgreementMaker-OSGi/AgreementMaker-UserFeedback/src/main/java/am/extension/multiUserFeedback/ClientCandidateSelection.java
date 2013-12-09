package am.extension.multiUserFeedback;

import java.util.List;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping.MappingRelation;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;
import am.extension.collaborationClient.api.CollaborationCandidateMapping;
import am.extension.userfeedback.CandidateSelection;

public class ClientCandidateSelection extends CandidateSelection<MUExperiment>{
	
	MUExperiment experiment;
	
	public ClientCandidateSelection(){
		super();
	}
	
	@Override
	public void rank(MUExperiment exp) {
		// TODO Auto-generated method stub
		this.experiment=exp;
	}

	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Mapping> getRankedMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mapping getCandidateMapping() {
		CollaborationCandidateMapping a=experiment.server.getCandidateMapping(experiment.clientID);
		Node source=experiment.getSourceOntology().getNodeByURI(a.getSourceURI());
		Node target=experiment.getTargetOntology().getNodeByURI(a.getTargetURI());
		selectedMapping=new Mapping(source,target,0.0,MappingRelation.EQUIVALENCE);
		return selectedMapping;
	}

	@Override
	public Mapping getSelectedMapping() {
		// TODO Auto-generated method stub
		return selectedMapping;
	}



}
