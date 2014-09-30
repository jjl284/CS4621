package cs4620.gl;

import java.io.BufferedReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL20;

import cs4620.common.Material;
import cs4620.common.Material.InputProvider.Type;
import egl.GL;
import egl.GL.TextureTarget;
import egl.GL.TextureUnit;
import egl.GLProgram;
import egl.GLTexture;
import egl.GLUniform;
import egl.IDisposable;
import egl.NativeMem;
import egl.SamplerState;
import egl.ShaderInterface;
import egl.math.Color;
import egl.math.Vector3;
import egl.math.Vector4;
import ext.java.IOUtils;

public class RenderMaterial implements IDisposable {
	private static interface IProvider {
		void set(GLProgram p);
	}
	private static class ColorProvider implements IProvider {
		String uniformName;
		Vector4 color;

		public ColorProvider(String name, Color c) {
			uniformName = "col" + name;
			color = new Vector4(
					c.r() / 255.0f,
					c.g() / 255.0f,
					c.b() / 255.0f,
					c.a() / 255.0f
					);
		}

		@Override
		public void set(GLProgram p) {
			int uniform = p.getUniform(uniformName);
			if(uniform == GL.BadUniformLocation) return;
			GL20.glUniform4f(uniform, color.x, color.y, color.z, color.w);
		}
	}
	private static class TextureProvider implements IProvider {
		GLTexture t;
		int tUnit, unTextureSampler;

		public TextureProvider(String samplerName, GLProgram p, int unit, String texName, RenderEnvironment env) {
			tUnit = TextureUnit.Texture0 + unit;
			unTextureSampler = p.getUniform("tex" + samplerName);
			t = env.textures.get(texName);
			if(t == null) unTextureSampler = GL.BadUniformLocation;
		}

		@Override
		public void set(GLProgram p) {
			if(unTextureSampler == GL.BadUniformLocation) return;
			t.use(tUnit, unTextureSampler);
			SamplerState.LINEAR_CLAMP.set(TextureTarget.Texture2D);
		}
	}

	private static final String ROOT_DIRECTORY = "cs4620/gl/shaders/";
	private static final String PROVIDER_FORMAT_TEXTURE = 
			"uniform sampler2D tex%s;\r\n" + 
					"vec4 get%sColor(vec2 uv) {\r\n" + 
					"  return texture2D(tex%s, uv);\r\n" + 
					"}\r\n";
	private static final String PROVIDER_FORMAT_COLOR = 
			"uniform vec4 col%s;\r\n" + 
					"vec4 get%sColor(vec2 uv) {\r\n" + 
					"  return col%s;\r\n" + 
					"}\r\n";
	private static String getProvider(String provider, String type) {
		return String.format(provider, type, type, type);
	}

	public final GLProgram program = new GLProgram(false);
	public final ShaderInterface shaderInterface = new ShaderInterface(RenderMesh.VERTEX_DECLARATION);
	
	private IProvider pDiffuse = null;
	private IProvider pNormal = null;
	private IProvider pSpecular = null;

	public final Material sceneMaterial;
	
	public int unWorld, unWorldIT, unVP, unLPos, unLIntensity, unLCount;
	private FloatBuffer fbLight = NativeMem.createFloatBuffer(16 * 3);

	public RenderMaterial(Material m) {
		sceneMaterial = m;
	}
	@Override
	public void dispose() {
		program.dispose();
	}
	
	private String readFullResource(String name) {
		BufferedReader reader = IOUtils.openReaderResource(name);
		if(reader == null) return null;
		
		return IOUtils.readFull(reader);
	}
	public void loadShaders(RenderEnvironment env) {
		// Read The Vertex Shader Source
		String vsSrc = readFullResource(ROOT_DIRECTORY + sceneMaterial.materialType + ".vert");
		if(vsSrc == null) {
			vsSrc = readFullResource(ROOT_DIRECTORY + Material.T_AMBIENT + ".vert");
			if(vsSrc == null) throw new RuntimeException("Could Not Load A Vertex Shader");
		}

		// Read The Fragment Shader Source
		String fsSrc = readFullResource(ROOT_DIRECTORY + sceneMaterial.materialType + ".frag");
		if(fsSrc == null) {
			fsSrc = readFullResource(ROOT_DIRECTORY + Material.T_AMBIENT + ".frag");
			if(fsSrc == null) throw new RuntimeException("Could Not Load A Vertex Shader");
		}

		// Add In The Special Providers
		fsSrc = fsSrc.replaceFirst("#version 120", "");
		fsSrc = getProvider(sceneMaterial.inputSpecular.type == Type.TEXTURE ? PROVIDER_FORMAT_TEXTURE : PROVIDER_FORMAT_COLOR, "Specular") + fsSrc;
		fsSrc = getProvider(sceneMaterial.inputNormal.type == Type.TEXTURE ? PROVIDER_FORMAT_TEXTURE : PROVIDER_FORMAT_COLOR, "Normal") + fsSrc;
		fsSrc = getProvider(sceneMaterial.inputDiffuse.type == Type.TEXTURE ? PROVIDER_FORMAT_TEXTURE : PROVIDER_FORMAT_COLOR, "Diffuse") + fsSrc;
		fsSrc = "#version 120\r\n" + fsSrc;
		
		// Create The Program
		program.quickCreateSource(vsSrc, fsSrc, null);
		
		// Create Mappings
		shaderInterface.build(program.semanticLinks);
		unWorld = program.getUniform("mWorld");
		unWorldIT = program.getUniform("mWorldIT"); 
		unVP = program.getUniform("mViewProjection");
		unLCount = program.getUniform("numLights");
		unLPos = program.getUniform("lightPosition");
		unLIntensity = program.getUniform("lightIntensity");
		
		createInputProviders(env);
	}

	public void createInputProviders(RenderEnvironment env) {
		if(sceneMaterial.inputDiffuse.type == Type.TEXTURE)
			pDiffuse = new TextureProvider("Diffuse", program, 0, sceneMaterial.inputDiffuse.texture, env);
		else
			pDiffuse = new ColorProvider("Diffuse", sceneMaterial.inputDiffuse.color);
		
		if(sceneMaterial.inputNormal.type == Type.TEXTURE)
			pNormal = new TextureProvider("Normal", program, 0, sceneMaterial.inputNormal.texture, env);
		else
			pNormal = new ColorProvider("Normal", sceneMaterial.inputNormal.color);
		
		if(sceneMaterial.inputSpecular.type == Type.TEXTURE)
			pSpecular = new TextureProvider("Specular", program, 0, sceneMaterial.inputSpecular.texture, env);
		else
			pSpecular = new ColorProvider("Specular", sceneMaterial.inputSpecular.color);
	}
	
	public void useProviders() {
		pDiffuse.set(program);
		pNormal.set(program);
		pSpecular.set(program);
	}
	public void useObject(RenderObject o) {
		if(unWorld != GL.BadUniformLocation) {
			GLUniform.setST(unWorld, o.mWorldTransform, false);
		}
		if(unWorldIT != GL.BadUniformLocation) {
			GLUniform.setST(unWorldIT, o.mWorldTransformIT, false);
		}
	}
	public void useCamera(RenderCamera c) {
		if(unVP != GL.BadUniformLocation) {
			GLUniform.setST(unVP, c.mViewProjection, false);
		}
	}
	public void useLights(ArrayList<RenderLight> lights, int s, int c) {
		if(unLPos != GL.BadUniformLocation) {
			fbLight.clear();
			Vector3 pos = new Vector3();
			for(int i = 0;i < c;i++) {
				RenderLight rl = lights.get(s + i);
				rl.mWorldTransform.mulPos(pos);
				fbLight.put(pos.x);
				fbLight.put(pos.y);
				fbLight.put(pos.z);
				pos.set(0);
			}
			fbLight.rewind();
			GL20.glUniform3(unLPos, fbLight);
		}
		if(unLIntensity != GL.BadUniformLocation) {
			fbLight.clear();
			for(int i = 0;i < c;i++) {
				RenderLight rl = lights.get(s + i);
				fbLight.put((float)rl.sceneLight.intensity.x);
				fbLight.put((float)rl.sceneLight.intensity.y);
				fbLight.put((float)rl.sceneLight.intensity.z);
			}
			fbLight.rewind();
			GL20.glUniform3(unLIntensity, fbLight);
		}
		if(unLCount != GL.BadUniformLocation) {
			GL20.glUniform1i(unLCount, c);
		}
	}
}
