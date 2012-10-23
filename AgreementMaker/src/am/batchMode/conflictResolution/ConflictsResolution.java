package am.batchMode.conflictResolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Node;
import am.app.ontology.Ontology;

public class ConflictsResolution {
	
	public ArrayList<AbstractMatcher> solveConflicts(ArrayList<AbstractMatcher> finalMatchers, Ontology[] ontologies, boolean classOrProp) {
		//modifies the mappings sets so that all mappings satisfy the transitive proverty
		//if O1.A = O2.A and O1.A = O3.A then it must be that O2.A = O3.A
		//the resolution is made by favoring mappings those mappings that appear more often in all ontologies
		//using some kind of a voting approach. However, the algorithm is a greedy approach, not the optimal one.
		//it uses an heuristic based on voting to assign a priority to mappings.
		//IT WORKS ONLY WITH 1-1 MATCHINGS
		//AlSo it is very important that the matching algorithm previously executed followed this iteration to match ontologies:
		//		for( int i = 0; i < numOntologies - 1; i++ ) {		
		//			for( int j = i+1; j < numOntologies; j++ ) {
		
		//initial structures (they are very expensive in terms of space)
		//VotedMapping is an object which contains a mapping and the vote assigned to this mapping by all matchings
		//for each set of mapping, we build an HashSet, that given a source node return the VotedMapping which involves that source node in the given set of mappings.
		//these hashSet are kept in a matrix NumOntologies x NumOntologies. The hastSet of VotedMappings between O1 and O2 is contained in the position (1,2) of the matrix
		// since we only performs n(n-2)/2 matchings and not all nxn, the lower part of the matrix will be empty. Therefore, when we scan the matrix we only scan the upper part.
		System.out.println(ontologies[0].getURI());
		//JOptionPane.showInputDialog("asda");
		int numOntologies = ontologies.length;
		//int numAlignments = (numOntologies * (numOntologies - 1))/2;
		VotedMappingSet[][] votedMatrix = new VotedMappingSet[numOntologies][numOntologies];
		Iterator<AbstractMatcher> itMatchers = finalMatchers.iterator();
		AbstractMatcher am;
		int sourceOnt;
		int targetOnt;
		while(itMatchers.hasNext()){
			am = itMatchers.next();
			sourceOnt = am.getSourceOntology().getIndex();
			targetOnt = am.getTargetOntology().getIndex();
			if(classOrProp)
				votedMatrix[sourceOnt][targetOnt] = new VotedMappingSet(am.getClassAlignmentSet(), sourceOnt, targetOnt);
			else votedMatrix[sourceOnt][targetOnt] = new VotedMappingSet(am.getPropertyAlignmentSet(), sourceOnt, targetOnt);
		}
		
		//1) Voting phase.
		//assign a vote to each mapping to establish a priority between mappings
		//the heuristic is that we solve conflicts by giving priority to mapping with higher vote
		//For each mapping establish the positive and negative votes 
		//positive vote = n of sets of mappings coherent with it (they satisfy the transitive property), which include the set of mappings itself containing the mapping, so it's at least 1.
		//negative vote = n of other sets not coherent with this mapping (the transitive prop is not satisfied)
		// final vote = positive -negative. And it can also be a negative value.
		//IMPORTANT this phase is based on the fact that matching process before followed this iteration:
		//		for( int i = 0; i < numOntologies - 1; i++ ) {		
		//			for( int j = i+1; j < numOntologies; j++ ) {
		
		VotedMappingSet currentSet;
		Collection<VotedMapping> currentList;
		Iterator<VotedMapping> itCurrentMappingSet;
		VotedMapping currentMapping;
		VotedMappingSet AvsCset;
		VotedMappingSet BvsCset;
		VotedMapping aVSc1mapping;
		VotedMapping bVSc2mapping;
		//for each set of mappings
		for( int i = 0; i < numOntologies - 1; i++ ) {		
			for( int j = i+1; j < numOntologies; j++ ) {
				currentSet = votedMatrix[i][j];
				currentList = currentSet.getVotedMappings();
				//For each mapping in the mapping set
				itCurrentMappingSet = currentList.iterator();
				while(itCurrentMappingSet.hasNext()){
					currentMapping = itCurrentMappingSet.next();
					//for each other ontology check if the transitive property is satisfied for that mapping or not
					//in particular if the current mapping is between ont A and B (A-B), I will have to check (A-C) VS (B-C), and then (A-D) vs (B-D)
					//therefore, for each column of the matrix (each target ontology), we need to consider the two rows identified by i and j.
					for(int k = j+1; k < numOntologies; k++ ){
						//k is the index of the target ontology C, and we have to get the sets (A-C) and (B-C)
						AvsCset = votedMatrix[i][k];
						BvsCset = votedMatrix[j][k];
						//given the mapping a-b, I need to get a-c1 and b-c2,
						//set.getVotedMapping(node n) return the mapping in the set with source equal to n
						aVSc1mapping = AvsCset.getSourceVotedMapping(currentMapping.mapping.getEntity1());
						bVSc2mapping = BvsCset.getSourceVotedMapping(currentMapping.mapping.getEntity2());
						
						//Check the TRANSITIVE PROPERTY
						//c1 and c2 are null, it means that the mapping doesn't exist between A-C and B-C
						//then the transitive property is satisfied but is not supportive to state the a-b is a good mapping
						if(aVSc1mapping == null && bVSc2mapping == null){
							//do nothing, neither pos nor neg
						}
						//if c1 == null and c2 is not, then the transitive is not satisfied
						else if(aVSc1mapping == null){
							currentMapping.negativeVote++;
							bVSc2mapping.negativeVote++;
						}
						//vice-versa
						else if(bVSc2mapping == null){
							currentMapping.negativeVote++;
							aVSc1mapping.negativeVote++;
						}
						//if c1 = c2 then positive vote += 1 for all of these mappings
						else if(aVSc1mapping.sameTarget(bVSc2mapping)){
							currentMapping.positiveVote++;
							aVSc1mapping.positiveVote++;
							bVSc2mapping.positiveVote++;
						}
						//else c1 and c2 are different
						else{
							currentMapping.negativeVote++;
							aVSc1mapping.negativeVote++;
							bVSc2mapping.negativeVote++;
						}
					}
					
					//just to speed up the processing later, but if pos = 1 and neg = 0, it means that
					//the mapping has no conflicts, and no related mappings, so it is only going to be validated later
					//therefore i set it to validate now
					//if(currentMapping.positiveVote == 1 && currentMapping.negativeVote == 0){
					//	currentMapping.validated = true;
					//}
				}
			}
		}
		
		//Voting phase debugging
		//currentSet = votedMatrix[1][3];
		//System.out.println(currentSet);


		
		
		//***now the conflict resolution part
		//first, the heuristic is applied
		//we start to solve conflicts by looking at the mappings with higher votes (pos - neg) that have not been validated yet
		//the node selected this way is considered as correct, and consequently related mappings are adjusted
		//when all conflicts are solved for a mapping it is set as validated and it means that is going to be in the final alignment
		//sometimes to solve the conflicts some mappings may be completely removed, others may be added
		//at the end all mappings should be either removed or validated
		
		//the algorithm continues until all mappings are validated
		//if all matchings are scanned without defining a maximum it means that all are validated
		//VotedMappingSet maxSet = null;
		VotedMapping maxMapping = null;
		do{
			//maxSet = null;
			maxMapping = null;
			//1) get the mapping not yet validated with highest vote
			for( int i = 0; i < numOntologies - 1; i++ ) {		
				for( int j = i+1; j < numOntologies; j++ ) {
					currentSet = votedMatrix[i][j];
					currentList = currentSet.getVotedMappings();
					//For each mapping in the mapping set
					itCurrentMappingSet = currentList.iterator();
					while(itCurrentMappingSet.hasNext()){
						currentMapping = itCurrentMappingSet.next();
						//validated mappings must be skipped
						if(!currentMapping.validated){
							//the max should have higher vote, or same vote but higher similarity
							if(maxMapping == null || maxMapping.getFinalVote() < currentMapping.getFinalVote() || (maxMapping.getFinalVote() == currentMapping.getFinalVote() && maxMapping.mapping.getSimilarity() < currentMapping.mapping.getSimilarity())){
								//Found a new maximum
								maxMapping = currentMapping;
								//maxSet = currentSet;
							}
						}

					}
				}
			}
			itCurrentMappingSet = null;
			//found the overall maximum
			//we set as validated and we solve conflicts consequently
			Node c1 = null;
			Node c2 = null;
			Mapping tempAlignment1 = null;
			Mapping tempAlignment2 = null;
			VotedMapping toBeDeleted1 = null;
			VotedMapping toBeDeleted2 = null;
			if(maxMapping!= null){
				//System.out.println("maxMapping diff from null: "+maxMapping);  
				//Queue<VotedMapping> queue = new LinkedList<VotedMapping>();
				Stack<VotedMapping> queue = new Stack<VotedMapping>();
				maxMapping.validated = true;
				queue.add(maxMapping);
				while(!queue.isEmpty()){
					
					//currentMapping is (a-b)
					//currentMapping = queue.poll();//get first element, by construction it must heuristically be a validated mapping
					currentMapping = queue.pop();
					int currentSource = currentMapping.mappingSet.sourceOntologyIndex;
					int currentTarget = currentMapping.mappingSet.targetOntologyIndex;
					//System.out.println("qsize: "+queue.size()+", extracted from "+currentSource+"-"+currentTarget+", mapping :"+currentMapping);
					//let's say that this mapping refers to the matching A-B
					//now for each other ontology C, I have to consider A-C (or C-A) and B-C (or C-B), A and B may be both source or target dependending on the matching
					for(int k = 0; k < numOntologies; k++){
						if(k != currentSource && k != currentTarget){//k identifies the third ontology to be considered
							
							AvsCset = null;
							BvsCset = null;
							aVSc1mapping = null;
							bVSc2mapping = null;
							c1 = null;
							c2 = null;
							tempAlignment1 = null;
							tempAlignment2 = null;
							toBeDeleted1 = null;
							toBeDeleted2 = null;
							if(k < currentSource){//it means that the matching is K-A
								
								AvsCset = votedMatrix[k][currentSource];
								aVSc1mapping = AvsCset.getTargetVotedMapping(currentMapping.mapping.getEntity1());//the mapping is c1-a
								if(aVSc1mapping!=null){
									c1 = aVSc1mapping.mapping.getEntity1();
									//System.out.println("checking "+k+"-"+currentSource+" mapping "+aVSc1mapping);
								}
							}
							else{//it's A-K
								AvsCset = votedMatrix[currentSource][k];
								aVSc1mapping = AvsCset.getSourceVotedMapping(currentMapping.mapping.getEntity1());//the mapping is a-c1
								if(aVSc1mapping!=null){
									c1 = aVSc1mapping.mapping.getEntity2();
									//System.out.println("checking "+currentSource+"-"+k+" mapping "+aVSc1mapping);
								}
							}
							
							if(k < currentTarget){//it means that the matching is K-B
								BvsCset = votedMatrix[k][currentTarget];
								bVSc2mapping = BvsCset.getTargetVotedMapping(currentMapping.mapping.getEntity2());//the mapping is c2-b
								if(bVSc2mapping!=null){
									c2 = bVSc2mapping.mapping.getEntity1();
									//System.out.println("checking "+k+"-"+currentTarget+" mapping "+bVSc2mapping);
								}
									
							}
							else{//it's source-K
								BvsCset = votedMatrix[currentTarget][k];
								bVSc2mapping = BvsCset.getSourceVotedMapping(currentMapping.mapping.getEntity2());//the mapping is b-c2
								if(bVSc2mapping!=null){
									c2 = bVSc2mapping.mapping.getEntity2();
									//System.out.println("checking "+currentTarget+"-"+k+" mapping "+bVSc2mapping);
								}
									
							}
							
							//prepare the alignments that will be added if needed
							if(k < currentSource){
								//need to add c2-a
								tempAlignment1 = new Mapping(c2, currentMapping.mapping.getEntity1(), currentMapping.mapping.getSimilarity(), currentMapping.mapping.getRelation());
								if(c2 != null)
									toBeDeleted1 = AvsCset.getSourceVotedMapping(c2);
							}
							else{
								//need to add a-c2
								tempAlignment1 = new Mapping(currentMapping.mapping.getEntity1(),c2,  currentMapping.mapping.getSimilarity(), currentMapping.mapping.getRelation());
								if(c2 != null)
									toBeDeleted1 = AvsCset.getTargetVotedMapping(c2);
							}
							if(k < currentTarget){
								//need to add c1-b
								tempAlignment2 = new Mapping(c1, currentMapping.mapping.getEntity2(), currentMapping.mapping.getSimilarity(), currentMapping.mapping.getRelation());
								if(c1 != null)
									toBeDeleted2 = BvsCset.getSourceVotedMapping(c1);
							}
							else{
								//need to add b-c1
								tempAlignment2 = new Mapping(currentMapping.mapping.getEntity2(),c1,  currentMapping.mapping.getSimilarity(), currentMapping.mapping.getRelation());
								if(c1 != null)
									toBeDeleted2 = BvsCset.getTargetVotedMapping(c1);
							}
							
							//case 1: no mapping found in the third of ontologies for both A and B, so everything is fine
							if(aVSc1mapping == null && bVSc2mapping == null){
								//do nothing
								//System.out.println("case 1 no no");
							}
							//case 2: B is mapped and A is not -> conflict
							//solution map also the second one
							else if(aVSc1mapping == null){
								
								aVSc1mapping = new VotedMapping(tempAlignment1,AvsCset);
								if(toBeDeleted1!=null){
									AvsCset.delVotedMapping(toBeDeleted1);
								}
								aVSc1mapping.added = true;
								AvsCset.putVotedMapping(aVSc1mapping);
								//System.out.println("case 2 a is null, new a is"+aVSc1mapping);
							}
							//case 3: A is mapped and B is not -> conflict
							//solution map also the second one
							else if(bVSc2mapping == null){
								bVSc2mapping = new VotedMapping(tempAlignment2,BvsCset);
								if(toBeDeleted2!=null){
									BvsCset.delVotedMapping(toBeDeleted2);
								}
								bVSc2mapping.added = true;
								BvsCset.putVotedMapping(bVSc2mapping);
								//System.out.println("case 3 b is null, new b is"+bVSc2mapping);
							}
							//case 4: they are mapped to the same concept
							//no conflict
							else if(c1.getIndex() == c2.getIndex()){
								//System.out.println("case 4 same conc");
								//do nothing
							}
							//case 5: they are mapped to different concepts c1 != c2
							//this is the hardest case, one of the two must become equal to the other
							//5a: both are already validated which means that they can't be removed, the algorithm is wrong
							//5b: one of the two is validated, so just remove the other and replace it 
							//5c: both are non validated, replace the one with lower vote, if same vote look at similarity
							else{
								//5a
								//System.out.println("case 5");
								if(aVSc1mapping.validated && bVSc2mapping.validated){
									throw new RuntimeException("Development error in conflict resolution: both mappings are validated but in conflict");
									//BvsCset.delVotedMapping(bVSc2mapping);
									//AvsCset.delVotedMapping(aVSc1mapping);
									//break;
								}
								//5b1: a-c1 is correct
								else if(aVSc1mapping.validated){
									
									//remove b-c2 and add b-c1
									BvsCset.delVotedMapping(bVSc2mapping);
									bVSc2mapping = new VotedMapping(tempAlignment2, BvsCset);
									if(toBeDeleted2!=null){
										BvsCset.delVotedMapping(toBeDeleted2);
									}
									bVSc2mapping.added = true;
									BvsCset.putVotedMapping(bVSc2mapping);
									//System.out.println("case 5b1 a valid change b in "+bVSc2mapping);
								}
								//5b2: b-c2 is correct
								else if(bVSc2mapping.validated){
									//remove a-c1 and add a-c2
									AvsCset.delVotedMapping(aVSc1mapping);
									aVSc1mapping = new VotedMapping(tempAlignment1, AvsCset);
									if(toBeDeleted1!=null){
										AvsCset.delVotedMapping(toBeDeleted1);
									}
									aVSc1mapping.added = true;
									AvsCset.putVotedMapping(aVSc1mapping);
									//System.out.println("case 5b2 b valid change a in "+aVSc1mapping);
								}
								//5c: both are not valid yet we have to pick one
								else{
									//System.out.println("case 5c not valid");
									if(aVSc1mapping.getFinalVote() > bVSc2mapping.getFinalVote() || (aVSc1mapping.getFinalVote() == bVSc2mapping.getFinalVote() && aVSc1mapping.mapping.getSimilarity() > bVSc2mapping.mapping.getSimilarity())){
										//a-c1 wins so b-c2 becomes b-c1
										//remove b-c2 and add b-c1
										BvsCset.delVotedMapping(bVSc2mapping);
										if(toBeDeleted2!=null){
											BvsCset.delVotedMapping(toBeDeleted2);
										}
										bVSc2mapping = new VotedMapping(tempAlignment2, BvsCset);
										bVSc2mapping.added = true;
										BvsCset.putVotedMapping(bVSc2mapping);
										//System.out.println("a > b new b "+bVSc2mapping);
									}
									else{
										//remove a-c1 and add a-c2
										AvsCset.delVotedMapping(aVSc1mapping);
										if(toBeDeleted1!=null){
											AvsCset.delVotedMapping(toBeDeleted1);
										}
										aVSc1mapping = new VotedMapping(tempAlignment1, AvsCset);
										aVSc1mapping.added = true;
										AvsCset.putVotedMapping(aVSc1mapping);
										//System.out.println("b > a new a "+aVSc1mapping);
									}
								}
							}
							
							//ALL CONFLICTS have been solved for A-C and A-B so add and validate the mappings
							if(aVSc1mapping != null && !aVSc1mapping.validated){
								aVSc1mapping.validated = true;
								queue.add(aVSc1mapping);
								//System.out.println("validated a "+aVSc1mapping);
								if(aVSc1mapping.added){
									if(aVSc1mapping.mappingSet.sourceOntologyIndex == 0 && aVSc1mapping.mappingSet.targetOntologyIndex == 2){
										if(aVSc1mapping.mapping.getEntity1().getIndex() == 49 && aVSc1mapping.mapping.getEntity2().getIndex() == 35){
											//System.out.println("STRANGE 0-2");
											//System.out.println("toBeDeleted1 "+toBeDeleted1);
											//System.out.println("toBeDeleted2 "+toBeDeleted2);
											//System.out.println(AvsCset.getSourceVotedMapping(49));
											//System.out.println(AvsCset.getTargetVotedMapping(35));
											//JOptionPane.showInputDialog("blabla");
											
										}
									}
									if(aVSc1mapping.mappingSet.sourceOntologyIndex == 2 && aVSc1mapping.mappingSet.targetOntologyIndex == 5){
										if(aVSc1mapping.mapping.getEntity1().getIndex() == 35 && aVSc1mapping.mapping.getEntity2().getIndex() == 11){
											//System.out.println("STRANGE 2-5");
											//System.out.println("toBeDeleted1 "+toBeDeleted1);
											//System.out.println("toBeDeleted2 "+toBeDeleted2);
											//System.out.println(AvsCset.getSourceVotedMapping(35));
											//System.out.println(AvsCset.getTargetVotedMapping(11));
											//JOptionPane.showInputDialog("blabla");
											
										}
									}
								}
								if(aVSc1mapping.mappingSet.sourceOntologyIndex == 0 && aVSc1mapping.mappingSet.targetOntologyIndex == 5){
									if(aVSc1mapping.mapping.getEntity1().getIndex() == 10 && aVSc1mapping.mapping.getEntity2().getIndex() == 11){
										//System.out.println("STRANGE 0-5");
										//System.out.println("toBeDeleted1 "+toBeDeleted1);
										//System.out.println("toBeDeleted2 "+toBeDeleted2);
										//System.out.println(AvsCset.getSourceVotedMapping(10));
										//System.out.println(AvsCset.getTargetVotedMapping(11));
										//JOptionPane.showInputDialog("blabla");
										
									}
								}
								if(aVSc1mapping.mappingSet.sourceOntologyIndex == 2 && aVSc1mapping.mappingSet.targetOntologyIndex == 10){
									if(aVSc1mapping.mapping.getEntity1().getIndex() == 35 && aVSc1mapping.mapping.getEntity2().getIndex() == 42){
										//System.out.println("STRANGE 2-10");
										//System.out.println("toBeDeleted1 "+toBeDeleted1);
										//System.out.println("toBeDeleted2 "+toBeDeleted2);
										//System.out.println(AvsCset.getSourceVotedMapping(35));
										//System.out.println(AvsCset.getTargetVotedMapping(42));
										//JOptionPane.showInputDialog("blabla");
										
									}
								}
								
							}
							if(bVSc2mapping != null && !bVSc2mapping.validated){
								bVSc2mapping.validated = true;
								queue.add(bVSc2mapping);
								//System.out.println("validated b "+bVSc2mapping);
								if(bVSc2mapping.added){
									if(bVSc2mapping.mappingSet.sourceOntologyIndex == 0 && bVSc2mapping.mappingSet.targetOntologyIndex == 2){
										if(bVSc2mapping.mapping.getEntity1().getIndex() == 49 && bVSc2mapping.mapping.getEntity2().getIndex() == 35){
											//System.out.println("STRANGE 0-2");
											//System.out.println("toBeDeleted2 "+toBeDeleted2);
											//System.out.println("toBeDeleted1 "+toBeDeleted1);
											////System.out.println("get source 18 "+BvsCset.getSourceVotedMapping(18));
											//System.out.println(BvsCset.getSourceVotedMapping(49));
											//System.out.println(BvsCset.getTargetVotedMapping(35));
											
											//JOptionPane.showInputDialog("blabla");
										}
									}
									if(bVSc2mapping.mappingSet.sourceOntologyIndex == 2 && bVSc2mapping.mappingSet.targetOntologyIndex == 5){
										if(bVSc2mapping.mapping.getEntity1().getIndex() == 35 && bVSc2mapping.mapping.getEntity2().getIndex() == 11){
											//System.out.println("STRANGE 2-5");
											//System.out.println("toBeDeleted2 "+toBeDeleted2);
											//System.out.println("toBeDeleted1 "+toBeDeleted1);
											////System.out.println("get source 18 "+BvsCset.getSourceVotedMapping(18));
											//System.out.println(BvsCset.getSourceVotedMapping(35));
											//System.out.println(BvsCset.getTargetVotedMapping(11));
											
											//JOptionPane.showInputDialog("blabla");
										}
									}
								}
								if(bVSc2mapping.mappingSet.sourceOntologyIndex == 0 && bVSc2mapping.mappingSet.targetOntologyIndex == 5){
									if(bVSc2mapping.mapping.getEntity1().getIndex() == 10  && bVSc2mapping.mapping.getEntity2().getIndex() == 11){
										//System.out.println("STRANGE 0-5");
										//System.out.println("toBeDeleted1 "+toBeDeleted1);
										//System.out.println("toBeDeleted2 "+toBeDeleted2);
										//System.out.println(BvsCset.getSourceVotedMapping(10));
										//System.out.println(BvsCset.getTargetVotedMapping(11));
										//JOptionPane.showInputDialog("blabla");
										
									}
								}
								if(bVSc2mapping.mappingSet.sourceOntologyIndex == 2 && bVSc2mapping.mappingSet.targetOntologyIndex == 10){
									if(bVSc2mapping.mapping.getEntity1().getIndex() == 35  && bVSc2mapping.mapping.getEntity2().getIndex() == 42){
										//System.out.println("STRANGE 2-10");
										//System.out.println("toBeDeleted1 "+toBeDeleted1);
										//System.out.println("toBeDeleted2 "+toBeDeleted2);
										//System.out.println(BvsCset.getSourceVotedMapping(35));
										//System.out.println(BvsCset.getTargetVotedMapping(42));
										//JOptionPane.showInputDialog("blabla");
										
									}
								}
							}	
						}
					}
				}
			}
		}
		while(maxMapping != null);
		//END of conflict resolution
		
		//final phase, create the new modified mapping sets
		//we modify the alignments of the matcher in input but we return the same matcher objects
		//ArrayList<AbstractMatcher> result = new ArrayList<AbstractMatcher>();
		itMatchers = finalMatchers.iterator();
		while(itMatchers.hasNext()){
			am = itMatchers.next();
			sourceOnt = am.getSourceOntology().getIndex();
			targetOnt = am.getTargetOntology().getIndex();
			
			if(classOrProp)
				am.setClassesAlignmentSet(votedMatrix[sourceOnt][targetOnt].getAlignmentSet());
			else am.setPropertiesAlignmentSet(votedMatrix[sourceOnt][targetOnt].getAlignmentSet());
			//result.add(am);
		}
		
		return finalMatchers;
	}
	
}
