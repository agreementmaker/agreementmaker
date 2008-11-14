

package agreementMaker.userInterface.table;



import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import agreementMaker.Utility;
import agreementMaker.application.Core;
import agreementMaker.application.mappingEngine.AbstractMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.EqualsMatcher;
import agreementMaker.application.mappingEngine.fakeMatchers.UserManualMatcher;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

public class MatchersTablePanel extends JPanel {
    private boolean DEBUG = false;
    
    private MatchersTable table;

    public MatchersTablePanel() {
        super(new GridLayout(1,0));
        
        MyTableModel mt = new MyTableModel();
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
        JComboBox comboTh = new JComboBox(Utility.getPercentStringList());
        TableColumn thColumn = table.getColumnModel().getColumn(MyTableModel.THRESHOLD);
        thColumn.setCellEditor(new DefaultCellEditor(comboTh));
        thColumn.setCellRenderer(renderer);
        
        //Fiddle with the column's cell editors/renderers: NUM RELATIONS FOR SOURCE AND TARGET
        JComboBox comboNumRelations = new JComboBox(Utility.getNumRelList());
        TableColumn SRelColumn = table.getColumnModel().getColumn(MyTableModel.SRELATIONS);
        SRelColumn.setCellEditor(new DefaultCellEditor(comboNumRelations));
        SRelColumn.setCellRenderer(renderer);
        TableColumn TRelColumn = table.getColumnModel().getColumn(MyTableModel.TRELATIONS);
        TRelColumn.setCellEditor(new DefaultCellEditor(comboNumRelations));
        TRelColumn.setCellRenderer(renderer);
        
        TableColumn inputColumn = table.getColumnModel().getColumn(MyTableModel.INPUTMATCHERS);
        MyCellEditor mc = new MyCellEditor();
        inputColumn.setCellEditor(mc);
        inputColumn.setCellRenderer(renderer);
        
        //STATICALLY ADD THE FIRST MATCHER THAT IS THE USER MANUAL MATCHER
        //is important to add this here so that initColumns can assign the best width to columns
        //This matcher cannot be deleted
        UserManualMatcher userMatcher = new UserManualMatcher(0);
        addMatcher(userMatcher);
        
        setOpaque(true); //content panes must be opaque
        //Set up column sizes.
        initColumnSizes(table);
        
        //Add the scroll pane to this panel.
        add(scrollPane);
    }

    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    private void initColumnSizes(JTable table) {
        MyTableModel model = (MyTableModel)table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        Object[] longValues = model.longValues;
        TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;
            if(model.getRowCount() > 0) {//consider also the dimension of the elements rendered in the first row, and then take the max between header and row cells
            	comp = table.getDefaultRenderer(model.getColumnClass(i)).
                getTableCellRendererComponent(
                    table, longValues[i],
                    false, false, 0, i);
				cellWidth = comp.getPreferredSize().width;
				
				if (DEBUG) {
				   System.out.println("Initializing width of column "
				                      + i + ". "
				                      + "headerWidth = " + headerWidth
				                      + "; cellWidth = " + cellWidth);
				}
				
				column.setPreferredWidth(Math.max(headerWidth, cellWidth));
				column.setMinWidth(Math.max(headerWidth, cellWidth));
            }
            else {//Else just consider the header width
            	column.setPreferredWidth(headerWidth);
            }
        }
    }

    
	public MatchersTable getTable() {
		return table;
	}
    
    public void addMatcher(AbstractMatcher a) {
    	Core.getInstance().addMatcherInstance(a);
    	TableColumn inputColumn = table.getColumnModel().getColumn(MyTableModel.INPUTMATCHERS);
    	MyCellEditor mc = (MyCellEditor)inputColumn.getCellEditor();
    	mc.addEditor(a);
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
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


}
