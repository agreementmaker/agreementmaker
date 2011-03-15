package am.userInterface;

import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import am.Utility;

public class DatabaseSettingsDialog extends JDialog {
	
	private JTextField dbName,host, port, username;
	private JPasswordField password;
	
	public DatabaseSettingsDialog(JDialog openFile)
	{
		super(openFile,true);
		Preferences p=Preferences.userNodeForPackage(this.getClass());
		password=new JPasswordField(p.get("password", ""));
	}
	
	private void setPreferences()
	{
		Preferences p=Preferences.userNodeForPackage(this.getClass());
		try{
			p.putInt("port", Integer.parseInt(port.getText()));
		}catch(NumberFormatException e)
		{
			Utility.displayErrorPane("Port number is invalid.", "ERROR");
		}
		p.put("host", host.getText());
		p.put("password",password.getText());
		p.put("dbName",dbName.getText());
	}

}
