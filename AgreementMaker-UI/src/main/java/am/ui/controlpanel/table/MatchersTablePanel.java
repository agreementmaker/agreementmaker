package am.ui.controlpanel.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.MatcherChangeListener;
import am.app.mappingEngine.MatchingTask;
import am.app.mappingEngine.MatchingTaskChangeEvent;
import am.app.mappingEngine.MatchingTaskChangeEvent.EventType;

public class MatchersTablePanel extends JPanel implements MatcherChangeListener {

	private static final long serialVersionUID = -5579979645883093290L;
	
	private MatchersTable table;

    public MatchersTablePanel() {
        super(new GridLayout(1,1));
        
        MatchersControlPanelTableModel mt = new MatchersControlPanelTableModel();
        table = new  MatchersTable(mt);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        //We need autoresize because when the table is fullscreen it's useful to use all the space
        //at the same time with autoresize set to ON there is not anymore horizontal scrolling
        //while we need it when the screen is smaller because we don't want columns to disappear
        //this is a famous problem that it is fixed with class MatcherTable
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click to modify values");
        
        //Fiddle with the column's cell editors/renderers: THRESHOLD
        JComboBox<String> comboTh = new JComboBox<String>(Utility.getPercentStringList());
        TableColumn thColumn = table.getColumnModel().getColumn(MatchersControlPanelTableModel.THRESHOLD);
        thColumn.setCellEditor(new DefaultCellEditor(comboTh));
        thColumn.setCellRenderer(renderer);
        
        //Fiddle with the column's cell editors/renderers: NUM RELATIONS FOR SOURCE AND TARGET
        JComboBox<String> comboNumRelations = new JComboBox<String>(Utility.getNumRelList());
        TableColumn SRelColumn = table.getColumnModel().getColumn(MatchersControlPanelTableModel.SRELATIONS);
        SRelColumn.setCellEditor(new DefaultCellEditor(comboNumRelations));
        SRelColumn.setCellRenderer(renderer);
        TableColumn TRelColumn = table.getColumnModel().getColumn(MatchersControlPanelTableModel.TRELATIONS);
        TRelColumn.setCellEditor(new DefaultCellEditor(comboNumRelations));
        TRelColumn.setCellRenderer(renderer);
        
        TableColumn inputColumn = table.getColumnModel().getColumn(MatchersControlPanelTableModel.INPUTMATCHERS);
        InputMatchersEditor mc = new InputMatchersEditor();
        inputColumn.setCellEditor(mc);
        inputColumn.setCellRenderer(renderer);
        
        ColorRenderer cr = new ColorRenderer(true);
        ColorEditor ce = new ColorEditor();
        TableColumn colorColumn = table.getColumnModel().getColumn(MatchersControlPanelTableModel.COLOR);
        colorColumn.setCellEditor(ce);
        colorColumn.setCellRenderer(cr);
                
        setOpaque(true); //content panes must be opaque
        //Set up column sizes.
        initColumnSizes(table);
        
        //Add the scroll pane to this panel.
        add(scrollPane);
        
        Core.getInstance().addMatcherChangeListener(this);
        
      //STATICALLY ADD THE FIRST MATCHER THAT IS THE USER MANUAL MATCHER
        //is important to add this here so that initColumns can assign the best width to columns
        //This matcher cannot be deleted
        
		/*try {
			UserManualMatcher userMatcher = (UserManualMatcher) MatcherFactory.getMatcherInstance(UserManualMatcher.class);
	        MatchingTask t = new MatchingTask(userMatcher, userMatcher.getParam(), 
					new MwbmSelection(), new DefaultSelectionParameters());
	        Core.getInstance().addMatchingTask(t);
		} catch (MatcherNotFoundException e) {
			e.printStackTrace();
		}*/
    }

    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    private void initColumnSizes(JTable table) {
    	  MatchersControlPanelTableModel model = (MatchersControlPanelTableModel)table.getModel();
          TableColumn column = null;
          Component comp = null;
          TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
          int width = 0;
          
          for (int i = 0; i < model.getColumnCount(); i++) {
              column = table.getColumnModel().getColumn(i);
              comp = headerRenderer.getTableCellRendererComponent(
                      null, column.getHeaderValue(),
                      false, false, 0, 0);
              width = 0;
          	if(i == MatchersControlPanelTableModel.INPUTMATCHERS || i == MatchersControlPanelTableModel.NAME){
          		width = 175;
          	}
          	else{
                  column = table.getColumnModel().getColumn(i);
                  comp = headerRenderer.getTableCellRendererComponent(
                          null, column.getHeaderValue(),
                          false, false, 0, 0);
                  width = comp.getPreferredSize().width;
          	}
          	column.setPreferredWidth(width);
          	column.setMinWidth(width);
          	column.setMaxWidth(width);
          }
      	/* THIS WAS MADE TO SET THE WIDTH AUTOMATICALLY BUT THERE WAS A BUG
      	 * 
          MyTableModel model = (MyTableModel)table.getModel();
          TableColumn column = null;
          Component comp = null;
          int headerWidth = 0;
          int cellWidth = 0;
          Object[] longValues = model.defaultValues;
          TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
          
          for (int i = 0; i < model.getColumnCount(); i++) {
              column = table.getColumnModel().getColumn(i);

              comp = headerRenderer.getTableCellRendererComponent(
                                   null, column.getHeaderValue(),
                                   false, false, 0, 0);
              headerWidth = comp.getPreferredSize().width;
              if(model.getRowCount() > 0) {//consider also the dimension of the elements rendered in the first row, and then take the max between header and row cells
              	comp = table.getDefaultRenderer(model.getColumnClass(i)).
                  getTableCellRendererComponent( table, longValues[i],
                      false, false, 0, i);
  				cellWidth = comp.getPreferredSize().width;

  				   System.out.println("Initializing width of column "
  				                      + i + ". "
  				                      + "headerWidth = " + headerWidth
  				                      + "; cellWidth = " + cellWidth);
  			
  				
  				
  				column.setPreferredWidth(Math.max(headerWidth, cellWidth));
  				column.setMinWidth(Math.max(headerWidth, cellWidth));
  				column.setMaxWidth(Math.max(headerWidth, cellWidth));
              }
              else {//Else just consider the header width
              	column.setPreferredWidth(headerWidth);
              	column.setMinWidth(headerWidth);
              	column.setMaxWidth(headerWidth);
              	
              }
          }
          */

    }

    
	public MatchersTable getTable() {
		return table;
	}
    
	public void addTask(MatchingTask task) {
		TableColumn inputColumn = table.getColumnModel().getColumn(MatchersControlPanelTableModel.INPUTMATCHERS);
    	InputMatchersEditor mc = (InputMatchersEditor)inputColumn.getCellEditor();
    	mc.addEditor(task.matchingAlgorithm);
    	final int tableSize = ((MatchersControlPanelTableModel)table.getModel()).data.size();
    	insertedRows(0, tableSize);
	}
	
    public void addMatcher(AbstractMatcher a) {
    	TableColumn inputColumn = table.getColumnModel().getColumn(MatchersControlPanelTableModel.INPUTMATCHERS);
    	InputMatchersEditor mc = (InputMatchersEditor)inputColumn.getCellEditor();
    	mc.addEditor(a);
    	insertedRows(a.getIndex(), a.getIndex());
    }
    
    public void insertedRows(int firstrow, int lastrow) {
    	((AbstractTableModel)table.getModel()).fireTableRowsInserted(firstrow, lastrow);
    }
    
    public void updatedRows(int firstrow, int lastrow) {
    	((AbstractTableModel)table.getModel()).fireTableRowsUpdated(firstrow, lastrow);
    }
    
    public void updatedCell(int row, int col) {
    	((AbstractTableModel)table.getModel()).fireTableCellUpdated(row, col);
    }
    
    public void deletedRows(int firstrow, int lastrow) {
    	((AbstractTableModel)table.getModel()).fireTableRowsDeleted(firstrow, lastrow);
    }
    
    public void dataChanged() {
    	((AbstractTableModel)table.getModel()).fireTableDataChanged();
    }
    
    public void removeTask(MatchingTask task) {
    	TableColumn inputColumn = table.getColumnModel().getColumn(MatchersControlPanelTableModel.INPUTMATCHERS);
    	InputMatchersEditor mc = (InputMatchersEditor)inputColumn.getCellEditor();
    	mc.removeEditor(task.matchingAlgorithm);
    	deletedRows(task.matchingAlgorithm.getIndex(), task.matchingAlgorithm.getIndex());
    }
 
    /**
     * Create the GUI and show it.  It s just a debugging method
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TableRenderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        MatchersTablePanel alignTable = new MatchersTablePanel();
        
        
        frame.setContentPane(alignTable);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

	@Override
	public void matcherChanged(MatchingTaskChangeEvent e) {
		if( e.getEvent() == EventType.MATCHER_ADDED ) {
			addTask(e.getTask());
			repaint();
		}
		
		if( e.getEvent() == EventType.MATCHER_REMOVED ) {
			removeTask(e.getTask());
			repaint();
		}
	}

}
