package am.ui.glue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import am.extension.userfeedback.ui.UFLBatchModeGUI;
import am.ui.UICore;
import am.ui.UIMenu;
import am.ui.api.AMMenuItem;

public class UFLMenuItem extends JMenuItem implements AMMenuItem {

	private static final long serialVersionUID = -7995890375277240631L;
	
	@Override public String getMenuLocation() { return UIMenu.MENU_MATCHERS + "/"; }
	@Override public JMenuItem getMenuItem() { return this; }
	
	public UFLMenuItem() {
		super("UFL Batch Mode");
		
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UFLBatchModeGUI gui = new UFLBatchModeGUI();
				UICore.getUI().addTab(gui);
			}
		});
		
	}
}
