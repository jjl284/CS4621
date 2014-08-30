package egl;

import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Queue;

import org.lwjgl.BufferUtils;

import egl.GL.BufferTarget;
import egl.GL.BufferUsageHint;
import egl.GL.GetProgramParameterName;
import egl.GL.PrimitiveType;
import egl.GL.ShaderParameter;
import egl.GL.ShaderType;
import egl.GL.TextureUnit;
import egl.GL.VertexAttribPointerType;
import egl.math.Color;
import egl.math.Matrix4;
import egl.math.Vector2;
import egl.math.Vector4;

public class SpriteBatch {
	public static final int INITIAL_GLYPH_CAP = 32;

	public static int SSMTexture(SpriteGlyph g1, SpriteGlyph g2) {
		return Integer.compare(g1.Texture.getID(), g2.Texture.getID());
	}
	public static int SSMFrontToBack(SpriteGlyph g1, SpriteGlyph g2) {
		return Float.compare(g1.Depth, g2.Depth);
	}
	public static int SSMBackToFront(SpriteGlyph g1, SpriteGlyph g2) {
		return Float.compare(g2.Depth, g1.Depth);
	}

	static class SpriteBatchCall {
		public GLTexture Texture;
		public int Indices;
		public int IndexOffset;

		public SpriteBatchCall(int iOff, GLTexture t, ArrayList<SpriteBatchCall> calls) {
			Texture = t;
			IndexOffset = iOff;
			Indices = 6;
			calls.add(this);
		}

		public SpriteBatchCall Append(SpriteGlyph g, ArrayList<SpriteBatchCall> calls) {
			if(g.Texture != Texture) return new SpriteBatchCall(IndexOffset + Indices, g.Texture, calls);
			else Indices += 6;
			return this;
		}
	}

	private static final String VS_SRC = 
			"#version 130\n" +
			"uniform mat4 World;\n" + 
			"uniform mat4 VP;\n" + 
			"in vec4 vPosition;\n" + 
			"in vec2 vUV;\n" + 
			"in vec4 vUVRect;\n" + 
			"in vec4 vTint;\n" + 
			"out vec2 fUV;\n" + 
			"out vec4 fUVRect;\n" + 
			"out vec4 fTint;\n" + 
			"void main(void) {\n" + 
			"    fTint = vTint;\n" + 
			"    fUV = vUV;\n" + 
			"    fUVRect = vUVRect;\n" + 
    		"    vec4 worldPos = vPosition * World;\n" + 
    		"    gl_Position = worldPos * VP;\n" + 
			"}";
	private static final String FS_SRC = 
			"#version 130\n" +
			"uniform sampler2D SBTex;\n" + 
			"in vec2 fUV;\n" + 
			"in vec4 fUVRect;\n" + 
			"in vec4 fTint;\n" + 
			"out vec4 out_Color;\n" +
			"void main(void) {\n" + 
			"    out_Color = texture(SBTex, (vec2(fract(fUV.x), fract(fUV.y)) * fUVRect.zw) + fUVRect.xy) * fTint;\n" + 
			"}";

	public static final Vector4 FULL_UV_RECT = new Vector4(0, 0, 1, 1);
	public static final Vector2 UV_NO_TILE = new Vector2(1, 1);

	public static Matrix4 CreateCameraFromWindow(float w, float h) {
		w *= 0.5f;
		h *= 0.5f;
		return Matrix4.createScale(1 / w, -1 / h, 1).mul(Matrix4.createTranslation(-1, 1, 0));
	}

	// Glyph Information
	private ArrayList<SpriteGlyph> glyphs;
	private Queue<SpriteGlyph> emptyGlyphs;

	// Render Batches
	private int bufUsage;
	private int vao, vbo, glyphCapacity;
	private ArrayList<SpriteBatchCall> batches;

	// Custom Shader
	private int idProg, idVS, idFS;
	private int unWorld, unVP, unTexture;
	private FloatBuffer fbUniforms;

	public SpriteBatch(boolean isDynamic) {
		bufUsage = isDynamic ? BufferUsageHint.DynamicDraw : BufferUsageHint.StaticDraw;

		InitGL();

		emptyGlyphs = new ArrayDeque<SpriteGlyph>();
		fbUniforms = BufferUtils.createFloatBuffer(16);
	}
	public void InitGL() {
		CreateProgram();
		SearchUniforms();
		CreateVertexArray();


	}
	public void Dispose() {
		glDeleteBuffers(vbo);
		vbo = 0;
		glDeleteVertexArrays(vao);
		vao = 0;

		//      program.Dispose();
		glDetachShader(idProg, idVS);
		glDeleteShader(idVS);
		glDetachShader(idProg, idFS);
		glDeleteShader(idFS);
		glDeleteProgram(idProg);
	}

	private void CreateProgram() {
		// Create The Program
		idProg = glCreateProgram();

		// Make Vertex Shader
		idVS = glCreateShader(ShaderType.VertexShader);
		glShaderSource(idVS, VS_SRC);
		glCompileShader(idVS);
		if(glGetShaderi(idVS, ShaderParameter.CompileStatus) != 1)
			throw new RuntimeException("Vert Shader Had Compilation Errors");
		glAttachShader(idProg, idVS);

		// Make Fragment Shader
		idFS = glCreateShader(ShaderType.FragmentShader);
		glShaderSource(idFS, FS_SRC);
		glCompileShader(idFS);
		if(glGetShaderi(idFS, ShaderParameter.CompileStatus) != 1)
			throw new RuntimeException("Frag Shader Had Compilation Errors");
		glAttachShader(idProg, idFS);

		// Setup Vertex Attribute Locations
		glBindAttribLocation(idProg, 0, "vPosition");
		glBindAttribLocation(idProg, 1, "vTint");
		glBindAttribLocation(idProg, 2, "vUV");
		glBindAttribLocation(idProg, 3, "vUVRect");

		glLinkProgram(idProg);
		glValidateProgram(idProg);
		if(glGetProgrami(idProg, GetProgramParameterName.LinkStatus) != 1)
			throw new RuntimeException("Program Had Compilation Errors");
	}
	private void SearchUniforms() {
		unWorld = glGetUniformLocation(idProg, "World");
		unVP = glGetUniformLocation(idProg, "VP");
		unTexture = glGetUniformLocation(idProg, "SBTex");
	}
	private void CreateVertexArray() {
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		vbo = glGenBuffers();
		glyphCapacity = INITIAL_GLYPH_CAP;
		glBindBuffer(BufferTarget.ArrayBuffer, vbo);
		glBufferData(BufferTarget.ArrayBuffer, (glyphCapacity * 6) * VertexSpriteBatch.Size, bufUsage);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		glVertexAttribPointer(0, 3, VertexAttribPointerType.Float, false, VertexSpriteBatch.Size, 0);
		glVertexAttribPointer(1, 4, VertexAttribPointerType.UnsignedByte, true, VertexSpriteBatch.Size, 36);
		glVertexAttribPointer(2, 2, VertexAttribPointerType.Float, false, VertexSpriteBatch.Size, 12);
		glVertexAttribPointer(3, 4, VertexAttribPointerType.Float, false, VertexSpriteBatch.Size, 20);

		glBindVertexArray(0);

		GLBuffer.Unbind(BufferTarget.ArrayBuffer);    	
	}

	public void Begin() {
		// Only Clear The Glyphs
		glyphs = new ArrayList<SpriteGlyph>();
		batches = new ArrayList<SpriteBatchCall>();
	}

	private SpriteGlyph CreateGlyph(GLTexture t, float d) {
		if(emptyGlyphs.size() > 0) {
			SpriteGlyph g = emptyGlyphs.remove();
			g.Texture = t;
			g.Depth = d;
			return g;
		}
		else {
			return new SpriteGlyph(t, d);
		}
	}
	public void Draw(GLTexture t, Vector4 uvRect, Vector2 uvTiling, Matrix4 mTransform, Color tint, float depth) {
		Vector4 uvr = uvRect != null ? uvRect : FULL_UV_RECT;
		Vector2 uvt = uvTiling != null ? uvTiling : UV_NO_TILE;
		SpriteGlyph g = CreateGlyph(t, depth);

		g.VTL.Position.x = 0;
		g.VTL.Position.y = 0;
		g.VTL.Position.z = depth;
		mTransform.mulPos(g.VTL.Position);
		g.VTL.UV.x = 0;
		g.VTL.UV.y = 0;
		g.VTL.UVRect.set(uvr);
		g.VTL.Color.set(tint);

		g.VTR.Position.x = 1;
		g.VTR.Position.y = 0;
		g.VTR.Position.z = depth;
		mTransform.mulPos(g.VTR.Position);
		g.VTR.UV.x = uvt.x;
		g.VTR.UV.y = 0;
		g.VTR.UVRect.set(uvr);
		g.VTR.Color.set(tint);

		g.VBL.Position.x = 0;
		g.VBL.Position.y = 1;
		g.VBL.Position.z = depth;
		mTransform.mulPos(g.VBL.Position);
		g.VBL.UV.x = 0;
		g.VBL.UV.y = uvt.y;
		g.VBL.UVRect.set(uvr);
		g.VBL.Color.set(tint);

		g.VBR.Position.x = 1;
		g.VBR.Position.y = 1;
		g.VBR.Position.z = depth;
		mTransform.mulPos(g.VBR.Position);
		g.VBR.UV.x = uvt.x;
		g.VBR.UV.y = uvt.y;
		g.VBR.UVRect.set(uvr);
		g.VBR.Color.set(tint);

		glyphs.add(g);
	}
	public void Draw(GLTexture t, Vector4 uvRect, Vector2 uvTiling, Vector2 position, Vector2 offset, Vector2 size, float rotation, Color tint, float depth) {
		Vector4 uvr = uvRect != null ? uvRect : FULL_UV_RECT;
		Vector2 uvt = uvTiling != null ? uvTiling : UV_NO_TILE;
		SpriteGlyph g = CreateGlyph(t, depth);

		float rxx = (float)Math.cos(-rotation);
		float rxy = (float)Math.sin(-rotation);
		float cl = size.x * (-offset.x);
		float cr = size.x * (1 - offset.x);
		float ct = size.y * (-offset.y);
		float cb = size.y * (1 - offset.y);

		g.VTL.Position.x = (cl * rxx) + (ct * rxy) + position.x;
		g.VTL.Position.y = (cl * -rxy) + (ct * rxx) + position.y;
		g.VTL.Position.z = depth;
		g.VTL.UV.x = 0;
		g.VTL.UV.y = 0;
		g.VTL.UVRect.set(uvr);
		g.VTL.Color.set(tint);

		g.VTR.Position.x = (cr * rxx) + (ct * rxy) + position.x;
		g.VTR.Position.y = (cr * -rxy) + (ct * rxx) + position.y;
		g.VTR.Position.z = depth;
		g.VTR.UV.x = uvt.x;
		g.VTR.UV.y = 0;
		g.VTR.UVRect.set(uvr);
		g.VTR.Color.set(tint);

		g.VBL.Position.x = (cl * rxx) + (cb * rxy) + position.x;
		g.VBL.Position.y = (cl * -rxy) + (cb * rxx) + position.y;
		g.VBL.Position.z = depth;
		g.VBL.UV.x = 0;
		g.VBL.UV.y = uvt.y;
		g.VBL.UVRect.set(uvr);
		g.VBL.Color.set(tint);

		g.VBR.Position.x = (cr * rxx) + (cb * rxy) + position.x;
		g.VBR.Position.y = (cr * -rxy) + (cb * rxx) + position.y;
		g.VBR.Position.z = depth;
		g.VBR.UV.x = uvt.x;
		g.VBR.UV.y = uvt.y;
		g.VBR.UVRect.set(uvr);
		g.VBR.Color.set(tint);

		glyphs.add(g);
	}
	public void Draw(GLTexture t, Vector4 uvRect, Vector2 uvTiling, Vector2 position, Vector2 offset, Vector2 size, Color tint, float depth) {
		Vector4 uvr = uvRect != null ? uvRect : FULL_UV_RECT;
		Vector2 uvt = uvTiling != null ? uvTiling : UV_NO_TILE;
		SpriteGlyph g = CreateGlyph(t, depth);

		float cl = size.x * (-offset.x);
		float cr = size.x * (1 - offset.x);
		float ct = size.y * (-offset.y);
		float cb = size.y * (1 - offset.y);

		g.VTL.Position.x = cl + position.x;
		g.VTL.Position.y = ct + position.y;
		g.VTL.Position.z = depth;
		g.VTL.UV.x = 0;
		g.VTL.UV.y = 0;
		g.VTL.UVRect.set(uvr);
		g.VTL.Color.set(tint);

		g.VTR.Position.x = cr + position.x;
		g.VTR.Position.y = ct + position.y;
		g.VTR.Position.z = depth;
		g.VTR.UV.x = uvt.x;
		g.VTR.UV.y = 0;
		g.VTR.UVRect.set(uvr);
		g.VTR.Color.set(tint);

		g.VBL.Position.x = cl + position.x;
		g.VBL.Position.y = cb + position.y;
		g.VBL.Position.z = depth;
		g.VBL.UV.x = 0;
		g.VBL.UV.y = uvt.y;
		g.VBL.UVRect.set(uvr);
		g.VBL.Color.set(tint);

		g.VBR.Position.x = cr + position.x;
		g.VBR.Position.y = cb + position.y;
		g.VBR.Position.z = depth;
		g.VBR.UV.x = uvt.x;
		g.VBR.UV.y = uvt.y;
		g.VBR.UVRect.set(uvr);
		g.VBR.Color.set(tint);

		glyphs.add(g);
	}
	public void Draw(GLTexture t, Vector4 uvRect, Vector2 uvTiling, Vector2 position, Vector2 size, Color tint, float depth) {
		Vector4 uvr = uvRect != null ? uvRect : FULL_UV_RECT;
		Vector2 uvt = uvTiling != null ? uvTiling : UV_NO_TILE;
		SpriteGlyph g = CreateGlyph(t, depth);

		g.VTL.Position.x = position.x;
		g.VTL.Position.y = position.y;
		g.VTL.Position.z = depth;
		g.VTL.UV.x = 0;
		g.VTL.UV.y = 0;
		g.VTL.UVRect.set(uvr);
		g.VTL.Color.set(tint);

		g.VTR.Position.x = size.x + position.x;
		g.VTR.Position.y = position.y;
		g.VTR.Position.z = depth;
		g.VTR.UV.x = uvt.x;
		g.VTR.UV.y = 0;
		g.VTR.UVRect.set(uvr);
		g.VTR.Color.set(tint);

		g.VBL.Position.x = position.x;
		g.VBL.Position.y = size.y + position.y;
		g.VBL.Position.z = depth;
		g.VBL.UV.x = 0;
		g.VBL.UV.y = uvt.y;
		g.VBL.UVRect.set(uvr);
		g.VBL.Color.set(tint);

		g.VBR.Position.x = size.x + position.x;
		g.VBR.Position.y = size.y + position.y;
		g.VBR.Position.z = depth;
		g.VBR.UV.x = uvt.x;
		g.VBR.UV.y = uvt.y;
		g.VBR.UVRect.set(uvr);
		g.VBR.Color.set(tint);

		glyphs.add(g);
	}
	public void Draw(GLTexture t, Vector4 uvRect, Vector2 position, Vector2 size, Color tint, float depth) {
		Vector4 uvr = uvRect != null ? uvRect : FULL_UV_RECT;
		SpriteGlyph g = CreateGlyph(t, depth);

		g.VTL.Position.x = position.x;
		g.VTL.Position.y = position.y;
		g.VTL.Position.z = depth;
		g.VTL.UV.x = 0;
		g.VTL.UV.y = 0;
		g.VTL.UVRect.set(uvr);
		g.VTL.Color.set(tint);

		g.VTR.Position.x = size.x + position.x;
		g.VTR.Position.y = position.y;
		g.VTR.Position.z = depth;
		g.VTR.UV.x = 1;
		g.VTR.UV.y = 0;
		g.VTR.UVRect.set(uvr);
		g.VTR.Color.set(tint);

		g.VBL.Position.x = position.x;
		g.VBL.Position.y = size.y + position.y;
		g.VBL.Position.z = depth;
		g.VBL.UV.x = 0;
		g.VBL.UV.y = 1;
		g.VBL.UVRect.set(uvr);
		g.VBL.Color.set(tint);

		g.VBR.Position.x = size.x + position.x;
		g.VBR.Position.y = size.y + position.y;
		g.VBR.Position.z = depth;
		g.VBR.UV.x = 1;
		g.VBR.UV.y = 1;
		g.VBR.UVRect.set(uvr);
		g.VBR.Color.set(tint);

		glyphs.add(g);
	}
	public void Draw(GLTexture t, Vector2 position, Vector2 size, Color tint, float depth) {
		SpriteGlyph g = CreateGlyph(t, depth);

		g.VTL.Position.x = position.x;
		g.VTL.Position.y = position.y;
		g.VTL.Position.z = depth;
		g.VTL.UV.x = 0;
		g.VTL.UV.y = 0;
		g.VTL.UVRect.set(FULL_UV_RECT);
		g.VTL.Color.set(tint);

		g.VTR.Position.x = size.x + position.x;
		g.VTR.Position.y = position.y;
		g.VTR.Position.z = depth;
		g.VTR.UV.x = 1;
		g.VTR.UV.y = 0;
		g.VTR.UVRect.set(FULL_UV_RECT);
		g.VTR.Color.set(tint);

		g.VBL.Position.x = position.x;
		g.VBL.Position.y = size.y + position.y;
		g.VBL.Position.z = depth;
		g.VBL.UV.x = 0;
		g.VBL.UV.y = 1;
		g.VBL.UVRect.set(FULL_UV_RECT);
		g.VBL.Color.set(tint);

		g.VBR.Position.x = size.x + position.x;
		g.VBR.Position.y = size.y + position.y;
		g.VBR.Position.z = depth;
		g.VBR.UV.x = 1;
		g.VBR.UV.y = 1;
		g.VBR.UVRect.set(FULL_UV_RECT);
		g.VBR.Color.set(tint);

		glyphs.add(g);
	}

	// TODOX: Finish The Fight
	//    public void DrawString(SpriteFont font, String s, Vector2 position, Vector2 scaling, Color tint, float depth) {
		//        if(s == null) s = "";
		//        font.Draw(this, s, position, scaling, tint, depth);
		//    }
	//    public void DrawString(SpriteFont font, String s, Vector2 position, float desiredHeight, float scaleX, Color tint, float depth) {
		//        if(s == null) s = "";
		//        Vector2 scaling = new Vector2(desiredHeight / font.getFontHeight());
		//        scaling.x *= scaleX;
		//        font.Draw(this, s, position, scaling, tint, depth);
		//    }
	
	private void SortGlyphs(int spriteSortMode) {
		if(glyphs.size() < 1) return;
		switch(spriteSortMode) {
		case SpriteSortMode.Texture:
			Collections.sort(glyphs, new Comparator<SpriteGlyph>() {
				@Override
				public int compare(SpriteGlyph o1, SpriteGlyph o2) {
					return SSMTexture(o1, o2);
				}
			});
			break;
		case SpriteSortMode.FrontToBack:
			Collections.sort(glyphs, new Comparator<SpriteGlyph>() {
				@Override
				public int compare(SpriteGlyph o1, SpriteGlyph o2) {
					return SSMFrontToBack(o1, o2);
				}
			});
			break;
		case SpriteSortMode.BackToFront:
			Collections.sort(glyphs, new Comparator<SpriteGlyph>() {
				@Override
				public int compare(SpriteGlyph o1, SpriteGlyph o2) {
					return SSMBackToFront(o1, o2);
				}
			});
			break;
		default:
			break;
		}
	}
	private void GenerateBatches() {
		if(glyphs.size() < 1) return;

		// Create Arrays
		ByteBuffer bb = BufferUtils.createByteBuffer(6 * glyphs.size() * VertexSpriteBatch.Size);
		SpriteBatchCall call = new SpriteBatchCall(0, glyphs.get(0).Texture, batches);
		glyphs.get(0).VTL.AppendToBuffer(bb);
		glyphs.get(0).VTR.AppendToBuffer(bb);
		glyphs.get(0).VBL.AppendToBuffer(bb);
		glyphs.get(0).VBL.AppendToBuffer(bb);
		glyphs.get(0).VTR.AppendToBuffer(bb);
		glyphs.get(0).VBR.AppendToBuffer(bb);
		emptyGlyphs.add(glyphs.get(0));
		
		int gc = glyphs.size();
		for(int i = 1; i < gc; i++) {
			SpriteGlyph glyph = glyphs.get(i);
			call = call.Append(glyph, batches);
			glyph.VTL.AppendToBuffer(bb);
			glyph.VTR.AppendToBuffer(bb);
			glyph.VBL.AppendToBuffer(bb);
			glyph.VBL.AppendToBuffer(bb);
			glyph.VTR.AppendToBuffer(bb);
			glyph.VBR.AppendToBuffer(bb);
			emptyGlyphs.add(glyphs.get(i));
		}
		bb.flip();
		glyphs = null;

		// Set The Buffer Data
		glBindBuffer(BufferTarget.ArrayBuffer, vbo);
		if(gc > glyphCapacity) {
			glyphCapacity = gc * 2;
			glBufferData(
					BufferTarget.ArrayBuffer,
					(glyphCapacity * 6) * VertexSpriteBatch.Size,
					bufUsage
					);
		}
		glBufferSubData(BufferTarget.ArrayBuffer, 0, bb);
		GLBuffer.Unbind(BufferTarget.ArrayBuffer);
	}
	public void End(int spriteSortMode) {
		SortGlyphs(spriteSortMode);
		GenerateBatches();
	}

	public void RenderBatch(Matrix4 mWorld, Matrix4 mCamera, BlendState bs, SamplerState ss, DepthState ds, RasterizerState rs) {
		// Set Up Render State
		if(bs == null) bs = BlendState.PremultipliedAlphaBlend;
		if(ds == null) ds = DepthState.None;
		if(rs == null) rs = RasterizerState.CullNone;
		if(ss == null) ss = SamplerState.LinearWrap;
		bs.Set();
		ds.Set();
		rs.Set();

		// Setup The Program
		glUseProgram(idProg);

		// Set Up The Matrices
		fbUniforms.position(0);
		fbUniforms.put(mWorld.m);
		fbUniforms.flip();
		glUniformMatrix4(unWorld, true, fbUniforms);
		fbUniforms.position(0);
		fbUniforms.put(mCamera.m);
		fbUniforms.flip();
		glUniformMatrix4(unVP, true, fbUniforms);

		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glEnableVertexAttribArray(3);
		
		// Draw All The Batches
		int bc = batches.size();
		for(int i = 0; i < bc; i++) {
			SpriteBatchCall batch = batches.get(i);
			batch.Texture.Use(TextureUnit.Texture0, unTexture);
			ss.Set(batch.Texture.getTarget());
			glDrawArrays(PrimitiveType.Triangles, batch.IndexOffset, batch.Indices);
			batch.Texture.Unuse();
		}

		GLProgram.Unuse();
		glBindVertexArray(0);
	}
}
