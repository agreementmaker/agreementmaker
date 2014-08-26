package am.extension.userfeedback.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.MLFExperiment;
import am.extension.userfeedback.experiments.UFLExperiment;

public class ManualUserValidationPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -8988272138943928646L;
	
	private JLabel lblIteration;
	private JLabel lblMapping;
	private JLabel lblInRef;
	private JButton btnCorrect, btnIncorrect, btnSkip, btnEndExperiment;

	private Mapping candidateMapping;
	private UFLExperiment experiment;
	
	public ManualUserValidationPanel(Mapping candidateMapping, UFLExperiment experiment) {
		super();
		
		this.candidateMapping = candidateMapping;
		this.experiment = experiment;
		
		lblIteration = new JLabel();
		
		lblMapping = new JLabel(candidateMapping.getEntity1().getLocalName() + " <--> " + candidateMapping.getEntity2().getLocalName());
		
		if( experiment instanceof MLFExperiment ) {
			if( ((MLFExperiment)experiment).server != null ) {
				// if we're connected to the server, we don't want to show the users if something is in the reference alignment.
				lblInRef = new JLabel();
			}
		}
		
		if( lblInRef == null && experiment.getReferenceAlignment() != null ) {
			if( experiment.getReferenceAlignment().contains(candidateMapping.getEntity1(), candidateMapping.getEntity2()) 
					!= null ) {
				lblInRef = new JLabel("(InRef)");
			} else {
				lblInRef = new JLabel("()");
			}
		}
		
		btnCorrect = new JButton("Correct");
		btnCorrect.addActionListener(this);
		
		btnIncorrect = new JButton("Incorrect");
		btnIncorrect.addActionListener(this);
		
		btnSkip = new JButton("Skip");
		btnSkip.addActionListener(this);
		
		btnEndExperiment = new JButton("End Experiment and Exit AgreementMaker");
		btnEndExperiment.addActionListener(this);
		if( experiment instanceof MLFExperiment ) {
			if( ((MLFExperiment)experiment).server != null ) {
				// we are connected to the server, don't allow the user to end the experiment.
				if( ((MLFExperiment)experiment).feedbackCount < 10 ) {
					btnEndExperiment.setEnabled(false);
					btnEndExperiment.setToolTipText("Experiment will end automatically.");
				}
				
				lblIteration = new JLabel("Feedback count # " + ((MLFExperiment)experiment).feedbackCount);
			}
		}
		
		GroupLayout layout = new GroupLayout(this);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup( layout.createParallelGroup()
				.addComponent(lblIteration)
				.addComponent(lblMapping)
				.addComponent(lblInRef)
				.addGroup( layout.createSequentialGroup()
						.addComponent(btnCorrect)
						.addComponent(btnIncorrect)
						.addComponent(btnSkip)
						.addComponent(btnEndExperiment)
				)
		);
		
		layout.setVerticalGroup( layout.createSequentialGroup()
			.addComponent(lblIteration)
			.addComponent(lblMapping)
			.addComponent(lblInRef)
			.addGroup( layout.createParallelGroup() 
					.addComponent(btnCorrect)
					.addComponent(btnIncorrect)
					.addComponent(btnSkip)
					.addComponent(btnEndExperiment)
			)
		);
		
		setLayout(layout);
		
		if( experiment.gui != null ) {
			experiment.gui.appendToReport("<html><p>Please select the correct validation for the given mapping.</p><p>If you are not sure if a mapping is CORRECT or INCORRECT, click SKIP instead.</p><p>You must provide a minimum of 10 mappings, after which you can just click the END EXPERIMENT button and close AgreementMaker.</p></html>");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnCorrect ) {
			experiment.userFeedback.setUserFeedback(Validation.CORRECT);
			experiment.info("Iteration number: " + experiment.getIterationNumber());
			experiment.info("Canidate Mapping: " + this.candidateMapping);
			experiment.info("Feedback " + Validation.CORRECT);
		} else if( e.getSource() == btnIncorrect ) {
			experiment.userFeedback.setUserFeedback(Validation.INCORRECT);
			experiment.info("Iteration number: " + experiment.getIterationNumber());
			experiment.info("Canidate Mapping: " + this.candidateMapping);
			experiment.info("Feedback " + Validation.INCORRECT);
		} else if( e.getSource() == btnSkip ) {
			experiment.userFeedback.setUserFeedback(Validation.SKIP);
			experiment.info("Iteration number: " + experiment.getIterationNumber());
			experiment.info("Canidate Mapping: " + this.candidateMapping);
			experiment.info("Feedback " + Validation.SKIP);
		} else if( e.getSource() == btnEndExperiment ) {
			experiment.userFeedback.setUserFeedback(Validation.END_EXPERIMENT);
			experiment.info("Iteration number: " + experiment.getIterationNumber());
			experiment.info("Canidate Mapping: " + this.candidateMapping);
			experiment.info("Feedback " + Validation.END_EXPERIMENT);
		}
	}
	
}
