package cs4620.mesh;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MeshData {
	/**
	 * Vector3 Components Representing Vertex Positions (Mandatory)
	 */
	public FloatBuffer positions;
	/**
	 * Normalized Vector3 Components Representing Vertex Normals (Optional)
	 */
	public FloatBuffer normals;
	/**
	 * Vector2 Components With Values In The Domain [0, 1]
	 */
	public FloatBuffer uvs;
	/**
	 * Integer Indices In The Domain [0, Vertex Count)
	 */
	public IntBuffer indices;

	/**
	 * Number Of Vertices Used In The Mesh
	 */
	public int vertexCount;
	/**
	 * Number Of Indices Used In The Mesh (Should Be A Multiple Of 3)
	 */
	public int indexCount;

	public MeshData() {
		positions = null;
		normals = null;
		uvs = null;
		indexCount = vertexCount = 0;
	}

	/**
	 * Checks For Position And Triangulation Information
	 * @return True If This Contains The Mandatory Data For Visualizing A Mesh
	 */
	public boolean hasData() {
		return vertexCount >= 3 && indexCount >= 3 && 
				positions != null && indices != null &&
				positions.capacity() >= (vertexCount * 3) && indices.capacity() >= indexCount;
	}
	/**
	 * Checks For Normals
	 * @return True If This Contains Normals
	 */
	public boolean hasNormals() {
		return normals != null && normals.capacity() >= (vertexCount * 3);
	}
	/**
	 * Checks For Texture Coordinates
	 * @return True If This Contains Texture Coordinates
	 */
	public boolean hasUVs() {
		return uvs != null && uvs.capacity() >= (vertexCount * 2);
	}
}
