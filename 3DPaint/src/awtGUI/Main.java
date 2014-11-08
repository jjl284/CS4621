package awtGUI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent; 
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

import cs4620.common.Scene;
import cs4620.common.event.SceneReloadEvent;
import cs4620.scene.ViewScreen;
import cs4620.scene.form.ControlWindow;
import cs4620.scene.form.ScenePanel;
import ext.java.Parser;

public class Main extends Frame {

	// TODO: implement methods needed for AWTGLCanvas such as add/removeNotify()
	// TODO: Put all global variables here
	
	/** AWT GL Canvas */
	private AWTGLCanvas awtCanvas;
	public Scene scene;
	public ViewScreen viewScreen;
	
	public int MAIN_WIDTH = 800;
	public int MAIN_HEIGHT = 600;

	// TODO: Initialize everything here
	public Main() throws LWJGLException {
		awtCanvas = new AWTGLCanvas();
		awtCanvas.setSize(MAIN_WIDTH, MAIN_HEIGHT);
		add(awtCanvas);
		scene = new Scene();
		viewScreen= new ViewScreen();
	}

	private void run() { //parallel to initialize in DemoBox
		setTitle("3DPaint Application");
		setBackground(Color.BLACK);
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});

		
		init_MenuBar(); // Add the menu bar to the frame
		init_Panels(); // Add panels to the frame (or just make separate frames)

		
		//TODO: Extract these to their own classes
		// Other Panels on Screen
		//Panel viewPanel = new Panel();
		//Panel toolPanel = new Panel();
		//Panel brushPanel = new Panel();
		
		setResizable(true);
		
		pack(); // put all the elements together on the frame		
	} 
	
	// TODO: Create actual menu names and functions
	private void init_MenuBar() {
		// Top Menu Bar
		MenuBar menubar = new MenuBar();
	   
	    // Create the menus (File, Shading, Mode)
	    Menu mFile=new Menu("File");
	    Menu mShading=new Menu("Shading");
	    Menu mMode=new Menu("Mode");
	    Menu mToolbars = new Menu("Toolbars");
	    
	   final Main main = this;
	    // Create MenuItems
	    MenuItem mbImp=new MenuItem("Import");
		mbImp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog fd = new FileDialog(main);
				fd.setVisible(true);
				for(File f : fd.getFiles()) {
					String file = f.getAbsolutePath();
					if(file != null) {
						Parser p = new Parser();
						Object o = p.parse(file, Scene.class);
						if(o != null) {
							Scene old = scene;
							scene = (Scene)o;
							if(old != null) old.sendEvent(new SceneReloadEvent(file));
							System.out.println("SCENE "+scene.getClass().toString());
							return;}}}}});
	    MenuItem mbExp=new MenuItem("Export");
	   
	    MenuItem mbBP=new MenuItem("Blinn-Phong");
	    MenuItem mbLamb=new MenuItem("Lambertian");
	    MenuItem mbCT=new MenuItem("Cook-Torrance");

	    MenuItem mbEdit=new MenuItem("Edit");
	    MenuItem mbView=new MenuItem("View");
	   
	    MenuItem cEdit = new CheckboxMenuItem("Edit Bar");
	    MenuItem cColor=  new CheckboxMenuItem("Color Bar");
	    MenuItem cManip = new CheckboxMenuItem("Manipulator Bar");
	    
	    cEdit.addActionListener(new ToolbarActionListener("Edit Bar", cEdit.isEnabled(), main));
	    cColor.addActionListener(new ToolbarActionListener("Color Bar", cColor.isEnabled(), main));
	    cManip.addActionListener(new ToolbarActionListener("Manipulator Bar", cManip.isEnabled(), main));
	    // Attach menu items to menu
	    mFile.add(mbImp);
	    mFile.add(mbExp);
	   
	    // Attach menu items to submenu
	    mShading.add(mbBP);
	    mShading.add(mbLamb);
	    mShading.add(mbCT);
	    mMode.add(mbEdit);
	    mMode.add(mbView);
	    mToolbars.add(cEdit);
	    mToolbars.add(cColor);
	    mToolbars.add(cManip);
	    // Attach menu to menu bar
	    menubar.add(mFile);
	    menubar.add(mShading);
	    menubar.add(mMode);
	    menubar.add(mToolbars);
	   
	    // Set menu bar to the frame
	    setMenuBar(menubar);
	}
	
	// TODO: Create actual panels we're using
	private void init_Panels() {
	    //MANIP PANEL
		ManipPanel mp = new ManipPanel();
		add(mp);
		mp.setVisible(true);
		
		
		//EDIT PANEL
	    EditPanel ep = new EditPanel();
		add(ep);
	    ep.setVisible(true);
	    
	    //COLOR PANEL
	    ColorPanel cp = new ColorPanel();
		add(cp);
		cp.setVisible(true);
		

	}
	
	/**
	 * Start up the application
	 * @param args
	 */
	public static void main(String[] args) throws LWJGLException {
		Main app = new Main();
		app.run();
		app.setVisible(true);
	}

}
