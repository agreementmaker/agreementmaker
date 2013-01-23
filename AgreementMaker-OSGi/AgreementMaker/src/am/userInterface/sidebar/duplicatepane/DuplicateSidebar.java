package am.userInterface.sidebar.duplicatepane;

import java.awt.Component;

import javax.swing.JScrollPane;

public class DuplicateSidebar extends JScrollPane{
	
	private static final long serialVersionUID = 1L;

	

	private Component oldComponent;

	public DuplicateSidebar() {
		super();
		getVerticalScrollBar().setUnitIncrement(20);
	}
	
	

	public Component getOldComponent() {
		
		
		return oldComponent;
	}
	public void setOldComponent(Component oldComponent) {
		this.oldComponent = oldComponent;
	}
	
}