package egl;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import egl.GL.BufferTarget;
import egl.GL.VertexAttribPointerType;

public class GLBuffer implements IDisposable {
	/**
	 * Because Java Sucks At Enums
	 */
	private static final int[] BUFFER_TARGETS = {
		BufferTarget.ArrayBuffer,
		BufferTarget.AtomicCounterBuffer,
		BufferTarget.CopyReadBuffer,
		BufferTarget.CopyWriteBuffer,
		BufferTarget.DispatchIndirectBuffer,
		BufferTarget.DrawIndirectBuffer,
		BufferTarget.ElementArrayBuffer,
		BufferTarget.PixelPackBuffer,
		BufferTarget.PixelUnpackBuffer,
		BufferTarget.QueryBuffer,
		BufferTarget.ShaderStorageBuffer,
		BufferTarget.TextureBuffer,
		BufferTarget.TransformFeedbackBuffer,
		BufferTarget.UniformBuffer
	};
	
	/**
	 * Use As Pointer
	 * @author Cristian
	 *
	 */
	static class Binding {
        private int Target;
        public GLBuffer Current;

        public Binding(int t) {
            Target = t;
            Current = null;
        }
        public void Unbind() {
            Current = null;
            glBindBuffer(Target, 0);
        }
    }

	/**
	 * The Bindings To All Of The Buffer Targets
	 */
    private static HashMap<Integer, Binding> currentBindings;
    static {
        currentBindings = new HashMap<Integer, Binding>();
        for(int bt : BUFFER_TARGETS) {
            currentBindings.put(bt, new Binding(bt));
        }
    }
    /**
     * Unbind The Buffer Set To The Current Target Or Clear It
     * When Messing With Wild OpenGL
     * @param t Enum {@link BufferTarget}
     */
    public static void Unbind(int t) {
        currentBindings.get(t).Unbind();
    }

    // OpenGL Buffer ID
    private int id;
    /**
     * Returns 0 If Uninitialized
     * @return OpenGL Buffer ID
     */
    public int getID() {
    	return id;
    }
    /**
     * 
     * @return True If The OpenGL Buffer Is Initialized
     */
    public boolean getIsCreated() {
        return id != 0;
    }

    // Buffer Type And Double Pointer For Checking Usage
    private int target;
    /**
     * @see Enum {@link egl.GL.BufferTarget}
     * @return The OpenGL Buffer Bind Target
     */
    public int getTarget() {
        return target;
    }
    private void setTarget(int value) {
        target = value;
        refBind = currentBindings.get(target);
    }
    private Binding refBind;
    /**
     * 
     * @return True If GLBuffer Recognizes This As The Currently Bound Buffer
     */
    public boolean getIsBound() {
        return refBind != null && refBind.Current == this;
    }

    // Scream At The GPU Where Data Should Be Placed
    private int usageType;
    /**
     * @see Enum {@link egl.GL.BufferUsageHint}
     * @return Data Storage Hints For OpenGL
     */
    public int getUsageType() {
    	return usageType;
    }

    // Element Information
    private int componentFormat, componentCount, elementByteSize;
    /**
     * @see {@link egl.GL.VertexAttribPointerType}
     * @return Format Of A Single Element Of The Buffer
     */
    public int getComponentFormat() {
    	return componentFormat;
    }
    /**
     * 
     * @return Number Of Components In A Full Element
     */
    public int getComponentCount() {
    	return componentCount;
    }
    /**
     * 
     * @return Element Stride In Bytes
     */
    public int getElementByteSize() {
    	return elementByteSize;
    }

    // Buffer Capacity (In Bytes) And Current Elements Count
    private int bufCapacity;
    /**
     * 
     * @return Buffer Capacity On The GPU In Bytes
     */
	public int getBufCapacity() {
    	return bufCapacity;
    }

    /**
     * Default Constructor For A Buffer
     * @param target Buffer Purpose - Enum {@link egl.GL.BufferTarget}
     * @param usage Data Storage Hint - Enum {@link egl.GL.BufferUsageHint}
     * @param init True To Call Init Inside In The Constructor (Requires Active OpenGL Context)
     */
    public GLBuffer(int target, int usage, boolean init) {
        // Default Parameters
        id = 0;

        setTarget(target);
        usageType = usage;

        if(init) Init();
    }
    /**
     * @see {@link #GLBuffer(int, int, boolean) GLBuffer(target, usage, false)}
     */
    public GLBuffer(int target, int usage) {
    	this(target, usage, false);
    }
    /**
     * Destroy The OpenGL Resources Held By The Buffer
     */
    @Override
    public void Dispose() {
        if(!getIsCreated()) return;
        glDeleteBuffers(id);
        id = 0;
    }

    /**
     * Create The OpenGL Buffer Resource In The Active Context
     * @return Self
     */
    public GLBuffer Init() {
        if(getIsCreated()) return this;
        id = glGenBuffers();
        return this;
    }

    /**
     * Set The Element Format For This Buffer
     * @param format Enum {@link egl.GL.VertexAttribPointerType}
     * @param count Number Of Components Of Type Format
     * @return Self
     */
    public GLBuffer SetElementFormat(int format, int count) {
        // Use A New Element Format Basis
        componentFormat = format;
        componentCount = count;
        elementByteSize = componentCount * GLUtil.SizeOf(componentFormat);
        return this;
    }
    /**
     * Turns This Buffer Into An Index Buffer Of 32-Bit Indices
     * @return Self
     */
    public GLBuffer SetAsIndexInt() {
        setTarget(BufferTarget.ElementArrayBuffer);
        return SetElementFormat(VertexAttribPointerType.UnsignedInt, 1);
    }
    /**
     * Turns This Buffer Into An Index Buffer Of 16-Bit Indices
     * @return Self
     */
    public GLBuffer SetAsIndexShort() {
    	setTarget(BufferTarget.ElementArrayBuffer);
        return SetElementFormat(VertexAttribPointerType.UnsignedShort, 1);
    }
    /**
     * Turns This Buffer Into A Vertex Buffer Using A Single 32-Bit Float
     * @return Self
     */
    public GLBuffer SetAsVertexFloat() {
    	setTarget(BufferTarget.ArrayBuffer);
        return SetElementFormat(VertexAttribPointerType.Float, 1);
    }
    /**
     * Turns This Buffer Into A Vertex Buffer Using 2 32-Bit Floats
     * @return Self
     */
    public GLBuffer SetAsVertexVec2() {
    	setTarget(BufferTarget.ArrayBuffer);
        return SetElementFormat(VertexAttribPointerType.Float, 2);
    }
    /**
     * Turns This Buffer Into A Vertex Buffer Using 3 32-Bit Floats
     * @return Self
     */
    public GLBuffer SetAsVertexVec3() {
    	setTarget(BufferTarget.ArrayBuffer);
        return SetElementFormat(VertexAttribPointerType.Float, 3);
    }
    /**
     * Turns This Buffer Into A Vertex Buffer Using 4 32-Bit Floats
     * @return Self
     */
    public GLBuffer SetAsVertexVec4() {
    	setTarget(BufferTarget.ArrayBuffer);
        return SetElementFormat(VertexAttribPointerType.Float, 4);
    }
    /**
     * Turns This Buffer Into A Vertex Buffer Using A Vertex Struct
     * @param vSize Size Of A Vertex Element In Bytes
     * @return Self
     */
    public GLBuffer SetAsVertex(int vSize) {
    	setTarget(BufferTarget.ArrayBuffer);
        return SetElementFormat(VertexAttribPointerType.UnsignedByte, vSize);
    }

    /**
     * Bind This Buffer To It's Target If Not Already Bound
     */
    public void Bind() {
        if(!getIsBound()) {
            refBind.Current = this;
            glBindBuffer(target, id);
            GLError.Get("Buffer Bind");
        }
    }
    /**
     * Unbind This Buffer If It Is Recognized As Being Bound
     */
    public void Unbind() {
        if(getIsBound()) refBind.Unbind();
    }
    /**
     * Use This As A Vertex Buffer With Elements Bound To An Attribute
     * @param loc Attribute Location
     * @param offset Starting Element
     * @param instDiv Instancing Count
     * @param norm True To Normalize Integer Components On The GPU
     */
    public void UseAsAttrib(int loc, int offset, int instDiv, boolean norm) {
        Bind();
        glEnableVertexAttribArray(loc);
        GLError.Get("Enable VAA");
        glVertexAttribPointer(loc, componentCount, componentFormat, norm, elementByteSize, offset * elementByteSize);
        if(instDiv > 0)
            glVertexAttribDivisor(loc, instDiv);
        GLError.Get("VAP");
        Unbind();
    }
    /**
     * @see {@link #UseAsAttrib(int, int, int, boolean) UseAsAttrib(loc, offset, instDiv, false)}
     */
    public void UseAsAttrib(int loc, int offset, int instDiv) {
    	UseAsAttrib(loc, offset, instDiv, false);
    }
    /**
     * @see {@link #UseAsAttrib(int, int, int, boolean) UseAsAttrib(loc, offset, 0, false)}
     */
    public void UseAsAttrib(int loc, int offset) {
    	UseAsAttrib(loc, offset, 0);
    }
   /**
    * @see {@link #UseAsAttrib(int, int, int, boolean) UseAsAttrib(loc, 0, instDiv, false)}
    */
    public void UseAsAttrib(int loc) {
    	UseAsAttrib(loc, 0);
    }
    /**
     * Use This As A Vertex Buffer With Struct Elements Bound To An Interface Of Attributes
     * @param si Interface For Binding Each Element In The Vertex Struct
     * @param offset Starting Element
     * @param instDiv Instancing Count
     */
    public void UseAsAttrib(ShaderInterface si, int offset, int instDiv) {
        Bind();
        // Calculate Stride And Bytes Of Offset
        offset *= elementByteSize;
        for(ArrayBind bind : si.Binds) {
            if(bind.Location < 0) continue;
            glEnableVertexAttribArray(bind.Location);
            GLError.Get("Enable VAA");
            glVertexAttribPointer(bind.Location, bind.CompCount, bind.CompType, bind.Normalized, elementByteSize, offset + bind.Offset);
            if(instDiv > 0)
                glVertexAttribDivisor(bind.Location, instDiv);
            GLError.Get("VAP");
        }
        Unbind();
    }
    /**
     * @see {@link #UseAsAttrib(ShaderInterface, int, int) UseAsAttrib(si, offset, 0)}
     */
    public void UseAsAttrib(ShaderInterface si, int offset) {
    	UseAsAttrib(si, offset, 0);
    }
    /**
     * @see {@link #UseAsAttrib(ShaderInterface, int, int) UseAsAttrib(si, 0, 0)}
     */
    public void UseAsAttrib(ShaderInterface si) {
    	UseAsAttrib(si, 0);
    }

    /**
     * Resize This Buffer (And Discard Buffer Data)
     * @param bytes New Capacity In Bytes
     */
    public void SetSizeInBytes(int bytes) {
        bufCapacity = bytes;
        Bind();
        glBufferData(target, bufCapacity, usageType);
        Unbind();
    }
    /**
     * @see {@link #SetSizeInBytes(int) SetSizeInBytes(elements * getElementByteSize())}
     * @param elements Element Capacity
     */
    public void SetSizeInElements(int elements) {
        SetSizeInBytes(elements * elementByteSize);
    }
    
    public void SetDataInitial(ByteBuffer data) {
        bufCapacity = data.limit();
        Bind();
        glBufferData(target, data, usageType);
        Unbind();
    }
    public void SetDataInitial(ShortBuffer data) {
        bufCapacity = data.limit() << 1;
        Bind();
        glBufferData(target, data, usageType);
        Unbind();
    }
    public void SetDataInitial(IntBuffer data) {
        bufCapacity = data.limit() << 2;
        Bind();
        glBufferData(target, data, usageType);
        Unbind();
    }
    public void SetDataInitial(FloatBuffer data) {
        bufCapacity = data.limit() << 2;
        Bind();
        glBufferData(target, data, usageType);
        Unbind();
    }

    public void CheckResizeInBytes(int bytes) {
        if(bytes <= bufCapacity / 4 || bytes > bufCapacity) {
            // Resize To Double The Desired Size
            SetSizeInBytes(bytes * 2);
        }
    }
    public void CheckResizeInElements(int elements) {
        CheckResizeInBytes(elements * elementByteSize);
    }

    public void SetData(ByteBuffer data, int len, int off) {
        Bind();
        data.limit(off + len);
        glBufferSubData(target, off, data);
        Unbind();
    }
    public void SetData(ByteBuffer data, long off) {
        Bind();
        glBufferSubData(target, off, data);
        Unbind();
    }
    public void SetData(byte[] data, int len, int off) {
    	if(len <= 0) len = data.length - off;
    	SetData(ByteBuffer.wrap(data, off, len), len, 0);
    }
    public void SetData(short[] data, int len, int off) {
    	if(len <= 0) len = data.length - off;
    	ByteBuffer bb = ByteBuffer.allocateDirect(len << 1);
    	bb.asShortBuffer().put(data, off, len);
    	bb.limit(bb.capacity());
    	SetData(bb, bb.capacity(), 0);
    }
    public void SetData(int[] data, int len, int off) {
    	if(len <= 0) len = data.length - off;
    	ByteBuffer bb = ByteBuffer.allocateDirect(len << 2);
    	bb.asIntBuffer().put(data, off, len);
    	bb.limit(bb.capacity());
    	SetData(bb, bb.capacity(), 0);
    }
    public void SetData(long[] data, int len, int off) {
    	if(len <= 0) len = data.length - off;
    	ByteBuffer bb = ByteBuffer.allocateDirect(len << 3);
    	bb.asLongBuffer().put(data, off, len);
    	bb.limit(bb.capacity());
    	SetData(bb, bb.capacity(), 0);
    }
    public void SetData(float[] data, int len, int off) {
    	if(len <= 0) len = data.length - off;
    	ByteBuffer bb = ByteBuffer.allocateDirect(len << 2);
    	bb.asFloatBuffer().put(data, off, len);
    	bb.limit(bb.capacity());
    	SetData(bb, bb.capacity(), 0);
    }
    public void SetData(double[] data, int len, int off) {
    	if(len <= 0) len = data.length - off;
    	ByteBuffer bb = ByteBuffer.allocateDirect(len << 3);
    	bb.asDoubleBuffer().put(data, off, len);
    	bb.limit(bb.capacity());
    	SetData(bb, bb.capacity(), 0);
    }
    public void SetData(IVertexType[] data, int len, int off) {
    	if(len <= 0) len = data.length - off;
    	int vs = data[0].getByteSize();
    	int e = off + len;
    	ByteBuffer bb = ByteBuffer.allocateDirect(len * vs);
    	for(int i = off;i < e;i++) {
    		data[i].AppendToBuffer(bb);
    	}
    	bb.flip();
    	SetData(bb, bb.capacity(), 0);
    }

    public void SmartSetData(ByteBuffer data, int len, int off) {
        CheckResizeInBytes(off + len);
        SetData(data, len, off);
    }
    public void SmartSetData(byte[] data, int len, int off) {
        CheckResizeInBytes(off + len);
        SetData(data, len, off);
    }
    public void SmartSetData(short[] data, int len, int off) {
        CheckResizeInBytes((off + len) << 1);
        SetData(data, len, off);
    }
    public void SmartSetData(int[] data, int len, int off) {
        CheckResizeInBytes((off + len) << 2);
        SetData(data, len, off);
    }
    public void SmartSetData(long[] data, int len, int off) {
        CheckResizeInBytes((off + len) << 3);
        SetData(data, len, off);
    }
    public void SmartSetData(float[] data, int len, int off) {
        CheckResizeInBytes((off + len) << 2);
        SetData(data, len, off);
    }
    public void SmartSetData(double[] data, int len, int off) {
        CheckResizeInBytes((off + len) << 3);
        SetData(data, len, off);
    }
    public void SmartSetData(IVertexType[] data, int len, int off) {
        CheckResizeInBytes((off + len) * data[0].getByteSize());
        SetData(data, len, off);
    }

    public GLBuffer InitAsVertex(float[] data, int vecDim) {
        Init();
        SetElementFormat(VertexAttribPointerType.Float, vecDim);
        setTarget(BufferTarget.ArrayBuffer);
        SmartSetData(data, 0, 0);
        return this;
    }
    public GLBuffer InitAsIndex(int[] data) {
        Init();
        SetAsIndexInt();
        SmartSetData(data, 0, 0);
        return this;
    }
    public GLBuffer InitAsIndex(short[] data) {
        Init();
        SetAsIndexShort();
        SmartSetData(data, 0, 0);
        return this;
    }
}
