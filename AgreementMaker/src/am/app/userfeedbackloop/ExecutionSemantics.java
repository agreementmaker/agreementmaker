package am.app.userfeedbackloop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.event.EventListenerList;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;

public abstract class ExecutionSemantics {

	EventListenerList listeners;  // list of listeners for this class
	
	public ExecutionSemantics() {
		listeners = new EventListenerList();
	}
	
	public abstract void run(UFLExperiment exp);
	public abstract List<AbstractMatcher> getComponentMatchers();
	public abstract Alignment<Mapping> getAlignment();
	
	public void addActionListener( ActionListener l ) {
		listeners.add(ActionListener.class, l);
	}
	/**
	 * This method fires an action event.
	 * @param e Represents the action that was performed.
	 */
	protected void fireEvent( ActionEvent e ) {
		ActionListener[] actionListeners = listeners.getListeners(ActionListener.class);
		
		for( int i = actionListeners.length-1; i > 0; i-- ) {
			actionListeners[i].actionPerformed(e);
		}
	}
	
}
