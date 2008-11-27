package agreementMaker.userInterface;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton; 
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//import com.hp.hpl.jena.query.extension.library.group;
import com.ibm.icu.lang.UCharacter.JoiningGroup;

import agreementMaker.GSM;
import agreementMaker.Utility;
import agreementMaker.application.evaluationEngine.ReferenceEvaluation;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.AbstractMatcherParametersPanel;
import agreementMaker.application.mappingEngine.AbstractParameters;
import agreementMaker.application.mappingEngine.DefnMappingOptions;
import agreementMaker.userInterface.vertex.VertexDescriptionPane;




public class AbstractMatcherParametersDialog extends JDialog implements ActionListener{
	
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
	public AbstractMatcherParametersDialog(AbstractMatcher a) {
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
			else Utility.dysplayErrorPane(check, "Illegal Parameters" );
		}
		
	}
	
	
	public boolean parametersSet() {
		return success;
	}
	
	public AbstractParameters getParameters() {
		return parametersPanel.getParameters();
	}
	
}
