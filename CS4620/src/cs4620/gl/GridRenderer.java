package cs4620.gl;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import egl.ArrayBind;
import egl.BlendState;
import egl.DepthState;
import egl.GL.BufferTarget;
import egl.GL.BufferUsageHint;
import egl.GL.GLType;
import egl.GL.PrimitiveType;
import egl.GLBuffer;
import egl.GLProgram;
import egl.GLUniform;
import egl.IDisposable;
import egl.NativeMem;
import egl.RasterizerState;
import egl.Semantic;
import egl.math.Vector4;

public class GridRenderer implements IDisposable {
	
	private static final int GRID_SIZE = 10;
	private static final Vector4 gridColor = new Vector4(0.5f, 0.5f, 0.5f, 0.5f);
	
	private static final int VERTEX_SIZE = 3 * 4;
	public static final ArrayBind[] VERTEX_DECLARATION = {
		new ArrayBind(Semantic.Position, GLType.Float, 3, 0),
	};
	
	final GLBuffer vBuffer = new GLBuffer(BufferTarget.ArrayBuffer, BufferUsageHint.StaticDraw, false);
	final GLBuffer iBuffer = new GLBuffer(BufferTarget.ElementArrayBuffer, BufferUsageHint.StaticDraw, false);

	GLProgram program = new GLProgram(true);

	public GridRenderer() {
		
		// Grid goes from -GRID_SIZE to GRID_SIZE in x and z.
		// First 10 points are -x, then +x, -z, +z.
		ByteBuffer vbb = NativeMem.createByteBuffer(VERTEX_SIZE * 4 * (2*GRID_SIZE + 1));
		for (int i = 0; i < 2*GRID_SIZE + 1; i++) {
			vbb.putFloat(-GRID_SIZE);
			vbb.putFloat(0);
			vbb.putFloat(i - GRID_SIZE);
		}
		for (int i = 0; i < 2*GRID_SIZE + 1; i++) {
			vbb.putFloat(GRID_SIZE);
			vbb.putFloat(0);
			vbb.putFloat(i - GRID_SIZE);
		}
		for (int i = 0; i < 2*GRID_SIZE + 1; i++) {
			vbb.putFloat(i - GRID_SIZE);
			vbb.putFloat(0);
			vbb.putFloat(-GRID_SIZE);
		}
		for (int i = 0; i < 2*GRID_SIZE + 1; i++) {
			vbb.putFloat(i - GRID_SIZE);
			vbb.putFloat(0);
			vbb.putFloat(GRID_SIZE);
		}
		vbb.flip();
		
		ByteBuffer ibb = NativeMem.createByteBuffer(4 * 4 * (2*GRID_SIZE + 1));
		for (int i = 0; i < 2*GRID_SIZE + 1; i++) {
			ibb.putInt(i);
			ibb.putInt(i + (2*GRID_SIZE + 1));
			ibb.putInt(i + 2 * (2*GRID_SIZE + 1));
			ibb.putInt(i + 3 * (2*GRID_SIZE + 1));
		}
		ibb.flip();
		
		// Send Data To GPU
		vBuffer.init();
		vBuffer.setAsVertexVec3();
		vBuffer.setDataInitial(vbb);
		
		iBuffer.init();
		iBuffer.setAsIndexInt();
		iBuffer.setDataInitial(ibb);
		
		HashMap<String, Integer> attrMap = new HashMap<>();
		attrMap.put("vPos", 0);
		program.quickCreateResource("Grid", "cs4620/gl/Grid.vert", "cs4620/gl/Grid.frag", attrMap);
	}
	
	@Override
	public void dispose() {
		vBuffer.dispose();
		iBuffer.dispose();
		program.dispose();
	}
	
	public void draw(RenderCamera camera) {
		program.use();
		
		GLUniform.setST(program.getUniform("VP"), camera.mViewProjection, false);
		GLUniform.set(program.getUniform("uGridColor"), gridColor);
				
		DepthState.DEFAULT.set();
		BlendState.OPAQUE.set();
		RasterizerState.CULL_CLOCKWISE.set();

		vBuffer.useAsAttrib(0);
		iBuffer.bind();
		GL11.glDrawElements(PrimitiveType.Lines, 4 * (2*GRID_SIZE + 1), GLType.UnsignedInt, 0);
		iBuffer.unbind();

		GLProgram.unuse();
	}
}
