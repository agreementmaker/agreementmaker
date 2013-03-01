package am.extension.userfeedback;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.ui.UFLControlGUI;

public abstract class CandidateSelection {

	EventListenerList listeners;  // list of listeners for this class
	
	public CandidateSelection() {
		listeners = new EventListenerList();
	}
	
	public abstract void rank( UFLExperiment exp );
	
	public abstract List<Mapping> getRankedMappings(alignType typeOfRanking);
	public abstract List<Mapping> getRankedMappings();
	
	public abstract Mapping getCandidateMapping();
	
	
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
		ActionEvent e = new ActionEvent(this, 0, UFLControlGUI.ActionCommands.CANDIDATE_SELECTION_DONE.name() );
		fireEvent(e);
	}

}
