package am.ui;

import java.awt.HeadlessException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import am.Utility;

public class UIUtility {

	public static void displayErrorPane(String desc, String title) {
		if(title == null)
			title = "Error";
		try {
			JOptionPane.showMessageDialog(null, desc,title, JOptionPane.ERROR_MESSAGE);
		} catch( HeadlessException ex ) {
			Logger log = Logger.getLogger(Utility.class);
			log.error(desc);
		}
	}

	public static boolean displayConfirmPane(String desc, String title) {
		if(title == null)
			title = "Confirmation required";
		int res =  JOptionPane.showConfirmDialog(null,
				desc,
				title,
				JOptionPane.YES_NO_OPTION);	
		if(res == JOptionPane.YES_OPTION)
			return true;
		else return false;
	}
	
}
