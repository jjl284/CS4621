package egl;

import static org.lwjgl.opengl.GL11.glTexParameteri;
import egl.GL.TextureMagFilter;
import egl.GL.TextureMinFilter;
import egl.GL.TextureParameterName;
import egl.GL.TextureWrapMode;

/**
 * OpenGL State For Texture Sampling Methods
 * @author Cristian
 *
 */
public class SamplerState {
    public static final SamplerState LINEAR_CLAMP = new SamplerState(
        TextureMinFilter.Linear,
        TextureMagFilter.Linear,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge
    );
    public static final SamplerState LINEAR_WRAP = new SamplerState(
        TextureMinFilter.Linear,
        TextureMagFilter.Linear,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat
    );
    public static final SamplerState POINT_CLAMP = new SamplerState(
        TextureMinFilter.Nearest,
        TextureMagFilter.Nearest,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge
    );
    public static final SamplerState POINT_WRAP = new SamplerState(
        TextureMinFilter.Nearest,
        TextureMagFilter.Nearest,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat
    );
    public static final SamplerState LINEAR_CLAMP_MM = new SamplerState(
        TextureMinFilter.LinearMipmapLinear,
        TextureMagFilter.Linear,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge
    );
    public static final SamplerState LINEAR_WRAP_MM = new SamplerState(
        TextureMinFilter.LinearMipmapLinear,
        TextureMagFilter.Linear,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat
    );
    public static final SamplerState POINT_CLAMP_MM = new SamplerState(
        TextureMinFilter.NearestMipmapLinear,
        TextureMagFilter.Nearest,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge
    );
    public static final SamplerState POINT_WRAP_MM = new SamplerState(
        TextureMinFilter.NearestMipmapLinear,
        TextureMagFilter.Nearest,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat
    );

    public int minFilter;
    public int magFilter;
    public int wrapS;
    public int wrapT;
    public int wrapR;

    public SamplerState(
		int textureMinFilter,
		int textureMagFilter,
		int textureWrapModeS,
		int textureWrapModeT,
		int textureWrapModeR
		) {
    	minFilter = textureMinFilter;
    	magFilter = textureMagFilter;
    	wrapS = textureWrapModeS;
    	wrapT = textureWrapModeT;
    	wrapR = textureWrapModeR;
	}

	public void set(int target) {
        glTexParameteri(target, TextureParameterName.TextureMagFilter, magFilter);
        glTexParameteri(target, TextureParameterName.TextureMinFilter, minFilter);
        glTexParameteri(target, TextureParameterName.TextureWrapS, wrapS);
        glTexParameteri(target, TextureParameterName.TextureWrapT, wrapT);
        glTexParameteri(target, TextureParameterName.TextureWrapR, wrapR);
    }
}
