package am.ui.sidebar.provenance;

import java.awt.Component;
import java.util.List;

import javax.swing.JMenuItem;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatcherFeature;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.ui.UICore;

public class ProvenanceMenuItem extends JMenuItem implements MatcherChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7711484614942406000L;
	public ProvenanceMenuItem(String title) {
		super(title);
	}
	@Override
	public void matcherChanged(MatchingTaskChangeEvent e) {
		//check here to grey out the menu item
		List<AbstractMatcher> c = Core.getInstance().getMatcherInstances();
		for(int i=0;i<c.size();i++){
			if(c.get(i).supportsFeature(MatcherFeature.MAPPING_PROVENANCE) && c.get(i).getParam().storeProvenance){
				this.setEnabled(true);
				return;
			}
		}
		if(UICore.getUI() !=null){
			Component rightSide= UICore.getUI().getUISplitPane().getRightComponent();
			if(rightSide instanceof ProvenanceSidebar)
				UICore.getUI().getUISplitPane().setRightComponent(((ProvenanceSidebar) rightSide).getOldComponent());
			this.setEnabled(false);
		}
	}

}
