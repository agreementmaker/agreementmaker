package am.app.feedback.ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SelectionPanel extends JPanel {

	
	
	public SelectionPanel() {	
		
	}
	
	
	
	public void showScreen_Start() {
		
		setLayout(new BorderLayout());
		JButton btn_start = new JButton("Start");
		add(btn_start, BorderLayout.CENTER);

		
	}
	
}
