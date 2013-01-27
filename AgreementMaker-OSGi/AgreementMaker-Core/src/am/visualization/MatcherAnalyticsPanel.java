package am.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.alignType;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.similarityMatrix.ArraySimilarityMatrix;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.app.ontology.Node;
import am.app.ontology.Ontology;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.ClusterFactory;
import am.evaluation.clustering.ClusterFactory.ClusteringType;
import am.evaluation.clustering.ClusteringMethod;
import am.evaluation.disagreement.DisagreementCalculationDialog;
import am.evaluation.disagreement.DisagreementCalculationMethod;
import am.evaluation.disagreement.variance.VarianceDisagreement;
import am.utility.WrapLayout;
import am.visualization.MatcherAnalyticsEvent.EventType;
import am.visualization.matrixplot.MatrixPlotPanel;
import am.visualization.matrixplot.OrderedMatrixPlot;

public class MatcherAnalyticsPanel extends JPanel implements MatcherChangeListener, MatcherAnalyticsEventDispatch, ActionListener {
	
	private static final long serialVersionUID = -5538266168231508803L;

	int plotsLoaded = 0;  // TODO: find a better way to do this
	
	ArrayList<MatcherAnalyticsEventListener> eventListeners = new ArrayList<MatcherAnalyticsEventListener>();
	
	public enum VisualizationType {
		CLASS_MATRIX, PROPERTIES_MATRIX
	}
	
	private JPanel pnlToolbar;
	private JPanel pnlInfo;
	
	private JScrollPane scrOuterScrollbars;
	private JPanel pnlPlots;
	
	private JButton btnDisagreementMeasure;
	private JButton btnCandidateSelection, btnConfirmMapping, btnRefuteMapping;
	
	//private JTextField txtClusterThreshold;
	//private JButton btnApplyThreshold;
	
	private VisualizationType type;
	private JLabel lblMapping;
	private Point currentSelectedMapping;
	
	private SimilarityMatrix disagreementMatrix = null;
	private SimilarityMatrix feedbackMatrix = null;
	private AbstractMatcher feedbackMatcher = null;
	private boolean[][] filteredCells = null;
	
	private JComboBox<String> cmbTopK;
	
	private AbstractMatcher refMatcher = null;
	private ClusteringMethod clusterMethod = null;
	private Mapping[] topK;
	private JTextField txtE;
	private List<Cluster<Mapping>> topKClusters;
	
	private WrapLayout wrap;
	
	public MatcherAnalyticsPanel( VisualizationType t ) {
		super();

		type = t;
		
		pnlToolbar = createToolbarPanel();
		
		pnlInfo = createInfoPanel();
		
		pnlPlots = createPlotsPanel();
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		
		topPanel.add(pnlToolbar, BorderLayout.NORTH);
		topPanel.add(pnlInfo, BorderLayout.CENTER);
		
		scrOuterScrollbars = createOuterScrollBars(pnlPlots);
		
		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.NORTH);
		add(scrOuterScrollbars, BorderLayout.CENTER);
		
		initializeMatchers();
		
		//Core.getInstance().addMatcherChangeListener(this);
		
	}
	
	/**
	 * If the panel is started after matchers have run, add the matchers that currently exist.
	 */
	private void initializeMatchers() {
		
		List<AbstractMatcher> matcherList = Core.getInstance().getMatchingAlgorithms();
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
	
	private JPanel createInfoPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout( new FlowLayout( FlowLayout.LEADING ) );
		
		lblMapping = new JLabel(" ");
		
		panel.add(lblMapping);
		
		return panel;
		
	}
	
	private JPanel createToolbarPanel() {
		JPanel panel = new JPanel();
		
		panel.setLayout( new WrapLayout(WrapLayout.LEADING) );
		
		//JCheckBox chkClusters = new JCheckBox("View individual cluster:");
		//JComboBox<String> boxClusters = new JComboBox<String>();
		
		//JLabel lblClusterThreshold = new JLabel( "Clustering Threshold:");
		//txtClusterThreshold = new JTextField();
		//txtClusterThreshold.setMinimumSize(new Dimension( 400, lblClusterThreshold.getHeight()));
		
		//btnApplyThreshold = new JButton("Apply");
		//panel.add(chkClusters);
		//panel.add(boxClusters);
		//panel.add(Box.createHorizontalStrut(10));
		
		btnDisagreementMeasure = new JButton("Calculate Disagreement");
		btnDisagreementMeasure.addActionListener(this);
		
		panel.add(btnDisagreementMeasure);
		panel.add(Box.createHorizontalStrut(10));
		
		btnCandidateSelection = new JButton("Candidate Selection");
		btnCandidateSelection.addActionListener(this);
		panel.add(btnCandidateSelection);
		panel.add(Box.createHorizontalStrut(10));
		
		panel.add(new JLabel("Candidate Mapping:") );
		cmbTopK = new JComboBox<String>();
		cmbTopK.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		cmbTopK.addActionListener(this);
		panel.add(cmbTopK);
		panel.add(Box.createHorizontalStrut(10));
		
		
		panel.add(new JLabel("e:"));
		txtE = new JTextField("0.2");
		panel.add(txtE);
		
		btnConfirmMapping = new JButton("Confirm Mapping");
		btnConfirmMapping.addActionListener(this);
		panel.add(btnConfirmMapping);
		
		btnRefuteMapping = new JButton("Refute Mapping");
		btnRefuteMapping.addActionListener(this);
		panel.add(btnRefuteMapping);
		
		//panel.add(lblClusterThreshold);
		//panel.add(txtClusterThreshold);
		//panel.add(btnApplyThreshold);

		//txtClusterThreshold.setPreferredSize(new Dimension( 70, btnApplyThreshold.getPreferredSize().height));
		
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
		wrap = new WrapLayout(WrapLayout.LEADING, 10, 10);
		panel.setLayout( wrap );
		return panel;
	}

	public VisualizationType getType() { return type; }
	public JPanel getPlotsPanel() { return pnlPlots; }
	public void setMappingLabel(String label) { lblMapping.setText(label); }
	
	/****************************** CHANGE LISTENERS *********************************/
	
	@Override
	public void matcherChanged(final MatchingTaskChangeEvent e) {
	
		switch( e.getEvent() ) {
		case MATCHER_ADDED:
			// when a matcher is added to the main
			AbstractMatcher a = e.getTask().matchingAlgorithm;
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
			final Object sourceObject = this;
			Runnable fire = new Runnable() {
				public void run() {
					broadcastEvent( new MatcherAnalyticsEvent( sourceObject,  EventType.MATRIX_UPDATED,  e.getTask().matchingAlgorithm ));
				}
			};
			
			SwingUtilities.invokeLater(fire);
			break;
		}
		
		
			
	}

	private MatrixPlotPanel addPlot(AbstractMatcher a, SimilarityMatrix matrix) {
		
		
		MatrixPlotPanel newPlot = new MatrixPlotPanel(a, matrix, this);
		newPlot.getPlot().draw(false);
		
		addMatcherAnalyticsEventListener(newPlot);
		
		pnlPlots.add(newPlot);
		plotsLoaded++;
		//int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		//int panelHeight = plotsLoaded * newPlot.getHeight();
		//pnlPlots.setPreferredSize(new Dimension(screenWidth, panelHeight));
		wrap.layoutContainer(pnlPlots);
		revalidate();
		repaint();
		
		return newPlot;
	}

	private MatrixPlotPanel addPlot(String name, SimilarityMatrix matrix, Gradient g) {
		
		
		MatrixPlotPanel newPlot = new MatrixPlotPanel(name, matrix, this, g);
		newPlot.getPlot().setGradient(g);
		newPlot.getPlot().draw(true);
		
		addMatcherAnalyticsEventListener(newPlot);
		
		pnlPlots.add(newPlot);
		plotsLoaded++;
		wrap.layoutContainer(pnlPlots);
		revalidate();
		repaint();
		
		return newPlot;
	}
	
	/** EVENT LISTENERS **/
	public void addMatcherAnalyticsEventListener( MatcherAnalyticsEventListener l )  { eventListeners.add(l); }
	public void removeMatcherAnalyticsEventListener( MatcherAnalyticsEventListener l ) { eventListeners.remove(l); }

	@Override
	public void broadcastEvent(MatcherAnalyticsEvent e) {	
		
		if( e.type == EventType.SET_FEEDBACK ) {
			// the feedback matcher has been set
			feedbackMatcher = ((AbstractMatcher)e.payload);
			if( type == VisualizationType.CLASS_MATRIX ) feedbackMatrix = ((AbstractMatcher)e.payload).getClassesMatrix();
			if( type == VisualizationType.PROPERTIES_MATRIX ) feedbackMatrix = ((AbstractMatcher)e.payload).getPropertiesMatrix();
			
			if( feedbackMatrix == null ) {
				System.err.println("Feedback matrix is null, this should not happen.");
				feedbackMatrix = ((AbstractMatcher)e.payload).getClassesMatrix();
			}
			
			return;
		}
		
		if( e.type == EventType.REMOVE_PLOT ) {
			// remove this plot.
			MatrixPlotPanel ptor = (MatrixPlotPanel) e.getSource();
			pnlPlots.remove(ptor);
			plotsLoaded--;
			removeMatcherAnalyticsEventListener(ptor);
			wrap.layoutContainer(pnlPlots);
			revalidate();
			repaint();
			return;
		}
		
		if( e.type == EventType.VIEW_ORDERED_PLOT ) {
			MatrixPlotPanel payload = (MatrixPlotPanel)e.payload;
			
			OrderedMatrixPlot plot = new OrderedMatrixPlot(payload.getMatcher(), payload.getMatrix(), payload);
			
			MatrixPlotPanel newPlot = new MatrixPlotPanel(payload.getMatcher(), this, plot);
			newPlot.getPlot().draw(false);
			
			addMatcherAnalyticsEventListener(newPlot);
			
			pnlPlots.add(newPlot);
			plotsLoaded++;
		}
		
		if( e.type == EventType.SET_REFERENCE ) {
			refMatcher = (AbstractMatcher)e.payload;
		}
		
		for( int i = eventListeners.size()-1; i >= 0; i-- ) {  // count DOWN from max (for a very good reason, http://book.javanb.com/swing-hacks/swinghacks-chp-12-sect-8.html )
			eventListeners.get(i).receiveEvent(e);
		}
		
		// update the mapping label if we are selecting a mapping.
		if( e.type == EventType.SELECT_MAPPING ) {
			Point mapRowCol = (Point) e.payload;
			currentSelectedMapping = mapRowCol;
			
			MatrixPlotPanel plotPanel = null;
			if( plotPanel == null ) {
				if( e.getSource() instanceof MatrixPlotPanel ) {
					plotPanel = (MatrixPlotPanel) e.getSource();
				} else {
					// Initiated by the combo box.
					for( MatcherAnalyticsEventListener l : eventListeners ) {
						if ( l instanceof MatrixPlotPanel ) {
							plotPanel = (MatrixPlotPanel) l;
						}
					}
				}
			}
			if( plotPanel == null ) { return; }  // If there are not MatrixPlotPanels, just return.
			
			Mapping selectedMapping = plotPanel.getPlot().getMatrix().get(mapRowCol.x, mapRowCol.y);
			Node sNode = null;
			Node tNode = null;
			if( selectedMapping == null ) {
				// we have to dig deeper.
				Ontology source = null;
				Ontology target = null;
				if( plotPanel.getMatcher() == null ) {
					// null matcher, get everything from Core
					source = Core.getInstance().getSourceOntology();
					target = Core.getInstance().getTargetOntology();
				} else {
					source = plotPanel.getMatcher().getSourceOntology();
					target = plotPanel.getMatcher().getTargetOntology();
				}
				if( source != null && target != null ) {
					if( type == VisualizationType.CLASS_MATRIX ) {
						sNode = source.getClassesList().get(mapRowCol.x);
						tNode = target.getClassesList().get(mapRowCol.y);
					} else if( type == VisualizationType.PROPERTIES_MATRIX ) {
						sNode = source.getPropertiesList().get(mapRowCol.x);
						tNode = target.getPropertiesList().get(mapRowCol.y);
					}
				}
			} else {
				sNode = selectedMapping.getEntity1();
				tNode = selectedMapping.getEntity2();
			}
			if( sNode != null && tNode != null ) {
				// FIXME: Display interactive graphical representation of the node, not text.
				lblMapping.setText(sNode.getLocalName() + " <--> " + tNode.getLocalName());
			}
		}
	}

	@Override
	public void buildClusters(ClusteringType t) {

		// make a list of the available matchers
		
		ArrayList<AbstractMatcher> matcherList = new ArrayList<AbstractMatcher>();
		for( MatcherAnalyticsEventListener l : eventListeners ) {
			if( l.getMatcher() != null ) matcherList.add(l.getMatcher());
		}
		
		ClusteringMethod method = ClusterFactory.createClusteringMethod(t, matcherList);
		
		if( method == null ) return; 
		Cluster<Mapping> c = method.getCluster(currentSelectedMapping.x, currentSelectedMapping.y, type);
		
		
		// fire an event telling the plots to display the cluster
		
		
		
		//MatcherAnalyticsEvent displayClusterEvent = new MatcherAnalyticsEvent(this, EventType.DISPLAY_CLUSTER, c);
		//broadcastEvent(displayClusterEvent);
		
		switch( type ) {
		case CLASS_MATRIX: {
			SimilarityMatrix matrix = new ArraySimilarityMatrix(Core.getInstance().getSourceOntology(), 
																Core.getInstance().getTargetOntology(),
																alignType.aligningClasses);	
			MatrixPlotPanel newPanel = addPlot("Cluster View", matrix, new Gradient(Color.WHITE, Color.WHITE));
			newPanel.setCluster(c);
			break;
		}
		case PROPERTIES_MATRIX: {
			SimilarityMatrix matrix = new ArraySimilarityMatrix(Core.getInstance().getSourceOntology(), 
					Core.getInstance().getTargetOntology(),
					alignType.aligningProperties);	
			MatrixPlotPanel newPanel = addPlot("Cluster View", matrix, new Gradient(Color.WHITE, Color.WHITE));
			newPanel.setCluster(c);
			break;
		}
		}
		
		
	}

	public void buildDisagreementMatrix() {
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( e.getSource() == btnDisagreementMeasure ) {
			// make a list of available matchers
			ArrayList<AbstractMatcher> matcherList = new ArrayList<AbstractMatcher>();
			for( MatcherAnalyticsEventListener l : eventListeners ) {
				matcherList.add(l.getMatcher());
			}
			
			// compute and display disagreement matrix
			DisagreementCalculationMethod disMethod = new VarianceDisagreement();
			disMethod.setAvailableMatchers(matcherList);
			
			DisagreementCalculationDialog calcD = new DisagreementCalculationDialog(disMethod);
			
			calcD.pack();
			calcD.setModal(true);
			calcD.setLocationRelativeTo(null);
			calcD.setVisible(true);
			
			disMethod.setParameters( calcD.getParameters() );
			
			disagreementMatrix = disMethod.getDisagreementMatrix( type );
			
			Gradient g = new Gradient(Color.BLACK, Color.WHITE);
			g.setMax( disagreementMatrix.getMaxValue() );
			
			
			addPlot("Disagreement Matrix", disagreementMatrix, g);
			
			return;
		}
		
		if( e.getSource() == btnCandidateSelection ) {
			
			// initialize the clustering method
			if( clusterMethod == null ) {
			
				ArrayList<AbstractMatcher> matcherList = new ArrayList<AbstractMatcher>();
				for( MatcherAnalyticsEventListener l : eventListeners ) {
					if( l.getMatcher() != null ) matcherList.add(l.getMatcher());
				}
			
				clusterMethod = ClusterFactory.createClusteringMethod(ClusteringType.LOCAL_BY_THRESHOLD, matcherList);
			}
			
			
			int k = 10;

			if( filteredCells != null ) { topK = disagreementMatrix.getTopK(k, filteredCells); } 
			else { topK = disagreementMatrix.getTopK(k); }
			
			topKClusters = new ArrayList<Cluster<Mapping>>(k);
			
			//String[] topKDescription = new String[k];
			Alignment<Mapping> refAlignment = null;
			if( refMatcher != null && type == VisualizationType.CLASS_MATRIX ) refAlignment = refMatcher.getClassAlignmentSet();
			if( refMatcher != null && type == VisualizationType.PROPERTIES_MATRIX ) refAlignment = refMatcher.getPropertyAlignmentSet();
			
			cmbTopK.removeAllItems();
			for( int i = 0; i < k; i++ ) {
				String inReference = "";
				if( refAlignment != null && refAlignment.contains(topK[i].getSourceKey() , topK[i].getTargetKey() ) ) inReference = "*";
				Cluster<Mapping> cl = clusterMethod.getCluster(topK[i].getSourceKey(), topK[i].getTargetKey(), type);
				topKClusters.set(i, cl);
				String topKDescription = topK[i].getEntity1().toString() +"<->" + topK[i].getEntity2().toString() + 
									     " Cl:(" + cl.size() + ") " + inReference;
				cmbTopK.insertItemAt(topKDescription, i);
			}
			
			return;
		}
		
		if( e.getSource() == btnConfirmMapping ) {
			// TODO: the user has clicked to confirm a mapping.
			
			// the user has selected a mapping from the drop down box.
			int sel = cmbTopK.getSelectedIndex();
			
			Mapping selectedMapping = topK[sel];
			
			// build the cluster for the mapping.
			//Cluster<Mapping> cl = clusterMethod.getCluster(selectedMapping.getSourceKey(), selectedMapping.getTargetKey(), VisualizationType.CLASS_MATRIX);
			
			Cluster<Mapping> cl = topKClusters.get(sel);
			
			double eValue = Double.parseDouble( txtE.getText() );
			
			if( filteredCells == null ) { filteredCells = new boolean[disagreementMatrix.getRows()][disagreementMatrix.getColumns()]; }
			
			rewardCluster( cl, eValue, feedbackMatrix );
			filteredCells[selectedMapping.getSourceKey()][selectedMapping.getTargetKey()] = true;
			
			feedbackMatrix.set(selectedMapping.getSourceKey(), selectedMapping.getTargetKey(), new Mapping(selectedMapping.getEntity1(), selectedMapping.getEntity2(), 1.0d) );
			
			
			
			
			feedbackMatcher.select();
			return;
		}
		
		if( e.getSource() == btnRefuteMapping ) {
			// TODO: the user has clicked to refute a mapping.
			int sel = cmbTopK.getSelectedIndex();
			
			Mapping selectedMapping = topK[sel];// build the cluster for the mapping.
			//Cluster<Mapping> cl = clusterMethod.getCluster(selectedMapping.getSourceKey(), selectedMapping.getTargetKey(), VisualizationType.CLASS_MATRIX);
			Cluster<Mapping> cl = topKClusters.get(sel);
			
			double eValue = Double.parseDouble( txtE.getText() );
			
			if( filteredCells == null ) { filteredCells = new boolean[disagreementMatrix.getRows()][disagreementMatrix.getColumns()]; }
			
			punishCluster( cl, eValue, feedbackMatrix );
			
			feedbackMatrix.set(selectedMapping.getSourceKey(), selectedMapping.getTargetKey(), new Mapping(selectedMapping.getEntity1(), selectedMapping.getEntity2(), 0.0d) );
			
			filteredCells[selectedMapping.getSourceKey()][selectedMapping.getTargetKey()] = true;
			
			feedbackMatcher.select();
			return;
		}
		
		
		if( e.getSource() == cmbTopK ) {
			// The user selected a mapping.  Select this mapping in all the panels.
			int sel = cmbTopK.getSelectedIndex();
			
			Mapping selectedMapping = topK[sel];
			
			
			broadcastEvent( new MatcherAnalyticsEvent( this ,  EventType.SELECT_MAPPING,  
					new Point(selectedMapping.getEntity1().getIndex(), selectedMapping.getEntity2().getIndex()) ));
			
		}
	}

	private void rewardCluster(Cluster<Mapping> cl, double eValue, SimilarityMatrix feedbackMatrix2) {
		
		for( Mapping m : cl ) {
			double sim = feedbackMatrix2.getSimilarity(m.getSourceKey(), m.getTargetKey());
			double newsim = (1 - eValue) * sim + eValue;
			feedbackMatrix2.get(m.getSourceKey(), m.getTargetKey()).setSimilarity(newsim);
			System.out.println("Rewarding " + m + ": " + sim + " updated to " + newsim );
			//filteredCells[m.getSourceKey()][m.getTargetKey()] = true;
		}
		
	}
	
	private void punishCluster(Cluster<Mapping> cl, double eValue, SimilarityMatrix feedbackMatrix2) {
		
		for( Mapping m : cl ) {
			double sim = feedbackMatrix2.getSimilarity(m.getSourceKey(), m.getTargetKey());
			double newsim = (1 - eValue) * sim;
			Mapping feedbackMapping = feedbackMatrix2.get(m.getSourceKey(), m.getTargetKey()); 
			if( feedbackMapping == null ) feedbackMatrix2.set(m.getSourceKey(), m.getTargetKey(), new Mapping(m.getEntity1(), m.getEntity2(), newsim));
			else feedbackMapping.setSimilarity(newsim);
			
			System.out.println("Punishing " + m + ": " + sim + " updated to " + newsim );
			//filteredCells[m.getSourceKey()][m.getTargetKey()] = true;
		}
		
	}

}
