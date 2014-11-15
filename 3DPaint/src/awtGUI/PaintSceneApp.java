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
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.lwjgl.LWJGLException;

import blister.FalseFirstScreen;
import blister.MainGame;
import blister.ScreenList;
import cs4620.common.Mesh;
import cs4620.common.Scene;
import cs4620.common.Scene.NameBindMesh;
import cs4620.common.Scene.NameBindSceneObject;
import cs4620.common.Scene.NameBindTexture;
import cs4620.common.SceneObject;
import cs4620.common.Texture;
import cs4620.common.event.SceneReloadEvent;
import cs4620.common.texture.TexGenUVGrid;
import cs4620.mesh.gen.MeshGenCube;
import egl.math.Vector3;
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
	private PaintCanvas paintCanvas;
	public Scene scene;
	
	/**
	 * Constructor
	 * @param canvas
	 * @throws LWJGLException
	 */
	public PaintSceneApp(PaintCanvas canvas) throws LWJGLException {
		super("3D Paint Scene Mesh", MAIN_WIDTH, MAIN_HEIGHT, canvas);
		//otherWindow = new ControlWindow(this);
		
		mainFrame = new Frame();
		mainFrame.setResizable(false);
		mainFrame.setSize(MAIN_WIDTH, MAIN_HEIGHT);
		mainFrame.setTitle("3DPaint Application");
		mainFrame.setBackground(Color.GRAY);
		mainFrame.setLayout(new BoxLayout(mainFrame,BoxLayout.Y_AXIS));
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
				//System.exit(0); this is already done in MainGame's exit()
			}
		});
		mainFrame.setVisible(true);
		
		paintCanvas = canvas;
		paintCanvas.setVisible(true);
		paintCanvas.setFocusable(true);
		paintCanvas.setIgnoreRepaint(true);
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
	    Menu mbNew=new Menu("New...");
	    
	    MenuItem newSphere = new MenuItem("Sphere");
	    MenuItem newCube = new MenuItem("Cube");
	    MenuItem newCylinder = new MenuItem("Cylinder");
	    MenuItem newPlane = new MenuItem("Plane");
	    MenuItem newTorus = new MenuItem("Torus");
	    MenuItem newMesh = new MenuItem("Import Mesh...");
	    
	    ActionListener newMeshActionListener = new ActionListener() {
	    	@Override
			public void actionPerformed(ActionEvent arg0) {
	    		// Create a new XML file with the given mesh and default texture, then load the mesh
	    		scene = new Scene();
	    		scene.setBackground( new Vector3(240,240,240) );
	    		
	    		Mesh m = new Mesh(); m.setGenerator( new MeshGenCube() );
	    		scene.addMesh( new NameBindMesh("Cube", m) );
	    		
	    		Texture t = new Texture();
	    		//t.setFile("data/textures/EarthLonLat.png");
	    		t.setGenerator( new TexGenUVGrid() );
	    		scene.addTexture( new NameBindTexture("CubeTexture", t) );
	    		
	    		SceneObject o = new SceneObject();
	    		scene.addObject(new NameBindSceneObject("PaintedCube", o));
	    		
	    		
	    		try {
					scene.saveData("data/scenes/NewPaintedCube.xml");
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    		System.out.println("Created a new scene with a cube");
	    		return;
	    	}
	    };
	    
	    newSphere.addActionListener(newMeshActionListener);
	    newCube.addActionListener(newMeshActionListener);
	    newCylinder.addActionListener(newMeshActionListener);
	    newPlane.addActionListener(newMeshActionListener);
	    newTorus.addActionListener(newMeshActionListener);
	    
	    newMesh.addActionListener(new ActionListener() {
	    	@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO: Allow user to select a specific mesh of their choosing
	    		// TODO: Direct user to select a texture size when done selecting mesh
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
							return;}}}}
	    });
	    
	    mbNew.add(newSphere);
	    mbNew.add(newCube);
	    mbNew.add(newCylinder);
	    mbNew.add(newPlane);
	    mbNew.add(newTorus);
	    mbNew.add(newMesh);
	    
	    mbNew.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO: Customize this to allow the user to select from existing
				// TODO: Customize this to ask user to specify a
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
	    mFile.add(mbNew);
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
		mainFrame.dispose();
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
