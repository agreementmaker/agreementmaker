/**
 * 
 */
package am.userInterface.matchingtask;

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
import am.app.mappingEngine.AbstractMatcher;
import am.app.mappingEngine.AbstractMatcher.MatcherCategory;
import am.userInterface.matchingtask.MatcherComboBox.ComboItem;
import am.utility.CanEnable;

/**
 * A combo box that displays the current list of matchers in the system.
 * It organizes the matchers by category and adds non-clickable separators
 * for each category.
 * 
 * @author Cosmin Stroe
 */
public class MatcherComboBox extends JComboBox<ComboItem<AbstractMatcher>> {

	private static final long serialVersionUID = 3318274165775084345L;
	
	protected static final String padLeft = "----------------- ";
	protected static final String padRight = " -----------------";
	
	/**
	 * 
	 */
	public MatcherComboBox() {
		super();
		
		// TODO: Remember the last selected item.
		int firstItem = populateComboBox();
		setSelectedIndex(firstItem);
		
		setRenderer(new ComboRenderer());
		addActionListener(new ComboListener(this));
	}

	/**
	 * Populates the combobox with Matching Algorithms, each separated
	 * by Matcher Category names.  The matcher categories are not enabled,
	 * so that they cannot be selected and show up as gray.
	 * 
	 * @return The index of the first matcher in the list.
	 */
	protected int populateComboBox() {
		
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		
		int longestCategory = getLongestCategory(fm);
		boolean firstItemFound = false;
		int firstItemIndex = -1;
		
		// get the list of matchers currently in the system
		List<AbstractMatcher> matchers = Core.getInstance().getMatchingAlgorithms();
		
		// populate the combobox by category
		for( MatcherCategory currentCategory : MatcherCategory.values() ) {
			
			final String categoryString = getCategoryString(currentCategory, longestCategory, fm);
			
			AbstractMatcher categoryStub = new AbstractMatcher() {
				private static final long serialVersionUID = 6056497255749446116L;
				@Override public String getName() { return categoryString; }
			};
			
			ComboItem<AbstractMatcher> categoryComboItem = new ComboItem<AbstractMatcher>( categoryStub, false);

			boolean categoryComboItemAdded = false;
			
			for( AbstractMatcher matcher : matchers ) {
				if( matcher.getCategory() == currentCategory ) {
					if( !categoryComboItemAdded ) {
						// only add the category string if the category isn't empty
						addItem(categoryComboItem);
						categoryComboItemAdded = true;
					}
					
					if( !firstItemFound ) { 
						firstItemIndex = getItemCount(); 
						firstItemFound = true; 
					}
					
					addItem(new ComboItem<AbstractMatcher>(matcher));
				}
			}
			
		}
		
		setMaximumRowCount(20);
		
		return firstItemIndex;
	}
	
	/**
	 * Gets the rendered length of the longest category string. 
	 * @param fm
	 * @return
	 */
	protected int getLongestCategory(FontMetrics fm) {
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
	protected String getCategoryString( MatcherCategory cat, int maxStringLength, FontMetrics fm ) {
		String left = padLeft;
		String right = padRight;
		while( fm.stringWidth(left + cat.name() + right ) < maxStringLength ) {
			left = "-" + left;
			right = right + "-";
		}
		return left + cat.name() + right;
	}
	
	// ***************************************************************8
	// Special inner classes for the combo box.
	
	/**
	 * This class displays disabled list entries as greyed out items.
	 * 
	 * List entries must implement CanEnable to work with this renderer.
	 * If they don't implement CanEnable, we assume they are enabled.
	 * 
	 * @see CanEnable
	 */
	class ComboRenderer extends JLabel implements ListCellRenderer<ComboItem<AbstractMatcher>> {

		private static final long serialVersionUID = 6823301981571072706L;

		public ComboRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
		}

		@Override
		public Component getListCellRendererComponent( JList<? extends ComboItem<AbstractMatcher>> list, 
				ComboItem<AbstractMatcher> value, int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			} 

			if( value instanceof CanEnable ) {
				if (! ((CanEnable)value).isEnabled()) {
					setBackground(list.getBackground());
					setForeground(UIManager.getColor("Label.disabledForeground"));
				}
			}
			
			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());
			return this;
		}

	}
	
	/**
	 * An action listener for a JComboBox which does not 
	 * allow disabled items to be selected in the combobox.
	 */
	class ComboListener implements ActionListener {
		JComboBox<ComboItem<AbstractMatcher>> combo;
		Object currentItem;

		ComboListener(JComboBox<ComboItem<AbstractMatcher>> combo) {
			this.combo  = combo;
			//combo.setSelectedIndex(0);
			currentItem = combo.getSelectedItem();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object tempItem = combo.getSelectedItem();
			if( tempItem instanceof CanEnable ) {
				if (! ((CanEnable)tempItem).isEnabled()) {
					combo.setSelectedItem(currentItem);
				} 
				else {
					currentItem = tempItem;
				}
			}
			else {
				currentItem = tempItem;
			}
		}
	}
	
	/**
	 * Wraps an object with the capability to be enabled.
	 * This is used as an object in the combobox.
	 */
	public class ComboItem<T> implements CanEnable {
		public T obj;
		public boolean isEnabled;

		public ComboItem(T obj, boolean isEnabled) {
			this.obj      = obj;
			this.isEnabled = isEnabled;
		}

		public ComboItem(T obj) {
			this(obj, true);
		}

		public boolean isEnabled() {
			return isEnabled;
		}

		public void setEnabled(boolean isEnable) {
			this.isEnabled = isEnable;
		}

		public String toString() {
			return obj.toString();
		}
	}
}

