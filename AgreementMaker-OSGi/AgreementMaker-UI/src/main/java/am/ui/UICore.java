package am.ui;

import java.util.HashMap;

import am.app.ontology.Node;
import am.ui.canvas2.utility.Canvas2Vertex;

/**
 * This class will help in the transition of the UI code from AgreementMaker-Core to AgreementMaker-UI.
 * It's meant to replace calls to Core and make things work until we implement a better designed solution.
 * 
 * FIXME: Get rid of this class!!!!!
 * 
 * @author Cosmin Stroe <cstroe@gmail.com>
 * 
 * @version Oct. 20, 2013
 */
public class UICore {

	private static final UICore core = new UICore();
	
	private HashMap<Node,Canvas2Vertex> graphicalRepresentations = new HashMap<>();
	
	private UI ui;
	
	private UICore() {}
	
	public static UICore getCore() {
		return core;
	}
	
	public void setUI(UI ui) {
		this.ui = ui;
	}
	
	public UI getUI() { return ui; }
	
}
