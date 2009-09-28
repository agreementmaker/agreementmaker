package am.batchMode.conflictResolution;

import am.application.mappingEngine.Alignment;

public class VotedMapping {
	
	
	boolean validated = false;//when this is set to true it means that this mapping is confirmed as valid and will be selected at the end
	boolean added = false;
	
	public Alignment mapping;
	
	public int positiveVote = 1; //each mapping is at least voted by the set of mapping in which it is included
	public int negativeVote = 0; //these values will be incremented by the algorithm in the voting phase
	//public int neutralVote; //if neither the source nor the target nodes are mapped with a third node in a third ontology the vote is netrual, the transitive property is satisfied but it doesn't support the mapping
	
	public VotedMappingSet mappingSet;
	
	public VotedMapping(Alignment map, VotedMappingSet mapSet){
		mapping = map;
		mappingSet = mapSet;
	}
	
	public int getFinalVote(){
		return positiveVote - negativeVote;
	}
	
	public boolean sameTarget(VotedMapping v){
		return this.mapping.getEntity2().getIndex() == v.mapping.getEntity2().getIndex();
	}
	
	public boolean sameSource(VotedMapping v){
		return this.mapping.getEntity1().getIndex() == v.mapping.getEntity1().getIndex();
	}
	
	public String toString(){
		return "VotedMapping: f= "+getFinalVote()+", p= "+positiveVote+", n = "+negativeVote+", added "+added+", val "+validated+", map"+mapping;
	}
	

}
