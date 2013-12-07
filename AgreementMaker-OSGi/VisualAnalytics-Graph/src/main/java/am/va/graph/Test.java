package am.va.graph;

import javax.swing.SwingUtilities;

import am.app.Core;
import am.ui.UI;
import am.ui.UICore;

@SuppressWarnings("restriction")
public class Test {

	private static final boolean START_AGREEMENTMAKER = false;

	public static void main(String[] args) {
		VASyncListener vaSync = new VASyncListener();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!START_AGREEMENTMAKER) {
					Core.getInstance().setRegistry(new ManualMatcherRegistry());
					UICore.setUI(new UI());
				}
			}
		});
	}
}
