package am.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.SimilarityMatrix;
import am.app.mappingEngine.MatcherChangeEvent;
import am.app.mappingEngine.MatcherChangeListener;
import am.utility.WrapLayout;
import am.visualization.MatcherAnalyticsEvent.EventType;
import am.visualization.matrixplot.MatrixPlotPanel;

public class MatcherAnalyticsPanel extends JPanel implements MatcherChangeListener, MatcherAnalyticsEventDispatch {
	
	private static final long serialVersionUID = -5538266168231508803L;

	int plotsLoaded = 0;  // TODO: find a better way to do this
	
	ArrayList<MatcherAnalyticsEventListener> eventListeners = new ArrayList<MatcherAnalyticsEventListener>();
	
	public enum VisualizationType {
		CLASS_MATRIX, PROPERTIES_MATRIX
	}
	
	private JPanel pnlToolbar;
	
	private JScrollPane scrOuterScrollbars;
	private JPanel pnlPlots;
	
	private JTextField txtClusterThreshold;
	private JButton btnApplyThreshold;
	
	private VisualizationType type;
	
	public MatcherAnalyticsPanel( VisualizationType t ) {
		super();

		type = t;
		
		pnlToolbar = createToolbarPanel();
		
		pnlPlots = createPlotsPanel();
		
		scrOuterScrollbars = createOuterScrollBars(pnlPlots);
		
		setLayout(new BorderLayout());
		add(pnlToolbar, BorderLayout.NORTH);
		add(scrOuterScrollbars, BorderLayout.CENTER);
		
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
	
	private JPanel createToolbarPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout( new FlowLayout(FlowLayout.LEADING) );
		
		JCheckBox chkClusters = new JCheckBox("View individual cluster:");
		JComboBox boxClusters = new JComboBox();
		
		JLabel lblClusterThreshold = new JLabel( "Clustering Threshold:");
		txtClusterThreshold = new JTextField();
		//txtClusterThreshold.setMinimumSize(new Dimension( 400, lblClusterThreshold.getHeight()));
		
		btnApplyThreshold = new JButton("Apply");
		
		panel.add(chkClusters);
		panel.add(boxClusters);
		panel.add(Box.createHorizontalStrut(10));
		panel.add(lblClusterThreshold);
		panel.add(txtClusterThreshold);
		panel.add(btnApplyThreshold);

		txtClusterThreshold.setPreferredSize(new Dimension( 70, btnApplyThreshold.getPreferredSize().height));
		
		return panel;
	}
	
	private JScrollPane createOuterScrollBars(JPanel plots) {
		JScrollPane pane = new JScrollPane();
		pane.setViewportView(plots);
		pane.setWheelScrollingEnabled(true);
		pane.getVerticalScrollBar().setUnitIncrement(20);
		return pane;
	}
	
	private JPanel createPlotsPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder());
		panel.setLayout( new WrapLayout(WrapLayout.LEADING, 10, 10) );
		return panel;
	}

	public VisualizationType getType() { return type; }
	public JPanel getPlotsPanel() { return pnlPlots; }
	
	/****************************** CHANGE LISTENERS *********************************/
	
	@Override
	public void matcherChanged(final MatcherChangeEvent e) {
	
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
			
		case MATCHER_ALIGNMENTSET_UPDATED:
			Runnable fire = new Runnable() {
				public void run() {
					broadcastEvent( new MatcherAnalyticsEvent( this,  EventType.MATRIX_UPDATED,  e.getMatcher() ));
				}
			};
			
			SwingUtilities.invokeLater(fire);
			break;
		}
		
		
			
	}

	private void addPlot(AbstractMatcher a, SimilarityMatrix matrix) {
		
		
		MatrixPlotPanel newPlot = new MatrixPlotPanel(a, matrix, this);
		newPlot.getPlot().draw(false);
		
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
