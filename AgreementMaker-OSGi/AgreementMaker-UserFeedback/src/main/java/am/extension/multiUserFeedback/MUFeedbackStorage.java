package am.extension.multiUserFeedback;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.UserFeedback.Validation;
import am.extension.userfeedback.ui.UFLControlGUI;

public abstract class MUFeedbackStorage <T extends UFLExperiment>{
	SimilarityMatrix classes;
	SimilarityMatrix properties;
	
	EventListenerList listeners;  // list of listeners for this class
	
	public MUFeedbackStorage() {
		listeners = new EventListenerList();
	}
	public abstract void addFeedback(T exp, Mapping candidateMapping, Validation val, String id);
	
	public abstract Object[][] getTrainingSet();
	
	public abstract void computeFinalMatrix();
	
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
	
	protected void done() {
		ActionEvent e = new ActionEvent(this, 0, UFLControlGUI.ActionCommands.EXECUTION_SEMANTICS_DONE.name() );
		fireEvent(e);
	}
	
}
