package am.app.feedback.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
/**
 * We could have used the JTable Class instead of using this extension
 * but there is a famous bug: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4127936
 * and this is the simplest workaround for that bug
 * basically if we want our table to Auto_Resize and having an horizontal scrolling at the same time we have to override these two methods
 * Basically each column width is maximized to the max dimension available, but is minimize until the minWidth of the column
 * when the size of the window is smaller than the minWidth of columns then the horizontal scrollbar should appear.
 * ATTENTION this is not the best solution. Right now it's working but couldn't always be working if something change.
 *
 */
public class CandidatesTable extends JTable {
	
	//these editor and renderer are needed to display radiobutton in a table
	class RadioButtonRenderer implements TableCellRenderer {
		  public Component getTableCellRendererComponent(JTable table, Object value,
		                   boolean isSelected, boolean hasFocus, int row, int column) {
		    if (value==null) return null;
		    ((JRadioButton)value).setBackground(Color.WHITE);
		    ((JRadioButton)value).setOpaque(true);
		    return (Component)value;
		  }
		}
		 
		class RadioButtonEditor extends    DefaultCellEditor
		                        implements ItemListener {
		private static final long serialVersionUID = -2659989651230572367L;
		private JRadioButton button;
		 
		  public RadioButtonEditor(JCheckBox checkBox) {
		    super(checkBox);
		  }
		 
		  public Component getTableCellEditorComponent(JTable table, Object value,
		                   boolean isSelected, int row, int column) {
		    if (value==null) return null;
		    button = (JRadioButton)value;
		    button.addItemListener(this);
		    //button.setOpaque(true);
		    return (Component)value;
		  }
		 
		  public Object getCellEditorValue() {
		    button.removeItemListener(this);
		    //button.setOpaque(true);
		    return button;
		  }
		 
		  public void itemStateChanged(ItemEvent e) {
		    super.fireEditingStopped();
		  }
		}
		
		//this is needed to set a tooltip in the source and target cells with the value
		//this way if the string is longer than the column we can still read it
		public Component prepareRenderer(TableCellRenderer renderer,
                int rowIndex, int vColIndex) {
			Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
			if(vColIndex == CandidatesTableModel.C2_SOURCE || vColIndex == CandidatesTableModel.C3_TARGET){
				if (c instanceof JComponent) {
					JComponent jc = (JComponent)c;
					jc.setToolTipText((String)getValueAt(rowIndex, vColIndex));
					}
			}
			return c;
		}
	
	
	
	private static final long serialVersionUID = 3268258464854514945L;

	public CandidatesTable(CandidatesTableModel mt) {
		super(mt);
        getColumn(mt.getColumnName(CandidatesTableModel.C1_RADIO)).setCellRenderer(new RadioButtonRenderer());
        getColumn(mt.getColumnName(CandidatesTableModel.C1_RADIO)).setCellEditor(new RadioButtonEditor(new JCheckBox()));
	}
	
	// when the viewport shrinks below the preferred size, stop tracking the viewport width
	public boolean getScrollableTracksViewportWidth() {
	    if (autoResizeMode != AUTO_RESIZE_OFF) {
	        if (getParent() instanceof JViewport) {
	            return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
	        }
	    }
	    return false;
	}

	// when the viewport shrinks below the preferred size, return the minimum size
	// so that scrollbars will be shown
	public Dimension getPreferredSize() {
	    if (getParent() instanceof JViewport) {
	        if (((JViewport)getParent()).getWidth() < super.getPreferredSize().width) {
	            return getMinimumSize();
	        }
	    }

	    return super.getPreferredSize();
	}
	
	/*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    public void initColumnSizes() {
          CandidatesTableModel model = (CandidatesTableModel)this.getModel();
          TableColumn column = null;
          Component comp = null;
          int headerWidth = 0;
          int cellWidth = 0;
          Object[] longValues = model.defaultValues;
          TableCellRenderer headerRenderer = this.getTableHeader().getDefaultRenderer();
          
          for (int i = 0; i < model.getColumnCount(); i++) {
                  column = this.getColumnModel().getColumn(i);

                  comp = headerRenderer.getTableCellRendererComponent(
                                       null, column.getHeaderValue(),
                                       false, false, 0, 0);
                  headerWidth = comp.getPreferredSize().width;
                  if(model.getRowCount() > 0 && i != CandidatesTableModel.C1_RADIO) {//consider also the dimension of the elements rendered in the first row, and then take the max between header and row cells
                  	comp = this.getDefaultRenderer(model.getColumnClass(i)).
                      getTableCellRendererComponent( this, longValues[i],
                          false, false, 0, i);
      				cellWidth = comp.getPreferredSize().width;
      			
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
    }

	public int calculateRealWidth() {
		int width = 0;
		TableColumnModel model = getColumnModel();
		for(int i =0; i< model.getColumnCount(); i++){
			width += model.getColumn(i).getWidth();
		}
		return width;
	}
	
}
