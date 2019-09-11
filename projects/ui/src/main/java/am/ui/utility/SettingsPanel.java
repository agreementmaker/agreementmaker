package am.ui.utility;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class SettingsPanel extends JPanel {

	private static final long serialVersionUID = -8133720483404357153L;
	
	private JDialog dialog;
	
	/** Called when the settings panel is added to a settings dialog */
	public void setDialog(JDialog dialog) {
		this.dialog = dialog;
	}
	
	/** Get the parent dialog of the settings panel. */
	public JDialog getDialog() {
		return dialog;
	}
	
	public void updateSize() {
		if( dialog != null ) {
			dialog.pack();
			dialog.setLocationRelativeTo(null);
		}
	}
	
}
