package test;

import javax.swing.JOptionPane;

import am.Utility;

public class TestFormatTime {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String input = JOptionPane.showInputDialog("Insert an integer number to be formatted in hh:mm:ss:msmsms");
		
		String result;
		while(input != null) {
			System.out.println("Original String was: "+input);
			try{
				long fulltime = Long.parseLong(input);
				String formattedTime = Utility.getFormattedTime(fulltime);
				System.out.println("Formatted time in hh:mm:ss:msmsms is: "+formattedTime);
				JOptionPane.showMessageDialog(null, "Formatted time in hh:mm:ss:msmsms is: "+formattedTime);
			}
			catch(Exception e){
				System.out.println("The inserted String is not an Long value");
				JOptionPane.showMessageDialog(null, "The inserted String is not an Long value");
			}
			input = JOptionPane.showInputDialog("Insert an integer number to be formatted in hh:mm:ss:msmsms");
		}

	}

}
