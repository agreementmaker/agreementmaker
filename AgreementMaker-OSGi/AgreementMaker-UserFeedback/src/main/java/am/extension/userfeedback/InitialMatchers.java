package am.extension.userfeedback;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.ui.UFLControlGUI;
import am.extension.userfeedback.utility.UFLutility;

/**
 * This class instantiates automatically instantiates a matcher configuration using
 * the ontology profiling methods.
 *  
 * @author cosmin
 *
 */
public abstract class InitialMatchers {

	protected UFLExperiment exp;
	
	EventListenerList listeners;  // list of listeners for this class
	
	public InitialMatchers() {
		listeners = new EventListenerList();
	}
	
	public abstract void run(UFLExperiment exp);
	public abstract List<AbstractMatcher> getComponentMatchers();
	public abstract Alignment<Mapping> getAlignment();
	public abstract Alignment<Mapping> getClassAlignment();
	public abstract Alignment<Mapping> getPropertyAlignment();
	public abstract AbstractMatcher getFinalMatcher(); // the matcher from which the final alignment is derived, and to which propagation should be done.
	
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
		UFLExperiment log = exp;
		
		// output the reference alignment
		Alignment<Mapping> referenceAlignment = exp.getReferenceAlignment();
		
		//FIXME: We should not be looking at the reference alignment here.
		UFLutility.logReferenceAlignment(referenceAlignment, exp);
		
		Alignment<Mapping> finalAlignment = getFinalMatcher().getAlignment();
		Alignment<Mapping> classAlignment = getFinalMatcher().getClassAlignmentSet();
		Alignment<Mapping> propertiesAlignment = getFinalMatcher().getPropertyAlignmentSet();

		log.info("Initial matchers have finished running.");
		log.info("Alignment contains " + finalAlignment.size() + " mappings. " + 
				  classAlignment.size() + " class mappings, " + propertiesAlignment.size() + " property mappings.");
		
		if (referenceAlignment == null) {
			log.info("Reference alignment is not defined.  Not doing any initial alignment statistics.");
			return;
		}
		
		log.info("Class mappings:");
		for( int i = 0; i < classAlignment.size(); i++ ) {
			Mapping currentMapping = classAlignment.get(i);
			boolean mappingCorrect = referenceAlignment.contains(currentMapping.getEntity1(),
					                                             currentMapping.getEntity2(), 
					                                             currentMapping.getRelation());
			
			String mappingAnnotation = "X";
			if( mappingCorrect || referenceAlignment == null ) mappingAnnotation = " ";
			
			log.info( i + ". " + mappingAnnotation + " " + currentMapping.toString() );
		}
		
		log.info("");
		
		log.info("Property mappings:");
		for( int i = 0; i < propertiesAlignment.size(); i++ ) {
			Mapping currentMapping = propertiesAlignment.get(i);
			boolean mappingCorrect = false;
			
			if( referenceAlignment != null && 
					referenceAlignment.contains(currentMapping.getEntity1(), currentMapping.getEntity2(), currentMapping.getRelation()) ) {
				mappingCorrect = true;
			}
			
			String mappingAnnotation = "X";
			if( mappingCorrect || referenceAlignment == null ) mappingAnnotation = " ";
			
			log.info( i + ". " + mappingAnnotation + " " + currentMapping.toString() );
		}
		
		log.info("");
		
		if( referenceAlignment != null ) {
			log.info("Missed mappings:");
			int missedMappingNumber = 0;
			for( Mapping referenceMapping : referenceAlignment ) {
				if( !finalAlignment.contains(referenceMapping.getEntity1(), referenceMapping.getEntity2(), referenceMapping.getRelation()) ) {
					log.info( missedMappingNumber + ". ? " + referenceMapping );
					missedMappingNumber++;
				}
			}
			
			log.info("");
		}

		// fire the event
		ActionEvent e = new ActionEvent(this, 0, UFLControlGUI.ActionCommands.INITIAL_MATCHERS_DONE.name() );
		fireEvent(e);
	}
	
}
