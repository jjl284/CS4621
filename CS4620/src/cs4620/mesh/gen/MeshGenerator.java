package cs4620.mesh.gen;

import cs4620.mesh.MeshData;


public abstract class MeshGenerator {
	/**
	 * Generate mesh geometry, writing into the data buffers in a MeshData object
	 * @param outData The object where the newly created buffers will be stored
	 * @param opt {@link MeshGenOptions}
	 */
	public abstract void generate(MeshData outData, MeshGenOptions opt);
}
