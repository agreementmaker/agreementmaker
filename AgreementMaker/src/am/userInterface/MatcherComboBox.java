/**
 * 
 */
package am.userInterface;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import am.app.Core;
import am.app.mappingEngine.MatcherCategory;
import am.utility.CanEnable;

/**
 * @author cosmin
 *
 */
public class MatcherComboBox extends JComboBox {

	private static final long serialVersionUID = 3318274165775084345L;
	
	private static final String padLeft = "----------------- ";
	private static final String padRight = " -----------------";
	
	/**
	 * 
	 */
	public MatcherComboBox() {
		super();
		
		setSelectedIndex( populateComboBox() );
		setRenderer(new ComboRenderer());
		addActionListener(new ComboListener(this));
		
		setMaximumRowCount(20);
		
	}

	private int populateComboBox() {
		
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		
		int longestCategory = getLongestCategory(fm);
		boolean firstItemFound = false;
		int firstItemIndex = 0;
		
		/*for( MatcherCategory currentCategory : MatcherCategory.values() ) {
			
			boolean categoryComboItemAdded = false;
			ComboItem categoryComboItem = new ComboItem( getCategoryString(currentCategory, longestCategory, fm), false);
			
			for( MatchersRegistry currentRegistryEntry : MatchersRegistry.values() ) {
				if( currentRegistryEntry.isShown() && currentRegistryEntry.getCategory() == currentCategory ) {
					if( !categoryComboItemAdded ) {
						// only add the category string is the category isn't empty
						addItem(categoryComboItem);
						categoryComboItemAdded = true;
					}
					if( !firstItemFound ) { firstItemIndex = getItemCount(); firstItemFound = true; }
					addItem(new ComboItem(currentRegistryEntry.getMatcherName()));
				}
			}
			
		}*/
		
		//load the matcher names from the osgi register
		List<String> matcherNames=Core.getInstance().getFramework().getRegistry().getMatcherNames();
		for(String mn:matcherNames)
			addItem(new ComboItem(mn));
		
		
		return firstItemIndex;
	}
	
	/**
	 * Gets the rendered length of the longest category string. 
	 * @param fm
	 * @return
	 */
	private int getLongestCategory(FontMetrics fm) {
		int max = 0;
		for( MatcherCategory currentCategory : MatcherCategory.values()) {
			int currentSize = fm.stringWidth(padLeft + currentCategory.name() + padRight);
			if( currentSize > max ) max = currentSize;
		}
		return max;
	}

	/**
	 * Make the category string the correct length (maxStringLength) by padding with dashes.
	 * @param cat
	 * @param maxStringLength
	 * @param fm
	 * @return
	 */
	private String getCategoryString( MatcherCategory cat, int maxStringLength, FontMetrics fm ) {
		String left = padLeft;
		String right = padRight;
		while( fm.stringWidth(left + cat.name() + right ) < maxStringLength ) {
			left = "-" + left;
			right = right + "-";
		}
		return left + cat.name() + right;
	}
	
	/**
	 * @param aModel
	 *//*
	public MatcherComboBox(ComboBoxModel aModel) {
		super(aModel);
		// TODO Auto-generated constructor stub
	}

	*//**
	 * @param items
	 *//*
	public MatcherComboBox(Object[] items) {
		super(items);
		// TODO Auto-generated constructor stub
	}

	*//**
	 * @param items
	 *//*
	public MatcherComboBox(Vector<?> items) {
		super(items);
		// TODO Auto-generated constructor stub
	}*/

	
	
	
	// ***************************************************************8
	// Special inner classes for the combo box.
	
	
	class ComboRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = 6823301981571072706L;

		public ComboRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
		}

		@Override
		public Component getListCellRendererComponent( JList list, 
				Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			} 

			if (! ((CanEnable)value).isEnabled()) {
				setBackground(list.getBackground());
				setForeground(UIManager.getColor("Label.disabledForeground"));
			}
			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());
			return this;
		}

	}
	  
	class ComboListener implements ActionListener {
		JComboBox combo;
		Object currentItem;

		ComboListener(JComboBox combo) {
			this.combo  = combo;
			combo.setSelectedIndex(0);
			currentItem = combo.getSelectedItem();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object tempItem = combo.getSelectedItem();
			if (! ((CanEnable)tempItem).isEnabled()) {
				combo.setSelectedItem(currentItem);
			} else {
				currentItem = tempItem;
			}
		}
	}
	  
	class ComboItem implements CanEnable {
		Object  obj;
		boolean isEnable;

		ComboItem(Object obj,boolean isEnable) {
			this.obj      = obj;
			this.isEnable = isEnable;
		}

		ComboItem(Object obj) {
			this(obj, true);
		}

		public boolean isEnabled() {
			return isEnable;
		}

		public void setEnabled(boolean isEnable) {
			this.isEnable = isEnable;
		}

		public String toString() {
			return obj.toString();
		}
	}
}

