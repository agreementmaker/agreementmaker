package am.visualization;

import java.awt.BorderLayout;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.MatcherChangeEvent;
import am.app.mappingEngine.MatcherChangeListener;
import am.utility.WrapLayout;
import am.visualization.matrixplot.MatrixPlotPanel;

public class MatcherAnalyticsPanel extends JPanel implements MatcherChangeListener, MatcherAnalyticsEventDispatch {
	
	private static final long serialVersionUID = -5538266168231508803L;

	int plotsLoaded = 0;  // TODO: find a better way to do this
	
	
	ArrayList<MatcherAnalyticsEventListener> eventListeners = new ArrayList<MatcherAnalyticsEventListener>();
	
	public enum VisualizationType {
		CLASS_MATRIX, PROPERTIES_MATRIX
	}
	
	private JScrollPane scrOuterScrollbars;
	private JPanel pnlPlots;
	
	private VisualizationType type;
	
	public MatcherAnalyticsPanel( VisualizationType t ) {
		super();

		type = t;
		
		pnlPlots = createPlotsPanel();
		
		scrOuterScrollbars = createOuterScrollBars(pnlPlots);
		
		setLayout(new BorderLayout());
		add(scrOuterScrollbars);
		
		initializeMatchers();
		
		//Core.getInstance().addMatcherChangeListener(this);
		
	}
	
	/**
	 * If the panel is started after matchers have run, add the matchers that currently exist.
	 */
	private void initializeMatchers() {
		
		List<AbstractMatcher> matcherList = Core.getInstance().getMatcherInstances();
		for( AbstractMatcher a : matcherList ) {
			switch( type ) {
			case CLASS_MATRIX:
				if( a.getClassesMatrix() != null ) {
					addPlot(a, a.getClassesMatrix());
				}
				break;
				
			case PROPERTIES_MATRIX:
				if( a.getPropertiesMatrix() != null ) {
					addPlot(a, a.getPropertiesMatrix());
				}
				break;
			}
		}
		
		
	}
	
	private JScrollPane createOuterScrollBars(JPanel plots) {
		JScrollPane pane = new JScrollPane(plots);
		pane.setWheelScrollingEnabled(true);
		pane.getVerticalScrollBar().setUnitIncrement(20);
		return pane;
	}
	
	private JPanel createPlotsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel.setLayout( new WrapLayout(WrapLayout.LEADING, 10, 10) );
		return panel;
	}

	public VisualizationType getType() { return type; }
	
	/****************************** CHANGE LISTENERS *********************************/
	
	@Override
	public void matcherChanged(MatcherChangeEvent e) {
	
		switch( e.getEvent() ) {
		case MATCHER_ADDED:
			// when a matcher is added to the main
			AbstractMatcher a = e.getMatcher();
			switch( type ) {
			case CLASS_MATRIX:
				if( a.getClassesMatrix() != null ) {
					addPlot(a, a.getClassesMatrix());
				}
				break;
				
			case PROPERTIES_MATRIX:
				if( a.getPropertiesMatrix() != null ) {
					addPlot(a, a.getPropertiesMatrix());
				}
				break;
			}
			break;
		}
	}

	private void addPlot(AbstractMatcher a, AlignmentMatrix matrix) {
		
		
		MatrixPlotPanel newPlot = new MatrixPlotPanel(a, matrix, this);
		newPlot.getPlot().draw();
		
		addMatcherAnalyticsEventListener(newPlot);
		
		pnlPlots.add(newPlot);
		plotsLoaded++;
		//int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		//int panelHeight = plotsLoaded * newPlot.getHeight();
		//pnlPlots.setPreferredSize(new Dimension(screenWidth, panelHeight));
	}

	
	
	/** EVENT LISTENERS **/
	public void addMatcherAnalyticsEventListener( MatcherAnalyticsEventListener l )  { eventListeners.add(l); }
	public void removeMatcherAnalyticsEventListener( MatcherAnalyticsEventListener l ) { eventListeners.remove(l); }

	@Override
	public void broadcastEvent(MatcherAnalyticsEvent e) {
		for( int i = eventListeners.size()-1; i >= 0; i-- ) {  // count DOWN from max (for a very good reason, http://book.javanb.com/swing-hacks/swinghacks-chp-12-sect-8.html )
			eventListeners.get(i).receiveEvent(e);
		}
	}

}
