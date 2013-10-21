package am.ui.canvas2.popupmenus;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import am.ui.canvas2.utility.Canvas2Layout;

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
		          
		JMenuItem miSuperclass = new JMenuItem("Superclass");
		          miSuperclass.setActionCommand("CREATE_SUPERCLASS");
		          
		JMenuItem miSubclass = new JMenuItem("Subclass");
		          miSubclass.setActionCommand("CREATE_SUBCLASS");
		          
		JMenuItem miSubset = new JMenuItem("Subset");
		          miSubset.setActionCommand("CREATE_SUBSET");
		          
		JMenuItem miSubsetComplete = new JMenuItem("Subset Complete");
				  miSubsetComplete.setActionCommand("CREATE_SUBSETCOMPLETE");
				  
		JMenuItem miSuperset = new JMenuItem("Superset");
				  miSuperset.setActionCommand("CREATE_SUPERSET");
		
		JMenuItem miSupersetComplete = new JMenuItem("Superset Complete");
				  miSupersetComplete.setActionCommand("CREATE_SUPERSETCOMPLETE");
		JMenuItem miRelated = new JMenuItem("Related");
		          miRelated.setActionCommand("CREATE_RELATED");
				  
		//JMenuItem miOther = new JMenuItem("Other");
		//		  miOther.setActionCommand("CREATE_OTHER");
				  
		mManual.add( miEquivalence);
		mManual.addSeparator();
		mManual.add( miSuperclass );
		mManual.add( miSubclass );
		mManual.addSeparator();
		mManual.add( miSubset);
		mManual.add( miSubsetComplete);
		mManual.add( miSuperset );
		mManual.add( miSupersetComplete);
		mManual.addSeparator();
		mManual.add( miRelated );
		//mManual.addSeparator();
		//mManual.add( miOther);
		
		add(miStandard);
		add(mManual);
		
		
		// Listeners
		miStandard.addActionListener(layout);
		miEquivalence.addActionListener(layout);
		miSuperclass.addActionListener(layout);
		miSubclass.addActionListener(layout);
		miSubset.addActionListener(layout);
		miSubsetComplete.addActionListener(layout);
		miSuperset.addActionListener(layout);
		miSupersetComplete.addActionListener(layout);
		miRelated.addActionListener(layout);
		//miOther.addActionListener(layout);
		
	}
}
