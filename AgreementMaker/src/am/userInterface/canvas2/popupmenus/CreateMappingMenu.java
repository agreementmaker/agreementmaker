package am.userInterface.canvas2.popupmenus;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import am.userInterface.canvas2.utility.Canvas2Layout;

public class CreateMappingMenu extends JPopupMenu {

	private static final long serialVersionUID = -4725875892839916016L;

	public CreateMappingMenu( Canvas2Layout layout ) {
		super();
		
		// menu layout (copied from old canvas) - Jan 29, 2010 Cosmin
		//  1. Create Standard Alignment (item)
		//  2. Create Manual Alignment (menu)
		//  	3. Equivalence
		//      4. Subset
		//      5. Subset Complete
		//      6. Superset
		//      7. Superset Complete
		//      8. Comparitive Exact
		//      9. Comparitive Subset
		//     10. Comparitive Superset
		//     11. Other

		JMenuItem miStandard = new JMenuItem("Create Standard Mapping");
			      miStandard.setActionCommand("CREATE_DEFAULT");
			      
		JMenu     mManual = new JMenu("Create Manual Mapping");
		JMenuItem miEquivalence = new JMenuItem("Equivalence");
		          miEquivalence.setActionCommand("CREATE_EQUIVALENCE");
		          
		JMenuItem miSubset = new JMenuItem("Subset");
		          miSubset.setActionCommand("CREATE_SUBSET");
		          
		JMenuItem miSubsetComplete = new JMenuItem("Subset Complete");
				  miSubsetComplete.setActionCommand("CREATE_SUBSETCOMPLETE");
				  
		JMenuItem miSuperset = new JMenuItem("Superset");
				  miSuperset.setActionCommand("CREATE_SUPERSET");
		
		JMenuItem miSupersetComplete = new JMenuItem("Superset Complete");
				  miSupersetComplete.setActionCommand("CREATE_SUPERSETCOMPLETE");
				  
		JMenuItem miOther = new JMenuItem("Other");
				  miOther.setActionCommand("CREATE_OTHER");
				  
		mManual.add( miEquivalence);
		mManual.add( miSubset);
		mManual.add( miSubsetComplete);
		mManual.add( miSuperset );
		mManual.add( miSupersetComplete);
		mManual.addSeparator();
		mManual.add( miOther);
		
		add(miStandard);
		add(mManual);
		
		
		// Listeners
		miStandard.addActionListener(layout);
		miEquivalence.addActionListener(layout);
		miSubset.addActionListener(layout);
		miSubsetComplete.addActionListener(layout);
		miSuperset.addActionListener(layout);
		miSupersetComplete.addActionListener(layout);
		miOther.addActionListener(layout);
		
	}
}
