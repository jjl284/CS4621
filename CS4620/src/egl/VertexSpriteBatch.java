package egl;

import java.nio.ByteBuffer;

import egl.GL.VertexAttribPointerType;
import egl.math.Color;
import egl.math.Vector2;
import egl.math.Vector3;
import egl.math.Vector4;

public class VertexSpriteBatch implements IVertexType {
	public static final int Size = 40;
	public static final ArrayBind[] Declaration = new ArrayBind[] {
        new ArrayBind(Semantic.Position, VertexAttribPointerType.Float, 3, 0),
        new ArrayBind(Semantic.TexCoord, VertexAttribPointerType.Float, 2, 12),
        new ArrayBind(Semantic.TexCoord | Semantic.Index1, VertexAttribPointerType.Float, 4, 20),
        new ArrayBind(Semantic.Color, VertexAttribPointerType.UnsignedByte, 4, 36, true)
      };
	
	public final Vector3 Position;
    public final Vector2 UV;
    public final Vector4 UVRect;
    public final Color Color;
	
    public VertexSpriteBatch() {
        Position = new Vector3();
        UV = new Vector2();
        UVRect = new Vector4();
        Color = new Color();
    }
    public VertexSpriteBatch(Vector3 p, Vector2 uv, Vector4 uvr, Color c) {
        Position = new Vector3(p);
        UV = new Vector2(uv);
        UVRect = new Vector4(uvr);
        Color = new Color(c);
    }
    
	@Override
	public int getByteSize() {
		return 40;
	}
	@Override
	public void AppendToBuffer(ByteBuffer bb) {
		VertexUtils.AppendToBuffer(bb, Position);
		VertexUtils.AppendToBuffer(bb, UV);
		VertexUtils.AppendToBuffer(bb, UVRect);
		VertexUtils.AppendToBuffer(bb, Color);
	}
	@Override
	public ArrayBind[] getDeclaration() {
		return Declaration;
	}
}
