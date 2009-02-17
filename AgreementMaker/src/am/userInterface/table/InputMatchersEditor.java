package am.userInterface.table;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import am.application.mappingEngine.AbstractMatcher;

public class InputMatchersEditor extends AbstractCellEditor implements TableCellEditor{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1514627921533089776L;
	
	private HashMap<Integer, DefaultCellEditor> rowEditors;
	
	public InputMatchersEditor() {
		super();
		rowEditors = new HashMap<Integer, DefaultCellEditor>();
	}
	
	public void removeEditor(AbstractMatcher a) {
		// TODO Auto-generated method stub
		rowEditors.remove(a.getIndex());
	}
	
	public void addEditor(AbstractMatcher a) {
		JComboBox jc;
		String s[];
		if(a!=null && a.getInputMatchers()!= null && a.getInputMatchers().size() > 0) {
			s = new String[a.getInputMatchers().size()];
			for(int i = 0; i < a.getInputMatchers().size(); i++) {
				s[i] = a.getInputMatchers().get(i).getName().getMatcherName();
			}
		}
		else {
			s = new String[1];
			s[0] = MyTableModel.NONE;
		}
		jc = new JComboBox(s);
		DefaultCellEditor dfc = new DefaultCellEditor(jc);
		rowEditors.put(a.getIndex(), dfc);
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		DefaultCellEditor dfc = rowEditors.get(row);
		return dfc.getTableCellEditorComponent(table, value, isSelected, row, column);
	}

	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return null;
	}

	


}
