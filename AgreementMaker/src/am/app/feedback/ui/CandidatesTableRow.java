package am.app.feedback.ui;

import javax.swing.JRadioButton;

import org.omg.SendingContext.RunTime;

import am.app.feedback.CandidateConcept;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.AbstractMatcher.alignType;

public class CandidatesTableRow {
	
	
	//each row refers to one single candidate mapping
	//that is obtained as the candidateConcept.getCandidateMappings.get(index)
	public int index;
	public JRadioButton radio;
	public int group;
	public CandidateConcept candidateConcept;
	
	public CandidatesTableRow(CandidateConcept c, int g, int ind, JRadioButton jr){
		index = ind;
		candidateConcept = c;
		group = g;
		radio = jr;
	}
	
	public Alignment getMapping(){
		if(candidateConcept.getCandidateMappings() == null || candidateConcept.getCandidateMappings().size() <= index)
			throw new RuntimeException("A row referring to no candidate mapping exists in the table.");
		else return candidateConcept.getCandidateMappings().get(index);
	}
	
	public String getTypeString(){
		if(candidateConcept.isType(alignType.aligningClasses))
			return "Class";
		else return "Property";
	}
	
	public String getGroupString(){
		return "Group "+group;
	}
}
