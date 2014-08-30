package egl;

import java.nio.ByteBuffer;

import egl.math.Color;
import egl.math.Vector2;
import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;
import egl.math.Vector4;
import egl.math.Vector4d;

public class VertexUtils {
	public static void AppendToBuffer(ByteBuffer b, byte v) {
		b.put(v);
	}
	public static void AppendToBuffer(ByteBuffer b, short v) {
		b.putShort(v);
	}
	public static void AppendToBuffer(ByteBuffer b, int v) {
		b.putInt(v);
	}
	public static void AppendToBuffer(ByteBuffer b, long v) {
		b.putLong(v);
	}
	public static void AppendToBuffer(ByteBuffer b, float v) {
		b.putFloat(v);
	}
	public static void AppendToBuffer(ByteBuffer b, double v) {
		b.putDouble(v);
	}

	public static void AppendToBuffer(ByteBuffer b, Color v) {
		b.put(v.R);
		b.put(v.G);
		b.put(v.B);
		b.put(v.A);
	}
	
	public static void AppendToBuffer(ByteBuffer b, Vector2 v) {
		b.putFloat(v.x);
		b.putFloat(v.y);
	}
	public static void AppendToBuffer(ByteBuffer b, Vector3 v) {
		b.putFloat(v.x);
		b.putFloat(v.y);
		b.putFloat(v.z);
	}
	public static void AppendToBuffer(ByteBuffer b, Vector4 v) {
		b.putFloat(v.x);
		b.putFloat(v.y);
		b.putFloat(v.z);
		b.putFloat(v.w);
	}
	
	public static void AppendToBuffer(ByteBuffer b, Vector2d v) {
		b.putDouble(v.x);
		b.putDouble(v.y);
	}
	public static void AppendToBuffer(ByteBuffer b, Vector3d v) {
		b.putDouble(v.x);
		b.putDouble(v.y);
		b.putDouble(v.z);
	}
	public static void AppendToBuffer(ByteBuffer b, Vector4d v) {
		b.putDouble(v.x);
		b.putDouble(v.y);
		b.putDouble(v.z);
		b.putDouble(v.w);
	}
}
