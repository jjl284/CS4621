package cs4620.demos;

import blister.MainGame;
import blister.ScreenList;

public class OpenGLDemoApp extends MainGame {

	public OpenGLDemoApp(String title, int w, int h) {
		super(title, w, h);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void buildScreenList() {
		screenList = new ScreenList(this, 3,
			new HelloWorldScreen(), new TwoBoxesScreen(), new ColorBoxesScreen(), new SierpinskiScreen(),
			new AlphaCompositeScreen()
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
	
	@Override
	public void exit() {
		super.exit();
	}

	public static void main(String[] args) {
		OpenGLDemoApp app = new OpenGLDemoApp("OpenGL Demo", 800, 800);
		app.run();
		app.dispose();
	}

}
