package am.userInterface;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton; 
import javax.swing.JDialog;
import javax.swing.JPanel;

import am.Utility;
import am.application.mappingEngine.AbstractMatcher;
import am.application.mappingEngine.AbstractMatcherParametersPanel;
import am.application.mappingEngine.AbstractParameters;



public class MatcherParametersDialog extends JDialog implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7150332604304262664L;
	/**
	 * @param args
	 */
	
	private JButton cancel, run;
	private boolean success = false;
	AbstractMatcherParametersPanel parametersPanel;
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public MatcherParametersDialog(AbstractMatcher a) {
		String name = a.getName().getMatcherName();
		setTitle(name+": additional parameters");
		//This is the specific panel defined by the developer to set additional parameters to the specific matcher implemented
		parametersPanel = a.getParametersPanel();
		
		run = new JButton("Run");
		cancel = new JButton("Cancel");
		run.addActionListener(this);
		cancel.addActionListener(this);
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottom.add(run);
		bottom.add(cancel);
		
		
		
		setLayout(new BorderLayout());
		add(parametersPanel, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		
		
		//frame.addWindowListener(new WindowEventHandler());//THIS SHOULD BE CHANGED THE PROGRAM SHOULD NOT CLOSE
		pack(); // automatically set the frame size
		//set the width equals to title dimension
		FontMetrics fm = getFontMetrics(getFont());
		// +100 to allow for icon and "x-out" button
		int width = fm.stringWidth(getTitle()) + 100;
		width = Math.max(width, getPreferredSize().width);
		setSize(new Dimension(width, getPreferredSize().height));
		pack();  // make it smaller.
		setLocationRelativeTo(null); 	// center the window on the screen
		setModal(true);
		setVisible(true);
	}
	

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */

	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if(obj == cancel){
			success = false;
			//setModal(false);
			setVisible(false);  // required
		}
		else if(obj == run){
			String check = parametersPanel.checkParameters();
			if(check == null || check.equals("")) {
				success = true;
				//setModal(false);
				setVisible(false);  // required
			}
			else Utility.displayErrorPane(check, "Illegal Parameters" );
		}
		
	}
	
	
	public boolean parametersSet() {
		return success;
	}
	
	public AbstractParameters getParameters() {
		return parametersPanel.getParameters();
	}
	
}
