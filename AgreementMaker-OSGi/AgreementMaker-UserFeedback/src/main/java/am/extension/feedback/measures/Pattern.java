package am.app.feedback.measures;

import java.util.ArrayList;
import java.util.List;

public class Pattern  {
	private int length;
	private ArrayList<Edge> edgeSequence;
	private int lastAssignedVisit;
	private static int lastVisit;

	public Pattern(List<Edge> edgeSeq, Pattern p){
		
		edgeSequence = new ArrayList<Edge>();
		if(p != null){
			for(Edge e: p.edgeSequence){
				edgeSequence.add(e);
				length++;
			}
		}
		edgeSequence.add( edgeSeq.get(0) );
		length++;
		
	}
	
	public Pattern(Pattern p){
		length = p.length;
		edgeSequence = new ArrayList<Edge>();
		for(Edge e: p.edgeSequence){
			edgeSequence.add(e);
		}
	}
	
	public String toString(){
		String out = "Length: " + length + ", EdgeSequence: ";
		for(Edge e : edgeSequence){
			out += e.getSourceNode().getLocalName();
			out += ", ";
			out += e.getTargetNode().getLocalName();
			out += " -- ";
		}
		return out;
	}
	
	public boolean equals(Pattern p){
		Edge anEdge, nextEdge;
		if(this.length != p.length){
			return false;
		}
		else{
			for(int i = 0; i < edgeSequence.size(); i++){
				anEdge = edgeSequence.get(i);
				nextEdge = p.getEdgeAtIndex(i);
				if(anEdge.getSourceVisit() == nextEdge.getSourceVisit()
						&& anEdge.getTargetVisit() == nextEdge.getTargetVisit()
						&& anEdge.getSourceNode().getIndex() == nextEdge.getSourceNode().getIndex()
						&& anEdge.getTargetNode().getIndex() == nextEdge.getTargetNode().getIndex()
						)
						{
							return true;
						}
			}
		}
		return false;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public void setLength(int len)
	{
		length = len;
	}
	
	public void setEdgeSequence(ArrayList<Edge> edgeSeq)
	{
		edgeSequence = edgeSeq;
	}
	
	public ArrayList<Edge> getEdgeSequence()
	{
		return edgeSequence;
	}
	
	public Edge getEdgeAtIndex(int i)
	{
		return edgeSequence.get(i);
	}


	public void setLastVisit(int lastVisit) {
		Pattern.lastVisit = lastVisit;
	}


	public int getLastVisit() {
		return lastVisit;
	}

	public void setLastAssignedVisit(int lastAssignedVisit) {
		this.lastAssignedVisit = lastAssignedVisit;
	}

	public int getLastAssignedVisit() {
		return lastAssignedVisit;
	}
}

