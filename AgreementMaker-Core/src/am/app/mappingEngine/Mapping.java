
package am.app.mappingEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.ontology.AMNode;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.parsing.OutputController;
import am.utility.BitVector;

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
    private MappingRelation relation = null;
    private String provenance;
    private BitVector featuresBitVector; // This integer represents a bit vector of the matcher features used to calculate the similarity

	private alignType typeOfConcepts = null;
    
    public enum MappingRelation {
    	EQUIVALENCE		 ("=", " = "),
    	SUPERCLASS       (")", "> c"),
    	SUBCLASS         ("(", "< c"),
    	SUPERSET         ("]", "> s"),
    	SUBSET           ("[", "< s"),
    	SUPERSETCOMPLETE ("}", "> s+"),
    	SUBSETCOMPLETE   ("{", "< s+"),
    	RELATED			 ("~", " ~ "),
    	UNKNOWN			 ("?", " ? ");
    	
    	private String internalRepresentation;
    	private String visualRepresentation;  // TODO: Change this to a graphical representation, not a string.
    	private MappingRelation(String rep, String vrep) { internalRepresentation = rep; visualRepresentation = vrep; }
    	public String toString() { return internalRepresentation; }
    	public String getVisualRepresentation() { return visualRepresentation; }
    	
		public static MappingRelation parseRelation(String relation) {
			for( MappingRelation rel : MappingRelation.values() ){
				if( relation.equals(rel.toString()) || 
						relation.equals(rel.getVisualRepresentation())) return rel;
			}
			if(relation.equals(">")) return MappingRelation.SUPERCLASS;
			if(relation.equals("<")) return MappingRelation.SUBCLASS;
			return UNKNOWN;
		}
		
    }
    

    
    public static String parseRelation(String r) {
    	for( MappingRelation rel : MappingRelation.values() ) {
    		if( r.equals( rel.toString() ) ) return rel.toString();
    	}    	
    	return MappingRelation.UNKNOWN.toString();  // unknown relation
    }
    
    /* Constructors */
    public Mapping(Node e1, Node e2, double sim, MappingRelation rel, alignType tyoc, String p) {
        entity1 = e1;
        entity2 = e2;
        similarity = sim;
        relation = rel;
        typeOfConcepts = tyoc;
        provenance = p;
    }
    
    public Mapping(Node e1, Node e2, double sim, MappingRelation r, alignType tyoc)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = sim;
        relation = r;
        typeOfConcepts = tyoc;
    }
    
    public Mapping(Node e1, Node e2, double sim, MappingRelation r)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = sim;
        relation = r;
        if( entity1.getType().equals( AMNode.OWLCLASS ) || entity1.getType().equals( AMNode.RDFNODE ) || entity1.getType().equals( AMNode.XMLNODE )  ) {
        	typeOfConcepts = alignType.aligningClasses;
        } else {
        	typeOfConcepts = alignType.aligningProperties;
        }
    }
    
    public Mapping(Node e1, Node e2, double sim, MappingRelation r, String p)
    {
        entity1 = e1;
        entity2 = e2;
        similarity = sim;
        relation = r;
        provenance = p;
        if( entity1.getType().equals( AMNode.OWLCLASS ) || entity1.getType().equals( AMNode.RDFNODE ) || entity1.getType().equals( AMNode.XMLNODE )  ) {
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
        relation = MappingRelation.EQUIVALENCE;
        if( entity1.getType().equals( AMNode.OWLCLASS ) || entity1.getType().equals( AMNode.RDFNODE ) || entity1.getType().equals( AMNode.XMLNODE )  ) {
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
        relation = MappingRelation.EQUIVALENCE;
        if( entity1.getType().equals( AMNode.OWLCLASS ) || entity1.getType().equals( AMNode.RDFNODE ) || entity1.getType().equals( AMNode.XMLNODE )  ) {
        	typeOfConcepts = alignType.aligningClasses;
        } else {
        	typeOfConcepts = alignType.aligningProperties;
        }
    }

    public Mapping(double s) {
		//This is a fake contructor to use an alignment as a container for sim and relation, to move both values between methods
    	similarity = s;
    	relation = MappingRelation.EQUIVALENCE;
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

	public MappingRelation getRelation() { return relation; }
	public void setRelation(MappingRelation r) { relation = r; }

	public String getProvenance() { return provenance; }
	public void setProvenance(String provenance) { this.provenance = provenance; }

	public String toString() { return "("+entity1.toString()+" -> "+entity2.toString()+
		": "+similarity+" "+relation.getVisualRepresentation()+" )"; }
	
	public String getString(boolean URI) { 
		String retValue = "";
		if(URI == true) retValue = entity1.getUri();
		else retValue = entity1.getLocalName();
		retValue += "\t"+OutputController.arrow+"\t";
		if(URI == true) retValue += entity2.getUri();
		else retValue += entity2.getLocalName();		
		retValue += "\t"+getSimilarity()+"\t"+getRelation().getVisualRepresentation();
		if(provenance != null){
			retValue += "\t" + provenance;
		}
		retValue += "\n"; 
		return retValue;
	}
	
	public String getString() { 
		return getString(false);
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
                && entity2.equals(alignment.getEntity2())
                	&& relation.equals(alignment.getRelation())) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns true if the input Mapping is considering the same source and target concepts.
     * Ignore if the relation types are not equal.
     */
    public boolean equalsIgnoreRelation(Mapping alignment)
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
			return equals((Mapping)o);
		}
		return false;
	}
    
	
	public int hashCode(){
		//relation may be added to this definition 
		//map can be replaced with string except empty string
		//this method is used in the PRAintegrationMatcher
		//and in the conference conflict resolution.
		return (typeOfConcepts.toString() + entity1.getIndex()+"map"+entity2.getIndex()).hashCode();
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
	
	public BitVector getFeaturesBitVector() { return featuresBitVector; }
	public void setFeaturesBitVector( BitVector bitVector ) { featuresBitVector = bitVector; }
	
}
