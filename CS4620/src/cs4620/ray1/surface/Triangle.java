package cs4620.ray1.surface;

import cs4620.mesh.MeshData;
import cs4620.ray1.IntersectionRecord;
import cs4620.ray1.Ray;
import egl.math.Vector2d;
import egl.math.Vector3;
import egl.math.Vector3d;
import egl.math.Vector3i;
import cs4620.ray1.shader.Shader;

/**
 * Represents a single triangle, part of a triangle mesh
 *
 * @author ags
 */
public class Triangle extends Surface {
  /** The normal vector of this triangle, if vertex normals are not specified */
  Vector3d norm;
  
  /** The mesh that contains this triangle */
  Mesh owner;
  
  MeshData mesh;
  
  /** 3 indices to the vertices of this triangle. */
  Vector3i index;
  
  double a, b, c, d, e, f;
  public Triangle(Mesh owner, Vector3i index, Shader shader) {
    this.owner = owner;
    this.index = new Vector3i(index);
    
    Vector3d v0 = owner.getPosition(index.x);
    Vector3d v1 = owner.getPosition(index.y);
    Vector3d v2 = owner.getPosition(index.z);
    
    if (!owner.hasNormals()) {
    	Vector3d e0 = new Vector3d(), e1 = new Vector3d();
    	e0.set(v1).sub(v0);
    	e1.set(v2).sub(v0);
    	norm = new Vector3d();
    	norm.set(e0).cross(e1);
    }
    a = v0.x-v1.x;
    b = v0.y-v1.y;
    c = v0.z-v1.z;
    
    d = v0.x-v2.x;
    e = v0.y-v2.y;
    f = v0.z-v2.z;
    
    this.setShader(shader);
  }
  
  public Triangle(MeshData mesh, Vector3i index) {
	    this.mesh = mesh;
	    this.index = new Vector3i(index);
	    
	    Vector3d v0 = owner.getPosition(index.x);
	    Vector3d v1 = owner.getPosition(index.y);
	    Vector3d v2 = owner.getPosition(index.z);
	    
	    a = v0.x-v1.x;
	    b = v0.y-v1.y;
	    c = v0.z-v1.z;
	    
	    d = v0.x-v2.x;
	    e = v0.y-v2.y;
	    f = v0.z-v2.z;
  }

  /**
   * Get the position of a vertex.
   * @param index The vertex index.
   * @return The position of the specified vertex.
   */
  public Vector3 getPosition(int index) {
	  return new Vector3(mesh.positions.get(3*index), 
			  mesh.positions.get(3*index+1), 
			  mesh.positions.get(3*index+2));
  }
  
  /**
   * Get the UV-coordinates of a vertex, assuming the coordinates exist.
   * @param index The vertex index.
   * @return The UV-coordinates of the specified vertex.
   */
  public Vector2d getUV(int index) {
	  return new Vector2d(mesh.uvs.get(2*index), 
			  mesh.uvs.get(2*index+1));
  }
  
  /**
   * Tests this surface for intersection with ray. If an intersection is found
   * record is filled out with the information about the intersection and the
   * method returns true. It returns false otherwise and the information in
   * outRecord is not modified.
   *
   * @param outRecord the output IntersectionRecord
   * @param rayIn the ray to intersect
   * @return true if the surface intersects the ray
   */
  public boolean intersect(IntersectionRecord outRecord, Ray rayIn) {
	    // TODO#A2: fill in this function.
	  System.out.println("Using wrong intersect");
	  return false;
  }
  
  public boolean intersect (IntersectionRecord outRecord, Vector3 origin, Vector3 direction) {
		double g = direction.x;
		double h = direction.y;
		double i = direction.z;
		Vector3 v = new Vector3(getPosition(index.x));
		v.sub(origin);
		double j = v.x;
		double k = v.y;
		double l = v.z;
		double M = a * (e * i - h * f) + b * (g * f - d * i) + c * (d * h - e * g);
		double beta = j * (e * i - h * f) + k * (g * f - d * i) + l * (d * h - e * g);
		double gamma = i * (a * k - j * b) + h * (j * c - a * l) + g * (b * l - k * c);
		beta /= M;
		gamma /= M;
		float t = (float) (f * (a * k - j * b) + e * (j * c - a * l) + d * (b * l - k * c));
		t = -t / (float) M;
		double alpha = 1.0d - (beta + gamma);
		if (!((beta > 0.0d) && (gamma > 0.0d) && (alpha > 0.0d)))
			return false;
		
		IntersectionRecord or = new IntersectionRecord();

		Vector3 loc = new Vector3(origin);
		Vector3 td = new Vector3(direction);
		td.mul(t);;
		loc.add(td);
		or.location.set(loc);
		if (mesh.hasUVs())
		{
			Vector2d uvs = new Vector2d();
			Vector2d alphaUV = new Vector2d(getUV(index.x));
			Vector2d betaUV = new Vector2d(getUV(index.y));
			Vector2d gammaUV = new Vector2d(getUV(index.z));
			alphaUV.mul(alpha);
			betaUV.mul(beta);
			gammaUV.mul(gamma);
			uvs.add(alphaUV);
			uvs.add(betaUV);
			uvs.add(gammaUV);
			or.texCoords.set(uvs);
		}
		or.surface = this;
		or.t = t;
		outRecord.set(or);
		return true;
  }

  /**
   * @see Object#toString()
   */
  public String toString() {
    return "Triangle ";
  }
}