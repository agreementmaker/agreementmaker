package am.utility;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * This is a panel that centers a component you put into it.
 * 
 * @author Cosmin Stroe
 *
 */
public class CenterPanel extends JPanel {

	private static final long serialVersionUID = -5832486927977728707L;

	public CenterPanel(JComponent component) {
		super();
		init(component, true, true);
	}

	public CenterPanel(JComponent component, boolean horizontalCenter, boolean verticalCenter) {
		super();
		init(component, horizontalCenter, verticalCenter);
	}

	private void init(JComponent component, boolean horizontalCenter, boolean verticalCenter) {
		GroupLayout layout = new GroupLayout(this);

		// the horizontal group
		SequentialGroup horizontalGroup = layout.createSequentialGroup();
		if( horizontalCenter ) {
			horizontalGroup.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
				.addComponent(component, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
		}
		else {
			horizontalGroup.addComponent(component, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
		}

		layout.setHorizontalGroup( horizontalGroup );


		// the vertical group
		if( verticalCenter ) {
			layout.setVerticalGroup( layout.createSequentialGroup()
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					.addComponent(component, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
					);
		}
		else {
			layout.setVerticalGroup( layout.createSequentialGroup()
					.addComponent(component, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					);
		}

		setLayout(layout);
	}
}
