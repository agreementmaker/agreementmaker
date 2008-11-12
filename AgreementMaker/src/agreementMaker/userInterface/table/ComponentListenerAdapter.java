package agreementMaker.userInterface.table;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public abstract class ComponentListenerAdapter implements ComponentListener {

    public void componentHidden(final ComponentEvent event) {
        resizingAction();
    }

    public void componentMoved(final ComponentEvent event) {
        resizingAction();
    }

    public void componentResized(final ComponentEvent event) {
        resizingAction();
    }

    public void componentShown(final ComponentEvent event) {
        resizingAction();
    }

    protected abstract void resizingAction();
    
}
