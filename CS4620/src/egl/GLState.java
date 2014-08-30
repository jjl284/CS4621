package egl;

import static egl.GL.*;
import static org.lwjgl.opengl.GL11.glEnable;

public class GLState {
	public static void EnableTextures() {
        glEnable(EnableCap.Texture1D);
        glEnable(EnableCap.Texture2D);
        glEnable(EnableCap.TextureRectangle);
    }
    public static void EnableBlending() {
    	glEnable(EnableCap.Blend);
    }
    public static void EnableAll() {
        EnableTextures();
        EnableBlending();
        DepthState.Default.Set();
        RasterizerState.CullCounterClockwise.Set();
        BlendState.Additive.Set();
    }
}
