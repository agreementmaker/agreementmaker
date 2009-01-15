package agreementMaker.userInterface;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton; 
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import agreementMaker.Utility;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.AbstractParameters;
import agreementMaker.application.mappingEngine.Combination.CombinationParameters;
import agreementMaker.application.mappingEngine.qualityEvaluation.QualityEvaluator;



public class QualityEvaluationDialog extends JDialog implements ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7150332604304262664L;
	/**
	 * @param args
	 */
	
	private JButton cancel, run;
	private boolean success = false;
	
	private JLabel topLabel;
	private JLabel qualLabel;
	public JComboBox qualCombo;
	
	/**
	 * @param ontoType
	 * @param userInterface
	 */
	public QualityEvaluationDialog() {
		
		setTitle("Quality Evaluation");
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		topLabel = new JLabel("Select the quality measure tol be used to evaluate matchings.");
		topPanel.add(topLabel);
		
		JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		qualLabel = new JLabel("Quality measure: ");
		qualCombo = new JComboBox(QualityEvaluator.QUALITIES);
		centerPanel.add(qualLabel);
		centerPanel.add(qualCombo);
		
		
		run = new JButton("Run");
		cancel = new JButton("Cancel");
		run.addActionListener(this);
		cancel.addActionListener(this);
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottom.add(run);
		bottom.add(cancel);
		
		
		
		setLayout(new BorderLayout());
		add(centerPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
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
			success = true;
			//setModal(false);
			setVisible(false);  // required
		}
		
	}
	
	
	public boolean isSuccess() {
		return success;
	}
	
}
