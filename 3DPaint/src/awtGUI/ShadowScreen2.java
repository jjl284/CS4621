package awtGUI;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cs4620.gl.GridRenderer;
import cs4620.gl.RenderCamera;
import cs4620.gl.Renderer;
import cs4620.scene.form.RPMaterialData;
import cs4620.scene.form.RPMeshData;
import cs4620.scene.form.RPTextureData;
import cs4620.scene.form.ScenePanel;

import egl.DepthState;
import egl.GL;
import egl.GL.BufferUsageHint;
import egl.GL.GLType;
import egl.GL.PixelFormat;
import egl.GL.PixelInternalFormat;
import egl.GL.PixelType;
import egl.BlendState;
import egl.GL.PrimitiveType;
import egl.GL.TextureUnit;
import egl.GLBuffer;
import egl.GLProgram;
import egl.GLRenderTarget;
import egl.GLTexture;
import egl.GLUniform;
import egl.GL.BufferTarget;
import egl.NativeMem;
import egl.RasterizerState;
import egl.SpriteBatch;
import egl.SpriteSortMode;
import egl.math.Color;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector3;
import blister.GameScreen;
import blister.GameTime;
import blister.ScreenState;

public class ShadowScreen2 extends GameScreen {
	GLProgram progRender;
	Matrix4 mWorld1, mWorld2, mVP;
	GLBuffer vb, ib;

	GLProgram progShadow;
	GLRenderTarget shadowMap;
	Matrix4 mVPShadow;
	
	SpriteBatch sb;
	
	Renderer renderer = new Renderer();
	int cameraIndex = 0;
	boolean pick;
	int prevCamScroll = 0;
	boolean wasPickPressedLast = false;
	boolean showGrid = true;
	
	int textureID;
	
	PaintSceneApp app;
	ScenePanel sceneTree;
	RPMeshData dataMesh;
	RPMaterialData dataMaterial;
	RPTextureData dataTexture;
	
	RenderController rController;
	CameraController camController;
	ManipController manipController;
	GridRenderer gridRenderer;

	@Override
	public int getNext() {
		return 0;
	}
	@Override
	protected void setNext(int next) {
	}

	@Override
	public int getPrevious() {
		return 0;
	}
	@Override
	protected void setPrevious(int previous) {
	}

	@Override
	public void build() {
		app = (PaintSceneApp)game;
		
		renderer = new Renderer();
		
		mWorld1 = Matrix4.createScale(5, 1, 5);
		mWorld2 = Matrix4.createTranslation(0, 2, 0);
		mVP = Matrix4.createLookAt(new Vector3(0, 2.5f, 5), new Vector3(0, 0, 0), new Vector3(0, 1, 0));
		mVP.mulAfter(Matrix4.createPerspectiveFOV(1.0f, (float)game.getWidth() / game.getHeight(), 0.01f, 100f));
		mVPShadow = Matrix4.createLookAt(new Vector3(0, 4, 0), new Vector3(0, 0, 0), new Vector3(0, 0, -1));
		mVPShadow.mulAfter(Matrix4.createPerspectiveFOV(1f, 1.0f, 0.5f, 30f));
	}
	@Override
	public void destroy(GameTime gameTime) {
	}

	@Override
	public void onEntry(GameTime gameTime) {
		
		cameraIndex = 0;
		
		rController = new RenderController(app.scene, new Vector2(app.getWidth(), app.getHeight()));
		renderer.buildPasses(rController.env.root);
		camController = new CameraController(app.scene, rController.env, null);
		camController.givePaintMeshInfo(app.paintMeshData, app.paintTexture);
		createCamController();
		manipController = new ManipController(rController.env, app.scene);
		gridRenderer = new GridRenderer();
		
		//KeyboardEventDispatcher.OnKeyPressed.add(onKeyPress);
		manipController.hook();
		
		//TODO: Set sceneTree, dataMaterial, dataMesh, and dataTexture
		
		wasPickPressedLast = false;
		prevCamScroll = 0;
		
		
		// Create our GL Program objects for use in drawing later
		progRender = new GLProgram(true).quickCreateResource("Render", "test/Render.vert", "test/Render.frag", null);
		progShadow = new GLProgram(true).quickCreateResource("Shadow", "test/Shadow.vert", "test/Shadow.frag", null);
		
		// Create the vertex buffers
		vb = new GLBuffer(BufferTarget.ArrayBuffer, BufferUsageHint.StaticDraw, true);
		vb.setAsVertexVec3();
		//FloatBuffer fbPos = NativeMem.createFloatBuffer(3 * 4);
		//fbPos.put(new float[] { -1, 0, -1, 1, 0, -1, -1, 0, 1, 1, 0, 1 });
		//fbPos.flip();
		vb.setDataInitial(PaintSceneApp.paintMeshData.positions);
		
		// Create the index buffers
		ib = new GLBuffer(BufferTarget.ElementArrayBuffer, BufferUsageHint.StaticDraw, true);
		ib.setAsIndexInt();
		//IntBuffer ibInd = NativeMem.createIntBuffer(3 * 2);
		//ibInd.put(new int[] { 0, 2, 1, 1, 2, 3 });
		//ibInd.flip();
		ib.setDataInitial(PaintSceneApp.paintMeshData.indices);
		
		// Create the Render Target (the thing with the colors to be updated)
		//shadowMap = new GLRenderTarget(true);
		//shadowMap.internalFormat = PixelInternalFormat.Rgba8;//PixelInternalFormat.R32f;
		
		BufferedImage image = PaintSceneApp.paintTexture.image;
		int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer bb = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB
        
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                bb.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                bb.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                bb.put((byte) (pixel & 0xFF));               // Blue component
                bb.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        bb.flip();
		
        textureID = GL11.glGenTextures(); //Generate texture ID
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); //Bind texture ID
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bb);
		//shadowMap.setImage(image.getWidth(), image.getHeight(), PixelFormat.Rgba, PixelType.Float, bb, false);
		//shadowMap.buildRenderTarget();
		
		// Create the sprite batch
		sb = new SpriteBatch(true);
		
		GL11.glClearDepth(1.0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	@Override
	public void onExit(GameTime gameTime) {
		// Remove all the things we created
		rController.dispose();
		manipController.dispose();
		progRender.dispose();
		progShadow.dispose();
		vb.dispose();
		ib.dispose();
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
		// Listen for input and update texture as needed
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
			manipController.checkMouse(Mouse.getX(), Mouse.getY(), camController.camera);
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
		rController.update(renderer, camController);

		if(pick && camController.camera != null) {
			manipController.checkPicking(renderer, camController.camera, Mouse.getX(), Mouse.getY());
		}
		
		Vector3 bg = PaintSceneApp.scene.background;
		GL11.glClearColor(bg.x, bg.y, bg.z, 0);
		GL11.glClearDepth(1.0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		if(camController.camera != null){
			renderer.draw(camController.camera, rController.env.lights);
			manipController.draw(camController.camera);
			if (showGrid)
				gridRenderer.draw(camController.camera);
		}
		
		
		// Move Camera Around
		//mVP.mulBefore(Matrix4.createRotationY(0.01f));
		
		// Shadow Passes
		// Set up the shadowMap Render Target (the minimap)
		//shadowMap.useTarget();
		// Set the clear color to transparent black
		//GL11.glClearColor(0, 0, 0, 0);
		//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//DepthState.DEFAULT.set();
		//RasterizerState.CULL_CLOCKWISE.set();
		//BlendState.OPAQUE.set();
		
		// Get the vertex positions from the shadow.vert and shadow.frag
		progShadow.use();
		GLUniform.setST(progShadow.getUniform("unVP"), mVPShadow, false);
		vb.useAsAttrib(progShadow.getAttribute("vPos"));
		ib.bind();
		
		// Apply shadow for the bottom square, then draw to render texture
		GLUniform.setST(progShadow.getUniform("unWorld"), mWorld1, false);
		GL11.glDrawElements(PrimitiveType.Triangles, 6, GLType.UnsignedInt, 0);

		// Apply shadow for the top square, then draw to render texture
		GLUniform.setST(progShadow.getUniform("unWorld"), mWorld2, false);
		GL11.glDrawElements(PrimitiveType.Triangles, 6, GLType.UnsignedInt, 0);

		//GL11.glTexSubImage2D(shadowMap.getTarget(), 0, 0, 0, 2, 5, PixelFormat.Rgba, PixelType.Float, null);
		
		// Render Passes
		// Now stop drawing to the render target and instead draw to the display
		GLRenderTarget.unuseTarget();
		GL11.glViewport(0, 0, game.getWidth(), game.getHeight());
		//GL11.glClearColor(0, 0, 0, 0);
		//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//DepthState.DEFAULT.set();
		//RasterizerState.CULL_CLOCKWISE.set();
		//BlendState.OPAQUE.set();
		
		// get mesh data from the Render vert and frag data
		progRender.use();
		GLUniform.setST(progRender.getUniform("unVP"), mVP, false);
		GLUniform.setST(progRender.getUniform("unVPShadow"), mVPShadow, false);
		//shadowMap.use(TextureUnit.Texture0, progRender.getUniform("unTexShadow"));
		vb.useAsAttrib(progRender.getAttribute("vPos"));
		ib.bind();
		
		// progRender
		// Draw the bottom square mesh (mWorld1 on mesh data) onto the display
		GLUniform.setST(progRender.getUniform("unWorld"), mWorld1, false);
		GL11.glDrawElements(PrimitiveType.Triangles, 6, GLType.UnsignedInt, 0);

		// Draw the top square mesh (mWorld2 on mesh data) onto the display
		GLUniform.setST(progRender.getUniform("unWorld"), mWorld2, false);
		GL11.glDrawElements(PrimitiveType.Triangles, 6, GLType.UnsignedInt, 0);
		
		// Debug Shadow Map
		// Actually draw the mini map (shadowMap, the Render Target)
		// Set up the sprite batch for rendering
		sb.begin();
		// Draw a gold square to act as a matte/border
		sb.draw(null, new Vector2(0, 0), new Vector2(102, 102), Color.Gold, 0.3f);
		// Draw the minimap on top of the square
		//sb.draw(shadowMap, new Vector2(1, 1), new Vector2(100, 100), Color.White, 0.1f);
		sb.end(SpriteSortMode.None);
		// Render the sprite batch
		sb.renderBatch(new Matrix4(), SpriteBatch.createCameraFromWindow(game.getWidth(), game.getHeight()), BlendState.ALPHA_BLEND, null, null, null);
		
	}
}
