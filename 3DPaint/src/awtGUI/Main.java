package awtGUI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Container;
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

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JSlider;
import javax.swing.JToolBar;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;


public class Main extends Frame {

	// TODO: implement methods needed for AWTGLCanvas such as add/removeNotify()
	// TODO: Put all global variables here
	
	/** AWT GL Canvas */
	//private AWTGLCanvas awtCanvas;
	
	private static PaintCanvas paintCanvas;
	
	public static int MAIN_WIDTH = 800;
	public static int MAIN_HEIGHT = 600;

	// TODO: Initialize everything here
	public Main() throws LWJGLException {
		//awtCanvas = new AWTGLCanvas();
		//awtCanvas.setSize(MAIN_WIDTH, MAIN_HEIGHT);
		//add(awtCanvas);
		
		PaintCanvas paintCanvas = new PaintCanvas();
		paintCanvas.setVisible(true);
		paintCanvas.setBackground(Color.CYAN);
		paintCanvas.setSize(MAIN_WIDTH, MAIN_HEIGHT);
		add(paintCanvas);
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
	    
	   
	    // Create MenuItems
	    MenuItem mbImp=new MenuItem("Import");
	    MenuItem mbExp=new MenuItem("Export");
	   
	    MenuItem mbBP=new MenuItem("Blinn-Phong");
	    MenuItem mbLamb=new MenuItem("Lambertian");
	    MenuItem mbCT=new MenuItem("Cook-Torrance");

	    MenuItem mbEdit=new MenuItem("Edit");
	    MenuItem mbView=new MenuItem("View");
	   
	    MenuItem cEdit = new CheckboxMenuItem("Edit Bar");
	    MenuItem cColor=  new CheckboxMenuItem("Color Bar");
	    MenuItem cManip = new CheckboxMenuItem("Manipulator Bar");
	    
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
		JRadioButton zoom = new JRadioButton();
		zoom.setText("Zoom");
		JRadioButton rotate = new JRadioButton();
		rotate.setText("Rotate");
		JRadioButton pan = new JRadioButton();
		pan.setText("Pan");
		JToolBar pManip = new JToolBar("Manipulator");
		ButtonGroup manipGroup = new ButtonGroup();
		manipGroup.add(zoom);
		manipGroup.add(rotate);
		manipGroup.add(pan);
		pManip.add(zoom);
		pManip.add(rotate);
		pManip.add(pan);
		add(pManip);
		pManip.setVisible(true);
		
		//EDIT PANEL
		Button bUndo =new Button("undo");
		Button bRedo =new Button("redo");
		Button bIncSize =new Button("Size +");
		Button bdecSize =new Button("Size -");
		JSlider bSize = new JSlider(JSlider.HORIZONTAL,1,50,12);
		bSize.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		JTextField txtSize = new JTextField("12");
		JToolBar editPanel = new JToolBar("Editor Bar");
		editPanel.setLayout(new BoxLayout(editPanel,BoxLayout.X_AXIS));
		editPanel.setBackground(Color.gray);
		
		editPanel.add(bUndo);
		editPanel.add(bRedo);
		editPanel.add(bIncSize);
		editPanel.add(bdecSize);
		editPanel.add(bSize);
		editPanel.add(txtSize);
	    
		add(editPanel);
	    editPanel.setVisible(true);
	    
	    //COLOR PANEL
		JColorChooser pallet = new JColorChooser();
		JToolBar pColor = new JToolBar("Pallet");
		pColor.add(pallet);
		add(pColor);
		pColor.setVisible(true);
		

	}
	
	/**
	 * Start up the application
	 * @param args
	 */
	public static void main(String[] args) throws LWJGLException {
		Main app = new Main();
		app.run();
		app.setVisible(true);
		
		PaintSceneApp sceneApp = new PaintSceneApp(paintCanvas);
		sceneApp.run();
		sceneApp.dispose();
	}

}
