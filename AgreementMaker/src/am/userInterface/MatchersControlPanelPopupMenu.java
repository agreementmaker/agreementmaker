package am.userInterface;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.Alignment;
import am.app.mappingEngine.Mapping;
import am.app.mappingEngine.MatcherChangeEvent;
import am.app.mappingEngine.SimilarityMatrix;
import am.userInterface.canvas2.Canvas2;
import am.userInterface.canvas2.graphical.MappingData;
import am.userInterface.canvas2.utility.Canvas2Edge;
import am.userInterface.canvas2.utility.CanvasGraph;
import am.userInterface.table.MatchersControlPanelTableModel;

/**
 * A popup menu for the MatchersControlPanel table.
 * 
 * Hide Class Mappings > 
 * 		... (matchers)
 * 		------------------
 * 		By Similarity Range
 * 		By Mapping Type
 * 		
 * Hide Property Mappings > 
 * 		... (matchers)
 * 		------------------
 * 		By Similarity Range
 * 		By Mapping Type 
 * 
 * -----------------------
 * Delete Hidden Mappings
 * 
 * @author cosmin
 *
 */
public class MatchersControlPanelPopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = -7225837638075055109L;

	private final MatchersControlPanel mcp;
	
	public MatchersControlPanelPopupMenu( MatchersControlPanel cp ) {
		this.mcp = cp;
		
		
		{ // Hide Class Mappings  (brackets to avoid cut and paste errors)
			
			JMenu mClassMappings = createHideClassMappingsMenu();
			mClassMappings.addSeparator();
			
			JMenuItem miClassSimRange = new JMenuItem("By Similarity Range");
			miClassSimRange.addActionListener(this);
			miClassSimRange.setActionCommand("FILTER_CLASS_BY_SIMRANGE");
			mClassMappings.add(miClassSimRange);
			
			JMenuItem miClassMappingType = new JMenuItem("By Mapping Type");
			miClassMappingType.addActionListener(this);
			miClassMappingType.setActionCommand("FILTER_CLASS_BY_MAPPINGTYPE");
			mClassMappings.add(miClassMappingType);
			
			add(mClassMappings);
		}
		
		{ // Hide Property Mappings  (brackets to avoid cut and paste errors)
			
			JMenu mPropertyMappings = createHidePropertyMappingsMenu();
			mPropertyMappings.addSeparator();
			
			JMenuItem miPropertySimRange = new JMenuItem("By Similarity Range");
			miPropertySimRange.addActionListener(this);
			miPropertySimRange.setActionCommand("FILTER_PROPERTY_BY_SIMRANGE");
			mPropertyMappings.add(miPropertySimRange);
			
			JMenuItem miPropertyMappingType = new JMenuItem("By Mapping Type");
			miPropertyMappingType.addActionListener(this);
			miPropertyMappingType.setActionCommand("FILTER_PROPERTY_BY_MAPPINGTYPE");
			mPropertyMappings.add(miPropertyMappingType);
			
			add(mPropertyMappings);
		}
		
		{ // Compare similarity matrix
			JMenu mCompareMatrix = createCompareMatrixMenu();
			add(mCompareMatrix);
		}
		
		addSeparator();
		
		JMenuItem miDeleteHiddenClassMappings = new JMenuItem("Delete Hidden Class Mappings");
		miDeleteHiddenClassMappings.addActionListener(this);
		miDeleteHiddenClassMappings.setActionCommand("DELETE_HIDDEN_CLASS_MAPPINGS");
		add(miDeleteHiddenClassMappings);
		
		JMenuItem miDeleteHiddenPropertyMappings = new JMenuItem("Delete Hidden Property Mappings");
		miDeleteHiddenPropertyMappings.addActionListener(this);
		miDeleteHiddenPropertyMappings.setActionCommand("DELETE_HIDDEN_PROPERTY_MAPPINGS");
		add(miDeleteHiddenPropertyMappings);
	}

	private JMenu createHideClassMappingsMenu() {
		
		JMenu mClassMappings = new JMenu("Hide Class Mappings");
		
		MatchersControlPanelTableModel tableModel = 
			(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
		
		AbstractAction hideClassMappingsFromMatcher = new AbstractAction() {
			
			private static final long serialVersionUID = -1014885910948614015L;

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Canvas2 canvas = (Canvas2) Core.getUI().getCanvas();
				
				// get the matcher by which we are filtering (was selected by the menu)
				int id = Integer.parseInt(e.getActionCommand());
				AbstractMatcher filterMatcher = Core.getInstance().getMatcherByID(id);
				
				if( filterMatcher == null ) return;
				
				// get the graphical representations
				//Alignment<Mapping> filterAlignment = filterMatcher.getClassAlignmentSet();
				CanvasGraph filterGraph = canvas.getMatcherGraph( filterMatcher.getID() );
				
				
				
				// filter out the matchers selected in the table by the filter matcher
				int[] selectedTableRows = mcp.getTablePanel().getTable().getSelectedRows();
				MatchersControlPanelTableModel tableModel = 
					(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
				
				for( int currentRow : selectedTableRows ) {
					AbstractMatcher matcherToFilter = tableModel.getData().get(currentRow);
					//Alignment<Mapping> classAlignment = matcherToFilter.getClassAlignmentSet();
					
					CanvasGraph matcherToFilterGraph = canvas.getMatcherGraph( matcherToFilter.getID() );
					
					for( Canvas2Edge filterEdge : filterGraph.getEdges() ) {
						for( Canvas2Edge matcherEdge : matcherToFilterGraph.getEdges() ) {
							if( filterEdge.getObject() instanceof MappingData && 
									matcherEdge.getObject() instanceof MappingData ) {
								MappingData filterData = (MappingData) filterEdge.getObject();
								MappingData matcherData = (MappingData) matcherEdge.getObject();
								
								if( filterData.alignment.getEntity1().isClass() &&
									matcherData.alignment.getEntity1().isClass() &&
									filterData.alignment.equals(matcherData.alignment) ) {
									// hide this mapping.
									matcherData.visible = false;
								}
							}
						}
					}

				}
				
				canvas.repaint();
			}
		};
		
		for( AbstractMatcher a : tableModel.getData() ) {
			JMenuItem miMatcher = new JMenuItem(a.getName());
			miMatcher.setAction(hideClassMappingsFromMatcher);
			miMatcher.setActionCommand(Integer.toString(a.getID()));
			miMatcher.setText(a.getName());
			mClassMappings.add(miMatcher);
		}
		
		return mClassMappings;
	}
	
	private JMenu createHidePropertyMappingsMenu() {
		
		JMenu mPropertyMappings = new JMenu("Hide Property Mappings");
		
		MatchersControlPanelTableModel tableModel = 
			(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
		
		AbstractAction hidePropertyMappingsFromMatcher = new AbstractAction() {

			private static final long serialVersionUID = 6609743099179900966L;

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Canvas2 canvas = (Canvas2) Core.getUI().getCanvas();
				
				// get the matcher by which we are filtering (was selected by the menu)
				int id = Integer.parseInt(e.getActionCommand());
				AbstractMatcher filterMatcher = Core.getInstance().getMatcherByID(id);
				
				if( filterMatcher == null ) return;
				
				// get the graphical representations
				CanvasGraph filterGraph = canvas.getMatcherGraph( filterMatcher.getID() );
				
				// filter out the matchers selected in the table by the filter matcher
				int[] selectedTableRows = mcp.getTablePanel().getTable().getSelectedRows();
				MatchersControlPanelTableModel tableModel = 
					(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
				
				for( int currentRow : selectedTableRows ) {
					AbstractMatcher matcherToFilter = tableModel.getData().get(currentRow);
					
					CanvasGraph matcherToFilterGraph = canvas.getMatcherGraph( matcherToFilter.getID() );
					
					for( Canvas2Edge filterEdge : filterGraph.getEdges() ) {
						for( Canvas2Edge matcherEdge : matcherToFilterGraph.getEdges() ) {
							if( filterEdge.getObject() instanceof MappingData && 
									matcherEdge.getObject() instanceof MappingData ) {
								MappingData filterData = (MappingData) filterEdge.getObject();
								MappingData matcherData = (MappingData) matcherEdge.getObject();
								
								if( filterData.alignment.getEntity1().isProp() &&
									matcherData.alignment.getEntity1().isProp() &&
									filterData.alignment.equals(matcherData.alignment) ) {
									// hide this mapping.
									matcherData.visible = false;
								}
							}
						}
					}

				}
				
				canvas.repaint();
			}
		};
		
		for( AbstractMatcher a : tableModel.getData() ) {
			JMenuItem miMatcher = new JMenuItem(a.getName());
			miMatcher.setActionCommand(Integer.toString(a.getID()));
			miMatcher.setAction(hidePropertyMappingsFromMatcher);
			mPropertyMappings.add(miMatcher);
		}
		
		return mPropertyMappings;
	}
	

	private JMenu createCompareMatrixMenu() {
		
		JMenu mClassMappings = new JMenu("Compare Similarity Matrix");
		
		MatchersControlPanelTableModel tableModel = 
			(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
		
		AbstractAction hideClassMappingsFromMatcher = new AbstractAction() {
			
			private static final long serialVersionUID = -1014885910948614015L;

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Canvas2 canvas = (Canvas2) Core.getUI().getCanvas();
				
				// get the matcher by which we are filtering (was selected by the menu)
				int id = Integer.parseInt(e.getActionCommand());
				AbstractMatcher compareMatcher = Core.getInstance().getMatcherByID(id);
				
				if( compareMatcher == null ) return;
				
				// filter out the matchers selected in the table by the filter matcher
				int[] selectedTableRows = mcp.getTablePanel().getTable().getSelectedRows();
				MatchersControlPanelTableModel tableModel = 
					(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
				
				if( selectedTableRows.length > 0 ) {
					AbstractMatcher referenceMatcher = tableModel.getData().get(selectedTableRows[0]);
					
					SimilarityMatrix compareMatrix = compareMatcher.getClassesMatrix();
					SimilarityMatrix referenceMatrix = referenceMatcher.getClassesMatrix();
					
					for( int row = 0; row < compareMatrix.getRows(); row++ ) {
						for( int col = 0; col < compareMatrix.getColumns(); col++ ) {
							Mapping compareMapping = compareMatrix.get(row, col);
							Mapping refMapping = referenceMatrix.get(row, col);
							
							if( (compareMapping == null && refMapping != null) ||
								(refMapping == null && compareMapping != null) ||
								( compareMapping != null && refMapping != null && !compareMatrix.get(row, col).equals(referenceMatrix.get(row,col)) ) ) {
								
								System.out.println("Difference at: (" + row + ", " + col + ")" );
								System.out.println(compareMatcher.getName() + ": " + compareMatrix.get(row, col));
								System.out.println(referenceMatcher.getName() + ": " + referenceMatrix.get(row, col));
							}
						}
					}
					
				}
			}
		};
		
		for( AbstractMatcher a : tableModel.getData() ) {
			JMenuItem miMatcher = new JMenuItem(a.getName());
			miMatcher.setAction(hideClassMappingsFromMatcher);
			miMatcher.setActionCommand(Integer.toString(a.getID()));
			miMatcher.setText(a.getName());
			mClassMappings.add(miMatcher);
		}
		
		return mClassMappings;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if( e.getActionCommand().equals("FILTER_CLASS_BY_SIMRANGE")) {
			
			// get the range bounds.
			float lowerBound = 0.0f; // lower bound of range
			while(true) {
				try {
					String lowerBoundString = 
						JOptionPane.showInputDialog("Please type in lower bound as a decimal from [0.0,1.0]:","0.0");
					if( lowerBoundString == null ) return;
					lowerBound = Float.parseFloat(lowerBoundString);
				} catch (NumberFormatException ex1) {
					continue;
				} catch ( HeadlessException ex2 ) {
					return;
				}
				if( lowerBound < 0.0f || lowerBound > 1.0f ) continue;
				break;
			}
			
			float upperBound = 1.0f; // upper bound of range
			while(true) {
				try {
					String upperBoundString = 
						JOptionPane.showInputDialog("Please type in lower bound as a decimal from [0.0,1.0]:","0.0");
					if( upperBoundString == null ) return;
					upperBound = Float.parseFloat(upperBoundString);
				} catch (NumberFormatException ex1) {
					continue;
				} catch ( HeadlessException ex2 ) {
					return;
				}
				if( upperBound < 0.0f || upperBound > 1.0f ) continue;
				break;
			}
				
			if( lowerBound > upperBound ) {
				Utility.displayErrorPane("The lower bound is greater than the upper bound.", "Invalid range");
				return;
			}
			
			Canvas2 canvas = (Canvas2) Core.getUI().getCanvas();
			
			int[] selectedTableRows = mcp.getTablePanel().getTable().getSelectedRows();
			MatchersControlPanelTableModel tableModel = 
				(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
			
			// filter out the mappings from the selected matchers 
			for( int currentRow : selectedTableRows ) {
				AbstractMatcher matcherToFilter = tableModel.getData().get(currentRow);
				
				CanvasGraph matcherToFilterGraph = canvas.getMatcherGraph( matcherToFilter.getID() );
				
				for( Canvas2Edge matcherEdge : matcherToFilterGraph.getEdges() ) {
					if( matcherEdge.getObject() instanceof MappingData ) {
						MappingData matcherData = (MappingData) matcherEdge.getObject();
						
						if( matcherData.alignment.getEntity1().isClass() &&
							matcherData.alignment.getSimilarity() >= lowerBound &&
							matcherData.alignment.getSimilarity() <= upperBound ) {
							// hide this mapping.
							matcherData.visible = false;
						}
					}
				}

			}
			
			canvas.repaint();
			
		}
		
		
		if( e.getActionCommand().equals("FILTER_PROPERTY_BY_SIMRANGE")) {
			
			// get the range bounds.
			float lowerBound = 0.0f; // lower bound of range
			while(true) {
				try {
					String lowerBoundString = 
						JOptionPane.showInputDialog("Please type in lower bound as a decimal from [0.0,1.0]:","0.0");
					if( lowerBoundString == null ) return;
					lowerBound = Float.parseFloat(lowerBoundString);
				} catch (NumberFormatException ex1) {
					continue;
				} catch ( HeadlessException ex2 ) {
					return;
				}
				if( lowerBound < 0.0f || lowerBound > 1.0f ) continue;
				break;
			}
			
			float upperBound = 1.0f; // upper bound of range
			while(true) {
				try {
					String upperBoundString = 
						JOptionPane.showInputDialog("Please type in lower bound as a decimal from [0.0,1.0]:","0.0");
					if( upperBoundString == null ) return;
					upperBound = Float.parseFloat(upperBoundString);
				} catch (NumberFormatException ex1) {
					continue;
				} catch ( HeadlessException ex2 ) {
					return;
				}
				if( upperBound < 0.0f || upperBound > 1.0f ) continue;
				break;
			}
				
			if( lowerBound > upperBound ) {
				Utility.displayErrorPane("The lower bound is greater than the upper bound.", "Invalid range");
				return;
			}
			
			Canvas2 canvas = (Canvas2) Core.getUI().getCanvas();
			
			int[] selectedTableRows = mcp.getTablePanel().getTable().getSelectedRows();
			MatchersControlPanelTableModel tableModel = 
				(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
			
			// filter out the mappings from the selected matchers 
			for( int currentRow : selectedTableRows ) {
				AbstractMatcher matcherToFilter = tableModel.getData().get(currentRow);
				
				CanvasGraph matcherToFilterGraph = canvas.getMatcherGraph( matcherToFilter.getID() );
				
				for( Canvas2Edge matcherEdge : matcherToFilterGraph.getEdges() ) {
					if( matcherEdge.getObject() instanceof MappingData ) {
						MappingData matcherData = (MappingData) matcherEdge.getObject();
						
						if( matcherData.alignment.getEntity1().isProp() &&
							matcherData.alignment.getSimilarity() >= lowerBound &&
							matcherData.alignment.getSimilarity() <= upperBound ) {
							// hide this mapping.
							matcherData.visible = false;
						}
					}
				}

			}
			
			canvas.repaint();
			
		}
		
		
		if( e.getActionCommand().equals("FILTER_CLASS_BY_MAPPINGTYPE")) {
			Utility.displayErrorPane("Not yet implemented.", "Not implemented.");
		}
		
		if( e.getActionCommand().equals("FILTER_PROPERTY_BY_MAPPINGTYPE")) {
			Utility.displayErrorPane("Not yet implemented.", "Not implemented.");
		}
		
		if( e.getActionCommand().equals("DELETE_HIDDEN_CLASS_MAPPINGS") ) {
			Canvas2 canvas = (Canvas2) Core.getUI().getCanvas();
			
			int[] selectedTableRows = mcp.getTablePanel().getTable().getSelectedRows();
			MatchersControlPanelTableModel tableModel = 
				(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
			
			// filter out the mappings from the selected matchers 
			for( int currentRow : selectedTableRows ) {
				AbstractMatcher matcherToFilter = tableModel.getData().get(currentRow);
				Alignment<Mapping> classAlignment = matcherToFilter.getClassAlignmentSet();
				
				CanvasGraph matcherToFilterGraph = canvas.getMatcherGraph( matcherToFilter.getID() );
				
				for( Canvas2Edge matcherEdge : matcherToFilterGraph.getEdges() ) {
					if( matcherEdge.getObject() instanceof MappingData ) {
						MappingData matcherData = (MappingData) matcherEdge.getObject();
						
						if( matcherData.alignment.getEntity1().isClass() &&
							matcherData.visible == false ) {
							// delete this mapping from the alignment.
							classAlignment.remove(matcherData.alignment);
						}
					}
				}

				MatcherChangeEvent evt = new MatcherChangeEvent(matcherToFilter, 
						MatcherChangeEvent.EventType.MATCHER_ALIGNMENTSET_UPDATED, matcherToFilter.getID() );
				
				Core.getInstance().fireEvent(evt);
				
			}
		}
		
		if( e.getActionCommand().equals("DELETE_HIDDEN_PROPERTY_MAPPINGS") ) {
			Canvas2 canvas = (Canvas2) Core.getUI().getCanvas();
			
			int[] selectedTableRows = mcp.getTablePanel().getTable().getSelectedRows();
			MatchersControlPanelTableModel tableModel = 
				(MatchersControlPanelTableModel) mcp.getTablePanel().getTable().getModel();
			
			// filter out the mappings from the selected matchers 
			for( int currentRow : selectedTableRows ) {
				AbstractMatcher matcherToFilter = tableModel.getData().get(currentRow);
				Alignment<Mapping> propAlignment = matcherToFilter.getPropertyAlignmentSet();
				
				CanvasGraph matcherToFilterGraph = canvas.getMatcherGraph( matcherToFilter.getID() );
				
				for( Canvas2Edge matcherEdge : matcherToFilterGraph.getEdges() ) {
					if( matcherEdge.getObject() instanceof MappingData ) {
						MappingData matcherData = (MappingData) matcherEdge.getObject();
						
						if( matcherData.alignment.getEntity1().isProp() &&
							matcherData.visible == false ) {
							// delete this mapping from the alignment.
							propAlignment.remove(matcherData.alignment);
						}
					}
				}

				MatcherChangeEvent evt = new MatcherChangeEvent(matcherToFilter, 
						MatcherChangeEvent.EventType.MATCHER_ALIGNMENTSET_UPDATED, matcherToFilter.getID() );
				
				Core.getInstance().fireEvent(evt);
				
			}
		}
	}
}
