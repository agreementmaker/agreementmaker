package am.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AlignmentMatrix;
import am.app.mappingEngine.MatcherChangeEvent;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.StringUtil.PorterStemmer;
import am.visualization.MatcherAnalyticsEvent.EventType;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class MatcherAnalyticsPanel extends JPanel implements MatcherChangeListener, MatcherAnalyticsEventDispatch {
	
	private static final long serialVersionUID = -5538266168231508803L;

	int plotsLoaded = 0;
	
	
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
		
		//Core.getInstance().addMatcherChangeListener(this);
		
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
		panel.setLayout( new FlowLayout(FlowLayout.LEADING, 10, 10) );
		return panel;
	}

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
		MatrixPlot newPlot = new MatrixPlot(matrix, this);
		newPlot.draw();
		
		addMatcherAnalyticsEventListener(newPlot);
		
		JPanel plotPanel = new JPanel();
		
		GroupLayout layPlotPanel = new GroupLayout(plotPanel);
		
		layPlotPanel.setHorizontalGroup( layPlotPanel.createParallelGroup()
				.addComponent(newPlot)
		);
		
		layPlotPanel.setVerticalGroup( layPlotPanel.createSequentialGroup()
				.addComponent(newPlot)
		);
		
		plotPanel.setLayout(layPlotPanel);
		
		plotPanel.setBorder( BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), a.getName().getMatcherName()));
		
		pnlPlots.add(plotPanel);
		plotsLoaded++;
		pnlPlots.setSize(scrOuterScrollbars.getWidth(), plotsLoaded * plotPanel.getHeight());
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int panelHeight = plotsLoaded * newPlot.getHeight();
		pnlPlots.setPreferredSize(new Dimension(screenWidth, panelHeight));
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
