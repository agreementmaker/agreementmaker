package am.userInterface.table;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.JViewport;
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
public class MatchersTable extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3268258464854514945L;

	public MatchersTable(MyTableModel mt) {
		super(mt);
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
	
}
