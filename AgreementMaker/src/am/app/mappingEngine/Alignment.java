
package am.app.mappingEngine;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.*;
import am.output.OutputController;


public class Alignment
{
    private Node entity1 = null;
    private Node entity2 = null;
    private double similarity = 0;
    private String relation = null;
    
    private alignType typeOfConcepts = null;
    
    //THE FONT USED IN THE CANVAS MUST BE A UNICODE FONT TO VIEW THIS SPECIAL CHARS
    public final static String EQUIVALENCE = "=";
    public final static String SUBSET = "\u2282";
    public final static String SUPERSET = "\u2283";
    public final static String SUBSETCOMPLETE = "\u2286";
    public final static String SUPERSETCOMPLETE = "\u2287";

    public Alignment(Node e1, Node e2, double sim, String r)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = sim;
        relation = r;
    }

    public Alignment(Node e1, Node e2, double sim)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = sim;
        relation = "=";
    }

    public Alignment(Node e1, Node e2)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = 1.0;
        relation = "=";
    }

    public Alignment(double s) {
		//This is a fake contructor to use an alignment as a container for sim and relation, to move both values between methods
    	similarity = s;
    	relation = EQUIVALENCE;
	}

	public Node getEntity1()
    {
        return entity1;
    }

    public void setEntity1(Node e1)
    {
        entity1 = e1;
    }

    public Node getEntity2()
    {
        return entity2;
    }

    public void setEntity2(Node e2)
    {
        entity2 = e2;
    }

    public void setSimilarity(double sim)
    {
        similarity = sim;
    }

    public double getSimilarity()
    {
        return similarity;
    }



    public String getRelation()
    {
        return relation;
    }

    public void setRelation(String r)
    {
        relation = r;
    }

    public String filter(String s)
    {
        if (s == null) {
            return null;
        } else {
            int index = s.lastIndexOf("#");
            if (index >= 0) {
                return s.substring(index + 1);
            } else {
                index = s.lastIndexOf("/");
                if (index >= 0) {
                    return s.substring(index + 1);
                } else { 
                    return s;
                }
            }
        }
    }
    
    public boolean equals(Alignment alignment)
    {
        if (entity1.equals(alignment.getEntity1()) 
                && entity2.equals(alignment.getEntity2())) {
            return true;
        } else {
            return false;
        }
    }
    

    
    public String toString() {
    	return "("+entity1+" "+entity2+" "+similarity+")";
    }
    

	public String getString() {
		return entity1.getLocalName()+"\t"+OutputController.arrow+"\t"+entity2.getLocalName()+"\t"+getSimilarity()+"\t"+getRelation()+"\n";
	}
	
	public int hashCode(){
		//relation may be added to this definition 
		//map can be replaced with string except empty string
		//this method is used in the PRAintegrationMatcher
		//and in the conference conflict resolution.
		return (entity1.getIndex()+"map"+entity2.getIndex()).hashCode();
	}
	
	public boolean equals(Object o){
		if(o instanceof Alignment){
			Alignment alignment = (Alignment)o;
	        if (entity1.equals(alignment.getEntity1()) 
	                && entity2.equals(alignment.getEntity2())) {
	            return true;
	        } else {
	            return false;
	        }
		}
		return false;
	}
	
	public int getSourceKey(){
		//used in the Conflict resulotion method of the conference track
		//it is based on the idea that in a 1-1 matching a mapping is identified by the sourcenode
		return getEntity1().getIndex();
	}
	
	public int getTargetKey(){
		//used in the Conflict resulotion method of the conference track
		//it is based on the idea that in a 1-1 matching a mapping is identified by the sourcenode
		return getEntity2().getIndex();
	}
	
	public void setAlignmentType( alignType t ) {
		typeOfConcepts = t;
	}
	
	public alignType getAlignmentType() {
		return typeOfConcepts;
	}
}
