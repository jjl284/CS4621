package egl;

import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL20.glBlendEquationSeparate;
import egl.GL.BlendEquationMode;
import egl.GL.BlendingFactorDest;
import egl.GL.BlendingFactorSrc;

public class BlendState {
	/**
	 * Fully Opaque Blending With No Usage Of Alpha Value:
	 * Pixel = Src
	 */
	public static final BlendState Opaque = new BlendState(
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.One,
        BlendingFactorDest.Zero,
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.One,
        BlendingFactorDest.Zero
    );
    /**
     * Usage Of Alpha For Both Pixels:
     * Pixel = Src * Alpha + Dest * (1 - Alpha)
     */
	public static final BlendState AlphaBlend = new BlendState(
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.SrcAlpha,
        BlendingFactorDest.OneMinusSrcAlpha,
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.One,
        BlendingFactorDest.Zero
    );
    /**
     * Usage Of Alpha On The Destination Pixel:
     * Pixel = Src + Dest * (1 - Alpha)
     */
	public static final BlendState PremultipliedAlphaBlend = new BlendState(
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.One,
        BlendingFactorDest.OneMinusSrcAlpha,
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.One,
        BlendingFactorDest.Zero
    );
	/**
     * Usage Of Alpha On The Source Pixel:
     * Pixel = Src * Alpha + Dest
     */
    public static final BlendState Additive = new BlendState(
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.SrcAlpha,
        BlendingFactorDest.One,
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.One,
        BlendingFactorDest.Zero
    );
    /**
     * Pure Color Addition:
     * Pixel = Src + Dest
     */
    public static final BlendState PremultipliedAdditive = new BlendState(
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.One,
        BlendingFactorDest.One,
        BlendEquationMode.FuncAdd,
        BlendingFactorSrc.One,
        BlendingFactorDest.Zero
    );

    public int BlendMode;
    public int BlendSrc;
    public int BlendDest;
    public int BlendModeAlpha;
    public int BlendSrcAlpha;
    public int BlendDestAlpha;

    public BlendState(
		int blendEquationMode,
		int blendingFactorSrc,
		int blendingFactorDest,
		int blendEquationModeAlpha,
		int blendingFactorSrcAlpha,
		int blendingFactorDestAlpha
		) {
    	BlendMode = blendEquationMode;
    	BlendSrc = blendingFactorSrc;
    	BlendDest = blendingFactorDest;
    	BlendModeAlpha = blendEquationModeAlpha;
    	BlendSrcAlpha = blendingFactorSrcAlpha;
    	BlendDestAlpha = blendingFactorDestAlpha;
	}

	public void Set() {
        glBlendEquationSeparate(BlendMode, BlendModeAlpha);
        glBlendFuncSeparate(BlendSrc, BlendDest, BlendSrcAlpha, BlendDestAlpha);
    }
}
