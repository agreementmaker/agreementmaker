package am.extension.userfeedback.common;

import java.lang.reflect.Constructor;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import am.app.Core;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.ontology.Ontology;
import am.app.ontology.ontologyParser.OntoTreeBuilder;
import am.extension.userfeedback.UFLRegistry.ExperimentRegistry;
import am.extension.userfeedback.experiments.UFLExperiment;
import am.extension.userfeedback.experiments.UFLExperimentSetup;
import am.extension.userfeedback.logic.api.UFLControlLogic;
import am.extension.userfeedback.preset.ExperimentPreset;
import am.extension.userfeedback.preset.MatchingTaskPreset;
import am.extension.userfeedback.ui.UFLProgressDisplay;
import am.utility.ArchiveManager;
import am.utility.Pair;
import am.utility.referenceAlignment.AlignmentUtilities;

/**
 * This class runs a single UFL experiment.
 * 
 * @author cosmin
 */
public class UFLExperimentRunner implements Runnable {

	private final EventListenerList listenerList = new EventListenerList();
	private final ChangeEvent stateChangeEvent = new ChangeEvent(this);
	
	private final MatchingTaskPreset task;
	private final ExperimentPreset experiment;
	private UFLProgressDisplay display;
	
	private UFLExperiment uflExperiment;
	
	public UFLExperimentRunner(Pair<MatchingTaskPreset, ExperimentPreset> run) {
		this.task = run.getLeft();
		this.experiment = run.getRight();
	}
	
	public UFLExperimentRunner(MatchingTaskPreset task, ExperimentPreset exp) {
		this.task = task;
		this.experiment = exp;
	}
	
	@Override
	public void run() {
		UFLExperimentSetup newSetup = experiment.getExperimentSetup();
		
		// instantiate the experiment
		ExperimentRegistry experimentRegistryEntry = newSetup.exp;
		try {
			Constructor<? extends UFLExperiment> constructor = 
					experimentRegistryEntry.getEntryClass().getConstructor(new Class<?>[] { UFLExperimentSetup.class });
			uflExperiment = constructor.newInstance(newSetup);
		} catch (Exception e) {
			// exceptions caught, skip this run
			e.printStackTrace();
			fireChange();
			return;
		}
		
		ArchiveManager ex = new ArchiveManager();
		ex.checkOntologyFile(task.getSourceOntology());
		ex.checkOntologyFile(task.getTargetOntology());
		
		final Ontology sourceOntology = OntoTreeBuilder.loadOWLOntology(task.getSourceOntology());
		final Ontology targetOntology = OntoTreeBuilder.loadOWLOntology(task.getTargetOntology());
		
		uflExperiment.setSourceOntology(sourceOntology);
		uflExperiment.setTargetOntology(targetOntology);
		
		Core.getInstance().setSourceOntology(sourceOntology);
		Core.getInstance().setTargetOntology(targetOntology);
		
		if( task.hasReference() ) {
			Alignment<Mapping> alignment = AlignmentUtilities.getOAEIAlignment(
					task.getReference(), sourceOntology, targetOntology);
			uflExperiment.setReferenceAlignment(alignment);
		}
		
		uflExperiment.gui = display;

		// Step 1.  experiment is starting.  Initialize the experiment setup.		
		try {
			final UFLControlLogic logic = uflExperiment.getControlLogic();
			logic.runExperiment(uflExperiment);
			while (!uflExperiment.experimentHasCompleted()) {
				Thread.sleep(1000);
			}
		} catch (Exception e) {
				e.printStackTrace();
		}
		
		Core.getInstance().removeOntology(sourceOntology);
		Core.getInstance().removeOntology(targetOntology);
		
		fireChange();
	}
	
	public void addChangeListener(ChangeListener l) {
	    listenerList.add(ChangeListener.class, l);
	}
	
	public void removeChangeListener(ChangeListener l) {
	    listenerList.remove(ChangeListener.class, l);
	}
	
	protected void fireChange() {
	    for (ChangeListener l: listenerList.getListeners(ChangeListener.class)) {
	        l.stateChanged(stateChangeEvent);
	    }
	}

	public void setUFLProgressDisplay(UFLProgressDisplay display) {
		this.display = display;
	}
	
	public UFLExperiment getExperiment() {
		return uflExperiment;
	}
}
