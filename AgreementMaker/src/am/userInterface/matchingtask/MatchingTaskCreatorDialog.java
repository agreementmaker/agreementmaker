package am.userInterface.matchingtask;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import am.app.Core;
import am.app.mappingEngine.MatchingTask;
import am.app.ontology.Ontology;
import am.utility.CenterPanel;
import am.utility.messagesending.Message;
import am.utility.messagesending.MessageConsumer;
import am.utility.messagesending.MessageDispatch;
import am.utility.messagesending.MessageDispatchSupport;

/**
 * Given two ontologies, create a matching task object which will
 * be used to match these ontologies.
 * 
 * @author Cosmin Stroe
 *
 */
public class MatchingTaskCreatorDialog extends JDialog implements MessageDispatch<Object> {

	private static final long serialVersionUID = 6754123828376018512L;
	
	private JTabbedPane mainPane = new JTabbedPane();
	
	private MessageDispatchSupport<Object> messageDispatch = new MessageDispatchSupport<Object>();
	
	public MatchingTaskCreatorDialog(Ontology sourceOntology, Ontology targetOntology) {
		super();
		
		MatchingTaskOverviewPanel overviewPanel = new MatchingTaskOverviewPanel(sourceOntology, targetOntology);
		messageDispatch.addConsumer(overviewPanel);
		
		mainPane.addTab("Matching Task Overview", 
				new CenterPanel(overviewPanel));
		mainPane.addTab("Matching Algorithm", new MatchingAlgorithmParametersPanel(this));
		mainPane.addTab("Selection Algorithm", new SelectionAlgorithmParametersPanel(this));
		if( Core.getInstance().getOntologyProfiler() != null ) {
			mainPane.addTab("Annotation Profiling", 
				Core.getInstance().getOntologyProfiler().getProfilerPanel(false));
		}
		
		getContentPane().add(mainPane, BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(800,600));
		
		pack();
		setLocationRelativeTo(null);
	}
	
	
	
	public MatchingTask getMatchingTask() {
		// FIXME: Implement this -- Cosmin.
		return null;
	}
	
	
	public static void main(String[] args) {
		JDialog d = new MatchingTaskCreatorDialog(null, null);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setVisible(true);
	}

	
	// MessageDispatch support
	
	@Override
	public void publish(Message<Object> message) {
		messageDispatch.publish(message);
	}

	@Override
	public void addConsumer(MessageConsumer<Object> consumer) {
		messageDispatch.addConsumer(consumer);
	}

	@Override
	public void addConsumer(MessageConsumer<Object> consumer, String messageKey) {
		messageDispatch.addConsumer(consumer, messageKey);
	}

	@Override
	public void removeConsumer(MessageConsumer<Object> consumer) {
		messageDispatch.removeConsumer(consumer);
	}
	
	public enum Messages {
		SELECT_MATCHING_ALGORITHM, SELECT_SELECTION_ALGORITHM;
	}
}

