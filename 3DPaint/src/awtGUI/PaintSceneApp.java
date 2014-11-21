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
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

import javax.swing.BoxLayout;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

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
import cs4620.mesh.MeshData;
import cs4620.mesh.gen.MeshGenCube;
import cs4620.mesh.gen.MeshGenCylinder;
import cs4620.mesh.gen.MeshGenOptions;
import cs4620.mesh.gen.MeshGenPlane;
import cs4620.mesh.gen.MeshGenSphere;
import cs4620.mesh.gen.MeshGenTorus;
import cs4620.mesh.gen.MeshGenerator;
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
	public MeshData paintMeshData;
	public String scenePath;
	public String sceneName;
	public String paintTextureName;
	
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
		
		// Create a directory for storing painted meshes
		File f = new File("../PaintedMeshes/");
		f.mkdir();
		scenePath = f.getAbsolutePath()+ "/";
		
		// Set up main frame container
		mainFrame = new Frame();
		mainFrame.setResizable(false);
		mainFrame.setSize(MAIN_WIDTH, MAIN_HEIGHT);
		mainFrame.setTitle("3DPaint Application");
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
		
		createNewScene("Cube"); //Default mesh to load
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
	    Menu mHelp = new Menu("Help");
	    
	    
	    // Create MenuItems for new scenes of Default Meshes
	    Menu mbNew=new Menu("New...");
	    
	    addDefaultMeshMenu(mbNew, "Sphere");
	    addDefaultMeshMenu(mbNew, "Cube");
	    addDefaultMeshMenu(mbNew, "Cylinder");
	    addDefaultMeshMenu(mbNew, "Plane");
	    addDefaultMeshMenu(mbNew, "Torus");
	    
	    
	    // Menu item for new scene from an imported mesh
	    MenuItem newMesh = new MenuItem("Import Mesh...");
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
							mode.setIcon(new ImageIcon("pencil.png"));
							return;
						}
					}
				}
			}
	    });
	    mbNew.add(newMesh);
	    
	    
	    // Specify an image to use for the texture
	    MenuItem mbTex = new MenuItem("Set Texture...");
	    mbTex.addActionListener(new ActionListener() {
	    	@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO: Allow user to select a specific mesh of their choosing
	    		// TODO: Direct user to select a texture size when done selecting mesh
				FileDialog fd = new FileDialog(mainFrame);
				fd.setVisible(true);
				for(File f : fd.getFiles()) {
					String file = f.getAbsolutePath();
					if(file != null) {
						if(scene.textures.get(paintTextureName) != null) {
							scene.removeTexture(paintTextureName);
	
							Texture t = new Texture();
							paintTexture = new PaintTexture(file, scenePath + sceneName + paintTextureName + ".png");
							t.setFile(scenePath + sceneName + paintTextureName + ".png");
							scene.addTexture( new NameBindTexture(paintTextureName, t) );
							
							scene.sendEvent(new SceneReloadEvent(scenePath + sceneName + ".xml"));
							return;
						} else {
							System.out.println("3D PAINT ERROR: CANNOT FIND " + paintTextureName + ".png");
						}
					}
				}
			}
	    });
	    
	    
	    // Import an existing Scene XML file
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
							SceneObject paintedObject = scene.objects.get("PaintedObject");
							
							if(paintedObject != null) {
								String matName = scene.objects.get("PaintedObject").material;
								String texName = scene.materials.get(matName).inputDiffuse.texture;
								String texFileName = scene.textures.get(texName).file;
								paintTexture = new PaintTexture(texFileName);
								mode.setIcon(new ImageIcon("pencil.png"));
								System.out.println("USING IMAGE " + texFileName + " while paintTextureName is " + paintTextureName);
							} else {
								// ERROR: specified XML file is not valid for 3D Paint App
								System.out.println("3D PAINT ERROR: The specified XML file does not contain a PaintedObject and is therefore not compatible with 3D Paint");
							}
							
							if(old != null) old.sendEvent(new SceneReloadEvent(file));
							return;
						}
					}
				}
			}
		});
		
		
		// Export (save) this Scene XML and PaintTexture png
	    //MenuItem mbExp=new MenuItem("Export");
	    //mbExp.addActionListener(new ActionListener(){
		//	@Override
			//public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
			//	System.out.println("EXP clicked");
		//	}});
	   
	    
	    // Add shading menu options
	    MenuItem mbBP=new MenuItem("Blinn-Phong");
	    mbBP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Material mat = new Material();
				mat.setType(Material.T_PHONG);
				scene.materials.get(0);
			}});
	    MenuItem mbLamb=new MenuItem("Lambertian");
	    mbLamb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Material mat = new Material();
				mat.setType(Material.T_LAMBERTIAN);
				scene.addMaterial(new NameBindMaterial("Lambertian", mat));			
			}});
	    MenuItem mbCT=new MenuItem("Cook-Torrance");
	    mbCT.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				Material mat = new Material();
				mat.setType(Material.T_COOKTORRANCE);
				scene.addMaterial(new NameBindMaterial("CookTorrance", mat));			
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
	    
	    MenuItem mCtrl = new MenuItem("Controls");
	    //CheckboxMenuItem cEdit = new CheckboxMenuItem("Edit Bar",true);
	    //CheckboxMenuItem cColor=  new CheckboxMenuItem("Color Bar",true);
	    //CheckboxMenuItem cManip = new CheckboxMenuItem("Manipulator Bar",true);
	    
	    mHelp.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String helpMsg = "W: Zoom in\n" +
						"S: Zoom out\n" +
						"Q: - Z \n" +
						"E: + Z\n" +
						"A: - X\n" +
						"D: + X\n" +
						"Z: - Y\n" +
						"C: + Y\n";
				JOptionPane.showMessageDialog(mainFrame, helpMsg);	
			}});
	    //cEdit.addActionListener(new ToolbarActionListener("Edit Bar", cEdit.getState(), mainFrame));
	    //cColor.addActionListener(new ToolbarActionListener("Color Bar", cColor.getState(), mainFrame));
	    //cManip.addActionListener(new ToolbarActionListener("Manipulator Bar", cManip.getState(), mainFrame));
	    // Attach menu items to menu
	    
	    mFile.add(mbNew);
	    mFile.add(mbTex);
	    mFile.add(mbImp);
	  //  mFile.add(mbExp);
	   
	    // Attach menu items to submenu
	    mShading.add(mbBP);
	    mShading.add(mbLamb);
	    mShading.add(mbCT);
	    mEdit.add(mbUndo);
	    mEdit.add(mbRedo);
	    //mMode.add(mbEdit);
	    //mMode.add(mbView);
	    
	    mHelp.add(mCtrl);
	    //mToolbars.add(cEdit);
	    //mToolbars.add(cColor);
	    //mToolbars.add(cManip);
	    
	    // Attach menu to menu bar
	    menubar.add(mFile);
	    menubar.add(mEdit);
	    menubar.add(mShading);
	    //menubar.add(mMode);
	    menubar.add(mHelp);
	   
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
	
	/** Create a menuItem for the specified default mesh, and add it to the given menu */
	private void addDefaultMeshMenu(Menu m, final String s) {
		MenuItem menuItem = new MenuItem(s);
	    
	    menuItem.addActionListener(new ActionListener() {
	    	@Override
			public void actionPerformed(ActionEvent arg0) {
	    		createNewScene(s);
	    		return;
	    	}
	    });
	    
	    m.add(menuItem);
	}
	
	/** Create a new XML file with the given mesh and default texture, then load the mesh */
	private void createNewScene(String shape) {
		Scene old = scene;
		scene = new Scene();
		
		paintMeshData = new MeshData();
		MeshGenerator meshGen; 
		MeshGenOptions meshGenOpt = new MeshGenOptions();
		mode.setIcon(new ImageIcon("pencil.png"));
		switch(shape) {
			case "Cube":
				meshGenOpt.setDivLatitude(32);
				meshGenOpt.setDivLongitude(32);
				meshGenOpt.setInnerRadius(5);
				meshGen = new MeshGenCube();
				meshGen.generate(paintMeshData, meshGenOpt);
				break;
			case "Cylinder":
				meshGenOpt.setDivLatitude(32);
				meshGenOpt.setDivLongitude(16);
				meshGenOpt.setInnerRadius(1);
				meshGen = new MeshGenCylinder();
				meshGen.generate(paintMeshData, meshGenOpt);
				break;
			case "Sphere":
				meshGenOpt.setDivLatitude(32);
				meshGenOpt.setDivLongitude(32);
				meshGenOpt.setInnerRadius(1);
				meshGen = new MeshGenSphere();
				meshGen.generate(paintMeshData, meshGenOpt);
				break;
			case "Torus":
				meshGenOpt.setDivLatitude(32);
				meshGenOpt.setDivLongitude(32);
				meshGenOpt.setInnerRadius(1);
				meshGen = new MeshGenTorus();
				meshGen.generate(paintMeshData, meshGenOpt);
				break;
			case "Plane":
				meshGenOpt.setDivLatitude(32);
				meshGenOpt.setDivLongitude(32);
				meshGenOpt.setInnerRadius(1);
				meshGen = new MeshGenPlane();
				meshGen.generate(paintMeshData, meshGenOpt);
				break;
			default:
				meshGenOpt.setDivLatitude(32);
				meshGenOpt.setDivLongitude(32);
				meshGenOpt.setInnerRadius(1);
				meshGen = new MeshGenPlane();
				meshGen.generate(paintMeshData, meshGenOpt);
				break;		
		}
		
		sceneName = shape;
		paintTextureName = "PaintedTexture";
		
		Mesh m = new Mesh(); m.setGenerator( meshGen );
		m.generator.generate(paintMeshData, new MeshGenOptions());
		scene.addMesh( new NameBindMesh("Default"+shape, m) );
		
		Texture t = new Texture();
		paintTexture = new PaintTexture(1024, 1024, scenePath+sceneName+paintTextureName+".png");
		t.setFile(scenePath+sceneName+paintTextureName+".png");
		scene.addTexture( new NameBindTexture(paintTextureName, t) );
		
		Material mat = new Material();
		mat.setType(Material.T_AMBIENT);
		InputProvider ip = new InputProvider();
		ip.setTexture(paintTextureName);
		mat.setDiffuse(ip);
		scene.addMaterial(new NameBindMaterial(shape+"Material", mat));
		
		SceneObject o = new SceneObject();
		o.setMaterial(shape+"Material");
		o.setMesh("Default"+shape);
		scene.addObject(new NameBindSceneObject("PaintedObject", o));
		
		SceneCamera cam = new SceneCamera();
		cam.addRotation(new Vector3(-30,45,0));
		cam.addTranslation(new Vector3(1.5f,1.5f,1.5f));
		scene.addObject(new NameBindSceneObject("Camera", cam));
		
		scene.removeTexture("NormalMapped");
		// Attempt to create a new scene file and then reload the display
		try {
			scene.saveData(scenePath+sceneName+".xml");
			if(old!=null) old.sendEvent(new SceneReloadEvent(scenePath+sceneName+".xml"));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
