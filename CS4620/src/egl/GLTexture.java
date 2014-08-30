package egl;

import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage1D;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUniform1i;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL30;

import egl.GL.PixelFormat;
import egl.GL.PixelInternalFormat;
import egl.GL.PixelType;
import egl.GL.TextureParameterName;
import egl.GL.TextureTarget;
import egl.GL.TextureUnit;

public class GLTexture implements IDisposable {
	private static final int[] TEXTURE_TARGETS = {
		TextureTarget.ProxyTexture1D,
		TextureTarget.ProxyTexture1DArray,
		TextureTarget.ProxyTexture2D,
		TextureTarget.ProxyTexture2DArray,
		TextureTarget.ProxyTexture2DMultisample,
		TextureTarget.ProxyTexture2DMultisampleArray,
		TextureTarget.ProxyTexture3D,
		TextureTarget.ProxyTextureCubeMap,
		TextureTarget.ProxyTextureCubeMapArray,
		TextureTarget.ProxyTextureRectangle,
		TextureTarget.Texture1D,
		TextureTarget.Texture1DArray,
		TextureTarget.Texture2D,
		TextureTarget.Texture2DArray,
		TextureTarget.Texture2DMultisample,
		TextureTarget.Texture2DMultisampleArray,
		TextureTarget.Texture3D,
		TextureTarget.TextureBaseLevel,
		TextureTarget.TextureBindingCubeMap,
		TextureTarget.TextureBuffer,
		TextureTarget.TextureCubeMap,
		TextureTarget.TextureCubeMapArray,
		TextureTarget.TextureCubeMapNegativeX,
		TextureTarget.TextureCubeMapNegativeY,
		TextureTarget.TextureCubeMapNegativeZ,
		TextureTarget.TextureCubeMapPositiveX,
		TextureTarget.TextureCubeMapPositiveY,
		TextureTarget.TextureCubeMapPositiveZ,
		TextureTarget.TextureMaxLevel,
		TextureTarget.TextureMaxLod,
		TextureTarget.TextureMinLod,
		TextureTarget.TextureRectangle,
	};
	static class Binding {
        private int Target;
        public GLTexture Current;

        public Binding(int t) {
            Target = t;
            Current = null;
        }
        public void Unbind() {
            Current = null;
            glBindTexture(Target, 0);
        }
    }
	
	private static final HashMap<Integer, Binding> currentBindings;
    static {
        currentBindings = new HashMap<Integer, Binding>();
        for(int bt : TEXTURE_TARGETS) {
            currentBindings.put(bt, new Binding(bt));
        }
    }
    public static void Unbind(int t) {
        currentBindings.get(t).Unbind();
    }
	
    private int id;
    public int getID() {
    	return id;
    }

    public boolean getIsCreated() {
        return id != 0;
    }

    private int[] dimensions;
    public int getWidth() {
        return dimensions[0];
    }
    public int getHeight() {
        return dimensions[1];
    }
    public int getDepth() {
        return dimensions[2];
    }

    private int target;
    public int getTarget() {
        return target;
    }
    private void setTarget(int value) {
        target = value;
        refBind = currentBindings.get(target);
    }
    private Binding refBind;
    public boolean getIsBound() {
        return refBind.Current == this;
    }

    public int InternalFormat;
	
    public GLTexture(int target, boolean init) {
        id = 0;
        dimensions = new int[] { 0, 0, 0 };

        setTarget(target);
        InternalFormat = PixelInternalFormat.Rgba;
        if(init) Init();
    }
    public GLTexture(int target) {
    	this(target, false);
    }
    public GLTexture() {
    	this(TextureTarget.Texture2D);
    }
    @Override
    public void Dispose() {
        if(getIsCreated()) {
            glDeleteTextures(id);
            id = 0;
        }
    }

    public GLTexture Init() {
        if(getIsCreated()) return this;
        id = glGenTextures();
        return this;
    }

    public void Bind() {
        if(!getIsBound()) {
            refBind.Current = this;
            glBindTexture(target, id);
            GLError.Get("Texture Bind");
        }
    }
    public void Unbind() {
        if(getIsBound()) refBind.Unbind();
    }

    public void SetImage(int[] dim, int pixelFormat, int pixelType, ByteBuffer buf, boolean mipMap) throws Exception {
        if(dim == null || dim.length < 1 || dim.length > 3)
            throw new Exception("Dimensions For The Texture Must Be Given (Must Be 1 - 3)");
        System.arraycopy(dim, 0, dimensions, 0, dim.length);

        int dims = 0;
        boolean found = false;
        if(getWidth() > 0) dims++;
        else found = true;
        if(!found && getHeight() > 0) dims++;
        else found = true;
        if(!found && getDepth() > 0) dims++;
        
        Bind();
        switch(dims) {
            case 1:
                if(buf != null) glTexImage1D(target, 0, InternalFormat, getWidth(), 0, pixelFormat, pixelType, buf);
                else  glTexImage1D(target, 0, InternalFormat, getWidth(), 0, pixelFormat, pixelType, 0);
                break;
            case 2:
                if(mipMap)
                    glTexParameteri(target, TextureParameterName.GenerateMipmap, 1);
                if(buf != null) glTexImage2D(target, 0, InternalFormat, getWidth(), getHeight(), 0, pixelFormat, pixelType, buf);
                else glTexImage2D(target, 0, InternalFormat, getWidth(), getHeight(), 0, pixelFormat, pixelType, 0);
                if(mipMap && getTarget() == TextureTarget.Texture2D)
                	GL30.glGenerateMipmap(TextureTarget.Texture2D);
                break;
            case 3:
            	if(buf != null) glTexImage3D(target, 0, InternalFormat, getWidth(), getHeight(), getDepth(), 0, pixelFormat, pixelType, buf);
            	else glTexImage3D(target, 0, InternalFormat, getWidth(), getHeight(), getDepth(), 0, pixelFormat, pixelType, 0);
                break;
            default:
                throw new Exception("Invalid Dimensions For The Texture (Must Be > 0)");
        }
        Unbind();
    }
    public void SetImage(int w, int h, int d, int pixelFormat, int pixelType, ByteBuffer buf, boolean mipMap) {
    	try {
			SetImage(new int[] {w, h, d}, pixelFormat, pixelType, buf, mipMap);
		} 
    	catch (Exception e) {
    		// The Apocalypse Has Begun. Quick... Hide In The Garage!
		}
    }
    public void SetImage(int w, int h, int pixelFormat, int pixelType, ByteBuffer buf, boolean mipMap) {
    	SetImage(w, h, 0, pixelFormat, pixelType, buf, mipMap);
    }
    public void SetImage(int w, int pixelFormat, int pixelType, ByteBuffer buf, boolean mipMap) {
    	SetImage(w, 0, 0, pixelFormat, pixelType, buf, mipMap);
    }

    public void SetImage2D(BufferedImage data, boolean mipMap) throws Exception {
    	int w = data.getWidth();
    	int h = data.getHeight();
    	byte[] pixels = ((DataBufferByte)data.getRaster().getDataBuffer()).getData();
    	for(int i = 0, y = 0;y < h;y++) {
    		for(int x = 0; x < w; x++) {
    			byte buf;
    			buf = pixels[i + 0]; pixels[i + 0] = pixels[i + 3]; pixels[i + 3] = buf;
    			buf = pixels[i + 1]; pixels[i + 1] = pixels[i + 2]; pixels[i + 2] = buf;
    			i += 4;
    		}
    	}
    	ByteBuffer bb = ByteBuffer.allocateDirect(w * h * 4);
    	bb.put(pixels);
    	bb.flip();
        SetImage(new int[] { w, h, 0 }, PixelFormat.Bgra, PixelType.UnsignedByte, bb, mipMap);
    }
    public void SetImage2D(String file, boolean mipMap) throws Exception {
    	BufferedImage image = ImageIO.read(new File(file));
    	SetImage2D(image, mipMap);
    }
    
    public void BindToUnit(int unit) {
        glActiveTexture(unit);
        Bind();
    }
    public void SetUniformSampler(int unit, int unSampler) {
        glUniform1i(unSampler, unit - TextureUnit.Texture0);
    }

    public void Use(int unit, int unSampler) {
        BindToUnit(unit);
        SetUniformSampler(unit, unSampler);
    }
    public void Unuse() {
        Unbind();
    }
}