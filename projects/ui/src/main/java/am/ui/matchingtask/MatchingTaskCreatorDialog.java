package am.ui.matchingtask;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.DefaultMatcherParameters;
import am.app.mappingEngine.DefaultSelectionParameters;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.SelectionAlgorithm;
import am.app.ontology.Ontology;
import am.app.ontology.profiling.OntologyProfiler;
import am.app.ontology.profiling.OntologyProfiler.ParamType;
import am.app.ontology.profiling.OntologyProfilerPanel;
import am.ui.UICore;
import am.utility.CenterPanel;
import am.utility.messagesending.Message;
import am.utility.messagesending.MessageConsumer;
import am.utility.messagesending.MessageDispatch;
import am.utility.messagesending.MessageDispatchSupport;

/**
 * Given two ontologies, create a matching task object which will
 * be used to match these ontologies.
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 *
 */
public class MatchingTaskCreatorDialog extends JDialog implements MessageDispatch<Object>, ActionListener {

	private static final long serialVersionUID = 6754123828376018512L;
	
	private JTabbedPane mainPane = new JTabbedPane();
	
	private JButton btnRunMatchingTask = new JButton("Run Matching Task");
	private JButton btnCancel = new JButton("Cancel");
	
	private boolean canceled = false;
	
	private MessageDispatchSupport<Object> messageDispatch = new MessageDispatchSupport<Object>();
	
	private MatchingAlgorithmParametersPanel pnlMatchingAlgorithm;
	private SelectionAlgorithmParametersPanel pnlSelectionAlgorithm;
	private OntologyProfilerPanel pnlAnnotationProfiling;
	
	private Ontology sourceOntology, targetOntology;
	
	public MatchingTaskCreatorDialog(Ontology sourceOntology, Ontology targetOntology) {
		super(UICore.getUI().getUIFrame());
		
		this.sourceOntology = sourceOntology;
		this.targetOntology = targetOntology;
		
		MatchingTaskOverviewPanel overviewPanel = new MatchingTaskOverviewPanel(sourceOntology, targetOntology);
		messageDispatch.addConsumer(overviewPanel);
		
		pnlMatchingAlgorithm = new MatchingAlgorithmParametersPanel(this);
		pnlSelectionAlgorithm = new SelectionAlgorithmParametersPanel(this);
		
		mainPane.addTab("Matching Task Overview", 
				new CenterPanel(overviewPanel));
		mainPane.addTab("Matching Algorithm", pnlMatchingAlgorithm);
		mainPane.addTab("Selection Algorithm", pnlSelectionAlgorithm);
		if( Core.getInstance().getOntologyProfiler() != null ) {
			pnlAnnotationProfiling = 
					Core.getInstance().getOntologyProfiler().getProfilerPanel(ParamType.MATCHING_PARAMETERS);
			
			mainPane.addTab("Annotation Profiling", pnlAnnotationProfiling);
		}
		
		getContentPane().add(mainPane, BorderLayout.CENTER);
		
		btnCancel.addActionListener(this);
		btnRunMatchingTask.addActionListener(this);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		buttonsPanel.add(btnCancel);
		buttonsPanel.add(btnRunMatchingTask);
		
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		
		setPreferredSize(new Dimension(800,600));
		
		pack();
		setLocationRelativeTo(null);
	}
	
	/**
	 * @return A matching task defined by the user.  null if the user canceled.
	 */
	public MatchingTask getMatchingTask() {
		
		if( canceled ) return null; // no matching task if the user cancels
		
		AbstractMatcher matchingAlgorithm = null;
		try {
			matchingAlgorithm = pnlMatchingAlgorithm.getMatcher().getClass().newInstance();
			
			// add the input matchers
			int[] rowsIndex = UICore.getUI().getControlPanel().getTablePanel().getTable().getSelectedRows();
			if( rowsIndex.length > matchingAlgorithm.getMaxInputMatchers() ) {
				System.err.println("You have selected more than " + 
						matchingAlgorithm.getMaxInputMatchers() + " input matcher(s).  Using the top " + 
						matchingAlgorithm.getMaxInputMatchers() + " matcher(s).");
			}
			for(int i = 0; i < rowsIndex.length && i < matchingAlgorithm.getMaxInputMatchers(); i++) {
				MatchingTask input = Core.getInstance().getMatchingTasks().get(rowsIndex[i]);
				matchingAlgorithm.addInputTask(input);
			}
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DefaultMatcherParameters matcherParameters = pnlMatchingAlgorithm.getMatcherParameters();
		matcherParameters.setOntologies(sourceOntology, targetOntology);
		
		SelectionAlgorithm selectionAlgorithm = pnlSelectionAlgorithm.getSelectionAlgorithm();
		DefaultSelectionParameters selectionParameters = pnlSelectionAlgorithm.getSelectionParameters();
		
		// set the annotation profiling parameters.
		OntologyProfiler profiler = Core.getInstance().getOntologyProfiler();
		if( pnlAnnotationProfiling != null && profiler != null ) 
			profiler.setParams(ParamType.MATCHING_PARAMETERS, pnlAnnotationProfiling.getParameters());
		
		return new MatchingTask(matchingAlgorithm, matcherParameters,
								selectionAlgorithm, selectionParameters);		
	}
	
	
	public static void main(String[] args) {
		JDialog d = new MatchingTaskCreatorDialog(null, null);
		d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		d.setVisible(true);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getSource() == btnCancel ) {
			canceled = true;
			setVisible(false);
			return;
		}
		
		if( e.getSource() == btnRunMatchingTask ) {
			setVisible(false);
			return;
		}
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
	
	public enum MatchingTaskCreatorDialogMessages {
		SELECT_MATCHING_ALGORITHM, SELECT_SELECTION_ALGORITHM;
	}
}

