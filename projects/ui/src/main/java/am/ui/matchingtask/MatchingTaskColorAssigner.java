package am.ui.matchingtask;

import am.app.mappingEngine.MatchingTask;

import java.awt.*;
import java.util.Map;
import java.util.Optional;

import static am.ui.Colors.matchersColors;

public class MatchingTaskColorAssigner {
    public static Color assignColor(
            MatchingTask task,
            Map<MatchingTask, MatchingTaskVisData> visualizationData) {
        for(Color color : matchersColors) {
            Optional<Color> colorIsFound = visualizationData.values().stream()
                    .map(MatchingTaskVisData::getColor)
                    .filter(c -> c == color)
                    .findAny();
            if(!colorIsFound.isPresent()) {
                return color;
            }
        }
        return Color.ORANGE;
    }
}
