package am.ui.canvas2.graphical;

import am.app.Core;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.ontology.Node;
import am.ui.UICore;
import am.ui.canvas2.utility.Canvas2Layout;
import am.ui.matchingtask.MatchingTaskVisData;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Mapping Data")
public class MappingDataTest {
    private Core mockCore = mock(Core.class);
    private UICore mockUICore = mock(UICore.class);
    private Canvas2Layout mockLayout = mock(Canvas2Layout.class);
    private OntResource mockResource = mock(OntResource.class);

    @Test
    @DisplayName("should inherit color from MatchingTask")
    public void setColorCorrectly() {
        MatchingTask mockTask = mock(MatchingTask.class);
        MatchingTaskVisData mockData = mock(MatchingTaskVisData.class);
        when(mockCore.getMatchingTaskByID(25673)).thenReturn(mockTask);
        when(mockUICore.getVisData(mockTask)).thenReturn(mockData);
        when(mockData.getColor()).thenReturn(Color.RED);

        {
            MappingData data = new MappingData(
                    mockCore, mockUICore, 0, 0, 0, 0, mockLayout, mockResource, mockResource, 0, 0, 25673,
                    MappingData.MappingType.NOT_SET);

            assertNotNull(data.color, "Color should be set.");
            assertEquals(data.color, Color.RED, "Color should be inherited from matcher.");
        }{
            Mapping mockMapping = mock(Mapping.class);
            Node mockNode = mock(Node.class);
            when(mockMapping.getEntity1()).thenReturn(mockNode);
            when(mockMapping.getEntity2()).thenReturn(mockNode);
            when(mockNode.getResource()).thenReturn(mockResource);

            MappingData data = new MappingData(
                    mockCore, mockUICore,
                    0, 0, 0, 0, mockLayout, mockResource, mockMapping, 0, 0, 25673,
                    MappingData.MappingType.NOT_SET);
            assertNotNull(data.color, "Color should be set.");
            assertEquals(data.color, Color.RED, "Color should be inherited from matcher.");
        }
    }
}