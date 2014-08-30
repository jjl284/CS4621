package egl;

import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetActiveAttrib;
import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import egl.GL.GetProgramParameterName;
import egl.GL.ShaderParameter;
import egl.GL.ShaderType;

public class GLProgram {
	private static final String NON_ALLOWABLE_PREFIX = "gl_";
    private static final Pattern RGX_SEMANTIC = Pattern.compile(
        "(\\w+)\\s*;\\s*//\\s*sem\\s*\\x28\\s*(\\w+)\\s*(\\d+)\\s*\\x29\\s*$",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
        );

    private static GLProgram programInUse;
    public static GLProgram getProgramInUse() {
    	return programInUse;
    }
    public static void Unuse() {
        programInUse = null;
        glUseProgram(0);
    }
    
    private int id, idVS, idFS;
    public int getID() {
    	return id;
    }
    
    public boolean getIsCreated() {
        return id != 0;
    }
    private boolean isLinked;
    public boolean getIsLinked() {
    	return isLinked;
    }
    public boolean getIsInUse() {
        return programInUse == this;
    }

    public final HashMap<String, Integer> Uniforms = new HashMap<>();
    public final HashMap<String, Integer> Attributes = new HashMap<>();
    public final HashMap<Integer, Integer> SemanticLinks = new HashMap<>();
    private HashMap<String, Integer> foundSemantics;
    
    public GLProgram(boolean init) {
        id = 0;
        idFS = 0;
        idVS = 0;

        if(init) Init();
        isLinked = false;
    }
    public GLProgram() {
    	this(false);
    }
    public void Dispose() {
        if(!getIsCreated()) return;

        if(getIsInUse()) Unuse();
        id = 0;
        if(idVS != 0) {
        	glDetachShader(id, idVS);
            glDeleteShader(idVS);
            idVS = 0;
        }
        if(idFS != 0) {
        	glDetachShader(id, idFS);
            glDeleteShader(idFS);
            idFS = 0;
        }
        glDeleteProgram(id);
    }

    public void Init() {
        if(getIsCreated()) return;
        isLinked = false;
        id = glCreateProgram();
    }

    public void AddShader(int st, String src) throws Exception {
        if(getIsLinked()) throw new Exception("Program Is Already Linked");

        switch(st) {
            case ShaderType.VertexShader:
                if(idVS != 0)
                    throw new Exception("Attempting To Add Another Vertex Shader To Program");
                break;
            case ShaderType.FragmentShader:
                if(idFS != 0)
                    throw new Exception("Attempting To Add Another Fragment Shader To Program");
                break;
            default:
                throw new Exception("Shader Type Is Not Supported");
        }
        int idS = glCreateShader(st);
        glShaderSource(idS, src);
        GLError.Get(st + " Source");
        glCompileShader(idS);
        GLError.Get(st + " Compile");

        // Check Status
        int status = glGetShaderi(idS, ShaderParameter.CompileStatus);
        if(status != 1) {
            glDeleteShader(idS);
            throw new Exception("Shader Had Compilation Errors");
        }

        glAttachShader(id, idS);
        GLError.Get(st + " Attach");

        // If It's A Vertex Shader -> Get Semantics From Source
        if(st == ShaderType.VertexShader) {
            foundSemantics = new HashMap<String, Integer>();
            Matcher mer = RGX_SEMANTIC.matcher(src);
            while(mer.find()) {
            	MatchResult m = mer.toMatchResult();
                String attr = m.group(1);
                int sem;
                switch(m.group(2).toLowerCase()) {
                    case "position": sem = Semantic.Position; break;
                    case "normal": sem = Semantic.Normal; break;
                    case "tangent": sem = Semantic.Tangent; break;
                    case "binormal": sem = Semantic.Binormal; break;
                    case "texcoord": sem = Semantic.TexCoord; break;
                    case "color": sem = Semantic.Color; break;
                    default: sem = Semantic.None; break;
                }
                int index = Integer.parseInt(m.group(3));
                sem |= index;
                foundSemantics.put(attr, sem);
            }
        }

        switch(st) {
            case ShaderType.VertexShader: idVS = idS; break;
            case ShaderType.FragmentShader: idFS = idS; break;
        }
    }
    public void AddShaderFile(int st, String file) throws Exception {
    	File f = new File(file);
        if(!f.exists())
            throw new Exception("Shader File \"" + file + "\" Was Not Found");

        String src = null;
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(f));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        src = fileData.toString();

        AddShader(st, src);
    }

    public void SetAttributes(HashMap<String, Integer> attr) throws Exception {
        if(getIsLinked()) throw new Exception("Program Is Already Linked");
        for(Entry<String, Integer> kvp : attr.entrySet()) {
            // Make Sure It Is A Good Binding
            String name = kvp.getKey();
            if(name.startsWith(NON_ALLOWABLE_PREFIX))
                continue;

            // Check Location
            int loc = kvp.getValue();
            if(loc < 0)
                continue;

            // Place It In
            Attributes.put(name, loc);
            glBindAttribLocation(id, loc, name);
            GLError.Get("Program Attr Bind");
        }
    }
    public boolean Link() {
        if(getIsLinked()) return false;

        glLinkProgram(id);
        GLError.Get("Program Link");
        glValidateProgram(id);
        GLError.Get("Program Validate");

//        glDeleteShader(idVS);
//        idVS = 0;
//        glDeleteShader(idFS);
//        idFS = 0;

        int status = glGetProgrami(id, GetProgramParameterName.LinkStatus);
        isLinked = status == 1;
        return isLinked;
    }
    public void InitAttributes() {
        // How Many Attributes Are In The Program
        int count = glGetProgrami(id, GetProgramParameterName.ActiveAttributes);

        // Necessary Info
        String name;
        int loc;

        // Enumerate Through Attributes
        for(int i = 0; i < count; i++) {
            // Get Uniform Info
            name = glGetActiveAttrib(id, i, 1024);
            loc = glGetAttribLocation(id, name);

            // Get Rid Of System Uniforms
            if(!name.startsWith(NON_ALLOWABLE_PREFIX) && loc > -1)
                Attributes.put(name, loc);
        }

        if(foundSemantics != null) {
            GenerateSemanticBindings(foundSemantics);
            foundSemantics = null;
        }
    }
    public void InitUniforms() {
        // How Many Uniforms Are In The Program
        int count = glGetProgrami(id, GetProgramParameterName.ActiveUniforms);

        // Necessary Info
        String name;
        int loc;

        // Enumerate Through Uniforms
        for(int i = 0; i < count; i++) {
            // Get Uniform Info
        	name = glGetActiveUniform(id, i, 1024);
            loc = glGetUniformLocation(id, name);

            // Get Rid Of System Uniforms
            if(!name.startsWith(NON_ALLOWABLE_PREFIX) && loc > -1)
                Uniforms.put(name, loc);
        }
    }
    
    public void GenerateSemanticBindings(HashMap<String, Integer> dSems) {
        for(Entry<String, Integer> kvp : dSems.entrySet()) {
        	Integer vi = Attributes.get(kvp.getKey());
            if(vi != null)
                SemanticLinks.put(kvp.getValue(), vi);
        }
    }

    public void Use() {
        if(getIsInUse()) return;
        programInUse = this;
        glUseProgram(id);
    }

    public GLProgram QuickCreate(String vsFile, String fsFile, HashMap<String, Integer> attr) {
        Init();
        try {
            AddShaderFile(ShaderType.VertexShader, vsFile);
            AddShaderFile(ShaderType.FragmentShader, fsFile);
            if(attr != null)
                SetAttributes(attr);
            Link();
        }
        catch(Exception e) {
            return this;
        }
        InitAttributes();
        InitUniforms();
        return this;
    }
    public GLProgram QuickCreateSource(String vsSrc, String fsSrc, HashMap<String, Integer> attr) {
        Init();
        try {
            AddShader(ShaderType.VertexShader, vsSrc);
            AddShader(ShaderType.FragmentShader, fsSrc);
            if(attr != null)
                SetAttributes(attr);
            Link();
        }
        catch(Exception e) {
            return this;
        }
        InitAttributes();
        InitUniforms();
        return this;
    }
}
