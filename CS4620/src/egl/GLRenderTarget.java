package egl;

import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;
import egl.GL.DrawBufferMode;
import egl.GL.FramebufferAttachment;
import egl.GL.FramebufferErrorCode;
import egl.GL.FramebufferTarget;
import egl.GL.RenderbufferStorage;
import egl.GL.RenderbufferTarget;
import egl.GL.TextureTarget;

public class GLRenderTarget extends GLTexture {
	private int fb, rb;

    public GLRenderTarget(boolean init) {
    	super(TextureTarget.Texture2D, init);
    }
    public GLRenderTarget() {
    	this(false);
    }

    public void buildRenderTarget() {
        fb = glGenFramebuffers();
        glBindFramebuffer(FramebufferTarget.Framebuffer, fb);

        bind();
        glFramebufferTexture2D(FramebufferTarget.Framebuffer, FramebufferAttachment.ColorAttachment0, getTarget(), getID(), 0);
        unbind();

        rb = glGenRenderbuffers();
        glBindRenderbuffer(RenderbufferTarget.Renderbuffer, rb);
        glRenderbufferStorage(RenderbufferTarget.Renderbuffer, RenderbufferStorage.DepthComponent24, getWidth(), getHeight());
        glFramebufferRenderbuffer(FramebufferTarget.Framebuffer, FramebufferAttachment.DepthAttachment, RenderbufferTarget.Renderbuffer, rb);

        glDrawBuffer(DrawBufferMode.ColorAttachment0);

        int err = glCheckFramebufferStatus(FramebufferTarget.Framebuffer);
        if(err != FramebufferErrorCode.FramebufferComplete)
            return;

        glBindFramebuffer(FramebufferTarget.Framebuffer, 0);
    }

    public void useTarget() {
    	glBindFramebuffer(FramebufferTarget.Framebuffer, fb);
    	glViewport(0, 0, getWidth(), getHeight());
    }
    public static void unuseTarget() {
    	glBindFramebuffer(FramebufferTarget.Framebuffer, 0);
    }
}
