package am.ui.canvas2.graphical;

import am.app.Core;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.ui.Colors;
import am.ui.UICore;
import am.ui.canvas2.utility.Canvas2Layout;
import am.ui.matchingtask.MatchingTaskVisData;
import com.hp.hpl.jena.ontology.OntResource;

import java.awt.*;

public class MappingData extends GraphicalData {

	public enum MappingType {
		NOT_SET,
		ALIGNING_CLASSES,
		ALIGNING_PROPERTIES,
	}
	
	public String label;
	
	@Deprecated public OntResource r2; // deprecated because we don't need the second resource, but we need the Mapping
	public int ontologyID2;
	
	// TODO: Rename to taskID
	public int matcherID;
	public Color color = Colors.mapped;
	public MappingType mappingType;
	public Mapping alignment = null;
	
    public MappingData(Core core, UICore uiCore, int x1, int y1, int width, int height, Canvas2Layout l,
                        OntResource res1, OntResource res2, int OntID1, int OntID2, int mID, MappingType t ) {
        super(x1, y1, width, height, res1, NodeType.MAPPING, l, 0);

        r2 = res2;
        ontologyID = OntID1;
        ontologyID2 = OntID2;
        matcherID = mID;

        this.color = getColor(core, uiCore, matcherID);
        
        mappingType = t;
    }

	public MappingData(Core core, UICore uiCore, int x1, int y1, int width, int height, Canvas2Layout l,
			OntResource res1, Mapping a, int OntID1, int OntID2, int mID, MappingType t ) {
		super(x1, y1, width, height, res1, NodeType.MAPPING, l);

		if( a.getEntity1().getResource().equals(res1) ) {
			// this is the typical case (res1 == entity1 resource)
			r2 = (OntResource) a.getEntity2().getResource();
		} else if ( a.getEntity2().getResource().equals(res1)) {
			// this should not happen.  for some reason (res1 == entity2 resource), they're switched
			r2 = (OntResource) a.getEntity1().getResource();  // so, because res1 == entity2, res2 must == entity1
		} else {
			// we should never get here. (but coded for robustness)
			// ya, this makes no sense, but switching to default assumption
			r2 = (OntResource) a.getEntity2().getResource();
		}
		
		alignment = a;
		
		ontologyID = OntID1;
		ontologyID2 = OntID2;
		matcherID = mID;

        color = getColor(core, uiCore, matcherID);
		mappingType = t;

	}

    private Color getColor(Core core, UICore uiCore, int matcherID) {
        MatchingTask m = core.getMatchingTaskByID(matcherID);
        if(m == null) {
            return Colors.matchersColors[0];
        }

        MatchingTaskVisData data = uiCore.getVisData(m);
        if(data != null) {
            return data.getColor();
        }

        return Colors.matchersColors[0];
    }
}
