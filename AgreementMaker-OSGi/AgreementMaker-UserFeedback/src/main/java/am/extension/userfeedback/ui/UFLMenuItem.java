package am.extension.userfeedback.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import am.ui.UICore;
import am.ui.UIMenu;
import am.ui.api.AMMenuItem;

public class UFLMenuItem extends JMenuItem implements AMMenuItem {

	private static final long serialVersionUID = -1276103269806800335L;

	public UFLMenuItem() {
		super("User Feedback Loop");
		
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				UFLControlGUI ufl_control = new UFLControlGUI(UICore.getUI());
				ufl_control.displayInitialScreen();
				UICore.getUI().addTab(ufl_control);
			}
		});
	}

	@Override public String getMenuLocation() { return UIMenu.MENU_MATCHERS + "/"; }
	@Override public JMenuItem getMenuItem() { return this; }
}
