package cs4620.mesh;

import java.util.ArrayList;

import egl.math.Vector3;
import egl.math.Vector3i;

/**
 * Performs Normals Reconstruction Upon A Mesh Of Positions
 * @author Cristian
 *
 */
public class MeshConverter {
	/**
	 * Reconstruct a mesh's normals so that it appears to have sharp creases
	 * @param positions List of positions
	 * @param tris List of triangles (Each is a group of three indices into the positions list)
	 * @return A mesh with all faces separated and normals at vertices that lie normal to faces
	 */
	public static MeshData convertToFaceNormals(ArrayList<Vector3> positions, ArrayList<Vector3i> tris) {
		MeshData data = new MeshData();

		// Notice
		System.out.println("Face normals are not implemented");
		
		// No need to implement this function, not part of the Mesh assignment.
		
		return data;
	}
	/**
	 * Reconstruct a mesh's normals so that it appears to be smooth
	 * @param positions List of positions
	 * @param tris List of triangles (Each is a group of three indices into the positions list)
	 * @return A mesh with normals at vertices
	 */
	public static MeshData convertToVertexNormals(ArrayList<Vector3> positions, ArrayList<Vector3i> tris) {
		MeshData data = new MeshData();

		// TODO#A1: Allocate mesh data and create mesh positions, normals, and indices (Remember to set mesh Vertex/Index counts)
		// Note that the vertex data has been supplied as a list of egl.math.Vector3 objects.  Take a
		// look at that class, which contains methods that are very helpful for performing vector
		// math.
		
		return data;
	}
}
