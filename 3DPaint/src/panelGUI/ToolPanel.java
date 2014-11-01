package panelGUI;

import de.matthiasmann.twl.*;

public class ToolPanel extends ResizableFrame{
	
	private final DialogLayout widgetManager; // add all elements to this

	public ToolPanel(String title) {
		setTitle(title);
		widgetManager = new DialogLayout();
		
		// TODO Create elements for tool panel
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
