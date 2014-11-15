package cs4620.demos;

import org.lwjgl.opengl.GL11;

import egl.BlendState;
import egl.DepthState;
import egl.GL.TextureTarget;
import egl.GLTexture;
import egl.RasterizerState;
import egl.SamplerState;
import egl.SpriteBatch;
import egl.SpriteSortMode;
import egl.math.Color;
import egl.math.Matrix4;
import egl.math.Vector2;
import blister.GameScreen;
import blister.GameTime;
import blister.ScreenState;

public class AlphaCompositeScreen extends GameScreen {
	boolean usePremul = true;
	GLTexture tex, tex2;
	SpriteBatch sb;
	
	@Override
	public int getNext() {
		return -1;
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
	}
	@Override
	public void destroy(GameTime gameTime) {
	}

	@Override
	public void onEntry(GameTime gameTime) {
		sb = new SpriteBatch(true);
		tex = new GLTexture(TextureTarget.Texture2D, true);
		tex2 = new GLTexture(TextureTarget.Texture2D, true);
		try {
			tex.setImage2D("data/textures/Blur.png", false);
			tex2.setImage2D("data/textures/BlurPreMul.png", false);
		} catch (Exception e) {
			e.printStackTrace();
			setState(ScreenState.ChangePrevious);
		}
	}
	@Override
	public void onExit(GameTime gameTime) {
		sb.dispose();
		tex.dispose();
		tex2.dispose();
	}

	@Override
	public void update(GameTime gameTime) {
	}
	@Override
	public void draw(GameTime gameTime) {
		GL11.glClearColor(0, 0, 0, 1);
		GL11.glClearDepth(1.0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		float h = Math.min(game.getWidth(), game.getHeight()) / 2f;
				
		sb.begin();
		sb.draw(tex, new Vector2(0, 0), new Vector2(h), Color.White, 1.0f);
		sb.draw(tex2, new Vector2(0, h), new Vector2(h), Color.White, 1.0f);
		sb.end(SpriteSortMode.None);
		
		Matrix4 mSB = SpriteBatch.createCameraFromWindow(game.getWidth(), game.getHeight());
		sb.renderBatch(new Matrix4(), mSB, BlendState.ALPHA_BLEND, SamplerState.POINT_CLAMP, DepthState.NONE, RasterizerState.CULL_NONE);
		sb.renderBatch(Matrix4.createTranslation(h, 0, 0), mSB, BlendState.PREMULTIPLIED_ALPHA_BLEND, SamplerState.POINT_CLAMP, DepthState.NONE, RasterizerState.CULL_NONE);
	}
}
