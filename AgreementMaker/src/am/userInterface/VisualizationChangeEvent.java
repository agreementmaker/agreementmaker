package am.userInterface;

import java.util.EventObject;

public class VisualizationChangeEvent extends EventObject {
	
	private static final long serialVersionUID = 5963937691370162428L;


	public enum VisualizationEventType {
		TOGGLE_SHOWMAPPINGSSHORTNAME,  // View -> Mappings with Matcher Name
		CONCEPT_SELECTED, // a concept was selected
		TOGGLE_SYNCHRONIZATION, // toggle the View -> Synchronized Views
		CUSTOM,
		NOT_SET
	}
	
	private VisualizationEventType typeOfEvent;
	
	public VisualizationChangeEvent(Object source) {
		super(source);
		typeOfEvent = VisualizationEventType.NOT_SET;
	}

	
	public VisualizationChangeEvent( Object s, VisualizationEventType t ) {
		super(s);
		typeOfEvent = t;
	}
		
	public VisualizationEventType getEvent()      { return typeOfEvent; }
	
}
