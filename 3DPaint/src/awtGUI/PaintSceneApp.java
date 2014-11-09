package awtGUI;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BoxLayout;

import org.lwjgl.LWJGLException;

import blister.FalseFirstScreen;
import blister.MainGame;
import blister.ScreenList;
import cs4620.common.Scene;
import cs4620.common.event.SceneReloadEvent;
import cs4620.scene.SceneApp;
import cs4620.scene.ViewScreen;
import cs4620.scene.form.ControlWindow;
import ext.java.Parser;

public class PaintSceneApp extends MainGame {
	
	/**
	 * The Thread That Runs The Other Window
	 */
	Thread tWindow = null;
	
	/**
	 * Window That Modifies The Underlying Data Of The Scene
	 */
	//ControlWindow otherWindow = null;
	
	/**
	 * Globals
	 */
	public static int MAIN_WIDTH = 800;
	public static int MAIN_HEIGHT = 600;
	
	private Frame mainFrame;
	private static PaintCanvas paintCanvas;
	
	public Scene scene;
	
	/**
	 * Constructor
	 * @param canvas
	 * @throws LWJGLException
	 */
	public PaintSceneApp(PaintCanvas canvas) throws LWJGLException {
		super("3D Paint Scene Mesh", Main.MAIN_WIDTH, Main.MAIN_HEIGHT, canvas);
		//otherWindow = new ControlWindow(this);
		
		mainFrame = new Frame();
		mainFrame.setTitle("3DPaint Application");
		mainFrame.setBackground(Color.BLACK);
		mainFrame.setLayout(new BoxLayout(mainFrame,BoxLayout.Y_AXIS));
		/*mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();

				System.exit(0);
			}
		});*/
		mainFrame.setVisible(true);
		
		paintCanvas = canvas;
		paintCanvas.setVisible(true);
		paintCanvas.setBackground(Color.CYAN);
		paintCanvas.setSize(MAIN_WIDTH, MAIN_HEIGHT);
		mainFrame.add(paintCanvas);
		
		scene = new Scene();
		
		init_MenuBar(); // Add the menu bar to the frame
		init_Panels(); // Add panels to the frame (or just make separate frames)
		
		mainFrame.pack();
	}
	
	public static void main(String[] args) throws LWJGLException {
		PaintSceneApp app = new PaintSceneApp(new PaintCanvas());
		app.run();
		app.dispose();
	}
	
	private void init_MenuBar() {
		// Top Menu Bar
		MenuBar menubar = new MenuBar();
	   
	    // Create the menus (File, Shading, Mode)
	    Menu mFile=new Menu("File");
	    Menu mShading=new Menu("Shading");
	    Menu mMode=new Menu("Mode");
	    Menu mToolbars = new Menu("Toolbars");
	    
	   //final PaintSceneApp psapp = this;
	    // Create MenuItems
	    MenuItem mbImp=new MenuItem("Import");
		mbImp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog fd = new FileDialog(mainFrame);
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
	    mbExp.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("EXP clicked");

			}});
	   
	    MenuItem mbBP=new MenuItem("Blinn-Phong");
	    MenuItem mbLamb=new MenuItem("Lambertian");
	    MenuItem mbCT=new MenuItem("Cook-Torrance");

	    MenuItem mbEdit=new MenuItem("Edit");
	    MenuItem mbView=new MenuItem("View");
	   
	    CheckboxMenuItem cEdit = new CheckboxMenuItem("Edit Bar",true);
	    CheckboxMenuItem cColor=  new CheckboxMenuItem("Color Bar",true);
	    CheckboxMenuItem cManip = new CheckboxMenuItem("Manipulator Bar",true);
	    
	    cEdit.addActionListener(new ToolbarActionListener("Edit Bar", cEdit.getState(), mainFrame));
	    cColor.addActionListener(new ToolbarActionListener("Color Bar", cColor.getState(), mainFrame));
	    cManip.addActionListener(new ToolbarActionListener("Manipulator Bar", cManip.getState(), mainFrame));
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
	    mainFrame.setMenuBar(menubar);
	}
	
	private void init_Panels() {
	    //MANIP PANEL
		ManipPanel mp = new ManipPanel();
		mainFrame.add(mp);
		mp.setVisible(true);
		
		
		//EDIT PANEL
		EditPanel ep = new EditPanel();
		mainFrame.add(ep);
	    ep.setVisible(true);
	    
	    //COLOR PANEL
		ColorPanel cp = new ColorPanel();
		mainFrame.add(cp);
		cp.setVisible(true);
	}
	
	@Override
	protected void buildScreenList() {
		screenList = new ScreenList(this, 0,
			new FalseFirstScreen(1),
			new PaintViewScreen()
			);
	}

	@Override
	protected void fullInitialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void fullLoad() {
        //tWindow = new Thread(new ControlThread(this));
        //tWindow.run();
	}
	
	@Override
	public void exit() {
		//if(otherWindow != null) {
			//tWindow.interrupt();
		//}
		super.exit();
	}
	
	/*class ControlThread implements Runnable {
		PaintSceneApp app;
		
		ControlThread(PaintSceneApp a) {
			app = a;
		}
		
		@Override
		public void run() {
			//app.otherWindow.run();
		}
		
	}*/
}
