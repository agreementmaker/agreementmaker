package am.extension.multiUserFeedback.storage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.ui.UFLControlGUI;

public abstract class FeedbackAgregation <T extends UFLExperiment>{
	SimilarityMatrix classes;
	SimilarityMatrix properties;
	Mapping candidateMapping;
	Validation userFeedback;
	EventListenerList listeners;  // list of listeners for this class
	
	public FeedbackAgregation() {
		listeners = new EventListenerList();
	}
	public abstract void addFeedback(T exp);
	
	public abstract double[][] getTrainingSet(alignType type, String quantity);
	
	public void addActionListener( ActionListener l ) {
		listeners.add(ActionListener.class, l);
	}
	
	
	protected void fireEvent( ActionEvent e ) {
		final ActionEvent evt = e;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ActionListener[] actionListeners = listeners.getListeners(ActionListener.class);
				
				for( int i = actionListeners.length-1; i >= 0; i-- ) {
					actionListeners[i].actionPerformed(evt);
				}
			}
		});
		
	}
	
	public Mapping getCandidateMapping() {
		return candidateMapping;
	}
	
	protected void done() {
		ActionEvent e = new ActionEvent(this, 0, UFLControlGUI.ActionCommands.FEEDBACK_AGREGATION_DONE.name() );
		fireEvent(e);
	}
	
}
