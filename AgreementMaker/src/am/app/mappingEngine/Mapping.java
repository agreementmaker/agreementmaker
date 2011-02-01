
package am.app.mappingEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.*;
import am.output.OutputController;

/**
 * This class represents a mapping between two concepts from separate ontologies.
 */
public class Mapping implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 7594296130576633495L;
	
	private Node entity1 = null;
    private Node entity2 = null;
    private double similarity = 0;
    private String relation = null;
    private String provenance;

	private alignType typeOfConcepts = null;
    
    //THE FONT USED IN THE CANVAS MUST BE A UNICODE FONT TO VIEW THIS SPECIAL CHARS
    public final static String EQUIVALENCE = "=";
    public final static String SUPERCLASS = ">";
    public final static String SUBCLASS = "<";
    public final static String SUBSET = "\u2282";
    public final static String SUPERSET = "\u2283";
    public final static String SUBSETCOMPLETE = "\u2286";
    public final static String SUPERSETCOMPLETE = "\u2287";

    
    public static String parseRelation(String r) {
    	if( r.equals(EQUIVALENCE.trim()) ) return EQUIVALENCE;
    	if( r.equals(SUPERCLASS.trim())) return SUPERCLASS;
    	if( r.equals(SUBCLASS.trim()) ) return SUBCLASS;
    	if( r.equals(SUBSET.trim()) ) return SUBSET;
    	if( r.equals(SUPERSET.trim()) ) return SUPERSET;
    	if( r.equals(SUBSETCOMPLETE.trim()) ) return SUBSETCOMPLETE;
    	if( r.equals(SUPERSETCOMPLETE.trim()) ) return SUPERSETCOMPLETE;
    	
    	return "?";  // unknown relation
    }
    
    /* Constructors */
    
    public Mapping(Node e1, Node e2, double sim, String r, alignType tyoc)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = sim;
        relation = r;
        typeOfConcepts = tyoc;
    }
    
    public Mapping(Node e1, Node e2, double sim, String r)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = sim;
        relation = r;
        if( entity1.getType().equals( Node.OWLCLASS ) || entity1.getType().equals( Node.RDFNODE ) || entity1.getType().equals( Node.XMLNODE )  ) {
        	typeOfConcepts = alignType.aligningClasses;
        } else {
        	typeOfConcepts = alignType.aligningProperties;
        }
    }

    public Mapping(Node e1, Node e2, double sim)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = sim;
        relation = "=";
        if( entity1.getType().equals( Node.OWLCLASS ) || entity1.getType().equals( Node.RDFNODE ) || entity1.getType().equals( Node.XMLNODE )  ) {
        	typeOfConcepts = alignType.aligningClasses;
        } else {
        	typeOfConcepts = alignType.aligningProperties;
        }
    }

    public Mapping(Node e1, Node e2)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = 1.0;
        relation = "=";
        if( entity1.getType().equals( Node.OWLCLASS ) || entity1.getType().equals( Node.RDFNODE ) || entity1.getType().equals( Node.XMLNODE )  ) {
        	typeOfConcepts = alignType.aligningClasses;
        } else {
        	typeOfConcepts = alignType.aligningProperties;
        }
    }

    public Mapping(double s) {
		//This is a fake contructor to use an alignment as a container for sim and relation, to move both values between methods
    	similarity = s;
    	relation = EQUIVALENCE;
	}

	public Mapping(Mapping old) {
		this.entity1 = old.entity1;
	    this.entity2 = old.entity2;
	    this.similarity = old.similarity;
	    this.relation = old.relation;
	    
	    this.typeOfConcepts = old.typeOfConcepts;
	}

	/* Access methods */
	
	public Node getEntity1() { return entity1; }
	public void setEntity1(Node e1) { entity1 = e1; }

	public Node getEntity2() { return entity2; }
	public void setEntity2(Node e2) { entity2 = e2; }

	public void setSimilarity(double sim) { similarity = sim; }
	public double getSimilarity() { return similarity; }

	public String getRelation() { return relation; }
	public void setRelation(String r) { relation = r; }

	public String getProvenance() { return provenance; }
	public void setProvenance(String provenance) { this.provenance = provenance; }

	public String toString() { return "("+entity1.toString()+" -> "+entity2.toString()+
		": "+similarity+" "+relation+" )"; }
	public String getString() { return entity1.getLocalName()+"\t"+OutputController.arrow+"\t"+
		entity2.getLocalName()+"\t"+getSimilarity()+"\t"+getRelation()+"\n"; }
	
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
	
	public void setAlignmentType( alignType t ) { typeOfConcepts = t; }
	public alignType getAlignmentType() { return typeOfConcepts; }

	
	/* Methods that calculate */
	
    public static String filter(String s)
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
    
    /**
     * Returns true if the input Mapping is considering the same source and target concepts.
     * Does not check for similarity equality.
     */
    public boolean equals(Mapping alignment)
    {
        if (entity1.equals(alignment.getEntity1()) 
                && entity2.equals(alignment.getEntity2())) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean equals(Object o){
		if(o instanceof Mapping){
			Mapping alignment = (Mapping)o;
	        if (entity1.equals(alignment.getEntity1()) 
	                && entity2.equals(alignment.getEntity2())) {
	            return true;
	        } else {
	            return false;
	        }
		}
		return false;
	}
    
	
	public int hashCode(){
		//relation may be added to this definition 
		//map can be replaced with string except empty string
		//this method is used in the PRAintegrationMatcher
		//and in the conference conflict resolution.
		return (entity1.getIndex()+"map"+entity2.getIndex()).hashCode();
	}
	
	
	
	
	/** ****************** Serialization methods *******************/
	
	private void readObject( ObjectInputStream in ) throws ClassNotFoundException, IOException {

		in.defaultReadObject();

		// fix the reference of the Nodes
		
		Ontology sourceOntology = Core.getInstance().getSourceOntology();
		Ontology targetOntology = Core.getInstance().getTargetOntology();

		Node sourceNode = null;
		Node targetNode = null;

		if( this.getAlignmentType() == alignType.aligningClasses ) {
			// class nodes
			sourceNode = sourceOntology.getClassesList().get( this.getSourceKey() );
			targetNode = targetOntology.getClassesList().get( this.getTargetKey() );
		} else {
			// property nodes
			sourceNode = sourceOntology.getPropertiesList().get( this.getSourceKey() );
			targetNode = targetOntology.getPropertiesList().get( this.getTargetKey() );
		}

		entity1 = sourceNode;
		entity2 = targetNode;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}
	
	/*
	  protected void testSerialization(){
		  Mapping a = null;
			try {
				writeObject(new ObjectOutputStream(new FileOutputStream("testFile")));
				a = readObject(new ObjectInputStream(new FileInputStream("testFile")));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(a.similarity);
	  }
	  */
}
