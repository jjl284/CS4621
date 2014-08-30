package cs4620.mesh.gen;

/**
 * Struct Holding Options That Govern Mesh Tesselation
 * @author Cristian
 *
 */
public class MeshGenOptions {
	/**
	 * Number Of Times To Cut A Mesh Along The Latitude Lines
	 */
	public int divisionsLatitude;
	/**
	 * Number Of Times To Cut A Mesh Along The Longitude Lines
	 */
	public int divisionsLongitude;
	/**
	 * Extra Value (Used For Torus)
	 */
	public float innerRadius;
}
