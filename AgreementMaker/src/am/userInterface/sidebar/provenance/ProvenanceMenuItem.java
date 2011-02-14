package am.userInterface.sidebar.provenance;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherChangeEvent;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatcherFeature;

public class ProvenanceMenuItem extends JMenuItem implements MatcherChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7711484614942406000L;
	public ProvenanceMenuItem(String title) {
		super(title);
	}
	@Override
	public void matcherChanged(MatcherChangeEvent e) {
		//check here to grey out the menu item
		ArrayList<AbstractMatcher> c=Core.getInstance().getMatcherInstances();
		for(int i=0;i<c.size();i++){
			if(c.get(i).supportsFeature(MatcherFeature.MAPPING_PROVENANCE) && c.get(i).getParam().storeProvenance){
				this.setEnabled(true);
				return;
			}
		}
		if(Core.getUI() !=null){
			Component rightSide=Core.getUI().getUISplitPane().getRightComponent();
			if(rightSide instanceof ProvenanceSidebar)
				Core.getUI().getUISplitPane().setRightComponent(((ProvenanceSidebar) rightSide).getOldComponent());
			this.setEnabled(false);
		}
	}

}
