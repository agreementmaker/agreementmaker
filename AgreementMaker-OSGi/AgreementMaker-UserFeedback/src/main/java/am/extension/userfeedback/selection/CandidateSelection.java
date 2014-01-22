package am.extension.userfeedback.selection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.ui.UFLControlGUI;

public abstract class CandidateSelection<T extends UFLExperiment> {

	protected EventListenerList listeners;  // list of listeners for this class
	
	protected Mapping selectedMapping;
	
	public CandidateSelection() {
		listeners = new EventListenerList();
	}
	
	/**
	 * Rank and select a candidate mapping.
	 */
	public abstract void rank( T exp );
	
	public abstract List<Mapping> getRankedMappings();
	public abstract List<Mapping> getRankedMappings(alignType typeOfRanking);
	
	/**
	 * @return The mapping that was selected by the candidate selection by the
	 *         last call to {@link #rank(UFLExperiment)}. Multiple calls to this
	 *         method (without any calls to {@link #rank(UFLExperiment)}) should
	 *         return the same mapping.
	 */
	public Mapping getSelectedMapping() {
		return selectedMapping;
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
		ActionEvent e = new ActionEvent(this, 0, UFLControlGUI.ActionCommands.CANDIDATE_SELECTION_DONE.name() );
		fireEvent(e);
	}

}
