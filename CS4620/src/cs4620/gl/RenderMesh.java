package cs4620.gl;


import java.nio.ByteBuffer;

import cs4620.common.Mesh;
import cs4620.mesh.MeshData;
import egl.ArrayBind;
import egl.GL.BufferTarget;
import egl.GL.BufferUsageHint;
import egl.GL.GLType;
import egl.GLBuffer;
import egl.IDisposable;
import egl.NativeMem;
import egl.Semantic;

public class RenderMesh implements IDisposable {
	private static final int VERTEX_SIZE = 8 * 4;
	public static final ArrayBind[] VERTEX_DECLARATION = {
		new ArrayBind(Semantic.Position, GLType.Float, 3, 0),
		new ArrayBind(Semantic.Normal, GLType.Float, 3, 3 * 4),
		new ArrayBind(Semantic.TexCoord, GLType.Float, 2, 6 * 4)
	};
	
	public final GLBuffer vBuffer = new GLBuffer(BufferTarget.ArrayBuffer, BufferUsageHint.StaticDraw, false);
	public final GLBuffer iBuffer = new GLBuffer(BufferTarget.ElementArrayBuffer, BufferUsageHint.StaticDraw, false);
	public int indexCount;
	
	public final Mesh sceneMesh;
	
	public RenderMesh(Mesh m) {
		sceneMesh = m;
	}
	@Override
	public void dispose() {
		vBuffer.dispose();
		iBuffer.dispose();
	}
	
	public void build(MeshData data) {
		// Interlace The Data
		ByteBuffer bb = NativeMem.createByteBuffer(data.vertexCount * VERTEX_SIZE);
		data.positions.position(0);
		data.positions.limit(data.vertexCount * 3);
		data.normals.position(0);
		data.normals.limit(data.vertexCount * 3);
		data.uvs.position(0);
		data.uvs.limit(data.vertexCount * 2);
		for(int i = 0;i < data.vertexCount;i++) {
			bb.putFloat(data.positions.get());
			bb.putFloat(data.positions.get());
			bb.putFloat(data.positions.get());
			bb.putFloat(data.normals.get());
			bb.putFloat(data.normals.get());
			bb.putFloat(data.normals.get());
			bb.putFloat(data.uvs.get());
			bb.putFloat(data.uvs.get());
		}
		bb.flip();
		
		// Send Data To GPU
		vBuffer.init();
		vBuffer.setAsVertex(VERTEX_SIZE);
		vBuffer.setDataInitial(bb);
		
		iBuffer.init();
		iBuffer.setAsIndexInt();
		data.indices.position(0);
		data.indices.limit(data.indexCount);
		iBuffer.setDataInitial(data.indices);
		
		indexCount = data.indexCount;
	}
}
