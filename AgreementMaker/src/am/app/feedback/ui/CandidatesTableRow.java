package am.app.feedback.ui;

import javax.swing.JRadioButton;

import am.app.mappingEngine.Alignment;

public class CandidatesTableRow {
	
	public int index;
	public JRadioButton radio;//maybe this is not needed;
	public Alignment mapping;
	public int group;
	String type;

	public CandidatesTableRow(int ind, Alignment map, int g, JRadioButton jr){
		index = ind;
		mapping = map;
		group = g;
		radio = jr;
		if(map.getEntity1().isProp()){
			type = "Property";
		}
		else type = "Class";
	}
}
