package cs4620.splines.form;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import blister.GameScreen;
import blister.GameTime;
import blister.MainGame;
import blister.ScreenState;
import blister.MainGame.WindowResizeArgs;
import blister.input.KeyboardEventDispatcher;
import blister.input.KeyboardKeyEventArgs;
import blister.input.MouseButtonEventArgs;
import blister.input.MouseEventDispatcher;
import blister.input.MouseMoveEventArgs;
import cs4620.common.Mesh;
import cs4620.common.Scene;
import cs4620.common.Scene.NameBindMesh;
import cs4620.common.SceneObject;
import cs4620.common.event.SceneTransformationEvent;
import cs4620.gl.GridRenderer;
import cs4620.gl.RenderCamera;
import cs4620.gl.RenderController;
import cs4620.gl.Renderer;
import cs4620.scene.form.RPMaterialData;
import cs4620.scene.form.RPMeshData;
import cs4620.scene.form.RPTextureData;
import cs4620.splines.BSpline;
import cs4620.splines.SplineApp;
import egl.math.Vector2;
import ext.csharp.ACEventFunc;

public class SplineScreen extends GameScreen {	
	public static final int NUM_PANELS = 3;
	
	public static float tol1 = .1f;
	public static float tol2 = .1f;
	
	static SplinePanel[] panels;
	static SplinePanel selectedPanel;
	
	public MeshGenSweepSpline generator;
	
	Renderer renderer = new Renderer();
	int cameraIndex = 0;
	boolean pick;
	int prevCamScroll = 0;
	boolean wasPickPressedLast = false;
	boolean showGrid = true;

	SplineApp app;
	RPMeshData dataMesh;
	RPMaterialData dataMaterial;
	RPTextureData dataTexture;

	RenderController rController;
	SplineCameraController camController;
	GridRenderer gridRenderer;
	

	@Override
	public int getNext() {
		return getIndex();
	}
	@Override
	protected void setNext(int next) {
	}

	@Override
	public int getPrevious() {
		return -1;
	}
	@Override
	protected void setPrevious(int previous) {
	}

	@Override
	public void build() {
		app = (SplineApp)game;

		renderer = new Renderer();
		panels = new SplinePanel[SplineScreen.NUM_PANELS];
		BSpline closed = new BSpline(app.leftPoints, true, tol1);
		BSpline open = new BSpline(app.centerPoints, false, tol2);
		panels[0] = new TwoDimSplinePanel(0, closed);
		panels[1] = new TwoDimSplinePanel(1, open);
		panels[2] = new SweepSplinePanel(2, closed, open, this);
		
		
		generator = new MeshGenSweepSpline();
		generator.setSplineToSweep(((TwoDimSplinePanel) panels[0]).spline);
		generator.setSplineToSweepAlong(((TwoDimSplinePanel) panels[1]).spline);
		generator.setScale(app.options.getScale());
		
		selectedPanel = null;
	}
	@Override
	public void destroy(GameTime gameTime) {
	}
	
	public void newSweep() {
		Mesh m= new Mesh();
		m.setGenerator(generator);
		app.sweepScene.addMesh(new NameBindMesh("SWEEP", m));// overwrites previous...

		SceneObject root= app.sweepScene.objects.get(Scene.ROOT_NODE_NAME);
		root.setMesh("SWEEP");
		root.setMaterial("Sweep");
		app.sweepScene.sendEvent(new SceneTransformationEvent(root));
	}

	/**
	 * Add Spline Data Hotkeys
	 */
	private final ACEventFunc<KeyboardKeyEventArgs> onKeyPress = new ACEventFunc<KeyboardKeyEventArgs>() {
		@Override
		public void receive(Object sender, KeyboardKeyEventArgs args) {
			switch (args.key) {
			case Keyboard.KEY_G:
				showGrid = !showGrid;
				break;
			case Keyboard.KEY_P:
				newSweep();
				break;
			default:
				break;
			}
		}
	};
	
	private final ACEventFunc<MouseButtonEventArgs> onMousePress = new ACEventFunc<MouseButtonEventArgs>() {
		@Override
		public void receive(Object sender, MouseButtonEventArgs args) {
			int index = (int) Math.floor(Mouse.getX() / SplinePanel.panelWidth);
			if (index >= 0 && index < panels.length && panels[index] != null) {
				if(panels[index] instanceof TwoDimSplinePanel) {
					TwoDimSplinePanel p= (TwoDimSplinePanel)panels[index];
					if (args.button == 1) {
						p.selectWithMouse(Mouse.getX(), Mouse.getY());
						selectedPanel = panels[index];
					} else if (args.button == 2) {
						int selectedIndex = p.getSelectedWithMouseClick(Mouse.getX(), Mouse.getY());
						if (selectedIndex == -1) {
							p.spline.addControlPoint(p.mouseClickToWorldTransform(Mouse.getX(), Mouse.getY()));
						} else {
							p.spline.removeControlPoint(selectedIndex);
						}
					}
					if(ControlFrame.REAL_TIME)
						newSweep();
				} else if(panels[index] instanceof SweepSplinePanel) {
					selectedPanel= panels[index];
					((SweepSplinePanel) selectedPanel).clickStartedHere= true;
				}
				
				
			}
		}
	};
	
	private final ACEventFunc<MouseMoveEventArgs> onMouseMotion = new ACEventFunc<MouseMoveEventArgs>() {
		@Override
		public void receive(Object sender, MouseMoveEventArgs args) {
			if (selectedPanel instanceof TwoDimSplinePanel) {
				((TwoDimSplinePanel)selectedPanel).updateSelectedWithMouse(Mouse.getX(), Mouse.getY());
				if(ControlFrame.REAL_TIME)
					newSweep();
			}
		}
	};

	private final ACEventFunc<MouseButtonEventArgs> onMouseRelease = new ACEventFunc<MouseButtonEventArgs>() {
		@Override
		public void receive(Object sender, MouseButtonEventArgs args) {
			if(selectedPanel instanceof TwoDimSplinePanel) {
				((TwoDimSplinePanel) selectedPanel).unselect();
				selectedPanel = null;
				if(ControlFrame.REAL_TIME)
					newSweep();
			} else if(selectedPanel instanceof SweepSplinePanel) {
				((SweepSplinePanel) selectedPanel).clickStartedHere= false;
				selectedPanel= null;
			}
		}
	};

	@Override
	public void onEntry(GameTime gameTime) {		
		cameraIndex = 0;
		app.OnWindowResize.add(new ACEventFunc<MainGame.WindowResizeArgs>() {
			@Override
			public void receive(Object sender, WindowResizeArgs args) {
				// change spline panels
				int width= Display.getWidth();
				int height= Display.getHeight();
				SplinePanel.resize(width / 3, height);
				
				// change rController env viewport to keep aspect ratio
				rController.env.viewportSize.set(width / 3, height);
				
			}
		});
		
		rController = new RenderController(app.sweepScene, new Vector2(app.getWidth()/NUM_PANELS, app.getHeight()));
		
		renderer.buildPasses(rController.env.root);
		camController = new SplineCameraController(app.sweepScene, rController.env, null, (SweepSplinePanel)panels[2]);
		createCamController();
		gridRenderer = new GridRenderer();

		KeyboardEventDispatcher.OnKeyPressed.add(onKeyPress);
		MouseEventDispatcher.OnMousePress.add(onMousePress);
		MouseEventDispatcher.OnMouseMotion.add(onMouseMotion);
		MouseEventDispatcher.OnMouseRelease.add(onMouseRelease);
		
		wasPickPressedLast = false;
		prevCamScroll = 0;
		
		newSweep();
	}
	
	@Override
	public void onExit(GameTime gameTime) {
		KeyboardEventDispatcher.OnKeyPressed.remove(onKeyPress);
		MouseEventDispatcher.OnMousePress.remove(onMousePress);
		MouseEventDispatcher.OnMouseMotion.remove(onMouseMotion);
		MouseEventDispatcher.OnMouseRelease.remove(onMouseRelease);
		rController.dispose();
	}

	private void createCamController() {
		if(rController.env.cameras.size() > 0) {
			RenderCamera cam = rController.env.cameras.get(cameraIndex);
			camController.camera = cam;
		}
		else {
			camController.camera = null;
		}
	}

	@Override
	public void update(GameTime gameTime) {
		pick = false;
		int curCamScroll = 0;

		if(Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) curCamScroll++;
		if(Keyboard.isKeyDown(Keyboard.KEY_MINUS)) curCamScroll--;
		if(rController.env.cameras.size() != 0 && curCamScroll != 0 && prevCamScroll != curCamScroll) {
			if(curCamScroll < 0) curCamScroll = rController.env.cameras.size() - 1;
			cameraIndex += curCamScroll;
			cameraIndex %= rController.env.cameras.size();
			createCamController();
		}
		prevCamScroll = curCamScroll;

		if(camController.camera != null) {
			camController.update(gameTime.elapsed);
		}

		if(Mouse.isButtonDown(1) || Mouse.isButtonDown(0) && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
			if(!wasPickPressedLast) pick = true;
			wasPickPressedLast = true;
		}
		else wasPickPressedLast = false;

		// View A Different Scene
		if(rController.isNewSceneRequested()) {
			setState(ScreenState.ChangeNext);
		}
	}
	

	@Override
	public void draw(GameTime gameTime) {
		int panelWidth = game.getWidth() / SplineScreen.NUM_PANELS;
		int panelHeight = game.getHeight();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glClearColor(0, 0, 0, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		
		GL11.glOrtho(-panelWidth/2,
				      panelWidth/2,
				     -panelHeight/2,
				      panelHeight/2,
				     -1.0, +1.0);
		
		SplinePanel.resize(panelWidth, panelHeight);
		for (SplinePanel sp : SplineScreen.panels) {
			if (sp != null) {
				sp.draw();
			}
		}
	}
}