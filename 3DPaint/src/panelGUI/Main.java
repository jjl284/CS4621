/**
 * 
 */
package panelGUI;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import de.matthiasmann.twl.*;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

import test.TestUtils;

/**
 * @author Natalie
 *
 */
public class Main extends DesktopArea {

	/**
	 * This is the first thing that starts our application
	 * @param args
	 */
	public static void main(String[] args) {
        try {
            Display.setDisplayMode(new DisplayMode(800, 600));
            Display.create();
            Display.setTitle("3DPainter Application");
            Display.setVSyncEnabled(true);

            LWJGLRenderer renderer = new LWJGLRenderer();
            Main panelGUI = new Main();
            GUI gui = new GUI(panelGUI, renderer);

            // TODO: Create our own theme XML if so desired
            //ThemeManager theme = ThemeManager.createThemeManager(
            //        Main.class.getResource("nodes.xml"), renderer);
            //gui.applyTheme(theme);
            

            while(!Display.isCloseRequested() && !panelGUI.quit) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

                gui.update();
                Display.update();
            }

            gui.destroy();
            // TODO: uncomment following line when we implement our own theme
            //theme.destroy();
        } catch (Exception ex) {
            //System.out.println(ex);
        	TestUtils.showErrMsg(ex);
        }
        Display.destroy();
    }

	/*
	 * These are our variables for Main.java
	 */    
    public boolean quit;
    private final ViewPanel viewPanel;
    private final ToolPanel toolPanel;
    private final BrushPanel brushPanel;
    private final MenuPanel menuPanel;

    /***
     * Our constructor. Initialize everything needed to be displayed here
     */
    public Main() {
    	quit = false;
    	
    	// TODO: Initialize these panels and implement their methods
    	viewPanel = new ViewPanel("View Panel", 300, 300, 50, 50);
    	//viewPanel.setTheme("nodes");
    	add(viewPanel);
    	
    	toolPanel = new ToolPanel("Tool Panel");
    	brushPanel = new BrushPanel("Brush Panel");
    	menuPanel = new MenuPanel("Menu Panel");
    	
    	
    }

    /***
     * Specify how different elements should be laid out on the display
     */
    //@Override
    //protected void layout() {
    //}

    // TODO: Add our own event handlers
    @Override
    protected boolean handleEvent(Event evt) {
        if(super.handleEvent(evt)) {
            return true;
        }
        switch (evt.getType()) {
            case KEY_PRESSED:
                switch (evt.getKeyCode()) {
                    case Event.KEY_ESCAPE:
                        quit = true;
                        return true;
                }
                break;
            case MOUSE_BTNDOWN:
                if(evt.getMouseButton() == Event.MOUSE_RBUTTON) {
                    return createRadialMenu().openPopup(evt);
                }
                break;
            default: // What else should happen? Probably nothing
            	break;
        }
        return evt.isMouseEventNoWheel();
    }

    // TODO: Modify this menu to do what we want
    RadialPopupMenu createRadialMenu() {
        RadialPopupMenu rpm = new RadialPopupMenu(this);
        return rpm;
    }

}
