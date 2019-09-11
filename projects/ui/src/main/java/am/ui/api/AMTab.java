package am.ui.api;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * This panel will be viewed as a Tab in the main AgreementMaker pane.
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 *
 */
public interface AMTab extends AMVisualizationComponent {
	
	public void   setLabel(String label);
	public String getLabel();
	
	public void   setTooltip(String tooltip);
	public String getTooltip();
	
	public void      setIcon(ImageIcon icon);
	public ImageIcon getIcon();
	
	public JPanel getTab();
}
