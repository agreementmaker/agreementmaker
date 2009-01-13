package test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import agreementMaker.userInterface.MatchersControlPanel;
import agreementMaker.userInterface.UI.WindowEventHandler;

public class TestUI {
	private JFrame frame;
	
	public void init() {
		frame = new JFrame("Agreement Maker");
		frame.getContentPane().setLayout(new BorderLayout());
		// TODO: Maybe ask the user if he wants to exit the program.  But that might be annoying.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // if the user closes the window from the window manager, close the application.
	    JPanel p0 = new JPanel(new BorderLayout());
		JPanel p1 = new JPanel(new FlowLayout());
		JPanel p2 = new JPanel(new FlowLayout());
		
		JButton button1 = new JButton("Ciaooooooooo");
		JButton button2 = new JButton("Ciaooooooooo");
		p1.add(button1);
		p1.add(button2);
		frame.getContentPane().add(p1, BorderLayout.PAGE_END);
		
		// set frame size (width = 1000 height = 700)
		//frame.setSize(900,600);
		frame.pack();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH); // maximize the window
		frame.setVisible(true);
	}
}
