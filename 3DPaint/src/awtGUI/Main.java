package awtGUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent; 

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

public class Main extends Frame {

	// TODO: implement methods needed for AWTGLCanvas such as add/removeNotify()
	// TODO: Put all global variables here
	
	/** AWT GL Canvas */
	private AWTGLCanvas awtCanvas;
	
	public int MAIN_WIDTH = 800;
	public int MAIN_HEIGHT = 600;
	
	
	// TODO: Initialize everything here
	public Main() throws LWJGLException {
		setTitle("3DPaint Application");
		setBackground(Color.BLACK);
		
		awtCanvas = new AWTGLCanvas();
		awtCanvas.setSize(MAIN_WIDTH, MAIN_HEIGHT);
		add(awtCanvas);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		
		setLayout(new GridLayout(1, 3)); // Change this later
		
		init_MenuBar(); // Add the menu bar to the frame
		
		init_Panels(); // Add panels to the frame (or just make separate frames)
		
		//TODO: Extract these to their own classes
		// Other Panels on Screen
		//Panel viewPanel = new Panel();
		//Panel toolPanel = new Panel();
		//Panel brushPanel = new Panel();
		
		setResizable(true);
		
		pack(); // put all the elements together on the frame
		
		setVisible(true); 
	}

	// TODO: Create actual menu names and functions
	private void init_MenuBar() {
		// Top Menu Bar
		MenuBar menubar = new MenuBar();
	   
	    // Create the menu
	    Menu menu=new Menu("Menu");
	   
	    // Create the submenu
	    Menu submenu=new Menu("Sub Menu");
	   
	    // Create MenuItems
	    MenuItem m1=new MenuItem("Menu Item 1");
	    MenuItem m2=new MenuItem("Menu Item 2");
	    MenuItem m3=new MenuItem("Menu Item 3");
	   
	    MenuItem m4=new MenuItem("Menu Item 4");
	    MenuItem m5=new MenuItem("Menu Item 5");
	   
	    // Attach menu items to menu
	    menu.add(m1);
	    menu.add(m2);
	    menu.add(m3);
	   
	    // Attach menu items to submenu
	    submenu.add(m4);
	    submenu.add(m5);
	   
	    // Attach submenu to menu
	    menu.add(submenu);
	   
	    // Attach menu to menu bar
	    menubar.add(menu);
	   
	    // Set menu bar to the frame
	    setMenuBar(menubar);
	}
	
	// TODO: Create actual panels we're using
	private void init_Panels() {
		Label msglabel = new Label();
		msglabel.setAlignment(Label.CENTER);
		msglabel.setText("I am a message on a panel inside a panel");
		
		Container controlPanel = new Panel();
		controlPanel.setLayout(new FlowLayout());
		controlPanel.setBackground(Color.blue);
		controlPanel.setSize(300, 300);
		
		Panel panel = new Panel();
	    panel.setBackground(Color.magenta);
	    panel.setLayout(new FlowLayout());        
	    panel.add(msglabel);
	    panel.setSize(200, 200);

	    controlPanel.add(panel);
	    add(controlPanel);
	}
	
	/**
	 * Start up the application
	 * @param args
	 */
	public static void main(String[] args) throws LWJGLException {
		new Main();
	} 

}
