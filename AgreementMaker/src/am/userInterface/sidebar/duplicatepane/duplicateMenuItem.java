package am.userInterface.sidebar.duplicatepane;

import java.awt.Component;

import javax.swing.JMenuItem;

import am.app.Core;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTaskChangeEvent;


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
			
			if(Core.getUI() !=null){
				Component rightSide=Core.getUI().getUISplitPane().getRightComponent();
				if(rightSide instanceof DuplicateSidebar)
					Core.getUI().getUISplitPane().setRightComponent(((DuplicateSidebar) rightSide).getOldComponent());
				this.setEnabled(false);
			}
		}

	}
