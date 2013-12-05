package am.ui.glue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.openjena.atlas.logging.Log;

import am.extension.batchmode.simpleBatchMode.SimpleBatchModeRunner;
import am.matcher.lod.LinkedOpenData.LODBatch;
import am.matcher.oaei.imei2013.InstanceMatching;
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
		
		addSeparator();
		
		
		// IMEI menu items
		
		JMenu runIMEI2013 = new JMenu("IMEI 2013");
		
		JMenuItem runIMEI2013_all = new JMenuItem("All Testcases");
		runIMEI2013_all.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				LODMenuItem.this.runIMEI2013_All();
			}
		});
		
		JMenuItem runIMEI2013_01 = new JMenuItem("Testcase 01");
		runIMEI2013_01.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				LODMenuItem.this.runIMEI2013(1);
			}
		});
		
		JMenuItem runIMEI2013_02 = new JMenuItem("Testcase 02");
		runIMEI2013_02.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				LODMenuItem.this.runIMEI2013(2);
			}
		});
		
		JMenuItem runIMEI2013_03 = new JMenuItem("Testcase 03");
		runIMEI2013_03.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				LODMenuItem.this.runIMEI2013(3);
			}
		});
		
		JMenuItem runIMEI2013_04 = new JMenuItem("Testcase 04");
		runIMEI2013_04.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				LODMenuItem.this.runIMEI2013(4);
			}
		});
		
		JMenuItem runIMEI2013_05 = new JMenuItem("Testcase 05");
		runIMEI2013_05.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				LODMenuItem.this.runIMEI2013(5);
			}
		});
		
		runIMEI2013.add(runIMEI2013_all);
		runIMEI2013.add(runIMEI2013_01);
		runIMEI2013.add(runIMEI2013_02);
		runIMEI2013.add(runIMEI2013_03);
		runIMEI2013.add(runIMEI2013_04);
		runIMEI2013.add(runIMEI2013_05);
		
		add(runIMEI2013);
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
	
	private void runIMEI2013_All() {
		Runnable runnable = new Runnable() {
			@Override public void run() {
				try {
					InstanceMatching im = new InstanceMatching();
					im.runTest(1);
					im.runTest(2);
					im.runTest(3);
					im.runTest(4);
					im.runTest(5);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(
							UICore.getUI().getUIFrame(), 
							e.getClass() + "\n" + e.getMessage(), 
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		Thread thread = new Thread(runnable);
		thread.setName("IMEI2013 All " +  thread.getId());
		thread.start();
	}
	
	private void runIMEI2013(final int testNum) {
		Runnable runnable = new Runnable() {
			@Override public void run() {
				try {
					InstanceMatching im = new InstanceMatching();
					im.runTest(testNum);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(
							UICore.getUI().getUIFrame(), 
							e.getClass() + "\n" + e.getMessage(), 
							"ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		Thread thread = new Thread(runnable);
		thread.setName("IMEI2013 0" + testNum + " " +  thread.getId());
		thread.start();
	}
}
