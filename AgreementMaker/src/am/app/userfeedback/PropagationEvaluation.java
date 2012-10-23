package am.app.userfeedback;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import am.app.userfeedback.ui.UFLControlGUI;

public abstract class PropagationEvaluation {

	EventListenerList listeners;  // list of listeners for this class
	
	public PropagationEvaluation() {
		listeners = new EventListenerList();
	}
	
	public void addActionListener( ActionListener l ) {
		listeners.add(ActionListener.class, l);
	}
	
	/**
	 * This method fires an action event.
	 * @param e Represents the action that was performed.
	 */
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
		ActionEvent e = new ActionEvent(this, 0, UFLControlGUI.ActionCommands.PROPAGATION_EVALUATION_DONE.name() );
		fireEvent(e);
	}
	
	public abstract void evaluate( UFLExperiment exp );
	
}
	