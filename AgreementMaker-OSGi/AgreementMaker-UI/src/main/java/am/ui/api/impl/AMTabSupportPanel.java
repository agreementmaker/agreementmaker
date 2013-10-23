package am.ui.api.impl;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import am.ui.api.AMTab;

/**
 * A decorator wrapper for JPanel that implements the AMTab interface.
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 */
public class AMTabSupportPanel extends JPanel implements AMTab {

	private static final long serialVersionUID = -1633523480324746365L;
	
	private ImageIcon icon;
	
	public AMTabSupportPanel() {
		super();
		setName("Tab");
	}
	
	public AMTabSupportPanel(String label) {
		super();
		setName(label);
	}
	
	public AMTabSupportPanel(String label, String tooltip) {
		super();
		setName(label);
		setToolTipText(tooltip);
	}
	
	public AMTabSupportPanel(String label, String tooltip, ImageIcon icon) {
		super();
		setName(label);
		setToolTipText(tooltip);
		setIcon(icon);
	}

	@Override public void      setLabel(String label)     { setName(label); }
	@Override public String    getLabel()                 { return getName(); }
	@Override public void      setTooltip(String tooltip) { setToolTipText(tooltip);}
	@Override public String    getTooltip()               { return getToolTipText(); }
	@Override public void      setIcon(ImageIcon icon)    { this.icon = icon; }
	@Override public ImageIcon getIcon()                  { return icon; }
	@Override public JPanel    getTab()                   { return this; }
}
