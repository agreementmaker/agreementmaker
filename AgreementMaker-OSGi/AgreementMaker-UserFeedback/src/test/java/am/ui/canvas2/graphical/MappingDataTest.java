package am.ui.canvas2.graphical;

import am.app.Core;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatchingTask;
import am.app.ontology.Node;
import am.ui.UICore;
import am.ui.canvas2.utility.Canvas2Layout;
import am.ui.matchingtask.MatchingTaskVisData;
import com.hp.hpl.jena.ontology.OntResource;
import org.junit.Test;

import java.awt.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MappingDataTest {
    private Core mockCore = mock(Core.class);
    private UICore mockUICore = mock(UICore.class);
    private Canvas2Layout mockLayout = mock(Canvas2Layout.class);
    private OntResource mockResource = mock(OntResource.class);

    @Test
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

            assertNotNull("Color should be set.", data.color);
            assertEquals("Color should be inherited from matcher.", data.color, Color.RED);
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
            assertNotNull("Color should be set.", data.color);
            assertEquals("Color should be inherited from matcher.", data.color, Color.RED);
        }
    }
}