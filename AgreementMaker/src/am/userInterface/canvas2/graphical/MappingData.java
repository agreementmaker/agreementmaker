package am.userInterface.canvas2.graphical;

import java.awt.Color;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.userInterface.Colors;
import am.userInterface.canvas2.utility.Canvas2Layout;

import com.hp.hpl.jena.ontology.OntResource;

public class MappingData extends GraphicalData {

	public enum MappingType {
		NOT_SET,
		ALIGNING_CLASSES,
		ALIGNING_PROPERTIES,
	}
	
	public String label;
	
	public OntResource r2;
	public int ontologyID2;
	public int matcherID;
	public Color color = Colors.mapped;
	public MappingType mappingType;
	
	public MappingData(int x1, int y1, int width, int height, Canvas2Layout l, 
						OntResource res1, OntResource res2, int OntID1, int OntID2, int mID, MappingType t ) {
		super(x1, y1, width, height, res1, NodeType.MAPPING, l);

		r2 = res2;
		ontologyID = OntID1;
		ontologyID2 = OntID2;
		matcherID = mID;
		
		AbstractMatcher m = Core.getInstance().getMatcherByID(matcherID);
		if( m != null ) {
			color = m.getColor();
		}
		
		mappingType = t;
		
	}

	public void setLabel(String lbl) { label = lbl; }
	
}
