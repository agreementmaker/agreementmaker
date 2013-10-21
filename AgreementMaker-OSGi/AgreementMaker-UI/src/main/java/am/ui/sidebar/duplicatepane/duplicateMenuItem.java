package am.ui.sidebar.duplicatepane;

import java.awt.Component;

import javax.swing.JMenuItem;

import am.app.Core;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.ui.UICore;


	public class duplicateMenuItem extends JMenuItem implements MatcherChangeListener{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -7711484614942406000L;
		public duplicateMenuItem(String title) {
			super(title);
		}
		@Override
		public void matcherChanged(MatchingTaskChangeEvent e) {
			//check here to grey out the menu item
			
			if(UICore.getUI() !=null){
				Component rightSide= UICore.getUI().getUISplitPane().getRightComponent();
				if(rightSide instanceof DuplicateSidebar)
					UICore.getUI().getUISplitPane().setRightComponent(((DuplicateSidebar) rightSide).getOldComponent());
				this.setEnabled(false);
			}
		}

	}
