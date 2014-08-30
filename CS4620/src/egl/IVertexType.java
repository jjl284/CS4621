package egl;

import java.nio.ByteBuffer;

public interface IVertexType {
	int getByteSize();
	void AppendToBuffer(ByteBuffer bb);
	ArrayBind[] getDeclaration();
}
