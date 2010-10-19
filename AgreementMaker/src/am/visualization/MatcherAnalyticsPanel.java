package am.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
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

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class MatcherAnalyticsPanel extends JPanel implements MatcherChangeListener {
	
	private static final long serialVersionUID = -5538266168231508803L;

	int plotsLoaded = 0;
	
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
		MatrixPlot newPlot = new MatrixPlot(matrix);
		newPlot.draw();
		
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
	
	
}
