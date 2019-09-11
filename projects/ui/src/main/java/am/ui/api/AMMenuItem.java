package am.ui.api;

import javax.swing.JMenuItem;

/**
 * Represents a menu item.  This menu item is added to the AgreementMaker menus.
 * 
 * NOTE: Because JMenu extends JMenuItem, the representation can be a full menu.
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 *
 */
public interface AMMenuItem extends AMVisualizationComponent {

	/**
	 * A string that follows an XPath style for specifying where the menu item
	 * should be located.
	 */
	public String getMenuLocation();
	
	public JMenuItem getMenuItem();
}
