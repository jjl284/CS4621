package egl;

import egl.GL.VertexAttribPointerType;

public class GLUtil {
	public static int SizeOf(int t) {
        switch(t) {
            case VertexAttribPointerType.UnsignedByte:
            case VertexAttribPointerType.Byte:
                return 1;
            case VertexAttribPointerType.UnsignedShort:
            case VertexAttribPointerType.Short:
            case VertexAttribPointerType.HalfFloat:
                return 2;
            case VertexAttribPointerType.UnsignedInt:
            case VertexAttribPointerType.Int:
            case VertexAttribPointerType.Float:
            case VertexAttribPointerType.Int2101010Rev:
            case VertexAttribPointerType.UnsignedInt2101010Rev:
            case VertexAttribPointerType.Fixed:
                return 4;
            case VertexAttribPointerType.Double:
                return 8;
        }
        return 0;
    }
}