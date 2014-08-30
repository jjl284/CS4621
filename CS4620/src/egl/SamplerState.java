package egl;

import static org.lwjgl.opengl.GL11.glTexParameteri;
import egl.GL.TextureMagFilter;
import egl.GL.TextureMinFilter;
import egl.GL.TextureParameterName;
import egl.GL.TextureWrapMode;

public class SamplerState {
    public static final SamplerState LinearClamp = new SamplerState(
        TextureMinFilter.Linear,
        TextureMagFilter.Linear,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge
    );
    public static final SamplerState LinearWrap = new SamplerState(
        TextureMinFilter.Linear,
        TextureMagFilter.Linear,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat
    );
    public static final SamplerState PointClamp = new SamplerState(
        TextureMinFilter.Nearest,
        TextureMagFilter.Nearest,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge
    );
    public static final SamplerState PointWrap = new SamplerState(
        TextureMinFilter.Nearest,
        TextureMagFilter.Nearest,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat
    );
    public static final SamplerState LinearClampMM = new SamplerState(
        TextureMinFilter.LinearMipmapLinear,
        TextureMagFilter.Linear,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge
    );
    public static final SamplerState LinearWrapMM = new SamplerState(
        TextureMinFilter.LinearMipmapLinear,
        TextureMagFilter.Linear,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat
    );
    public static final SamplerState PointClampMM = new SamplerState(
        TextureMinFilter.NearestMipmapLinear,
        TextureMagFilter.Nearest,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge,
        TextureWrapMode.ClampToEdge
    );
    public static final SamplerState PointWrapMM = new SamplerState(
        TextureMinFilter.NearestMipmapLinear,
        TextureMagFilter.Nearest,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat,
        TextureWrapMode.Repeat
    );

    public int MinFilter;
    public int MagFilter;
    public int WrapS;
    public int WrapT;
    public int WrapR;

    public SamplerState(
		int textureMinFilter,
		int textureMagFilter,
		int textureWrapModeS,
		int textureWrapModeT,
		int textureWrapModeR
		) {
    	MinFilter = textureMinFilter;
    	MagFilter = textureMagFilter;
    	WrapS = textureWrapModeS;
    	WrapT = textureWrapModeT;
    	WrapR = textureWrapModeR;
	}

	public void Set(int target) {
        glTexParameteri(target, TextureParameterName.TextureMagFilter, MagFilter);
        glTexParameteri(target, TextureParameterName.TextureMinFilter, MinFilter);
        glTexParameteri(target, TextureParameterName.TextureWrapS, WrapS);
        glTexParameteri(target, TextureParameterName.TextureWrapT, WrapT);
        glTexParameteri(target, TextureParameterName.TextureWrapR, WrapR);
    }
}
