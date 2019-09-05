package am.ui.matchingtask;

import am.app.mappingEngine.MatchingTask;

import java.awt.*;

/**
 * Keeps track of visualization data for a {@link MatchingTask}. 
 * 
 * @author Cosmin Stroe
 */
public class MatchingTaskVisData {
    private boolean isShown;
    private Color color;

    public MatchingTaskVisData() {
        this.isShown = true;
        this.color = Color.ORANGE;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setShown(boolean shown) {
        isShown = shown;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
