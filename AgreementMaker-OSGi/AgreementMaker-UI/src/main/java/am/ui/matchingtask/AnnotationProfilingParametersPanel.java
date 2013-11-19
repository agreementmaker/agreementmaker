package am.ui.matchingtask;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherFeature;
import am.app.ontology.profiling.OntologyProfilerPanel;
import am.app.ontology.profiling.ProfilerRegistry;
import am.app.ontology.profiling.OntologyProfiler.ParamType;

public class AnnotationProfilingParametersPanel extends JPanel {

	private static final long serialVersionUID = -9124301137070897217L;

	private AbstractMatcher matcher;
	
	/* Ontology profiling panel */
	private OntologyProfilerPanel matchTimeProfilingPanel = null;
	
	private void createPanel() {
		// initialize the ontology profiling panel.		
		JPanel profilingPanel = new JPanel();
		profilingPanel.setLayout(new BorderLayout());


		if( Core.getInstance().getOntologyProfiler() == null ) {
			// there is no ontology profiling algorithm defined.	
			profilingPanel.add(new JLabel("No ontology profiling algorithm selected."), BorderLayout.CENTER);
		} else if( !matcher.supportsFeature( MatcherFeature.ONTOLOGY_PROFILING ) ) {
			profilingPanel.add(new JLabel("This matcher does not support ontology profiling."), BorderLayout.CENTER);
		} else if( !Core.getInstance().getOntologyProfiler().needsParams(ParamType.MATCHING_PARAMETERS) ){
			// the ontology profiler does not have a match time parameters panel
			ProfilerRegistry name = Core.getInstance().getOntologyProfiler().getName();
			profilingPanel.add(new JLabel( name.getProfilerName() + " has been selected." + 
					"\nThe profiling algorithm does not need parameters at match time."), BorderLayout.CENTER);
		} else {
			if( matchTimeProfilingPanel == null ) {
				matchTimeProfilingPanel = Core.getInstance().getOntologyProfiler().getProfilerPanel(ParamType.MATCHING_PARAMETERS);
			}
			JScrollPane profilingScroll = new JScrollPane(matchTimeProfilingPanel);
			profilingScroll.getVerticalScrollBar().setUnitIncrement(20);
			profilingPanel.add( profilingScroll, BorderLayout.CENTER);
		}
	}

}
