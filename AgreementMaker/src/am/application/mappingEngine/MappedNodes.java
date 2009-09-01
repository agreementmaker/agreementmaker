package am.application.mappingEngine;

import java.util.ArrayList;

import am.application.ontology.Node;

public class MappedNodes {
	//true if the node is mapped at least the minimum number of times expressed in the cardinality
	protected int[] mappedSources;
	protected int[] mappedTargets;
	protected int sourceCardinality;
	protected int targetCardinality;
	
	public int[] getMappedSources() {
		return mappedSources;
	}

	public int[] getMappedTargets() {
		return mappedTargets;
	}
	
	public MappedNodes(ArrayList<Node> sources, ArrayList<Node> targets, AlignmentSet alignment, int sourceCardinality, int targetCardinality){
		//assumption is that java init an array of boolean to false
		//if not we should set all values to false
		mappedSources = new int[sources.size()];
		mappedTargets = new int[targets.size()];
		this.sourceCardinality = sourceCardinality;
		this.targetCardinality = targetCardinality;
		
		Alignment mapping;
		Node source;
		Node target;
		for(int i = 0; i < alignment.size(); i++){
			mapping = alignment.getAlignment(i);
			source = mapping.getEntity1();
			target = mapping.getEntity2();
			mappedSources[source.getIndex()] += 1;
			mappedTargets[target.getIndex()] += 1;
		}
		
	}
	
	public boolean isSourceMapped(Node source){
		return mappedSources[source.getIndex()] >= sourceCardinality;
	}
	
	public boolean isTargetMapped(Node target){
		return mappedTargets[target.getIndex()] >= targetCardinality;
	}
	
	public int getNumSourceMappings(Node source){
		return mappedSources[source.getIndex()];
	}
	
	public int getNumTargetMappings(Node target){
		return mappedTargets[target.getIndex()];
	}

	public void addAlignment(Alignment alignment) {
		mappedSources[alignment.getEntity1().getIndex()]+=1;
		mappedTargets[alignment.getEntity2().getIndex()]+=1;
		
	}

}
