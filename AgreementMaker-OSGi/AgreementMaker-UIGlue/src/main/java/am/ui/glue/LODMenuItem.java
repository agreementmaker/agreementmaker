package am.ui.glue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import am.extension.batchmode.simpleBatchMode.SimpleBatchModeRunner;
import am.matcher.lod.LinkedOpenData.LODBatch;
import am.ui.UICore;
import am.ui.UIMenu;
import am.ui.api.AMMenuItem;

public class LODMenuItem extends JMenu implements AMMenuItem {

	private static final long serialVersionUID = 4214490219241892299L;

	@Override public String getMenuLocation() { return UIMenu.MENU_MATCHERS + "/"; }
	@Override public JMenuItem getMenuItem() { return this; }
	

	public LODMenuItem() {
		super("Linked Open Data");
		
		JMenuItem runBatchLOD = new JMenuItem("Run LOD Schema Matching (old)");
		
		runBatchLOD.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				LODMenuItem.this.runLODBatchOld();
			}
		});
		
		add(runBatchLOD);
		
		JMenuItem runBatchLODnew = new JMenuItem("Run LOD Schema Matching");
		runBatchLODnew.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				LODMenuItem.this.runLODBatch();
			}
		});
		
		add(runBatchLODnew);
	}
	
	private void runLODBatch() {
		Runnable lod = new Runnable() {
			@Override public void run() {
				try {
					LODBatch batch = new LODBatch();
					batch.run();
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(
							UICore.getUI().getUIFrame(), 
							e.getClass() + "\n" + e.getMessage(), 
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		Thread lodThread = new Thread(lod);
		lodThread.setName("LODBatch " +  lodThread.getId());
		lodThread.start();
	}
	
	private void runLODBatchOld() {
		Runnable lod = new Runnable() {
			@Override public void run() {
				try {
					LODBatch batch = new LODBatch();
					batch.runOldVersion();
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(
							UICore.getUI().getUIFrame(), 
							e.getClass() + "\n" + e.getMessage(), 
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		Thread lodThread = new Thread(lod);
		lodThread.setName("LODBatch " +  lodThread.getId());
		lodThread.start();
	}
	
}
