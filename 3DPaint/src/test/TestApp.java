package test;

import org.lwjgl.opengl.ContextAttribs;

import blister.MainGame;
import blister.ScreenList;

public class TestApp extends MainGame {

	public TestApp(int w, int h) {
		super("Test", w, h, new ContextAttribs(3, 0), null);
	}

	@Override
	protected void buildScreenList() {
		screenList = new ScreenList(this, 0, new ShadowScreen());
	}

	@Override
	protected void fullInitialize() {
	}

	@Override
	protected void fullLoad() {
	}

	public static void main(String[] args) {
		TestApp app = new TestApp(800, 600);
		app.run();
		app.dispose();
	}
}
