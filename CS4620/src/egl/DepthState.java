package egl;

import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import egl.GL.DepthFunction;
import egl.GL.EnableCap;

public class DepthState {
	/**
	 * Do Not Write Depth Or Read Depth To Draw
	 */
	public static final DepthState None = new DepthState(
        false,
        DepthFunction.Always,
        false
    );
	/**
	 * Draw Only When Depth Is Less Than Or Equal To Depth Buffer, Without Overwriting Depth
	 */
	public static final DepthState DepthRead = new DepthState(
        true,
        DepthFunction.Lequal,
        false
    );
	/**
	 * Overwrite The Depth Buffer With Current Depth
	 */
    public static final DepthState DepthWrite = new DepthState(
        false,
        DepthFunction.Always,
        true
    );
    /**
     * Draw Only When Depth Is Less Than Or Equal To Depth Buffer, Overwriting Depth
     */
    public static final DepthState Default = new DepthState(
        true,
        DepthFunction.Lequal,
        true
    );

    public boolean Read;
    public int DepthFunc;
    public boolean Write;

    public DepthState(boolean read, int depthFunction, boolean write) {
    	Read = read;
    	DepthFunc = depthFunction;
    	Write = write;
    }
    
    public void Set() {
        if(Read || Write) {
            glEnable(EnableCap.DepthTest);
            glDepthMask(Write);
            glDepthFunc(DepthFunc);
        }
        else {
            glDisable(EnableCap.DepthTest);
        }
    }
}