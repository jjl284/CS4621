package panelGUI;

import de.matthiasmann.twl.*;

public class BrushPanel extends ResizableFrame {

	private final DialogLayout widgetManager; // add all elements to this
	
	public BrushPanel(String title) {
		setTitle(title);
		widgetManager = new DialogLayout();
		
		// TODO Create elements for brush panel
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
