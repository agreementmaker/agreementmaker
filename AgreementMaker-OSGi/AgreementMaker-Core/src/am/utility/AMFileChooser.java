package am.utility;

import java.awt.Component;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

/**
 * This class extends the JFileChooser in order to:
 * 1) Disable the new folder button when showing an open dialog;
 * 2) Remove the ability to rename files.
 * 
 * @author cosmin
 *
 */
public class AMFileChooser extends JFileChooser {

	private static final long serialVersionUID = -4545466115923454129L;

	public static final int ALL_FILTER = 0;
	public static final int OWL_FILTER = 1;
	public static final int RDFS_FILTER = 2;
	public static final int XML_FILTER = 3;
	
	public AMFileChooser(File selectedFile, int selectedFilter ) {
		super( selectedFile );
		
		String[] owl = new String[] { "owl" };
		String[] rdf = new String[] { "rdf", "rdfs" };
		String[] xml = new String[] { "xml" };

		SimpleFileFilter owlFilter = new SimpleFileFilter(owl, "OWL Ontologies (*.owl)");
		SimpleFileFilter rdfsFilter = new SimpleFileFilter(rdf, "RDFS Ontologies (*.rdf, *.rdfs)");
		SimpleFileFilter xmlFilter = new SimpleFileFilter(xml, "XML Ontologies (*.xml)");
		
		addChoosableFileFilter(owlFilter);
		addChoosableFileFilter(rdfsFilter);
		addChoosableFileFilter(xmlFilter);

		FileFilter[] currentFilters = getChoosableFileFilters();
		if( selectedFilter >= 0 && selectedFilter < currentFilters.length ) {
			setFileFilter(currentFilters[selectedFilter]);
		} else {
			setFileFilter(currentFilters[0]);
		}
		
		MouseListener removedHandler = disableRenameOnClick(this);
		addDoubleClickHandler(this, removedHandler);
	}

	public int getFileFilterIndex() {
		FileFilter selectedFilter = getFileFilter();
		FileFilter[] filters = getChoosableFileFilters();
		for( int i = 0; i < filters.length; i++ ) {
			if( filters[i].equals(selectedFilter) ) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public int showOpenDialog(Component parent) throws HeadlessException {
		disableNewFolderButton(this);
		return super.showOpenDialog(parent);
	}
	
	private void addDoubleClickHandler(AMFileChooser amFileChooser, MouseListener removedHandler) {
		JList list = findFileList(amFileChooser);
		if( list!= null ) {
			AMFileChooserDoubleclickListener clickListener = new AMFileChooserDoubleclickListener(removedHandler);
			list.addMouseListener(clickListener);
		}
	}

	private static MouseListener disableRenameOnClick(AMFileChooser amFileChooser) {
		JList list = findFileList(amFileChooser);
		if (list!=null) {
			String listenerClassName;
			MouseListener[] listeners = list.getMouseListeners();
			for (int i=0;i< listeners.length;i++) {
				listenerClassName = listeners[i].getClass().getName();
				if(listenerClassName.indexOf("sun.swing.FilePane$Handler")!= -1) {    
					list.removeMouseListener(listeners[i]);
					return listeners[i];
					//break;
				}
			}
		}
		return null;
	}

	
	private static JList findFileList(Component comp)
	{
		if (comp instanceof JList) return (JList)comp;

		if (comp instanceof Container) {
			for (Component child : ((Container)comp).getComponents()) {
				JList list = findFileList(child);
				if (list != null) return list;
			}
		}
		return null;
	}

	/**
	 * Taken from:
	 * http://www.codecodex.com/wiki/Disable_the_JFileChooser%27s_%22New_folder%22_button
	 * @param c
	 */
	public static void disableNewFolderButton( Container c ) {
		int len = c.getComponentCount();
		for (int i = 0; i < len; i++) {
			Component comp = c.getComponent(i);
			if (comp instanceof JButton) {
				JButton b = (JButton)comp;
				Icon icon = b.getIcon();
				if (icon != null
						&& icon == UIManager.getIcon("FileChooser.newFolderIcon"))
					b.setEnabled(false);
			}
			else if (comp instanceof Container) {
				disableNewFolderButton((Container)comp);
			}
		}
	}

	/**
	 * MouseListener class that only handles doubleclick events and 
	 * passes them on to another mouse listener.
	 * 
	 * This class is used to suppress single click events in the file chooser.
	 * 
	 * @author cosmin
	 */
	private class AMFileChooserDoubleclickListener extends MouseAdapter {

		private MouseListener handler;

		public AMFileChooserDoubleclickListener(MouseListener receiver) {
			this.handler = receiver;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount() == 2 &&  SwingUtilities.isLeftMouseButton(e) ) {
				// user doubleclicked, pass it on
				handler.mouseClicked(e);
			}
		}
	}
	
	/*
	Java Swing, 2nd Edition
	By Marc Loy, Robert Eckstein, Dave Wood, James Elliott, Brian Cole
	ISBN: 0-596-00408-7
	Publisher: O'Reilly
	Taken from: http://www.java2s.com/Code/Java/Swing-JFC/seewhatittakestomakeoneofthesefilterswork.htm 
	*/
	private class SimpleFileFilter extends FileFilter {

		String[] extensions;

		String description;

		public SimpleFileFilter(String ext) {
			this(new String[] { ext }, null);
		}

		public SimpleFileFilter(String[] exts, String descr) {
			// Clone and lowercase the extensions
			extensions = new String[exts.length];
			for (int i = exts.length - 1; i >= 0; i--) {
				extensions[i] = exts[i].toLowerCase();
			}
			// Make sure we have a valid (if simplistic) description
			description = (descr == null ? exts[0] + " files" : descr);
		}

		public boolean accept(File f) {
			// We always allow directories, regardless of their extension
			if (f.isDirectory()) {
				return true;
			}

			// Ok, it's a regular file, so check the extension
			String name = f.getName().toLowerCase();
			for (int i = extensions.length - 1; i >= 0; i--) {
				if (name.endsWith(extensions[i])) {
					return true;
				}
			}
			return false;
		}

		public String getDescription() {
			return description;
		}
	}
}
