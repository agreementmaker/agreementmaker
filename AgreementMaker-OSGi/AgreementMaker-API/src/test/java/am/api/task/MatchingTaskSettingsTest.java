package am.api.task;

import org.junit.Test;

import static org.junit.Assert.*;

public class MatchingTaskSettingsTest {
    @Test
    public void builder() {
        MatchingTaskSettings setting = new MatchingTaskSettings.Builder()
                .addSetting("key1", "val1")
                .addSetting("key2", "val2")
                .build();

        assertEquals("val1", setting.getSetting("key1").get());
        assertEquals("val2", setting.getSetting("key2").get());
        assertFalse(setting.getSetting("key3").isPresent());
    }

}