package panelGUI;

import de.matthiasmann.twl.*;

public class ViewPanel extends ResizableFrame {
	
	private final DialogLayout widgetManager; // add all elements to this

	public ViewPanel(String title, int width, int height, int x, int y) {
		setTitle(title);
		setSize(width, height);
		setPosition(x, y);
		setDraggable(true);
		setVisible(true);
		
		widgetManager = new DialogLayout();
		
		// TODO Create elements for view panel
	}
	
    /*
     * TODO: Specify how different elements should be laid out on the display
    @Override
    protected void layout() { 
    }
    */

    /* 
     * TODO: Add our own event handlers
    @Override
    protected boolean handleEvent(Event evt) {
    	return true;
    }
    */

}
