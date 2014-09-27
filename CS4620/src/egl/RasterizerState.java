package egl;

import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import egl.GL.CullFaceMode;
import egl.GL.EnableCap;
import egl.GL.FrontFaceDirection;

/**
 * OpenGL State For Specifying Triangle Culling And Orientation
 * @author Cristian
 *
 */
public class RasterizerState {
	public static final RasterizerState CULL_NONE = new RasterizerState(
        false,
        CullFaceMode.Back,
        FrontFaceDirection.Ccw
    );
    public static final RasterizerState CULL_CLOCKWISE = new RasterizerState(
        true,
        CullFaceMode.Back,
        FrontFaceDirection.Ccw
    );
    public static final RasterizerState CULL_COUNTER_CLOCKWISE = new RasterizerState(
        true,
        CullFaceMode.Back,
        FrontFaceDirection.Cw
    );

    public boolean useCulling;
    public int cullMode;
    public int faceOrientation;

    public RasterizerState(boolean use, int cullFaceMode, int frontFaceDirection) {
    	useCulling = use;
    	cullMode = cullFaceMode;
    	faceOrientation = frontFaceDirection;
	}

	public void set() {
        if(useCulling) {
            glEnable(EnableCap.CullFace);
            glFrontFace(faceOrientation);
            glCullFace(cullMode);
        }
        else {
            glDisable(EnableCap.CullFace);
        }
    }
}
