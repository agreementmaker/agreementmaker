package am.visualization.matrixplot;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.similarityMatrix.SimilarityMatrix;
import am.evaluation.clustering.Cluster;
import am.evaluation.clustering.ClusterFactory;
import am.evaluation.clustering.ClusterFactory.ClusteringType;
import am.visualization.Gradient;
import am.visualization.MatcherAnalyticsEvent;
import am.visualization.MatcherAnalyticsEvent.EventType;
import am.visualization.MatcherAnalyticsEventDispatch;
import am.visualization.MatcherAnalyticsEventListener;

public class MatrixPlotPanel extends JPanel implements MouseListener, MatcherAnalyticsEventListener, ActionListener {

	private static final long serialVersionUID = -8579270363759208673L;

	private final MatcherAnalyticsEventDispatch dispatch;
	private final MatrixPlot plot;
	private final JLabel lblName;
	private final JLabel lblSimilaritySelected;
	private final AbstractMatcher matcher;
	
	private AbstractMatcher referenceMatcher = null; 
	
	//private boolean popupMenuActive = false;
	
	//private VisualizationType type;
	
	public MatrixPlotPanel(AbstractMatcher a, SimilarityMatrix mtx, MatcherAnalyticsEventDispatch d ) {
		super();

		dispatch = d;
		
		
		plot = new MatrixPlot(a, mtx, this);
		
		plot.addMouseListener(this);
		plot.draw(false);
		
		if( a != null ) { lblName = new JLabel(a.getName()); }
		else { lblName = new JLabel("--"); }
		
		lblSimilaritySelected = new JLabel("", JLabel.TRAILING);
		
		matcher = a;
		
		initThisPanel();		
		
	}
	
	public MatrixPlotPanel(AbstractMatcher a, MatcherAnalyticsEventDispatch d , MatrixPlot plot) {
		super();

		dispatch = d;
		
		
		this.plot = plot;
		
		plot.addMouseListener(this);
		plot.draw(false);
		
		if( a != null ) { lblName = new JLabel(a.getRegistryEntry().getMatcherName()); }
		else { lblName = new JLabel("--"); }
		
		lblSimilaritySelected = new JLabel("", JLabel.TRAILING);
		
		matcher = a;
		
		initThisPanel();		
		
	}
	
	public MatrixPlotPanel(AbstractMatcher a, SimilarityMatrix mtx, MatcherAnalyticsEventDispatch d, Gradient g ) {
		super();

		dispatch = d;
		plot = new MatrixPlot(a, mtx, this, g);
		plot.addMouseListener(this);
		plot.draw(false);
		
		if( a != null ) { lblName = new JLabel(a.getRegistryEntry().getMatcherName()); }
		else { lblName = new JLabel("--"); }
		
		lblSimilaritySelected = new JLabel("", JLabel.TRAILING);
		
		matcher = a;
		
		initThisPanel();		
		
	}
	
	public MatrixPlotPanel( String name, SimilarityMatrix mtx, MatcherAnalyticsEventDispatch d, Gradient g ) {
		super();
		
		dispatch = d;
		plot = new MatrixPlot( mtx, this, g);
		plot.addMouseListener(this);
		plot.draw(false);
		
		lblName = new JLabel(name);
		lblSimilaritySelected = new JLabel("", JLabel.TRAILING);
		
		matcher = null;
		
		
		
		initThisPanel();
	}

	private void initThisPanel() {
		JPanel plotPanel = createNewPlotPanel();
		
		JPanel pnlLabels = new JPanel();
		SpringLayout layLabels = new SpringLayout();
		pnlLabels.setLayout(layLabels);
		
		pnlLabels.add(lblName);
		pnlLabels.add(lblSimilaritySelected);
		
		layLabels.putConstraint(SpringLayout.WEST, lblName, 5, SpringLayout.WEST, pnlLabels);
		layLabels.putConstraint(SpringLayout.NORTH, lblName, 0, SpringLayout.NORTH, pnlLabels);
		
		layLabels.putConstraint(SpringLayout.EAST, lblSimilaritySelected, -5, SpringLayout.EAST, pnlLabels);
		layLabels.putConstraint(SpringLayout.NORTH, lblSimilaritySelected, 0, SpringLayout.NORTH, pnlLabels);
		
		layLabels.putConstraint(SpringLayout.SOUTH, pnlLabels, 0, SpringLayout.SOUTH, lblName);
		layLabels.putConstraint(SpringLayout.EAST, lblName, 0, SpringLayout.WEST, lblSimilaritySelected);
		
		GroupLayout layMain = new GroupLayout(this);
		
		layMain.setHorizontalGroup( layMain.createParallelGroup()
				.addComponent(pnlLabels)
				.addComponent(plotPanel)
		);
		
		layMain.setVerticalGroup( layMain.createSequentialGroup()
				.addComponent(pnlLabels)
				.addComponent(plotPanel)
		);
		
		this.setLayout(layMain);
	}
	
	
	private JPanel createNewPlotPanel() {
		
		JPanel newPlotPanel = new JPanel();
		
		GroupLayout layPlotPanel = new GroupLayout(newPlotPanel);
		
		layPlotPanel.setHorizontalGroup( layPlotPanel.createParallelGroup()
				.addComponent(plot)
		);
		
		layPlotPanel.setVerticalGroup( layPlotPanel.createSequentialGroup()
				.addComponent(plot)
		);
		
		newPlotPanel.setLayout(layPlotPanel);
		
		//plotPanel.setBorder( BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), a.getName().getMatcherName()));
		newPlotPanel.setBorder( BorderFactory.createLineBorder(Color.BLACK));
		return newPlotPanel;
	}
	
	/**
	 * Draw a line crosshair for a selected mapping.
	 */
		
	@Override
	public void mouseClicked(MouseEvent e) {
		// the user has clicked on some part of our canvas.
		
		// Step 1.  Figure out which mapping the user wants to highlight.
		if( e.getButton() == MouseEvent.BUTTON1 ) {
			Point clickPoint = e.getPoint();
			
			int squareSize = plot.getSquareSize();
			
			int rowMod = clickPoint.x % squareSize;
			int row = (clickPoint.x - rowMod) / squareSize;
			
			int colMod = clickPoint.y % squareSize;
			int col = (clickPoint.y - colMod) / squareSize;
			
			final int rowInverse = plot.inverseTranslateRow(row);
			final int colInverse = plot.inverseTranslateCol(col);
			
			plot.selectMapping(rowInverse,colInverse);
			lblSimilaritySelected.setText( Double.toString( Utility.roundDouble( plot.getMatrix().getSimilarity(rowInverse, colInverse), 4) ) );
			
			if( dispatch != null ) {
				// broadcast this event to the other MatrixPlot objects.
				final Object sourceObject = this;
				Runnable fire = new Runnable() {
					
					public void run() {
						dispatch.broadcastEvent( new MatcherAnalyticsEvent( sourceObject ,  EventType.SELECT_MAPPING,  new Point(rowInverse,colInverse) ));
					}
				};
				
				SwingUtilities.invokeLater(fire);
			}
		}
		
		if( e.getButton() == MouseEvent.BUTTON3 ) {  // Right Click
			MatrixPlotPopupMenu popup = new MatrixPlotPopupMenu(this);
			//popup.addPopupMenuListener(this);
			popup.setLightWeightPopupEnabled(false); // to avoid drawing over this menu
			popup.show(plot, e.getX(), e.getY());
			popup.repaint();
		}
	}

	public MatrixPlot getPlot() { return plot; }
	public AbstractMatcher getMatcher() { return matcher; }
	public SimilarityMatrix getMatrix() { return plot.getMatrix(); }
	//public boolean popupMenuActive() { return popupMenuActive; }
	public boolean getViewAlignmentOnly() { return plot.getViewAlignmentOnly(); }
	public boolean getViewReferenceAlignment() { return plot.getViewReferenceAlignment(); }

	
	/** These mouse event functions are not used yet. **/
	@Override public void mousePressed(MouseEvent e) {  }
	@Override public void mouseReleased(MouseEvent e) {	}
	@Override public void mouseEntered(MouseEvent e) {  }
	@Override public void mouseExited(MouseEvent e) {  }

	
	/** ACTION LISTENER **/
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( e.getActionCommand().equals( MatrixPlotPopupMenu.ActionCommands.SET_REFERENCE.name() ) ) {
			// set this matcher as the reference
			//popupMenuActive = false;
			if( matcher != null ) {
				setReference(matcher);
				
				if( dispatch != null ) {
					// broadcast this event to the other MatrixPlot objects.
					Runnable fire = new Runnable() {
						public void run() {
							dispatch.broadcastEvent( new MatcherAnalyticsEvent( this,  EventType.SET_REFERENCE,  matcher ));
						}
					};
					
					SwingUtilities.invokeLater(fire);
				}
			}
		}
		
		if( e.getActionCommand().equals( MatrixPlotPopupMenu.ActionCommands.VIEW_ALIGNMENT.name()  ) ) {
			// for this matcher, toggle the viewAlignmentOnly option.
			plot.setViewAlignmentOnly( !plot.getViewAlignmentOnly() );
		}
		
		if( e.getActionCommand().equals( MatrixPlotPopupMenu.ActionCommands.VIEW_REF_ALIGNMENT.name()  ) ) {
			// for this matcher, toggle the viewAlignmentOnly option.
			plot.setViewReferenceAlignment( !plot.getViewReferenceAlignment() );
		}
		
		if( e.getActionCommand().length() > MatrixPlotPopupMenu.ActionCommands.VIEW_CLUSTER.name().length() + 1 ) {
			String command = e.getActionCommand().substring(0, MatrixPlotPopupMenu.ActionCommands.VIEW_CLUSTER.name().length());
			String action = MatrixPlotPopupMenu.ActionCommands.VIEW_CLUSTER.name();
		
			if(		command.equals(action)  ) {
				// one of the VIEW_CLUSTER options was selected
				String whichCluster = e.getActionCommand().substring(MatrixPlotPopupMenu.ActionCommands.VIEW_CLUSTER.name().length() + 1);
				
				for(ClusteringType t : ClusterFactory.ClusteringType.values()) {
			    	if( whichCluster.equals(t.name()) && dispatch != null ) {
		    			
			    		final ClusteringType ct = t;
			    		
			    		Runnable buildClusters = new Runnable() {
			    			public void run() {
			    				dispatch.buildClusters(ct);
			    			}
			    		};
			    		
			    		SwingUtilities.invokeLater(buildClusters); // run in separate thread
		    			
			    		break;
			    	}
			    }
			}
		}
		
		if( e.getActionCommand() == MatrixPlotPopupMenu.ActionCommands.CLEAR_CLUSTER.name() ) {
			// clear the clusters.
			Runnable buildClusters = new Runnable() {
    			public void run() {
    				dispatch.broadcastEvent( new MatcherAnalyticsEvent( this,  EventType.CLEAR_CLUSTER,  null ));
    			}
    		};
    		
    		SwingUtilities.invokeLater(buildClusters); // run in separate thread
		}
		
		if( e.getActionCommand() == MatrixPlotPopupMenu.ActionCommands.REMOVE_PLOT.name() ) {
			final MatrixPlotPanel plotToRemove = this;
			Runnable removePlot = new Runnable() {
    			public void run() {
    				dispatch.broadcastEvent( new MatcherAnalyticsEvent( plotToRemove,  EventType.REMOVE_PLOT,  null ));
    			}
    		};
    		
    		SwingUtilities.invokeLater(removePlot); // run in separate thread
		}
		
		if( e.getActionCommand() == MatrixPlotPopupMenu.ActionCommands.SET_FEEDBACK.name() ) {
			if( dispatch != null ) {
				// broadcast this event to the other MatrixPlot objects.
				Runnable fire = new Runnable() {
					public void run() {
						dispatch.broadcastEvent( new MatcherAnalyticsEvent( this,  EventType.SET_FEEDBACK,  matcher ));
					}
				};
				
				SwingUtilities.invokeLater(fire);
			}
		}
		
		if( e.getActionCommand() == MatrixPlotPopupMenu.ActionCommands.VIEW_ORDERED_PLOT.name() ) {
			if( dispatch != null ) {
				// broadcast this event to the other MatrixPlot objects.
				final MatrixPlotPanel mpp = this;
				Runnable fire = new Runnable() {
					public void run() {
						dispatch.broadcastEvent( new MatcherAnalyticsEvent( this,  EventType.VIEW_ORDERED_PLOT,  mpp ));
					}
				};
				
				SwingUtilities.invokeLater(fire);
			}
		}
		
	}
	
	private void setReference(AbstractMatcher matcher2) {
		if( dispatch == null ) return;

		referenceMatcher = matcher2;
		
		switch( dispatch.getType() ) {
		case aligningClasses:
			plot.setReferenceAlignment( matcher2.getClassAlignmentSet() );
			break;
		case aligningProperties:
			plot.setReferenceAlignment( matcher2.getPropertyAlignmentSet() );
			break;
		}
		
	}

	public void setName(String newName) { lblName.setText(newName); }

	@SuppressWarnings("unchecked")
	@Override
	public void receiveEvent(MatcherAnalyticsEvent e) {
		if( e.getSource() == this ) return;
		
		if( e.type == EventType.SELECT_MAPPING ) {
			Point sel = (Point)e.payload;
			plot.selectMapping(sel.x, sel.y);
			lblSimilaritySelected.setText( Double.toString( Utility.roundDouble( plot.getMatrix().getSimilarity(sel.x, sel.y), 4) ) );
		}
		
		if( e.type == EventType.SET_REFERENCE ) {
			AbstractMatcher ref = (AbstractMatcher)e.payload;
			// we have set the reference alignment.
			setReference(ref);
			if( ref == matcher ) lblName.setText(lblName.getText() + " (reference)");
		}
		
		if( e.type == EventType.MATRIX_UPDATED ) {
			if( (matcher != null && e.payload == matcher) || e.payload == referenceMatcher ) {
				if( e.payload == referenceMatcher ) setReference(referenceMatcher);
				plot.createImage(true);
				plot.repaint();				
			}
		}
		
		if( e.type == EventType.DISPLAY_CLUSTER ) {
			Cluster<Mapping> c = (Cluster<Mapping>) e.payload;
			plot.setCluster(c);
		}
		
		if( e.type == EventType.CLEAR_CLUSTER ) {
			plot.setCluster(null);
		}
		
	}

	public void setCluster(Cluster<Mapping> c) { 
		plot.setCluster(c); 
	}
	
}
