package lexicalmatcherjaws.internal;

import java.util.ArrayList;
import java.util.Iterator;

public class ComplexConcept {

	
	enum ConceptType {
		Object ("Object"), // this concept is an object
		Modifier ("Modifier"),  // this concept modifies another concept
		Undefined ("Undefined"), // undefined
		Group ("Group");  // concept group
		
		String desc;
		
		ConceptType( String d ) { desc = new String(d); }
		
		public String toString() {	return desc; }
	}
	
	ConceptType typeOfConcept;
	String conceptString = null;
	ComplexConcept definedBy = null;
	ComplexConcept operatesOn = null;
	ArrayList<ComplexConcept> operators = new ArrayList<ComplexConcept>();

	
	/**
	 * This operator is used when the concept is not definedBy and does not operateOn any other concept. 
	 * This is an indivisible concept.
	 */
	public ComplexConcept( String cs, ConceptType type ) {
		typeOfConcept = type;
		conceptString = new String(cs);
	}
	
	
	public ComplexConcept( ArrayList<ComplexConcept> sub, ConceptType type ) {
		typeOfConcept = type;
		
		operators = new ArrayList<ComplexConcept>(sub);
		
		for( int i = 0; i < operators.size(); i++ ) {
				if( conceptString == null ) {
					conceptString = new String(operators.get(i).getConceptString());
				} else {
					conceptString += " " + operators.get(i).getConceptString();
				}
		}
		
	}

	public void setDefinedBy( ComplexConcept def ) { definedBy = def; }
	public ComplexConcept getDefinedBy() { return definedBy; }
	
	public void setOperatesOn( ComplexConcept oon ) { operatesOn = oon; }
	public ComplexConcept getOperatesOn() { return operatesOn; }
	
	public void addOperator( ComplexConcept oper ) { operators.add(oper); }
	public ArrayList<ComplexConcept> getOperators() { return operators; }
	
	public String getConceptString() { return conceptString; }
	
	public String toString() {
		String s = new String();
		
		if( operators != null ) {
			Iterator<ComplexConcept> operatorsIter = operators.iterator();
			while( operatorsIter.hasNext() ) {
				s += "o: " + operatorsIter.toString() + " ";
			}
		}
				
		s += conceptString + " (" + typeOfConcept.toString() + ")";
		return s;
	}
	
}
