package am.ui.glue;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

import am.Utility;
import am.app.Core;
import am.app.mappingEngine.AbstractMatcher;
import am.evaluation.clustering.gvm.GVM_Clustering_Panel;
import am.ui.UICore;
import am.ui.UIMenu;
import am.ui.api.AMMenuItem;
import am.ui.controlpanel.MatchersControlPanel;
import am.ui.controlpanel.table.MatchersTablePanel;

public class GVMClusterMenuItem extends JMenuItem implements AMMenuItem {

	private static final long serialVersionUID = 1799700209616795888L;

	@Override public String getMenuLocation() { return UIMenu.MENU_MATCHERS + "/"; }
	@Override public JMenuItem getMenuItem() { return this; }

	public GVMClusterMenuItem() {
		super("GVM Clustering (Classes)");
		
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GVMClusterMenuItem.this.showGVMPanel();
			}
		});
	}
	
	private void showGVMPanel() {
		
		MatchersControlPanel controlPanel = UICore.getUI().getControlPanel();
		
		/** Clustering with GVM for classes */
		MatchersTablePanel m = controlPanel.getTablePanel();

		int[] selectedRows =  m.getTable().getSelectedRows();

		if(selectedRows.length < 2) {
			Utility.displayErrorPane("You must select at least two matchers in the Matchers Control Panel.", null);
			return;
		}

		List<AbstractMatcher> selectedMatchers = new ArrayList<AbstractMatcher>();
		List<AbstractMatcher> matcherInstances = Core.getInstance().getMatcherInstances();

		for( int i : selectedRows ) {
			selectedMatchers.add(matcherInstances.get(i));
		}

		GVM_Clustering_Panel gvm = new GVM_Clustering_Panel(selectedMatchers);

		JFrame frm = new JFrame();
		frm.setLayout(new BorderLayout());
		frm.add(gvm, BorderLayout.CENTER);
		frm.pack();
		frm.setLocationRelativeTo(null);
		frm.setVisible(true);
	}
}
