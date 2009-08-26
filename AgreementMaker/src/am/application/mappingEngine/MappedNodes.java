package am.application.mappingEngine;

import java.util.ArrayList;

import am.application.ontology.Node;

public class MappedNodes {
	//true if the node is mapped at least the minimum number of times expressed in the cardinality
	protected boolean[] mappedSources;
	protected boolean[] mappedTargets;
	
	public boolean[] getMappedSources() {
		return mappedSources;
	}
	public void setMappedSources(boolean[] mappedSources) {
		this.mappedSources = mappedSources;
	}
	public boolean[] getMappedTargets() {
		return mappedTargets;
	}
	public void setMappedTargets(boolean[] mappedTargets) {
		this.mappedTargets = mappedTargets;
	}
	
	public MappedNodes(ArrayList<Node> sources, ArrayList<Node> targets, AlignmentSet alignment, int sourceCardinality, int targetCardinality){
		//assumption is that java init an array of boolean to false
		//if not we should set all values to false
		mappedSources = new boolean[sources.size()];
		mappedTargets = new boolean[targets.size()];
		
		int[] numOfSourceMappings = new int[mappedSources.length];
		int[] numOfTargetMappings = new int[mappedTargets.length];
		
		Alignment mapping;
		Node source;
		Node target;
		for(int i = 0; i < alignment.size(); i++){
			mapping = alignment.getAlignment(i);
			source = mapping.getEntity1();
			target = mapping.getEntity2();
			numOfSourceMappings[source.getIndex()] += 1;
			if(numOfSourceMappings[source.getIndex()] >= sourceCardinality){
				mappedSources[source.getIndex()] = true;
			}
			numOfTargetMappings[target.getIndex()] += 1;
			if(numOfTargetMappings[target.getIndex()] >= targetCardinality){
				mappedTargets[target.getIndex()] = true;
			}
		}
		
		numOfSourceMappings = null;
		numOfTargetMappings = null;
	}
	
	public boolean isSourceMapped(Node source){
		return mappedSources[source.getIndex()];
	}
	
	public boolean isTargetMapped(Node target){
		return mappedTargets[target.getIndex()];
	}

}
