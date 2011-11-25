package am.app.userfeedbackloop.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import am.app.mappingEngine.Mapping;
import am.app.userfeedback.UFLExperiment;
import am.app.userfeedback.UserFeedback.Validation;

public class ManualUserValidationPanel extends JPanel implements ActionListener {

	private JLabel lblMapping;
	private JLabel lblInRef;
	private JButton btnCorrect, btnIncorrect, btnEndExperiment;
	
	private Mapping candidateMapping;
	private UFLExperiment experiment;
	
	public ManualUserValidationPanel(Mapping candidateMapping, UFLExperiment experiment) {
		super();
		
		this.candidateMapping = candidateMapping;
		this.experiment = experiment;
		
		lblMapping = new JLabel(candidateMapping.toString());
		
		if( experiment.getReferenceAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2()) 
				!= null ) {
			lblInRef = new JLabel("(InRef)");
		} else {
			lblInRef = new JLabel("()");
		}
		
		btnCorrect = new JButton("Correct");
		btnCorrect.addActionListener(this);
		
		btnIncorrect = new JButton("Incorrect");
		btnIncorrect.addActionListener(this);
		
		btnEndExperiment = new JButton("End Experiment");
		btnEndExperiment.addActionListener(this);
		
		
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup( layout.createParallelGroup()
				.addComponent(lblMapping)
				.addComponent(lblInRef)
				.addGroup( layout.createSequentialGroup()
						.addComponent(btnCorrect)
						.addComponent(btnIncorrect)
						.addComponent(btnEndExperiment)
				)
		);
		
		layout.setVerticalGroup( layout.createSequentialGroup()
			.addComponent(lblMapping)
			.addComponent(lblInRef)
			.addGroup( layout.createParallelGroup() 
					.addComponent(btnCorrect)
					.addComponent(btnIncorrect)
					.addComponent(btnEndExperiment)
			)
		);
		
		setLayout(layout);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnCorrect ) {
			experiment.userFeedback.setUserFeedback(Validation.CORRECT);
		} else if( e.getSource() == btnIncorrect ) {
			experiment.userFeedback.setUserFeedback(Validation.INCORRECT);
		} else if( e.getSource() == btnEndExperiment ) {
			experiment.userFeedback.setUserFeedback(Validation.END_EXPERIMENT);
		}
	}
	
}
