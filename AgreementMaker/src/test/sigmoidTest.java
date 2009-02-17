package test;

import javax.swing.JOptionPane;

import am.Utility;

public class sigmoidTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String input = JOptionPane.showInputDialog("Insert num to be processed");
		
		while(input != null) {
			double d = Double.parseDouble(input);
			double sigmoid = Utility.getSigmoidFunction(d);
			JOptionPane.showMessageDialog(null, sigmoid);
			System.out.println(sigmoid);
			input = JOptionPane.showInputDialog("Insert string to be processed");
		}
	}

}
