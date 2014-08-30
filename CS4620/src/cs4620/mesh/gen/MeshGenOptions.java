package cs4620.mesh.gen;

/**
 * Struct Holding Options That Govern Mesh Tesselation
 * @author Cristian
 *
 */
public class MeshGenOptions {
	/**
	 * Number of times to cut a mesh along the latitude lines
	 */
	public int divisionsLatitude;
	/**
	 * Number of times to cut a mesh along the longitude lines
	 */
	public int divisionsLongitude;
	/**
	 * Extra radius value (used for torus)
	 */
	public float innerRadius;
}
