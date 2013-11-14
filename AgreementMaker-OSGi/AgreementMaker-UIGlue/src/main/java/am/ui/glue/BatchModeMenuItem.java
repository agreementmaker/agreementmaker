package am.ui.glue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import am.ui.UIMenu;
import am.ui.api.AMMenuItem;

public class BatchModeMenuItem extends JMenuItem implements AMMenuItem {

	private static final long serialVersionUID = -7679368085474956610L;
	
	public BatchModeMenuItem() {
		super("Batch Mode");
		
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
	}
	
	@Override public String getMenuLocation() { return UIMenu.MENU_MATCHERS + "/"; }
	@Override public JMenuItem getMenuItem() { return this; }
}
