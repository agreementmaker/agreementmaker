package am.va.graph;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;

import javax.swing.JFrame;

public class VAClustersPanel {
	private JFrame frameSub;
	private JFXPanel fxPanelSub;
	private Group rootSub;
	private Scene mySubScene;

	private int FrameWidth = 200;
	private int FrameHeight = 200;

	public VAClustersPanel() {
		frameSub = new JFrame("VA-UFL"); // init UFL panel
		fxPanelSub = new JFXPanel();
		frameSub.setSize(FrameWidth, FrameHeight);
		frameSub.setLocation(500, 200);
		frameSub.setVisible(true);
		frameSub.add(fxPanelSub);

		// frameSub.setAlwaysOnTop(true); //place the panel on the very top
		frameSub.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				VAPanel.enableUFL(true);
			}
		});
	}
	
	public void showFrame(boolean show) {
		frameSub.setVisible(show);
	}
	
	public void setPosition(int x, int y){
		frameSub.setBounds(x, y, FrameWidth, FrameHeight);
	}
}
