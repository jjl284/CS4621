package awtGUI;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.BoxLayout;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import org.lwjgl.LWJGLException;

import blister.FalseFirstScreen;
import blister.MainGame;
import blister.ScreenList;
import cs4620.common.Material;
import cs4620.common.Material.InputProvider;
import cs4620.common.Mesh;
import cs4620.common.Scene;
import cs4620.common.Scene.NameBindMaterial;
import cs4620.common.Scene.NameBindMesh;
import cs4620.common.Scene.NameBindSceneObject;
import cs4620.common.Scene.NameBindTexture;
import cs4620.common.SceneCamera;
import cs4620.common.SceneObject;
import cs4620.common.Texture;
import cs4620.common.event.SceneReloadEvent;
import cs4620.common.texture.TexGenUVGrid;
import cs4620.mesh.gen.MeshGenCube;
import egl.math.Vector3;
import ext.java.Parser;

public class PaintSceneApp extends MainGame implements ActionListener, ChangeListener{
	
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
	public PaintTexture paintTexture;
	
	private JLabel toolSizeLabel;

	private JToggleButton pencil;
	private JToggleButton eraser;
	
	private JButton colorButton;
	
	private JButton mode;
	
	private JSlider toolSizeSlider;
	private final int sliderMin = 0;
	private final int sliderMax = 50;
	private final int sliderInit = 0;
	private int iconSize = 48;	
	
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
		mainFrame.setLayout(new BorderLayout());
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
	    Menu mEdit = new Menu ("Edit");
	    Menu mShading=new Menu("Shading");
	    //Menu mMode=new Menu("Mode");
	    Menu mToolbars = new Menu("Toolbars");
	    
	    Menu mbLoad = new Menu("Load Default");
	    MenuItem loadEarth = new MenuItem("Earth");
	    loadEarth.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("ACTION LISTENER");
				
				File f = new File("Earth.xml");
				String file = f.getAbsolutePath();
				System.out.println("FILE TO STRING"+file.toString());
				if(file!=null){
					Parser p = new Parser();
					Object o = p.parse(file, Scene.class);
					if(o!=null) {
						Scene old = scene;
						scene = (Scene)o;
						String matName = scene.objects.get("Earth").material;
						String texName = scene.materials.get(matName).inputDiffuse.texture;
						String texFileName = scene.textures.get(texName).file;
						//File f = new File(texFileName);
						//paintTexture = new PaintTexture(texFileName);
						if(old!=null) old.sendEvent(new SceneReloadEvent(file));
						System.out.println("SCENE"+scene.getClass().toString());
						return;
					}
				}
			}});
	    
	    
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
	    		Scene old = scene;
	    		scene = new Scene();
	    		scene.setBackground( new Vector3(40,40,40) );
	    		
	    		Mesh m = new Mesh(); m.setGenerator( new MeshGenCube() );
	    		scene.addMesh( new NameBindMesh("DefaultCube", m) );
	    		
	    		Texture t = new Texture();
	    		//t.setFile("data/textures/EarthLonLat.png");
	    		//scene.addTexture( new NameBindTexture("CubeTexture", t) );
	    		paintTexture = new PaintTexture(1024, 1024, "data/textures/CubeTexture.png");
	    		t.setFile("data/textures/CubeTexture.png");
	    		scene.addTexture( new NameBindTexture("CubeTexture", t) );
	    		
	    		Material mat = new Material();
	    		mat.setType(Material.T_AMBIENT);
	    		InputProvider ip = new InputProvider();
	    		ip.setTexture("CubeTexture");
	    		mat.setDiffuse(ip);
	    		scene.addMaterial(new NameBindMaterial("GenericCubeMaterial", mat));
	    		
	    		SceneObject o = new SceneObject();
	    		o.setMaterial("GenericCubeMaterial");
	    		o.setMesh("DefaultCube");
	    		scene.addObject(new NameBindSceneObject("PaintedCube", o));
	    		
	    		SceneCamera cam = new SceneCamera();
	    		cam.addTranslation(new Vector3(0,0,2));
	    		scene.addObject(new NameBindSceneObject("Camera", cam));
	    		
	    		
	    		// Attempt to create a new scene file and then reload the display
	    		try {
					scene.saveData("data/scenes/PaintedCube.xml");
					File f = new File("data/scenes/PaintedCube.xml");
					String file = f.getAbsolutePath();
					if(old!=null) old.sendEvent(new SceneReloadEvent(file));
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
	    mbBP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//paintCanvas.setShading(Shading.PHONG);			
			}});
	    MenuItem mbLamb=new MenuItem("Lambertian");
	    mbLamb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//paintCanvas.setShading(Shading.LAMBERTIAN);			
			}});
	    MenuItem mbCT=new MenuItem("Cook-Torrance");
	    mbCT.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//paintCanvas.setShading(Shading.CT);			
			}});
	    
	    MenuItem mbUndo=new MenuItem("Undo");
	    mbUndo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				paintCanvas.LoadPrevState(paintCanvas.currState);				
			}});
	    MenuItem mbRedo=new MenuItem("Redo");
	    mbRedo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				paintCanvas.LoadNextState(paintCanvas.currState);				
			}});
	    
	    CheckboxMenuItem cShow = new CheckboxMenuItem("Show",true);
	    //CheckboxMenuItem cEdit = new CheckboxMenuItem("Edit Bar",true);
	    //CheckboxMenuItem cColor=  new CheckboxMenuItem("Color Bar",true);
	    //CheckboxMenuItem cManip = new CheckboxMenuItem("Manipulator Bar",true);
	    
	    cShow.addActionListener(new ToolbarActionListener("Show",cShow.getState(), mainFrame));
	    //cEdit.addActionListener(new ToolbarActionListener("Edit Bar", cEdit.getState(), mainFrame));
	    //cColor.addActionListener(new ToolbarActionListener("Color Bar", cColor.getState(), mainFrame));
	    //cManip.addActionListener(new ToolbarActionListener("Manipulator Bar", cManip.getState(), mainFrame));
	    // Attach menu items to menu
	    
	    mFile.add(mbNew);
	    mFile.add(mbLoad);
	    	mbLoad.add(loadEarth);
	    mFile.add(mbImp);
	    mFile.add(mbExp);
	   
	    // Attach menu items to submenu
	    mShading.add(mbBP);
	    mShading.add(mbLamb);
	    mShading.add(mbCT);
	    mEdit.add(mbUndo);
	    mEdit.add(mbRedo);
	    //mMode.add(mbEdit);
	    //mMode.add(mbView);
	    
	    mToolbars.add(cShow);
	    //mToolbars.add(cEdit);
	    //mToolbars.add(cColor);
	    //mToolbars.add(cManip);
	    
	    // Attach menu to menu bar
	    menubar.add(mFile);
	    menubar.add(mEdit);
	    menubar.add(mShading);
	    //menubar.add(mMode);
	    menubar.add(mToolbars);
	   
	    // Set menu bar to the frame
	    mainFrame.setMenuBar(menubar);
	}
	

	// TODO: Create actual panels we're using
	private void init_Panels() {
		//EDIT PANEL
		JToolBar ep = makeToolBar();
	    
		
		//TOOL SIZE SLIDER BAR
		toolSizeSlider = new JSlider(JSlider.VERTICAL,sliderMin, sliderMax, sliderInit);
		toolSizeSlider.addChangeListener(this);
		toolSizeSlider.setMinorTickSpacing(1);
		toolSizeSlider.setPaintTicks(true);
		toolSizeSlider.setSnapToTicks(true);
		
		//STATUS PANEL (also houses MANIP PANEL)
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setPreferredSize(new Dimension(getWidth(),18));
		statusPanel.setLayout(new GridLayout(1,1));
		toolSizeLabel = new JLabel ("    "+String.valueOf(paintCanvas.activeToolSize));

		statusPanel.add(toolSizeLabel);
		
		//mainFrame.add(toolSizeSlider, BorderLayout.WEST);
		mainFrame.add(ep, BorderLayout.EAST);
		mainFrame.add(toolSizeSlider, BorderLayout.WEST);
		mainFrame.add(statusPanel, BorderLayout.SOUTH);
		
		mainFrame.pack();
		mainFrame.setVisible(true);
		
	}
		
	private JToolBar makeToolBar(){
		JPanel[] panelHolder = new JPanel[1];
		
		ButtonGroup tools = new ButtonGroup();
		pencil = new JToggleButton(new ImageIcon("brush.png"));
		pencil.setToolTipText("pencil");
		pencil.addActionListener(this);
		
		eraser = new JToggleButton(new ImageIcon("eraser.png"));
		eraser.setToolTipText("eraser");
		eraser.addActionListener(this);
		
		colorButton = new JButton();
		colorButton.setIcon(iconOfColor(paintCanvas.activeColor, iconSize));
		colorButton.addActionListener(this);
		

		mode = new JButton();
		mode.setIcon(new ImageIcon("pencil.png"));
		mode.addActionListener(this);
		

		//MANIP PANEL (probably not neccessary anymore?)
		//ManipPanel mp = new ManipPanel();	
		//So that JToggleButtons can only have 1 active at a time
		tools.add(pencil);
		tools.add(eraser);
		
		JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.add(pencil);
		toolBar.add(eraser);
		toolBar.add(colorButton);
		for(JPanel p: panelHolder){
			p = new JPanel();
			toolBar.add(p);
		}
		toolBar.add(mode,BorderLayout.SOUTH);
		//toolBar.add(mp);
		
		return toolBar;
	}
	
	private static ImageIcon iconOfColor(Color c, int size){
		BufferedImage img = new BufferedImage (size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) img.getGraphics();
		g2d.setColor(c);
		g2d.fillOval(0, 0, size, size);
		
		ImageIcon imic = new ImageIcon(img);
		return imic;
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		
		if (s == pencil){
			//set tool as pencil
		}
		else if (s == eraser){
			//set tool as eraser
		}
		else if(s == colorButton){
			Color newColor = JColorChooser.showDialog(mainFrame, "Foreground Color", Color.BLACK);
			if(newColor!=null){
				
			}
		}
		else if(s == mode){
			paintCanvas.setEdit(!paintCanvas.editMode);
			scene.setEditMode(!scene.editMode);
			if(paintCanvas.editMode)
				mode.setIcon(new ImageIcon("pencil.png"));
			else
				mode.setIcon(new ImageIcon("view.png"));
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object s = e.getSource();
		
		if(s == toolSizeSlider){
			int newToolSize = ((JSlider)s).getValue();
			paintCanvas.setToolSize(newToolSize);
			toolSizeLabel.setText("    "+String.valueOf(paintCanvas.activeToolSize));
		}
		
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
