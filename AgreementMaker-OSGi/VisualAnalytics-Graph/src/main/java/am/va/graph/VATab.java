package am.va.graph;

import javafx.embed.swing.JFXPanel;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import am.ui.api.AMTab;

public class VATab extends JFXPanel implements AMTab {

	private static final long serialVersionUID = -1633523480324746365L;

	private ImageIcon icon;

	public VATab() {
		super();
		setName("Visual Analytics");
	}

	public VATab(String label) {
		super();
		setName(label);
	}

	public VATab(String label, String tooltip) {
		super();
		setName(label);
		setToolTipText(tooltip);
	}

	public VATab(String label, String tooltip, ImageIcon icon) {
		super();
		setName(label);
		setToolTipText(tooltip);
		setIcon(icon);
	}

	@Override
	public void setLabel(String label) {
		setName(label);
	}

	@Override
	public String getLabel() {
		return getName();
	}

	@Override
	public void setTooltip(String tooltip) {
		setToolTipText(tooltip);
	}

	@Override
	public String getTooltip() {
		return getToolTipText();
	}

	@Override
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	@Override
	public ImageIcon getIcon() {
		return icon;
	}

	@Override
	public JComponent getTab() {
		return this;
	}

}
