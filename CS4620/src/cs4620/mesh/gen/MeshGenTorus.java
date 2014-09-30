package cs4620.mesh.gen;

import cs4620.common.BasicType;
import cs4620.mesh.MeshData;
import egl.NativeMem;
import egl.math.Matrix4;
import egl.math.Vector3;

/**
 * Generates A Torus Mesh
 * @author Cristian
 *
 */
public class MeshGenTorus extends MeshGenerator {
	@Override
	public void generate(MeshData outData, MeshGenOptions opt) {
		// Extra Credit, But Not Difficult
		
		// TODO#A1 SOLUTION START

		// Calculate Vertex And Index Count
		int vertsPerRing = opt.divisionsLatitude + 1;
		outData.vertexCount = (opt.divisionsLongitude + 1) * vertsPerRing;
		int tris = opt.divisionsLongitude * opt.divisionsLatitude * 2;
		outData.indexCount = tris * 3;
		
		// Create Storage Spaces
		outData.positions = NativeMem.createFloatBuffer(outData.vertexCount * 3);
		outData.uvs = NativeMem.createFloatBuffer(outData.vertexCount * 2);
		outData.normals = NativeMem.createFloatBuffer(outData.vertexCount * 3);
		outData.indices = NativeMem.createIntBuffer(outData.indexCount);
		
		// Calculate A Ring Of Information
		Vector3[] rPos = new Vector3[opt.divisionsLatitude + 1];
		Vector3[] rNorm = new Vector3[opt.divisionsLatitude + 1];
		for(int pi = 0;pi <= opt.divisionsLatitude;pi++) {
			float pPhi = (float)pi / (float)opt.divisionsLatitude;
			double phi = pPhi * Math.PI * 2.0 + Math.PI;
			
			rPos[pi] = new Vector3(
				0,
				(float)Math.sin(phi) * opt.innerRadius,
				(float)Math.cos(phi) * opt.innerRadius + 1
				);
			rNorm[pi] = new Vector3(
				0,
				(float)Math.sin(phi),
				(float)Math.cos(phi)
				);
		}
		
		// Create The Vertices
		Vector3 v = new Vector3();
		for(int i = 0;i <= opt.divisionsLongitude;i++) {
			// Calculate Rotation Matrix
			float pTheta = (float)i / (float)opt.divisionsLongitude;
			Matrix4 mRot = Matrix4.createRotationY((float)(pTheta * Math.PI * 2.0 + Math.PI));

			// Traverse The Ring While Transforming Initial Data By A Rotation
			for(int pi = 0;pi <= opt.divisionsLatitude;pi++) {
				v.set(rPos[pi]); mRot.mulPos(v);
				outData.positions.put(v.x); outData.positions.put(v.y); outData.positions.put(v.z);
				v.set(rNorm[pi]); mRot.mulDir(v);
				outData.normals.put(v.x); outData.normals.put(v.y); outData.normals.put(v.z);
				outData.uvs.put(pTheta); outData.uvs.put((float)pi / (float)opt.divisionsLatitude);
			}
		}
		
		// Create The Indices
		for(int i = 0;i < opt.divisionsLongitude;i++) {
			int si = i * vertsPerRing;
			for(int pi = 0;pi < opt.divisionsLatitude;pi++) {
				outData.indices.put(si);
				outData.indices.put(si + vertsPerRing);
				outData.indices.put(si + 1);
				outData.indices.put(si + 1);
				outData.indices.put(si + vertsPerRing);
				outData.indices.put(si + vertsPerRing + 1);
				si++;
			}
		}
		
		// #SOLUTION END
	}
	
	@Override
	public BasicType getType() {
		return BasicType.TriangleMesh; // Ray-casting Slightly More Difficult On A Torus 
	}
}
