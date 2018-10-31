package am.ui.glue;

import am.app.Core;
import am.app.mappingEngine.registry.MatcherRegistryImpl;
import am.ui.UI;
import am.ui.UICore;

public class StartAgreementMaker {
    public static void main(String[] args) {
        Core.getInstance().setRegistry(new MatcherRegistryImpl());
        UICore.setUI(new UI());
    }
}
