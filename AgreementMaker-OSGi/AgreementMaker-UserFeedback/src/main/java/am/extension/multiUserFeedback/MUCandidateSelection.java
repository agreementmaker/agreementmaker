package am.extension.multiUserFeedback;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.extension.userfeedback.CandidateSelection;
import am.extension.userfeedback.UFLExperiment;
import am.extension.userfeedback.ui.UFLControlGUI;

public abstract class MUCandidateSelection<T extends MUExperiment> extends CandidateSelection<T> {
	EventListenerList listeners;  // list of listeners for this class
	public Mapping selectedMapping;
	
	public MUCandidateSelection() {
		listeners = new EventListenerList();
	}
	
	public abstract void rank( T exp, String id );
	
	public abstract List<Mapping> getRankedMappings(alignType typeOfRanking, String id);
	public abstract List<Mapping> getRankedMappings(String id);
	
	public abstract Mapping getCandidateMapping(String id);
	
	@Override
	public Mapping getCandidateMapping() {
		throw new RuntimeException("Use getCandidateMapping(String id)");
	}
	
	@Override
	public List<Mapping> getRankedMappings() {
		throw new RuntimeException("Use getRankedMappings(String id)");
	}
	
	@Override
	public List<Mapping> getRankedMappings(alignType typeOfRanking) {
		throw new RuntimeException("Use getRankedMappings(alignType typeOfRanking, String id)");
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
