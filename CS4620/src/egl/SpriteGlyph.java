package egl;


public class SpriteGlyph {
	public GLTexture Texture;
    public float Depth;

    public final VertexSpriteBatch VTL;
    public final VertexSpriteBatch VTR;
    public final VertexSpriteBatch VBL;
    public final VertexSpriteBatch VBR;

    public SpriteGlyph(GLTexture t, float d) {
        Texture = t;
        Depth = d;
        VTL = new VertexSpriteBatch();
        VTR = new VertexSpriteBatch();
        VBL = new VertexSpriteBatch();
        VBR = new VertexSpriteBatch();
    }
}
