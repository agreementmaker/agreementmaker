package agreementMaker.userInterface;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import agreementMaker.GSM;


public class UIMenu implements ActionListener, ItemListener {
	
	// create 4 menus
	private JMenu fileMenu, editMenu, viewMenu, helpMenu;
	
	// menu items for helpMenu
	private JMenuItem howToUse, aboutItem;		

	private JMenuItem keyItem;

	// checkbox menu items and for view menu
	private JCheckBoxMenuItem mapByDefinition, mapByUser, mapByContext, mapByConsolidation;
	
	//creates a menu bar
	private JMenuBar myMenuBar;
	
	private UI ui;
	
	// menu items for edit menu
	private JMenuItem undo, redo;
	// menu itmes for fileMenu
	private JMenuItem xit, openSource, openTarget;

	public UIMenu(UI ui){
		this.ui=ui;
		init();
		
	}
	
	public void actionPerformed (ActionEvent ae){
		Object obj = ae.getSource();
		
		if (obj == xit){
			// confirm exit
			int n = JOptionPane.showConfirmDialog(null,"Are you sure you want to exit ?","Exit Agreement Maker",JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION)
			{
				System.out.println("Exiting the program.\n");
				System.exit(0);   
			}
			// if it is no, then do nothing		
		}else if (obj == keyItem){
			new Legend(ui);	
		}else if (obj == howToUse){
			displayOptionPane("How to use Agreement Maker","How to..");
		}else if (obj == openSource){
			openAndReadFilesForMapping(GSM.SOURCENODE);
		}else if (obj == openTarget){
			openAndReadFilesForMapping(GSM.TARGETNODE);
		}else if (obj == aboutItem){
			displayOptionPane("Agreement Maker 3.0\nAdvis research group\nThe University of Illinois at Chicago 2004","About Agreement Maker");
		}else if (obj == undo){
			displayOptionPane("Undo Clicked","Undo");
		}else if (obj == redo){
			displayOptionPane("Redo Clicked","Redo");
		}
	}
	
	public void displayOptionPane(String desc, String title){
			JOptionPane.showMessageDialog(null,desc,title, JOptionPane.PLAIN_MESSAGE);					
	}
	public void init(){
		//Creating the menu bar
		myMenuBar = new JMenuBar();
		ui.getUIFrame().setJMenuBar(myMenuBar);

		// building the file menu
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		myMenuBar.add(fileMenu);	

		//add openGFile menu item to file menu
		openSource = new JMenuItem("Open Source Ontology...",new ImageIcon("../images/fileImage.gif"));
		//openSource.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));                		
		//openSource.setMnemonic(KeyEvent.VK_O);
		openSource.addActionListener(this);
		fileMenu.add(openSource);
		
		//add openGFile menu item to file menu
		openTarget = new JMenuItem("Open Target Ontology...",new ImageIcon("../images/fileImage.gif"));
		//openTarget.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));                		
		//openTarget.setMnemonic(KeyEvent.VK_O);
		openTarget.addActionListener(this);
		fileMenu.add(openTarget);

		// add separator
		fileMenu.addSeparator();

		// add exit menu item to file menu
		xit = new JMenuItem("Exit", KeyEvent.VK_X);
		xit.addActionListener(this);
		fileMenu.add(xit);

		// Build edit menu in the menu bar.
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		myMenuBar.add(editMenu);

		// add undo menu item to edit menu
		undo = new JMenuItem("Undo", KeyEvent.VK_U);
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		undo.addActionListener(this);
		undo.setEnabled(false);
		editMenu.add(undo);

		// add redo menu item to edit menu
		redo = new JMenuItem("Redo", KeyEvent.VK_R);
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK)); 		
		redo.addActionListener(this);
		redo.setEnabled(false);
		editMenu.add(redo);

		// Build view menu in the menu bar.
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		myMenuBar.add(viewMenu);

		// add mapping by definition
		mapByDefinition = new JCheckBoxMenuItem("Mapping by Definition");
		mapByDefinition.setMnemonic(KeyEvent.VK_D);
		mapByDefinition.addItemListener(this);
		viewMenu.add(mapByDefinition);

		// add mapping by user
		mapByUser = new JCheckBoxMenuItem("Mapping by User");
		mapByUser.setMnemonic(KeyEvent.VK_U);		
		mapByUser.addItemListener(this);
		viewMenu.add(mapByUser);

		// add mapping by context
		mapByContext = new JCheckBoxMenuItem("Mapping by Context");
		mapByContext.setMnemonic(KeyEvent.VK_O);		
		mapByContext.addItemListener(this);
		viewMenu.add(mapByContext);

		// add mapping by consolidation
		mapByConsolidation = new JCheckBoxMenuItem("Mapping by Consolidation");
		mapByConsolidation.setMnemonic(KeyEvent.VK_C);		
		mapByConsolidation.addItemListener(this);
		viewMenu.add(mapByConsolidation);

		// add separator
		viewMenu.addSeparator();

		// add keyItem 
		keyItem = new JMenuItem("Key",KeyEvent.VK_K);
		keyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK)); 	                
		keyItem.addActionListener(this);
		viewMenu.add(keyItem);


		// Build help menu in the menu bar.
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		myMenuBar.add(helpMenu);

		// add menu item to help menu
		howToUse = new JMenuItem("How to use Agreement Maker?", new ImageIcon("images/helpImage.gif"));
		howToUse.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));                	
		howToUse.setMnemonic(KeyEvent.VK_H);
		howToUse.addActionListener(this);
		helpMenu.add(howToUse);

		// add about item to help menu
		aboutItem = new JMenuItem("About Agreement Maker", new ImageIcon("images/aboutImage.gif"));
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));                
		aboutItem.addActionListener(this);
		helpMenu.add(aboutItem);

	}
	public void itemStateChanged (ItemEvent e){
		Object obj = e.getItemSelectable();
		if (obj == mapByUser)
		{
			if (e.getStateChange() == ItemEvent.DESELECTED)
			{
				ui.getPanelControlPanel().mappingByUserCheckBoxSetSelected(false);   
				mapByUser.setSelected(false);
				ui.getCanvas().setMapByUser(false);
			}
			else
			{
				ui.getPanelControlPanel().mappingByUserCheckBoxSetSelected(true);   
				mapByUser.setSelected(true);				
				ui.getCanvas().setMapByUser(true);
			}
		}
		//else if ((obj == mappingByContext) || (obj == mapByContext))
		else if (obj == mapByContext)
		{
			if (e.getStateChange() == ItemEvent.DESELECTED)
			{
				ui.getPanelControlPanel().mappingByContextCheckBoxSetSelected(false);   
				mapByContext.setSelected(false);
				ui.getCanvas().deselectedContextMapping();
			}
			else
			{
				ui.getPanelControlPanel().mappingByContextCheckBoxSetSelected(true);   
				mapByContext.setSelected(true);				
				// perform mapping by context by calling method in myCanvas class
				ui.getCanvas().mapByContext();

			}
		}
		else if (obj == mapByDefinition) //JMENUcheckbox
		{
			if (e.getStateChange() == ItemEvent.DESELECTED)
			{
				//displayOptionPane("mappingBydefn","mappingBydefn");
				ui.getPanelControlPanel().mappingByDefinitionCheckBoxSetSelected(false);   
				mapByDefinition.setSelected(false);	
				ui.getCanvas().deselectedDefnMapping();
				//displayOptionPane("Mapping by Definitionss DESELECTED","Mapping by Definition");
			}
			else
			{
				ui.getPanelControlPanel().mappingByDefinitionCheckBoxSetSelected(true);   
				mapByDefinition.setSelected(true);	
				ui.getCanvas().selectedDefnMapping();
				//displayOptionPane("Mapping by Definition SELECTED Muhammad","Mapping by Definition");
			}
		}
		else if (obj == mapByConsolidation)
		{	
			if (e.getStateChange() == ItemEvent.DESELECTED)
			{
				ui.getPanelControlPanel().mappingByConsolidationCheckBoxSetSelected(false);   
				mapByConsolidation.setSelected(false);	                                        
				//displayOptionPane("Mapping by Consolidation DESELECTEDs","Mapping by Consolidation");
			}
			else
			{
				ui.getPanelControlPanel().mappingByConsolidationCheckBoxSetSelected(true);   
				mapByConsolidation.setSelected(true);	     				
				//displayOptionPane("Mapping by Consolidation SELECTEDs","Mapping by Consolidation");
			}
		}

	}
	
	public void mapByConsolidationSetSelected(boolean consolidationMapping){
		mapByConsolidation.setSelected(consolidationMapping);
	}
	
	public void mapByContextSetSelected(boolean contextMapping){
		mapByContext.setSelected(contextMapping);	
	}
	
	public void mapByDefinitionSetSelected(boolean definitionMapping){
		mapByDefinition.setSelected(definitionMapping);
	}
	
	public void mapByUserSetSelected(boolean userMapping){
		mapByUser.setSelected(userMapping);
	}
	/*******************************************************************************************/
	/**
	 * This method reads the XML or OWL files and creates trees for mapping
	 */	
	 public void openAndReadFilesForMapping(int fileType){
		new OpenOntologyFileDialog(fileType, ui);
	 }
	
}
