package am.ui.utility;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public class SettingsDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -4301016627200440987L;
	
	private JButton btnCancel = new JButton("Cancel");
	private JButton btnOk = new JButton("Ok");
	
	public static int OK = 1;
	public static int CANCEL = 0; 
	
	private int status = CANCEL;
	
	public SettingsDialog(JFrame parent, SettingsPanel content) {
		super(parent);
		setParams();
		setListeners();
		setContent(content);
	}
	
	public SettingsDialog(JFrame parent, SettingsPanel content, String okLabel) {
		super(parent);
		setParams();
		setListeners();
		setContent(content);
		btnOk.setText(okLabel);
	}
	
	public SettingsDialog(JDialog parent, SettingsPanel content) {
		super(parent);
		setParams();
		setListeners();
		setContent(content);
	}
	
	public SettingsDialog(JDialog parent, SettingsPanel content, String okLabel) {
		super(parent);
		setParams();
		setListeners();
		setContent(content);
		btnOk.setText(okLabel);
	}
	
	private void setParams() {
		setModal(true);
	}
	
	private void setListeners() {
		btnCancel.addActionListener(this);
		btnOk.addActionListener(this);
	}
	
	private void setContent(SettingsPanel content) {
		
		content.setDialog(this);
		
		setLayout(new GridBagLayout());
		{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			//content.setBorder(new LineBorder(Color.BLUE));
			add(content, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 2;
			c.insets = new Insets(10,0,5,0);
			c.anchor = GridBagConstraints.CENTER;
			add(new JSeparator(JSeparator.HORIZONTAL), c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			c.insets = new Insets(5,0,0,10);
			c.anchor = GridBagConstraints.PAGE_END;
			add(btnCancel, c);
		}{
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 1;
			c.gridy = 2;
			c.anchor = GridBagConstraints.PAGE_END;
			add(btnOk, c);
		}
		
		getRootPane().setBorder(new EmptyBorder(10,10,10,10));
		
		pack();
		setLocationRelativeTo(getParent());
	}

	public void disableCancelButton(boolean disabled) {
		btnCancel.setVisible(disabled);
	}
	
	public int getStatus() {
		return status;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnCancel ) {
			status = CANCEL;
			setVisible(false);
		}
		else if( e.getSource() == btnOk ) {
			status = OK;
			setVisible(false);
		}
	}
	
}
