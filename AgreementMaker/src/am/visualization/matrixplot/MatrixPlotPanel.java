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
import javax.swing.RepaintManager;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import am.Utility;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.SimilarityMatrix;
import am.visualization.MatcherAnalyticsEvent;
import am.visualization.MatcherAnalyticsEventDispatch;
import am.visualization.MatcherAnalyticsEventListener;
import am.visualization.MatcherAnalyticsEvent.EventType;

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
		plot = new MatrixPlot(mtx, this);
		plot.addMouseListener(this);
		plot.draw(false);
		
		lblName = new JLabel(a.getName().getMatcherName());
		lblSimilaritySelected = new JLabel("", JLabel.TRAILING);
		
		matcher = a;
		
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
			final int row = (clickPoint.x - rowMod) / squareSize;
			
			int colMod = clickPoint.y % squareSize;
			final int col = (clickPoint.y - colMod) / squareSize;
			
			plot.selectMapping(row, col);
	
			if( dispatch != null ) {
				// broadcast this event to the other MatrixPlot objects.
				Runnable fire = new Runnable() {
					public void run() {
						dispatch.broadcastEvent( new MatcherAnalyticsEvent( this,  EventType.SELECT_MAPPING,  new Point(row,col) ));
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
	//public boolean popupMenuActive() { return popupMenuActive; }

	
	/** These mouse event functions are not used yet. **/
	@Override public void mousePressed(MouseEvent e) {  }
	@Override public void mouseReleased(MouseEvent e) {	}
	@Override public void mouseEntered(MouseEvent e) {  }
	@Override public void mouseExited(MouseEvent e) {  }

	
	/** ACTION LISTENER **/
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if( e.getActionCommand() == "SET_REFERENCE" ) {
			// set this matcher as the reference
			//popupMenuActive = false;
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
	
	//@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {	}
	//@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { popupMenuActive = false; }
	//@Override public void popupMenuCanceled(PopupMenuEvent e) { popupMenuActive = false; }
	
	private void setReference(AbstractMatcher matcher2) {
		if( dispatch == null ) return;

		referenceMatcher = matcher2;
		
		switch( dispatch.getType() ) {
		case CLASS_MATRIX:
			plot.setReferenceAlignment( matcher2.getClassAlignmentSet() );
			break;
		case PROPERTIES_MATRIX:
			plot.setReferenceAlignment( matcher2.getPropertyAlignmentSet() );
			break;
		}
		
	}

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
		}
		
		if( e.type == EventType.MATRIX_UPDATED ) {
			if( e.payload == matcher || e.payload == referenceMatcher ) {
				if( e.payload == referenceMatcher ) setReference(referenceMatcher);
				plot.createImage(true);
				plot.repaint();				
			}
		}
		
	}
	
}
