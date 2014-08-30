package egl;

import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import egl.GL.CullFaceMode;
import egl.GL.EnableCap;
import egl.GL.FrontFaceDirection;

public class RasterizerState {
	public static final RasterizerState CullNone = new RasterizerState(
        false,
        CullFaceMode.Back,
        FrontFaceDirection.Ccw
    );
    public static final RasterizerState CullClockwise = new RasterizerState(
        true,
        CullFaceMode.Back,
        FrontFaceDirection.Ccw
    );
    public static final RasterizerState CullCounterClockwise = new RasterizerState(
        true,
        CullFaceMode.Back,
        FrontFaceDirection.Cw
    );

    public boolean UseCulling;
    public int CullMode;
    public int FaceOrientation;

    public RasterizerState(boolean use, int cullFaceMode, int frontFaceDirection) {
    	UseCulling = use;
    	CullMode = cullFaceMode;
    	FaceOrientation = frontFaceDirection;
	}

	public void Set() {
        if(UseCulling) {
            glEnable(EnableCap.CullFace);
            glFrontFace(FaceOrientation);
            glCullFace(CullMode);
        }
        else {
            glDisable(EnableCap.CullFace);
        }
    }
}
