package am.app.userfeedbackloop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.EventListenerList;

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
		ActionListener[] actionListeners = listeners.getListeners(ActionListener.class);
		
		for( int i = actionListeners.length-1; i >= 0; i-- ) {
			actionListeners[i].actionPerformed(e);
		}
	}
	
	public abstract void evaluate( FeedbackPropagation prop );
	
}
	