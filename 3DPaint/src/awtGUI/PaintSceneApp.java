package awtGUI;

import org.lwjgl.LWJGLException;

import blister.FalseFirstScreen;
import blister.MainGame;
import blister.ScreenList;
import cs4620.common.Scene;
import cs4620.scene.SceneApp;
import cs4620.scene.ViewScreen;
import cs4620.scene.form.ControlWindow;

public class PaintSceneApp extends MainGame {
	
	/**
	 * Scene
	 */
	public Scene scene;
	
	public PaintSceneApp(PaintCanvas canvas) throws LWJGLException {
		super("3D Paint Scene Mesh", Main.MAIN_WIDTH, Main.MAIN_HEIGHT, canvas);
		
		scene = new Scene();
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
		// TODO Auto-generated method stub
		
	}
}
